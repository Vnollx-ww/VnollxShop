<template>
  <div class="page">
    <div class="flash-sale-header">
      <h1 class="title">🔥 秒杀专区</h1>
      <p class="subtitle">每天 12:00-14:00 限时特价，抢购从速！</p>
    </div>

    <!-- 倒计时区域 -->
    <div class="countdown-section">
      <div class="countdown-card">
        <div class="countdown-title">
          {{ isFlashSaleActive ? '距离活动结束还有' : '距离活动开始还有' }}
        </div>
        <div class="countdown-timer">
          <div class="time-unit">
            <span class="time-value">{{ countdown.hours }}</span>
            <span class="time-label">时</span>
          </div>
          <div class="time-separator">:</div>
          <div class="time-unit">
            <span class="time-value">{{ countdown.minutes }}</span>
            <span class="time-label">分</span>
          </div>
          <div class="time-separator">:</div>
          <div class="time-unit">
            <span class="time-value">{{ countdown.seconds }}</span>
            <span class="time-label">秒</span>
          </div>
        </div>
        <div class="flash-sale-status">
          {{ isFlashSaleActive ? '🔥 秒杀进行中 (12:00-14:00)' : '⏰ 即将开始 (12:00-14:00)' }}
        </div>
      </div>
    </div>

    <!-- 秒杀商品列表 -->
    <div class="flash-sale-products">
      <div class="section-title">热门秒杀商品</div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <i class="el-icon-loading loading-icon"></i>
        <p class="loading-text">正在加载秒杀商品...</p>
      </div>
      
      <!-- 商品网格 -->
      <div v-else class="products-grid">
        <div 
          v-for="product in flashSaleProducts" 
          :key="product.id" 
          class="product-card"
          :class="{ 'disabled': !isFlashSaleActive }"
          @click="handleProductClick(product.id)"
        >
          <div class="product-image">
            <img :src="product.cover || defaultImage" :alt="product.name" />
            <div class="flash-sale-badge">秒杀</div>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="price-section">
              <span class="flash-price">¥{{ product.price }}</span>
              <span class="original-price">¥{{ product.originalPrice || (product.price * 1.2).toFixed(0) }}</span>
              <span class="discount">{{ Math.round((product.price / (product.originalPrice || product.price * 1.2)) * 10) }}折</span>
            </div>
            <div class="progress-section">
              <div class="progress-label">库存 {{ product.stock }} 件</div>
              <el-progress 
                :percentage="Math.min((product.sales / (product.stock + product.sales)) * 100, 100)" 
                :show-text="false"
                stroke-width="6"
                color="#1890ff"
              />
            </div>
            <el-button 
              type="primary" 
              size="small" 
              class="flash-buy-btn"
              :disabled="!isFlashSaleActive || product.stock <= 0"
              @click.stop="buyNow(product)"
            >
              {{ !isFlashSaleActive ? '活动未开始' : (product.stock <= 0 ? '已抢完' : '立即抢购') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="flashSaleProducts.length === 0" class="empty-state">
      <i class="el-icon-goods empty-icon"></i>
      <p class="empty-text">暂无秒杀商品</p>
    </div>
  </div>
</template>

<script>
import { getFlashSaleProducts } from "@/api/product";

export default {
  name: 'FlashSale',
  data() {
    return {
      countdown: {
        hours: 0,
        minutes: 0,
        seconds: 0
      },
      flashSaleProducts: [],
      defaultImage: 'https://via.placeholder.com/300x300?text=商品图片',
      countdownTimer: null,
      flashSaleStartTime: null,
      isFlashSaleActive: false,
      loading: true
    }
  },
  async mounted() {
    await this.loadFlashSaleProducts()
    this.calculateFlashSaleTime()
    this.startCountdown()
  },
  beforeDestroy() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  },
  methods: {
    async loadFlashSaleProducts() {
      try {
        this.loading = true
        const { data } = await getFlashSaleProducts()
        this.flashSaleProducts = data || []
      } catch (error) {
        console.error('加载秒杀商品失败:', error)
        this.$message.error('加载秒杀商品失败')
        this.flashSaleProducts = []
      } finally {
        this.loading = false
      }
    },
    calculateFlashSaleTime() {
      const now = new Date()
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
      
      // 设置今天中午12点（活动开始时间）
      const todayNoon = new Date(today.getTime() + 12 * 60 * 60 * 1000)
      // 设置今天下午2点（活动结束时间）
      const today2PM = new Date(today.getTime() + 23 * 60 * 60 * 1000)
      
      if (now < todayNoon) {
        // 还没到12点，显示距离开始时间
        this.flashSaleStartTime = todayNoon
        this.isFlashSaleActive = false
      } else if (now >= todayNoon && now < today2PM) {
        // 12点到14点之间，活动进行中，显示距离结束时间
        this.flashSaleStartTime = today2PM
        this.isFlashSaleActive = true
      } else {
        // 已经过了14点，显示距离明天12点开始时间
        this.flashSaleStartTime = new Date(todayNoon.getTime() + 24 * 60 * 60 * 1000)
        this.isFlashSaleActive = false
      }
    },
    startCountdown() {
      this.countdownTimer = setInterval(() => {
        const now = new Date().getTime()
        const distance = this.flashSaleStartTime.getTime() - now
        
        if (distance < 0) {
          // 时间到了，重新计算时间状态
          this.calculateFlashSaleTime()
          return
        }
        
        this.countdown.hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
        this.countdown.minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60))
        this.countdown.seconds = Math.floor((distance % (1000 * 60)) / 1000)
      }, 1000)
    },
    handleProductClick(productId) {
      if (!this.isFlashSaleActive) {
        this.$message.warning('秒杀活动尚未开始或已结束，无法查看商品详情')
        return
      }
      this.goToProductDetail(productId)
    },
    goToProductDetail(productId) {
      this.$router.push(`/product/${productId}`)
    },
    buyNow(product) {
      if (!this.isFlashSaleActive) {
        this.$message.warning('秒杀活动尚未开始！')
        return
      }
      
      if (product.stock <= 0) {
        this.$message.warning('商品已抢完！')
        return
      }
      
      // 跳转到商品详情页面进行购买
      this.$message.success(`正在跳转到 ${product.name} 的购买页面...`)
      this.goToProductDetail(product.id)
    }
  }
}
</script>

