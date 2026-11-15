<template>
  <div id="app">
    <HeaderNav />

    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <footer class="modern-footer">
      <div class="footer-container">
        <div class="footer-brand">
          <div class="footer-logo">
            <el-icon><Stopwatch /></el-icon>
            <span>生鲜冷链物流监控系统</span>
          </div>
          <p class="footer-desc">提供全方位、高标准、智能化的冷链物流监控解决方案，保障生鲜品质，让新鲜触手可及。</p>
        </div>
        <div class="footer-links">
          <div class="link-group">
            <h4>快速链接</h4>
            <router-link to="/">系统首页</router-link>
            <router-link to="/cangku">仓储资源</router-link>
            <router-link to="/shangpin">商品中心</router-link>
          </div>
          <div class="link-group">
            <h4>支持与服务</h4>
            <router-link to="/gonggao">系统公告</router-link>
            <a href="#" @click.prevent="openChatWindow">在线客服</a>
            <router-link to="/user">个人中心</router-link>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>生鲜冷链物流监控与预警系统 © 2026 | 用心服务每一位客户</p>
      </div>
    </footer>

    <LoginModal />
    <RegisterModal />
    <ChatWindow />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import HeaderNav from '@/components/common/HeaderNav.vue'
import LoginModal from '@/components/common/LoginModal.vue'
import RegisterModal from '@/components/common/RegisterModal.vue'
import ChatWindow from '@/components/common/ChatWindow.vue'
import { useModalStore } from '@/stores/modal'

const authStore = useAuthStore()
const modalStore = useModalStore()

const openChatWindow = () => {
  window.dispatchEvent(new CustomEvent('openChatWindow'))
}

onMounted(() => {
  window.addEventListener('showLoginModal', (event) => {
    modalStore.showLoginModal(event.detail.message)
  })

  authStore.getUserInfo()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f8fafc;
  color: #334155;
  -webkit-font-smoothing: antialiased;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  background: #f8fafc;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 现代化页脚 */
.modern-footer {
  background: #0f172a;
  color: #94a3b8;
  padding-top: 60px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 40px;
}

.footer-brand {
  max-width: 400px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 20px;
}

.footer-logo .el-icon {
  color: #3b82f6;
  font-size: 24px;
}

.footer-desc {
  line-height: 1.8;
  font-size: 14px;
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group h4 {
  color: #f8fafc;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
}

.link-group a {
  display: block;
  color: #94a3b8;
  text-decoration: none;
  margin-bottom: 12px;
  font-size: 14px;
  transition: color 0.3s, transform 0.3s;
}

.link-group a:hover {
  color: #3b82f6;
  transform: translateX(5px);
}

.footer-bottom {
  border-top: 1px solid rgba(255,255,255,0.1);
  text-align: center;
  padding: 20px;
  font-size: 14px;
}

/* 全局 Element Plus 样式覆盖，提升现代感 */
.el-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.el-button {
  border-radius: 8px;
  font-weight: 500;
}

.el-button--primary {
  background-color: #3b82f6 !important;
  border-color: #3b82f6 !important;
}

.el-button--primary:hover {
  background-color: #2563eb !important;
  border-color: #2563eb !important;
}

.el-input__wrapper {
  border-radius: 8px !important;
  box-shadow: 0 0 0 1px #e2e8f0 inset !important;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #3b82f6 inset !important;
}

.el-card {
  border-radius: 12px !important;
  border: 1px solid #f1f5f9 !important;
}

@media (max-width: 768px) {
  .footer-links {
    flex-direction: column;
    gap: 40px;
  }
}
</style>
