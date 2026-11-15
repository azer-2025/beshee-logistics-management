<template>
  <el-dialog
    v-model="modalStore.loginModalVisible"
    title="登录"
    width="400px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      label-width="60px"
    >
      <el-form-item label="账号" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入账号" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          show-password
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="modalStore.hideLoginModal">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </div>
    </template>
    
    <div class="register-link">
      <span>还没有账号？</span>
      <el-link type="primary" @click="switchToRegister">立即注册</el-link>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useModalStore } from '@/stores/modal'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const modalStore = useModalStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await authStore.login(loginForm)
        modalStore.hideLoginModal()
        loginFormRef.value.resetFields()
      } catch (error) {
        // Error handled in auth store
      } finally {
        loading.value = false
      }
    }
  })
}

const handleClose = () => {
  loginFormRef.value?.resetFields()
}

const switchToRegister = () => {
  modalStore.hideLoginModal()
  modalStore.showRegisterModal()
}
</script>

<style scoped>
.register-link {
  text-align: center;
  margin-top: 10px;
}
</style>