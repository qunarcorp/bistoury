var REQ_TYPE_PROFILER = 51;
var REQ_TYPE_PROFILER_STOP = 52;
var globalProfilerId;
var intervalId;
var isStop = false;
initNoStartState();

function startProfiler() {
    console.log("start profiler.");
    initStartState();
    sendStartCommand();
}

function sendStopCommand() {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var command = "profilerstop " + globalProfilerId;
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER_STOP, command, stop, handleResult);
}

function stopProfiler() {
    console.log("stop profiler.");
    sendStopCommand(globalProfilerId);
    stopInterval();
}

function analyzeProfiler() {
    console.log("analyze profiler");
    analyze(globalProfilerId);
}

function redirectResult() {

}

function sendStartCommand(duration, frequency) {
    var currentHost = $('#menu').treeview('getSelected')[0].value;
    var command = "profilerstart -m 0 -d 60 -f 20";
    bistouryWS.sendCommand(currentHost, REQ_TYPE_PROFILER, command, stop, handleResult);
}

function initProfiler(profilerId) {
    globalProfilerId = profilerId;
    var reportUrl = "html/report.html?profilerId=" + profilerId;
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
    console.log("result: " + result);
    if (resType === "profilerstart") {
        var data = result.data;
        if (data.code === 0) {
            initProfiler(data.id);
            bistoury.success("开始性能分析")
        } else {
            bistoury.error("添加性能分析失败, " + data.message)
        }
    } else if (resType === "profilerstop") {
        var data = result.data;
        if (data.code === 0) {
            isStop = true;
            stopInterval();
            bistoury.success("手动停止性能分析成功")
            initAnalysisState();
        } else {
            bistoury.error("手动停止性能分析失败: " + data.message)
        }
    } else if (resType === "profilersearch") {
        if (result.code === 0 && result.data) {
            bistoury.success("性能分析正常停止,可进行下一步操作.");
            isStop = true;
            stopInterval();
            initAnalysisState();
        }
    }
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
                bistoury.success("分析结果成功");
                initEndState();
            } else {
                console.log(ret.message);
                bistoury.error(ret.message);
            }
        }
    })
}


function searchProfiler() {
    if (isStop) {
        stopInterval();
        return;
    }
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
            }
        }
    })
}

function initNoStartState() {
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
    $('#btn_start_profiler').prop('disabled', true);
    $('#btn_stop_profiler').prop('disabled', true);
    $('#btn_analyze_profiler').prop('disabled', false);
    $('#btn_result').addClass("disabled");
}

function initEndState() {
    $('#btn_start_profiler').prop('disabled', true);
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
    intervalId = setInterval(searchProfiler, 3000);
}
