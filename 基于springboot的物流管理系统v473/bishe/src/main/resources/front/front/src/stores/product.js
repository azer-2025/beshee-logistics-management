import { defineStore } from 'pinia'
import api from '@/utils/request'

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    loading: false,
    pagination: {
      current: 1,
      size: 10,
      total: 0
    },
    currentProduct: null,
    productTypes: [],
    // 添加收藏商品列表状态
    collectedProducts: []
  }),
  
  actions: {
    async fetchProducts(params = {}) {
      this.loading = true
      try {
        const response = await api.get('/shangpin/page', {
          params: {
            page: this.pagination.current,
            limit: this.pagination.size,
            ...params
          }
        })
        if (response.code === 0) {
          this.products = response.data.list
          this.pagination.total = response.data.total
        }
      } catch (error) {
        console.error('获取商品失败:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchProductDetail(id) {
      try {
        const response = await api.get(`/shangpin/info/${id}`)
        if (response.code === 0) {
          this.currentProduct = response.data
          return response.data
        }
      } catch (error) {
        console.error('获取商品详情失败:', error)
      }
    },

    async fetchProductTypes() {
      try {
        const response = await api.get('/dictionary/page', {
          params: {
            dicCode: 'shangpin_types',
            page: 1,
            limit: 100
          }
        })
        if (response.code === 0) {
          this.productTypes = response.data.list
        }
      } catch (error) {
        console.error('获取商品类型失败:', error)
      }
    },

    async collectProduct(productId) {
      const userid = localStorage.getItem('userid')
      if (!userid) {
        window.dispatchEvent(new CustomEvent('showLoginModal', {
          detail: { message: '请先登录' }
        }))
        return
      }

      try {
        const response = await api.post('/shangpinCollection/save', {
          shangpinId: productId,
          yonghuId: parseInt(userid)
        })

        // 收藏成功后更新 collectedProducts 状态
        if (response.code === 0) {
          await this.fetchFavorites()
        }


        return response
      } catch (error) {
        console.error('收藏商品失败:', error)
      }
    },

    async fetchFavorites(params = {}) {
      const userid = localStorage.getItem('userid')
      if (!userid) return []

      try {
        const response = await api.get('/shangpinCollection/page', {
          params: {
            page: 1,
            limit: 100,
            yonghuId: parseInt(userid),
            ...params
          }
        })
        if (response.code === 0) {
          // 更新 collectedProducts 状态
          this.collectedProducts = response.data.list
          return response.data
        }
        return { list: [], total: 0 }
      } catch (error) {
        console.error('获取收藏失败:', error)
        return { list: [], total: 0 }
      }
    },

    async removeFavorite(id) {
      try {
        const response = await api.post('/shangpinCollection/delete', [id])

        // 删除成功后更新 collectedProducts 状态
        if (response.code === 0) {
          await this.fetchFavorites()
        }

        return response
      } catch (error) {
        console.error('取消收藏失败:', error)
      }
    },

    async clearAllFavorites() {
      const userid = localStorage.getItem('userid')
      if (!userid) return

      try {
        // 获取所有收藏ID
        const response = await this.fetchFavorites()
        const ids = response.list.map(item => item.id)
        
        if (ids.length > 0) {
          await api.post('/shangpinCollection/delete', ids)
        }
      } catch (error) {
        console.error('清空收藏失败:', error)
      }
    }
  }
})