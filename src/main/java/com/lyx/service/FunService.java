package com.lyx.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lyx.common.CommonResult;
import com.lyx.common.InvokeSDEConfig;
import com.lyx.entity.InfoItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("funService")
public class FunService
{
    @Autowired
    @Qualifier("sdeInvoker")
    private InvokeSDEConfig invokeSDEConfig;

    @Autowired
    private ObjectMapper oMapper;

    /**
     * 获得车次数据
     * @return 车票数据
     */
    public CommonResult getData(String startCityId, String startCityName, String endCityName, Date startDate)
    {
        // ①获得列表
        String url = "/shifts?ttsId=&startCityId={p0}&startCityName={p1}&endCityName={p2}&startDate={p3}&lon=0&lat=0";
        CommonResult<JsonNode> result = invokeSDEConfig.get(url, startCityId, startCityName, endCityName, DateUtil.formatDate(startDate));
        if (!result.isSuccess())
        {
            return result;
        }
        JsonNode dataList = result.getData();

        // ②转换数据
        List<InfoItemVo> resultList = CollUtil.newArrayList();
        for (JsonNode el : dataList)
        {
            resultList.add(this.transformOneToInfoItem(startCityId, el));
        }

        return CommonResult.successData(CollUtil.sortByProperty(resultList, "leftSeatNum"));
    }

    /**
     * 将一个车次信息转换为前端需要的形式
     * @param one 一条车次信息
     * @return 车次信息
     */
    private InfoItemVo transformOneToInfoItem(String startCityId, JsonNode one)
    {
        InfoItemVo infoItemVo = new InfoItemVo();

        // 车次
        infoItemVo.setShiftNum(one.get("shiftNum").asText() + "次");

        // 剩余车票
        ObjectNode shiftId = oMapper.createObjectNode();
        shiftId.put("stationId", one.get("stationId").asText());
        shiftId.put("sendDate", one.get("sendDate").asText());
        shiftId.put("sendTime", one.get("sendTime").asText());
        shiftId.put("shiftNum", one.get("shiftNum").asText());
        shiftId.put("portName", one.get("portName").asText());

        String url = "/suit?ttsId=&startId={p0}&shiftId={p1}&isWeixin=0";

        CommonResult<JsonNode> leftSeatNumNode = invokeSDEConfig.get(url, startCityId, shiftId.toString());
        if (leftSeatNumNode.isSuccess())
        {
            int num = leftSeatNumNode.getData().at("/shiftInfo/leftSeatNum").asInt();
            infoItemVo.setLeftSeatNum(num);
        }
        else
        {
            infoItemVo.setLeftSeatNum(-1);
        }


        // 发车时间
        JsonNode memo;
        try
        {
            memo = oMapper.readTree(one.get("memo").asText());
            String sendTime = memo.get("sendTime").asText();
            if (StrUtil.contains(sendTime, "-"))
            {
                infoItemVo.setSendTime("流水班");
            }
            else
            {
                infoItemVo.setSendTime(sendTime);
            }
        }
        catch (JsonProcessingException e)
        {
            infoItemVo.setSendTime("反序列化出错");
        }

        // 是不是加班车
        if (StrUtil.contains(one.get("companyName").asText(), "加班"))
        {
            infoItemVo.setWorkOvertime("加班车");
        }
        else
        {
            infoItemVo.setWorkOvertime("非加班车");
        }

        // 始发车站
        infoItemVo.setStationName(one.get("stationName").asText());

        return infoItemVo;
    }

    /**
     * 获得要缓存的，出发地点的数据.
     * @return 出发地点的数据
     */
    public CommonResult getCacheStartPoint()
    {
        CommonResult<JsonNode> resp = invokeSDEConfig.get("/startPoint?ttsId=");
        if (!resp.isSuccess())
        {
            return resp;
        }

        ArrayNode result = oMapper.createArrayNode();

        JsonNode data = resp.getData();
        for (JsonNode oneArray : data)
        {
            for (JsonNode el : oneArray)
            {
                result.add(el);
            }
        }

        return CommonResult.successData(result);
    }

    /**
     * 获得要缓存的，结束地点的数据.
     * @return 终点地址的数据
     */
    public CommonResult getCacheEndPoint(String startId)
    {
        CommonResult<JsonNode> resp = invokeSDEConfig.get("/endPoint?departure={p}&ttsId=", startId);
        if (!resp.isSuccess())
        {
            return resp;
        }

        List<String> endNameList = CollUtil.newArrayList();
        JsonNode dataNode = resp.getData();
        for (JsonNode oneArray : dataNode)
        {
            for (JsonNode innerArray : oneArray)
            {
                endNameList.add(innerArray.get(0).asText());
            }
        }

        return CommonResult.successData(endNameList);
    }
}
