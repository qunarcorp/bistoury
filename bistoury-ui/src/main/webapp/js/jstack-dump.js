function getUrlParam(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)')
    var r = window.location.search.substr(1).match(reg)
    if (r != null) return unescape(r[2]);
    return null
}

var startTime
var endTime
var echartEndTime
var threadNumDatas = []
var threadCpuTimeDatas = []
var totalCpuTimeDatas = []
var legend = {
    data: ['总cpu占比', '线程数统计', '所选cpu占比']
}
var yDatas = []
var xDatas = []
var threadSelectName

var threadNumContext = {}
var threadNameContext = {}

var REQ_TYPE_CPU_JSTACK_TIMES = 20
var REQ_TYPE_CPU_JSTACK_THREADS = 21
var REQ_TYPE_CPU_THREAD_NUM = 22
var minuteTotalCPUTime
var momentTotalCPUTime

ip = getUrlParam('ip')
appCode = getUrlParam('appCode')
host = getUrlParam('host')

// 表格数据展示
var filterByBlocked = false
var filterByRunnable = false
var filterByWaiting = false
var filterByTimedWaiting = false
var filterByThreadName
var filterByStackTrace
var threadInfoData
var clickRow
var echartMain
var options = getEchartOption(xDatas, yDatas)
var labelColor = {
    'BLOCKED': 'label-danger',
    'RUNNABLE': 'label-success',
    'WAITING': 'label-warning',
    'TIMED_WAITING': 'label-warning',
    'OTHER': 'label-default'
}

function fillThreadInfo(data) {
    if (isOldAgent(data)) {
        $('#all-jstack-table').attr('data-sort-name', 'cpuTime')
    }
    threadNumContext = {BLOCKED: 0, RUNNABLE: 0, WAITING: 0, OTHER: 0, TOTAL: 0}
    threadNameContext = {thread: []}
    threadInfos = data.threadInfo
    threadInfoData = Object.values(data.threadInfo)
    threadNameContext['thread'].push({value: '', name: '线程总占比'})
    for (var i = 0, len = threadInfoData.length; i < len; i++) {
        var state = threadInfoData[i].state
        var id = threadInfoData[i].id
        var name = threadInfoData[i].name
        var temp = {value: id, name: name}
        threadNumContext['TOTAL']++
        threadNameContext['thread'].push(temp)
        if (state == 'TIMED_WAITING') {
            threadNumContext['WAITING']++
            continue
        }
        if (threadNumContext[state] != undefined) {
            threadNumContext[state]++
        } else {
            threadNumContext['OTHER']++
        }
    }
    if (typeof (data.threadId) == 'undefined') {
        initThreadNum()
    }
    initAllThreadName()
    initAllThreadTable()
    $('#all-jstack-table').bootstrapTable('removeAll')
    $('#all-jstack-table').bootstrapTable('append', Object.values(threadInfos))
    dataFilter()
}

function fillEchart(data) {
    if (!echartMain) {
        initJStackEchart()
    }
    xDatas = []
    yDatas = []
    if (!isThreadNum(data)) {
        formatDatas(xDatas, yDatas, data.cpuTimes, 'time')
    } else {
        formatDatas(xDatas, yDatas, data.threadNums, 'num')
    }

    var yDatasInterval = getYDatasInterval(xDatas, yDatas)
    options.xAxis[0].data = xDatas
    echartEndTime = xDatas[xDatas.length - 1].replace(' ', '').replace('/', '').replace('/', '').replace(':', '')
    if (isTotalThreads(data)) {
        totalCpuTimeDatas = yDatasInterval
        options.series.splice(2)
        options.series[0] = getSeries('总cpu占比', yDatasInterval, 0)
        options.yAxis[0].max = Math.max.apply(Math, yDatasInterval)
        requestThreadNum()
        minuteTotalCPUTime = yDatas[yDatas.length - 1]
        buildClickTimeInfo(endTime.format('YYYY/MM/DD HH:mm'))
    } else if (isSingleThread(data)) {
        legend.data[2] = threadSelectName
        threadCpuTimeDatas = yDatasInterval
        options.series[2] = getSeries(threadSelectName, yDatasInterval, 0)
    } else if (isThreadNum(data)) {
        options.yAxis[1].max = Math.max.apply(Math, yDatasInterval)
        options.series.splice(2)
        threadNumDatas = yDatasInterval
        options.series[1] = getSeries('线程数统计', yDatasInterval, 1)
        requestThreadInfo(echartEndTime)
    }
    echartMain.setOption(options, true)
}

