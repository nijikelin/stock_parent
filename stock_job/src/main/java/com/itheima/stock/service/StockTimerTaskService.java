package com.itheima.stock.service;

/**
 * @ClassName StockTimerTaskService
 * @Description todo:定义采集股票数据的定时任务的服务接口
 * @Author 1
 * @Date 2025/9/15 16:29
 * @Version 1.0
 */
public interface StockTimerTaskService {
    /**
     * 获取国内大盘实时股票信息
     */
    void getInnerMarketInfo();
    /**
     * 定义获取分钟级股票数据
     */
    void getStockRtIndex();

    void getStockBlockRtInfo();

    void getOuterMarketInfo();
}
