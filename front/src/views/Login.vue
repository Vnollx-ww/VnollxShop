<template>
  <div class="login-page">
    <!-- 背景装饰元素 -->
    <div class="bg-decor decor-1"></div>
    <div class="bg-decor decor-2"></div>

    <!-- 登录主体容器 -->
    <div class="login-card">
      <!-- Logo 区域 -->
      <div class="login-logo">
        <div class="logo-icon">
          <i class="el-icon-shopping-bag"></i>
        </div>
      </div>

      <!-- 标题区域 -->
      <div class="login-header">
        <h2 class="login-title">Vnollx 电商管理系统</h2>
        <p class="login-subtitle">请登录您的账户，继续管理工作</p>
      </div>

      <!-- 登录表单 -->
      <form @submit.prevent="handleSubmit" class="login-form">
        <!-- 邮箱输入框 -->
        <div class="form-group" :class="{ 'has-error': emailError }">
          <label class="form-label">邮箱</label>
          <div class="input-group">
            <i class="el-icon-message input-icon"></i>
            <input
                v-model="loginForm.email"
                type="email"
                placeholder="请输入注册邮箱"
                autocomplete="username"
                class="form-input"
                @focus="clearError('email')"
                @blur="validateEmail"
            >
          </div>
          <p class="error-tip">{{ emailError }}</p>
        </div>

        <!-- 密码输入框（带可见切换） -->
        <div class="form-group" :class="{ 'has-error': passwordError }">
          <label class="form-label">密码</label>
          <div class="input-group">
            <i class="el-icon-lock input-icon"></i>
            <input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码（6-20位）"
                autocomplete="current-password"
                class="form-input"
                @focus="clearError('password')"
                @blur="validatePassword"
            >
            <i
                :class="showPassword ? 'el-icon-eye' : 'el-icon-eye-off'"
                class="toggle-password"
                @click="showPassword = !showPassword"
            ></i>
          </div>
          <p class="error-tip">{{ passwordError }}</p>
        </div>

        <!-- 记住登录 & 忘记密码 -->
        <div class="form-extra">
          <label class="remember-checkbox" @click.stop>
            <input
                v-model="loginForm.remember"
                type="checkbox"
                class="custom-checkbox"
            >
            <span class="checkbox-text">记住登录（7天）</span>
          </label>
          <a
              href="javascript:;"
              class="forgot-link"
              @click="handleForgotPassword"
          >
            忘记密码？
          </a>
        </div>

        <!-- 登录按钮 -->
        <button
            type="submit"
            class="login-btn"
            :disabled="loading"
        >
          <span v-if="!loading">登录系统</span>
          <div v-else class="loading-spinner">
            <span class="spinner"></span>
          </div>
        </button>
      </form>

      <!-- 底部版权信息 -->
      <div class="login-footer">
        <p>© 2025 Vnollx 电商管理系统. 保留所有权利</p>
      </div>
    </div>
  </div>
</template>

<script>
import { login as apiLogin } from "@/api/user";

export default {
  name: "VnollxLogin",
  data() {
    return {
      loginForm: {
        email: "",
        password: "",
        remember: false
      },
      loading: false,       // 登录加载状态
      showPassword: false,  // 密码可见切换
      emailError: "",       // 邮箱错误提示
      passwordError: ""     // 密码错误提示
    };
  },
  created() {
    // 读取本地存储的记住邮箱
    const rememberedEmail = localStorage.getItem("rememberEmail");
    if (rememberedEmail) {
      this.loginForm.email = rememberedEmail;
      this.loginForm.remember = true;
    }
  },
  methods: {
    // 表单提交
    async handleSubmit() {
      // 先执行全量验证
      const isEmailValid = this.validateEmail();
      const isPasswordValid = this.validatePassword();

      if (!isEmailValid || !isPasswordValid) return;

      this.loading = true;
      try {
        // 调用登录接口
        const { data: token } = await apiLogin({
          email: this.loginForm.email,
          password: this.loginForm.password
        });

        if (!token) throw new Error("登录响应异常");

        // 存储Token和记住邮箱
        localStorage.setItem("token", token);
        if (this.loginForm.remember) {
          localStorage.setItem("rememberEmail", this.loginForm.email);
        } else {
          localStorage.removeItem("rememberEmail");
        }

        // 提示并跳转
        this.$message.success("登录成功，正在跳转...");
        setTimeout(() => {
          this.$router.replace("/homepage");
        }, 800);
      } catch (error) {
        const errorMsg = error.response?.data?.message || "登录失败，请检查邮箱或密码";
        if(!error || !error.shown){ this.$message.error(errorMsg); }
      } finally {
        this.loading = false;
      }
    },

    // 邮箱验证
    validateEmail() {
      const email = this.loginForm.email.trim();
      if (!email) {
        this.emailError = "请输入邮箱地址";
        return false;
      }
      // 邮箱格式正则
      const emailReg = /^[\w.-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
      if (!emailReg.test(email)) {
        this.emailError = "请输入正确的邮箱格式（如：admin@vnollx.com）";
        return false;
      }
      this.emailError = "";
      return true;
    },

    // 密码验证
    validatePassword() {
      const password = this.loginForm.password;
      if (!password) {
        this.passwordError = "请输入密码";
        return false;
      }
      if (password.length < 6 || password.length > 20) {
        this.passwordError = "密码长度需在6-20位之间";
        return false;
      }
      this.passwordError = "";
      return true;
    },

    // 清除单个字段错误
    clearError(field) {
      this[`${field}Error`] = "";
    },

    // 忘记密码处理（预留扩展）
    handleForgotPassword() {
      this.$message.info("忘记密码功能即将上线，敬请期待");
      // 后续可扩展跳转：this.$router.push("/forgot-password");
    }
  }
};
</script>

<style scoped>
/* 页面基础样式 */
.login-page {
  min-height: 100vh;
  background: linear-gradient(145deg, #f0f4f8 0%, #d9e2ec 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decor {
  position: absolute;
  border-radius: 50%;
  background: rgba(24, 144, 255, 0.08);
  z-index: 0;
}
.decor-1 {
  width: 500px;
  height: 500px;
  top: 15%;
  left: 10%;
}
.decor-2 {
  width: 600px;
  height: 600px;
  bottom: 10%;
  right: 10%;
}

/* 登录卡片 */
.login-card {
  width: 420px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
  padding: 50px 40px;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}
.login-card:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
}

/* Logo 样式 */
.login-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}
.logo-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 28px;
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.25);
}