function formatDatas(xDatas, yDatas, sourceDatas, name) {
    var timestamp = moment(startTime)
    var formatEndTime = moment(endTime).add(1, 'minutes').format('YYYYMMDDHHmm')
    for (position = 0; timestamp.format('YYYYMMDDHHmm') != formatEndTime;) {
        var formatTimeStamp = timestamp.format('YYYY/MM/DD HH:mm')
        if (position >= sourceDatas.length) {
            xDatas.push(formatTimeStamp)
            yDatas.push(0)
            timestamp = timestamp.add(1, 'minutes')
            continue
        }
        if (sourceDatas[position].timestamp != timestamp.format('YYYYMMDDHHmm')) {
            xDatas.push(formatTimeStamp)
            yDatas.push(0)
        } else {
            xDatas.push(formatTimeStamp)
            yDatas.push(sourceDatas[position][name])
            position++
        }
        timestamp = timestamp.add(1, 'minutes')
    }
}

function isSingleThread(data) {
    return data.type == 'cpuTime' && data.threadId
}

function isTotalThreads(data) {
    return data.type == 'cpuTime' && (!data.threadId)
}

function isThreadNum(data) {
    return data.type == 'threadNum'
}

function initJStackEchart() {
    var dom = document.getElementById('jstack-curve')
    echartMain = echarts.init(dom)
    echartMain.on('click', function (params) {
        if (params.componentType != 'series') {
            return
        }
        minuteTotalCPUTime = totalCpuTimeDatas[params.dataIndex]
        requestThreadInfo(params.name.replace(' ', '').replace('/', '').replace('/', '').replace(':', ''))
        buildClickTimeInfo(params.name)
    })
}

function initAllThreadName() {
    var template = Handlebars.compile($('#thread-info-template').html())
    var context = threadNameContext
    $('#thread-select').html(template(context))
    $('.selectpicker').selectpicker('refresh')
}

function initAllThreadTable() {
    $('#all-jstack-table').bootstrapTable({
        data: [],
        striped: true, // 是否显示行间隔色
        pageNumber: 1, // 初始化加载第一页
        pagination: false, // 是否分页
        sidePagination: 'client', // server:服务器端分页|client：前端分页
        searchAlign: 'left',
        buttonsAlign: 'left',
        columns: [
            {
                title: 'id',
                field: 'id',
                sortable: false,
                width: '3%'
            }, {
                title: 'StackTrace',
                field: 'stack',
                sortable: false,
                formatter: stackTraceFormatter,
                width: '12%'
            }, {
                title: 'threadName',
                field: 'name',
                sortable: true,
                cellStyle: cellStyle,
                width: '35%'
            }, {
                title: '每分钟cpu占用率',
                field: 'minuteCpuTime',
                sortable: true,
                searchable: true,
                'data-sort-order': 'desc',
                cellStyle: cellStyle,
                formatter: cpuTimeFormatter,
                width: '35%'
            }, {
                title: '瞬时cpu占用率',
                field: 'cpuTime',
                sortable: true,
                searchable: true,
                'data-sort-order': 'desc',
                cellStyle: cellStyle,
                formatter: cpuTimeFormatter,
                width: '35%'
            }, {
                title: 'State',
                field: 'state',
                sortable: true,
                cellStyle: cellStyle,
                width: '10%'
            }, {
                title: 'lockOn',
                field: 'lockOn',
                sortable: true,
                cellStyle: cellStyle,
                width: '10%'
            }],
        onPreBody: function () {
            $('[rel=popover]').popover('destroy')
        },
        onPostBody: function () {
            $('[rel=popover]').popover({html: true, container: 'body', trigger: 'manual'})
            $('.glyphicon-stacktrace').click(function (e) {
                if ($(this).hasClass('glyphicon-eye-close')) {
                    $(this).removeClass('glyphicon-eye-close')
                    $(this).addClass('glyphicon-eye-open')
                    $(this).popover('hide')
                } else if ($(this).hasClass('glyphicon-eye-open')) {
                    $(this).popover('show')
                    $(this).removeClass('glyphicon-eye-open')
                    $(this).addClass('glyphicon-eye-close')
                    $('[rel=popover]').not($(this)).popover('hide')
                    $('[rel=popover]').not($(this)).removeClass('glyphicon-eye-close')
                    $('[rel=popover]').not($(this)).addClass('glyphicon-eye-open')
                }
            })
        }
    })
    initThreadNum()
}

