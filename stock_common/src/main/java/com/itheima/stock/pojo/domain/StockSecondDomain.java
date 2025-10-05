package com.itheima.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author nijikelin
 * @Date 2025/9/24 14:03
 * @Description
 */
@Data
public class StockSecondDomain {
    private BigDecimal tradeAmt;//最新交易量
    private BigDecimal preClosePrice;//前收盘价格
    private BigDecimal lowPrice;//最低价
    private BigDecimal highPrice;//最高价
    private BigDecimal openPrice;//开盘价
    private BigDecimal tradeVol;//交易金额
    private BigDecimal tradePrice;//当前价格
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date curDate;//当前日期
}
