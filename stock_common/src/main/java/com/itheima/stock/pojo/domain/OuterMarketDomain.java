package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author nijikelin
 * @Date 2025/9/23 18:53
 * @Description 外盘数据实体类
 */
@Data
public class OuterMarketDomain {

    /**
     * 大盘名称
     */
    private String name;
    /**
     * 当前点
     */
    private BigDecimal curPoint;
    /**
     * 涨跌值
     */
    private BigDecimal upDown;
    /**
     * 涨幅
     */
    private BigDecimal rose;
    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date curTime;
}
