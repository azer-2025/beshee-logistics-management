<template>
  <svg aria-hidden="true" class="svg-icon" :style="{height:size,width:size}">
    <use :xlink:href="symbolId" :fill="color"></use>
  </svg>
</template>

<script setup >
import {computed} from 'vue'
const props = defineProps({
  //图标大小
  size:{
    type:[Number,String],
    default: '16px'
  },
  //图标颜色
  color:{
    type:String,
    default:"#606266"
  },
  //图标前缀，默认icon
  prefix:{
    type:String,
    default:'icon'
  },
  //图标名称
  iconClass:{
    type:String,
    default:""
  }
});
// 注意： symbolId 的值需要与 vite.config.ts中配置的 symbolId 保持一致。 最终symbolId的值格式为#icon-svgName。
const symbolId = computed(()=>`#${props.prefix}-${props.iconClass}`);
</script>

<style scoped lang="scss">

</style>
