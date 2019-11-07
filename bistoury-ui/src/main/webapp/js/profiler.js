var REQ_TYPE_PROFILER = 51;
var REQ_TYPE_PROFILER_STOP = 52;
var REQ_TYPE_PROFILER_STATE_SEARCH = 53;
var globalProfilerId;
var historyProfilerId;
var intervalId;

var START_STATE = "start";
var READY_STATE = "ready";
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

    if (frequency < 10) {
        bistoury.warning("抽样间隔应该大于10ms.");
        return;
    }
    initStartState();
    sendStartCommand(duration, frequency);
    $(".model-profiler-setting").modal("hide");
}

function openStartSettingModel() {
    var agentId = getAgentId();
    var lastProfiler = searchLastProfiler(agentId);
    if (lastProfiler != null && (lastProfiler.state === READY_STATE || lastProfiler.state === START_STATE)) {
        bistoury.warning("正在等待数据库更新.请稍后");
        return;
    }
    searchProfilerHistory(getAgentId());
    $(".model-profiler-setting").modal("show");
}

function analyzeCurrentProfiler() {
    console.log("analyze profiler");
    analyze(globalProfilerId);
    searchProfilerHistory(getAgentId());
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
                    var result_btn = $("#btn_result");
                    result_btn.attr("disabled", false);
                    result_btn.prop("href", reportUrl);
                    var agentId = getAgentId();
                    searchProfilerHistory(agentId);
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


function searchAnalysisState(profilerId) {
    var profilerFileVo;
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

                var history_result_btn = $("#btn-history-result");
                profilerFileVo = ret.data;
                if (ret.data == null) {
                    history_result_btn.attr("disabled", true);
                    return;
                }
                proxy = ret.data.proxyInfo;

                history_result_btn.attr("disabled", false);
                proxyUrl = proxy.ip + ":" + proxy.tomcatPort;
                var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
                history_result_btn.prop("href", reportUrl);
            }
        }
    })
    return profilerFileVo;
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

function showHistory(profilerId, startTime, duration, frequency) {
    historyProfilerId = profilerId;
    var profilerFileVo = searchAnalysisState(profilerId);
    var history_analysis_btn = $("#btn-history-analysis");
    if (profilerFileVo != null) {
        history_analysis_btn.attr("disabled", true);
        $("#model-profiler-title").html(getHistoryMessage(startTime, profilerFileVo.duration, profilerFileVo.frequency));
    } else {
        $("#model-profiler-title").html(getDefaultHistoryMessage(startTime, duration, frequency));
        history_analysis_btn.attr("disabled", false);
    }
    $(".model-profiler").modal("show");
    history_analysis_btn.attr("onclick", "analyze('" + profilerId + "')");
    var reportUrl = "html/report.html?profilerId=" + profilerId + "&proxyUrl=" + proxyUrl;
    $("#btn-history-result").prop("href", reportUrl);
}

function getHistoryMessage(startTime, duration, frequency) {
    var msg = "<span>开始时间: " + startTime + "</br>";
    msg += "实际持续时长: " + duration + " (s)</br>";
    msg += "实际间隔时长: " + frequency + " (ms)</br>";
    return msg;
}

function getDefaultHistoryMessage(startTime, duration, frequency) {
    var msg = "<span>开始时间: &nbsp;&nbsp;" + startTime + "</br>";
    msg += "预设持续时长: " + frequency + " (s)</br>";
    msg += "预设间隔时长: " + frequency + " (ms)</br>";
    return msg;
}

function initHistoryTable(data) {
    $("#history-body").empty();
    data.forEach(function (profiler) {
        var aElement = document.createElement("a");
        var trElement = document.createElement('tr')
        var tdElement = document.createElement("td");
        aElement.text = profiler.startTime;
        aElement.setAttribute("onclick", "showHistory('" + profiler.profilerId + "','" + profiler.startTime +
            "','" + profiler.duration + "','" + profiler.frequency + "')")
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
    if (lastProfiler.state === START_STATE || lastProfiler.state === READY_STATE) {
        initStartState();
        initCurrentProfilerTable(globalProfilerId);
    } else if (lastProfiler.state === ANALYZED_STATE) {
        var reportUrl = "html/report.html?profilerId=" + globalProfilerId + "&proxyUrl=" + proxyUrl;
        var result_btn = $("#btn_result");
        result_btn.attr("disabled", false);
        result_btn.prop("href", reportUrl);
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
    intervalId = setInterval(searchProfilerState, 5000);
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
    } else if (resType === "profilerstatesearch") {
        var data = result.data;
        if (data.code === 0 && data.data.state) {
            bistoury.success("性能监控正常停止");
            initAnalysisState();
            stopInterval();
            var agentId = getAgentId();
            searchProfilerHistory(agentId);
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

function searchProfilerState() {
    sendFinishStateCommand();
}

function sendFinishStateCommand() {
    var currentHost = getCurrentHost();
    var command = "profilerstatesearch " + globalProfilerId + " profilerfinishsearch";
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER_STATE_SEARCH, command, stop, handleResult);
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