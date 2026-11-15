import { defineStore } from 'pinia'

export const useModalStore = defineStore('modal', {
  state: () => ({
    loginModalVisible: false,
    registerModalVisible: false,
    modalMessage: ''
  }),
  
  actions: {
    showLoginModal(message = '请先登录') {
      this.modalMessage = message
      this.loginModalVisible = true
    },
    
    hideLoginModal() {
      this.loginModalVisible = false
    },
    
    showRegisterModal() {
      this.registerModalVisible = true
    },
    
    hideRegisterModal() {
      this.registerModalVisible = false
    }
  }
})