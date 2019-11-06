var REQ_TYPE_PROFILER = 51;
var REQ_TYPE_PROFILER_STOP = 52;
var globalProfilerId;
var historyProfilerId;
var intervalId;

var START_STATE = "start";
var STOP_STATE = "stop";
var ANALYZED_STATE = "analyzed";

var proxyUrl;

function startProfiler() {
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

function openStartSettingModel() {
    $(".model-profiler-setting").modal("show");
}

function analyzeCurrentProfiler() {
    console.log("analyze profiler");
    analyze(globalProfilerId);
}

function initCurrentProfilerTable(profilerId) {
    globalProfilerId = profilerId;
    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
    $("#btn_result").prop("href", reportUrl);
    startInterval();
}


function initHistoryEndState(profilerId) {
    $("#btn-history-analysis").attr("disabled", true);
    var history_result_btn = $("#btn-history-result");
    history_result_btn.attr("disabled", false);
    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
    history_result_btn.prop("href", reportUrl);
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
                    var history_result_btn = $("#btn-history-result");
                    history_result_btn.attr("disabled", false);
                    $("#btn_result").prop("href", reportUrl);
                    history_result_btn.prop("href", reportUrl);
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
        "url": "profiler/get.do",
        "type": "get",
        "dataType": 'JSON',
        "data": {
            profilerId: globalProfilerId
        },
        success: function (res) {
            if (res.status === 0) {
                if (res.data.state === STOP_STATE) {
                    bistoury.success("性能监控正常停止");
                    initAnalysisState();
                    stopInterval();
                    var agentId = getAgentId();
                    searchProfilerHistory(agentId);
                }
            }
        }
    })
}

function searchAnalysisState(profilerId) {
    var proxy;
    $.ajax({
        "url": "profiler/analysis/state.do",
        "type": "get",
        "dataType": 'JSON',
        async: false,
        "data": {
            profilerId: profilerId
        },
        success: function (ret) {
            if (ret.status === 0) {
                proxy = ret.data;
                var history_result_btn = $("#btn-history-result");
                if (ret.data == null) {
                    history_result_btn.attr("disabled", true);
                    return;
                }

                history_result_btn.attr("disabled", false);
                proxyUrl = ret.data.ip + ":" + ret.data.tomcatPort;
                var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
                history_result_btn.prop("href", reportUrl);
            }
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

function showHistory(profilerId, startTime) {
    historyProfilerId = profilerId;
    var proxy = searchAnalysisState(profilerId);
    $("#model-profiler-title").text("开始时间: " + startTime);
    var history_analysis_btn = $("#btn-history-analysis");
    if (proxy != null) {
        history_analysis_btn.attr("disabled", true);
    } else {
        history_analysis_btn.attr("disabled", false);
    }
    $(".model-profiler").modal("show");
    history_analysis_btn.attr("onclick", "analyze('" + profilerId + "')");
    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
    $("#btn-history-result").prop("href", reportUrl);
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
        initCurrentProfilerTable(globalProfilerId);
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
    intervalId = setInterval(searchProfilerState, 10000);
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
    console.log("result: " + result);
    if (resType === "profilerstart") {
        var data = result.data;
        if (data.code === 0) {
            initCurrentProfilerTable(data.data.profilerId);
            bistoury.success("开始性能分析")
        } else {
            initState();
            bistoury.error("添加性能分析失败, " + data.message)
        }
    } else if (resType === "profilerstop") {
        var data = result.data;
        if (data.code === 0) {

        } else {
            bistoury.error("手动停止性能分析失败: " + data.message)
        }
    }
}

function getCurrentHost() {
    return $('#menu').treeview('getSelected')[0].value;
}

function getAgentId() {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    return currentHost.ip;
}

function sendStopCommand() {
    var currentHost = getCurrentHost();
    var command = "profilerstop " + globalProfilerId;
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER_STOP, command, stop, handleResult);
}

function stopProfiler() {
    console.log("stop profiler.");
    sendStopCommand(globalProfilerId);
}

function sendStartCommand(duration, frequency) {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var command = "profilerstart -m 0";
    command += " -d " + duration;
    command += " -f " + frequency;
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER, command, stop, handleResult);
}