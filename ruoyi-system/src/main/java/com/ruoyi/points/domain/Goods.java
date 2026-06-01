package com.ruoyi.points.domain;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 商品对象 t_goods
 *
 * @author ruoyi-points
 */
public class Goods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long goodsId;

    /** 分类ID */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /** 分类名称（冗余展示） */
    @Excel(name = "分类")
    private String categoryName;

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    @Excel(name = "商品名称")
    private String goodsName;

    /** 商品类型 0实物 1虚拟 */
    @Excel(name = "类型", readConverterExp = "0=实物,1=虚拟")
    private String goodsType;
    private Long deptId;

    private Long parentId;


    /** 封面图 */
    private String coverImg;

    /** 轮播图 JSON 数组字符串 */
    private String images;

    /** 兑换所需积分 */
    @NotNull(message = "积分不能为空")
    @Excel(name = "积分")
    private Integer points;

    /** 原价 */
    @Excel(name = "原价")
    private BigDecimal originalPrice;

    /** 库存 */
    @Excel(name = "库存")
    private Integer stock;

    /** 已售数量 */
    @Excel(name = "销量")
    private Integer sales;

    /** 每人限兑数量(0不限制) */
    private Integer limitPerUser;

    /** 富文本描述 */
    private String description;

    /** 上架状态 */
    @Excel(name = "状态", readConverterExp = "0=下架,1=上架")
    private String status;

    /** 排序 */
    private Integer sort;

    /** 删除标志 */
    private String delFlag;

    /** 乐观锁版本 */
    private Integer version;

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public String getGoodsType() { return goodsType; }
    public void setGoodsType(String goodsType) { this.goodsType = goodsType; }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCoverImg() { return coverImg; }
    public void setCoverImg(String coverImg) { this.coverImg = coverImg; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }
    public Integer getLimitPerUser() { return limitPerUser; }
    public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
