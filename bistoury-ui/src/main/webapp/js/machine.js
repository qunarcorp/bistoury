$(document).ready(function () {
    var days = 3;
    var storeDays = days * 24 * 60 * 60 * 1000;//3天
    var keepRunning = false;
    var interval = 5000;
    var minutes = 10;
    var keepTime = minutes * 60 * 1000;//10分钟
    var startTime = new Date().getTime();
    var KB = 1.0;
    var MB = 1024.0 * KB;
    var GB = 1024.0 * MB;
    var TB = 1024.0 * GB;
    var currentHost = {};
    var currentThreadTime = new Date().getTime();
    var currentMemTime = currentThreadTime;
    var currentVisuaTime = currentThreadTime;

    var firstIn = {
        jarInfo: true,
        heapHisto: true,
        appConfig: true
    }

    var threadTimeAxis = [];
    var memTimeAxis = [];
    var visuaTimeAxis = [];
    for (var i = 0; i < 20; i++) {
        var current = dateFormat(currentThreadTime -= interval).substring(10);
        threadTimeAxis.push(current);
        memTimeAxis.push(current);
        visuaTimeAxis.push(current);

    }
    threadTimeAxis.reverse();
    memTimeAxis.reverse();
    visuaTimeAxis.reverse();

    $(".curve").css("width", $(".content").width());
    var threadCurve = echarts.init(document.getElementById("thread-curve"));

    var metaspaceCurve = echarts.init(document.getElementById("metaspace-curve"));
    var psOldGenCurve = echarts.init(document.getElementById("psOldGen-curve"));
    var compressedClassSpaceCurve = echarts.init(document.getElementById("compressedClassSpace-curve"));
    var pSSurvivorSpaceCurve = echarts.init(document.getElementById("pSSurvivorSpace-curve"));
    var pSEdenSpaceCurve = echarts.init(document.getElementById("pSEdenSpace-curve"));
    var codeCacheCurve = echarts.init(document.getElementById("codeCache-curve"));
    var codeHeap_non_nmethods_curve = echarts.init(document.getElementById("codeHeap-non-nmethods"));
    var codeHeap_non_profiled_nmethods_curve = echarts.init(document.getElementById("codeHeap-non-profiled-nmethods"));

    var compileTimeCurve = echarts.init(document.getElementById("compile-time-curve"));
    var classLoaderTimeCurve = echarts.init(document.getElementById("class-loader-time-curve"));
    var gcTimeCurve = echarts.init(document.getElementById("gc-time-curve"));
    var edenSpaceCurv = echarts.init(document.getElementById("eden-space-curve"));
    var survivor0Curve = echarts.init(document.getElementById("survivor-0-curve"));
    var survivor1Curve = echarts.init(document.getElementById("survivor-1-curve"));
    var oldGenCurve = echarts.init(document.getElementById("old-gen-curve"));
    var permGenCurve = echarts.init(document.getElementById("perm-gen-curve"));
    var gcMetaspaceCurve = echarts.init(document.getElementById("gc-metaspace-curve"));

    var currentThreadCounts = [];
    var peakThreadCounts = [];
    var totalStartedThreadCounts = [];
    var daemonThreadCounts = [];

    var metaspaceUsed = [];
    var metaspaceCommit = [];
    var psOldGenUsed = [];
    var psOldGenCommit = [];
    var compressedClassSpaceUsed = [];
    var compressedClassSpaceCommit = [];
    var pSSurvivorSpaceUsed = [];
    var pSSurvivorSpaceCommit = [];
    var pSEdenSpaceUsed = [];
    var pSEdenSpaceCommit = [];
    var codeCacheUsed = [];
    var codeCacheCommit = [];
    var codeHeapNonMmethodsUsed = [];
    var codeHeapNonMmethodsCommit = [];
    var codeHeapNonProfilednmethodsUsed = [];
    var codeHeapNonProfilednmethodsCommit = [];

    var compileTime = [];
    var classLoaderTime = [];
    var gcTime = [];
    var edenSpace = [];
    var survivor0 = [];
    var survivor1 = [];
    var oldGen = [];
    var permGen = [];
    var gcMetaspace = [];

    var memPool = {};


    function buildThreadInfo(jvmInfo) {
        currentThreadCounts.push(jvmInfo.currentThreadCount);
        peakThreadCounts.push(jvmInfo.peakThreadCount);
        totalStartedThreadCounts.push(jvmInfo.totalStartedThreadCount);
        daemonThreadCounts.push(jvmInfo.daemonThreadCount);
        threadTimeAxis.push(dateFormat(currentThreadTime += interval).substring(10));
        shift(threadTimeAxis);
        shift(currentThreadCounts);
        shift(peakThreadCounts);
        shift(totalStartedThreadCounts);
        shift(daemonThreadCounts)
        threadCurve.setOption({
            title: {
                text: "线程",
                subtext: "活动线程: " + jvmInfo.currentThreadCount + " 守护线程: " + jvmInfo.daemonThreadCount
                    + " 启动总数: " + jvmInfo.totalStartedThreadCount + " 峰值: " + jvmInfo.peakThreadCount
            },
            xAxis: {
                data: threadTimeAxis
            },
            series: [{
                data: currentThreadCounts
            }, {
                data: daemonThreadCounts
            }, {
                data: totalStartedThreadCounts
            }, {
                data: peakThreadCounts
            }]
        })
    }

    function buildMemPoolCurve(memPools) {
        memTimeAxis.push(dateFormat(currentMemTime += interval).substring(10));
        shift(memTimeAxis);
        memPools.forEach(function (value) {
            var mem = memPool[value.key];
            if (!mem) {
                return;
            }
            mem.used.push(getMemoryByMB(value.used));
            mem.committed.push(getMemoryByMB(value.committed));
            shift(mem.used);
            shift(mem.committed)
            mem.curve.setOption({
                title: {
                    text: value.name,
                    subtext: "Max: " + getMemory(value.max) + "  Init: " + getMemory(value.init)
                },
                xAxis: {
                    data: memTimeAxis
                },
                series: [{
                    data: mem.used
                }, {
                    data: mem.committed
                }]
            })
        })
    }

    function buildVisuaGcCurve(visuaGc) {
        visuaTimeAxis.push(dateFormat(currentVisuaTime += interval).substring(10));
        compileTime.push(visuaGc.totalCompile);
        classLoaderTime.push(getTimeMs(visuaGc.classLoadTime));
        gcTime.push(getTimeMs(visuaGc.edenGCTime));
        edenSpace.push(getMemoryByMB(visuaGc.edenUsed));
        survivor0.push(getMemoryByMB(visuaGc.survivor0Used));
        survivor1.push(getMemoryByMB(visuaGc.survivor1Used));
        oldGen.push(getMemoryByMB(visuaGc.tenuredUsed));
        permGen.push(getMemoryByMB(visuaGc.permUsed));
        gcMetaspace.push(getMemoryByMB(visuaGc.metaUsed));
        shift(visuaTimeAxis);
        shift(compileTime);
        shift(classLoaderTime);
        shift(gcTime);
        shift(edenSpace);
        shift(survivor0);
        shift(survivor1);
        shift(oldGen);
        shift(permGen);
        shift(gcMetaspace)

        visuaGcCurveBuild(compileTimeCurve, "Compile time", visuaGc.totalCompile + " compiles - " + getTimeSec(visuaGc.totalCompileTime) + " s", memTimeAxis, compileTime)
        visuaGcCurveBuild(classLoaderTimeCurve, "Class loader time", visuaGc.classesLoaded + " loaded, " + visuaGc.classesUnloaded + " unloaded - " + getTimeSec(visuaGc.classLoadTime) + " s", memTimeAxis, classLoaderTime)
        visuaGcCurveBuild(gcTimeCurve, "GC time", visuaGc.edenGCEvents + " collections, " + getTimeMs(visuaGc.edenGCTime) + "ms, Last Causes: " + visuaGc.lastGCCause, memTimeAxis, gcTime)
        visuaGcCurveBuild(edenSpaceCurv, "Eden Space ( MaxCapacity: " + getMemoryByMB(visuaGc.edenSize) + "MB, Capacity: " + getMemoryByMB(visuaGc.edenCapacity) + "MB )", "Used: " + getMemoryByMB(visuaGc.edenUsed) + "MB, " + visuaGc.edenGCEvents + " collections, " + getTimeMs(visuaGc.edenGCTime) + "ms", memTimeAxis, edenSpace)
        visuaGcCurveBuild(survivor0Curve, "Survivor 0 ( MaxCapacity: " + getMemoryByMB(visuaGc.survivor0Size) + "MB, Capacity: " + getMemoryByMB(visuaGc.survivor0Capacity) + "MB )", "Used: " + getMemoryByMB(visuaGc.survivor0Used) + "MB", memTimeAxis, survivor0)
        visuaGcCurveBuild(survivor1Curve, "Survivor 1 ( MaxCapacity: " + getMemoryByMB(visuaGc.survivor1Size) + "MB, Capacity: " + getMemoryByMB(visuaGc.survivor1Capacity) + "MB )", "Used: " + getMemoryByMB(visuaGc.survivor1Used) + "MB", memTimeAxis, survivor1)
        visuaGcCurveBuild(oldGenCurve, "Old Gen ( MaxCapacity: " + getMemoryByMB(visuaGc.tenuredSize) + "MB, Capacity: " + getMemoryByMB(visuaGc.tenuredCapacity) + "MB )", "Used: " + getMemoryByMB(visuaGc.tenuredUsed) + "MB, " + visuaGc.tenuredGCEvents + " collections, " + getTimeMs(visuaGc.tenuredGCTime) + "ms", memTimeAxis, oldGen)
        visuaGcCurveBuild(permGenCurve, "Perm Gen ( MaxCapacity: " + getMemoryByMB(visuaGc.permSize) + "MB, Capacity: " + getMemoryByMB(visuaGc.permCapacity) + "MB )", "Used: " + getMemoryByMB(visuaGc.permUsed) + "MB", memTimeAxis, permGen)
        visuaGcCurveBuild(gcMetaspaceCurve, "Metaspace ( MaxCapacity: " + getMemoryByMB(visuaGc.metaSize) + "MB, Capacity: " + getMemoryByMB(visuaGc.metaCapacity) + "MB )", "Used: " + getMemoryByMB(visuaGc.metaUsed) + "MB", memTimeAxis, gcMetaspace)
    }

    function visuaGcCurveBuild(curve, title, subtext, visuaTimeAxis, data) {
        curve.setOption({
            title: {
                text: title,
                subtext: subtext
            }, xAxis: {
                data: visuaTimeAxis
            },
            series: [{
                data: data
            }]
        })
    }

    function buildHeapHisto(heapHistos) {
        if (!heapHistos || heapHistos.length <= 0) {
            bistoury.warning("没有查询到数据");
            return;
        }
        heapHistos.forEach(function (heapHisto) {
            heapHisto.className = heapHisto.className.replace(/\</g, "&lt").replace(/\>/g, "&gt");
        })
        $('#jvm-heap-histo-table').bootstrapTable('append', heapHistos);
    }

    function buildJarInfo(jarInfos) {
        var newJarInfos = [];
        jarInfos.forEach(function (jarInfo) {
            var newJarInfo = {};
            newJarInfo.fileName = jarInfo;
            newJarInfos.push(newJarInfo);
        })
        $('#jar-dep-table').bootstrapTable('append', newJarInfos);
    }

    function buildAppConfigTable(appConfig) {
        $('#appconfig-table').bootstrapTable('append', appConfig);
    }

    function buildFileContent(content, fileName) {
        $("#file-content-panel").empty();
        var language = getMode(fileName);
        if (language == 'qtable') {
            language = 'json';
            if (bistoury.isJsonDataStr(content)) {
                content = JSON.stringify(JSON.parse(content), null, 4);
            }
        }
        var content = hljs.highlight(language, content);
        var codetext = content.value.split('\n');
        var code = $("<code></code>");
        var lineNumberPanel = $("<div></div>").addClass("line-number");
        var codeLinePanel = $("<div></div>").addClass("code-line");
        codetext.forEach(function (value, index) {
            var lineNumber = $("<div></div>").addClass("number").append($("<span></span>").append(index + 1));
            var codeLine = $("<div></div>").addClass("line").append($("<span></span>").addClass("code-content").append(value));
            lineNumberPanel.append(lineNumber);
            code.append(codeLine);
        })
        var pre = $("<pre></pre>").append(code)
        codeLinePanel.append(pre);
        $("#file-content-panel").append(lineNumberPanel).append(codeLinePanel);
    }

    function buildAllThreadTable(threads, totalCpuTime) {
        $("#thread-total-cpu-time").text(getTimeMs(totalCpuTime) + " ms");

        $("#all-thread-table tbody").empty();
        $.each(threads, function (index, thread) {
            var cpuTime = getTimeMs(thread.cpuTime);
            var cpuTimePercent = getTimePercent(thread.cpuTime, totalCpuTime);
            var cpuTimeTd = $("<td></td>").append('<div class="progress"><span class="progress-value">' + cpuTime + ' ms - ' + cpuTimePercent + '%</span><div class="progress-bar" role="progressbar" aria-valuenow="' + cpuTimePercent + '" aria-valuemin="0" aria-valuemax="100" style="width: ' + cpuTimePercent + '%;"></div></div>');
            var stateTd = $("<td></td>").append(thread.state);
            if (thread.state == "RUNNABLE") {
                stateTd.addClass("run");
            }
            var nameTd = $("<td></td>").append(thread.name);
            var threadTr = $("<tr></tr>").append(cpuTimeTd).append(stateTd).append(nameTd);
            threadTr.click(function () {
                $("#thread-detail-info table td[datatype='value']").text("");
                $("#thread-detail-highlight").val("");
                getThreadDetail(thread.id);
            });
            threadTr.appendTo("#all-thread-table tbody");
        })
    }


    function buildThreadDetail(thread, cpuTime) {
        $("#thread-detail-highlight").val("");
        $("#thread-detail-name").text(thread.threadName);
        $("#thread-detail-cpuTime").text(cpuTime);
        $("#thread-detail-state").text(thread.threadState);
        $("#thread-detail-blocked").text(thread.blockedCount);
        $("#thread-detail-waited").text(thread.waitedCount);
        var divAll = $("<div id='thread-stackTrace'></div>");
        thread.stackTrace.forEach(function (trace) {
            if (trace.nativeMethod) {
                divAll.append($("<div></div>").append(trace.className + "(Native Method)"));
            } else {
                divAll.append($("<div></div>").append(trace.className + "(" + trace.fileName + ":" + trace.lineNumber + ")"));
            }
        })
        $("#thread-detail-stackTrace").html(divAll);
    }

    function buildThreadDump(threads) {
        $("#thread-dump-wait").hide();
        if (threads.length <= 0) {
            $("#thread-dump-no-data").show();
            return;
        }
        $("#thread-dump-table").show();
        $("#thread-dump-table tbody").empty();
        threads.forEach(function (thread) {
            var idTd = $("<td></td>").append(thread.threadId);
            var nameTd = $("<td></td>").append(thread.threadName);
            var stateTd = $("<td></td>").append(thread.threadState);
            var waitedCountTd = $("<td></td>").append(thread.waitedCount);
            var blockedCountTd = $("<td></td>").append(thread.blockedCount);
            var traceDiv = $("<div id='thread-dump-stackTrace'></div>");
            thread.stackTrace.forEach(function (trace) {
                if (trace.nativeMethod) {
                    traceDiv.append($("<p></p>").append(trace.className + "(Native Method)"));
                } else {
                    traceDiv.append($("<p></p>").append(trace.className + "(" + trace.fileName + ":" + trace.lineNumber + ")"));
                }
            })
            var traceTd = $("<td></td>").append(traceDiv);
            $("<tr></tr>").append(idTd).append(nameTd).append(stateTd).append(waitedCountTd).append(blockedCountTd).append(traceTd).appendTo("#thread-dump-table tbody");
        })
    }

    function buildBaseInfo(hostInfo, jvmInfo) {
        $("#host-name").text(currentHost.host);
        $("#host-ip").text(currentHost.ip);
        $("#host-am").text(getMemory(hostInfo.freeMemory));
        $("#host-pma").text(getMemory(hostInfo.freePhysicalMemorySize));
        $("#host-pmt").text(getMemory(hostInfo.totalPhysicalMemorySize));
        $("#host-scl").text((hostInfo.cpuRatio * 100).toFixed(3) + "%");
        $("#host-cla").text(hostInfo.cpuLoadAverages);
        $("#host-da").text(getMemory(hostInfo.freeSpace));
        $("#host-dt").text(getMemory(hostInfo.totalSpace));
        $("#host-os").text(hostInfo.osName);
        $("#host-java-version").text(jvmInfo.jdkVersion);
        $("#host-nop").text(hostInfo.availableProcessors);
    }

    function buildJvmInfo(jvmInfo) {
        //基本信息
        $("#jvm-upTime").text(jvmInfo.upTime);
        $("#jvm-vmName").text(jvmInfo.vmName);
        $("#jvm-vmVendor").text(jvmInfo.vmVendor);
        $("#jvm-jdkVersion").text(jvmInfo.jdkVersion);
        $("#jvm-processCpuTime").text(jvmInfo.processCpuTime);
        $("#jvm-jitCompiler").text(jvmInfo.jitCompiler);

        //程序
        $("#jvm-currentThreadCount").text(jvmInfo.currentThreadCount);
        $("#jvm-peakThreadCount").text(jvmInfo.peakThreadCount);
        $("#jvm-totalStartedThreadCount").text(jvmInfo.totalStartedThreadCount);
        $("#jvm-daemonThreadCount").text(jvmInfo.daemonThreadCount);
        $("#jvm-loadedClassCount").text(jvmInfo.loadedClassCount);
        $("#jvm-totalLoadedClassCount").text(jvmInfo.totalLoadedClassCount);
        $("#jvm-unloadedClassCount").text(jvmInfo.unloadedClassCount);

        //规格
        $("#jvm-heapUsedMemory").text(getMemory(jvmInfo.heapUsedMemory));
        $("#jvm-heapMaxMemory").text(getMemory(jvmInfo.heapMaxMemory));
        $("#jvm-heapCommitedMemory").text(getMemory(jvmInfo.heapCommitedMemory));
        $("#jvm-nonHeapCommitedMemory").text(getMemory(jvmInfo.nonHeapCommitedMemory));
        $("#jvm-nonHeapUsedMemory").text(getMemory(jvmInfo.nonHeapUsedMemory));
        $("#jvm-nonHeapMaxMemory").text(getMemory(jvmInfo.nonHeapMaxMemory));
        $("#jvm-gcInfos").html(jvmInfo.gcInfos.join("</br>"));

        //软件
        $("#jvm-os").text(jvmInfo.os);
        $("#jvm-osArch").text(jvmInfo.osArch);
        $("#jvm-availableProcessors").text(jvmInfo.availableProcessors);
        $("#jvm-commitedVirtualMemory").text(getMemory(jvmInfo.commitedVirtualMemory));
        $("#jvm-totalPhysicalMemorySize").text(getMemory(jvmInfo.totalPhysicalMemorySize));
        $("#jvm-freePhysicalMemorySize").text(getMemory(jvmInfo.freePhysicalMemorySize));
        $("#jvm-totalSwapSpaceSize").text(getMemory(jvmInfo.totalSwapSpaceSize));
        $("#jvm-freeSwapSpaceSize").text(getMemory(jvmInfo.freeSwapSpaceSize));

        //jvm
        $("#jvm-vmOptions").text(jvmInfo.vmOptions);
        $("#jvm-classPath").text(jvmInfo.classPath);
        $("#jvm-libraryPath").text(jvmInfo.libraryPath);
        $("#jvm-bootClassPath").text(jvmInfo.bootClassPath);
    }

    function keepRunningFun() {
        keepRunning = true;
    }

    function stop() {
        keepRunning = false;
    }

    function handleResult(content) {
        if (!content) {
            return;
        }
        var result = JSON.parse(content);
        if (!result) {
            return;
        }
        keepRunningFun();
        var resType = result.type;
        if (resType == "hostInfo") {
            var hostInfo = result.host;
            var jvmInfo = result.jvm;
            var memPoolInfo = result.memPool;
            var visuaGC = result.visuaGC;
            buildBaseInfo(hostInfo, jvmInfo);
            buildJvmInfo(jvmInfo);
            buildThreadInfo(jvmInfo);
            buildMemPoolCurve(memPoolInfo);
            buildVisuaGcCurve(visuaGC)

            keepRunning = true;
        } else if (resType == "allThreadInfo") {
            buildAllThreadTable(result.threads, result.totalCpuTime);
        } else if (resType == "threadDetail") {
            buildThreadDetail(result.thread, result.cpuTime);
        } else if (resType == "threadDump") {
            buildThreadDump(result.threads);
        } else if (resType == "threadDeadLock") {
            buildThreadDump(result.threads);
        } else if (resType == "heapHisto") {
            if (result.code == 0) {
                buildHeapHisto(result.data);
            } else {
                bistoury.error(result.message);
            }
        } else if (resType == "jarinfo") {
            var res = result.data;
            if (res.code == 0) {
                buildJarInfo(res.data)
            } else {
                bistoury.error(res.message);
            }
        } else if (resType == "config" || resType == "appconfig" || resType == "appconfigfile") {
            var res = result.data;
            if (res.code == 0) {
                if (resType == "appconfig") {
                    buildAppConfigTable(res.data);
                } else if (resType == "appconfigfile") {
                    buildFileContent(res.data, res.id);
                }
            } else {
                $("#file-content-modal").modal('hide');
                bistoury.error(res.message);
            }
        } else if (resType === "profilerstart" || resType === "profilerstop" || resType === "profilerstatesearch") {
            buildProfiler(result);
        }
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
                if (currentHost != data.value) {
                    keepRunning = false;
                    currentHost = data.value;
                    stopProcessStateInterval();
                    stopSearchStateInterval();
                }
                startTime = new Date().getTime();
                currentThreadTime = new Date().getTime();
                currentMemTime = currentThreadTime;
                currentVisuaTime = currentThreadTime;

                firstIn = {
                    jarInfo: true,
                    heapHisto: true,
                    appConfig: true,
                    allThreads: true
                }

                getHostInfo();

                initCurveXAxis();
                initThreadCurve();
                initMemPoolCurve();
                initVisuaCurve();
                initHeapHisto();

                cleanData();
                removeActiveClass();
                $("#content-pane").show();
                $("#base-info-menu").addClass("active");
                $("#base-info").show();
            }
        });
    }


    function getHostInfo() {
        bistouryWS.send(currentHost, 10, "", "", stop, handleResult)
        //send(currentHost, 10, "");
    }

    function getAllThreads() {
        bistouryWS.send(currentHost, 11, "0@-1@-1", {
            type: 0,
            maxDepth: -1,
            threadId: -1
        }, keepRunningFun, handleResult);
        //send(currentHost, 11, "0@-1@-1")
    }

    function getThreadDetail(threadId) {
        var maxDepth = $("#thread-max-depth").val();
        //send(currentHost, 11, "1@" + threadId + "@" + maxDepth);
        bistouryWS.send(currentHost, 11, "1@" + threadId + "@" + maxDepth, {
            type: 1,
            maxDepth: maxDepth,
            threadId: threadId
        }, keepRunningFun, handleResult)
    }

    function getThreadDump() {
        var maxDepth = $("#thread-max-depth").val();
        //send(currentHost, 11, "2@-1@" + maxDepth);
        bistouryWS.send(currentHost, 11, "2@-1@" + maxDepth, {
            type: 2,
            maxDepth: maxDepth,
            threadId: -1
        }, keepRunningFun, handleResult)
    }

    function getDeadLock() {
        var maxDepth = $("#thread-max-depth").val();
        //send(currentHost, 11, "3@-1@" + maxDepth)
        bistouryWS.send(currentHost, 11, "3@-1@" + maxDepth, {
            type: 3,
            maxDepth: maxDepth,
            threadId: -1
        }, keepRunningFun, handleResult)
    }

    function getHeapHisto(timestamp) {
        $('#jvm-heap-histo-table').bootstrapTable('removeAll');
        var param = $("#heap-histo-param").val();
        if (timestamp) {
            bistouryWS.send(currentHost, 12, "heaphisto " + param + " " + timestamp, {
                param: param,
                timestamp: timestamp
            }, keepRunningFun, handleResult);
            //send(currentHost, 12, "heaphisto " + param + " " + timestamp);
        } else {
            bistouryWS.send(currentHost, 12, "heaphisto " + param + " " + -1, {
                param: param,
                timestamp: -1
            }, keepRunningFun, handleResult)
            //send(currentHost, 12, "heaphisto " + param + " " + -1);
        }
    }

    function getJarInfo() {
        var command = "jarinfo";
        bistouryWS.sendCommand(currentHost, 13, command, keepRunningFun, handleResult);
        //send(currentHost, 13, "jarinfo");
    }

    function getAppConfigInfo() {
        var command = "appconfig";
        bistouryWS.sendCommand(currentHost, 13, command, keepRunningFun, handleResult);
        //send(currentHost, 14, command);
    }

    function getAppConfigFile(path) {
        var command = "appconfigfile " + encodeURI(path);
        bistouryWS.sendCommand(currentHost, 13, command, keepRunningFun, handleResult);
        // send(currentHost, 14, command);
    }

    function initCurveXAxis() {
        currentThreadCounts = getCurveData();
        peakThreadCounts = getCurveData();
        totalStartedThreadCounts = getCurveData();
        daemonThreadCounts = getCurveData();

        metaspaceUsed = getCurveData();
        metaspaceCommit = getCurveData();
        psOldGenUsed = getCurveData();
        psOldGenCommit = getCurveData();
        compressedClassSpaceUsed = getCurveData();
        compressedClassSpaceCommit = getCurveData();
        pSSurvivorSpaceUsed = getCurveData();
        pSSurvivorSpaceCommit = getCurveData();
        pSEdenSpaceUsed = getCurveData();
        pSEdenSpaceCommit = getCurveData();
        codeCacheUsed = getCurveData();
        codeCacheCommit = getCurveData();
        codeHeapNonMmethodsUsed = getCurveData();
        codeHeapNonMmethodsCommit = getCurveData();
        codeHeapNonProfilednmethodsUsed = getCurveData();
        codeHeapNonProfilednmethodsCommit = getCurveData();

        compileTime = getCurveData();
        classLoaderTime = getCurveData();
        gcTime = getCurveData();
        edenSpace = getCurveData();
        survivor0 = getCurveData();
        survivor1 = getCurveData();
        oldGen = getCurveData();
        permGen = getCurveData();
        gcMetaspace = getCurveData();

        memPool = {
            Metaspace: {
                curve: metaspaceCurve,
                used: metaspaceUsed,
                committed: metaspaceCommit
            },
            OldGen: {
                curve: psOldGenCurve,
                used: psOldGenUsed,
                committed: psOldGenCommit
            }
            ,
            EdenSpace: {
                curve: pSEdenSpaceCurve,
                used: pSEdenSpaceUsed,
                committed: pSEdenSpaceCommit
            },
            CompressedClassSpace: {
                curve: compressedClassSpaceCurve,
                used: compressedClassSpaceUsed,
                committed: compressedClassSpaceCommit
            },
            CodeCache: {
                curve: codeCacheCurve,
                used: codeCacheUsed,
                committed: codeCacheCommit
            },
            CodeHeapnonnmethods: {
                curve: codeHeap_non_nmethods_curve,
                used: codeHeapNonMmethodsUsed,
                committed: codeHeapNonMmethodsCommit
            },
            CodeHeapnonprofilednmethods: {
                curve: codeHeap_non_profiled_nmethods_curve,
                used: codeHeapNonProfilednmethodsUsed,
                committed: codeHeapNonProfilednmethodsCommit
            },
            SurvivorSpace: {
                curve: pSSurvivorSpaceCurve,
                used: pSSurvivorSpaceUsed,
                committed: pSSurvivorSpaceCommit
            }
        }
    }

    function getCurveData() {
        return new Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    function initHeapHisto() {
        $('#jvm-heap-histo-table').bootstrapTable('removeAll');
    }

    function initVisuaCurve() {
        var legend = {
            data: ['']
        }
        var yAxis = {
            splitLine: {
                show: false
            }
        }
        var yAxisMB = {
            name: "(MB)",
            splitLine: {
                show: false
            }
        }
        var yAxisMS = {
            name: "(ms)",
            splitLine: {
                show: false
            }
        }
        var title = {text: "Compile time"};
        dravCurve(compileTimeCurve, visuaTimeAxis, yAxis, legend, title, getVisuaSeries(compileTime));

        title = {text: "Class loader time"};
        dravCurve(classLoaderTimeCurve, visuaTimeAxis, yAxisMS, legend, title, getVisuaSeries(classLoaderTime));

        title = {text: "GC time"};
        dravCurve(gcTimeCurve, visuaTimeAxis, yAxisMS, legend, title, getVisuaSeries(gcTime));

        title = {text: "Eden Space "};
        dravCurve(edenSpaceCurv, visuaTimeAxis, yAxisMB, legend, title, getVisuaSeries(edenSpace));

        title = {text: "Survivor 0 "};
        dravCurve(survivor0Curve, visuaTimeAxis, yAxisMB, legend, title, getVisuaSeries(survivor0));

        title = {text: "Survivor 1 "};
        dravCurve(survivor1Curve, visuaTimeAxis, yAxisMB, legend, title, getVisuaSeries(survivor1));

        title = {text: "Old Gen"};
        dravCurve(oldGenCurve, visuaTimeAxis, yAxisMB, legend, title, getVisuaSeries(oldGen));

        title = {text: "Perm Gen"};
        dravCurve(permGenCurve, visuaTimeAxis, yAxisMB, legend, title, getVisuaSeries(permGen));

        title = {text: "Metaspace"};
        dravCurve(gcMetaspaceCurve, visuaTimeAxis, yAxisMB, legend, title, getVisuaSeries(gcMetaspace));

    }

    function initMemPoolCurve() {
        var legend = {
            data: ["used", "committed"]
        }
        var yAxis = {
            name: "(MB)",
            splitLine: {
                show: false
            }
        }
        var title = {text: "Metaspace"}
        dravCurve(metaspaceCurve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(metaspaceUsed, metaspaceCommit));

        title = {text: "Old Gen"}
        dravCurve(psOldGenCurve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(psOldGenUsed, psOldGenCommit));

        title = {text: "Compressed Class Space"}
        dravCurve(compressedClassSpaceCurve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(compressedClassSpaceUsed, compressedClassSpaceCommit));

        title = {text: "Survivor Space"}
        dravCurve(pSSurvivorSpaceCurve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(pSSurvivorSpaceUsed, pSSurvivorSpaceCommit));

        title = {text: "Eden Space"}
        dravCurve(pSEdenSpaceCurve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(pSEdenSpaceUsed, pSEdenSpaceCommit));

        title = {text: "Code Cache"}
        dravCurve(codeCacheCurve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(codeCacheUsed, codeCacheCommit));

        title = {text: "CodeHeap 'non-nmethods'"}
        dravCurve(codeHeap_non_nmethods_curve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(codeHeapNonMmethodsUsed, codeHeapNonMmethodsCommit));

        title = {text: "CodeHeap'non-profilednmethods'"}
        dravCurve(codeHeap_non_profiled_nmethods_curve, memTimeAxis, yAxis, legend, title, getMemPoolSeries(codeHeapNonProfilednmethodsUsed, codeHeapNonProfilednmethodsCommit));

    }

    function getVisuaSeries(value) {
        return [{
            name: '',
            type: 'line',
            showSymbol: false,
            data: value
        }];
    }

    function getMemPoolSeries(used, committed) {
        return [{
            name: 'used',
            type: 'line',
            showSymbol: false,
            data: used
        }, {
            name: 'committed',
            type: 'line',
            showSymbol: false,
            data: committed
        }];
    }

    function initThreadCurve() {
        var title = {
            text: '线程'
        };
        var legend = {
            data: ['current count', 'daemon count', 'total started count', 'peak count'],
            selected: {
                'current count': true,
                'daemon count': true,
                'total started count': false,
                'peak count': true
            }
        };
        var yAxis = {
            name: "数量",
            splitLine: {
                show: false
            }
        }
        var series = [{
            name: 'current count',
            type: 'line',
            showSymbol: false,
            data: currentThreadCounts
        }, {
            name: 'daemon count',
            type: 'line',
            showSymbol: false,
            data: daemonThreadCounts
        }, {
            name: 'total started count',
            type: 'line',
            showSymbol: false,
            data: totalStartedThreadCounts
        }, {
            name: 'peak count',
            type: 'line',
            showSymbol: false,
            data: peakThreadCounts
        }];
        dravCurve(threadCurve, threadTimeAxis, yAxis, legend, title, series);
    }

    function dravCurve(curve, xData, yAxisData, legend, title, series) {
        curve.setOption({
            title: title,
            legend: legend,
            tooltip: {
                trigger: 'axis'
            },
            grid: {
                x2: 10,
                y2: 20,
                y: 80,
                x: 90,
            },
            toolbox: {
                show: true,
                feature: {
                    mark: {show: true},
                    dataView: {show: false, readOnly: false},
                    magicType: {show: true, type: ['line', 'bar']},
                    restore: {show: true},
                    saveAsImage: {show: true}
                }
            },
            xAxis: {
                type: 'category',
                boundaryGap: true,
                minInterval: interval,
                data: xData,
                axisTick: {
                    alignWithLabel: true
                }
            },
            yAxis: yAxisData,
            series: series
        })
    }


    $("#base-info-menu").click(function () {
        removeActiveClass();
        $("#base-info-menu").addClass("active");
        $("#base-info").show();
    });

    $("#app-info-menu").click(function () {
        removeActiveClass();
        $("#app-info-menu").addClass("active");
        $("#app-info").show();
    });

    $("#config-info-menu").click(function () {
        removeActiveClass();
        $("#config-info-menu").addClass("active");
        $("#config-info").show();

        removeConfigActiveClass();
        $("#appconfig-menu").addClass("active");
        $("#appconfig").show();

        if (!firstIn.appConfig) {
            return;
        } else {
            firstIn.appConfig = false;
            $('#appconfig-table').bootstrapTable('removeAll');
            getAppConfigInfo();
        }
    });


    $("#appconfig-menu").click(function () {
        removeConfigActiveClass();
        $("#appconfig-menu").addClass("active");
        $("#appconfig").show();

        if (!firstIn.appConfig) {
            return;
        } else {
            firstIn.appConfig = false;
            $("#appconfig-table").bootstrapTable('removeAll');
            getAppConfigInfo();
        }
    });

    $("#jvm-info-menu").click(function () {
        removeActiveClass();
        $("#jvm-info-menu").addClass("active");
        $("#jvm-info").show();
        removeJvmActiveClass();
        $("#jvm-outline-menu").addClass("active");
        $("#jvm-outline").show();
    });

    $("#jvm-outline-menu").click(function () {
        removeJvmActiveClass();
        $("#jvm-outline-menu").addClass("active");
        $("#jvm-outline").show();
    });

    $("#jar-dep-menu").click(function () {
        removeJvmActiveClass();
        $("#jar-dep-menu").addClass("active");
        $("#jar-dep").show();

        if (!firstIn.jarInfo) {
            return;
        } else {
            firstIn.jarInfo = false;
            $('#jar-dep-table').bootstrapTable('removeAll');
            getJarInfo();
        }
    })

    $("#jvm-memory-monitor-menu").click(function () {
        removeJvmActiveClass();
        $("#jvm-memory-monitor-menu").addClass("active");
        $("#jvm-memory-monitor").show();
    });

    $("#jvm-visua-gc-menu").click(function () {
        removeJvmActiveClass();
        $("#jvm-visua-gc-menu").addClass("active");
        $("#jvm-visua-gc").show();
    });

    $("#jvm-heap-histo-menu").click(function () {
        removeJvmActiveClass();
        $("#jvm-heap-histo-menu").addClass("active");
        $("#jvm-heap-histo").show();

        if (!firstIn.heapHisto) {
            return;
        } else {
            firstIn.heapHisto = false;
            $("#heap-histo-time").val("")
            bistoury.info("点击刷新图标查询数据");
            //getHeapHisto();
        }
    });

    $("#cpu-profiler-menu").click(function () {
        removeActiveClass();
        $("#cpu-profiler-menu").addClass("active");
        $("#cpu-profiler").show();
        initNoStartState();
    });

    $("#heap-histo-search").click(function () {
        /* var now = new Date(dateFormat(new Date())).getTime();
         var dateStr = $("#heap-histo-time").val()
         if (dateStr) {
             var timestamp = new Date(dateStr).getTime();
             if (now <= timestamp) {
                 bistoury.warning("查询时间不能超过当前时间");
                 return;
             }
             if (now - storeDays > timestamp) {
                 bistoury.warning("不能查询三天前的数据");
                 return;
             }
             getHeapHisto(timestamp);
         } else {
             bistoury.warning("请选择查询查询时间");
         }*/
        bistoury.warning("该功能暂时不可用")
    })

    $("#thread-info-menu").click(function () {
        removeActiveClass();
        $("#thread-info-menu").addClass("active");
        $("#thread-info").show();
        if (!firstIn.allThreads) {
            return;
        } else {
            firstIn.allThreads = false;
            getAllThreads();
        }
    });

    $('#process-dump-menu').click(function () {
        removeActiveClass()
        $('#process-dump-menu').addClass('active')
        $('#process-panel').load('../html/process-dump.html')
        $('#process-panel').show()
    })

    $('#jstack-dump-menu').click(function () {
        var currentHost = $('#menu').treeview('getSelected')[0].value
        var ip = currentHost.ip
        var appCode = currentHost.appCode
        var host = currentHost.host
        var params = "?ip=" + ip + "&appCode=" + appCode + "&host=" + host
        window.open("../html/jstack-dump.html" + params, "_blank")
    })

    $("#thread-info-refresh").click(function () {
        $("#thread-table-search").val("");
        getAllThreads();
    });

    $("#thread-dump").click(function () {
        $("#thread-dump-modal").modal('show');
        $("#thread-dump-modal-title").text("All Threads");
        $("#thread-dump-wait").show();
        $("#thread-dump-table").hide();
        $("#thread-dump-no-data").hide();
        $("#thread-dump-search").val("");
        getThreadDump();
    })

    $("#thread-dead-Lock").click(function () {
        $("#thread-dump-modal").modal('show');
        $("#thread-dump-modal-title").text("Dead Lock");
        $("#thread-dump-wait").show();
        $("#thread-dump-table").hide();
        $("#thread-dump-no-data").hide();
        $("#thread-dump-search").val("");
        getDeadLock();
    })

    $("#thread-table-search").on("keyup", function () {
        var input, filter, table, tr, td, i;
        input = document.getElementById("thread-table-search");
        filter = input.value.toLowerCase();
        table = document.getElementById("all-thread-table");
        tr = table.getElementsByTagName("tr");

        // 循环表格每一行，查找匹配项
        for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[2];
            if (td) {
                if (td.innerHTML.toLowerCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    });

    $("#thread-dump-search").on("keyup", function () {
        var input, filter, table, tr, td, i;
        input = document.getElementById("thread-dump-search");
        filter = input.value.toLowerCase();
        table = document.getElementById("thread-dump-table");
        tr = table.getElementsByTagName("tr");

        // 循环表格每一行，查找匹配项
        for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[1];
            if (td) {
                if (td.innerHTML.toLowerCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    });

    $("#thread-detail-highlight").on("keyup", function () {
        var input, filter, list, divs, i;
        input = document.getElementById("thread-detail-highlight");
        filter = input.value.toLowerCase();
        list = document.getElementById("thread-stackTrace");
        if (!list || list.length == 0) {
            return;
        }
        divs = list.getElementsByTagName("div");
        for (i = 0; i < divs.length; i++) {
            var div = divs[i];
            var re = new RegExp(filter, 'ig');
            var text = div.innerText;
            $(div).empty();
            if (re.test(text)) {
                $(div).append(text.replace(re, '<span class="highlight-style">$&</span>'));
            } else {
                $(div).append(text);
            }

        }
    })

    $("#thread-max-depth").on("keyup", function () {
        var maxDepth = $("#thread-max-depth").val();
        if (maxDepth < 0) {
            $("#thread-max-depth").val(10);
            alert("max depth 不能小于0")
        }
    });

    $("#profiler-mode").change(function () {
        var option = $(this).children('option:selected').val();
        if (option === sampler_code) {
            $("#profiler-threads-div").css('display', 'none');
            $("#profiler-event-div").css('display', 'none');
            $("#async-profiler-href-div").css('display', 'none');
            $("#profiler-frequency").val(20)
        } else if (option === async_sampler_code) {
            $("#profiler-threads-div").css('display', 'flex');
            $("#profiler-event-div").css('display', 'flex');
            $("#async-profiler-href-div").css('display', 'flex');
            $("#profiler-frequency").val(5)
        }
    })

    function initHeapHistoTable() {
        $('#jvm-heap-histo-table').bootstrapTable({
            data: [{}],
            striped: true, //是否显示行间隔色
            pageNumber: 1, //初始化加载第一页
            pagination: true,//是否分页
            sidePagination: 'client',//server:服务器端分页|client：前端分页
            pageSize: 10,//单页记录数
            pageList: [10, 20, 50, 100],//可选择单页记录数
            showRefresh: true,//刷新按钮
            toolbar: '#toolbar',
            search: true,
            searchAlign: "right",
            buttonsAlign: "left",
            columns: [{
                title: 'Class',
                field: 'className',
                sortable: true,
                searchable: true,
                formatter: function (vaule) {
                    switch (vaule) {
                        case "[B":
                            return "byte[]";
                        case "[C":
                            return "char[]";
                        case "[I":
                            return "int[]";
                        case "[Z":
                            return "boolean[]";
                        case "[S":
                            return "short[]";
                        case "[J":
                            return "long[]";
                        case "[F":
                            return "float[]";
                        case "[D":
                            return "double[]";
                        default:
                            break;
                    }
                    if ((vaule + "").indexOf("[L") == 0) {
                        return vaule.substr(2, vaule.length - 2) + "[]"
                    }
                    return vaule;
                }
            }, {
                title: 'Count',
                field: 'count',
                sortable: true
            }, {
                title: 'Bytes',
                field: 'bytes',
                sortable: true,
                formatter: function (value, row, index) {
                    if (value) {
                        return getMemory(value / 1024);
                    } else {
                        return "-";
                    }
                }
            }],
            onRefresh: function () {
                $("#heap-histo-time").val("")
                getHeapHisto();
            }
        });
        $("#heap-histo-time").datetimepicker({
            language: 'zh-CN',
            format: "yyyy-mm-dd hh:ii",
            todayBtn: true,
            autoclose: true,
            startView: 2,
            /* 0 or 'hour' for the hour view
            1 or 'day' for the day view
            2 or 'month' for month view (the default)
            3 or 'year' for the 12-month overview
            4 or 'decade' for the 10-year overview. Useful for date-of-birth datetimepickers. */
            minView: 0,//最低视图 小时视图
            maxView: 4, //最高视图 十年视图
            showSecond: true,
            showHours: true,
            minuteStep: 1
        });
    }

    function initJarInfoTable() {
        $('#jar-dep-table').bootstrapTable({
            data: [{}],
            striped: true, //是否显示行间隔色
            pageNumber: 1, //初始化加载第一页
            pagination: true,//是否分页
            sidePagination: 'client',//server:服务器端分页|client：前端分页
            pageSize: 10,//单页记录数
            pageList: [10, 20, 50, 100],//可选择单页记录数
            showRefresh: true,//刷新按钮
            search: true,
            searchAlign: "left",
            buttonsAlign: "left",
            columns: [{
                title: 'File Name',
                field: 'fileName',
                sortable: true,
                searchable: true
            }],
            onRefresh: function () {
                $('#jar-dep-table').bootstrapTable('removeAll');
                getJarInfo();
            }
        });
    }


    window.operateEvents = {
        "click .appconfig-file-display": function (e, value, row, index) {
            $("#file-content-modal").modal('show');
            $("#file-content-panel").empty();
            getAppConfigFile(value);
        }
    }

    function initAppConfigTable() {
        $('#appconfig-table').bootstrapTable({
            data: [{}],
            striped: true, //是否显示行间隔色
            pageNumber: 1, //初始化加载第一页
            pagination: true,//是否分页
            sidePagination: 'client',//server:服务器端分页|client：前端分页
            pageSize: 10,//单页记录数
            pageList: [10, 20, 50, 100],//可选择单页记录数
            showRefresh: true,//刷新按钮
            search: true,
            searchAlign: "left",
            buttonsAlign: "left",
            columns: [{
                title: 'File Name',
                field: 'name',
                sortable: true,
                searchable: true,
                width: "60%",
                events: operateEvents,
                formatter: function (value) {
                    return "<a style='cursor: pointer;' class='appconfig-file-display'>" + value + "</a>";
                }
            }, {
                title: 'Last Modified Time',
                field: 'modifiedTime',
                sortable: true,
                searchable: true,
                width: "20%",
                formatter: function (value, row, index) {
                    if (value) {
                        return dateFormat(value);
                    } else {
                        return "-";
                    }
                }
            }, {
                title: 'Size',
                field: 'size',
                sortable: true,
                searchable: true,
                width: "20%",
                formatter: function (value, row, index) {
                    if (value) {
                        return getMemory(value / 1024);
                    } else {
                        return "-";
                    }
                }
            }],
            onRefresh: function () {
                $('#appconfig-table').bootstrapTable('removeAll');
                getAppConfigInfo();
            }
        });
    }

    function init() {
        hidePane();
        getAppList();
        initHeapHistoTable();
        initJarInfoTable();
        initAppConfigTable();
    }

    function cleanData() {
        $(".tab-info table td[datatype='value']").text("");
        $("#all-thread-table tbody").empty();
        $("#thread-dump-table tbody").empty();
        $("#thread-detail-highlight").val("");
    }

    function removeActiveClass() {
        $(".tab").hide();
        $("#tab-menu li").removeClass("active");
    }

    function removeJvmActiveClass() {
        $(".jvm-tab").hide();
        $("#jvm-tab-menu button").removeClass("active");
    }

    function removeConfigActiveClass() {
        $(".config-tab").hide();
        $("#config-tab-menu button").removeClass("active");
    }

    function hidePane() {
        $("#content-pane").hide();
        $("#base-info").hide();
        $("#appinfo").hide();
        $("#jvm-info").hide();
    }

    function getTimeMs(timeNs) {
        return timeNs / 1000000;
    }

    function getTimeSec(timeNs) {
        return timeNs / 1000000000;
    }

    function getTimePercent(time, total) {
        return getPoint((time / total) * 100);
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
        return memory + " KB";
    }


    function getMemoryByMB(memory) {
        return getPoint(memory / MB);
    }

    function getPoint(value) {
        return Math.round(value * 1000) / 1000;
    }

    function shift(array) {
        if (array.length > 20) {
            array.shift();
        }
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

    window.setInterval(function () {
        if (keepRunning) {
            var now = new Date().getTime();
            if (now - startTime < keepTime) {
                currentThreadTime = new Date().getTime() - interval;
                currentMemTime = currentThreadTime;
                currentVisuaTime = currentThreadTime;
                getHostInfo();
            } else {
                keepRunning = false;
                bistoury.keepError("运行超过 " + minutes + " 分钟，停止查询", function () {
                    var continueRun = confirm("是否继续查询");
                    if (continueRun) {
                        keepRunning = true;
                        startTime = new Date().getTime();
                    }
                });
            }
        }
    }, interval);
    var preMouseMoveTime = new Date().getTime();
    $(document).mousemove(function (e) {
        var now = new Date().getTime();
        //限制时间更新频率
        if (now - preMouseMoveTime > 500) {
            preMouseMoveTime = now
            startTime = now;
        }
    });

    var EXT_MODE_MAPPINGS = {
        "m": "objectivec",
        "mm": "objectivec",
        "sql": "sql",
        "xml": "xml",
        "xsd": "xml",
        "md": "markdown",
        "perl ": "perl",
        "pl ": "perl",
        "java": "java",
        "csh": "bash",
        "ksh": "bash",
        "sh": "shell",
        "json": "json",
        "t": "qtable",
        "ini": "ini",
        "css": "css",
        "ts": "javascript",
        "js": "javascript",
        "rb": "ruby",
        "cs": "cs",
        "coffee": "coffeescript",
        "go": "go",
        "php": "php",
        "cpp": "cpp",
        "properties": "properties",
        "diff": "diff",
        "py": "python",
        "c": "c",
        "html": "xml",
        "groovy": "groovy",
        "gradle": "gradle",
        "swift": "swift"
    };

    function getMode(filename) {
        if (filename) {
            var dotIndex = filename.lastIndexOf('.');
            if (dotIndex == -1) {
                return 'java';
            } else {
                var ext = filename.substr(dotIndex + 1);
                if (EXT_MODE_MAPPINGS[ext]) {
                    return EXT_MODE_MAPPINGS[ext];
                } else {
                    return 'java';
                }
            }
        }
        return 'java';
    }

    init();
})