function isOldAgent(data) {
    if (Object.values(data.threadInfo).length == 0) {
        return true
    }
    if (typeof (Object.values(data.threadInfo)[0].minuteCpuTime) == 'undefined') {
        return true
    }
}

function cpuTimeFormatter(value) {
    var context = {percent: value / 100}
    var template = Handlebars.compile($('#progress-template').html())
    return template(context)
}

function initThreadNum() {
    var template = Handlebars.compile($('#state-num-template').html())
    var context = threadNumContext
    $('#state-num').html(template(context))
}

function dataFilter() {
    var filterData = threadInfoData
    if (filterByThreadName) {
        filterData = filterData.filter(function (value) {
            return value.name.toLowerCase().indexOf(filterByThreadName.toLowerCase()) != -1
        })
    }
    if (filterByStackTrace) {
        filterData = filterData.filter(function (value) {
            return value.stack.toLowerCase().indexOf(filterByStackTrace.toLowerCase()) != -1
        })
    }
    filterData = filterData.filter(function (value) {
        var state = value.state
        if (filterByBlocked && isBlockedState(state)) {
            return false
        }
        if (filterByRunnable && isRunnableState(state)) {
            return false
        }
        if (filterByTimedWaiting && isTimeWaitingState(state)) {
            return false
        }
        if (filterByWaiting && isWaitingState(state)) {
            return false
        }
        return true
    })
    minuteTotalCPUTime = 0
    momentTotalCPUTime = 0
    filterData.forEach(function (entry) {
        minuteTotalCPUTime += entry.minuteCpuTime / 100
        momentTotalCPUTime += entry.cpuTime / 100
    })
    $('#label_minute_total_cpu_time').text('每分钟总cpu占用率 : ' + minuteTotalCPUTime.toFixed(2) + ' %')
    $('#label_moment_total_cpu_time').text('瞬时总cpu占用率 : ' + momentTotalCPUTime.toFixed(2) + ' %')
    $('#all-jstack-table').bootstrapTable('removeAll')
    $('#all-jstack-table').bootstrapTable('append', filterData)
}

function isBlockedState(state) {
    return state == 'BLOCKED'
}

function isRunnableState(state) {
    return state == 'RUNNABLE'
}

function isWaitingState(state) {
    return state == 'WAITING'
}

function isTimeWaitingState(state) {
    return state == 'TIMED_WAITING'
}

function getYDatasInterval(curXDatas, curYDatas) {
    var pointCount = 120
    if (curXDatas.length > pointCount) {
        var newXDatas = []
        var newYDatas = []
        var interval = parseInt(curXDatas.length / pointCount) + 1
        var position = 0
        for (; position < curXDatas.length;) {
            newXDatas.push(curXDatas[position])
            newYDatas.push(curYDatas[position])
            position += interval
        }
        $('#minuteInterval').text(interval)
    } else {
        var newXDatas = curXDatas
        var newYDatas = curYDatas
        $('#minuteInterval').text('1')
    }
    xDatas = newXDatas
    yDatas = newYDatas
    return newYDatas
}

