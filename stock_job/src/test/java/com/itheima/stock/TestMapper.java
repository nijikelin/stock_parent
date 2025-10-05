package com.itheima.stock;

import com.google.common.collect.Lists;
import com.itheima.stock.mapper.StockBusinessMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author nijikelin
 * @Date 2025/9/16 20:51
 * @Description
 */
@SpringBootTest
public class TestMapper {
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Test
    public void testcodes(){
        List<String> allStockCodes = stockBusinessMapper.getAllStockCodes();
        allStockCodes = allStockCodes.stream().map(code->code.startsWith("6")?"sh"+code:"sz"+code).collect(Collectors.toList());
//        System.out.println(allStockCodes);
        List<List<String>> allCodes = Lists.partition(allStockCodes, 15);
        allCodes.forEach(codes-> System.out.println("size"+codes.size()+":"+codes));
    }
}
