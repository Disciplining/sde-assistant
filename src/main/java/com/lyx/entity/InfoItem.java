package com.lyx.entity;

import lombok.Data;

/**
 * 一个车辆信息
 */
@Data
public class InfoItem
{
    /**
     * 车次，唯一标识.
     */
    private String shiftNum;

    /**
     * 剩余车票
     */
    private Integer leftSeatNum;

    /**
     * 发车时间
     */
    private String sendTime;

    /**
     * 是不是加班车
     * true-是  false-不是
     */
    private Boolean workOvertime;

    /**
     * 始发车站
     */
    private String stationName;
}
