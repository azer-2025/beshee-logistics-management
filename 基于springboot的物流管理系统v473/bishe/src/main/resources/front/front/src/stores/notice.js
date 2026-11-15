import { defineStore } from 'pinia'
import api from '@/utils/request'

export const useNoticeStore = defineStore('notice', {
  state: () => ({
    notices: [],
    loading: false,
    pagination: {
      current: 1,
      size: 10,
      total: 0
    },
    currentNotice: null,
    noticeTypes: []
  }),
  
  actions: {
    async fetchNotices(params = {}) {
      this.loading = true
      try {
        const response = await api.get('/gonggao/page', {
          params: {
            page: this.pagination.current,
            limit: this.pagination.size,
            ...params
          }
        })
        if (response.code === 0) {
          this.notices = response.data.list
          this.pagination.total = response.data.total
        }
      } catch (error) {
        console.error('获取公告失败:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchNoticeDetail(id) {
      try {
        const response = await api.get(`/gonggao/info/${id}`)
        if (response.code === 0) {
          this.currentNotice = response.data
          return response.data
        }
      } catch (error) {
        console.error('获取公告详情失败:', error)
      }
    },

    async fetchNoticeTypes() {
      try {
        const response = await api.get('/dictionary/page', {
          params: {
            dicCode: 'gonggao_types',
            page: 1,
            limit: 100
          }
        })
        if (response.code === 0) {
          this.noticeTypes = response.data.list
        }
      } catch (error) {
        console.error('获取公告类型失败:', error)
      }
    }
  }
})