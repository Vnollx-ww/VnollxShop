<template>
  <div class="page">
    <div v-if="orders.length" class="orders-container">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <!-- 订单基本信息 -->
        <div class="order-header">
          <div class="order-info">
            <span class="order-id">订单号：{{ order.id }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>
          <div class="order-amount">
            <span class="amount-label">订单金额：</span>
            <span class="amount-value">¥ {{ order.totalCost }}</span>
          </div>
        </div>

        <!-- 收货信息 -->
        <div class="delivery-info">
          <div class="info-item">
            <span class="label">收货人：</span>
            <span class="value">{{ order.consignee }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ order.email }}</span>
          </div>
          <div class="info-item">
            <span class="label">地址：</span>
            <span class="value">{{ order.address }}</span>
          </div>
          <div v-if="order.remark" class="info-item">
            <span class="label">备注：</span>
            <span class="value">{{ order.remark }}</span>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="products-section">
          <div class="section-title">商品详情</div>
          <div class="products-list">
            <div v-for="item in order.orderItemVOList" :key="item.id" class="product-item" @click="goToProductDetail(item.pid)">
              <div class="product-image">
                <img :src="item.cover || defaultProductImage" :alt="item.productName" class="product-cover" />
              </div>
              <div class="product-info">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-details">
                  <div class="detail-row">
                    <span class="detail-item">
                      <i class="el-icon-goods"></i>
                      商品ID：{{ item.pid }}
                    </span>
                    <span class="detail-item">
                      <i class="el-icon-shopping-cart-2"></i>
                      数量：{{ item.number }}
                    </span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-item">
                      <i class="el-icon-money"></i>
                      单价：¥ {{ item.unitPrice }}
                    </span>
                  </div>
                </div>
              </div>
              <div class="product-total">
                <div class="total-label">小计</div>
                <div class="total-value">¥ {{ item.cost }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="order-actions">
          <el-popconfirm title="确认删除该订单？" @confirm="remove(order)">
            <el-button slot="reference" type="danger" size="small">删除订单</el-button>
          </el-popconfirm>
        </div>
      </div>
    </div>
    
    <div v-else class="empty">
      <img src="/favicon.ico" alt="empty" />
      <div class="tip">暂无订单，去创建第一笔订单吧～</div>
      <el-button type="primary" plain @click="$router.push('/')">去选购</el-button>
    </div>
  </div>
</template>

<script>
import { getOrderList, deleteOrder } from "@/api/order";
export default {
  name:'Orders',
  data(){
    return { 
      orders: [],
      defaultProductImage: 'https://via.placeholder.com/120x120?text=商品图片'
    }
  },
  async created(){
    await this.fetch()
  },
  methods:{
    async fetch(){
      const { data } = await getOrderList()
      this.orders = Array.isArray(data) ? data : []
    },
    async remove(order){
      try{
        await deleteOrder({ oid: String(order.id) })
        this.$message.success('已删除')
        await this.fetch()
      }catch(e){
        this.$message.error('删除失败')
      }
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    goToProductDetail(productId) {
      this.$router.push(`/product/${productId}`)
    }
  }
}
</script>

<style scoped>
.orders-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #e9ecef;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-id {
  font-size: 16px;
  font-weight: bold;
  color: #1f3b57;
}

.order-time {
  font-size: 14px;
  color: #6b87a1;
}

.order-amount {
  text-align: right;
}

.amount-label {
  font-size: 14px;
  color: #6b87a1;
}

.amount-value {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
  margin-left: 8px;
}

.delivery-info {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  font-weight: 500;
  color: #6b87a1;
  min-width: 60px;
}

.value {
  color: #1f3b57;
  flex: 1;
}

.products-section {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #1f3b57;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #1890ff;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.product-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #1890ff;
}

.product-item::after {
  content: '点击查看商品详情';
  position: absolute;
  top: 8px;
  right: 8px;
  background: #1890ff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.product-item:hover::after {
  opacity: 1;
}

.product-image {
  flex-shrink: 0;
}

.product-cover {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.product-cover:hover {
  border-color: #1890ff;
  transform: scale(1.05);
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f3b57;
  margin-bottom: 12px;
  line-height: 1.4;
}

.product-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #6b87a1;
  background: white;
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.detail-item:hover {
  background: #f0f7ff;
  border-color: #1890ff;
  color: #1890ff;
}

.detail-item i {
  font-size: 16px;
}

.product-total {
  flex-shrink: 0;
  text-align: right;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 2px solid #1890ff;
  min-width: 120px;
}

.total-label {
  font-size: 14px;
  color: #6b87a1;
  margin-bottom: 4px;
}

.total-value {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.order-actions {
  padding: 20px;
  text-align: right;
  background: #f8f9fa;
}

.empty { 
  text-align: center; 
  color: #6b87a1; 
  padding: 36px 0; 
}

.empty img { 
  width: 48px; 
  height: 48px; 
  opacity: .6; 
}

.empty .tip { 
  margin: 8px 0 12px; 
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .order-amount {
    text-align: left;
  }
  
  .product-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .product-image {
    align-self: center;
  }
  
  .product-cover {
    width: 100px;
    height: 100px;
  }
  
  .product-total {
    text-align: center;
    width: 100%;
    min-width: auto;
  }
  
  .detail-row {
    flex-direction: column;
    gap: 8px;
  }
  
  .detail-item {
    justify-content: center;
  }
}
</style>



