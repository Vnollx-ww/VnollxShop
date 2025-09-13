<template>
  <div class="page">
    <el-table :data="items" size="small" style="width:100%" v-if="items.length" :header-cell-style="{background:'#f0f7ff'}">
      <el-table-column label="商品" prop="productName" />
      <el-table-column label="单价" width="120">
        <template slot-scope="{row}">¥ {{ row.unitPrice }}</template>
      </el-table-column>
      <el-table-column label="数量" width="160">
        <template slot-scope="{row}">
          <el-input-number v-model="row.number" :min="1" @change="sync" />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="140">
        <template slot-scope="{row}">¥ {{ (row.unitPrice * row.number).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template slot-scope="{$index}">
          <el-button type="text" @click="remove($index)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-else class="empty">
      <img src="/favicon.ico" alt="empty" />
      <div class="tip">购物车还是空的，去逛逛吧～</div>
      <el-button type="primary" plain @click="$router.push('/ProductList')">去选购</el-button>
    </div>

    <div class="checkout" v-if="items.length">
      <div>合计：<b>¥ {{ total.toFixed(2) }}</b></div>
      <el-button type="primary" @click="openOrder">去下单</el-button>
    </div>

    <el-drawer title="填写收货信息" :visible.sync="showOrder" size="30%">
      <el-form label-width="80px" :model="order" ref="orderRef" :rules="rules">
        <el-form-item label="收货人">
          <el-input v-model="order.consignee" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="order.email" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="order.address" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="order.remark" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitOrder">提交</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </div>
</template>

<script>
import { createOrder } from "@/api/order";
export default {
  name:'Cart',
  data(){
    return {
      items: [],
      showOrder: false,
      submitting: false,
      order: {
        consignee: '',
        email: '',
        address: '',
        remark: ''
      },
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
    total(){
      return this.items.reduce((s,i)=> s + i.unitPrice * i.number, 0)
    }
  },
  created(){
    this.load()
  },
  methods:{
    load(){
      try{
        this.items = JSON.parse(localStorage.getItem('cart')||'[]')
      }catch(e){ this.items = [] }
    },
    sync(){
      localStorage.setItem('cart', JSON.stringify(this.items))
    },
    remove(index){
      this.items.splice(index,1)
      this.sync()
    },
    openOrder(){
      if(!this.items.length){
        this.$message.warning('购物车为空')
        return
      }
      this.showOrder = true
    },
    async submitOrder(){
      this.$refs.orderRef.validate(async (valid)=>{
        if(!valid) return this.$message.warning('请完善收货信息');
        this.submitting = true
        try{
          const dto = {
          // uid 后端会从网关token注入，无需传
          items: JSON.stringify(this.items.map(i=>({
            pid: String(i.id),
            number: i.number,
            unitPrice: i.unitPrice,
            productName: i.productName
          }))),
          totalCost: this.total,
          consignee: this.order.consignee,
          email: this.order.email,
          address: this.order.address,
          remark: this.order.remark
          }
          await createOrder(dto)
          this.$message.success('下单成功')
          this.items = []
          localStorage.removeItem('cart')
          this.showOrder = false
          this.$router.push('/orders')
        }catch(e){
          this.$message.error('下单失败，请确认登录并重试')
        }finally{
          this.submitting = false
        }
      })
    }
  }
}
</script>

<style scoped>
.checkout{ display:flex; justify-content: space-between; align-items:center; margin-top: 12px; }
.empty{ text-align:center; color:#6b87a1; padding: 36px 0; }
.empty img{ width: 48px; height:48px; opacity: .6; }
.empty .tip{ margin: 8px 0 12px; }
</style>



