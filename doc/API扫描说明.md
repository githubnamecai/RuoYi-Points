# RuoYi-Points 权益平台 API 接口说明

- 编制时间：2026-11-19
- 适用系统：
  - 后台（管理端）服务：`http://xxx.xxx.xxx.xxx:8080`
  - 前端（H5 移动端 / 管理端页面）：`http://xxx.xxx.xxx.xxx:81`
- 安全说明：本文档仅描述接口与鉴权方式，**不包含**任何真实 Token、密钥、密码或示例值。所有凭证类字段一律以占位符表示。

---

## 一、鉴权方式

本系统**只接受 HTTP 请求头中的 Bearer Token**，**不读取 Cookie，也没有独立的 API Key**。调用业务接口必须携带有效 Token，否则一律返回 401。

| 项目 | 说明 |
|------|------|
| 鉴权头名称 | `Authorization` |
| 取值格式 | `Bearer <token>`（`Bearer` 后有一个空格） |
| Token 类型 | 登录后由服务端下发的会话令牌（随机串），存于服务端 Redis，有一定有效期 |
| 是否接受 Cookie | **不接受**。`Admin-Token` 只是前端浏览器本地暂存，后台接口不读取它 |
| 是否有 API Key | **无**。系统不提供静态 API Key，任何调用方都通过登录换取会话 Token |

> ⚠️ **重要**：Cookie 导出文件（Netscape cookies.txt，含 `Admin-Token`）**无法**用于鉴权。正确做法是从登录响应或浏览器本地存储中取得 Token 值，再以 `Authorization: Bearer <token>` 请求头方式注入。

### 鉴权模型（分端说明）

| 端 | Spring Security 层 | 应用层拦截器 | 说明 |
|----|--------------------|--------------|------|
| 后台 `/system/**`、`/points/**`、`/monitor/**` | 校验 `Authorization: Bearer <token>` | 无 | 未带或无效 Token → 401 |
| H5 端 `/h5-api/**` | 整体 `permitAll`（由应用层拦截器二次鉴权） | `H5TokenInterceptor` 校验 Token | 除少数公开接口外，其余需带 Token，否则 401 |

### 凭证时效

- Token 有**有效期**，到期后需重新登录换取；请在使用全程保持 Token 有效。
- 使用期间**不要退出登录**，也不要删除浏览器中对应会话。
- 凭证应**尽快使用**，取得后请勿长时间闲置。

---

## 二、服务端口与入口

| 项 | 地址 | 说明 |
|----|------|------|
| 后台服务（API） | `http://xxx.xxx.xxx.xxx:8080` | 所有后台接口的根地址 |
| 前端（管理端页面） | `http://xxx.xxx.xxx.xxx:81` | 后台管理页面（`ruoyi-ui`），静态页面，非鉴权入口 |
| H5 移动端页面 | 经 `http://xxx.xxx.xxx.xxx:81` 或独立配置访问 | H5 前端，调用 `/h5-api/**` |

> 接口鉴权入口是后台 8080 端口；前端 81 端口主要是静态页面与代理转发，实际数据接口仍在 8080。

---

## 三、后台接口清单

以下为后台端已注册的接口路径，统一使用 `Authorization: Bearer <token>` 头（标注"公开"的接口除外）。

### 3.1 后台认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/login` | 登录，换取 Token |
| POST | `/register` | 自助注册（默认关闭） |
| GET | `/captchaImage` | 图形验证码 |
| GET | `/getInfo` | 获取当前用户信息（需 Token） |
| GET | `/getRouters` | 获取路由菜单（需 Token） |
| POST | `/scan/scan` | 扫码统计上报（匿名公开） |

### 3.2 后台系统管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET/... | `/system/user/**` | 用户管理 |
| GET/... | `/system/role/**` | 角色管理 |
| GET/... | `/system/menu/**` | 菜单管理 |
| GET/... | `/system/dept/**` | 部门管理 |
| GET/... | `/system/post/**` | 岗位管理 |
| GET/... | `/system/dict/**` | 字典管理（类型 / 数据） |
| GET/... | `/system/config/**` | 参数配置 |
| GET/... | `/system/notice/**` | 通知公告 |
| GET/PUT | `/system/user/profile**` | 个人中心 |
| GET | `/system/user/export` | 用户导出 |

### 3.3 后台监控接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/monitor/online/*` | 在线用户 |
| GET | `/monitor/operlog/**` | 操作日志 |
| GET | `/monitor/logininfor/**` | 登录日志 |
| GET | `/monitor/server` | 服务器监控 |
| GET | `/monitor/cache` | 缓存监控 |

### 3.4 积分业务（后台管理端）接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET/... | `/points/goods/**` | 积分商品管理 |
| GET/... | `/points/category/**` | 商品分类管理 |
| GET/... | `/points/coupon/**` | 优惠券/兑换券管理（含发放） |
| GET/... | `/points/usercoupon/**` | 用户券管理 |
| GET/... | `/points/order/**` | 订单管理（含发货、关闭、导出） |
| GET/... | `/points/rule/**` | 积分规则管理 |
| GET/... | `/points/detail/**` | 积分明细（含统计、导出） |
| GET/... | `/points/h5user/**` | H5 用户管理（含调账、重置密码） |

---

## 四、H5 接口清单

以下为 H5 端已注册的接口路径，统一使用 `Authorization: Bearer <token>` 头（标注"公开"的接口除外）。H5 端同时接受 `Authorization: Bearer <token>` 与前端自定义头（双头携带，后端均能识别）。

### 4.1 H5 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/h5-api/auth/login` | 登录（手机号 + 密码 + 图形验证码） |
| POST | `/h5-api/auth/register` | 注册 |
| GET | `/h5-api/auth/config` | 前端配置（公开） |

### 4.2 H5 公开接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/h5-api/goods/list` | 商品列表（公开） |
| GET | `/h5-api/goods/categories` | 商品分类（公开） |
| GET | `/h5-api/goods/*` | 商品详情（公开） |

### 4.3 H5 用户中心接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/h5-api/user/info` | 用户信息 |
| PUT | `/h5-api/user/profile` | 修改资料 |
| PUT | `/h5-api/user/resetpwd` | 修改密码（需原密码） |
| GET | `/h5-api/user/points/detail` | 积分明细 |
| GET | `/h5-api/user/orders` | 我的订单 |
| GET | `/h5-api/user/orders/{id}` | 订单详情 |
| PUT | `/h5-api/user/orders/{id}/confirm` | 确认收货 |
| GET/POST/PUT/DELETE | `/h5-api/user/addresses**` | 收货地址管理 |

### 4.4 H5 业务接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST | `/h5-api/sign/**` | 签到 |
| GET/POST | `/h5-api/coupon/**` | 优惠券（领券中心、我的券、可用券、领取） |
| POST | `/h5-api/exchange` | 积分兑换/下单 |

---

## 五、接口文档（Swagger / 接口清单）

- 生产环境**已显式关闭** Swagger UI 与 OpenAPI（`/swagger-ui.html`、`/v3/api-docs` 均不可访问），**不提供**接口清单文档。
- 接口范围以本文档第三、四节为准。
