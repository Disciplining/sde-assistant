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
     * 返回数据
     */
    private T data;

    /**
     * 处理失败时的错误信息
     */
    private String errorMsg;



    private CommonResult()
    {
    }



    @Override
    public String toString()
    {
        return "CommonResult{" +
                "success=" + success +
                ", data=" + data +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }



    public static CommonResult success()
    {
        CommonResult<Object> result = new CommonResult<>();
        result.success = true;
        return result;
    }

    public static <R> CommonResult<R> successData(R data)
    {
        CommonResult<R> result = new CommonResult<R>();
        result.success = true;
        result.data = data;

        return result;
    }

    public static CommonResult error()
    {
        CommonResult<Object> result = new CommonResult<>();
        result.success = false;
        return result;
    }

    public static CommonResult errorMsg(String msg)
    {
        CommonResult<Object> result = new CommonResult<>();
        result.success = false;
        result.errorMsg = msg;
        return result;
    }
}
