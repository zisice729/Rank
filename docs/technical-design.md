# Rank 项目技术设计方案

## 1. 业务背景

### 1.1 项目概述

Rank 项目是一个商家排行榜数据服务，主要提供以下核心功能：

- **排行榜查询**：支持按城市、榜单类型、类目三个维度查询商家排行榜数据
- **数据同步**：通过定时任务将数据库中的排行榜数据同步到 Redis 缓存
- **高性能读取**：查询请求直接从 Redis 缓存读取，保证毫秒级响应

### 1.2 业务场景

| 场景 | 描述 |
|------|------|
| 用户浏览排行榜 | 用户通过APP/小程序查看不同城市、不同类型的商家排行榜 |
| 数据定时更新 | 每天定时从数据源获取最新的排行榜数据，更新到数据库和缓存 |
| 多维度筛选 | 支持全国/城市、爆款/飙升、全品类/特定类目等组合查询 |

### 1.3 数据维度

| 维度 | 取值 | 说明 |
|------|------|------|
| 城市ID | `000000`(全国), `100000`(北京), `310000`(上海), `440100`(广州), `440300`(深圳) | 6位数字编码 |
| 榜单类型 | `0`(爆款榜), `1`(飙升榜) | 数字标识 |
| 类目 | `0`(全部), `1`(美食) | 数字标识 |

---

## 2. 系统架构

### 2.1 架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端 (Client)                         │
│                     (APP / 小程序 / Web)                       │
└─────────────────────────────┬─────────────────────────────────┘
                              │ HTTP POST
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Controller Layer                            │
│                  RankController                                │
│              POST /rank/list                                   │
└─────────────────────────────┬─────────────────────────────────┘
                              │ 方法调用
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Service Layer                               │
│                  RankService (接口)                             │
│                  RankServiceImpl (实现)                         │
│           ┌──────────────────┬──────────────────┐              │
│           ▼                  ▼                  ▼              │
│      缓存读取            数据刷新            参数校验            │
└────────────┬─────────────────┬──────────────────┬──────────────┘
             │                 │                  │
             ▼                 ▼                  │
┌─────────────────────┐  ┌─────────────────────┐ │
│      Redis          │  │      MySQL          │ │
│   (缓存层)          │  │   (持久化层)        │ │
└─────────────────────┘  └─────────────────────┘ │
                                                 │
                                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Task Layer (XXL-Job)                        │
│                  RankSyncTask                                  │
│              定时调用 rankRefresh()                             │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 模块划分

| 模块 | 目录 | 职责 |
|------|------|------|
| `controller` | `com.rank.controller` | REST API 控制层，处理HTTP请求 |
| `service` | `com.rank.service` | 业务逻辑层，核心业务处理 |
| `mapper` | `com.rank.mapper` | 数据访问层，数据库操作 |
| `entity` | `com.rank.common.entity` | 数据库实体类 |
| `enums` | `com.rank.common.enums` | 枚举定义 |
| `config` | `com.rank.config` | 配置类 |
| `task` | `com.rank.task` | 定时任务 |

### 2.3 技术栈

| 组件 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 4.0.6 | 应用框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | - | 缓存层 |
| XXL-Job | 2.4.0 | 分布式定时任务 |
| FastJSON | 2.0.42 | JSON序列化 |
| Lombok | - | 代码简化 |

---

## 3. 核心流程

### 3.1 查询流程

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        查询流程图                                        │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  客户端                                                                  │
│    │                                                                     │
│    │ POST /rank/list?city_id=000000&type=0&category=0                    │
│    ▼                                                                     │
│  RankController.getRankList()                                            │
│    │                                                                     │
│    │ 参数透传 (cityId, type, category)                                   │
│    ▼                                                                     │
│  RankService.list()                                                      │
│    │                                                                     │
│    │ 1. 获取版本日期                                                      │
│    │    redisTemplate.get("dy:rank:date:refresh")                         │
│    │                                                                     │
│    │ 2. 构建Redis Key                                                    │
│    │    "dy:rank:date:{date}:type:{type}:cityId:{cityId}:category:{cat}"  │
│    │                                                                     │
│    │ 3. 读取缓存                                                         │
│    │    redisTemplate.get(redisKey)                                      │
│    │                                                                     │
│    │ 4. 缓存为空?                                                        │
│    │    ├─ 是 → 返回空列表                                               │
│    │    └─ 否 → JSON反序列化                                             │
│    │                                                                     │
│    ▼                                                                     │
│  返回 List<MerchantRankInfo>                                             │
│    │                                                                     │
│    ▼                                                                     │
│  客户端接收响应                                                           │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.2 同步流程

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        同步流程图                                        │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  XXL-Job 调度中心                                                         │
│    │                                                                     │
│    │ 定时触发 (如: 每天凌晨1点)                                           │
│    ▼                                                                     │
│  RankSyncTask.rankRefresh()                                              │
│    │                                                                     │
│    │ 调用 RankService.rankRefresh()                                      │
│    ▼                                                                     │
│  RankServiceImpl.rankRefresh()                                           │
│    │                                                                     │
│    │ 1. 获取当前日期 (yyyy-MM-dd)                                        │
│    │                                                                     │
│    │ 2. 查询数据库                                                        │
│    │    "select * from merchant_rank_info where date = '{date}'"         │
│    │                                                                     │
│    │ 3. 数据为空?                                                        │
│    │    ├─ 是 → 返回 ReturnT.FAIL                                       │
│    │    └─ 否 → 继续                                                     │
│    │                                                                     │
│    │ 4. 按维度分组                                                        │
│    │    Map<redisKey, List<MerchantRankInfo>>                            │
│    │                                                                     │
│    │ 5. 写入Redis缓存 (过期时间7天)                                        │
│    │    redisTemplate.set(redisKey, JSON.toJSONString(data), 7天)        │
│    │                                                                     │
│    │ 6. 更新版本号                                                       │
│    │    redisTemplate.set("dy:rank:date:refresh", date, 7天)             │
│    │                                                                     │
│    ▼                                                                     │
│  返回 ReturnT.SUCCESS                                                    │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 4. HTTP 接口

