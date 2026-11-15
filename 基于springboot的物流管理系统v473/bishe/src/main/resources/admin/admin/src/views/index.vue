<template>
  <el-container class="admin-layout">
    <el-header height="64px" class="admin-header">
      <IndexHeader />
    </el-header>
    <el-container class="admin-body">
      <IndexAside />
      <IndexMain />
    </el-container>
  </el-container>
</template>

<script setup>
import { defineAsyncComponent } from 'vue';
import IndexHeader from '@/components/index/IndexHeader.vue';

const IndexAside = defineAsyncComponent(() =>
  import('@/components/index/IndexAsideStatic.vue')
);
const IndexMain = defineAsyncComponent(() =>
  import('@/components/index/IndexMain.vue')
);
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-header {
  padding: 0;
  z-index: 1001;
  background: #fff;
}

.admin-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}
</style>
