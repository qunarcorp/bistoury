;(function () {
    'use strict';
    var Bistoury = function () {
        this.result = "";
        var defaultCodeMap = {
            "-101": "系统异常",
            "-102": "错误的请求，请检查agent版本是否更新",
            "-103": "Agent未启动，请刷新重试或联系tcdev热线",
            "-104": "PID获取错误，请检查应用是否启动",
            "-105": "Agent 版本错误，请检查agent版本是否更新",
            "-106": "应用日志目录不存在",
            "-107": "该命令不支持多机执行",
            "-108": "版本不支持该命令，请升级",
            "-109": "请选择一台主机",
            "-110": "命令解析错误，请检查命令是否正确"
        }
        this.errorMapping = defaultCodeMap;
        var self = this;
        $.ajax({
            "url": "/api/errorcode/mapping.do",
            "type": "get",
            "data": {},
            success: function (ret) {
                if (ret.status === 0) {
                    self.errorMapping = ret.data;
                } else {
                    console.log(ret.message);
                }
            },
            error: function (request, message) {
                console.log(message);
            }
        });
        return this;
    }

    Bistoury.prototype.checkNew = function (type, content) {
        if (type == -1) {
            if (bistoury.isJsonDataStr(content)) {
                var errorMsg = JSON.parse(content);
                var error = bistoury.errorMapping[errorMsg.code];
                if (errorMsg.message) {
                    bistoury.error(errorMsg.message);
                } else if (error) {
                    bistoury.error(error)
                } else {
                    bistoury.error("未定义的错误" + (content ? ": " : "") + content);
                }
                bistoury.result = "";
                return false;
            } else {
                bistoury.error("未知错误, " + content);
                bistoury.result = "";
                return false;
            }
        }
        if (content.indexOf("Error during processing the command") == 0) {
            bistoury.result = "";
            bistoury.keepError("命令执行错误，请尽快检查应用是否运行正常，" + content.substring(36))
            return false;
        }

        return true;
    }
    Bistoury.prototype.check = function (id, type, content) {
        return this.checkNew(type, content);
    }

    Bistoury.prototype.receive = function (content, type, func) {
        if (!this.isJsonDataStr(content)) {
            if (type != 2 && type != 3) {
                bistoury.result += content;
                return false;
            } else {
                func.call(this, bistoury.result, type);
                bistoury.result = "";
                return true;
            }
        } else {
            func.call(this, content);
            bistoury.result = "";
            return true;
        }
    }

    Bistoury.prototype.getInt64 = function (dataview, byteOffset, littleEndian) {
        var low = byteOffset + 4;
        var high = byteOffset
        if (littleEndian) {
            low = byteOffset;
            high = byteOffset + 4;
        }
        return (dataview.getUint32(high, littleEndian) << 32) | dataview.getUint32(low, littleEndian);
    }

    Bistoury.prototype.isJsonData = function (data) {
        try {
            if (typeof data == "object") {
                return true;
            }
        } catch (e) {
            return false;
        }
        return false;
    }

    Bistoury.prototype.isJsonDataStr = function (data) {
        try {
            if (typeof JSON.parse(data) == "object") {
                return true;
            }
        } catch (e) {
            return false;
        }
        return false;

    }

    Bistoury.prototype.info = function (msg) {
        spop({
            template: msg,
            position: 'top-center',
            style: 'info',
            autoclose: 3000,
        });
    }

    Bistoury.prototype.success = function (msg) {
        spop({
            template: msg,
            style: 'success',
            autoclose: 3000,

        });
    }
    Bistoury.prototype.warning = function (warningMsg) {
        spop({
            template: warningMsg,
            style: 'warning',
            autoclose: 5000,
        });
    }
    Bistoury.prototype.error = function (errorMsg) {
        var index = errorMsg.indexOf("Command preprocess failed: ");
        if (index >= 0) {
            errorMsg = errorMsg.substr(index + 27);
        }
        spop({
            template: this.getErrorMsg(errorMsg),
            style: 'error',
            autoclose: 7000,

        });
    }
    Bistoury.prototype.errorCode = function (errorCode) {
        this.error(this.errorMapping[errorCode])
    }
    Bistoury.prototype.keepError = function (errorMsg, func) {
        if (func) {
            spop({
                template: this.getErrorMsg(errorMsg),
                position: 'top-center',
                style: 'error',
                onClose: func
            });
        } else {
            spop({
                template: this.getErrorMsg(errorMsg),
                position: 'top-center',
                style: 'error'
            });
        }

    }
    Bistoury.prototype.errorAndClose = function (errorMsg) {
        spop({
            template: this.getErrorMsg(errorMsg),
            style: 'error',
            position: 'top-center',
            autoclose: 5000,
            onClose: function () {
                window.close();
            }
        });
    }
    Bistoury.prototype.getErrorMsg = function (errorMsg) {
        try {
            var index = errorMsg.indexOf("Command preprocess failed: ");
            if (index >= 0) {
                return errorMsg.substr(index + 27);
            }
        } catch (e) {
            return errorMsg;
        }
        return errorMsg;
    }

    window.bistoury = new Bistoury();
    $.ajaxSetup({
        complete: function (context) {
            if ("REDIRECT" == context.getResponseHeader("REDIRECT")) { //若HEADER中含有REDIRECT说明后端想重定向，
                var win = window;
                while (win != win.top) {
                    win = win.top;
                }
                win.location.href = context.getResponseHeader("REDIRECT_PATH");//将后端重定向的地址取出来,使用win.location.href去实现重定向的要求
            }
        }
    });
}());