### 4.1 查询排行榜列表

| 属性 | 值 |
|------|------|
| **路径** | `/rank/list` |
| **方法** | `POST` |
| **Content-Type** | `application/x-www-form-urlencoded` |

#### 请求参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `city_id` | String | 否 | `000000` | 城市ID，000000表示全国 |
| `type` | Integer | 否 | `0` | 榜单类型：0-爆款榜，1-飙升榜 |
| `category` | Integer | 否 | `0` | 类目：0-全部，1-美食 |

#### 请求示例

```bash
curl -X POST "http://localhost:8080/rank/list" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "city_id=000000&type=0&category=0"
```

#### 响应示例

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "cityId": "000000",
      "type": 0,
      "category": 0,
      "merchantId": "MERCHANT_001",
      "sort": 1,
      "saleNumMonth": 10000,
      "saleNumDay": 500,
      "date": "2026-06-18",
      "isDelete": 0,
      "createTime": "2026-06-18T00:00:00",
      "updateTime": "2026-06-18T00:00:00"
    },
    {
      "id": 2,
      "cityId": "000000",
      "type": 0,
      "category": 0,
      "merchantId": "MERCHANT_002",
      "sort": 2,
      "saleNumMonth": 8000,
      "saleNumDay": 400,
      "date": "2026-06-18",
      "isDelete": 0,
      "createTime": "2026-06-18T00:00:00",
      "updateTime": "2026-06-18T00:00:00"
    }
  ]
}
```

#### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 主键ID |
| `cityId` | String | 城市ID |
| `type` | Integer | 榜单类型 |
| `category` | Integer | 类目 |
| `merchantId` | String | 商家ID |
| `sort` | Integer | 排名 |
| `saleNumMonth` | Integer | 月销量 |
| `saleNumDay` | Integer | 日销量 |
| `date` | String | 统计日期 |
| `isDelete` | Integer | 是否已删除 |
| `createTime` | String | 创建时间 |
| `updateTime` | String | 更新时间 |

---

## 5. 数据库设计

### 5.1 表结构

**表名**: `merchant_rank_info`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 主键 |
| `city_id` | VARCHAR(6) | NOT NULL | 城市ID |
| `type` | INT | NOT NULL | 榜单类型 |
| `category` | INT | NOT NULL | 类目 |
| `merchant_id` | VARCHAR(64) | NOT NULL | 商家ID |
| `sort` | INT | NOT NULL | 排名 |
| `sale_num_month` | INT | DEFAULT 0 | 月销量 |
| `sale_num_day` | INT | DEFAULT 0 | 日销量 |
| `date` | VARCHAR(10) | NOT NULL | 统计日期 |
| `is_delete` | TINYINT | DEFAULT 0 | 逻辑删除 |
| `create_time` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| `update_time` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

### 5.2 索引建议

```sql
-- 联合索引：按日期+城市+类型+类目查询
CREATE INDEX idx_date_city_type_category ON merchant_rank_info(date, city_id, type, category);

