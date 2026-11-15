// import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

// 面包屑导航，注册为全局组件
import BreadCrumbs from '@/components/common/BreadCrumbs'
import "virtual:svg-icons-register";
import svgIcon from "@/components/SvgIcon.vue";
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import '@/assets/theme-chalk/index.css' // element-ui核心样式
import '@/assets/theme-chalk/icon.css'  // element-ui图标样式
import 'element-plus/dist/index.css'
import '@/assets/css/style.scss'   //element-plus覆盖样式
import router from './router/router-static.js'
import http from './utils/http.js'
import * as echarts from 'echarts'
import base from '@/utils/base.js'
import { isAuth, getCurDate, getCurDateTime } from '@/utils/utils.js'
import storage from "@/utils/storage.js";
import * as validate from '@/utils/validate.js'
import FileUpload from "@/components/common/FileUpload";
import VueEditor  from './components/common/VueEditor.vue'
import store from './store'
import JsonExcel from 'vue-json-excel3'
import App from './App.vue'





const app = createApp(App)
app.config.globalProperties.$echarts = echarts
app.config.globalProperties.$http = http
app.config.globalProperties.$base = base.get()
app.config.globalProperties.$project = base.getProjectName()
app.config.globalProperties.$storage = storage
app.config.globalProperties.$validate = validate
app.config.globalProperties.isAuth = isAuth
app.config.globalProperties.getCurDate = getCurDate
app.config.globalProperties.getCurDateTime = getCurDateTime

app.use(ElementPlus, { locale: zhCn, size: 'small', zIndex: 3000 })

app.use(createPinia())
app.use(store)
app.use(router)

app.component('svg-icon', svgIcon);
app.component('bread-crumbs', BreadCrumbs)
app.component('file-upload', FileUpload)
app.component('editor', VueEditor)
app.component('downloadExcel', JsonExcel)





app.mount('#app')
