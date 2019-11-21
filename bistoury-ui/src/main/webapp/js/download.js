$(document).ready(function () {
    var KB = 1024.0;
    var MB = 1024.0 * KB;
    var GB = 1024.0 * MB;
    var TB = 1024.0 * GB;
    var currentHost;

    function getDownloadFileList(type) {
        $('#download-file-table').bootstrapTable('removeAll');
        bistouryWS.sendCommand(currentHost, 701, type, null, handleResult)
    }

    function handleResult(content) {
        if (!content) {
            return;
        }
        var result = JSON.parse(content);
        if (!result) {
            return;
        }
        var resType = result.type;
        if (resType == "downloadfilelist") {
            var res = result.data;
            if (res.code == 0) {
                $('#download-file-table').bootstrapTable('removeAll');
                $('#download-file-table').bootstrapTable('append', res.data);
            } else {
                bistoury.error(res.message);
            }

        }
        console.log(JSON.parse(content))
    }

    function getAppList() {
        $.ajax({
            "url": "/getApps.do",
            "type": "get",
            success: function (ret) {
                if (ret.status === 0) {
                    var apps = [];
                    ret.data.forEach(function (app) {
                        apps.push({text: app, value: app, lazyLoad: true, selectable: false, tags: ["0"]})
                    })
                    initMenu(apps);
                } else {
                    bistoury.error('获取应用列表失败');
                }
            }

        });
    }

    function getHosts(node, func) {
        $.ajax({
            "url": "/getHosts.do",
            "type": "get",
            "data": {
                "appCode": node.value
            },
            success: function (ret) {
                if (ret.status == 0) {
                    var list = [];

                    ret.data.forEach(function (machine) {
                        list.push({text: machine.host, value: machine, selectable: true});
                    })
                    func(list);
                } else {
                    bistoury.error('获取主机列表失败, 错误信息：' + data.message);
                }
            }
        });

    }

    $("#list-all-file").click(function () {
        removeListFileActiveClass();
        $(this).addClass("active");
        getDownloadFileList("all");
    });

    $("#list-log-file").click(function () {
        removeListFileActiveClass();
        $(this).addClass("active");
        getDownloadFileList("log");
    });

    $("#list-dump-file").click(function () {
        removeListFileActiveClass();
        $(this).addClass("active");
        getDownloadFileList("dump");
    });

    $("#list-other-file").click(function () {
        removeListFileActiveClass();
        $(this).addClass("active");
        getDownloadFileList("other");
    });

    function removeListFileActiveClass() {
        $('#download-file-table').bootstrapTable('removeAll');
        $("#list-file-btn button").removeClass("active");
    }


    function initMenu(apps) {
        $('#menu').treeview({
            data: apps,
            levels: 2,
            propagateCheckEvent: true,
            hierarchicalCheck: true,
            loadingIcon: "glyphicon glyphicon-refresh",//懒加载过程中显示的沙漏字符图标,
            collapseIcon: "glyphicon glyphicon-menu-down",
            expandIcon: "glyphicon glyphicon-menu-right",
            lazyEmptyIcon: "glyphicon glyphicon-minus",
            lazyLoad: getHosts,
            onNodeSelected: function (event, data) {
                currentHost = data.value;
                removeListFileActiveClass();
                $("#list-log-file").addClass("active");
                $("#download-file-panel").show();
                getDownloadFileList("log");
            }
        });
    }

    function initDownloadFileTable() {
        $('#download-file-table').bootstrapTable({
            data: [{}],
            striped: true, //是否显示行间隔色
            pageNumber: 1, //初始化加载第一页
            pagination: true,//是否分页
            sidePagination: 'client',//server:服务器端分页|client：前端分页
            pageSize: 10,//单页记录数
            pageList: [10, 20, 50, 100],//可选择单页记录数
            showRefresh: true,//刷新按钮
            search: true,
            toolbar: "#download-table-toolbar",
            searchAlign: "right",
            buttonsAlign: "right",
            columns: [{
                title: 'File Name',
                field: 'name',
                sortable: true,
                searchable: true
            }, {
                title: 'File Path',
                field: 'path',
                sortable: true,
                searchable: true
            }, {
                title: 'File Size',
                field: 'size',
                sortable: true,
                searchable: true,
                width: 180,
                formatter: function (value, row, index) {
                    if (value) {
                        return getMemory(value);
                    } else if (value == 0) {
                        return 0;
                    } else {
                        return "-";
                    }
                }
            }, {
                title: 'Last Modified Time',
                field: 'modifiedTime',
                sortable: true,
                searchable: true,
                width: 180,
                formatter: function (value, row, index) {
                    if (value) {
                        return dateFormat(value);
                    } else {
                        return "-";
                    }
                }
            }, {
                title: "Download",
                field: "operate",
                width: 100,
                events: operateEvents,
                formatter: function () {
                    return "<a class='download-file' style='cursor: pointer'>download</a>"
                }
            }],
            onRefresh: function () {
                $('#download-file-table').bootstrapTable('removeAll');
                getDownloadFileList();
            }
        });
    }

    window.operateEvents = {
        "click .download-file": function (e, value, row, index) {
            console.log(row);
            var file = row;
            download(file);
        }
    }

    function download(file) {
        fetch("/file/download.do", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: "appcode=" + currentHost.appCode
                + "&host=" + currentHost.host
                + "&agentIp=" + currentHost.ip
                + "&path=" + file.path
                + "&filename=" + file.name
        }).then(function (res) {
            if (res.ok) {
                var filename = res.headers.get('Content-Disposition').split("=")[1];
                res.blob().then(function (data) {
                    var blobUrl = window.URL.createObjectURL(data);
                    var a = document.createElement('a');
                    a.setAttribute('href', blobUrl);
                    a.setAttribute('download', filename);
                    a.click();
                }).catch(function (reason) {
                    console.log(reason);
                    bistoury.error(reason)
                })
            } else {
                res.text().then(function (value) {
                    bistoury.error(value);
                })
            }

        }).catch(function (reason) {
            console.log(reason)
            bistoury.error(reason)
        })
    }

    function dateFormat(dateStr) {
        var date = new Date(dateStr)
        var year = date.getFullYear();
        var month = ("0" + (date.getMonth() + 1)).slice(-2);
        var day = ("0" + date.getDate()).slice(-2);
        var h = ("0" + date.getHours()).slice(-2);
        var m = ("0" + date.getMinutes()).slice(-2);
        var s = ("0" + date.getSeconds()).slice(-2);
        return year + "-" + month + "-" + day + " " + h + ":" + m + ":" + s;
    }

    function getMemory(memory) {
        if (memory > TB) {
            return getPoint(memory / TB) + " TB";
        }
        if (memory > GB) {
            return getPoint(memory / GB) + " GB";
        }
        if (memory > MB) {
            return getPoint(memory / MB) + " MB";
        }
        if (memory > KB) {
            return getPoint(memory / KB) + " KB"
        }
        return getPoint(memory) + " B";
    }

    function getPoint(value) {
        return Math.round(value * 10000) / 10000;
    }

    function init() {
        initDownloadFileTable();
        getAppList();
    }


    init()

})