/* 标题区域 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
}
.login-title {
  font-size: 24px;
  color: #1d2939;
  font-weight: 600;
  margin-bottom: 8px;
}
.login-subtitle {
  font-size: 14px;
  color: #667085;
  margin: 0;
}

/* 表单样式 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 表单项组 */
.form-group {
  width: 100%;
}
/* 错误状态 */
.form-group.has-error .form-input {
  border-color: #ff4d4f;
}
.form-group.has-error .input-icon {
  color: #ff4d4f;
}

/* 表单标签 */
.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #344054;
  font-weight: 500;
}

/* 输入框组 */
.input-group {
  position: relative;
  display: flex;
  align-items: center;
}
/* 输入框图标 */
.input-icon {
  position: absolute;
  left: 14px;
  color: #98a2b3;
  font-size: 16px;
  z-index: 1;
}
/* 输入框样式 */
.form-input {
  width: 100%;
  height: 46px;
  padding: 0 14px 0 44px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #1d2939;
  background-color: #f9fafb;
  transition: all 0.2s ease;
}
.form-input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.12);
  background-color: #ffffff;
}
/* 密码切换图标 */
.toggle-password {
  position: absolute;
  right: 14px;
  color: #98a2b3;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.2s ease;
}
.toggle-password:hover {
  color: #1890ff;
}

/* 错误提示 */
.error-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #ff4d4f;
  height: 16px; /* 固定高度避免布局跳动 */
}

/* 表单额外操作（记住登录/忘记密码） */
.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
/* 记住登录复选框 */
.remember-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}
.custom-checkbox {
  width: 16px;
  height: 16px;
  margin-right: 8px;
  accent-color: #1890ff;
}
.checkbox-text {
  font-size: 13px;
  color: #667085;
  transition: color 0.2s ease;
}
.remember-checkbox:hover .checkbox-text {
  color: #1890ff;
}
/* 忘记密码链接 */
.forgot-link {
  font-size: 13px;
  color: #1890ff;
  text-decoration: none;
  transition: all 0.2s ease;
}
.forgot-link:hover {
  color: #096dd9;
  text-decoration: underline;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 12px;
}
.login-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #36bffA 0%, #1890ff 100%);
  box-shadow: 0 6px 16px rgba(24, 144, 255, 0.25);
}
.login-btn:disabled {
  background: #e2e8f0;
  color: #98a2b3;
  cursor: not-allowed;
  box-shadow: none;
}

/* 加载动画 */
.loading-spinner {
  width: 20px;
  height: 20px;
  position: relative;
}
.spinner {
  width: 100%;
  height: 100%;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 底部版权 */
.login-footer {
  margin-top: 45px;
  text-align: center;
}
.login-footer p {
  font-size: 12px;
  color: #98a2b3;
}

/* 响应式适配 */
@media (max-width: 480px) {
  .login-card {
    width: 100%;
    padding: 40px 24px;
  }
  .bg-decor {
    width: 300px;
    height: 300px;
  }
  .decor-1 {
    top: 5%;
    left: 5%;
  }
  .decor-2 {
    bottom: 5%;
    right: 5%;
  }
}
</style>