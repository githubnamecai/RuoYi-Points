<template>
  <div class="activity-page">
    <van-nav-bar title="热门活动" left-arrow @click-left="$router.back()" />

    <div class="activity-list">
      <div class="activity-card" v-for="(item, index) in activities" :key="index">
        <div class="card-tag">{{ item.tag }}</div>
        <div class="card-content">{{ item.content }}</div>
      </div>
    </div>

    <div class="footer-tip">更多活动敬请期待...</div>
  </div>
</template>

<script setup>
defineOptions({ name: 'Activity' })

const activities = [
  {
    tag: '手机优惠',
    content: '东方有线用户，购买苹果手机，可领取1300元抵扣券。'
  },
  {
    tag: '宽带套餐',
    content: '东方有线用户，300M、500M繁花套餐，缴费两年享路由器一台。'
  },
  {
    tag: '夏日礼包',
    content: '东方有线用户到厅参与活动，领取夏日礼包一份（帆布袋+扇子）。'
  },
  {
    tag: '夏日礼包',
    content: '关注线上全部职人号，到厅领取夏日礼包一份（帆布袋+扇子+伞）。'
  },
  {
    tag: '缴费赠礼',
    content: '线上下单并完成核销，融合套餐缴费1年赠送2.5kg大米一份，预缴两年加赠防蚊被一份。'
  },
  {
    tag: '出行优惠',
    content: '东方有线用户，购买萌大圣、小森林品牌自行车，可领取200元抵扣券，平衡车100元抵扣券。'
  },
  {
    tag: '生活权益',
    content: '东方有线用户，万象杯7折权益。'
  },
  {
    tag: '美食权益',
    content: '东方有线用户，丁义兴8折权益。'
  },
  {
    tag: '美食权益',
    content: '东方有线用户，望秦啤酒85折权益。'
  }
]
</script>

<style scoped lang="scss">
.activity-page {
  min-height: 100vh;
  background: #f7f7f7;
}

.activity-list {
  padding: 16px 12px;
}

.activity-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-tag {
  display: inline-block;
  background: linear-gradient(135deg, #ff8c00, #ffb84d);
  color: #fff;
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 12px;
  margin-bottom: 10px;
}

.card-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.footer-tip {
  text-align: center;
  color: #ccc;
  font-size: 13px;
  padding: 20px 0 40px;
}
</style>
