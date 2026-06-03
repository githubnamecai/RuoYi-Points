# RuoYi-Points 权益积分平台

> 基于 **RuoYi-Vue 3.9.2 (Spring Boot 3.x)** 二次开发的"权益积分平台"，由三部分组成：
> 1. **后端服务**：在 RuoYi 原有 ruoyi-system / ruoyi-admin 模块中扩展，提供 PC 后台与 H5 用户端两套 API
> 2. **PC 管理端**：在 ruoyi-ui (Vue2 + Element UI) 中新增"积分商城"功能菜单
> 3. **H5 用户端**：独立的 Vue3 + Vite + Vant4 移动端项目（位于 `h5/` 目录）

## ✨ 功能特性

### PC 后台 (ruoyi-ui)
| 模块       | 路径                         | 主要功能                                       |
| :--------- | :--------------------------- | :--------------------------------------------- |
| 商品分类   | `/points/category`           | 树形分类管理（新增/编辑/删除）                 |
| 商品管理   | `/points/goods`              | 商品 CRUD、富文本描述、上下架、库存            |
| 订单管理   | `/points/order`              | 订单列表、发货（填写快递信息）、关闭（退积分） |
| 积分规则   | `/points/rule`               | 积分获取规则配置（含每日上限）                 |
| 签到配置   | `/points/sign`               | 签到积分、连续签到奖励、补签开关               |
| 积分明细   | `/points/detail`             | 全量积分流水 + 6 项统计看板 + 导出             |
| H5 用户    | `/points/h5user`             | H5 用户列表、冻结/解冻、手动调整积分           |

### H5 用户端 (h5/)
- 短信登录（开发环境万能验证码 `123456`，验证码会打印到后端控制台）
- 首页瀑布流商品（按分类筛选、分页加载）
- 商品详情 + 兑换确认（库存乐观锁 + Redis 分布式锁防超兑）
- 每日签到（月历视图、连续签到进度、补签）
- 积分明细（按月分组）
- 我的订单（状态 Tab、确认收货）
- 收货地址管理（增删改查 + 设默认 + 选择器模式）
- 个人中心（资料编辑、退出登录）

### 后端核心技术点
- **乐观锁防超兑**：`UPDATE t_goods SET stock=stock-?, version=version+1 WHERE id=? AND stock>=? AND version=?`
- **原子扣积分**：`UPDATE t_user SET points_balance=points_balance-? WHERE user_id=? AND points_balance>=? AND status='0'`
- **Redis 分布式锁**：兑换接口加锁 (`exchange:lock:{goodsId}:{userId}`, TTL 5s)
- **签到限流**：每用户每秒一次 (`h5:sign:rate:{userId}`, TTL 1s)
- **JWT 双端隔离**：H5 Token 存储在独立 Redis 命名空间 `h5_login_tokens:{uuid}`
- **拦截器分离**：`/h5-api/**` 由 `H5TokenInterceptor` 鉴权（已在 SecurityConfig 中放行）

---

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 5.7+ / 8.0+
- Redis 5.0+
- Node.js 16+ (PC 管理端) / Node.js 18+ (H5 端)

### 1️⃣ 初始化数据库

按顺序执行 SQL 文件（库名默认 `ry-vue`）：

```bash
mysql -uroot -p ry-vue < sql/ry_20250522.sql       # RuoYi 基础表（视具体仓库版本而定）
mysql -uroot -p ry-vue < sql/quartz.sql            # 定时任务表
mysql -uroot -p ry-vue < sql/points_platform.sql   # 权益积分平台业务表（本项目新增）
```

`sql/points_platform.sql` 会创建：
- 9 张业务表：`t_goods_category` / `t_goods` / `t_user` / `t_user_address` / `t_order` / `t_points_rule` / `t_sign_config` / `t_sign_record` / `t_points_detail`
- 菜单（ID 2000–2099，归属"积分商城"主菜单）
- 字典：`points_goods_type` / `points_order_status` / `points_change_type` / `points_source_type`
- 演示数据：默认签到配置、示例分类与商品、示例 H5 用户

### 2️⃣ 启动后端

修改 `ruoyi-admin/src/main/resources/application-druid.yml` 中数据库连接、`application.yml` 中 Redis 配置。

```bash
# 在项目根目录
mvn clean install -DskipTests
# 启动
cd ruoyi-admin
mvn spring-boot:run
# 或直接运行 com.ruoyi.RuoYiApplication 主类
```

默认监听 **8080** 端口；默认管理员账号 `admin / admin123`。

### 3️⃣ 启动 PC 管理端

```bash
cd ruoyi-ui
npm install
npm run dev
```

默认监听 **80** 端口，浏览器打开 `http://localhost`，使用 `admin / admin123` 登录后即可在左侧菜单看到「积分商城」。

### 4️⃣ 启动 H5 用户端

```bash
cd h5
npm install
# 开发模式（默认代理 /h5-api → http://localhost:8080）
npm run dev
# 生产打包
npm run build
# 如果代码里导入了 dayjs
npm install dayjs
```

默认监听 **5173** 端口。代理地址可在 `h5/.env.development` 修改：

```env
VITE_API_BASE=http://localhost:8080
```

H5 登录验证码：
- 开发环境点击「发送验证码」后，**实际验证码会打印到后端 console**（如：`[H5短信] 138****1234 验证码: 458291`）
- 也可直接使用万能验证码 `123456` 完成登录
- 首次登录的手机号会自动注册为 H5 用户

---

## 📁 项目结构

