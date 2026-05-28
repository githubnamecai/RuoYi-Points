package com.ruoyi.web.controller.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.points.domain.dto.ExchangeDTO;
import com.ruoyi.points.service.IOrderService;

@RestController
@RequestMapping("/h5-api/exchange")
public class H5ExchangeController extends H5BaseController
{
    @Autowired private IOrderService orderService;

    /** 兑换商品（核心：分布式锁 + 库存 + 积分扣减） */
    @PostMapping
    public AjaxResult exchange(@Validated @RequestBody ExchangeDTO dto)
    {
        return success(orderService.exchange(currentUserId(), dto));
    }
}
