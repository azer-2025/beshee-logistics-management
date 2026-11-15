import { defineStore } from 'pinia'
import api from '@/utils/request'

export const useWarehouseStore = defineStore('warehouse', {
  state: () => ({
    warehouses: [],
    loading: false,
    pagination: {
      current: 1,
      size: 10,
      total: 0
    },
    currentWarehouse: null,
    warehouseTypes: [],
    warehouseProducts: []
  }),
  
  actions: {
    async fetchWarehouses(params = {}) {
      this.loading = true
      try {
        const response = await api.get('/cangku/page', {
          params: {
            page: this.pagination.current,
            limit: this.pagination.size,
            ...params
          }
        })
        if (response.code === 0) {
          this.warehouses = response.data.list
          this.pagination.total = response.data.total
        }
      } catch (error) {
        console.error('获取仓库失败:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchWarehouseDetail(id) {
      try {
        const response = await api.get(`/cangku/info/${id}`)
        if (response.code === 0) {
          this.currentWarehouse = response.data
          return response.data
        }
      } catch (error) {
        console.error('获取仓库详情失败:', error)
      }
    },

    async fetchWarehouseTypes() {
      try {
        const response = await api.get('/dictionary/page', {
          params: {
            dicCode: 'cangku_types',
            page: 1,
            limit: 100
          }
        })
        if (response.code === 0) {
          this.warehouseTypes = response.data.list
        }
      } catch (error) {
        console.error('获取仓库类型失败:', error)
      }
    },

    async fetchWarehouseProducts(warehouseId) {
      try {
        const response = await api.get('/shangpin/page', {
          params: {
            cangkuId: warehouseId,
            page: 1,
            limit: 100
          }
        })
        if (response.code === 0) {
          this.warehouseProducts = response.data.list
        }
      } catch (error) {
        console.error('获取仓库商品失败:', error)
      }
    }
  }
})