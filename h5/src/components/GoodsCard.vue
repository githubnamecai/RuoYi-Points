<template>
  <div class="goods-card" @click="$router.push('/product/' + goods.goodsId)">
    <div class="img-wrap">
      <img :src="formatImg(goods.coverImg)" alt="" />
      <div class="top-tags">
        <div v-if="goods.categoryName" class="tag category">{{ goods.categoryName }}</div>
        <div class="tag" :class="goods.goodsType === '1' ? 'virtual' : 'physical'">
          {{ goods.goodsType === '1' ? '数字权益' : '实物兑换' }}
        </div>
      </div>
      <div class="heat-pill">
        <van-icon name="fire-o" />
        <span>{{ goods.sales || 0 }}</span>
      </div>
    </div>
    <div class="info">
      <div class="title">{{ goods.goodsName }}</div>
      <div class="meta-row">
        <span class="metric">
          <van-icon name="inventory-o" />
          库存 {{ goods.stock ?? 0 }}
        </span>
        <span v-if="goods.limitPerUser > 0" class="metric accent">限兑 {{ goods.limitPerUser }}</span>
      </div>
      <div class="bottom">
        <!-- 实物商品：显示金额 + 优惠价 -->
        <template v-if="goods.goodsType === '0'">
          <span class="price cash">
            <span class="unit">¥</span>
            <span class="num">{{ goods.price || 0 }}</span>
          </span>
          <span v-if="goods.discountPrice" class="discount-tag">优惠价¥{{ goods.discountPrice }}</span>
        </template>
        <!-- 虚拟商品：显示积分 -->
        <template v-else>
          <span class="price">
            <span class="unit">积分</span>
            <span class="num">{{ goods.points }}</span>
          </span>
        </template>
        <van-icon name="shopping-cart-o" class="cart-icon" />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ goods: { type: Object, required: true } })

const defaultImg = 'https://via.placeholder.com/300x300?text=Goods'
const baseApi = import.meta.env.VITE_APP_BASE_API

/**
 * 格式化商品图片地址。
 */
function formatImg(url) {
  if (!url) return defaultImg
  if (url.startsWith('/profile')) {
    return baseApi + url
  }
  return url
}
</script>

<style scoped lang="scss">
.goods-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 251, 255, 0.88));
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid rgba(124, 147, 187, 0.12);
  box-shadow: 0 14px 28px rgba(22, 53, 110, 0.08);
  display: flex;
  flex-direction: column;
  width: 100%;
  min-width: 0;
  position: relative;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.goods-card:active {
  transform: scale(0.985);
  box-shadow: 0 10px 18px rgba(22, 53, 110, 0.1);
}

.img-wrap {
  position: relative;
  width: 100%;
  height: 172px;
  background: linear-gradient(160deg, #edf4ff 0%, #f7faff 48%, #eef6ff 100%);
  overflow: hidden;
  padding: 10px;
}

.img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.top-tags {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  max-width: calc(100% - 24px);
}

.tag {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.tag.category {
  background: rgba(255, 255, 255, 0.88);
  color: #36598b;
  border: 1px solid rgba(54, 89, 139, 0.08);
}

.tag.virtual {
  background: rgba(13, 91, 215, 0.12);
  color: #0d5bd7;
}

.tag.physical {
  background: rgba(242, 186, 104, 0.18);
  color: #9b6212;
}

.heat-pill {
  position: absolute;
  right: 12px;
  bottom: 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 9px;
  border-radius: 999px;
  background: rgba(18, 34, 61, 0.52);
  color: #fff;
  font-size: 11px;
  backdrop-filter: blur(8px);
}

.info {
  padding: 12px 12px 14px;
}

.title {
  font-size: 14px;
  font-weight: 600;
  line-height: 1.5;
  height: 42px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  color: #182237;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.metric {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(13, 91, 215, 0.08);
  color: #36609a;
  font-size: 11px;
}

.metric.accent {
  background: rgba(242, 186, 104, 0.14);
  color: #9b6212;
}

.bottom {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.discount-tag {
  font-size: 12px;
  color: #0d5bd7;
  background: rgba(13, 91, 215, 0.08);
  padding: 4px 8px;
  border-radius: 999px;
  line-height: 1.3;
  max-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price {
  color: #0d5bd7;
  display: flex;
  align-items: baseline;
}

.price.cash {
  color: #d85267;
}

.price.cash .num {
  font-size: 20px;
}

.price .unit {
  font-size: 12px;
  margin-right: 2px;
}

.price .num {
  font-size: 18px;
  font-weight: 700;
}

.cart-icon {
  flex-shrink: 0;
  font-size: 16px;
  color: #0d5bd7;
  border: 1px solid rgba(13, 91, 215, 0.12);
  border-radius: 50%;
  padding: 8px;
  background: rgba(13, 91, 215, 0.08);
}
</style>
