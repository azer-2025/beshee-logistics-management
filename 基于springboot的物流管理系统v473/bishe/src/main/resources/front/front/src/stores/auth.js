import { defineStore } from 'pinia'
import api from '@/utils/request'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('Token') || null,
    sessionTable: localStorage.getItem('sessionTable') || null,
    adminName: localStorage.getItem('adminName') || null,
    role: localStorage.getItem('role') || null,
    userid: localStorage.getItem('userid') || null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    currentUser: (state) => state.user,
    userRole: (state) => state.role
  },
  actions: {
    async login(loginData) {
      try {
        const response = await api.post('/yonghu/login', null, {
          params: {
            username: loginData.username,
            password: loginData.password
          }
        })
        
        if (response.code === 0) {
          this.token = response.token
          this.user = response
          this.sessionTable = 'yonghu'
          this.adminName = response.username
          this.role = '用户'

          localStorage.setItem('Token', response.token)
          localStorage.setItem('sessionTable', 'yonghu')
          localStorage.setItem('adminName', response.username)
          localStorage.setItem('role', '用户')

          // Get user session to get userid
          const sessionResponse = await api.get('/yonghu/session')
          if (sessionResponse.code === 0) {
            this.userid = sessionResponse.data.id
            localStorage.setItem('userid', sessionResponse.data.id)
          }
          

          
          ElMessage.success('登录成功')
          return response
        } else {
          throw new Error(response.msg || '登录失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
        throw error
      }
    },
    async register(registerData) {
      try {
        const response = await api.post('/yonghu/register', registerData)
        if (response.code === 0) {
          ElMessage.success('注册成功，请登录')
          return response
        } else {
          throw new Error(response.msg || '注册失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
        throw error
      }
    },
    logout() {
      this.user = null
      this.token = null
      this.sessionTable = null
      this.adminName = null
      this.role = null
      this.userid = null
      
      localStorage.removeItem('Token')
      localStorage.removeItem('sessionTable')
      localStorage.removeItem('adminName')
      localStorage.removeItem('role')
      localStorage.removeItem('userid')
      
      ElMessage.success('已退出登录')
    },
    async getUserInfo() {
      if (!this.token) return null
      try {
        const response = await api.get('/yonghu/session')
        if (response.code === 0) {
          this.user = response.data
          this.userid = response.data.id
          localStorage.setItem('userid', response.data.id)
          return response.data
        }
      } catch (error) {
        this.logout()
      }
    },

    async updateUserInfo(userData) {
      try {
        const response = await api.post('/yonghu/update', userData)
        if (response.code === 0) {
          this.user = { ...this.user, ...userData }
          return response
        } else {
          throw new Error(response.msg || '更新失败')
        }
      } catch (error) {
        throw new Error(error.message || '更新失败')
      }
    },

    async changePassword(passwordData) {
      try {
        const response = await api.get('/yonghu/updatePassword', {
          params: {
            oldPassword: passwordData.oldPassword,
            newPassword: passwordData.newPassword
          }
        })
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '密码修改失败')
        }
      } catch (error) {
        throw new Error(error.message || '密码修改失败')
      }
    },

    async uploadAvatar(formData) {
      try {
        const response = await api.post('/file/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '上传失败')
        }
      } catch (error) {
        throw new Error(error.message || '上传失败')
      }
    },

    async recharge(amount) {
      try {
        const response = await api.post('/yonghu/update', {
          newMoney: amount,
          id: localStorage.getItem('userid')
        })
        if (response.code === 0) {
          // 充值成功后自动刷新用户信息，确保余额同步到所有组件
          await this.getUserInfo()
          return response
        } else {
          throw new Error(response.msg || '充值失败')
        }
      } catch (error) {
        throw new Error(error.message || '充值失败')
      }
    },

    async getAddresses(userId) {
      try {
        const response = await api.get(`/address/page`, {
          params: {
            page: 1,
            limit: 100,
            yonghuId: userId
          }
        })
        if (response.code === 0) {
          return response.data.list
        } else {
          throw new Error(response.msg || '获取地址失败')
        }
      } catch (error) {
        throw new Error(error.message || '获取地址失败')
      }
    },

    async getUserGoods(params) {
      try {
        const response = await api.get('/huowu/page', {
          params: {
            ...params,
            yonghuId: this.userid
          }
        })
        if (response.code === 0) {
          return response.data
        } else {
          throw new Error(response.msg || '获取货物失败')
        }
      } catch (error) {
        throw new Error(error.message || '获取货物失败')
      }
    },

    async getUserOrders(params) {
      try {
        const response = await api.get('/shangpinOrder/list', {
          params: {
            ...params,
            yonghuId: this.userid
          }
        })
        if (response.code === 0) {
          return response.data
        } else {
          throw new Error(response.msg || '获取订单失败')
        }
      } catch (error) {
        throw new Error(error.message || '获取订单失败')
      }
    },

    async getUserAlerts(params = {}) {
      try {
        const response = await api.get('/jiankongyujing/page', {
          params: {
            page: params.page || 1,
            limit: params.limit || 10,
            sort: params.sort || 'id',
            order: params.order || 'desc',
            ...params
          }
        })
        if (response.code === 0) {
          return response.data
        } else {
          throw new Error(response.msg || '获取预警失败')
        }
      } catch (error) {
        throw new Error(error.message || '获取预警失败')
      }
    },

    async addAddress(addressData) {
      try {
        const response = await api.post('/address/save', addressData)
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '添加地址失败')
        }
      } catch (error) {
        throw new Error(error.message || '添加地址失败')
      }
    },

    async updateAddress(addressData) {
      try {
        const response = await api.post('/address/update', addressData)
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '更新地址失败')
        }
      } catch (error) {
        throw new Error(error.message || '更新地址失败')
      }
    },

    async deleteAddress(addressId) {
      try {
        const response = await api.post('/address/delete', [addressId])
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '删除地址失败')
        }
      } catch (error) {
        throw new Error(error.message || '删除地址失败')
      }
    },

    async addGoods(goodsData) {
      try {
        const response = await api.post('/huowu/save', goodsData)
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '添加货物失败')
        }
      } catch (error) {
        throw new Error(error.message || '添加货物失败')
      }
    },

    async updateGoods(goodsData) {
      try {
        const response = await api.post('/huowu/update', goodsData)
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '更新货物失败')
        }
      } catch (error) {
        throw new Error(error.message || '更新货物失败')
      }
    },

    async deleteGoods(goodsId) {
      try {
        const response = await api.post('/huowu/delete', [goodsId])
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '删除货物失败')
        }
      } catch (error) {
        throw new Error(error.message || '删除货物失败')
      }
    },

    async payOrder(orderId) {
      try {
        const response = await api.post('/shangpinOrder/pay', { id: orderId })
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '支付失败')
        }
      } catch (error) {
        throw new Error(error.message || '支付失败')
      }
    },

    async confirmReceipt(orderId) {
      try {
        const response = await api.post('/shangpinOrder/receipt', { id: orderId })
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '确认收货失败')
        }
      } catch (error) {
        throw new Error(error.message || '确认收货失败')
      }
    },

    async cancelOrder(orderId) {
      try {
        const response = await api.post('/shangpinOrder/cancel', { id: orderId })
        if (response.code === 0) {
          return response
        } else {
          throw new Error(response.msg || '取消订单失败')
        }
      } catch (error) {
        throw new Error(error.message || '取消订单失败')
      }
    },
    async createOrder(orderData) {
      try {
        const response = await api.post('shangpinOrder/add', orderData)
        return response
      } catch (error) {
        throw new Error(error.message || '创建订单失败')
      }
    },
    // 添加字典数据获取方法
    async getDictionaryPage(params) {
      try {
        const response = await api.get('/dictionary/page', {
          params: {
            page: params.page || 1,
            limit: params.limit || 10,
            sort: params.sort || '',
            order: params.order || '',
            dicCode: params.dicCode || '',
            ...params
          }
        })
        if (response.code === 0) {
          return response.data
        } else {
          throw new Error(response.msg || '获取字典数据失败')
        }
      } catch (error) {
        throw new Error(error.message || '获取字典数据失败')
      }
    },
    // 在 auth.js 的 actions 部分添加以下方法
    async getOrderStatusTypes() {
      try {
        const response = await api.get('/dictionary/page', {
          params: {
            page: 1,
            limit: 100,
            dicCode: 'shangpin_order_types'
          }
        })
        if (response.code === 0) {
          return response.data
        } else {
          throw new Error(response.msg || '获取订单状态失败')
        }
      } catch (error) {
        throw new Error(error.message || '获取订单状态失败')
      }
    },


// 添加获取货物司机信息的方法
    async getHuowuInfo(id){
      try {
        const response = await api.get(
           `/huowu/info2/${id}`
        )
        return response
      } catch (error) {
        console.error('获取货物信息失败:', error)
        throw error
      }
    }





  }
})
