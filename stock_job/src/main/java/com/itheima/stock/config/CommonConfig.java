package com.itheima.stock.config;

import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.utils.IdWorker;
import com.itheima.stock.utils.ParserStockInfoUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(StockInfoConfig.class)
@Configuration
public class CommonConfig {

    @Bean
    IdWorker idWorker(){
        return new IdWorker(1L,2L);
    }

    @Bean
    ParserStockInfoUtil parserStockInfoUtil(IdWorker idWorker){
        return new ParserStockInfoUtil(idWorker);
    }

}