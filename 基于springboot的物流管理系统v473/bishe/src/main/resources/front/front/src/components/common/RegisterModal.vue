<template>
  <el-dialog
      v-model="modalStore.registerModalVisible"
      title="注册"
      width="400px"
      :close-on-click-modal="false"
      @close="handleClose"
  >
    <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
    >
      <el-form-item label="账户" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入账户" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="password2">
        <el-input
            v-model="registerForm.password2"
            type="password"
            placeholder="请确认密码"
            show-password
        />
      </el-form-item>

      <el-form-item label="姓名" prop="yonghuName">
        <el-input v-model="registerForm.yonghuName" placeholder="请输入用户姓名" />
      </el-form-item>

      <el-form-item label="手机" prop="yonghuPhone">
        <el-input v-model="registerForm.yonghuPhone" placeholder="请输入用户手机号" />
      </el-form-item>

      <el-form-item label="身份证" prop="yonghuIdNumber">
        <el-input v-model="registerForm.yonghuIdNumber" placeholder="请输入用户身份证号" />
      </el-form-item>

      <el-form-item label="性别" prop="sexTypes">
        <el-select v-model="registerForm.sexTypes" placeholder="请选择性别">
          <el-option
              v-for="item in sexTypesList"
              :key="item.codeIndex"
              :label="item.indexName"
              :value="item.codeIndex"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="邮箱" prop="yonghuEmail">
        <el-input v-model="registerForm.yonghuEmail" placeholder="请输入用户邮箱" />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="modalStore.hideRegisterModal">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleRegister">
          注册
        </el-button>
      </div>
    </template>

    <div class="login-link">
      <span>已有账号？</span>
      <el-link type="primary" @click="switchToLogin">立即登录</el-link>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useModalStore } from '@/stores/modal'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const modalStore = useModalStore()
const registerFormRef = ref()
const loading = ref(false)
const sexTypesList = ref([])

const registerForm = reactive({
  username: '',
  password: '',
  password2: '',
  yonghuName: '',
  yonghuPhone: '',
  yonghuIdNumber: '',
  sexTypes: '',
  yonghuEmail: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入账户', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  password2: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  yonghuName: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' }
  ],
  yonghuPhone: [
    { required: true, message: '请输入用户手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  yonghuIdNumber: [
    { required: true, message: '请输入用户身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  sexTypes: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  yonghuEmail: [
    { required: true, message: '请输入用户邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 构造提交数据，移除确认密码字段
        const submitData = {
          username: registerForm.username,
          password: registerForm.password,
          yonghuName: registerForm.yonghuName,
          yonghuPhone: registerForm.yonghuPhone,
          yonghuIdNumber: registerForm.yonghuIdNumber,
          sexTypes: registerForm.sexTypes,
          yonghuEmail: registerForm.yonghuEmail
        }

        await authStore.register(submitData)
        modalStore.hideRegisterModal()
        registerFormRef.value.resetFields()
      } catch (error) {
        // Error handled in auth store
      } finally {
        loading.value = false
      }
    }
  })
}

const handleClose = () => {
  registerFormRef.value?.resetFields()
}

const switchToLogin = () => {
  modalStore.hideRegisterModal()
  modalStore.showLoginModal()
}

// 获取性别字典数据
const loadSexTypes = async () => {
  try {
    const response = await authStore.getDictionaryPage({
      page: 1,
      limit: 100,
      dicCode: 'sex_types'
    })
    sexTypesList.value = response.list || []
  } catch (error) {
    ElMessage.error('加载性别数据失败')
  }
}

onMounted(() => {
  loadSexTypes()
})
</script>

<style scoped>
.login-link {
  text-align: center;
  margin-top: 10px;
}
</style>
