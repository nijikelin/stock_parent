package com.itheima.stock;

import com.itheima.stock.service.StockCacheFace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author nijikelin
 * @Date 2025/10/4 20:31
 * @Description 缓存测试类
 */
@SpringBootTest
public class TestSpringCache {
    @Autowired
    private StockCacheFace stockCacheFace;
    @Test
    public void testCache(){
        List<String> all = stockCacheFace.getAllStockCodeWithPredix();
        System.out.println(all);
    }
}
