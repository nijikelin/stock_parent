package com.itheima.stock.service;

import com.itheima.stock.pojo.entity.StockBusiness;

import java.util.List;

/**
 * @Author nijikelin
 * @Date 2025/10/4 20:11
 * @Description 定义股票缓存层
 */
public interface StockCacheFace {
    /**
     * 获取所有股票编码
     * @return
     */
    List<String> getAllStockCodeWithPredix();

    void updateStockInfoById(StockBusiness info);
}
