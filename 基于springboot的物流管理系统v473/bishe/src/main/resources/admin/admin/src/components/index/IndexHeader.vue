<template>
  <div class="modern-navbar">
    <div class="header-left">
      <div class="logo-wrapper">
        <div class="logo-box">
          <el-icon><Management /></el-icon>
        </div>
        <span class="brand-name">{{ projectTitle }}</span>
      </div>
      
      <!-- 快捷工具栏 -->
      <div class="header-tools">
        <el-tooltip content="刷新页面" placement="bottom">
          <div class="tool-btn" @click="refreshPage">
            <el-icon><Refresh /></el-icon>
          </div>
        </el-tooltip>
      </div>
    </div>

    <div class="header-right">
      <div class="user-control">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-pill">
            <el-avatar :size="32" class="pill-avatar">
              {{ adminName ? adminName.charAt(0).toUpperCase() : 'A' }}
            </el-avatar>
            <div class="pill-info">
              <span class="pill-name">{{ adminName }}</span>
              <span class="pill-role">{{ role }}</span>
            </div>
            <el-icon class="pill-arrow"><CaretBottom /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="modern-dropdown">
              <el-dropdown-item command="center">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="password">
                <el-icon><Lock /></el-icon>修改密码
              </el-dropdown-item>
              <el-dropdown-item divided command="logout" class="logout-btn">
                <el-icon><SwitchButton /></el-icon>安全退出
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getCurrentInstance } from 'vue';
import { Management, User, Lock, SwitchButton, CaretBottom, Refresh } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const router = useRouter();

const projectTitle = ref(proxy.$project.projectName);
const role = ref('');
const adminName = ref('');

onMounted(() => {
  role.value = proxy.$storage.get('role');
  adminName.value = proxy.$storage.get('adminName');
});

const handleCommand = (command) => {
  if (command === 'logout') {
    proxy.$storage.clear();
    router.replace({ name: "login" });
  } else if (command === 'center') {
    router.push('/center');
  } else if (command === 'password') {
    router.push('/updatePassword');
  }
};

const refreshPage = () => {
  window.location.reload();
};
</script>

<style lang="scss" scoped>
.modern-navbar {
  height: 64px;
  width: 100%;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-sizing: border-box;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  border-bottom: 1px solid #f1f5f9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 32px;

  .logo-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .logo-box {
      width: 32px;
      height: 32px;
      background: linear-gradient(135deg, #3b82f6, #1e40af);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 20px;
      box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
    }
    
    .brand-name {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
      letter-spacing: 0.5px;
    }
  }

  .header-tools {
    display: flex;
    align-items: center;
    border-left: 1px solid #e2e8f0;
    padding-left: 24px;
    
    .tool-btn {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 8px;
      color: #64748b;
      cursor: pointer;
      transition: all 0.2s;
      &:hover {
        background: #f1f5f9;
        color: #3b82f6;
      }
    }
  }
}

.header-right {
  .user-pill {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 4px 12px 4px 6px;
    border-radius: 30px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      background: #f1f5f9;
      border-color: #cbd5e1;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    }

    .pill-avatar {
      background: #3b82f6;
      color: #fff;
      font-weight: 700;
      border: 2px solid #fff;
    }

    .pill-info {
      display: flex;
      flex-direction: column;
      line-height: 1.2;
      
      .pill-name {
        font-size: 14px;
        font-weight: 600;
        color: #1e293b;
      }
      .pill-role {
        font-size: 11px;
        color: #94a3b8;
        font-weight: 500;
      }
    }

    .pill-arrow {
      font-size: 12px;
      color: #94a3b8;
      margin-left: 4px;
    }
  }
}

.modern-dropdown {
  padding: 8px;
  border-radius: 12px;
  min-width: 160px;
  
  :deep(.el-dropdown-menu__item) {
    padding: 10px 16px;
    border-radius: 8px;
    font-weight: 500;
    gap: 10px;
    margin: 2px 0;
    
    &:hover {
      background-color: #eff6ff !important;
      color: #3b82f6 !important;
    }
  }

  .logout-btn {
    color: #ef4444 !important;
    &:hover {
      background-color: #fef2f2 !important;
    }
  }
}
</style>
