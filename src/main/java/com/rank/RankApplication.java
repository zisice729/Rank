package com.rank;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 排行榜服务应用启动类
 * <p>
 * 该服务提供商家排行榜数据的查询和同步功能，
 * 支持按城市、榜单类型、类目等维度进行排行榜查询，
 * 并通过定时任务将数据库中的排行榜数据同步到Redis缓存。
 * </p>
 */
@SpringBootApplication
@MapperScan("com.rank.mapper")
public class RankApplication {

    /**
     * 应用程序入口方法
     * <p>
     * 启动Spring Boot应用，自动扫描并装配所有Bean组件。
     * </p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(RankApplication.class, args);
    }

}