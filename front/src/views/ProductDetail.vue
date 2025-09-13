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
          <h1 class="product-title">
            {{ detail.name }}
            <span v-if="isFlashSaleProduct" class="flash-sale-tag">秒杀商品</span>
          </h1>
          <p class="product-subtitle">{{ detail.introduce }}</p>

          <!-- 价格信息 -->
          <div class="price-section">
            <span class="price-label">售价：</span>
            <span class="price-value">¥ {{ detail.price }}</span>
            <span v-if="isFlashSaleProduct" class="flash-sale-price-note">（秒杀特价）</span>
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
                :max="isFlashSaleProduct ? 1 : detail.stock"
                :disabled="isFlashSaleProduct"
                class="quantity-input"
            />
            <span v-if="isFlashSaleProduct" class="flash-sale-limit">（秒杀商品，每人限购1件）</span>
          </div>

          <div class="button-group" :class="{ 'flash-sale-product': isFlashSaleProduct }">
            <el-button
                type="success"
                @click="buyNow(detail)"
                class="buy-now-button"
            >
              立即购买
            </el-button>
            <el-button
                v-if="!isFlashSaleProduct"
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

    <!-- 直接购买订单对话框 -->
    <el-dialog title="填写订单信息" :visible.sync="showOrderDialog" width="500px" :close-on-click-modal="false">
      <el-form :model="orderForm" :rules="orderRules" ref="orderFormRef" label-width="80px">
        <el-form-item label="商品信息">
          <div class="order-product-info">
            <img :src="detail?.cover" :alt="detail?.name" class="order-product-image" />
            <div class="order-product-details">
              <div class="order-product-name">{{ detail?.name }}</div>
              <div class="order-product-price">¥ {{ detail?.price }} × {{ buyNum }} = ¥ {{ (detail?.price * buyNum).toFixed(2) }}</div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="orderForm.consignee" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="orderForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="orderForm.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="orderForm.remark" placeholder="选填：订单备注信息" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showOrderDialog = false">取消</el-button>
        <el-button type="primary" :loading="submittingOrder" @click="submitOrder">确认下单</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getProductInfo, addLike, cancelLike } from "@/api/product";
import { addCartItem, getCartCount } from "@/api/cart";
import { createOrder } from "@/api/order";
export default {
  name: 'ProductDetail',
  data() {
    return {
      detail: null,
      buyNum: 1,
      showOrderDialog: false,
      submittingOrder: false,
      orderForm: {
        consignee: '',
        email: '',
        address: '',
        remark: ''
      },
      orderRules: {
        consignee: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
        ],
        address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isFlashSaleProduct() {
      return this.detail && this.detail.type === "1"
    }
  },
  async created() {
    await this.fetch()
  },
  methods: {
    async fetch() {
      const pid = this.$route.params.id
      const { data } = await getProductInfo(pid)
      console.log(data);
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
        
        // 检查秒杀商品限购
        if (this.isFlashSaleProduct && buyNumInt > 1) {
          this.$message.warning('秒杀商品每人限购1件')
          return
        }
        
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
    },
    buyNow(item) {
      // 检查库存
      if (this.buyNum > item.stock) {
        this.$message.warning('购买数量超过库存限制')
        return
      }
      
      // 检查秒杀商品限购
      if (this.isFlashSaleProduct && this.buyNum > 1) {
        this.$message.warning('秒杀商品每人限购1件')
        return
      }
      
      // 重置订单表单
      this.orderForm = {
        consignee: '',
        email: '',
        address: '',
        remark: ''
      }
      
      // 显示订单对话框
      this.showOrderDialog = true
    },
    async submitOrder() {
      // 验证表单
      this.$refs.orderFormRef.validate(async (valid) => {
        if (!valid) {
          this.$message.warning('请完善订单信息')
          return
        }
        
        this.submittingOrder = true
        try {
          // 构建订单数据
          const orderData = {
            items: [{
              pid: String(this.detail.id),
              number: this.buyNum,
              unitPrice: this.detail.price,
              productName: this.detail.name
            }],
            totalCost: this.detail.price * this.buyNum,
            consignee: this.orderForm.consignee,
            email: this.orderForm.email,
            address: this.orderForm.address,
            remark: this.orderForm.remark
          }
          
          // 如果是秒杀商品，添加type参数
          if (this.isFlashSaleProduct) {
            orderData.type = "1"
          }
          
          // 调用创建订单接口
          await createOrder(orderData)
          
          this.$message.success('下单成功！')
          this.showOrderDialog = false
          
          // 跳转到订单列表页面
          this.$router.push('/orders')
          
        } catch (error) {
          console.error('下单失败:', error)
          // 显示后端返回的具体错误信息
          const errorMessage = error.message || '下单失败，请重试'
          this.$message.error(errorMessage)
        } finally {
          this.submittingOrder = false
        }
      })
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

.buy-now-button, .cart-button, .like-button {
  padding: 10px 24px;
  font-size: 16px;
}

.buy-now-button {
  flex: 2;
  background-color: #52c41a;
  border-color: #52c41a;
}

.buy-now-button:hover {
  background-color: #73d13d;
  border-color: #73d13d;
}

.cart-button {
  flex: 2;
}

.like-button {
  flex: 1;
}

/* 秒杀商品时，立即购买按钮占更多空间 */
.flash-sale-product .buy-now-button {
  flex: 3;
}

.flash-sale-product .like-button {
  flex: 1;
}

/* 订单对话框样式 */
.order-product-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.order-product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.order-product-details {
  flex: 1;
}

.order-product-name {
  font-weight: 600;
  color: #1f3b57;
  margin-bottom: 4px;
  font-size: 14px;
  line-height: 1.4;
}

.order-product-price {
  color: #ff4d4f;
  font-weight: bold;
  font-size: 16px;
}

.dialog-footer {
  text-align: right;
}

/* 秒杀商品相关样式 */
.flash-sale-tag {
  background: #1890ff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  margin-left: 12px;
  vertical-align: middle;
}

.flash-sale-price-note {
  color: #1890ff;
  font-size: 14px;
  font-weight: 500;
  margin-left: 8px;
}

.flash-sale-limit {
  color: #ff4d4f;
  font-size: 12px;
  margin-left: 8px;
  font-weight: 500;
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

  .buy-now-button, .cart-button, .like-button {
    width: 100%;
  }
}
</style>