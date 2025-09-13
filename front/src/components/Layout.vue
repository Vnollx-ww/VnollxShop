<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="logo">Element UI 管理系统</div>
        <div style="flex:1"></div>
        <div class="userbar">
          <el-dropdown trigger="click" @command="onUserCommand">
            <span class="avatar-trigger">
              <img :src="user.avatar || defaultAvatar" alt="avatar" class="avatar" />
              <span class="nickname">{{ user.name || user.email || '未登录' }}</span>
              <i class="el-icon-caret-bottom caret"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="recharge">充值</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
              :default-active="activeMenu"
              class="el-menu-vertical"
              @open="handleOpen"
              @close="handleClose"
              router
          >
            <el-submenu index="1">
              <template #title>
                <i class="el-icon-location"></i>
                <span>系统管理</span>
              </template>
              <el-menu-item-group>
                <template #title>业务功能</template>
                <el-menu-item index="/">商品列表</el-menu-item>
                <el-menu-item index="/cart">购物车</el-menu-item>
                <el-menu-item index="/orders">订单列表</el-menu-item>
                <el-menu-item index="/profile">个人中心</el-menu-item>
              </el-menu-item-group>
              <el-menu-item-group title="示例">
                <el-menu-item index="/system/user">用户管理</el-menu-item>
                <el-menu-item index="/test2">页面 1-2</el-menu-item>
              </el-menu-item-group>
            </el-submenu>

          </el-menu>
        </el-aside>
        <el-container>

          <el-main style="padding: 0px">
            <el-breadcrumb separator-class="el-icon-arrow-right"
                           style="background-color:white;height: 20px;padding: 10px">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item
                  v-for="(item, index) in breadcrumbList"
                  :key="index"
                  :to="item.path ? { path: item.path } : '#'"
              >
                {{ item.name }}
              </el-breadcrumb-item>
            </el-breadcrumb>
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { getMyInfo, updateUserInfo } from "@/api/user"
import { createPayment } from "@/api/pay"
export default {
  name: 'my_layout',
  data() {
    return {
      activeMenu: '',
      breadcrumbList: [],
      user: {},
      defaultAvatar: 'https://avatars.githubusercontent.com/u/0?v=4',
      // 个人信息对话框
      showProfile: false,
      saving: false,
      form: { name: '', email: '', avatar: '' },
      // 充值对话框
      showRecharge: false,
      paying: false,
      rechargeAmount: 100,
      channel: 'alipay'
    }
  },
  watch: {
    '$route'(to) {
      this.updateBreadcrumb(to);
      this.activeMenu = to.path;
    }
  },
  created() {
    this.updateBreadcrumb(this.$route);
    this.activeMenu = this.$route.path;
    this.fetchMe()
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    updateBreadcrumb(route) {
      const matched = route.matched.filter(record => record.name);
      this.breadcrumbList = matched.map(record => {
        return {
          name: record.name,
          path: record.path || ''
        };
      });
    },
    async fetchMe(){
      try{
        // 依赖网关注入 token，后端识别当前用户
        const { data } = await getMyInfo()
        this.user = data || {}
      }catch(e){ this.user = {} }
    },
    onUserCommand(cmd){
      if(cmd==='profile'){
        this.form.name = this.user.name || ''
        this.form.email = this.user.email || ''
        this.form.avatar = this.user.avatar || ''
        this.showProfile = true
      }else if(cmd==='recharge'){
        this.showRecharge = true
      }else if(cmd==='logout'){
        localStorage.removeItem('token')
        this.$message.success('已退出登录')
        this.$router.replace('/login')
      }
    },
    async saveProfile(){
      this.saving = true
      try{
        const formData = new FormData()
        if(this.user.id) formData.append('id', this.user.id)
        formData.append('name', this.form.name || '')
        await updateUserInfo(formData)
        this.$message.success('已保存')
        await this.fetchMe()
        this.showProfile = false
      }catch(e){} finally{ this.saving = false }
    },
    async doRecharge(){
      if(!this.rechargeAmount || this.rechargeAmount<=0){
        return this.$message.warning('请输入有效金额')
      }
      this.paying = true
      try{
        const { data } = await createPayment({ channel: this.channel, amount: this.rechargeAmount })
        if(data && data.url){ window.open(data.url, '_blank') }
        else { this.$message.success('已发起支付，请完成付款') }
        this.showRecharge = false
      }catch(e){} finally{ this.paying = false }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0px;
}

.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
  text-align: left;
}

.logo {
  text-align: left;
  padding-left: 20px;
  font-weight: bold;
  font-size: 18px;
}
.userbar{ display:flex; align-items:center; justify-content:flex-end; }
.avatar-trigger{ display:flex; align-items:center; gap:10px; cursor:pointer; user-select:none; }
.avatar{ width: 32px; height:32px; border-radius:50%; object-fit: cover; border:1px solid #e9eef3; }
.nickname{ color:#1d2939; font-size: 14px; }
.caret{ color:#98a2b3; }
</style>
