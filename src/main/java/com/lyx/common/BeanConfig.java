package com.lyx.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig
{
    @Bean("restTemplate")
    public RestTemplate getRestTemplate()
    {
        return new RestTemplate();
    }
}