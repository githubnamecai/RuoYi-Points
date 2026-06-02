

## 需求1

1、优化H5页面整体效果参考图片样式内容。

2、H5商城的全部切换到分类失败，并且页面返回商品全部时不显示商品，商品的分类是树形数据，直接查询分类是不清楚父子节点，需要修改后端接口和前端H5页面

3、H5的订单选择已完成后订单会有重复的订单数据

4、优化H5的页面对应的接口逻辑

## 答案1

1. 优化 H5 页面整体效果

- 首页布局重构 ：修改了 Index.vue 的布局，完全参考了设计图样式。顶部去除了原来的积分卡，新增了带圆角与渐变背景的搜索栏；在轮播图下方增加了 “金刚区” 图标导航（商品分类、每日签到、我的订单、积分明细、地址管理），采用了彩色圆形背景+Vant图标的样式，界面更加美观。
- 商品卡片样式升级 ：在 GoodsCard.vue 中，新增了品牌名/分类名显示，将积分数值和字体加粗标红，并在卡片右下角添加了细边框的“购物车”图标，全面贴合了设计图的商品双列瀑布流展示效果。
2. 修复 H5 树形分类查询及“全部”分类异常

- 后端接口调整 ：针对商品由于树形分类导致查不到子节点商品的问题，我修改了 GoodsMapper.xml 中的 selectGoodsList 和 selectH5GoodsList 查询语句。将原先的精准匹配改为了包含所有子级的递归查询条件：
- 前端逻辑修复 ：修复了 H5 页面切换回“全部”时列表空白的问题。同时优化了接口传参，保证 categoryId 为空时后端能够正确返回全部商品。
3. 修复 H5 订单列表重复数据问题

- 前端并发请求拦截 ：问题原因是当切换状态 Tab（例如切到“已完成”）时，代码清空了列表数据并手动调用了加载方法，但因为列表瞬间变空，Vant 的 <van-list> 组件内置机制判断当前数据未铺满屏幕，自动触发了额外的 load 事件，导致同一个页码被请求了两次。
- 解决方式 ：在 List.vue 的 onTabChange 和 onRefresh 事件中，在重置列表后显式加上 loading.value = true ，从而成功拦截 <van-list> 的多余调用，彻底解决了分页加载时的订单重复问题。
4. 优化 H5 页面对应的接口逻辑

- 前端补充了搜索关键词变量 keyword ，与后端的 goodsName 字段直接打通；

- 去除了前端路由由于意外跳转导致的异常加载流程，减少了后端的并发脏查询，使得整个 H5 的商品获取和订单读取的性能及逻辑更加稳定。

  ## 问题2

   **Vant `van-list` 组件在路由返回后不再触发 `load` 事件**的问题

- **场景一：没有使用 `keep-alive`** 组件被销毁重建，`onMounted` 里没有主动调用加载逻辑，`van-list` 初始状态 `loading=false`、`finished=false`，但由于数据为空列表高度为 0，`van-list` 的滚动检测可能未正确触发。

  **场景二：使用了 `keep-alive`** 组件被缓存，返回时不再执行 `onMounted`，数据状态保留在之前的状态（如 `finished=true` 或 `loading=true`），导致不再触发加载。

  ------

  ## 解决方案2

  ### 方案一：使用 `onActivated` 配合 `keep-alive`（推荐）

  在路由配置中使用 `keep-alive` 缓存商城页，并在 `onActivated` 中重新触发加载检测：

1、优化商品详情的图片显示和详情的图片显示逻辑
2、优化商品列表的图片显示
3、优化兑换确认的图片显示
4、优化订单的图片显示



我已经完成了你提到的各项优化与修复工作。以下是详细的修改内容说明：

1. H5 收货地址页面设为默认的修复

- 问题分析 ：由于在 List.vue 中使用了 Vant 4 的 <van-checkbox> ，原生绑定的 @change 配合事件冒泡会导致设为默认功能无法正常被触发。
- 解决方案 ：将 @change 替换为了正确的 @update:model-value 事件，并额外添加了 @click.stop 阻止点击事件向上冒泡，现在可以正常点击并勾选“设为默认”了。
2. H5 新增地址成功的提示消息

- 问题分析 ：在 Edit.vue 中，之前在请求结束后立即调用了 showToast ，但全局 Axios 请求拦截器在关闭 Loading 状态时（ closeToast() ）会把刚刚弹出的成功提示一起清除掉，导致用户看不见。
- 解决方案 ：通过使用 setTimeout 对成功提示做了一点点微小的延迟（100ms），避开了拦截器关闭弹窗的时机，现在新增或修改成功后都会正常弹出“新增成功”或“修改成功”。
3. H5 积分明细页面需手动刷新的问题

