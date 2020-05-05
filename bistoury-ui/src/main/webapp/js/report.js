var proxyUrl;
var sampler_mode = "sampler";
var async_sampler_mode = "async_sampler";
var syncTreeData = [
    {
        text: "紧凑栈(压缩常见中间件)",
        state: {
            expanded: true
        },
        nodes:
            [
                {
                    selectable: true,
                    text: "runnable state(CPU Time)",
                    state: {
                        selected: true
                    }
                },
                {
                    text: "waiting state"
                },
                {
                    text: "blocked state"
                },
                {
                    text: "timed waiting state"
                },
                {
                    text: "all state"
                }
            ]
    },
    {
        text: "完整栈",
        state: {
            expanded: true
        },
        nodes:
            [
                {
                    text: "runnable state(CPU Time)"
                },
                {
                    text: "waiting state"
                },
                {
                    text: "blocked state"
                },
                {
                    text: "timed waiting state"
                },
                {
                    text: "all state"
                }
            ]
    }
];


var asyncTreeData = [
    {
        text: "性能火焰图",
        selectable: true,
        state: {
            selected: true
        }
    },
    // {
    //     text: "热点方法图",
    //     selectable: true,
    // },
    // {
    //     text: "java热点方法图",
    //     selectable: true,
    // },
    {
        text: "java热点方法图(压缩常用中间件)",
        selectable: true,
    },

];

function initSyncTree(info) {
    var treeViewObject = $('#svg-tree');
    treeViewObject.treeview({data: syncTreeData})
        .on('nodeSelected', function (event, data) {
            switch (data.nodeId) {
                case "0.0.0":
                    chooseSvg("runnable", true);
                    break;
                case "0.0.1":
                    chooseSvg("waiting", true);
                    break;
                case "0.0.2":
                    chooseSvg("blocked", true);
                    break;
                case "0.0.3":
                    chooseSvg("timed-waiting", true);
                    break;
                case "0.0.4":
                    chooseSvg("all", true);
                    break;
                case "0.1.0":
                    chooseSvg("runnable", false);
                    break;
                case "0.1.1":
                    chooseSvg("waiting", false);
                    break;
                case "0.1.2":
                    chooseSvg("blocked", false);
                    break;
                case "0.1.3":
                    chooseSvg("timed-waiting", false);
                    break;
                case "0.1.4":
                    chooseSvg("all", false);
                    break;
            }
        });
    chooseSvg("runnable", true);
}

function initAsyncTree(info) {
    var treeViewObject = $('#svg-tree');
    treeViewObject.treeview({data: asyncTreeData})
        .on('nodeSelected', function (event, data) {
            switch (data.nodeId) {
                case "0.0":
                    $(".svg-page-content").css("display", "block");
                    $("#tree-page").css("display", "none");
                    $("#tree-page-java").css("display", "none");
                    $("#tree-page-java-compact").css("display", "none");
                    download("async.svg");
                    break;
                // case "0.1":
                //     $(".svg-page-content").css("display", "none");
                //     $("#tree-page").css("display", "block");
                //     $("#tree-page-java").css("display", "none");
                //     $("#tree-page-java-compact").css("display", "none");
                //     addTree("hotMethod.json", "#tree-page");
                //     break;
                // case "0.2":
                //     $(".svg-page-content").css("display", "none");
                //     $("#tree-page").css("display", "none");
                //     $("#tree-page-java").css("display", "block");
                //     $("#tree-page-java-compact").css("display", "none");
                //     addTree("hotMethod-java.json", "#tree-page-java");
                //     break;
                case "0.1":
                    $(".svg-page-content").css("display", "none");
                    $("#tree-page").css("display", "none");
                    $("#tree-page-java").css("display", "none");
                    $("#tree-page-java-compact").css("display", "block");
                    addTree("hotMethod-java-compact.json", "#tree-page-java-compact");
                    break;
            }
        });
    $(".svg-page-content").css("display", "block");
    $("#tree-page").css("display", "none");
    download("async.svg");
}

$(document).ready(function () {
    var info = searchAnalysisInfo(getProfilerId());
    init(info);
    if (info.profiler.mode === async_sampler_mode) {
        initAsyncTree(info);
    } else if (info.profiler.mode === sampler_mode) {
        initSyncTree(info);
    }
});

function init(info) {
    if (info == null) {
        bistoury.error("获取性能分析的信息失败");
        return;
    }
    var profiler = info.profiler;
    $("#profiler_mode_info").html("分析模式: " + getMode(profiler.mode));
    if (profiler.mode === async_sampler) {
        $("#event_type").html("分析模式: " + info.eventType);
        $("#event_type").css("display", "");
    }
    $("#start_time").html("开始时间: " + profiler.startTime)
    $("#duration").html("实际性能分析时长:" + info.realDuration + "s")
    $("#default_duration").html("预设性能分析时长:" + profiler.duration + "s");
    $("#default_interval").html("预设性能分析间隔:" + profiler.interval + "ms");
    var proxy = info.proxyInfo;
    proxyUrl = proxy.ip + ":" + proxy.tomcatPort;
}


