<template>
  <div class="activity-page">
    <van-nav-bar title="热门活动" left-arrow @click-left="$router.back()" />
    <div class="hero-panel">
      <div class="hero-title">热门活动精选</div>
      <div class="hero-subtitle">到厅福利、生活权益与套餐优惠集中呈现</div>
    </div>

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
  background: transparent;
}

.hero-panel {
  margin: 12px 12px 0;
  padding: 22px 20px;
  border-radius: 24px;
  background: linear-gradient(135deg, #0c4ead 0%, #1765e3 52%, #57b9ff 100%);
  box-shadow: 0 20px 38px rgba(13, 91, 215, 0.2);
  color: #fff;
}

.hero-title {
  font-size: 22px;
  font-weight: 700;
}

.hero-subtitle {
  margin-top: 8px;
  font-size: 13px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.84);
}

.activity-list {
  padding: 16px 12px;
}

.activity-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(249, 251, 255, 0.82));
  border-radius: 22px;
  padding: 18px 16px;
  margin-bottom: 14px;
  box-shadow: 0 16px 30px rgba(22, 53, 110, 0.08);
  border: 1px solid rgba(124, 147, 187, 0.12);
}

.card-tag {
  display: inline-block;
  background: linear-gradient(135deg, #123f96, #1f73ef 70%, #58b9ff);
  color: #fff;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 999px;
  margin-bottom: 12px;
  font-weight: 600;
}

.card-content {
  font-size: 14px;
  color: #1f2c45;
  line-height: 1.8;
}

.footer-tip {
  text-align: center;
  color: #9ea7b8;
  font-size: 13px;
  padding: 20px 0 40px;
}
</style>
