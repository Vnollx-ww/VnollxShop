<template>
  <div class="topbar">
    <div class="logo">Vnollx 商城</div>
    <div class="spacer"></div>
    <el-dropdown trigger="click" @command="onUserCommand">
      <span class="avatar-trigger">
        <img :src="user.avatar || defaultAvatar" alt="avatar" class="avatar" />
        <span class="nickname">{{ user.name || user.email || '未登录' }}</span>
        <i class="el-icon-caret-bottom caret"></i>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="profile">个人信息</el-dropdown-item>
        <el-dropdown-item command="orders">个人订单</el-dropdown-item>
        <el-dropdown-item command="recharge">充值</el-dropdown-item>
        <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>

    <!-- 个人信息弹窗 -->
    <el-dialog title="编辑个人信息" :visible.sync="showProfile" width="520px">
      <el-form :model="form" label-width="88px">
        <el-form-item label="头像">
          <div class="avatar-upload">
            <img :src="form.avatar || defaultAvatar" alt="preview" class="avatar-preview" />
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-change="onAvatarChange"
              accept="image/*"
              :auto-upload="false"
            >
              <el-button size="small" type="primary">选择头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 充值弹窗 -->
    <el-dialog title="账户充值" :visible.sync="showRecharge" width="520px">
      <div class="recharge">
        <div class="amount">
          <span>充值金额：</span>
          <el-input-number v-model="rechargeAmount" :min="1" :step="10" />
        </div>
        <div class="channels">
          <el-radio-group v-model="channel">
            <el-radio label="alipay">支付宝</el-radio>
            <el-radio label="wechat">微信支付</el-radio>
          </el-radio-group>
        </div>
        <div class="actions">
          <el-button type="primary" :loading="paying" @click="doRecharge">去支付</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMyInfo, updateUserInfo,recharge} from "@/api/user"
import { createPayment } from "@/api/pay"
export default {
  name: 'TopBar',
  data(){
    return {
      user: {},
      defaultAvatar: 'https://avatars.githubusercontent.com/u/0?v=4',
      showProfile: false,
      saving: false,
      form: { name: '', avatar: '' },
      showRecharge: false,
      paying: false,
      rechargeAmount: 100,
      channel: 'alipay',
      avatarFile: null // 存储上传的文件对象
    }
  },
  async created(){
    await this.fetchMe()
  },
  methods:{
    async fetchMe(){
      try{
        const { data } = await getMyInfo()
        this.user = data || {}
      }catch(e){ this.user = {} }
    },
    onUserCommand(cmd){
      if(cmd==='profile'){
        this.form.name = this.user.name || ''
        this.form.avatar = this.user.avatar || ''
        this.avatarFile = null // 重置文件对象
        this.showProfile = true
      }else if(cmd==='recharge'){
        this.showRecharge = true
      }else if (cmd==='orders'){
        this.$router.replace('/orders')
      } else if(cmd==='logout'){
        localStorage.removeItem('token')
        this.$message.success('已退出登录')
        this.$router.replace('/login')
      }
    },
    async saveProfile(){
      this.saving = true
      try{
        const fd = new FormData()
        fd.append('name', this.form.name || '')
        // 如果有新上传的头像文件，添加到表单数据
        if(this.avatarFile) {
          fd.append('avatar', this.avatarFile)
        }
        const { data } = await updateUserInfo(fd)
        // 后端返回 UserInfoVO，直接更新本地状态
        if(data) {
          this.user = { ...this.user, ...data }
        } else {
          await this.fetchMe()
        }
        this.$message.success('已保存')
        this.showProfile = false
      }catch(e){} finally{ this.saving = false }
    },
    beforeAvatarUpload(file){
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    onAvatarChange(file, fileList){
      // 选择文件后，存储文件对象并预览
      this.avatarFile = file.raw
      // 创建预览URL
      const reader = new FileReader()
      reader.onload = (e) => {
        this.form.avatar = e.target.result
      }
      reader.readAsDataURL(file.raw)
    },
    async doRecharge(){
      if(!this.rechargeAmount || this.rechargeAmount<=0){
        return this.$message.warning('请输入有效金额')
      }
      this.paying = true
      try{
        const { data } = await recharge({ channel: this.channel, amount: this.rechargeAmount })
        this.$message.success('充值成功')
        this.showRecharge = false
      }catch(e){

      }
    }
  }
}
</script>

<style scoped>
.topbar{ display:flex; align-items:center; gap:12px; height:56px; padding: 0 16px; background:#fff; border-radius: 10px; box-shadow: 0 6px 18px rgba(24, 144, 255, 0.08), 0 2px 6px rgba(24, 144, 255, 0.06); margin-bottom: 12px; }
.logo{ font-weight: 600; color:#1f3b57; }
.spacer{ flex:1; }
.avatar-trigger{ display:flex; align-items:center; gap:10px; cursor:pointer; user-select:none; }
.avatar{ width: 32px; height:32px; border-radius:50%; object-fit: cover; border:1px solid #e9eef3; }
.nickname{ color:#1d2939; font-size: 14px; }
.caret{ color:#98a2b3; }
.recharge{ padding: 8px 6px; }
.recharge .amount{ margin-bottom: 12px; display:flex; align-items:center; gap:8px; }
.channels{ margin: 12px 0; }
.avatar-upload{ display:flex; align-items:center; gap:12px; }
.avatar-preview{ width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 1px solid #e9eef3; }
</style>


