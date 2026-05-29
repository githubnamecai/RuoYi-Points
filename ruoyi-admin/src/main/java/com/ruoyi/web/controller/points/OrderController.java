package com.ruoyi.web.controller.points;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.points.domain.Order;
import com.ruoyi.points.service.IOrderService;

/**
 * 兑换订单管理
 */
@RestController
@RequestMapping("/points/order")
public class OrderController extends BaseController
{
    @Autowired private IOrderService orderService;

    @PreAuthorize("@ss.hasPermi('points:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(Order order)
    {
        startPage();
        return getDataTable(orderService.selectOrderList(order));
    }

    @PreAuthorize("@ss.hasPermi('points:order:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(orderService.selectOrderById(orderId));
    }

    @PreAuthorize("@ss.hasPermi('points:order:ship')")
    @Log(title = "订单发货", businessType = BusinessType.UPDATE)
    @PutMapping("/ship")
    public AjaxResult ship(@RequestBody Order order)
    {
        return toAjax(orderService.shipOrder(order, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('points:order:edit')")
    @Log(title = "修改订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Order order)
    {
        return toAjax(orderService.updateOrder(order, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('points:order:close')")
    @Log(title = "关闭订单", businessType = BusinessType.UPDATE)
    @PutMapping("/close/{orderId}")
    public AjaxResult close(@PathVariable Long orderId, @RequestParam(required = false) String reason)
    {
        return toAjax(orderService.closeOrder(orderId, reason, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('points:order:export')")
    @Log(title = "订单导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Order order)
    {
        List<Order> list = orderService.selectOrderList(order);
        ExcelUtil<Order> util = new ExcelUtil<>(Order.class);
        util.exportExcel(response, list, "兑换订单");
    }
}
