<template>
  <div class="page-container" v-if="detail">
    <!-- 商品详情卡片 -->
    <div class="product-card">
      <!-- 左侧图片区域 -->
      <div class="product-image-section">
        <div class="image-container">
          <img
              :src="detail.cover"
              :alt="detail.name"
              class="product-cover"
          />
        </div>
      </div>

      <!-- 右侧信息区域 -->
      <div class="product-info-section">
        <div class="product-header">
          <h1 class="product-title">{{ detail.name }}</h1>
          <p class="product-subtitle">{{ detail.introduce }}</p>

          <!-- 价格信息 -->
          <div class="price-section">
            <span class="price-label">售价：</span>
            <span class="price-value">¥ {{ detail.price }}</span>
          </div>
        </div>

        <!-- 详细信息表格：点赞数会实时更新 -->
        <div class="details-table">
          <div class="detail-row">
            <span class="detail-label">库存</span>
            <span class="detail-value">{{ detail.stock }} 件</span>
            <span class="detail-label">分类</span>
            <span class="detail-value">{{ detail.category }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">品牌</span>
            <span class="detail-value">{{ detail.brand }}</span>
            <span class="detail-label">销量</span>
            <span class="detail-value">{{ detail.sales }} 件</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">评分</span>
            <span class="detail-value">{{ detail.score }}（{{ detail.scoreCount }}人）</span>
            <span class="detail-label">浏览</span>
            <span class="detail-value">{{ detail.visitCount }} 次</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">点赞</span>
            <!-- 这里的 likeCount 会实时变化 -->
            <span class="detail-value">{{ detail.likeCount }} 次</span>
          </div>
        </div>

        <!-- 操作区域 -->
        <div class="action-area">
          <div class="quantity-selector">
            <span class="quantity-label">购买数量：</span>
            <el-input-number
                v-model="buyNum"
                :min="1"
                :max="detail.stock"
                class="quantity-input"
            />
          </div>

          <div class="button-group">
            <el-button
                type="primary"
                @click="addToCart(detail)"
                class="cart-button"
            >
              加入购物车
            </el-button>
            <el-button
                :type="detail.isLike ? 'danger' : 'info'"
                @click="toggleLike(detail)"
                class="like-button"
            >
              {{ detail.isLike ? '取消点赞' : '点赞' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductInfo, addLike, cancelLike } from "@/api/product";
import { addCartItem, getCartCount } from "@/api/cart";
export default {
  name: 'ProductDetail',
  data() {
    return {
      detail: null,
      buyNum: 1
    }
  },
  async created() {
    await this.fetch()
  },
  methods: {
    async fetch() {
      const pid = this.$route.params.id
      const { data } = await getProductInfo(pid)
      this.detail = data || null
    },
    async toggleLike(item) {
      try {
        if (item.isLike) {
          // 1. 取消点赞：调用接口成功后，点赞数-1
          await cancelLike(item.id)
          item.isLike = false
          item.likeCount -= 1  // 核心：前端实时减少点赞数
          this.$message.success('已取消点赞')
        } else {
          // 2. 点赞：调用接口成功后，点赞数+1
          await addLike(item.id)
          item.isLike = true
          item.likeCount += 1  // 核心：前端实时增加点赞数
          this.$message.success('已点赞')
        }
      } catch (e) {
        // 接口失败时回滚状态（避免前端显示与后端不一致）
        if (item.isLike) {
          item.isLike = true  // 若取消点赞失败，恢复“已点赞”状态
        } else {
          item.isLike = false // 若点赞失败，恢复“未点赞”状态
        }
        this.$message.error('操作失败，请先登录或稍后再试')
      }
    },
    async addToCart(item) {
      try {
        // 确保购买数量是有效的整数
        const buyNumInt = parseInt(this.buyNum) || 1
        
        // 准备添加到购物车的数据
        const cartData = {
          pid: item.id,
          productName: item.name,
          unitPrice: item.price,
          number: buyNumInt,
          cover: item.cover
        }
        
        // 调用API将商品添加到购物车数据库
        await addCartItem(cartData)
        
        // 获取最新的购物车数量
        const { data: count } = await getCartCount()
        
        // 触发全局购物车计数更新事件
        window.dispatchEvent(new CustomEvent('cart-count-change', { detail: { count } }))
        
        this.$message.success(`已将商品加入购物车`)
      } catch (e) {
        console.error('加入购物车失败', e)
        this.$message.error('加入购物车失败，请先登录或稍后再试')
      }
    }
  }
}
</script>

<style scoped>
/* 样式部分无修改，保持原样 */
.page-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.product-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.product-image-section {
  flex: 0 0 40%;
  padding: 20px;
  background-color: #f9fbfd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-container {
  border: 1px solid #e6f4ff;
  border-radius: 4px;
  padding: 15px;
  background-color: white;
}

.product-cover {
  max-width: 100%;
  max-height: 400px;
  height: auto;
  border-radius: 4px;
}

.product-info-section {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

.product-header {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e6f4ff;
}

.product-title {
  margin: 0 0 10px 0;
  color: #1f3b57;
  font-size: 24px;
  line-height: 1.3;
}

.product-subtitle {
  color: #6b87a1;
  margin: 0 0 20px 0;
  line-height: 1.6;
  font-size: 14px;
}

.price-section {
  margin: 15px 0;
}

.price-label {
  color: #6b87a1;
  font-size: 14px;
}

.price-value {
  color: #1f3b57;
  font-size: 26px;
  font-weight: bold;
  margin-left: 5px;
}

.details-table {
  margin-bottom: 30px;
  flex-grow: 1;
}

.detail-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px dashed #e6f4ff;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  flex: 0 0 25%;
  color: #6b87a1;
  font-size: 14px;
}

.detail-value {
  flex: 0 0 25%;
  color: #1f3b57;
  font-size: 14px;
}

.action-area {
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid #e6f4ff;
}

.quantity-selector {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.quantity-label {
  color: #6b87a1;
  margin-right: 10px;
  font-size: 14px;
}

.quantity-input {
  width: 120px;
}

.button-group {
  display: flex;
  gap: 15px;
}

.cart-button, .like-button {
  padding: 10px 24px;
  font-size: 16px;
}

.cart-button {
  flex: 2;
}

.like-button {
  flex: 1;
}

@media (max-width: 768px) {
  .product-card {
    flex-direction: column;
  }

  .product-image-section {
    flex: none;
    padding: 15px;
  }

  .detail-row {
    flex-wrap: wrap;
  }

  .detail-label, .detail-value {
    flex: 0 0 50%;
    margin-bottom: 8px;
  }

  .button-group {
    flex-direction: column;
  }

  .cart-button, .like-button {
    width: 100%;
  }
}
</style>