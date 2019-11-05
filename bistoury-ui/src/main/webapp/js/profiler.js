var REQ_TYPE_PROFILER = 51;
var REQ_TYPE_PROFILER_STOP = 52;
var globalProfilerId;
var historyProfilerId;
var intervalId;

var START_STATE = "start";
var STOP_STATE = "stop";
var READY_STATE = "ready";
var ANALYZED_STATE = "analyzed";

var proxyUrl;


function doStartProfiler() {
    var duration = $("#profiler-duration").val();
    var frequency = $("#profiler-frequency").val();
    console.log("start profiler.");
    if (duration > 3600) {
        bistoury.warning("性能分析时长不能超过30分钟");
        return;
    }
    if (duration < 30) {
        bistoury.warning("性能分析时长不能低于30秒");
        return;
    }
    initStartState();
    sendStartCommand(duration, frequency);
    $(".model-profiler-setting").modal("hide");
}

function startProfiler() {
    $(".model-profiler-setting").modal("show");
}

function sendStopCommand() {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var command = "profilerstop " + globalProfilerId;
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER_STOP, command, stop, handleResult);
}

function stopProfiler() {
    console.log("stop profiler.");
    sendStopCommand(globalProfilerId);
}

function analyzeProfiler() {
    console.log("analyze profiler");
    analyze(globalProfilerId);
}

function sendStartCommand(duration, frequency) {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var command = "profilerstart -m 0";
    command += " -d " + duration;
    command += " -f " + frequency;
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER, command, stop, handleResult);
}

function initProfiler(profilerId) {
    globalProfilerId = profilerId;
    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
    $("#btn_result").prop("href", reportUrl);
    startInterval();
}

function handleResult(content) {
    if (!content) {
        return;
    }
    var result = JSON.parse(content);
    if (!result) {
        return;
    }
    buildProfiler(result);
}

function buildProfiler(result) {
    var resType = result.type;
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var agentId = currentHost.ip;
    console.log("result: " + result);
    if (resType === "profilerstart") {
        var data = result.data;
        if (data.code === 0) {
            initProfiler(data.id);
            bistoury.success("开始性能分析")
        } else {
            initState();
            bistoury.error("添加性能分析失败, " + data.message)
        }
    } else if (resType === "profilerstop") {
        var data = result.data;
        if (data.code === 0) {
            // stopInterval();
            // bistoury.success("手动停止性能分析成功")
            // initAnalysisState();
            // searchProfilerHistory(agentId);
        } else {
            bistoury.error("手动停止性能分析失败: " + data.message)
        }
    } else if (resType === "profilersearch") {
        if (result.code === 0 && result.data) {

            bistoury.success("性能分析正常停止,可进行下一步操作.");
            stopInterval();
            initAnalysisState();
            searchProfilerHistory(agentId);
        }
    }
}

function initHistoryEndState(profilerId) {
    $("#btn-history-analysis").attr("disabled", true);
    $("#btn-history-result").attr("disabled", false);
    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
    $("#btn-history-result").prop("href", reportUrl);
}

function analyze(profilerId) {
    $.ajax({
        "url": "profiler/analyze.do",
        "type": "post",
        "dataType": 'JSON',
        "data": {
            profilerId: profilerId
        },
        success: function (ret) {
            if (ret.status === 0) {
                proxyUrl = ret.data.proxyUrl;
                bistoury.success("分析结果成功");
                if (globalProfilerId === profilerId) {
                    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
                    $("#btn-history-result").attr("disabled", false);
                    $("#btn_result").prop("href", reportUrl);
                    $("#btn-history-result").prop("href", reportUrl);
                    initEndState();
                } else {
                    initHistoryEndState(profilerId);
                }
            } else {
                console.log(ret.message);
                bistoury.error(ret.message);
            }
        }
    })
}


function searchProfilerState() {
    $.ajax({
        "url": "profiler/state.do",
        "type": "post",
        "dataType": 'JSON',
        "data": {
            profilerId: globalProfilerId
        },
        success: function (ret) {
            if (ret.state === "stop") {
                bistoury.success("性能监控正常停止");
                initAnalysisState();
                stopInterval();
                var currentHost = $('#menu').treeview('getSelected')[0].value;
                var agentId = currentHost.ip;
                searchProfilerHistory(agentId);
            }
        }
    })
}

