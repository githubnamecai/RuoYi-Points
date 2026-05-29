<template>
  <div class="goods-card" @click="$router.push('/product/' + goods.goodsId)">
    <div class="img-wrap">
      <img :src="goods.coverImg || defaultImg" alt="" />
      <div v-if="goods.goodsType === '1'" class="tag virtual">虚拟</div>
    </div>
    <div class="info">
      <div class="brand" v-if="goods.categoryName">{{ goods.categoryName }}</div>
      <div class="title">{{ goods.goodsName }}</div>
      <div class="bottom">
        <span class="price">
          <span class="unit">积分</span>
          <span class="num">{{ goods.points }}</span>
        </span>
        <van-icon name="shopping-cart-o" class="cart-icon" />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ goods: { type: Object, required: true } })
const defaultImg = 'https://via.placeholder.com/300x300?text=Goods'
</script>

<style scoped lang="scss">
.goods-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
}
.img-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  background: #f3f3f3;
}
.img-wrap img { width: 100%; height: 100%; object-fit: cover; display: block; }
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
.info { padding: 10px; }
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
  align-items: center;
}
.price { color: #f44336; display: flex; align-items: baseline; }
.price .unit { font-size: 12px; margin-right: 2px; }
.price .num { font-size: 16px; font-weight: bold; }
.cart-icon {
  font-size: 14px;
  color: #666;
  border: 1px solid #eee;
  border-radius: 50%;
  padding: 4px;
}
</style>
