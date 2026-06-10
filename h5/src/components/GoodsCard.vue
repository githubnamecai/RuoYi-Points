<template>
  <div class="goods-card" @click="$router.push('/product/' + goods.goodsId)">
    <div class="img-wrap">
      <img :src="formatImg(goods.coverImg)" alt="" />
      <div v-if="goods.goodsType === '1'" class="tag virtual">虚拟</div>
    </div>
    <div class="info">
      <div class="brand" v-if="goods.categoryName">{{ goods.categoryName }}</div>
      <div class="title">{{ goods.goodsName }}</div>
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
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  width: 100%;
  min-width: 0;
}

.img-wrap {
  position: relative;
  width: 100%;
  height: 160px;
  background: #f3f3f3;
  overflow: hidden;
}

.img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.tag.virtual {
  position: absolute;
  top: 6px;
  left: 6px;
  background: #ff8c00;
  color: #fff;
  font-size: 10px;
  padding: 1px 5px;
  border-radius: 4px;
}

.info {
  padding: 10px;
}

.brand {
  font-size: 12px;
  font-weight: bold;
  color: #333;
  margin-bottom: 2px;
}

.title {
  font-size: 13px;
  line-height: 1.4;
  height: 36px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  color: #666;
}

.bottom {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.discount-tag {
  font-size: 12px;
  color: #ff8c00;
  background: #fff3e0;
  padding: 1px 4px;
  border-radius: 3px;
  line-height: 1.3;
  max-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price {
  color: #f44336;
  display: flex;
  align-items: baseline;
}

.price.cash {
  color: #e53935;
}

.price.cash .num {
  font-size: 18px;
}

.price .unit {
  font-size: 12px;
  margin-right: 2px;
}

.price .num {
  font-size: 16px;
  font-weight: bold;
}

.cart-icon {
  font-size: 14px;
  color: #666;
  border: 1px solid #eee;
  border-radius: 50%;
  padding: 4px;
}
</style>
