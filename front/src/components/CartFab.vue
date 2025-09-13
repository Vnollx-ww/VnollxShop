<template>
  <div>
    <!-- 自定义悬浮按钮（底部居中、渐变圆） -->
    <div class="fab" @click="openDrawer">
      <i class="el-icon-shopping-cart-2"></i>
      <span class="fab-text">购物车</span>
      <span class="count" v-if="badgeCount > 0">{{ badgeCount > 99 ? '99+' : badgeCount }}</span>
    </div>

    <!-- 抽屉：购物车明细 -->
    <el-drawer :visible.sync="visible" size="420px" title="我的购物车">
      <div class="cart-container">
        <el-table :data="items" v-if="items.length" size="small" :header-cell-style="{background:'#f0f7ff'}" @selection-change="onSelectionChange">
          <el-table-column type="selection" width="45" />
          <el-table-column label="商品" min-width="160">
            <template slot-scope="{row}">
              <div class="row-flex">
                <img :src="row.cover" alt="cover" class="thumb" v-if="row.cover" />
                <div class="name-price">
                  <div class="name" :title="row.productName">{{ row.productName }}</div>
                  <div class="price">¥ {{ row.unitPrice }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="160">
            <template slot-scope="{row}">
              <el-input-number v-model="row.number" :min="1" @change="val=>onChangeNumber(row, val)" />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template slot-scope="{row}">¥ {{ (row.unitPrice * row.number).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="90">
            <template slot-scope="{row}">
              <el-button type="text" @click="removeRow(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="empty" v-else>
          <img src="/favicon.ico" alt="empty" />
          <div class="tip">购物车为空</div>
        </div>

        <div class="footer" v-if="items.length">
          <div class="summary">
            <span class="summary-count">已选 {{ selectedCount }} 件</span>
            <span class="divider">|</span>
            <span class="summary-total">合计：<b>¥ {{ selectedTotal.toFixed(2) }}</b></span>
          </div>
          <div class="actions">
            <el-popconfirm
              title="确认批量移除选中商品？"
              confirmButtonText="移除"
              cancelButtonText="取消"
              icon="el-icon-warning"
              iconColor="#f56c6c"
              @confirm="batchRemove"
            >
              <el-button slot="reference" type="danger" plain :disabled="!selectedIds.length">批量移除</el-button>
            </el-popconfirm>
            <el-button type="primary" :disabled="!selectedIds.length" @click="openOrderForm">去下单</el-button>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 下单信息表单 -->
    <el-drawer :visible.sync="showOrder" title="填写收货信息" size="30%">
      <el-form label-width="84px" :model="order" ref="orderRef" :rules="rules">
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="order.consignee" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="order.email" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="order.address" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="order.remark" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="goCheckout">提交订单</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </div>
  
</template>

<script>
import { getCartList, updateCartNumber, deleteCartItems, getCartCount } from "@/api/cart"
import { createOrder } from "@/api/order"
export default {
  name: 'CartFab',
  data(){
    return {
      visible: false,
      items: [],
      submitting: false,
      cartCount: 0,
      selectedIds: [],
      showOrder: false,
      order: { consignee: '', email: '', address: '', remark: '' },
      rules: {
        consignee: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: ['blur','change'] }
        ],
        address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
      }
    }
  },
  computed:{
    badgeCount(){ return this.cartCount },
    total(){
      return this.items.reduce((s,i)=> s + (i.unitPrice * i.number), 0)
    },
    selectedItems(){
      const set = new Set(this.selectedIds.map(String))
      return this.items.filter(i=> set.has(String(i.id)))
    },
    selectedTotal(){
      return this.selectedItems.reduce((s,i)=> s + (i.unitPrice * i.number), 0)
    },
    selectedCount(){
      return this.selectedItems.reduce((s,i)=> s + (i.number||0), 0)
    }
  },
  mounted(){
    // 初始化购物车数量
    this.syncCountFromAPI()
    // 监听全局购物车数量变更事件
    window.addEventListener('cart-count-change', this.onGlobalCountChange)
  },
  beforeDestroy(){
    window.removeEventListener('cart-count-change', this.onGlobalCountChange)
  },
  methods:{
    async syncCountFromAPI(){
      try{
        const { data } = await getCartCount()
        this.cartCount = Number(data || 0)
      }catch(e){
        console.error('获取购物车数量失败', e)
        this.cartCount = 0
      }
    },
    setCountToState(next){
      this.cartCount = Math.max(0, Number(next) || 0)
    },
    onGlobalCountChange(e){
      const detail = e && e.detail || {}
      if (typeof detail.count === 'number'){
        this.setCountToState(detail.count)
      } else if (typeof detail.delta === 'number'){
        this.setCountToState((this.cartCount || 0) + detail.delta)
      } else {
        this.syncCountFromAPI()
      }
    },
    async openDrawer(){
      this.visible = true
      await this.load()
    },
    async load(){
      try{
        // 清空当前购物车数据，避免重复累加
        this.items = []
        
        // 从API获取购物车数据
        const { data } = await getCartList()
        if (Array.isArray(data)) {
          this.items = data
          console.log('从API加载购物车数据成功', this.items.length)
          
          // 确保所有数量都是数字
          this.items.forEach(item => {
            item.number = parseInt(item.number) || 1
          })
          
          // 获取最新的购物车数量
          const { data: count } = await getCartCount()
          this.setCountToState(count)
          console.log('购物车加载完成，总数量:', count)
        } else {
          console.warn('API返回的购物车数据无效')
          this.items = []
          this.setCountToState(0)
        }
      }catch(e){
        console.error('加载购物车失败', e)
        // 出现异常，清空购物车
        this.items = []
        this.setCountToState(0)
        this.$message.error('加载购物车失败，请先登录或稍后再试')
      }
    },
    async onChangeNumber(row, val){
      try{
        // 确保数量是整数
        row.number = parseInt(row.number) || 1
        
        // 调用API更新数量
        await updateCartNumber({ ciid: row.id, number: row.number })
        
        // 获取最新的购物车数量
        const { data: count } = await getCartCount()
        this.setCountToState(count)
        window.dispatchEvent(new CustomEvent('cart-count-change', { detail: { count } }))
        
        this.$message.success('已更新数量')
      }catch(e){
        console.error('更新数量失败', e)
        this.$message.error('更新数量失败，请先登录或稍后再试')
      }
    },
    onSelectionChange(rows){
      this.selectedIds = (rows||[]).map(r=> String(r.id))
    },
    async batchRemove(){
      if(!this.selectedIds.length) return
      try{
        await deleteCartItems({ idList: this.selectedIds })
        this.items = this.items.filter(i=> !this.selectedIds.includes(String(i.id)))
        
        // 获取最新的购物车数量
        const { data: count } = await getCartCount()
        this.setCountToState(count)
        window.dispatchEvent(new CustomEvent('cart-count-change', { detail: { count } }))
        
        this.$message.success('已批量移除')
      }catch(e){
        console.error('批量移除失败', e)
        this.$message.error('批量移除失败，请先登录或稍后再试')
      }
    },
    async removeRow(row){
      try{
        // 调用API删除购物车项
        await deleteCartItems({ idList: [String(row.id)] })
        
        this.$message.success('已移除商品')
        this.items = this.items.filter(i=> i.id !== row.id)
        
        // 获取最新的购物车数量
        const { data: count } = await getCartCount()
        this.setCountToState(count)
        window.dispatchEvent(new CustomEvent('cart-count-change', { detail: { count } }))
      }catch(e){
        console.error('移除失败', e)
        this.$message.error('移除失败，请先登录或稍后再试')
      }
    },
    openOrderForm(){
      if(!this.items.length) return this.$message.warning('购物车为空')
      if(!this.selectedIds.length) return this.$message.warning('请先勾选要下单的商品')
      this.showOrder = true
    },
    async goCheckout(){
      if(!this.items.length) return this.$message.warning('购物车为空')
      if(!this.selectedIds.length) return this.$message.warning('请先勾选要下单的商品')
      this.submitting = true
      try{
        const valid = await new Promise(resolve=> this.$refs.orderRef.validate(v=> resolve(v)))
        if(!valid){ this.submitting = false; return }
        const itemsPayload = this.selectedItems.map(i=>({
          pid: String(i.pid || i.id),
          number: i.number,
          unitPrice: i.unitPrice,
          productName: i.productName || i.name || ''
        }))
        await createOrder({
          items: itemsPayload,
          totalCost: this.selectedTotal,
          consignee: this.order.consignee,
          email: this.order.email,
          address: this.order.address,
          remark: this.order.remark
        })
        this.$message.success('下单成功')
        // 仅删除已下单的选择项
        try{ await deleteCartItems({ idList: this.selectedIds }) }catch(e){}
        // 从本地列表移除选中项
        const selectedSet = new Set(this.selectedIds.map(String))
        this.items = this.items.filter(i=> !selectedSet.has(String(i.id)))
        
        // 获取最新的购物车数量
        const { data: count } = await getCartCount()
        this.setCountToState(count)
        window.dispatchEvent(new CustomEvent('cart-count-change', { detail: { count } }))
        // 清空选择
        this.selectedIds = []
        this.visible = false
        this.showOrder = false
        this.$router.push('/orders')
      }catch(e){
        // 直接显示错误信息，优先使用后端返回的错误信息
        this.$message.error(e && e.message ? e.message : '下单失败，请稍后再试')
      }finally{
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.fab{
  position: fixed;
  left: 50%;
  bottom: 22px;
  transform: translateX(-50%);
  z-index: 2000;
  height: 52px;
  padding: 0 18px 0 52px;
  border-radius: 28px;
  background: linear-gradient(135deg, #7367f0 0%, #32ccbc 100%);
  color: #fff;
  display: inline-flex;
  align-items: center;
  box-shadow: 0 10px 24px rgba(0,0,0,.18);
  cursor: pointer;
  user-select: none;
}
.fab i{
  position: absolute;
  left: 12px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,.22);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}
.fab-text{ font-size: 14px; letter-spacing: .5px; }
.count{
  margin-left: 10px;
  background: #ff4d4f;
  color: #fff;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
  line-height: 1;
}
.cart-container{ padding: 8px 16px 16px; }
.row-flex{ display:flex; align-items:center; gap:10px; }
.thumb{ width: 40px; height:40px; object-fit: cover; border-radius: 4px; border:1px solid #eee; }
.name-price .name{ font-size:13px; color:#1f3b57; line-height: 1.3; max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.name-price .price{ color:#1677ff; margin-top: 2px; }
.footer{ display:flex; justify-content: space-between; align-items:center; padding-top: 8px; border-top:1px solid #f0f0f0; margin-top: 8px; }
.footer .summary{ display:flex; align-items:center; gap:10px; color:#1f3b57; font-size: 13px; }
.footer .summary-count{ background:#f6f8ff; padding:4px 8px; border-radius: 12px; color:#6b87a1; }
.footer .divider{ color:#d0d8e2; }
.footer .summary-total b{ color:#1677ff; font-size: 16px; }
.footer .actions{ display:flex; align-items:center; gap:10px; }
.empty{ text-align:center; color:#6b87a1; padding: 24px 0; }
.empty img{ width: 40px; height:40px; opacity: .6; }
</style>