- 问题分析 ：原先的 Detail.vue 依赖 <van-list> 的初次渲染自动加载事件，且没有在 onMounted 钩子中强制重新拉取列表，导致如果从其他页面切回来可能会卡在空状态。
- 解决方案 ：修改了组件加载逻辑，在进入页面触发的 onMounted 中主动调用一次 loadMore() 并重新获取最新的积分余额，保证每次进页面都会自动刷新最新数据。
4. H5 兑换商品未选择地址的拦截弹窗

- 问题分析 ：在 Exchange.vue 中，原先的逻辑是直接禁用了“确认兑换”按钮，导致点击无效，没有交互反馈。
- 解决方案 ：放开了按钮的禁用限制，在点击提交时，如果是实物商品且没有选择地址，则会触发 showDialog 弹出“实物商品需选择收货地址，请先选择地址”的提示框，并提供“去选择”按钮直接跳转到地址选择页面。
5. PC 后台订单管理增加修改功能

- 前端调整 ：在 index.vue 列表中添加了“修改”按钮和相应的弹窗表单（支持修改收货人、联系电话、收货地址、物流公司和物流单号）。
- 后端调整 ：在 IOrderService.java 及对应的实现类中补充了 updateOrder 接口，并在 OrderController 中暴露了 PUT 修改端点。
6. PC 后台 H5 用户 CRUD 及密码登录

- 数据库及实体层 ：修改了 points_platform.sql ，在 t_user 表中增加了 password 字段，并同步更新了 H5User.java 和对应的 H5UserMapper.xml 保证密码能够落库。
- 后端业务逻辑 ：在 H5LoginServiceImpl 中新增了 loginByPassword 密码登录方法，补充了用户的添加、修改和删除逻辑。
- PC 前端 ：在 H5 用户管理列表 index.vue 中新增了“新增”、“修改”和“删除”操作，允许管理员直接设置和管理用户的密码。
- H5 前端 ：在 H5 的 Login.vue 中引入了顶部 Tabs 切换，实现了“短信登录”与“密码登录”的双轨验证模式。
- 1、H5选择收获地址页面上，不能勾选设为默认
  2、H5新增成功地址后没有提示消息
  3、H5我的页面上点击明细，在积分明细页面上还需要再次刷新才能看到积分的记录
  4、H5兑换设置如果没有设置选择地址，及时积分能兑换也不能兑换，弹出一个提示框
  5、后台订单关修改按钮操作，可以修改订单的信息
  6、新增后台H5用户页面可以增删改查，新增用户的密码字段，优化sql语句，H5登录页面能够切换账号密码进行登录
  7、修改菜单管理对应订单修改、H5用户新增、修改和删除的权限设置
  8、订单管理的修改新增状态、原因、备注字段
  9、商品管理新增用户的deptid字段和parientid字段
  10、H5用户可以修改密码
  11、H5用户管理新增姓名字段

# 优惠券功能需求


好的，关于新增优惠券功能，我为您整理了以下需求方案和数据库设计草案。由于该功能涉及 PC 管理端、H5 用户端以及前后端的交互，我们需要先对表结构和业务逻辑进行梳理。

### 一、 核心需求分析

1.  **字段与权限要求**：
    *   需要有 `dept_id`（部门ID）和 `parent_id`（父级ID），以便适应 RuoYi 的数据权限控制（例如按部门隔离优惠券的管理权限）。
    *   商品范围控制：支持“指定商品”或“全部商品”。配置为“全部商品”时，需要后台权限校验（例如仅超级管理员可以创建全场通用的优惠券）。
2.  **时效性配置**：
    *   优惠券需要支持时效性，通常有两种模式：
        *   绝对时间：例如 `2024-01-01` 至 `2024-12-31` 有效。
        *   相对时间：例如“领取后 N 天内有效”。
3.  **发放与领取机制**：
    *   H5 用户主动领取：在 H5 的领券中心或商品详情页点击领取。
    *   后台主动发放：管理员在后台选择特定的 H5 用户，定向发放优惠券。

---

### 二、 数据库表设计 (SQL 规划)

我们需要新建三张表：`优惠券主表`、`优惠券-商品关联表`（用于指定商品范围）、`用户领券记录表`。

