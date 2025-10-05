package com.itheima.stock.job;

import com.itheima.stock.service.StockTimerTaskService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author nijikelin
 * @Date 2025/9/20 13:53
 * @Description 定义股票任务
 */
@Component
public class StockJob {

    @Autowired
    private StockTimerTaskService stockTimerTaskService;
    /**
     * 1、股票任务（Bean模式）
     */
    @XxlJob("stockHandler")
    public void stockHandler() {
        System.out.println("jobTest run...");
    }
    /**
     * 1、采集大盘数据任务（Bean模式）
     */
    @XxlJob("getInnerMarketInfo")
    public void getInnerMarketInfo() {
        stockTimerTaskService.getInnerMarketInfo();
    }
    /**
     * 1、采集个股数据任务（Bean模式）
     */
    @XxlJob("getStockRtIndex")
    public void getStockRtIndex() {
        stockTimerTaskService.getStockRtIndex();
    }
    /**
     * 1、采集板块数据任务（Bean模式）
     */
    @XxlJob("getStockBlockRtInfo")
    public void getStockBlockRtInfo() {
        stockTimerTaskService.getStockBlockRtInfo();
    }
    /**
     * 1、采集板块数据任务（Bean模式）
     */
    @XxlJob("getOuterMarketInfo")
    public void getOuterMarketInfo() {
        stockTimerTaskService.getOuterMarketInfo();
    }
}
