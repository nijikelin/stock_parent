package com.itheima.stock;

import com.itheima.stock.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author nijikelin
 * @Date 2025/9/22 16:27
 * @Description
 */
@SpringBootTest
public class TestInner {

    @Autowired
    private StockService stockService;

    @Test
    public void testInner(){
//        stockService.getStock4();
        stockService.innerIndexAll();
    }
}
