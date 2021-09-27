package com.lyx.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用返回类
 */
@Getter
@Setter
public class CommonResult<T>
{
    /**
     * 是否处理成功
     */
    private boolean success;

    /**
     * 状态码
     * 0  - 处理成功
     * -1 - 处理失败
     */
    private Integer code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 处理失败时的错误信息
     */
    private String msg;



    private CommonResult()
    {
    }



    @Override
    public String toString()
    {
        return "CommonResult{" +
                "success=" + success +
                ", data=" + data +
                ", errorMsg='" + msg + '\'' +
                '}';
    }



    public static CommonResult success()
    {
        CommonResult<Object> result = new CommonResult<>();
        result.success = true;
        result.code = 0;
        return result;
    }

    public static <R> CommonResult<R> successData(R data)
    {
        CommonResult<R> result = new CommonResult<R>();
        result.success = true;
        result.code = 0;
        result.data = data;

        return result;
    }

    public static CommonResult error()
    {
        CommonResult<Object> result = new CommonResult<>();
        result.success = false;
        result.code = -1;
        return result;
    }

    public static CommonResult errorMsg(String msg)
    {
        CommonResult<Object> result = new CommonResult<>();
        result.success = false;
        result.msg = msg;
        result.code = -1;
        return result;
    }
}
