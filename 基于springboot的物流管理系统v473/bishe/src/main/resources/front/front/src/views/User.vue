<template>
  <div class="user-container">
    <div class="user-layout">
      <div class="user-sidebar">
        <div class="user-info">
          <div class="avatar">
            <img
              :src="userInfo?.yonghuPhoto ? getImageUrl(userInfo.yonghuPhoto) : 'https://via.placeholder.com/80x80?text=用户'"
              alt="用户头像"
            />
          </div>
          <h3>{{ userInfo?.yonghuName || '用户' }}</h3>
          <p>余额: ¥{{ userInfo?.newMoney || 0 }}</p>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="user-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>

          <el-menu-item index="address">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>

          <el-menu-item index="goods">
            <el-icon><Box /></el-icon>
            <span>货物管理</span>
          </el-menu-item>

          <el-menu-item index="collection">
            <el-icon><Star /></el-icon>
            <span>商品收藏</span>
          </el-menu-item>

          <el-menu-item index="orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>商品订单</span>
          </el-menu-item>

          <el-menu-item index="alerts">
            <el-icon><Bell /></el-icon>
            <span>预警消息</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="user-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeMenu = ref('profile')
const userInfo = computed(() => authStore.user)

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://via.placeholder.com/80x80?text=用户'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const loadUserInfo = async () => {
  await authStore.getUserInfo()
}

const handleMenuSelect = (index) => {
  activeMenu.value = index

  const routes = {
    profile: '/user',
    address: '/user/address',
    goods: '/user/huowu',
    collection: '/user/collection',
    orders: '/user/orders',
    alerts: '/user/alerts'
  }

  if (routes[index]) {
    router.push(routes[index])
  }
}

const updateActiveMenu = () => {
  const path = route.path
  if (path === '/user') {
    activeMenu.value = 'profile'
  } else if (path.includes('/address')) {
    activeMenu.value = 'address'
  } else if (path.includes('/huowu')) {
    activeMenu.value = 'goods'
  } else if (path.includes('/collection')) {
    activeMenu.value = 'collection'
  } else if (path.includes('/orders')) {
    activeMenu.value = 'orders'
  } else if (path.includes('/alerts')) {
    activeMenu.value = 'alerts'
  }
}

onMounted(() => {
  loadUserInfo()
  updateActiveMenu()
})

watch(route, updateActiveMenu)
</script>

<style scoped>
.user-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.user-layout {
  display: flex;
  gap: 20px;
  min-height: 600px;
}

.user-sidebar {
  width: 250px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.user-info {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 10px;
}

.user-info h3 {
  margin: 10px 0 5px 0;
  color: #333;
}

.user-info p {
  color: #666;
  font-size: 14px;
}

.user-menu {
  border: none;
}

.user-content {
  flex: 1;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

@media (max-width: 768px) {
  .user-container {
    padding: 10px;
  }

  .user-layout {
    flex-direction: column;
  }

  .user-sidebar {
    width: 100%;
    margin-bottom: 20px;
  }

  .user-menu {
    display: flex;
    overflow-x: auto;
    white-space: nowrap;
  }

  .user-menu .el-menu-item {
    display: inline-block;
    padding: 0 15px;
  }
}
</style>
