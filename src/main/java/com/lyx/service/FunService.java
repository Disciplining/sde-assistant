package com.lyx.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lyx.common.CommonResult;
import com.lyx.common.InvokeSDEConfig;
import com.lyx.entity.InfoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        JsonNode data = result.getData();


        Console.log("结果数据：{}", result);

        return null;
    }

    /**
     * 将一个车次信息转换为前端需要的形式
     * @param one 一条车次信息
     * @return 车次信息
     */
    private InfoItem transformOneToInfoItem(JsonNode one)
    {
        InfoItem infoItem = new InfoItem();

        // 车次
        infoItem.setShiftNum(one.get("shiftNum").asText() + "次");

        // 剩余车票
        infoItem.setLeftSeatNum(14); // TODO 剩余车票
        ObjectNode shiftId = oMapper.createObjectNode();
        shiftId.put("stationId", one.get("stationId").asText());
        shiftId.put("sendDate", one.get("sendDate").asText());
        shiftId.put("sendTime", one.get("sendTime").asText());
        shiftId.put("shiftNum", one.get("shiftNum").asText());
        shiftId.put("portName", one.get("portName").asText());

        // 发车时间
        JsonNode memo;
        try
        {
            memo = oMapper.readTree(one.get("memo").asText());
            String sendTime = memo.get("sendTime").asText();
            if (StrUtil.contains(sendTime, "-"))
            {
                infoItem.setSendTime("流水班");
            }
        }
        catch (JsonProcessingException e)
        {
            infoItem.setSendTime("反序列化出错");
        }

        // 是不是加班车
        if (StrUtil.contains(one.get("companyName").asText(), "加班"))
        {
            infoItem.setWorkOvertime(true);
        }
        else
        {
            infoItem.setWorkOvertime(false);
        }

        // 始发车站
        infoItem.setStationName(one.get("stationName").asText());

        return infoItem;
    }
}
