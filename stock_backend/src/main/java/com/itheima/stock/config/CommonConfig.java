package com.itheima.stock.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.itheima.stock.pojo.vo.StockInfoConfig;
import com.itheima.stock.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(StockInfoConfig.class)
@Configuration
public class CommonConfig {

//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    IdWorker idWorker(){
        return new IdWorker(1L,2L);
    }

    /**
     * 构建缓存Bean
     * @return
     */
    @Bean
    public Cache<String,Object> caffeineCache(){
        Cache<String, Object> cache = Caffeine
                .newBuilder()
                .maximumSize(200)
                .initialCapacity(100)
                .recordStats()
                .build();
        return cache;
    }

}
