package com.lyx;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lyx.service.FunService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests
{
    @Autowired
    @Qualifier("funService")
    private FunService funService;

    @Test
    void contextLoads() throws JsonProcessingException
    {
        funService.getData("37010001", "济南", "沂水", DateUtil.parseDate("2021-09-26"));
    }
}
