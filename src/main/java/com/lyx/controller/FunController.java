package com.lyx.controller;

import com.lyx.common.CommonResult;
import com.lyx.service.FunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class FunController
{
    @Autowired
    @Qualifier("funService")
    private FunService funService;

    /**
     * 获得车次数据
     */
    @GetMapping("/get-data")
    public CommonResult getData(@RequestParam String startCityId, @RequestParam String startCityName,
                                @RequestParam String endCityName, @RequestParam Date startDate)
    {
        return funService.getData(startCityId, startCityName, endCityName, startDate);
    }
}