```sql
-- 1. 优惠券主表
DROP TABLE IF EXISTS `t_coupon`;
CREATE TABLE `t_coupon` (
    `coupon_id`      bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `dept_id`        bigint(20)      DEFAULT NULL COMMENT '部门ID（用于数据权限）',
    `parent_id`      bigint(20)      DEFAULT 0 COMMENT '父级ID（用于层级管理，无则为0）',
    `coupon_name`    varchar(128)    NOT NULL COMMENT '优惠券名称',
    `coupon_type`    char(1)         DEFAULT '0' COMMENT '类型（0满减券 1折扣券 2无门槛券）',
    `min_amount`     int(11)         DEFAULT 0 COMMENT '使用门槛（满X积分可用，0为无门槛）',
    `discount_value` int(11)         NOT NULL COMMENT '优惠面值（抵扣积分或折扣率）',
    `total_count`    int(11)         DEFAULT -1 COMMENT '发行总量（-1为不限量）',
    `issued_count`   int(11)         DEFAULT 0 COMMENT '已发放数量',
    `limit_per_user` int(11)         DEFAULT 1 COMMENT '每人限领数量',
    `use_type`       char(1)         DEFAULT '0' COMMENT '适用范围（0全部商品 1指定商品分类 2指定商品）',
    `time_type`      char(1)         DEFAULT '0' COMMENT '有效期类型（0绝对时间 1相对天数）',
    `start_time`     datetime        DEFAULT NULL COMMENT '有效期开始时间（绝对时间用）',
    `end_time`       datetime        DEFAULT NULL COMMENT '有效期结束时间（绝对时间用）',
    `valid_days`     int(11)         DEFAULT NULL COMMENT '领取后有效天数（相对天数用）',
    `status`         char(1)         DEFAULT '0' COMMENT '状态（0正常 1停用 2过期）',
    `del_flag`       char(1)         DEFAULT '0' COMMENT '删除标志',
    `create_by`      varchar(64)     DEFAULT '' COMMENT '创建者',
    `create_time`    datetime        DEFAULT NULL COMMENT '创建时间',
    `update_by`      varchar(64)     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime        DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(500)    DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券主表';

-- 2. 优惠券-商品/分类 关联表 (当 use_type 为 1 或 2 时使用)
DROP TABLE IF EXISTS `t_coupon_goods`;
CREATE TABLE `t_coupon_goods` (
    `id`             bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `coupon_id`      bigint(20)      NOT NULL COMMENT '优惠券ID',
    `ref_id`         bigint(20)      NOT NULL COMMENT '关联的商品ID或分类ID',
    PRIMARY KEY (`id`),
    KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券-商品关联表';

-- 3. 用户领券记录表 (优惠券明细)
DROP TABLE IF EXISTS `t_user_coupon`;
CREATE TABLE `t_user_coupon` (
    `user_coupon_id` bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '领券记录ID',
    `user_id`        bigint(20)      NOT NULL COMMENT 'H5用户ID',
    `coupon_id`      bigint(20)      NOT NULL COMMENT '优惠券ID',
    `receive_type`   char(1)         DEFAULT '0' COMMENT '获取方式（0主动领取 1后台发放）',
    `status`         char(1)         DEFAULT '0' COMMENT '使用状态（0未使用 1已使用 2已过期）',
    `order_id`       bigint(20)      DEFAULT NULL COMMENT '使用的订单ID',
    `start_time`     datetime        NOT NULL COMMENT '生效时间',
    `end_time`       datetime        NOT NULL COMMENT '过期时间',
    `use_time`       datetime        DEFAULT NULL COMMENT '使用时间',
    `create_time`    datetime        DEFAULT NULL COMMENT '领取时间',
    PRIMARY KEY (`user_coupon_id`),
    KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券记录表';
```

---

### 三、 业务功能规划

#### 1. PC 后台端 (ruoyi-ui + Java 后端)
*   **优惠券管理模块**：
    *   **列表查询**：支持按名称、状态、创建时间查询。数据需根据 `dept_id` 进行数据权限过滤（仅能看本部门及下属部门创建的券）。
    *   **新增/编辑**：
        *   配置名称、类型、门槛、面值、发行量等。
        *   配置时效性（绝对时间区间 或 领取后有效天数）。
        *   配置适用范围。**【权限控制点】**：前端与后端拦截，当选择“全部商品”时，判断当前登录用户是否为超级管理员（`SecurityUtils.isAdmin()`），如果不是则提示无权限。
        *   若是指定商品，则弹出商品选择器（穿梭框或表格勾选），并将结果存入 `t_coupon_goods` 表。
*   **发放与记录模块**：
    *   **定向发放**：在优惠券列表点击“发放”，弹出 H5 用户列表弹窗，勾选用户后批量生成 `t_user_coupon` 记录（标记为后台发放）。
    *   **领取/使用记录**：查看某张优惠券被哪些用户领取了，当前的使用状态（未使用/已使用/已过期）。

