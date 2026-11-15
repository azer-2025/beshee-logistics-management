import { createStore } from 'vuex'
import tagsView from './modules/tagsView'

// 使用 createStore 替代 new Vuex.Store
const store = createStore({
  modules: {
    tagsView  // 模块化结构保持不变
  }
})

export default store
