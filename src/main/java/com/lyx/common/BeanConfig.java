package com.lyx.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class BeanConfig
{
    @Bean("restTemplate")
    public RestTemplate restTemplate(@Qualifier("fac") ClientHttpRequestFactory factory)
    {
        RestTemplate restTemplate = new RestTemplate(factory);
        List<HttpMessageConverter<?>> converterList;
        converterList = restTemplate.getMessageConverters();
        converterList.remove(1);
        HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converterList.add(1, converter);
        restTemplate.setMessageConverters(converterList);

        return restTemplate;
    }

    @Bean("fac")
    public ClientHttpRequestFactory simpleClientHttpRequestFactory()
    {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        // 超时时间修改为10分钟
        factory.setConnectTimeout(600000);
        factory.setReadTimeout(600000);
        return factory;
    }
}
