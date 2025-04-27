# 用户管理系统

## 项目简介
这是一个基于 Spring Boot 和 Vue.js 的前后端分离的用户管理系统，主要实现登录功能、超级管理员后台管理功能及用户积分查询功能。

## 技术栈
- **后端**：
  - Spring Boot 3.1.2
  - MyBatis 3.0.0
  - JWT 4.4.0
  - MySQL
  - Redis
- **前端**：(待实现)
  - Vue.js
  - Element UI

## 系统功能

### 1. 登录模块
- 提供用户名和密码登录功能
- 密码采用 MD5 加密
- 使用 JWT 实现身份验证

### 2. 用户管理模块
- 超级管理员可查看所有用户信息
- 支持新增和编辑用户功能
- 支持禁用/启用用户功能

### 3. 积分查询模块
- 超级管理员可查看所有用户的积分明细
- 普通用户只能查看自己的积分明细
- 支持按日期范围、积分类型查询

### 4. 积分日志模块
- 自动记录用户积分变动历史
- 记录积分变动的详细信息（操作人、操作类型、变动前后积分等）
- 支持查询积分变动历史记录

## 数据库设计
- **用户表(users)**：存储用户基本信息
- **积分表(points)**：存储用户积分记录
- **积分日志表(point_logs)**：存储用户积分变动历史记录

## API 接口

### 登录模块
- `POST /api/login`：用户登录

### 用户管理模块
- `GET /api/admin/users`：获取用户列表
- `GET /api/admin/users/{id}`：获取用户详情
- `POST /api/admin/users`：新增用户
- `PUT /api/admin/users/{id}`：编辑用户
- `PATCH /api/admin/users/{id}/status`：更新用户状态

### 积分查询模块
- `GET /api/points`：查询积分明细
- `POST /api/points`：添加积分记录
- `GET /api/points/total`：获取用户总积分

### 积分日志模块
- `GET /api/pointlogs`：查询积分变动日志

## 项目启动

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 5.7+

### 配置修改
1. 修改 `src/main/resources/application.yml` 中的数据库连接信息
2. 根据实际情况调整 JWT 配置等

### 运行步骤
1. 首先创建数据库：
```sql
CREATE DATABASE operation;
```

2. 使用 schema.sql 初始化数据库表结构和测试数据

3. 编译打包项目：
```bash
mvn clean package
```

4. 运行项目：
```bash
java -jar target/Springboot-0.0.1-SNAPSHOT.jar
```

## 默认账户
- 超级管理员：admin / admin123
- 普通用户：user1 / 123456 