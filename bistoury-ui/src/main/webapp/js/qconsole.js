var REQ_TYPE_PROFILER_STOP = 52;
var REQ_TYPE_PROFILER_INFO = 58;
$(document).ready(function () {
    document.onkeydown = function (e) {
        if ($(e.target).hasClass("chosen-search-input")) {
            return
        }
        var tabName = $('.ui-tabs-active>a').text()
        var tabId = "#tabs-" + tabName
        var context = $(tabId).data("context");
        if (typeof (context) != "undefined") {
            context.jqconsole.Focus()
        }
    }
    Array.prototype.remove = function (val) {
        var index = this.indexOf(val);
        if (index > -1) {
            this.splice(index, 1);
        }
    };
    $(function () {
        var linuxCommandType = [1, 4, 6];
        var errorMapping = bistoury.errorMapping;
        var waitCommand = [
            "ls",
            "jvm",
            "thread",
            "sysprop",
            "sysenv",
            "dump",
            "classloader",
            "options",
            "logger",
            "getstatic",
            "vmoption",
            "mbean",
            "ognl",
            "jad",
            "sc",
            "sm"
        ];
        var arthasCommands = [
            "dashboard",
            "thread",
            "jvm",
            "sysprop",
            "getstatic",
            "sc",
            "sm",
            "dump",
            "jad",
            "classloader",
            "redefine",
            "monitor",
            "watch",
            "trace",
            "stack",
            "tt",
            "options",
            "reset",
            "shutdown",
            "history",
            "sysenv",
            "ognl",
            "mc",
            "mbean",
            "heapdump",
            "vmoption",
            "logger",
            "stop",
            "profilerstart",
            "profilerstop",
            "profilersearch"
        ];

        var debugCommand = [
            "qdebugadd",
            "qdebugremove",
            "qdebugsearch",
            "qdebugreleaseinfo",
        ];

        var profilerStop = "profilerstop";

        var profilerInfo = "profilerinfo";

        function arrayContains(array, obj) {
            for (var i = 0; i < array.length; ++i) {
                if (array[i] === obj) {
                    return true;
                }
            }
            return false;
        }

        $.ajax({
            "url": "/getApps.do",
            "type": "get",
            success: function (ret) {
                if (ret.status === 0) {
                    var apps = ret.data;
                    addApps(apps);
                } else {
                    bistoury.error('获取应用列表失败')
                }
            }
        });

        // autoCompleteApp();
        function addApps(apps) {
            var group = $(".js-apps-group");
            var select = $("<select><option value=''></option></select>").attr("data-placeholder", "请选择应用").attr("id", "app-select");
            for (var i = 0; i < apps.length; i++) {
                var app = apps[i];
                var appOption = $("<option></option>").attr("id", "app-" + app).attr("value", app).append(app);
                select.append(appOption);
            }
            group.append(select)

            $("#app-select").on('change', function () {
                addTab($('#app-select').val());
            })
            $('#app-select').chosen({"width": "100%", search_contains: true})
            initAdminUser();
        }

        function initAdminUser() {
            $.ajax({
                "url": "/isAdmin.do",
                "type": "get",
                success: function (ret) {
                    if (ret.status === 0) {
                        isAdmin = ret.data;
                        if (isAdmin) {
                            $('#app_select_chosen .chosen-drop .chosen-search .chosen-search-input').on('input', function () {
                                var value = $(this).val();
                                updateAdminApps(value)
                            });
                        }
                    }
                }
            });
        }

        function updateAdminApps(searchAppKey) {
            $.ajax({
                "url": "/searchApps.do",
                "type": "get",
                "data": {
                    "searchAppKey": searchAppKey
                },
                success: function (ret) {
                    if (ret.status === 0) {
                        var apps = ret.data;
                        doUpdateAdminApps(apps, searchAppKey);
                    } else {
                        bistoury.error("获取admin的应用列表失败");
                    }
                }
            });
        }

        function doUpdateAdminApps(apps, searchAppKey) {
            $('#app-select').empty()
            $('#app-select').append('<option value=""></option>');
            for (var i = 0; i < apps.length; i++) {
                var app = apps[i];
                var appOption = $("<option></option>").attr("id", "app-" + app).attr("value", app).append(app);
                $('#app-select').append(appOption);
            }
            $('#app-select').trigger('chosen:updated');
            $('#app_select_chosen .chosen-drop .chosen-search .chosen-search-input').val(searchAppKey)
        }

        var tabTemplate = "<li app-controls='#{app-controls}'><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";

        var tabs = $("#tabs").tabs();
        tabs.find(".ui-tabs-nav").sortable({
            axis: "x",
            stop: function () {
                tabs.tabs("refresh");
            }
        });

        // 模态对话框的初始化：自定义按钮和一个重置内部表单的 "close" 回调
        var dialog = $("#dialog").dialog({
            autoOpen: false,
            modal: true,
            stack: false,
            buttons: {
                添加: function () {
                    var app = $("#app_tab_title").val();
                    $(this).dialog("close");
                    addTab(app);
                },
                取消: function () {
                    $(this).dialog("close");
                }
            },
            close: function () {
                form[0].reset();
            }
        });

        // addTab 表单：当提交时调用 addTab 函数，并关闭对话框
        var form = dialog.find("form").submit(function (event) {
            addTab($("#app_tab_title").val());
            dialog.dialog("close");
            event.preventDefault();
        });

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

        function addTab(appCode) {
            if ($("[app-controls=apps-" + appCode + "]").length) {
                var tid = "#tabs-" + appCode;
                tabs.tabs("option", "active", tabs.find(tid).index() - 1);
                var context = $(tid).data("context");
                context.jqconsole.Focus();
                return;
            }

            getHosts(appCode);
        }

        function getUserName() {
            var cookiename = $.cookie('login_id');
            return cookiename;
        }

        function getToken() {
            var token = $.cookie('login_token').replace(/\s/g, '+');
            return token;
        }

        //IP转成整型
        function ip2Int(ip) {
            var num = 0;
            ip = ip.split(".");
            num = Number(ip[0]) * 256 * 256 * 256 + Number(ip[1]) * 256 * 256 + Number(ip[2]) * 256 + Number(ip[3]);
            num = num >>> 0;
            return num;
        }

        //整型解析为IP地址
        function int2IP(num) {
            var str;
            var tt = new Array();
            tt[0] = (num >>> 24) >>> 0;
            tt[1] = ((num << 8) >>> 24) >>> 0;
            tt[2] = (num << 16) >>> 24;
            tt[3] = (num << 24) >>> 24;
            str = String(tt[0]) + "." + String(tt[1]) + "." + String(tt[2]) + "." + String(tt[3]);
            return str;
        }

        function getHosts(appCode) {
            $.ajax({
                "url": "/getHosts.do",
                "type": "get",
                "data": {
                    "appCode": appCode
                },
                success: function (ret) {
                    if (ret.status == 0) {
                        var list = ret.data;
                        initTabContent(appCode, list);
                    } else {
                        bistoury.error('获取应用列表失败, 错误信息：' + data.message);
                    }
                }
            });
        }

        function initTabContent(appCode, hosts) {
            var commandMode = false;
            var tid = "tabs-" + appCode,
                cid = "qconsoles-" + appCode,
                li = $(tabTemplate.replace(/#\{app-controls\}/g, "apps-" + appCode).replace(/#\{href\}/g, "#" + tid).replace(/#\{label\}/g, appCode));

            tabs.find(".ui-tabs-nav").append(li);
            tabs.append("<div id='" + tid + "' style='height: calc(100% - 65px);'><ul style='height: calc(100% - 10px);'>" + "<li class='f-item'><div class='label-group js-hosts-group' style='width: 100%'></div></li>" + "<li class='f-item console-li'>" + "<div id='" + cid + "' class='console-content console'></div>" + "</li></ul></div>");
            tabs.tabs("refresh");
            tabs.tabs("option", "active", tabs.find("#" + tid).index() - 1);

            var jqcontext = $("#" + tid);
            var context = {};
            context.wsPromiseMap = {};
            context.currentWsList = [];
            context.wsOpen = [];
            context.tabHostLength = hosts.length;
            context.multi = false;
            jqcontext.data("context", context);
            var commandLine = '';
            var command = '';
            var args = '';
            var qconsole = $("#" + cid);

            var jqconsole = context.jqconsole = qconsole.jqconsole('', '');
            var allIpToHosts = {};
            var allHostToIps = {};
            var checkedHost = '';
            var isTab = false;
            var allContent = {};
            var lastHost = '';
            var tabContent = '';
            var isCanceled = false;
            var lastOutput = '';
            var proxy = {};

            var addHosts = function () {
                var group = $("#" + tid + " .js-hosts-group");


                if (hosts.length >= 0) {
                    var placeholder;
                    if (hosts.length == 0) {
                        placeholder = "应用中心未查询到属于该应用的机器";
                    } else {
                        placeholder = "请选择机器，不选则表示选中所有机器（tail、grep、zgrep、ls、head命令支持多机操作，其余命令均需要选择一台机器）";

                    }
                    var select = $("<select multiple  tabindex='2'></select>").attr("data-placeholder", placeholder).attr("id", "host-select-" + tid).attr("class", "chosen-select");
                    select.append($("<option value=\"\"></option>"))
                    for (var i = 0; i < hosts.length; i++) {
                        var host = hosts[i].host;
                        var ip = hosts[i].ip;
                        allIpToHosts[ip] = host;
                        allHostToIps[host] = ip;
                        var hostOption = $("<option></option>").attr("id", "host-" + host).attr("value", host).append(host);
                        select.append(hostOption);
                    }
                    group.append(select);
                    $("#host-select-" + tid).on('change', function () {
                        if (!commandMode) return;
                        checkedHost = $('#host-select-' + tid).val();
                        if (checkedHost == null || checkedHost == undefined) {
                            checkedHost = "";
                        }
                        jqconsole.Focus();
                        startNewLine();
                    })
                    $('#host-select-' + tid).chosen({
                        "width": "calc(100% - 60px)",
                        search_contains: true,
                        allow_single_deselect: true
                    });
                    $('.console').focus();

                    var fullScreenBtn = $("<button></button>").addClass("btn btn-info btn-sm").css("float", "right").css("height", "32px").css("max-width", "55px").append("全屏");
                    var isFull = false;
                    fullScreenBtn.click(function () {
                        bistoury.info("Esc退出全屏")
                        isFull = true;
                        $("#" + cid).removeClass("console").addClass("full-screen-console");
                    })
                    $(document).on("keydown", function (e) {
                        if (e.keyCode == 27 && isFull) {
                            isFull = false;
                            $("#" + cid).removeClass("full-screen-console").addClass("console");
                            e.preventDefault();
                        }
                    })
                    group.append(fullScreenBtn);

                } else {
                    var hostsHtml = group.html();
                    hostsHtml += '<input id="hosts-' + tid + '" type="checkbox" autocomplete="off" name="host" value="" checked="checked">' +
                        '<label for="hosts-' + tid + '" class="cur">所有</label>';
                    group.html(hostsHtml);
                    for (var i = 0; i < hosts.length; i++) {
                        var host = hosts[i].host;
                        var ip = hosts[i].ip;
                        allIpToHosts[ip] = host;
                        allHostToIps[host] = ip;
                        hostsHtml = group.html();
                        hostsHtml += '<input id="hosts-' + host + '-' + tid + '" type="checkbox" autocomplete="off" name="host" value="' + host + '">' +
                            '<label for="hosts-' + host + '-' + tid + '">' + host + '</label>';
                        group.html(hostsHtml);
                    }
                    group.on('click', "label", function () {
                        if (!commandMode) return;
                        checkedHost = $(this).prev().val();
                        $(this).addClass("cur").siblings().removeClass("cur");
                        jqconsole.Focus();
                        startNewLine();
                    });
                }
            };

            var parseCommandLine = function (input) {
                commandLine = input;
                args = commandLine.trim().split(/\s+/);
                command = args[0];
            };

            var reset = function () {
                commandMode = false;
                allContent = {};
                tabContent = '';
                isCanceled = false;
            };

            var prompt = function () {
                // Start the prompt with history enabled.
                jqconsole.Prompt(true, function (input, keyCode) {
                    //clear();
                    if (!commandMode) {
                        prompt();
                        return;
                    }
                    if (input == "") {
                        startPrompt();
                    } else {
                        reset();
                        parseCommandLine(input);
                        try {
                            if (keyCode == $.ui.keyCode.ENTER) {
                                isTab = false;
                                execCommand(input);
                            } else if (keyCode == $.ui.keyCode.TAB) {
                                isTab = true;
                                if (checkedHost === '') {
                                    context.tabHostLength = hosts.length;
                                } else {
                                    context.tabHostLength = checkedHost.length;
                                }
                                send(3, input);
                            } else {
                                // ignore
                            }
                        } catch (e) {
                            outputln(e.message);
                            startPrompt();
                        }
                    }
                });
            };

            var checkArgument = function (bool) {
                if (bool) {
                    outputln('命令参数不正确!');
                    return false;
                }
                return true;
            };

            var tryLocalCommand = function () {
                if (command == 'cl') {
                    if (!checkArgument(args.length != 1)) {
                        return true;
                    }
                    jqconsole.ClearScreen();
                } else if (command == 'list') {
                    if (!checkArgument(args.length != 1)) {
                        return true;
                    }
                    for (var i = 0; i < hosts.length; i++) {
                        outputln(i + ". " + hosts[i].host);
                    }
                } else if (command == 'ch') {
                    if (!checkArgument(args.length != 2)) {
                        return true;
                    }
                    var arg = args[1];
                    if (!isNaN(arg)) {
                        if (!checkArgument(arg >= hosts.length)) {
                            return true;
                        }
                        checkedHost = hosts[arg].host;
                    } else {
                        checkedHost = arg;
                    }
                    var label = $('label[for="hosts-' + checkedHost + '-' + tid + '"]');
                    $(label).addClass("cur").siblings().removeClass("cur");
                    prompt();
                } else if (command == 'exit') {
                    if (!checkArgument(args.length != 1)) {
                        return true;
                    }
                    checkedHost = '';
                    var label = $('label[for="hosts-' + tid + '"]');
                    $(label).addClass("cur").siblings().removeClass("cur");
                    prompt();
                } else {
                    return false;
                }
                return true;
            };

            var qToolsSet = new Set(["qjmap", "qjtop", "qjmxcli", "qjdump"]);
            var tryRemoteCommand = function () {
                //vjtools工具集 (qjdump例外) type=6
                if (qToolsSet.has(command)) {
                    send(6, commandLine);
                } else if (arrayContains(arthasCommands, command)) {
                    send(7, commandLine);
                } else if (arrayContains(debugCommand, command)) {
                    send(8, commandLine);
                } else if (command == 'jstack' || command == 'jstat') {
                    send(4, commandLine);
                } else if (command == 'dl') {
                    if (!checkArgument(args.length != 2)) {
                        return true;
                    }
                    send(5, commandLine);
                } else if (command === profilerStop) {
                    send(REQ_TYPE_PROFILER_STOP, commandLine);
                } else if (command === profilerInfo) {
                    send(REQ_TYPE_PROFILER_INFO, commandLine);
                } else {
                    send(1, commandLine);
                }
                return false;
            };

            var execCommand = function () {
                if (tryLocalCommand() || tryRemoteCommand()) {
                    startPrompt();
                    return true;
                }
                return false;
            };

            var maxLines = 500;
            var clear = function () {
                var length = jqconsole.GetLine();
                if (length <= maxLines) {
                    return;
                }
                var count = length - maxLines;
                var delLine = 0;
                var spans = jqconsole.$console.children("span");
                for (var i = 1; i < spans.length; i++) {
                    var text = $(spans[i]).find('span').text();
                    $(spans[i]).remove();
                    if (text.indexOf('\n') != -1) {
                        delLine++;
                    }
                    if (delLine == count) {
                        break;
                    }
                }
            };

            var startPrompt = function () {
                commandMode = true;
                var lastOutputLen = lastOutput.length;
                var prefix = '';
                if (lastOutputLen > 0 && lastOutput.charAt(lastOutputLen - 1) != '\n') {
                    prefix += '\n';
                }
                var hostLine;
                if (checkedHost == '') {
                    hostLine = "all";
                } else if (typeof checkedHost === 'object' && checkedHost.length > 2) {
                    hostLine = " " + checkedHost.length + " machines";
                } else {
                    hostLine = checkedHost;
                }
                jqconsole.Write(prefix + getUserName() + '@' + (hostLine) + '@' + appCode + ':\\>', 'jqconsole-prompt-prefix');
                prompt();
                lastOutput = '';
            };

            jqconsole.RegisterShortcut('C', function (event) {
                event.preventDefault();
                if (!commandMode) {
                    send(2, '');
                } else {
                    startNewLine();
                }
                isCanceled = true;
            });

            jqconsole.RegisterShortcut('L', function (event) {
                event.preventDefault();
                jqconsole.ClearScreen();
                if (commandMode) {
                    startPrompt();
                }
            });

            jqconsole.RegisterShortcut('A', function (event) {
                event.preventDefault();
                jqconsole.MoveToStart();
            });

            jqconsole.RegisterShortcut('E', function (event) {
                event.preventDefault();
                jqconsole.MoveToEnd();
            });

            jqconsole.RegisterShortcut('U', function (event) {
                event.preventDefault();
                jqconsole.ClearPromptText();
            });

            var commandCheck = function (input) {
                if (!input) {
                    return true;
                }
                var commands = input.split(/\&|\&\&|\|\||\;|\|/);
                for (var i = 0; i < commands.length; i++) {
                    var command = commands[i].trim().split(" ")[0];
                    if (!isMatchCommand(command)) {
                        return false;
                    }
                }
                return true;
            }

            var multiMachineCommands = ["tail", "grep", "zgrep", "ls", "head"];

            var isMatchCommand = function (command) {
                if (command === "") {
                    return false;
                }
                for (var j = 0; j < multiMachineCommands.length; j++) {
                    if (command.indexOf(multiMachineCommands[j]) == 0) {
                        return true;
                    }
                }
                return false;
            }

            var send = function (type, input) {
                if (input.indexOf('｜') >= 0) {
                    outputln("\033[31m[WARING]:\033[0m Chinese character [｜] in command");
                }
                proxy = {};
                lastHost = "";
                if (hosts == null || hosts.length <= 0) {
                    output("The application center didn't find the machine belonging to the application")
                    startPrompt();
                    return;
                }
                if (checkedHost != null && checkedHost != '' && typeof checkedHost === "object" && checkedHost.length > 1) {
                    sends(type, input, checkedHost);
                    return;
                }
                if (checkedHost === '' || checkedHost.length == 0) {
                    sends(type, input, hosts.map(function (value) {
                            return value.host
                        })
                    );
                    return;
                }
                if (checkedHost === '') {
                    output("A machine must be selected");
                    startPrompt();
                    return;
                }
                var host = typeof checkedHost === "string" ? checkedHost : checkedHost[0];
                if (context.wsOpen.indexOf(host) < 0) {
                    context.wsOpen.push(host);
                }
                context.multi = false;
                sendWs(type, input, checkedHost);
            };
            var sends = function (type, input, checkHosts) {
                if (!commandCheck(input)) {
                    output("The command does not support multi machine execution");
                    startPrompt();
                    return;
                }
                context.multi = true;
                checkHosts.forEach(function (host) {
                    if (context.wsOpen.indexOf(host) < 0) {
                        context.wsOpen.push(host);
                    }
                    sendWs(type, input, host);
                })
            }
            var sendWs = function (type, input, host) {
                var agentIp = allHostToIps[host];
                var wsPromise = getWs(agentIp);
                wsPromise.done(function (ws) {
                    if (proxy[agentIp] && arrayContains(linuxCommandType, type)) {
                        input = JSON.stringify({command: input});
                    }
                    var content = {
                        user: getUserName(),
                        type: type,
                        app: appCode,
                        hosts: ['' + host + ''],
                        command: input,
                        token: getToken()
                    };
                    var data = encrypt(JSON.stringify(content));

                    ws.send(data);
                }).fail(function () {
                    context.wsOpen.remove(host);
                    if (context.wsOpen.length == 0) {
                        startPrompt();
                    }
                });
            }
            var getWs = function (agentIp) {
                var deferred = $.Deferred();
                if (!agentIp) {
                    output("A machine must be selected");
                    deferred.reject();
                    return deferred.promise();
                }
                var host = allIpToHosts[agentIp];
                $.ajax({
                    "url": "/getProxyWebSocketUrl.do?agentIp=" + agentIp,
                    "type": "get",
                    "data": {},
                    success: function (ret) {
                        if (ret.status === 0 || ret.status === 100) {
                            proxy[agentIp] = ret.status === 100;
                            var proxyUrl = ret.data;
                            doGetWs(deferred, proxyUrl, host);
                        } else {
                            context.wsOpen.remove(host)
                            context.tabHostLength--;
                            console.log(ret.message);
                            if (context.multi) {
                                outputln(host + "\\> not find proxy for agent");
                            } else {
                                outputln("not find proxy for agent");
                            }
                            deferred.reject();
                        }
                    },
                    error: function (request, message) {
                        context.wsOpen.remove(host)
                        context.tabHostLength--;
                        console.log(message);
                        if (context.multi) {
                            outputln(host + "\\> not find proxy for agent");
                        } else {
                            outputln("not find proxy for agent");
                        }
                        deferred.reject();
                    }
                });

                return deferred.promise();
            };

            var doGetWs = function (deferred, proxyUrl, host) {
                var wsPromise = context.wsPromiseMap[host];
                if (!wsPromise) {
                    try {
                        var ws = new WebSocket(proxyUrl);
                        ws.binaryType = "arraybuffer";
                        context.currentWsList.push(ws);
                        context.wsPromiseMap[host] = deferred.promise();

                        ws.onopen = function (event) {
                            deferred.resolve(ws);
                        };

                        ws.onmessage = function (event) {
                            if (context.currentWsList.indexOf(ws) < 0) {
                                return;
                            }

                            if (typeof (event.data) == "string") {
                                recv(JSON.parse(event.data), host);
                            } else {
                                recv(event.data, host);
                            }
                        };

                        ws.onclose = function (event) {
                            if (context.wsOpen.indexOf(host) >= 0) {
                                outputln(host + "\\> disconnected from proxy");
                            }
                            var open = context.wsOpen.indexOf(host) >= 0;
                            context.wsOpen.remove(host)
                            if (open && context.wsOpen.length == 0) {
                                startPrompt();
                            }
                            removeHostAndWs(ws, host)
                        };

                        ws.onerror = function (event) {
                            console.log("ws error")
                            try {
                                ws.close();
                            } catch (e) {
                                // ignore
                            }
                        };
                    } catch (ex) {
                        if (context.wsOpen.indexOf(host) >= 0) {
                            outputln(host + "\\> failed to connect to proxy!");
                        }
                        context.wsOpen.remove(host)
                        context.tabHostLength--;
                        console.log("connection failed: " + ex.message);
                        deferred.reject();
                    }
                } else {
                    wsPromise.done(function (ws) {
                        deferred.resolve(ws);
                    }).fail(function () {
                        deferred.reject();
                    });
                }
            };
            var removeHostAndWs = function (ws, host) {
                context.wsPromiseMap[host] = null;
                context.currentWsList.remove(ws);
            }
            var output = function (content) {
                lastOutput = content;
                jqconsole.Write(content, 'jqconsole-output');
            };

            var outputln = function (content) {
                //                  var lines = content.split('\n');
                //                  for (var i = 0; i < lines.length; i++) {
                //                      jqconsole.Write(lines[i] + '\n', 'jqconsole-output');
                //                  }
                output(content + '\n');
            };

            var startNewLine = function () {
                var text = jqconsole.GetPromptText();
                jqconsole.ClearPromptText();
                outputln(text);
                startPrompt();
            };

            var matchItem = function (lines, str) {

                if (lines == null || lines.length == 0) {
                    return str;
                }

                for (var i = 0; i < lines.length;) {
                    var line = lines[i];
                    if (line.indexOf("No such file or directory") >= 0) {
                        lines.remove(line);
                    } else {
                        i++;
                    }
                }

                if (lines.length == 0) {
                    return str;
                }

                var first = lines[0];
                var match = str;
                var len = str.length;
                for (var i = len; i < first.length; i++) {
                    var c = first[i];
                    for (var j = 1; j < lines.length; j++) {
                        var line = lines[j];
                        if (line == '') continue;
                        if (i >= line.length || line[i] != c) {
                            return match;
                        }
                    }
                    match += c;
                }
                return match;
            };

            var multiHostChecked = function () {
                return checkedHost == '' && hosts.length > 1;
            };

            var recover = function () {
                var lastIndex = commandLine.lastIndexOf(' ');
                var lastStr = commandLine.substring(lastIndex + 1);
                var command = commandLine.substring(0, lastIndex + 1);
                var lines = tabContent.match(/[^\n]+/g);
                var match = matchItem(lines, lastStr);
                if (!context.multi || context.tabHostLength <= 0) {
                    startPrompt();
                }
                jqconsole.SetPromptText(command + match);
            };


            //                var myrepeat = function (data) {
            //                    var ret = '';
            //                    for (var j = 0; j < 10000; j++) {
            //                        ret += data;
            //                    }
            //                    return ret;
            //                }
            //                var i = 0;

            var wait = function () {
                //                    return !isTab && (command == 'tail' || command == 'cat' || command == 'awk' || command == 'jstack' || command == 'jstat');
                return isTab || arrayContains(waitCommand, command);
            };

            var recv = function (data, agentHost) {
                //                    jqconsole.Write(myrepeat(++i) + '\n', 'jqconsole-output');
                var dataView = new DataView(data);
                var id = bistoury.getInt64(dataView, 0, false);
                var type;
                var ip;
                var len;
                var content;
                if (id == -1) {
                    type = dataView.getInt32(8);
                    ip = dataView.getInt32(12);
                    len = dataView.getInt32(16);
                    //8+4+4+4
                    content = Utf8ArrayToStr(new Uint8Array(data.slice(20, 20 + len)));
                } else {
                    type = dataView.getInt8(8);
                    ip = dataView.getInt32(9);
                    len = dataView.getInt32(13);
                    //8+1+4+4
                    content = Utf8ArrayToStr(new Uint8Array(data.slice(17, 17 + len)));
                }
                //                    var log = 'id: ' + id + ',type: ' + type + ',ip: ' + ip + ',content:\n' + content;
                //                    console.log("recv: " + log);
                var line = "------------------";
                var fullContent = '';
                var host = agentHost;
                var _wait = wait();
                allContent[host] = allContent[host] || '';
                if (type != 2 && type != 3) {
                    if (type == 1 && !_wait) {
                        if (context.multi) {
                            if (host != lastHost) {
                                if (lastOutput && !endWith(lastOutput, "\n")) {
                                    outputln("");
                                }
                                outputln(line + host + line)
                                lastHost = host;
                            }
                            output(content);
                        } else {
                            if (isProfilerReq(context.reqType)) {
                                content = parseProfilerMsg(context.reqType, content);
                            }
                            output(content);
                        }
                    } else {
                        allContent[host] += content;
                    }
                }
                if (type == -1 || type == 2) {
                    if (type == 2 && isTab) {
                        tabContent += allContent[host];
                    }
                    if (type == 2 && !_wait) {
                        return;
                    }
                    var result = '';
                    if (context.multi && type != -1) {
                        result = line + host + line + "\n";
                    }
                    fullContent = allContent[host];
                    if (type == 2) {
                        fullContent = process(allContent[host]);
                    }
                    result += fullContent;
                    var len = result.length;
                    if (len > 0 && result.charAt(len - 1) != '\n') {
                        result += '\n';
                    }
                    if (type == -1 && id == -1) {
                        var errorMsg = JSON.parse(result);
                        console.log(errorMsg);
                        var error = errorMapping[errorMsg.code];
                        if (errorMsg.message) {
                            output(line + host + line + "\n")
                            output(errorMsg.message);
                        } else if (error) {
                            output(line + host + line + "\n")
                            output(error)
                        } else {
                            output(line + host + line + "\n")
                            output("未定义的错误" + (result ? ": " : "") + result);
                        }
                        context.wsOpen.remove(agentHost);
                        if (context.wsOpen.length == 0) {
                            startPrompt();
                        }
                    } else {
                        output(result);
                    }
                } else if (type == 3) {
                    if (content != '' && !isCanceled) {
                        outputln(line + "统计" + line + "\n" + content);
                    }
                    if (isTab) {
                        context.tabHostLength--;
                        recover();
                    } else {
                        context.wsOpen.remove(agentHost);
                        if (context.wsOpen.length == 0) {
                            startPrompt();
                        }
                    }
                } else {
                    // ignore
                }
            };

            var isProfilerReq = function (reqType) {
                return reqType === REQ_TYPE_PROFILER_INFO || reqType === REQ_TYPE_PROFILER_STOP;
            };

            var parseProfilerMsg = function (reqType, content) {
                if (reqType === REQ_TYPE_PROFILER_STOP) {
                    return doParseProfilerStopContent(content);
                } else if (reqType === REQ_TYPE_PROFILER_INFO) {
                    return doParseProfilerInfoContent(content);
                }
                return content;
            };

            var doParseProfilerStopContent = function (content) {
                var response = JSON.parse(content);
                return response.data.message;
            };

            var doParseProfilerInfoContent = function (content) {
                var response = JSON.parse(content);
                var msg = "";
                var data = response.data.data;
                return msg + "is active: " + data.isProfiling
                    + "\nstart time: " + data.startTime
                    + "\nid: " + data.id
                    + "\ninterval(ms): " + data.interval;
            };

            var endWith = function (line, ch) {
                if (!line) {
                    return false;
                }
                for (var i = line.length - 1; i >= 0; i--) {
                    var c = line.charAt(i);
                    if (c == ch) {
                        return true;
                    } else if (c == " " || c == "\r" || c == "\t") {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
            var process = function (content) {
                if ((isTab || (command == 'ls' && !ls_l()))) {
                    var lines = content.split('\n');
                    content = adapt(lines, lines.length, $('.jqconsole').width() / 10);
                }
                return content;
            };

            var adapt = function (list, size, width) {
                var i, j, line_num, formatted = '';

                line_num = get_line_num(list, size, width);
                for (i = 0; i < line_num; i++) {
                    for (j = i; j < size; j += line_num) {
                        formatted += format(list[j], get_col_width(list, size, line_num, Math.floor(j / line_num)) + 2);
                    }
                    formatted += '\n';
                }
                return formatted;
            };

            var ls_l = function () {
                if (command != "ls") {
                    return false;
                }
                var paramsLine = commandLine.substring(2);
                var params = paramsLine.trim().split(/\s/g);
                if (params.length > 0 && params[0].startsWith("-") && params[0].indexOf("l") > 0) {
                    return true;
                }
                return false;
            }

            var format = function (content, width) {
                var str = '';
                for (var i = 0; i < width - content.length; i++) {
                    str += ' ';
                }
                return content + str;
            };


            var init = function () {
                addHosts();
                startPrompt();
                startNewLine();
                isCanceled = true;
                jqconsole.ClearScreen();
                if (commandMode) {
                    startPrompt();
                }
            }
            init();
        }

        // addTab 按钮：值打开对话框
        $("#add_tab")
            .button()
            .click(function () {
                dialog.dialog("open");
            });

        // 关闭图标：当点击时移除标签页
        tabs.on("click", "span.ui-icon-close", function () {
            var panelId = $(this).closest("li").remove().attr("aria-controls");
            var index = tabs.find("#" + panelId).index() - 1;
            var tabWsList = $("#" + panelId).data("context").currentWsList
            if (tabWsList) {
                tabWsList.forEach(function (item) {
                    item.close();
                })
            }
            if (panelId.substring(5) === $('#app-select').val()) {
                $('#app-select').val("")
                $('#app-select').trigger("chosen:updated")
            }
            $("#" + panelId).remove();
            tabs.tabs("refresh");
            tabs.tabs("option", "active", index);
        });

        tabs.on("keyup", function (event) {
            if (event.altKey && event.keyCode === $.ui.keyCode.BACKSPACE) {
                var panelId = tabs.find(".ui-tabs-active").remove().attr("aria-controls");
                $("#" + panelId).remove();
                tabs.tabs("refresh");
            }
        });

        tabs.on("tabsactivate", function (event, ui) {
            var appId = ui.newTab.attr("app-controls");
            var label = $("label[for=" + appId + "]");
            if (label.length) {
                label.addClass("cur").siblings().removeClass("cur");
            } else {
                $(".js-apps-group label").removeClass("cur");
            }
        });

        dialog.on("dialogfocus", function (event, ui) {
            $(this).closest(".ui-dialog").css("z-index", 101);
        });

        function encrypt(content) {
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
    });

    function encryptByDES(message, key) {
        var keyHex = CryptoJS.enc.Utf8.parse(key);
        var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.toString();
    }

    function makeid() {
        var text = "";
        var possible = "0123456789abcdef";

        for (var i = 0; i < 8; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

    function get_col_width(list, size, line_num, col_index) {
        var i, start, len, max_len = 0;

        start = col_index * line_num;
        for (i = start; i < size; i++) {
            if (i != start && i % line_num == 0) {
                break;
            }
            len = list[i].length;
            if (len > max_len) {
                max_len = len;
            }
        }
        return max_len;
    }

    function get_line_width(list, size, line_num) {
        var i, line_width = 0;

        for (i = 0; i < size; i += line_num) {
            line_width += get_col_width(list, size, line_num, Math.floor(i / line_num)) + 2;
        }
        return line_width;
    }

    function get_line_num(list, size, width) {
        var line_num = 1;

        while (line_num < size && get_line_width(list, size, line_num) > width) {
            line_num++;
        }
        // console.log("合适的行数: " + line_num);
        return line_num;
    }

})