function getEchartOption() {
    return {
        title: {
            text: 'cpu占比',
            subtext: '点击相应点查看信息\n\n( 使用前需要在应用\n中心打开jstack开关)'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: legend,
        toolbox: {
            show: true,
            feature: {
                dataView: {show: true, readOnly: false},
                magicType: {show: true, type: ['line', 'bar']},
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },
        calculable: true,
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                data: []
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '每分钟cpu占比',
                min: 0,
                max: 0,
                axisLabel: {
                    formatter: '{value}'
                },
                splitLine: {
                    show: false
                }
            },
            {
                type: 'value',
                name: '线程数量',
                min: 0,
                max: 0,
                axisLabel: {
                    formatter: '{value}'
                },
                splitLine: {
                    show: false
                }
            }
        ],
        series: []
    }
}

function getSeries(name, data, yAxisIndex) {
    return {
        name: name,
        type: 'line',
        data: yDatas,
        yAxisIndex: yAxisIndex,
        markPoint: {
            data: [
                {type: 'max', name: '最大值'},
                {type: 'min', name: '最小值'}
            ]
        }
    }
}

function cellStyle(value, row, index, field) {
    if (field == 'state') {
        if (isBlockedState(value)) {
            return {
                css: {
                    'color': '#d9534f'
                }
            }
        } else if (isRunnableState(value)) {
            return {
                css: {
                    'color': '#5cb85c'
                }
            }
        } else if (isTimeWaitingState(value) || isWaitingState(value)) {
            return {
                css: {
                    'color': '#f0ad4e'
                }
            }
        } else if (value == 'OTHER') {
            return {
                css: {
                    'color': '#777'
                }
            }
        }
    }
    return {}
}

function stackTraceFormatter(index, row) {
    // var list = ['qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler', 'qunar.tc.commands.client.ChannelDuplexHandler.ChannelDuplexHandler']
    var list = row.stack.split('\n')
    var stateColor = labelColor[row.state]
    if (stateColor == undefined) {
        stateColor = ' label-default'
    }
    var context = {stacktrace: list, labelColor: stateColor}
    var template = Handlebars.compile($('#stacktrace-template').html())
    return template(context)
}

$(document).ready(function () {
    function dataRangeRefresh() {
        curClickDate = new Date()
        endDate = moment(curClickDate).subtract(1, 'minutes')
        startDate = moment().subtract(2, 'hours')
        $('.datepicker').daterangepicker({
            timePicker: true,
            startDate: startDate,
            endDate: endDate,
            maxDate: endDate,
            minDate: moment().subtract(3, 'days'),
            minuteStep: 120,
            ranges: {
                '最近2小时': [moment().subtract(2, 'hours'), moment()],
                '最近3小时': [moment().subtract(3, 'hours'), moment()],
                '最近4小时': [moment().subtract(4, 'hours'), moment()],
                '最近5小时': [moment().subtract(5, 'hours'), moment()],
                '最近6小时': [moment().subtract(5, 'hours'), moment()]
            },
            locale: {
                format: 'YYYY/MM/DD HH:mm'
            },
            autoApply: true,
            timePicker24Hour: true

        }, function (startDate, endDate, period) {
            startTime = moment(startDate)
            endTime = moment(endDate)
            requestCpuTime($('#thread-select').val())
            buildClickTimeInfo(endTime.format('YYYY/MM/DD HH:mm'))
        })
        endTime = endDate
        startTime = startDate
        requestCpuTime('')
    }

    $('#btn_refresh').click(function (e) {
        $('.datepicker').daterangepicker('destroy')
        dataRangeRefresh()
    })
    $('#thread-select').on('change', function () {
        var threadId = $(this).find('option:selected').val()
        requestCpuTime(threadId)
        threadSelectName = $(this).find('option:selected').text() + '线程 cpu占比'
    })
    dataRangeRefresh()
    $('#input_checkbox_blocked').change(function (e) {
        filterByBlocked = !this.checked
        dataFilter()
    })
    $('#input_checkbox_runnable').change(function (e) {
        filterByRunnable = !this.checked
        dataFilter()
    })
    $('#input_checkbox_waiting').change(function (e) {
        filterByWaiting = !this.checked
        dataFilter()
    })

    $('#input_checkbox_time_waiting').change(function (e) {
        filterByTimedWaiting = !this.checked
        dataFilter()
    })

    $('#search_stack_trace').change(function (e) {
        $('#all-jstack-table').bootstrapTable('removeAll')
        var val = $(this).val()
        filterByStackTrace = val
        dataFilter()
    })

    $('#search_jstack_name').change(function (e) {
        $('#all-jstack-table').bootstrapTable('removeAll')
        var val = $(this).val()
        filterByThreadName = val
        dataFilter()
    })

    // 点击空白处关闭popover
    $(document).click(function () {
        var target = $(event.target)
        if (target.hasClass('glyphicon-stacktrace')) {
            return
        }
        if (!target.hasClass('popover')
            && target.parent('.popover-content').length === 0
            && target.parent('.myPopover').length === 0
            && target.parent('.popover-title').length === 0
            && target.parent('.popover').length === 0) {
            $('[rel=popover]').popover('hide')
            $('[rel=popover]').removeClass('glyphicon-eye-close')
            $('[rel=popover]').removeClass('glyphicon-eye-open')
            $('[rel=popover]').addClass('glyphicon-eye-open')
        }
    })
})

