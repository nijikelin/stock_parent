package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author nijikelin
 * @Date 2025/9/24 19:00
 * @Description 个股交易流水行情
 */
@Data
public class StockScreenSecondDomain {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
    private Date date;//当前时间，精确到分
    private BigDecimal tradeAmt;//交易量
    private BigDecimal tradeVol;//交易金额
    private BigDecimal tradePrice;//交易价格
}
