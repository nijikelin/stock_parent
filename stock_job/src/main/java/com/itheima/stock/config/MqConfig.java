package com.itheima.stock.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author nijikelin
 * @Date 2025/9/18 20:49
 * @Description rabbitMQ相关配置
 */
@Configuration
public class MqConfig {
    /**
     * 重新定义消息序列化的方式，改为基于json格式序列化和反序列化
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定义国内大盘消息队列
     * @return
     */
    @Bean
    public Queue innerMarketQueue(){
        return new Queue("innerMarketQueue",true);
    }

    /**
     * 定义路由股票信息的交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("stockExchange",true,false);
    }

    /**
     * 绑定队列到指定的交换机
     * @return
     */
    @Bean
    public Binding bindingTopicExchange(){
        return BindingBuilder.bind(innerMarketQueue()).to(topicExchange()).with("inner.market");
    }

}
