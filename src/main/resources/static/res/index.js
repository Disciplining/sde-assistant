layui.use
(
    ['form', 'layer', 'laydate', 'table'],
    function()
    {
        // ①获得模块
        var form = layui.form;
        var layer = layui.layer;
        var laydate = layui.laydate;
        var table = layui.table;

        // ②渲染日期组件
        laydate.render({elem: '#startDate', min: dateFormat(new Date(), 'yyyy-MM-dd')});
        laydate.render({elem: '#quick1date', min: dateFormat(new Date(), 'yyyy-MM-dd')});
        laydate.render({elem: '#quick2date', min: dateFormat(new Date(), 'yyyy-MM-dd')});

        // ②缓存出发地点 与 定义结束地点变量
        let startPointList = getCacheStartPoint();
        let endNameList;

        // ③监听提交
        form.on // 任意地址
        (
            'submit(search)',
            function(data)
            {
                // 1.检查是否为空
                let obj = data.field;
                let haveBlank = false;
                Object.keys(obj).forEach
                (
                    function (key)
                    {
                        let value = obj[key];
                        if(typeof value==="undefined" || value===null || value==='')
                        {
                            haveBlank = true;
                        }
                    }
                );
                if (haveBlank)
                {
                    layer.alert("存在未填项", {title: '错误信息'});
                    return false;
                }

                // 2.根据用户输入的起点名称，获取出发point.
                let theStartPoint;
                $.each
                (
                    startPointList,
                    function (index, value)
                    {
                        if (obj.startCityName.indexOf(value.name) >= 0)
                        {
                            theStartPoint = value;
                            return false;
                        }
                    }
                )
                if (typeof(theStartPoint) === "undefined")
                {
                    layer.alert("不存在此出发地点", {title: '错误信息'});
                    return false;
                }

                // 3.获取到出发point后，根据出发城市的id获取终点名称list
                $.ajax
                (
                    {
                        url : '/getCacheEndPoint?startId=' + theStartPoint.id, //请求的url
                        async: false,
                        type : 'GET', //以何种方法发送报文
                        dataType : 'json', //预期的服务器返回的数据类型
                        success : function (data) //请求成功执行的访求
                        {
                            endNameList = data.data;
                        },
                        error : function () //请求失败执行的方法
                        {
                            layer.alert("获取出发地点出错", {title: '错误信息'});
                        }
                    }
                );

                // 4.根据用户输入的终点名称，从终点名称list中获取实际的出发名称
                let theEndName;
                for (let endName of endNameList)
                {
                    if (obj.endCityName.indexOf(endName) >= 0)
                    {
                        theEndName = endName;
                        break;
                    }
                }
                if (typeof(theEndName) === "undefined")
                {
                    layer.alert("不存在此终点", {title: '错误信息'});
                    return false;
                }

                // 5.生成数据列表
                generateDataList(table, theStartPoint.id, theStartPoint.name, theEndName, obj.startDate);
                return false;
            }
        );
        form.on // 济南→沂水
        (
            'submit(quick1)',
            function (data)
            {
                let quick1Date = data.field.quick1date;
                if (typeof quick1Date==="undefined" || quick1Date===null || quick1Date==='')
                {
                    layer.alert("出发日期未填", {title: '错误信息'});
                    return false;
                }

                generateDataList(table, '37010001', '济南', '沂水', quick1Date);
                return false;
            }
        );
        form.on // 沂水→济南
        (
            'submit(quick2)',
            function (data)
            {
                let quick2Date = data.field.quick2date;
                if (typeof quick2Date==="undefined" || quick2Date===null || quick2Date==='')
                {
                    layer.alert("出发日期未填", {title: '错误信息'});
                    return false;
                }

                generateDataList(table, '37130005', '沂水', '济南', quick2Date);
                return false;
            }
        );
    }
);

/**
 * 生成数据表格函数
 * @param table         layui表格对象
 * @param startCityId   url参数
 * @param startCityName url参数
 * @param endCityName   url参数
 * @param startDate     url参数
 */
function generateDataList(table, startCityId, startCityName, endCityName, startDate)
{
    table.render
    (
        {
            elem: '#info',
            url:'/get-data?startCityId=' + startCityId + '&startCityName=' + startCityName + '&endCityName=' + endCityName + '&startDate=' + startDate,
            cellMinWidth: 80, //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            cols:
            [
                [
                    {field:'leftSeatNum', title: '剩余车票'},
                    {field:'shiftNum', title: '车次'},
                    {field:'sendTime', title: '发车时间', sort: true},
                    {field:'workOvertime', title: '是否为加班'},
                    {field:'stationName', title: '始发车站', sort: true}
                ]
            ]
        }
    );
}

/**
 * 格式化日期时间
 * @param date    日期时间
 * @param pattern 要格式化成的格式
 * @returns 格式化后的字符串
 */
function dateFormat(date, pattern)
{
    let ret;
    const opt =
    {
        "Y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (let k in opt)
    {
        ret = new RegExp("(" + k + ")").exec(pattern);
        if (ret)
        {
            pattern = pattern.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        };
    };
    return pattern;
}

/**
 * 获得需要缓存下来的出发地点.
 */
function getCacheStartPoint(layer)
{
    let startPointList;

    $.ajax
    (
        {
            url : '/getCacheStartPoint', //请求的url
            type : 'GET', //以何种方法发送报文
            async: false,
            dataType : 'json', //预期的服务器返回的数据类型
            success : function (data) //请求成功执行的访求
            {
                startPointList = data.data;
            },
            error : function () //请求失败执行的方法
            {
                layer.alert("获取出发地点出错", {title: '错误信息'});
            }
        }
    );

    return startPointList;
}