-- 商家ID索引
CREATE INDEX idx_merchant_id ON merchant_rank_info(merchant_id);
```

---

## 6. Redis 设计

### 6.1 Key 设计

| Key | 类型 | 格式 | 说明 |
|-----|------|------|------|
| `dy:rank:date:refresh` | String | `yyyy-MM-dd` | 当前数据版本日期 |
| `dy:rank:date:{date}:type:{type}:cityId:{cityId}:category:{category}` | String | JSON数组 | 具体维度的排行榜数据 |

### 6.2 数据结构示例

**版本号 Key**:
```
Key: "dy:rank:date:refresh"
Value: "2026-06-18"
```

**数据 Key**:
```
Key: "dy:rank:date:2026-06-18:type:0:cityId:000000:category:0"
Value: "[{\"id\":1,\"cityId\":\"000000\",\"type\":0,\"category\":0,...}, ...]"
```

### 6.3 过期策略

| Key类型 | 过期时间 | 说明 |
|---------|----------|------|
| 版本号 | 7天 | 保证数据不会无限期留存 |
| 排行榜数据 | 7天 | 同版本号，保证一致性 |

---

## 7. 配置说明

### 7.1 application.properties

```properties
# 应用配置
spring.application.name=rank

# MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/example_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis-Plus 配置
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.mapper-locations=classpath:mapper/*.xml

# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
spring.data.redis.timeout=3000ms

# XXL-Job 配置
xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin
xxl.job.accessToken=
xxl.job.executor.appname=rank-executor
xxl.job.executor.address=
xxl.job.executor.ip=
xxl.job.executor.port=9999
xxl.job.executor.logpath=/data/applogs/xxl-job/jobhandler
xxl.job.executor.logretentiondays=30
```

### 7.2 配置项说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.datasource.url` | MySQL连接地址 | `jdbc:mysql://localhost:3306/example_db` |
| `spring.datasource.username` | MySQL用户名 | `root` |
| `spring.datasource.password` | MySQL密码 | `123456` |
| `spring.data.redis.host` | Redis主机 | `localhost` |
| `spring.data.redis.port` | Redis端口 | `6379` |
| `xxl.job.admin.addresses` | XXL-Job管理地址 | `http://127.0.0.1:8080/xxl-job-admin` |
| `xxl.job.executor.appname` | 执行器名称 | `rank-executor` |
| `xxl.job.executor.port` | 执行器端口 | `9999` |

---

## 8. 项目目录结构

```
Rank/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── rank/
│       │           ├── common/           # 公共模块
│       │           │   ├── entity/       # 实体类
│       │           │   │   └── MerchantRankInfo.java
│       │           │   └── enums/        # 枚举类
│       │           │       └── RankDimension.java
│       │           ├── config/           # 配置类
│       │           │   └── RedisConfig.java
│       │           ├── controller/       # 控制器
│       │           │   └── RankController.java
│       │           ├── mapper/           # 数据访问层
│       │           │   └── MerchantRankInfoMapper.java
│       │           ├── service/          # 服务层
│       │           │   ├── RankService.java
│       │           │   └── impl/
│       │           │       └── RankServiceImpl.java
│       │           ├── task/             # 定时任务
│       │           │   └── RankSyncTask.java
│       │           └── RankApplication.java  # 启动类
│       └── resources/
│           ├── mapper/                   # MyBatis映射文件
│           │   └── MerchantRankInfoMapper.xml
│           └── application.properties    # 应用配置
├── docs/                                 # 文档目录
│   └── technical-design.md               # 技术设计文档
└── pom.xml                               # Maven配置
```

---

## 9. 定时任务配置

### 9.1 XXL-Job 任务配置

| 属性 | 值 |
|------|------|
| **任务名称** | `rankRefresh` |
| **任务描述** | 排行榜数据同步任务 |
| **执行器** | `rank-executor` |
| **Cron表达式** | `0 0 1 * * ?` (每天凌晨1点执行) |
| **运行模式** | BEAN模式 |
| **JobHandler** | `rankRefresh` |

---

## 10. 核心类说明

### 10.1 类职责表

| 类名 | 路径 | 职责 |
|------|------|------|
| `RankApplication` | `com.rank` | 应用启动类 |
| `RedisConfig` | `com.rank.config` | Redis模板配置 |
| `RankController` | `com.rank.controller` | HTTP接口控制层 |
| `MerchantRankInfo` | `com.rank.common.entity` | 数据库实体 |
| `RankDimension` | `com.rank.common.enums` | 排行榜维度枚举 |
| `MerchantRankInfoMapper` | `com.rank.mapper` | 数据访问接口 |
| `RankService` | `com.rank.service` | 业务服务接口 |
| `RankServiceImpl` | `com.rank.service.impl` | 业务服务实现 |
| `RankSyncTask` | `com.rank.task` | XXL-Job定时任务 |

### 10.2 关键方法说明

| 方法 | 所属类 | 说明 |
|------|--------|------|
| `getRankList()` | `RankController` | 处理排行榜查询请求 |
| `list()` | `RankServiceImpl` | 从Redis读取排行榜数据 |
| `rankRefresh()` | `RankServiceImpl` | 从数据库同步数据到Redis |
| `rankRefresh()` | `RankSyncTask` | XXL-Job定时任务入口 |
| `listRankInfoBySQL()` | `MerchantRankInfoMapper` | 执行自定义SQL查询 |
