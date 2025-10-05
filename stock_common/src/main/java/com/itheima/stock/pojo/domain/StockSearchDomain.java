package com.itheima.stock.pojo.domain;

import lombok.Data;

/**
 * @Author nijikelin
 * @Date 2025/9/24 7:27
 * @Description 股票查询实体类
 */
@Data
public class StockSearchDomain {
    private String name;
    private String code;
}
