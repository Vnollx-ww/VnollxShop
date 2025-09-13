<template>
  <div class="page">
    <el-table :data="orders" size="small" v-if="orders.length" :header-cell-style="{background:'#f0f7ff'}">
      <el-table-column label="订单号" prop="id" />
      <el-table-column label="金额" width="120">
        <template slot-scope="{row}">¥ {{ row.totalCost }}</template>
      </el-table-column>
      <el-table-column label="收货人" prop="consignee" />
      <el-table-column label="邮箱" prop="email" />
      <el-table-column label="地址" prop="address" min-width="160" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="120">
        <template slot-scope="{row}">
          <el-popconfirm title="确认删除该订单？" @confirm="remove(row)">
            <el-button slot="reference" type="text">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div v-else class="empty">
      <img src="/favicon.ico" alt="empty" />
      <div class="tip">暂无订单，去创建第一笔订单吧～</div>
      <el-button type="primary" plain @click="$router.push('/ProductList')">去选购</el-button>
    </div>
  </div>
</template>

<script>
import { getOrderList, deleteOrder } from "@/api/order";
export default {
  name:'Orders',
  data(){
    return { orders: [] }
  },
  async created(){
    await this.fetch()
  },
  methods:{
    async fetch(){
      const { data } = await getOrderList()
      this.orders = Array.isArray(data) ? data : []
    },
    async remove(row){
      try{
        await deleteOrder({ oid: String(row.id) })
        this.$message.success('已删除')
        await this.fetch()
      }catch(e){
        this.$message.error('删除失败')
      }
    }
  }
}
</script>

<style scoped>
.empty{ text-align:center; color:#6b87a1; padding: 36px 0; }
.empty img{ width: 48px; height:48px; opacity: .6; }
.empty .tip{ margin: 8px 0 12px; }
</style>



