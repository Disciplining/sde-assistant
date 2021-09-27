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

        // ②日期组件
        laydate.render({elem: '#startDate'});
        laydate.render({elem: '#quick1date'});

        // ③监听提交
        form.on
        (
            'submit(search)',
            function(data)
            {
                // 检查是否为空
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

                // 生成数据列表
                generateDataList(table, obj.startCityId, obj.startCityName, obj.endCityName, obj.startDate);
                return false;
            }
        );
        form.on
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
                        {field:'sendTime', title: '发车时间'},
                        {field:'workOvertime', title: '是否为加班'},
                        {field:'stationName', title: '始发车站'}
                    ]
                ]
        }
    );
}
