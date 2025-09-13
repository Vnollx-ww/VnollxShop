<template>
  <div class="page">
    <el-card shadow="hover">
      <div slot="header" class="header">个人中心</div>
      <el-descriptions :column="2" border size="small" :label-style="{width:'90px'}" class="info">
        <el-descriptions-item label="用户ID">{{ uid || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ user?.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="余额">¥ {{ balance == null ? '-' : balance }}</el-descriptions-item>
      </el-descriptions>
      <div class="actions">
        <el-button type="primary" plain @click="load">刷新</el-button>
        <el-button type="danger" plain @click="doLogout">退出登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getUserInfoById, getBalance, logout } from "@/api/user";
export default {
  name:'Profile',
  data(){
    return { uid:'', email:'', user:null, balance:null }
  },
  created(){
    this.parseToken()
    this.load()
  },
  methods:{
    parseToken(){
        const token = localStorage.getItem('token')
        if(!token) return
        const payload = JSON.parse(atob(token.split('.')[1]||'')) || {}
        this.uid = payload.uid || payload.sub || ''
        this.email = payload.identity || ''
    },
    async load(){
      if(!this.uid) return
      try{
        const u = await getUserInfoById(this.uid)
        this.user = u.data || null
        const b = await getBalance(this.uid)
        this.balance = b.data
      }catch(e){
        this.$message.error('加载用户信息失败')
      }
    },
    async doLogout(){
      try{
        await logout({})
      }catch(e){}
      localStorage.removeItem('token')
      this.$router.replace('/login')
    }
  }
}
</script>

<style scoped>
.header{ font-weight: 600; color:#1f3b57; }
.info{ border-color:#e6f4ff; }
.actions{ margin-top:12px; display:flex; gap:8px; }
</style>



