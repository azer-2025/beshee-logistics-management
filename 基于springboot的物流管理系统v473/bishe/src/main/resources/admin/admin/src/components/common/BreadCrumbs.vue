<template>
  <div class="breadcrumb-wrapper">
    <el-breadcrumb class="modern-breadcrumb" separator="/">
      <transition-group name="breadcrumb-list">
        <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
          <span v-if="index === levelList.length - 1" class="no-redirect">{{ item.name }}</span>
          <a v-else @click.prevent="handleLink(item)" class="redirect-link">{{ item.name }}</a>
        </el-breadcrumb-item>
      </transition-group>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const levelList = ref([])

const getBreadcrumb = () => {
  // 获取当前匹配的所有路由记录
  let matched = route.matched.filter(item => item.name || (item.meta && item.meta.title))
  
  // 转换数据结构，优先使用 meta.title，其次使用 name
  let breadcrumbs = matched.map(item => ({
    path: item.path,
    name: (item.meta && item.meta.title) ? item.meta.title : item.name,
    redirect: item.redirect
  })).filter(item => item.name && item.name !== '系统首页') // 过滤掉父级的冗余名称

  // 如果不是首页，手动在最前面添加“首页”
  if (route.path !== '/index' && route.path !== '/') {
    breadcrumbs = [{ path: '/index', name: '系统首页' }].concat(breadcrumbs)
  } else {
    // 如果是首页，确保只显示一个“首页”
    breadcrumbs = [{ path: '/index', name: '系统首页' }]
  }

  levelList.value = breadcrumbs
}

const handleLink = (item) => {
  const { redirect, path } = item
  if (redirect) {
    router.push(redirect)
    return
  }
  router.push(path)
}

watch(() => route.path, () => {
  getBreadcrumb()
})

onMounted(() => {
  getBreadcrumb()
})
</script>

<style lang="scss" scoped>
.breadcrumb-wrapper {
  padding: 0;
  margin-bottom: 0;
}

.modern-breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
  line-height: 1;
  padding: 0;
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;

  :deep(.el-breadcrumb__inner) {
    display: flex;
    align-items: center;
    
    .no-redirect {
      color: #1e293b;
      font-weight: 700;
      cursor: default;
    }

    .redirect-link {
      color: #64748b;
      font-weight: 500;
      text-decoration: none;
      transition: color 0.2s;

      &:hover {
        color: #3b82f6;
      }
    }
  }

  :deep(.el-breadcrumb__separator) {
    color: #cbd5e1;
    margin: 0 12px;
    font-weight: 400;
  }
}

/* 动效 */
.breadcrumb-list-enter-active,
.breadcrumb-list-leave-active {
  transition: all .3s;
}

.breadcrumb-list-enter-from,
.breadcrumb-list-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.breadcrumb-list-move {
  transition: all .3s;
}

.breadcrumb-list-leave-active {
  position: absolute;
}
</style>
