var REQ_TYPE_PROFILER = 51;
var REQ_TYPE_PROFILER_STOP = 52;
var REQ_TYPE_PROFILER_STATE_SEARCH = 53;
var globalProfilerId;
var searchStateIntervalId;

var curDuration;
var keepedTime = 0;
var startTime = null;
var processIntervalId;
var currentUiTime = null;

var READY_STATE = "ready";
var START_STATE = "start";
var STOP_STATE = "stop";

var async_sampler = "async_sampler";
var sampler = "sampler";

var async_sampler_code = "0";
var sampler_code = "1";

var globalProfilerState;

function startProfiler() {
    var duration = $("#profiler-duration").val();
    console.log("start profiler.");
    if (duration > 3600) {
        bistoury.warning("性能分析时长不能超过一小时");
        return;
    }
    if (duration < 30) {
        bistoury.warning("性能分析时长不能低于30秒");
        return;
    }
    initStartState();
    curDuration = duration;
    sendStartRequest(duration);
    $(".model-profiler-setting").modal("hide");
}

function openStartSettingModel() {
    var lastProfiler = searchLastProfiler();
    var lastProfileId = lastProfiler == null ? null : lastProfiler.profilerId;
    if (lastProfileId !== globalProfilerId) {
        bistoury.warning("最新的性能分析记录已经变更,请刷新界面");
        return;
    }
    $(".model-profiler-setting").modal("show");
}

function initCurrentProfilerTable(profilerId) {
    globalProfilerId = profilerId;
    var reportUrl = "html/report.html?profilerId=" + profilerId;
    $("#btn_result").prop("href", reportUrl);
    startSearchStateInterval();
}

function searchLastProfiler() {
    var lastProfiler;
    $.ajax({
        "url": "profiler/last.do",
        "type": "get",
        "dataType": 'JSON',
        "data": {
            agentId: getAgentId(),
            appCode: getAppCode()
        },
        async: false,
        success: function (ret) {
            console.log(ret.data);
            if (ret.data != null) {
                lastProfiler = ret.data.info;
                startTime = lastProfiler.startTime;
                currentUiTime = ret.data.curTime;
            }
        }
    });
    return lastProfiler;
}

function searchProfilerHistory() {
    $.ajax({
        "url": "profiler/records.do",
        "type": "get",
        "dataType": 'JSON',
        "data": {
            appCode: getAppCode(),
            agentId: getAgentId()
        },
        async: false,
        success: function (ret) {
            if (ret.status === 0) {
                initHistoryTable(ret.data);
            }
        }
    });
}

function getMode(mode) {
    if (mode === async_sampler) {
        return "异步抽样";
    } else if (mode === sampler) {
        return "抽样";
    }
}

function initHistoryTable(data) {
    $("#history-body").empty();
    data.forEach(function (profiler) {
        var aElement = document.createElement("a");
        var trElement = document.createElement('tr');
        var tdElement = document.createElement("td");
        aElement.text = profiler.startTime;
        var reportUrl = "html/report.html?profilerId=" + profiler.profilerId;
        aElement.setAttribute("href", reportUrl);
        aElement.setAttribute("target", "_blank");
        trElement.appendChild(tdElement);
        tdElement.appendChild(aElement);

        var durationText = document.createTextNode(profiler.duration);
        var frequencyText = document.createTextNode(profiler.frequency);
        var modeText = document.createTextNode(getMode(profiler.mode));
        var durationElement = document.createElement("td");
        var modeElement = document.createElement("td");
        var frequencyElement = document.createElement("td");
        modeElement.appendChild(modeText)
        durationElement.appendChild(durationText);
        frequencyElement.appendChild(frequencyText);

        trElement.appendChild(modeElement);
        trElement.appendChild(durationElement);
        trElement.appendChild(frequencyElement);

        $("#history-body").append(trElement);
    });
}

function initNoStartState() {
    var lastProfiler = searchLastProfiler();

    searchProfilerHistory();
    if (lastProfiler == null) {
        initState();
        return;
    }

    globalProfilerId = lastProfiler.profilerId;
    if (lastProfiler.state === START_STATE || lastProfiler.state === READY_STATE) {
        curDuration = lastProfiler.duration;
        globalProfilerState = START_STATE;
        startProcessStateInterval();
        initStartState();
        initCurrentProfilerTable(globalProfilerId);
        return;
    }
    globalProfilerState = STOP_STATE;
    initState();
}

