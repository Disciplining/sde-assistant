package com.lyx;

import cn.hutool.core.lang.Console;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyx.common.CommonResult;
import com.lyx.common.InvokeSDEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests
{
    @Autowired
    private ObjectMapper oMapper;

    @Autowired
    private InvokeSDEConfig invokeSDEConfig;

    @Test
    void contextLoads() throws JsonProcessingException
    {
        StringBuilder builder = new StringBuilder("/suit");
        builder.append("?ttsId=")
                .append("&startId={p1}")
                .append("&shiftId={p2}")
                .append("&isWeixin={p3}");

        CommonResult<JsonNode> result = invokeSDEConfig.get(builder.toString(), "21070002", "{\"stationId\":\"2107000201\",\"sendDate\":\"2021-09-26\",\"sendTime\":\"16:10\",\"shiftNum\":\"1461\",\"portName\":\"百户营\"}", 0);

        Console.log(result);
    }
}
