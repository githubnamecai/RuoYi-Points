package com.ruoyi.points.mapper;

import com.ruoyi.points.domain.SignConfig;

public interface SignConfigMapper
{
    SignConfig selectConfig();
    int updateConfig(SignConfig config);
}