function initState() {
    $('#btn_start_profiler').prop('disabled', false);
    $('#btn_stop_profiler').prop('disabled', true);
    $('#btn_result').addClass("disabled");
}

function initStartState() {
    $('#btn_start_profiler').prop('disabled', true);
    $('#btn_stop_profiler').prop('disabled', false);
    $('#btn_result').addClass("disabled");
}

function initEndState() {
    $('#btn_start_profiler').prop('disabled', false);
    $('#btn_stop_profiler').prop('disabled', true);
    $('#btn_result').removeClass("disabled");
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
    var data = result.data;
    console.log("result: " + result);
    if (resType === "profilerstart") {
        if (data.code === 0) {
            globalProfilerState = READY_STATE;
            initCurrentProfilerTable(data.data.profilerId);
            bistoury.success("开始性能分析");
            currentUiTime = null;
            startProcessStateInterval();
        } else {
            initState();
            bistoury.error("添加性能分析失败, " + data.message)
        }
    } else if (resType === "profilerstop") {
        if (data.code === 0) {
            globalProfilerState = STOP_STATE;
        } else {
            bistoury.error("手动停止性能分析失败: " + data.message)
        }
    } else if (resType === "profilerstatesearch") {
        if (data.code === 0) {
            var status = data.data.status;
            if (status === "running") {
                globalProfilerState = START_STATE;
            } else if (status === "finish") {
                bistoury.success("性能分析正常停止");
                stopSearchStateInterval();
                stopProcessStateInterval();
                initEndState();
                searchProfilerHistory();
                globalProfilerState = STOP_STATE;
            } else if (status === "error") {
                bistoury.success("性能分析异常结束");
                stopSearchStateInterval();
                stopProcessStateInterval();
                initNoStartState();
                globalProfilerState = STOP_STATE;
            }
        }
    }
}

function getCurrentHost() {
    return $('#menu').treeview('getSelected')[0].value;
}

function getAppCode() {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    return currentHost.appCode;
}

function getAgentId() {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    return currentHost.ip;
}

function searchProfilerState() {
    sendStateCommand();
}

function sendStateCommand() {
    var currentHost = getCurrentHost();
    var command;
    if (globalProfilerState === READY_STATE) {
        command = "profilerstatesearch " + globalProfilerId + " profilerstartsearch";
    } else {
        command = "profilerstatesearch " + globalProfilerId + " profilerfinishsearch";
    }
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

function sendStartRequest(duration) {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var command = "profilerstart " + getAppCode();
    command += " " + duration;
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER, command, stop, handleResult);
}


function changeProcessState() {
    var profiler_process = document.getElementById("cpu-profiler-process");
    keepedTime += 1;
    var percent = Math.ceil(keepedTime / curDuration * 100);
    percent = percent > 99 ? 99 : percent;
    profiler_process.style.width = percent + "%";
    profiler_process.innerHTML = profiler_process.style.width;
}

function stopSearchStateInterval() {
    if (searchStateIntervalId) {
        clearInterval(searchStateIntervalId);
    }
}

function startSearchStateInterval() {
    stopSearchStateInterval();
    searchStateIntervalId = setInterval(searchProfilerState, 3000);
}

function startProcessStateInterval() {
    stopProcessStateInterval();
    $("#profiler-process-div").css('display', 'block');
    if (currentUiTime === null) {
        keepedTime = 0;
    } else {
        var startMoment = moment(startTime, "yyyy-MM-dd HH:mm:ss");
        var curMoment = moment(currentUiTime, "yyyy-MM-dd HH:mm:ss");
        keepedTime = moment.duration(curMoment - startMoment).asSeconds();
    }
    processIntervalId = setInterval(changeProcessState, 1000);
}

function stopProcessStateInterval() {
    var profiler_process = document.getElementById("cpu-profiler-process");
    profiler_process.style.width = 0 + "%";
    profiler_process.innerHTML = profiler_process.style.width;
    $("#profiler-process-div").css('display', 'none');
    if (processIntervalId) {
        clearInterval(processIntervalId);
    }
}

