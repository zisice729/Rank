package org.zisice.douyin_rank_1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 抖音排行榜应用启动类
 */
@SpringBootApplication
@MapperScan("org.zisice.douyin_rank_1.mapper")
public class DouYinRank1Application {

    public static void main(String[] args) {
        SpringApplication.run(DouYinRank1Application.class, args);
    }

}