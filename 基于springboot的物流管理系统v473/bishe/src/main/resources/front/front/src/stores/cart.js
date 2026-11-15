import { defineStore } from 'pinia'
import api from '@/utils/request'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [],
    loading: false
  }),
  
  getters: {
    cartTotal: (state) => state.cartItems.reduce((total, item) => total + (item.buyNumber || 0), 0),
    cartAmount: (state) => state.cartItems.reduce((total, item) => total + (item.shangpinNewMoney * (item.buyNumber || 0)), 0)
  },
  
  actions: {
    async loadCart() {
      const userid = localStorage.getItem('userid')
      if (!userid) return
      
      try {
        const response = await api.get('/shangpinOrder/page', {
          params: { 
            yonghuId: userid,
            page: 1,
            limit: 100
          }
        })
        if (response.code === 0) {
          this.cartItems = response.data.list
        }
      } catch (error) {
        console.error('获取订单失败:', error)
      }
    },

    async createOrder(orderData) {
      const userid = localStorage.getItem('userid')
      if (!userid) {
        window.dispatchEvent(new CustomEvent('showLoginModal', {
          detail: { message: '请先登录' }
        }))
        return
      }

      try {
        const response = await api.post('/shangpinOrder/save', {
          ...orderData,
          yonghuId: parseInt(userid)
        })
        if (response.code === 0) {
          ElMessage.success('下单成功')
          return response
        }
      } catch (error) {
        console.error('创建订单失败:', error)
      }
    },

    async requestRefund(orderId) {
      try {
        const response = await api.post('/shangpinOrder/refund', [orderId])
        if (response.code === 0) {
          ElMessage.success('申请退款成功')
          this.loadCart()
        }
      } catch (error) {
        console.error('申请退款失败:', error)
      }
    }
  }
})