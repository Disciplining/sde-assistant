package com.lyx.common;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * 山东e出行平台调用配置
 */
@Component("sdeInvoker")
public class InvokeSDEConfig
{
    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    private static final String HOST = "https://m.xintuyun.cn/book";

    /**
     * 以 GET 方式请求山东e出行
     * @param url          具体的url
     * @param uriVariables url 参数
     * @return 山东e出行返回的数据转换面成.
     *          如果状态码是200的话，就是body转换而来的.
     *          如果状态码不是200，就会报错，根据报文或body生成错误信息.
     *          山东e出行报文为200时，返回的数据格式：https://panoramic-mayonnaise-894.notion.site/e-ca4a7c25e21748d4a4095be40359c4b5
     */
    public CommonResult<JsonNode> get(String url, Object... uriVariables)
    {
        ResponseEntity<JsonNode> resp;
        try
        {
            resp = restTemplate.getForEntity(HOST + url, JsonNode.class, uriVariables);
        }
        catch (HttpStatusCodeException e) // 返回的状态码为 4xx 5xx
        {
            String errorMsg = StrUtil.format("请求山东e出行平台出错：{}  {}", e.getStatusCode().value(), e.getMessage());
            return CommonResult.errorMsg(errorMsg);
        }

        // 状态码为 1xx 3xx
        if (resp.getStatusCode().is1xxInformational() || resp.getStatusCode().is3xxRedirection())
        {
            return CommonResult.errorMsg(StrUtil.format("山东e出行返回1XX  2XX：{}  {}", resp.getStatusCode().value(), resp.getBody()));
        }

        // 状态码为 2XX
        JsonNode body = resp.getBody();
        if (StrUtil.equals(body.at("/STATUS").asText(), "SUCCESS"))
        {
            return CommonResult.successData(body.at("/DATA"));
        }

        return CommonResult.errorMsg(body.at("CODE").asText());
    }
}
