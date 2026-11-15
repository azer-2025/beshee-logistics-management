<template>
  <div id="app" class="">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'


</script>

<style lang="scss">
@import "utils/theme.scss";/*引入全局颜色*/
* {
  padding: 0;
  margin: 0;
  box-sizing: border-box;
}
html,
body {
  width: 100%;
  height: 100%;
}
#app {
  height: 100%;
}
body {
  padding: 0;
  margin: 0;
}
</style>
