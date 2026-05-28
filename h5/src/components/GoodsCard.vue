<template>
  <div class="goods-card" @click="$router.push('/product/' + goods.goodsId)">
    <div class="img-wrap">
      <img :src="goods.coverImg || defaultImg" alt="" />
      <div v-if="goods.goodsType === '1'" class="tag virtual">虚拟</div>
    </div>
    <div class="info">
      <div class="title">{{ goods.goodsName }}</div>
      <div class="bottom">
        <span class="points">
          <span class="num">{{ goods.points }}</span>
          <span class="unit">积分</span>
        </span>
        <span class="sales">已兑{{ goods.sales || 0 }}</span>
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
.info { padding: 8px 10px 10px; }
.title {
  font-size: 13px;
  line-height: 1.3;
  height: 34px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  color: #333;
}
.bottom {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.points .num { color: #ff8c00; font-size: 16px; font-weight: 700; }
.points .unit { color: #ff8c00; font-size: 11px; margin-left: 2px; }
.sales { color: #999; font-size: 11px; }
</style>
