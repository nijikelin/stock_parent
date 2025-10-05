package com.itheima.stock.mq;

import com.github.benmanes.caffeine.cache.Cache;
import com.itheima.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author nijikelin
 * @Date 2025/9/19 17:10
 * @Description 监听股市变化消息
 */
@Component
@Slf4j
public class MqListener {
    @Autowired
    private Cache<String,Object> caffeineCache;

    @Autowired
    private StockService stockService;


    @RabbitListener(queues = "innerMarketQueue")
    public void acceptInnerMarketInfo(Date date)throws Exception{
        //获取时间毫秒差值
        long diffTime= DateTime.now().getMillis()-new DateTime(date).getMillis();
        //超过一分钟告警
        if (diffTime>60000) {
            log.error("采集国内大盘时间点：{},同步超时：{}ms",new DateTime(date).toString("yyyy-MM-dd HH:mm:ss"),diffTime);
        }
        //将缓存置为失效删除
        caffeineCache.invalidate("innerMarketInfos");
        //调用服务更新缓存
        stockService.innerIndexAll();
    }
}