function buildClickTimeInfo(curClickDate) {
    $('#label_appCode').text('appCode:' + appCode)
    $('#label_hostIp').text('ip:' + ip)
    $('#label_date').text('Date:' + curClickDate)
}

function contains(array, obj) {
    var i = array.length
    while (i--) {
        if (array[i] === obj) {
            return i;
        }
    }
    return false
}

function handleResult(content) {
    if (!content) {
        return;
    }
    var result = JSON.parse(content);
    if (!result) {
        return;
    }
    var type = result.type;
    if (type === 'cpuTime') {
        fillEchart(result)
    } else if (type === 'jstackThreads') {
        fillThreadInfo(result)
    } else if (type == 'threadNum') {
        fillEchart(result)
    }
}

function encrypt(content) {
    var k1 = makeid()
    var dataEnc = encryptByDES(content, k1)

    var publicKey = '-----BEGIN PUBLIC KEY-----\n' +
        'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzYgJiPl4ltUdOvTIx8yu5iw0+\n' +
        'k7jANyrVzXOJy+478EhBaf8MoHaHvbH06PfaLjmFJXsRZKv9Qq5SieQcLlnG60Uu\n' +
        'utpen1Nf490au+nPCP++nK3L5ZBqaSCAq4GUAniARR1wWl9TYW0walBCpD2N2Swy\n' +
        'MLu9z+Lnhd7auqYSzwIDAQAB\n' +
        '-----END PUBLIC KEY-----'
    var crypt = new JSEncrypt()
    crypt.setPublicKey(publicKey)
    var k1Enc = crypt.encrypt(k1)

    return '{"0":"' + k1Enc + '","1":"' + dataEnc + '"}'
}

function makeid() {
    var text = ''
    var possible = '0123456789abcdef'

    for (var i = 0; i < 8; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length))
    }

    return text
}

function encryptByDES(message, key) {
    var keyHex = CryptoJS.enc.Utf8.parse(key)
    var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    })
    return encrypted.toString()
}

var send = function (machine, type, input) {

    if (machine == null) {
        bistoury.error("请选择机器");
        console.log('请选择机器')
    }

    bistouryWS.sendCommand({ip: machine, host: host, appCode: appCode}, type, input, stop, handleResult)

}

function stop() {
    isRunCommand = false
}

function requestCpuTime(threadId) {
    var requestObj = {threadId: threadId, start: startTime.format('YYYYMMDDHHmm'), end: endTime.format('YYYYMMDDHHmm')}
    var command
    command = JSON.stringify(requestObj)

    send(ip, REQ_TYPE_CPU_JSTACK_TIMES, command)
}

function requestThreadInfo(time) {
    send(ip, REQ_TYPE_CPU_JSTACK_THREADS, time)
}

function requestThreadNum() {
    var requestObj = {start: startTime.format('YYYYMMDDHHmm'), end: endTime.format('YYYYMMDDHHmm')}
    var command
    command = JSON.stringify(requestObj)

    send(ip, REQ_TYPE_CPU_THREAD_NUM, command)
}
