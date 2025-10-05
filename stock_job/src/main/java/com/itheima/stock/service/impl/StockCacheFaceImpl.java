package com.itheima.stock.service.impl;

import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.mapper.StockBusinessMapper;
import com.itheima.stock.pojo.entity.StockBusiness;
import com.itheima.stock.service.StockCacheFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author nijikelin
 * @Date 2025/10/4 20:17
 * @Description
 */
@Component
@CacheConfig(cacheNames = StockConstant.STOCK)
public class StockCacheFaceImpl implements StockCacheFace {
    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Cacheable(key = "#root.method.getName()")
    @Override
    public List<String> getAllStockCodeWithPredix() {
        List<String> stockCodes = stockBusinessMapper.getAllStockCodes();
        List<String> codesWithPrefix = stockCodes.stream().map(code -> code.startsWith("6") ? "sh" + code : "sz" + code
        ).collect(Collectors.toList());
        return codesWithPrefix;
    }

    @CacheEvict(key = "getAllStockCodeWithPredix")
    @Override
    public void updateStockInfoById(StockBusiness info) {
        stockBusinessMapper.updateByPrimaryKeySelective(info);

    }
}
