package com.itheima.stock;

import com.itheima.stock.service.StockTimerTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description
 * @Author 1
 * @Date 2025/9/15 19:06
 */
@SpringBootTest
public class Test01 {
    @Autowired
    private StockTimerTaskService stockTimerTaskService;
    @Test
    public void testInnerMarket(){
//        stockTimerTaskService.getInnerMarketInfo();
//        stockTimerTaskService.getStockRtIndex();
//        stockTimerTaskService.getStockBlockRtInfo();
        stockTimerTaskService.getOuterMarketInfo();
    }
}
