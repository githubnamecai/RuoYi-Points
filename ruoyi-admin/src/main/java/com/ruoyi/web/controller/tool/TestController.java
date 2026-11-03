// ============================================================================
// 【已停用】若依自带的 Swagger 演示控制器
//
// 停用原因：
//   1. 接口文档（Swagger UI / api-docs）已在 application.yml 中整体关闭，
//      本控制器原本仅用于接口文档演示，已无使用场景；
//   2. 类中内存写死了 admin/admin123、ry/admin123 等演示账号，属无用且敏感代码，
//      保留为可执行状态存在被误用的风险。
//
// 处理方式：整文件注释停用，代码原样保留便于查阅与回溯，不做物理删除。
// 恢复方式：去掉下方各行的行首注释符（// ），同时需要在 application.yml 中打开
//          springdoc.api-docs.enabled 与 springdoc.swagger-ui.enabled，
//          并恢复 SecurityConfig 中对 /v3/api-docs/**、/swagger-ui/** 的放行。
// ============================================================================

// package com.ruoyi.web.controller.tool;
//
// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import com.ruoyi.common.core.controller.BaseController;
// import com.ruoyi.common.core.domain.R;
// import com.ruoyi.common.utils.StringUtils;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.tags.Tag;
//
// /**
//  * swagger 用户测试方法
//  *
//  * @author ruoyi
//  */
// @Tag(name = "用户信息管理")
// @RestController
// @RequestMapping("/test/user")
// public class TestController extends BaseController
// {
//     private final static Map<Integer, UserEntity> users = new LinkedHashMap<Integer, UserEntity>();
//     {
//         users.put(1, new UserEntity(1, "admin", "admin123", "15888888888"));
//         users.put(2, new UserEntity(2, "ry", "admin123", "15666666666"));
//     }
//
//     @Operation(summary = "获取用户列表")
//     @GetMapping("/list")
//     public R<List<UserEntity>> userList()
//     {
//         List<UserEntity> userList = new ArrayList<UserEntity>(users.values());
//         return R.ok(userList);
//     }
//
//     @Operation(summary = "获取用户详细")
//     @GetMapping("/{userId}")
//     public R<UserEntity> getUser(@PathVariable(name = "userId")
//     Integer userId)
//     {
//         if (!users.isEmpty() && users.containsKey(userId))
//         {
//             return R.ok(users.get(userId));
//         }
//         else
//         {
//             return R.fail("用户不存在");
//         }
//     }
//
//     @Operation(summary = "新增用户")
//     @PostMapping("/save")
//     public R<String> save(UserEntity user)
//     {
//         if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
//         {
//             return R.fail("用户ID不能为空");
//         }
//         users.put(user.getUserId(), user);
//         return R.ok();
//     }
//
//     @Operation(summary = "更新用户")
//     @PutMapping("/update")
//     public R<String> update(@RequestBody
//     UserEntity user)
//     {
//         if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
//         {
//             return R.fail("用户ID不能为空");
//         }
//         if (users.isEmpty() || !users.containsKey(user.getUserId()))
//         {
//             return R.fail("用户不存在");
//         }
//         users.remove(user.getUserId());
//         users.put(user.getUserId(), user);
//         return R.ok();
//     }
//
//     @Operation(summary = "删除用户信息")
//     @DeleteMapping("/{userId}")
//     public R<String> delete(@PathVariable(name = "userId")
//     Integer userId)
//     {
//         if (!users.isEmpty() && users.containsKey(userId))
//         {
//             users.remove(userId);
//             return R.ok();
//         }
//         else
//         {
//             return R.fail("用户不存在");
//         }
//     }
// }
//
// @Schema(description = "用户实体")
// class UserEntity
// {
//     @Schema(title = "用户ID")
//     private Integer userId;
//
//     @Schema(title = "用户名称")
//     private String username;
//
//     @Schema(title = "用户密码")
//     private String password;
//
//     @Schema(title = "用户手机")
//     private String mobile;
//
//     public UserEntity()
//     {
//
//     }
//
//     public UserEntity(Integer userId, String username, String password, String mobile)
//     {
//         this.userId = userId;
//         this.username = username;
//         this.password = password;
//         this.mobile = mobile;
//     }
//
//     public Integer getUserId()
//     {
//         return userId;
//     }
//
//     public void setUserId(Integer userId)
//     {
//         this.userId = userId;
//     }
//
//     public String getUsername()
//     {
//         return username;
//     }
//
//     public void setUsername(String username)
//     {
//         this.username = username;
//     }
//
//     public String getPassword()
//     {
//         return password;
//     }
//
//     public void setPassword(String password)
//     {
//         this.password = password;
//     }
//
//     public String getMobile()
//     {
//         return mobile;
//     }
//
//     public void setMobile(String mobile)
//     {
//         this.mobile = mobile;
//     }
// }
