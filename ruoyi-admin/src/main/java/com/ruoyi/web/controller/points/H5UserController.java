package com.ruoyi.web.controller.points;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.dto.PointsAdjustDTO;
import com.ruoyi.points.service.IH5UserService;

@RestController
@RequestMapping("/points/h5user")
public class H5UserController extends BaseController
{
    @Autowired private IH5UserService h5UserService;

    @PreAuthorize("@ss.hasPermi('points:h5user:list')")
    @GetMapping("/list")
    public TableDataInfo list(H5User user)
    {
        startPage();
        return getDataTable(h5UserService.selectH5UserList(user));
    }

    @PreAuthorize("@ss.hasPermi('points:h5user:query')")
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable Long userId)
    {
        return success(h5UserService.selectUserById(userId));
    }

    @PreAuthorize("@ss.hasPermi('points:h5user:add')")
    @Log(title = "H5用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody H5User user)
    {
        return toAjax(h5UserService.insertUser(user));
    }

    @PreAuthorize("@ss.hasPermi('points:h5user:edit')")
    @Log(title = "H5用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody H5User user)
    {
        return toAjax(h5UserService.updateUser(user));
    }

    @PreAuthorize("@ss.hasPermi('points:h5user:remove')")
    @Log(title = "H5用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(h5UserService.deleteUserByIds(userIds));
    }

    @PreAuthorize("@ss.hasPermi('points:h5user:freeze')")
    @Log(title = "H5用户状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{userId}/{status}")
    public AjaxResult changeStatus(@PathVariable Long userId, @PathVariable String status)
    {
        return toAjax(h5UserService.changeStatus(userId, status));
    }

    @PreAuthorize("@ss.hasPermi('points:h5user:adjust')")
    @Log(title = "H5用户积分调整", businessType = BusinessType.UPDATE)
    @PostMapping("/adjust")
    public AjaxResult adjust(@Validated @RequestBody PointsAdjustDTO dto)
    {
        return toAjax(h5UserService.adjustPoints(dto, getUsername()));
    }
}