function chooseSvg(state, isCompact) {
    var prefix = "";
    if (isCompact) {
        prefix = "filter-"
    }
    var fileName = prefix + chooseSvgFile(state);
    download(fileName);
}

function download(fileName) {
    var url = "/profiler/download.do";
    var profilerId = getProfilerId();
    url = url + "?profilerId=" + profilerId + "&name=" + fileName + "&proxyUrl=" + proxyUrl;

    var embedHtml = ' <embed id="svg-file" src="' + url + '"/>';
    $(".svg-page-content").html(embedHtml)
}

function chooseSvgFile(state) {
    switch (state) {
        case "runnable":
            return "runnable-traces.svg";
        case "waiting":
            return "waiting-traces.svg";
        case "blocked":
            return "blocked-traces.svg";
        case "timed-waiting":
            return "timed-waiting-traces.svg";
        case "all":
            return "all-state-traces.svg";
    }
}

function getProfilerId() {
    return getParam("profilerId");
}

function getParam(key) {
    var url = new URL(location.href);
    return url.searchParams.get(key);
}

function searchAnalysisInfo(profilerId) {
    var profilerFileVo;
    $.ajax({
        "url": "/profiler/analysis/info.do",
        "type": "get",
        "dataType": 'JSON',
        async: false,
        "data": {
            profilerId: profilerId
        },
        success: function (ret) {
            if (ret.status === 0) {
                profilerFileVo = ret.data;
            } else {
                bistoury.error("获取性能分析的火焰图信息失败");
            }
        }
    })
    return profilerFileVo;
}

var treeIdSet = new Set();
var maxSamples;
var allSamples;

function addTree(fileName, treeId) {
    if (treeIdSet.has(treeId)) {
        return;
    }
    treeIdSet.add(treeId);
    var treedata = getTreeData(fileName);
    var data = [];
    //不能从0开始，有bug，会替换成j1_1
    var index = 1;
    maxSamples = treedata.nodes[0].count;
    allSamples = treedata.count;
    treedata.nodes.forEach(element => {
        data.push({
            id: index++,
            text: replaceSpecialChar(element.text) + getPercentProcess(element.count)
        })
    });

    $(treeId).jstree({
        'core': {
            "check_callback": true,
            'data': data
        }
    }).on("select_node.jstree", function (e, node) {
        var isParent = (node.node.children.length > 0);
        if (isParent) {
            var nodeID = $(treeId).jstree(true).get_selected()[0];
            var children = $(treeId).jstree(true).get_node(nodeID).children;
            $(treeId).jstree(true).delete_node(children);
            return;
        }
        var nodeId = $(treeId).jstree('get_selected')[0];
        var data = treedata.nodes;
        var isFirst = true;
        nodeId.split("-").forEach(index => {
            if (isFirst) {
                data = data[index - 1];
                isFirst = false;
            } else {
                data = data.nodes[index];
    }
    });
        createNode(treeId, nodeId, data);
        // createNode(id, nodeId, data, element.text, Math.ceil(element.count / data.count * 100), element.count

    });
}

function createNode(treeId, nodeId, parent) {
    if (parent === undefined || parent.nodes === undefined) {
        return;
    }
    var index = 0;
    if (parent.nodes.length === 1) {
        doCreateNode(nodeId, treeId, nodeId + "-0", parent.nodes[0].text, parent.nodes[0].count);
        createNode(treeId, nodeId + "-0", parent.nodes[0]);
    } else {
        parent.nodes.forEach(element => {
            doCreateNode(nodeId, treeId, nodeId + "-" + (index++), element.text, element.count);
    });
    }

    $(treeId).jstree("open_node", nodeId);
}

function doCreateNode(parent_node, treeId, id, text, count) {
    $(treeId).jstree().create_node(parent_node, {
        "id": id,
        "text": replaceSpecialChar(text) + getPercentProcess(count)
    }, "last", function () {

    });
}

function getPercentProcess(count) {
    var displayPercent = Math.ceil(count / maxSamples * 100);
    var percent = Math.ceil(count / allSamples * 100);
    return "&nbsp;&nbsp;&nbsp;" + '<progress value="' + displayPercent + '" max="100">' + '</progress> ' + count + " samples(" + percent + "%)";
}

function replaceSpecialChar(text) {
    return text.replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


function getTreeData(fileName) {
    var data;
    $.ajax({
        "url": "/profiler/download.do",
        "type": "get",
        "dataType": 'JSON',
        async: false,
        "data": {
            profilerId: getProfilerId(),
            name: fileName,
            proxyUrl: proxyUrl,
            contentType: "application/json"
        },
        success: function (ret) {
            if (ret.status === 0) {
                data = ret.data;
            } else {
                bistoury.error("获取性能分析的热点数据失败");
            }
        }
    })
    return data;
}