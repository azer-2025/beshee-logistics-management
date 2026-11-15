<template>
  <el-aside class="modern-aside" width="240px">
    <div class="aside-container">
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          :unique-opened="true"
          background-color="#0f172a"
          text-color="#94a3b8"
          active-text-color="#ffffff"
        >
          <!-- 首页 -->
          <el-menu-item index="home" @click="menuHandler('')">
            <el-icon><HomeFilled /></el-icon>
            <span>控制台首页</span>
          </el-menu-item>

          <!-- 个人中心 -->
          <el-sub-menu index="user-center">
            <template #title>
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </template>
            <el-menu-item index="center" @click="menuHandler('center')">个人信息</el-menu-item>
            <el-menu-item index="updatePassword" @click="menuHandler('updatePassword')">修改密码</el-menu-item>
          </el-sub-menu>

          <!-- 动态菜单 -->
          <div v-for="item in filteredMenuList" :key="item.roleName">
            <el-sub-menu v-for="(menu, index) in item.backMenu" :key="menu.menu" :index="String(index)">
              <template #title>
                <el-icon><Menu /></el-icon>
                <span>{{ menu.menu }}</span>
              </template>
              <el-menu-item 
                v-for="(child, sort) in menu.child" 
                :key="sort" 
                :index="child.tableName" 
                @click="menuHandler(child.tableName)"
              >
                {{ child.menu }}
              </el-menu-item>
            </el-sub-menu>
          </div>
        </el-menu>
      </el-scrollbar>
    </div>
  </el-aside>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getCurrentInstance } from 'vue';
import { HomeFilled, User, Menu } from '@element-plus/icons-vue';
import menu from '@/utils/menu';

const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();

const menuList = ref([]);
const role = ref('');

const activeMenu = computed(() => {
  const path = route.path.replace('/', '');
  return path || 'home';
});

const filteredMenuList = computed(() => {
  return menuList.value.filter(item => role.value === item.roleName);
});

onMounted(() => {
  const menus = menu.list();
  if (menus) {
    menuList.value = menus;
  }
  role.value = proxy.$storage.get('role');
});

const menuHandler = (name) => {
  router.push('/' + name);
};
</script>

<style lang="scss" scoped>
.modern-aside {
  background-color: #0f172a;
  height: calc(100vh - 64px);
  transition: width 0.3s;
  overflow: hidden;
  box-shadow: 2px 0 8px 0 rgba(29, 35, 41, 0.05);
}

.aside-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.sidebar-menu {
  border-right: none;
  padding-top: 16px;

  :deep(.el-menu-item), :deep(.el-sub-menu__title) {
    height: 50px;
    line-height: 50px;
    margin: 4px 12px;
    border-radius: 8px;
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.05) !important;
      color: #ffffff !important;
      
      .el-icon {
        color: #ffffff !important;
      }
    }
  }

  :deep(.el-menu-item.is-active) {
    background-color: #3b82f6 !important;
    color: #ffffff !important;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
    
    .el-icon {
      color: #ffffff !important;
    }
  }

  :deep(.el-sub-menu.is-active) {
    .el-sub-menu__title {
      color: #ffffff !important;
      .el-icon {
        color: #ffffff !important;
      }
    }
  }

  .el-icon {
    font-size: 18px;
    margin-right: 12px;
    transition: color 0.3s;
  }
}

:deep(.el-menu--inline) {
  background-color: #020617 !important;
  .el-menu-item {
    margin-left: 32px !important;
    margin-right: 12px !important;
    padding-left: 12px !important;
  }
}
</style>
