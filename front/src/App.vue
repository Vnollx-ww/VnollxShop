<template>
  <div id="app">
    <div class="app-shell">
      <TopBar v-if="$route.path !== '/login'" />
      <router-view/>
      <CartFab v-if="$route.path !== '/login'" />
    </div>
  </div>
</template>

<script>
import TopBar from "@/components/TopBar.vue"
import CartFab from "@/components/CartFab.vue"
export default {
  name: 'App'
  ,components: { TopBar, CartFab }
}
</script>

<style>
/* 全局浅色主题：偏浅蓝与白 */
html, body, #app {
  height: 100%;
}

body {
  margin: 0;
  background: linear-gradient(180deg, #f6fbff 0%, #ffffff 100%);
  color: #2c3e50;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
}

#app .app-shell {
  min-height: 100vh;
  padding: 16px;
}

/* 页面默认容器 */
.page {
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(24, 144, 255, 0.08), 0 2px 6px rgba(24, 144, 255, 0.06);
  padding: 16px;
}

/* 统一 Element 消息提示颜色更温和 */
.el-message--success {
  background: #f6ffed;
  border-color: #b7eb8f;
}
.el-message--error {
  background: #fff2f0;
  border-color: #ffccc7;
}
.el-message--warning {
  background: #fffbe6;
  border-color: #ffe58f;
}
.el-message--info {
  background: #e6f7ff;
  border-color: #91d5ff;
}

/* 统一按钮风格（更轻浅） */
.el-button--primary {
  background-color: #1890ff;
  border-color: #1890ff;
}
.el-button--primary:hover,
.el-button--primary:focus {
  background-color: #40a9ff;
  border-color: #40a9ff;
}
</style>