function searchAgentProfilerState() {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var agentId = currentHost.ip;
    $.ajax({
        "url": "profiler/agent/state.do",
        "type": "post",
        "dataType": 'JSON',
        async: false,
        "data": {
            agentId: agentId
        },
        success: function (ret) {
            if (ret.status === 0) {

            }
        }
    })
}


function searchAnalysisState(profilerId) {
    var proxy;
    $.ajax({
        "url": "profiler/analysis/state.do",
        "type": "post",
        "dataType": 'JSON',
        async: false,
        "data": {
            profilerId: profilerId
        },
        success: function (ret) {
            console.log(ret.data);
            proxy = ret.data;
            if (ret.data == null) {
                $("#btn-history-result").attr("disabled", true);
                return;
            }
            $("#btn-history-result").attr("disabled", false);
            proxyUrl = ret.data.ip + ":" + ret.data.tomcatPort;
            var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
            $("#btn-history-result").prop("href", reportUrl);
        }
    })
    return proxy;
}

function searchLastProfiler(agentId) {
    var lastProfiler;
    $.ajax({
        "url": "profiler/last.do",
        "type": "get",
        "dataType": 'JSON',
        "data": {
            agentId: agentId
        },
        async: false,
        success: function (ret) {
            console.log(ret.data);
            lastProfiler = ret.data;

        }
    })
    return lastProfiler;
}

function initHistoryTable(data) {
    $("#history-body").empty();
    data.forEach(function (profiler) {
        var aElement = document.createElement("a");
        var trElement = document.createElement('tr')
        var tdElement = document.createElement("td");
        aElement.text = profiler.startTime;
        aElement.setAttribute("onclick", "showHistory('" + profiler.profilerId + "','" + profiler.startTime + "')")
        trElement.appendChild(tdElement);
        tdElement.appendChild(aElement);
        $("#history-body").append(trElement);
    });
}

function searchProfilerHistory(agentId) {
    $.ajax({
        "url": "profiler/records.do",
        "type": "get",
        "dataType": 'JSON',
        "data": {
            agentId: agentId
        },
        async: false,
        success: function (ret) {
            if (ret.status === 0) {
                initHistoryTable(ret.data);
            }
        }
    });
}

function initNoStartState() {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var agentId = currentHost.ip;
    var lastProfiler = searchLastProfiler(agentId);


    searchProfilerHistory(agentId);
    if (lastProfiler == null) {
        initState();
        return;
    }

    globalProfilerId = lastProfiler.profilerId;
    if (lastProfiler.state === START_STATE) {
        initStartState();
        initProfiler(globalProfilerId);
    } else if (lastProfiler.state === ANALYZED_STATE) {
        initEndState();
    }
}


function initState() {
    $('#btn_start_profiler').prop('disabled', false);
    $('#btn_stop_profiler').prop('disabled', true);
    $('#btn_analyze_profiler').prop('disabled', true);
    $('#btn_result').addClass("disabled");
}

function initStartState() {
    $('#btn_start_profiler').prop('disabled', true);
    $('#btn_stop_profiler').prop('disabled', false);
    $('#btn_analyze_profiler').prop('disabled', true);
    $('#btn_result').addClass("disabled");
}

function initAnalysisState() {
    $('#btn_start_profiler').prop('disabled', false);
    $('#btn_stop_profiler').prop('disabled', true);
    $('#btn_analyze_profiler').prop('disabled', false);
    $('#btn_result').addClass("disabled");
    // var currentHost = $('#menu').treeview('getSelected')[0].value;
    // var agentId = currentHost.ip;
    // searchProfilerHistory(agentId);
}

function initEndState() {
    $('#btn_start_profiler').prop('disabled', false);
    $('#btn_stop_profiler').prop('disabled', true);
    $('#btn_analyze_profiler').prop('disabled', true);
    $('#btn_result').removeClass("disabled");
}

function stopInterval() {
    if (intervalId) {
        clearInterval(intervalId);
    }
}

function startInterval() {
    stopInterval();
    intervalId = setInterval(searchProfilerState, 3000);
}

function showHistory(profilerId, startTime) {
    historyProfilerId = profilerId;
    var proxy = searchAnalysisState(profilerId);
    $("#model-profiler-title").text("开始时间: " + startTime);
    if (proxy != null) {
        $("#btn-history-analysis").attr("disabled", true);
    } else {
        $("#btn-history-analysis").attr("disabled", false);
    }
    $(".model-profiler").modal("show");
    $("#btn-history-analysis").attr("onclick", "analyze('" + profilerId + "')");
    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
    $("#btn-history-result").prop("href", reportUrl);
}