```
RuoYi-Points/
├── ruoyi-admin/                # 启动模块
│   └── src/main/java/com/ruoyi/web/controller/
│       ├── points/             # PC 后台 6 个控制器
│       └── h5/                 # H5 端 6 个控制器 + 拦截器 + WebMvcConfig
├── ruoyi-system/
│   └── src/main/java/com/ruoyi/points/
│       ├── constant/           # 常量（Redis key、状态码）
│       ├── domain/             # 9 个实体 + VO/DTO
│       ├── mapper/             # 9 个 Mapper 接口
│       ├── service/            # 9 个 Service 接口 + impl
│       └── service/impl/       # 含 OrderServiceImpl（核心兑换逻辑）
├── ruoyi-system/src/main/resources/mapper/points/   # 9 个 Mapper XML
├── ruoyi-framework/
│   └── .../SecurityConfig.java # 已添加 /h5-api/** 放行
├── ruoyi-ui/                   # PC 管理端 (Vue2 + Element UI)
│   └── src/
│       ├── api/points/         # 6 个 API 文件
│       └── views/points/       # 7 个页面
├── h5/                         # H5 用户端 (Vue3 + Vite + Vant4)
│   ├── src/
│   │   ├── api/                # auth / user / goods / sign
│   │   ├── stores/             # Pinia user store
│   │   ├── router/             # vue-router (hash mode)
│   │   ├── utils/              # request / auth
│   │   ├── components/         # GoodsCard
│   │   └── views/              # login / home / sign / product / order / points / user / address
│   ├── vite.config.js
│   └── package.json
└── sql/
    └── points_platform.sql     # 权益平台 SQL 初始化脚本
```

## 🔑 关键 API

### PC 后台（需 RuoYi Token）
| 方法   | 路径                                | 说明           |
| :----- | :---------------------------------- | :------------- |
| GET    | `/points/category/tree`             | 分类树         |
| GET    | `/points/goods/list`                | 商品列表       |
| PUT    | `/points/order/ship`                | 订单发货       |
| PUT    | `/points/order/close/{id}`          | 关闭并退积分   |
| PUT    | `/points/h5user/adjust`             | 手动调整积分   |
| GET    | `/points/detail/stat`               | 积分统计       |

### H5 端（需 `Authorization: Bearer xxx`）
| 方法   | 路径                                | 说明            |
| :----- | :---------------------------------- | :-------------- |
| POST   | `/h5-api/auth/sms`                  | 发送验证码      |
| POST   | `/h5-api/auth/login`                | 登录            |
| GET    | `/h5-api/goods/list`                | 商品列表        |
| POST   | `/h5-api/exchange/submit`           | 兑换下单        |
| GET    | `/h5-api/sign/info`                 | 签到信息（月历）|
| POST   | `/h5-api/sign/do`                   | 每日签到        |
| POST   | `/h5-api/sign/repair`               | 补签            |
| GET    | `/h5-api/user/orders`               | 我的订单        |
| GET    | `/h5-api/user/addresses`            | 收货地址        |

---

<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">RuoYi v3.9.2</h1>
<h4 align="center">基于SpringBoot+Vue前后端分离的Java快速开发框架</h4>
<p align="center">
	<a href="https://gitee.com/y_project/RuoYi-Vue/stargazers"><img src="https://gitee.com/y_project/RuoYi-Vue/badge/star.svg?theme=dark"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue"><img src="https://img.shields.io/badge/RuoYi-v3.9.2-brightgreen.svg"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 平台简介

若依是一套全部开源的快速开发平台，毫无保留给个人及企业免费使用。

* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。
* 阿里云折扣场：[点我进入](http://aly.ruoyi.vip)，腾讯云秒杀场：[点我进入](http://txy.ruoyi.vip)&nbsp;&nbsp;

# 版本分支

RuoYi-Vue 后端项目提供 Spring Boot 2.x / 3.x / 4.x 多版本分支的并行维护。

| 名称              | 说明                      | 地址                                                    |
| :---------------- | :------------------------ | :------------------------------------------------------ |
| master 默认分支   | Spring Boot 4.x (JDK 17+) | https://gitee.com/y_project/RuoYi-Vue                   |
| springboot3 分支  | Spring Boot 3.x (JDK 17+) | https://gitee.com/y_project/RuoYi-Vue/tree/springboot3  |
| springboot2 分支  | Spring Boot 2.x (JDK 8+)  | https://gitee.com/y_project/RuoYi-Vue/tree/springboot2  |

RuoYi-Vue 前端项目提供 Vue 2.x / 3.x / JavaScript TypeScript 版本均可混用搭配

| 项目名称      | **RuoYi-Vue** | **RuoYi-Vue3** | **RuoYi-Vue3-TypeScript**   |
| :---          | :---          | :---           | :---                        |
| **前端框架**  | Vue 2        | Vue 3          | Vue 3                       |
| **脚本语言**  | JavaScript   | JavaScript     | TypeScript                  |
| **构建工具**  | Vue CLI      | Vite           | Vite                        |
| **UI 组件库** | Element UI   | Element Plus   | Element Plus                |
| **状态管理**  | Vuex         | Pinia          | Pinia                       |
| **路由管理**  | Vue Router 3 | Vue Router 4   | Vue Router 4                |
| **核心特点**  | 1. 技术栈经典稳定<br>2. 社区资料丰富<br>3. 当前维护重心已转移 | 1. 现代前端技术栈<br>2. 开发体验与性能更优<br>3. 官方主推的活跃版本 | 1. 类型加持，减少沟通成本<br>2. 开发时有提示，效率更高<br>3. 多人协作企业级开发项目 |
| **仓库地址**  | [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) | [RuoYi-Vue3](https://gitcode.com/yangzongzhuan/RuoYi-Vue3) | [RuoYi-Vue3-TypeScript](https://gitcode.com/yangzongzhuan/RuoYi-Vue3/tree/typescript) |

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存信息查询，命令统计等。
17. 在线构建器：拖动表单元素生成相应的HTML代码。
18. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

