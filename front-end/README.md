# Spring Demo 前端项目

这是一个基于Vue 3 + TypeScript + Vite + Element Plus开发的管理系统前端。

## 技术栈

- Vue 3
- TypeScript
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios

## 功能模块

- 用户认证（登录/注销）
- 首页仪表盘
- 用户管理
- 积分管理
- 个人中心

## 项目结构

```
├── src/                  # 源代码
│   ├── api/              # API接口定义
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── layouts/          # 布局组件
│   ├── router/           # 路由配置
│   ├── stores/           # 状态管理
│   ├── types/            # TypeScript类型定义
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── public/               # 公共资源
├── index.html            # HTML模板
├── package.json          # 项目依赖
├── tsconfig.json         # TypeScript配置
└── vite.config.ts        # Vite配置
```

## 启动项目

1. 安装依赖

```bash
npm install
```

2. 启动开发服务器

```bash
npm run dev
```

3. 打包生产环境

```bash
npm run build
```

## 接口说明

前端通过API与后端进行通信，接口基础路径为`/api`。主要接口包括：

- 用户认证
  - POST `/api/login` - 用户登录
  - GET `/api/user/info` - 获取用户信息

- 用户管理（管理员）
  - GET `/api/admin/users` - 获取用户列表
  - GET `/api/admin/users/:id` - 获取用户详情
  - POST `/api/admin/users` - 添加用户
  - PUT `/api/admin/users/:id` - 更新用户
  - PATCH `/api/admin/users/:id/status` - 更新用户状态

- 积分相关
  - GET `/api/points` - 查询积分明细
  - GET `/api/points/total` - 获取用户总积分
  - POST `/api/points` - 添加积分记录（管理员）

## 开发注意事项

1. 使用TypeScript进行类型检查
2. 使用Pinia进行状态管理
3. 使用Vue Router处理路由
4. 使用Element Plus组件库构建界面
5. API请求统一通过Axios进行封装 