<style scoped>
.flash-sale-header {
  text-align: center;
  margin-bottom: 24px;
}

.title {
  font-size: 32px;
  font-weight: bold;
  color: #1890ff;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.countdown-section {
  margin-bottom: 32px;
}

.countdown-card {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: white;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.3);
}

.countdown-title {
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: 500;
}

.countdown-timer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}

.time-unit {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.time-value {
  font-size: 36px;
  font-weight: bold;
  line-height: 1;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 12px;
  border-radius: 8px;
  min-width: 60px;
}

.time-label {
  font-size: 14px;
  margin-top: 4px;
  opacity: 0.9;
}

.time-separator {
  font-size: 24px;
  font-weight: bold;
  opacity: 0.8;
}

.flash-sale-status {
  font-size: 16px;
  font-weight: bold;
  margin-top: 12px;
  opacity: 0.9;
}

.flash-sale-products {
  margin-bottom: 32px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #1f3b57;
  margin-bottom: 20px;
  text-align: center;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover:not(.disabled) {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.product-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

.product-card.disabled .product-image img {
  filter: grayscale(50%);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.flash-sale-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: #1890ff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f3b57;
  margin: 0 0 12px 0;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.flash-price {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.discount {
  background: #1890ff;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.progress-section {
  margin-bottom: 16px;
}

.progress-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
}

.flash-buy-btn {
  width: 100%;
  font-weight: bold;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  color: #ddd;
}

.empty-text {
  font-size: 16px;
  margin: 0;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.loading-icon {
  font-size: 32px;
  margin-bottom: 16px;
  color: #1890ff;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 16px;
  margin: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
  }
  
  .countdown-timer {
    gap: 12px;
  }
  
  .time-value {
    font-size: 28px;
    min-width: 50px;
  }
  
  .title {
    font-size: 24px;
  }
}
</style>
