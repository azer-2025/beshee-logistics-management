<template>
  <header class="modern-header" :class="{ scrolled: isScrolled }">
    <div class="header-container">
      <div class="logo-area">
        <el-icon class="logo-icon"><Stopwatch /></el-icon>
        <span class="logo-text">生鲜冷链物流监控与预警系统</span>
      </div>

      <nav class="nav-menu">
        <router-link to="/" class="nav-item">
          <el-icon><House /></el-icon>首页
        </router-link>
        <router-link to="/cangku" class="nav-item">
          <el-icon><OfficeBuilding /></el-icon>仓库
        </router-link>
        <router-link to="/gonggao" class="nav-item">
          <el-icon><Bell /></el-icon>公告
        </router-link>
        <router-link to="/shangpin" class="nav-item">
          <el-icon><Goods /></el-icon>商品
        </router-link>
      </nav>

      <div class="user-actions">
        <a href="#" class="action-btn" @click.prevent="openChatWindow">
          <el-icon><ChatDotRound /></el-icon>客服
        </a>

        <template v-if="authStore.isLoggedIn">
          <a href="#" class="action-btn alert-btn" @click.prevent="goAlerts">
            <el-badge :hidden="activeAlertCount <= 0" :value="activeAlertCount" :max="99">
              <span class="badge-trigger">
                <el-icon><Warning /></el-icon>预警
              </span>
            </el-badge>
          </a>

          <el-dropdown @command="handleUserCommand" trigger="click">
            <div class="user-profile">
              <el-avatar :size="32" class="avatar">
                {{ authStore.adminName ? authStore.adminName.charAt(0).toUpperCase() : 'U' }}
              </el-avatar>
              <span class="username">{{ authStore.adminName }}</span>
              <el-icon class="dropdown-icon"><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown">
                <el-dropdown-item command="user">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><List /></el-icon>我的订单
                </el-dropdown-item>
                <el-dropdown-item command="alerts">
                  <el-icon><Warning /></el-icon>预警消息
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" class="logout-item">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button type="primary" round class="login-btn" @click="showLoginModal">
            登录 / 注册
          </el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useModalStore } from '@/stores/modal'

const router = useRouter()
const authStore = useAuthStore()
const modalStore = useModalStore()
const isScrolled = ref(false)
const activeAlertCount = ref(0)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const showLoginModal = () => {
  modalStore.showLoginModal('请先登录')
}

const loadAlertCount = async () => {
  if (!authStore.isLoggedIn) {
    activeAlertCount.value = 0
    return
  }
  try {
    const data = await authStore.getUserAlerts({
      page: 1,
      limit: 50,
      sort: 'id',
      order: 'desc'
    })
    activeAlertCount.value = (data.list || []).filter((item) => [1, 2].includes(Number(item.yujingStatusTypes))).length
  } catch (error) {
    activeAlertCount.value = 0
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'user':
      router.push('/user')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'alerts':
      router.push('/user/alerts')
      break
    case 'logout':
      authStore.logout()
      router.push('/')
      break
  }
}

const goAlerts = () => {
  router.push('/user/alerts')
}

const openChatWindow = () => {
  window.dispatchEvent(new CustomEvent('openChatWindow'))
}

onMounted(() => {
  authStore.getUserInfo().then(loadAlertCount)
  window.addEventListener('scroll', handleScroll)
})

watch(() => authStore.isLoggedIn, () => {
  loadAlertCount()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.modern-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modern-header.scrolled {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
  color: #3b82f6;
}

.logo-text {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #1e3a8a, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 1px;
}

.nav-menu {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  color: #475569;
  font-weight: 600;
  font-size: 15px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.nav-item:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
}

.nav-item.router-link-active {
  color: #fff;
  background: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #64748b;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.action-btn:hover {
  color: #3b82f6;
}

.badge-trigger {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.alert-btn :deep(.el-badge__content) {
  border: none;
}

.login-btn {
  padding: 8px 24px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 12px 4px 4px;
  border-radius: 30px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s;
}

.user-profile:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.avatar {
  background: #3b82f6;
  color: white;
  font-weight: bold;
}

.username {
  font-weight: 600;
  color: #334155;
  font-size: 14px;
}

.dropdown-icon {
  font-size: 12px;
  color: #94a3b8;
}

.logout-item {
  color: #ef4444 !important;
}

.logout-item:hover {
  background-color: #fef2f2 !important;
  color: #dc2626 !important;
}

@media (max-width: 900px) {
  .logo-text {
    display: none;
  }

  .nav-item {
    padding: 8px 12px;
    font-size: 14px;
  }
}
</style>