#### 2. H5 用户端 (Vite + Vue3 + Vant4)
*   **领券中心 / 首页展示**：
    *   展示当前处于发放状态且未达到领取上限的优惠券列表。
    *   用户点击领取，后端校验库存及每人限领数量，写入 `t_user_coupon`。
*   **我的优惠券页面**：
    *   在“我的”页面新增“我的优惠券”入口。
    *   分 Tab 展示：未使用、已使用、已过期。
*   **下单兑换流程改造 (核心)**：
    *   在 [Exchange.vue](file:///d:/Code/RuoYi-Points/h5/src/views/product/Exchange.vue) 确认兑换页面，新增“优惠券”选择栏。
    *   加载该用户拥有的、状态为“未使用”、且满足当前商品范围与金额门槛的优惠券列表。
    *   选择优惠券后，动态重新计算 `totalPoints`（如：原价 1000 积分 - 200 积分券 = 实扣 800 积分）。
    *   提交兑换时，将 `user_coupon_id` 传给后端，后端校验优惠券合法性，扣减积分，同时将优惠券状态更新为“已使用”并关联 `order_id`。

---

您可以先看看以上的设计思路是否符合您的期望？
重点确认：
1. **表结构设计**（尤其是时效性控制和指定商品的设计）
2. **"全部商品"的超管权限控制逻辑**
3. **前端的 H5 领券和下单抵扣逻辑**

如果没问题，您可以回复 **“确认”**，我将为您生成相应的 Java 后端代码、MyBatis XML、RuoYi-UI 页面以及 H5 页面的完整端的改造方案以及 H5 的修改代码。

# 优惠券功能已完成

本次新增功能的核心要点：

### 1. 数据库改造 (points_platform.sql)
- 新增了 3张核心表 ：
  - t_coupon （优惠券主表）：包含 dept_id （用于部门数据权限）、 parent_id ，以及门槛、面值、适用范围（全场/分类/指定商品）、时效性等字段。
  - t_coupon_goods （商品关联表）：用于存放优惠券关联的指定分类ID或指定商品ID。
  - t_user_coupon （用户领券记录表）：记录H5用户领取或后台发放的优惠券状态及使用记录。
- 自动写入了后台菜单数据（优惠券管理、查询、新增、修改、删除、发放）及字典数据（优惠券类型、适用范围、有效期类型等）。
### 2. 后端核心逻辑开发
- PC端接口 ( CouponController.java )：
  - 提供了标准的 CRUD 接口，其中在 CouponServiceImpl 新增了 超管权限校验 ：若创建“全部商品”适用的优惠券，会通过 SecurityUtils.isAdmin() 强制校验只有超级管理员才可创建。
  - 列表查询加入了 @DataScope(deptAlias = "c") 部门数据权限过滤。
  - 增加了 /issue/{couponId} 接口，供后台管理员定向给指定 H5 用户下发优惠券。
- H5端接口 ( H5CouponController.java )：
  - 提供了领券中心列表、主动领取、我的优惠券列表接口。
  - 核心兑换改造 ( OrderServiceImpl.java )：
    - 下单前提供 /available 接口查询当前商品可用的优惠券；
    - 在兑换下单方法中增加了对优惠券的校验（门槛、适用范围、有效期），并在扣减积分前先算出抵扣后的最终积分，下单成功后更新优惠券为“已使用”状态；如果订单关闭退款，同时也会把优惠券退回。
### 3. PC 管理端开发 (ruoyi-ui)
- 新增了 views/points/coupon/index.vue 优惠券管理页面。
- 实现了对优惠券面值、发放量、每人限领的配置。
- 支持动态切换有效期类型（绝对时间/相对天数）及适用范围；如果是指定商品或分类，可以输入关联的ID。
- 列表页提供了“发放”按钮，可输入 H5 用户的 ID 批量下发。
### 4. H5 用户端开发 (h5/)
- 路由与入口 ：在 Home/Index.vue 金刚区加入了“领券中心”入口，在 User/Index.vue 加入了“我的优惠券”入口，并在 router/index.js 中注册了路由。
- 领券中心 ( Center.vue )：以双色卡片形式展示可领取的优惠券，点击即可一键领取。
- 我的优惠券 ( My.vue )：分为“未使用、已使用、已过期”三个 Tab 展示用户的优惠券资产。
- 下单抵扣 ( Exchange.vue )：在兑换确认页底部新增了优惠券选择栏。进入页面会自动拉取可用优惠券并默认选中抵扣额度最优的一张；用户也可点击打开弹窗手动切换不使用或其他优惠券，页面底部的合计积分会实时动态更新。
您可以重启一下后端服务并刷新页面，体验完整的发券、领券以及下单抵扣的流程了！