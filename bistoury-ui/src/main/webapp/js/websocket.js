;(function () {
    'use strict';
    var BistouryWebSocket = function () {
        this.wsList = {};
        this.proxy = {};
        return this;
    }
    BistouryWebSocket.prototype.sendCommand = function (machine, type, command, stopFunc, handleResultFunc) {
        this.send(machine, type, command, command, stopFunc, handleResultFunc);
    }
    BistouryWebSocket.prototype.send = function (machine, type, oldCommand, newCommand, stopFunc, handleResultFunc) {
        if (machine == null) {
            bistoury.warning("请选择机器");
            callBack(stopFunc);
            console.log("请选择机器");
        }

        bistouryWS.proxy = {};

        var agentIp = machine.ip;
        var wsPromise = getWs(agentIp, machine.host, stopFunc, handleResultFunc);

        wsPromise.done(function (ws) {
            if (bistoury.isJsonData(newCommand)) {
                newCommand = JSON.stringify(newCommand);
            }
            var content = {
                user: bistouryWS.getUserName(),
                type: type,
                app: machine.appCode,
                hosts: ['' + machine.host + ''],
                command: bistouryWS.proxy[agentIp] ? newCommand : oldCommand,
                token: bistouryWS.getToken()
            };
            var data = bistouryWS.encrypt(JSON.stringify(content));
            ws.send(data);
        }).fail(function () {
            callBack(stopFunc);
            console.log("连接失败");
        });
    }

    function getWs(agentIp, host, stopFunc, handleResultFunc) {
        var deferred = $.Deferred();
        if (!agentIp) {
            callBack(stopFunc);
            bistoury.warning("必须选择一台机器");
            deferred.reject();
            return deferred.promise();
        }
        $.ajax({
            "url": "/getProxyWebSocketUrl.do?agentIp=" + agentIp,
            "type": "get",
            "data": {},
            success: function (ret) {
                //status 为100是new proxy,0是old proxy
                if (ret.status === 0 || ret.status === 100) {
                    var proxyUrl = ret.data;
                    bistouryWS.proxy[agentIp] = ret.status === 100;
                    doGetWs(deferred, proxyUrl, host, stopFunc, handleResultFunc);
                } else {
                    console.log(ret.message);
                    callBack(stopFunc);
                    bistoury.error(host + "\\> not find proxy for agent");
                    deferred.reject();
                }
            },
            error: function (request, message) {
                console.log(message);
                callBack(stopFunc);
                bistoury.error(host + "\\> not find proxy for agent");
                deferred.reject();
            }
        });

        return deferred.promise();
    }

    var doGetWs = function (deferred, proxyUrl, host, stopFunc, handleResultFunc) {
        var ws = bistouryWS.wsList[host];
        if (!ws) {
            try {
                ws = new WebSocket(proxyUrl);
                ws.binaryType = "arraybuffer";
                bistouryWS.wsList[host] = ws;

                ws.onopen = function (event) {
                    deferred.resolve(ws);
                };

                ws.onmessage = function (event) {
                    if (bistouryWS.wsList[host] == null) {
                        return;
                    }
                    if (typeof (event.data) == "string") {
                        recv(JSON.parse(event.data), stopFunc, handleResultFunc);
                    } else {
                        recv(event.data, stopFunc, handleResultFunc);
                    }
                };

                ws.onclose = function (event) {
                    console.log(host + "\\> 已经与proxy断开连接")
                    callBack(stopFunc);
                    bistouryWS.wsList[host] = null;
                };

                ws.onerror = function (event) {
                    bistoury.error("websocket连接失败");
                    console.log("ws error")
                    try {
                        ws.close();
                    } catch (e) {
                        // ignore
                    }
                };
            } catch (ex) {
                bistouryWS.wsList[host] = null;
                console.log("连接失败: " + ex.message);
                callBack(stopFunc)
                deferred.reject();
            }
        } else {
            bistouryWS.wsList[host] = ws;
            deferred.resolve(ws);
        }
    };

    function recv(data, stopFunc, handleResultFunc) {
        var dataView = new DataView(data);
        var id = bistoury.getInt64(dataView, 0, false);
        var type = dataView.getInt32(8);
        var ip = dataView.getInt32(12);
        var len = dataView.getInt32(16);
        //8+4+4+4
        var content = Utf8ArrayToStr(new Uint8Array(data.slice(20, 20 + len)));
        if (!bistoury.check(id, type, content)) {
            console.log(content)
            callBack(stopFunc, type);
            return;
        }
        if (!bistoury.receive(content, type, handleResultFunc)) {
            callBack(stopFunc, type, true);
        }
    }

    function callBack(func, type, isReceiveCall) {
        if (func != null) {
            func.call(this, type, isReceiveCall);
        }
    }

    BistouryWebSocket.prototype.getUserName = function () {
        return $.cookie('login_id');
    }

    BistouryWebSocket.prototype.getToken = function () {
        return $.cookie('login_token').replace(/\s/g, '+');
    }

    BistouryWebSocket.prototype.encrypt = function (content) {
        var k1 = makeid();
        var dataEnc = encryptByDES(content, k1);


        var publicKey = '-----BEGIN PUBLIC KEY-----\n' +
            'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzYgJiPl4ltUdOvTIx8yu5iw0+\n' +
            'k7jANyrVzXOJy+478EhBaf8MoHaHvbH06PfaLjmFJXsRZKv9Qq5SieQcLlnG60Uu\n' +
            'utpen1Nf490au+nPCP++nK3L5ZBqaSCAq4GUAniARR1wWl9TYW0walBCpD2N2Swy\n' +
            'MLu9z+Lnhd7auqYSzwIDAQAB\n' +
            '-----END PUBLIC KEY-----';
        var crypt = new JSEncrypt();
        crypt.setPublicKey(publicKey);
        var k1Enc = crypt.encrypt(k1);

        return "{\"0\":\"" + k1Enc + "\",\"1\":\"" + dataEnc + "\"}";
    }

    function makeid() {
        var text = "";
        var possible = "0123456789abcdef";

        for (var i = 0; i < 8; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }

        return text;
    }

    function encryptByDES(message, key) {
        var keyHex = CryptoJS.enc.Utf8.parse(key);
        var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.toString();
    }

    function Utf8ArrayToStr(array) {
        var out, i, len, c;
        var char2, char3;

        out = "";
        len = array.length;
        i = 0;
        while (i < len) {
            c = array[i++];
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    // 0xxxxxxx
                    out += String.fromCharCode(c);
                    break;
                case 12:
                case 13:
                    // 110x xxxx   10xx xxxx
                    char2 = array[i++];
                    out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14:
                    // 1110 xxxx  10xx xxxx  10xx xxxx
                    char2 = array[i++];
                    char3 = array[i++];
                    out += String.fromCharCode(((c & 0x0F) << 12) |
                        ((char2 & 0x3F) << 6) |
                        ((char3 & 0x3F) << 0));
                    break;
            }
        }

        return out;
    }

    window.bistouryWS = new BistouryWebSocket();
}())