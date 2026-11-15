<template>
  <el-main class="modern-main">
    <div class="main-container">
      <!-- 顶部面包屑与标题 -->
      <div class="page-header-wrapper">
        <bread-crumbs :title="title" class="custom-breadcrumbs"></bread-crumbs>
      </div>

      <!-- 内容区 -->
      <div class="content-body">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </el-main>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const title = ref('');

// 监听路由变化更新标题（这里可以根据实际需求调整）
onMounted(() => {
  // 可以在这里通过 route.meta 或者其他方式获取标题
});
</script>

<style lang="scss" scoped>
.modern-main {
  background-color: #f1f5f9;
  padding: 0 !important;
  height: calc(100vh - 64px);
  overflow-y: auto;
  overflow-x: hidden;
}

.main-container {
  padding: 24px;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header-wrapper {
  margin-bottom: 20px;
  background: transparent;
}

.content-body {
  flex: 1;
  // 这里不再强行给白色背景，因为子组件内部通常有自己的 el-card 或 table-content
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

:deep(.custom-breadcrumbs) {
  padding: 0;
  margin: 0;
  background: transparent !important;
  
  .el-breadcrumb__item {
    font-size: 14px;
    .el-breadcrumb__inner {
      color: #64748b;
      font-weight: 500;
      &.is-link:hover {
        color: #3b82f6;
      }
    }
    &:last-child .el-breadcrumb__inner {
      color: #1e293b;
      font-weight: 600;
    }
  }
}
</style>
