<template>
  <div class="user-profile">
    <div class="profile-header">
      <h3>基本资料</h3>
    </div>

    <div class="profile-content">
      <div class="avatar-section">
        <div class="avatar-upload">
          <img
            :src="userInfo?.yonghuPhoto ? getImageUrl(userInfo.yonghuPhoto) : 'https://via.placeholder.com/100x100?text=用户'"
            alt="用户头像"
            class="avatar-image"
          />
          <div class="upload-overlay" @click="triggerFileUpload">
            <el-icon><Camera /></el-icon>
            <span>上传头像</span>
          </div>
        </div>
        
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarUpload"
        />
      </div>

      <div class="info-section">
        <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          label-width="120px"
          class="user-form"
        >
          <el-form-item label="用户编号">
            <el-input v-model="userForm.id" disabled />
          </el-form-item>

          <el-form-item label="用户姓名" prop="yonghuName">
            <el-input v-model="userForm.yonghuName" />
          </el-form-item>

          <el-form-item label="用户手机号" prop="yonghuPhone">
            <el-input v-model="userForm.yonghuPhone" />
          </el-form-item>

          <el-form-item label="用户身份证号" prop="yonghuIdNumber">
            <el-input v-model="userForm.yonghuIdNumber" />
          </el-form-item>

          <el-form-item label="性别" prop="sexTypes">
            <el-select v-model="userForm.sexTypes" placeholder="请选择性别">
              <el-option label="男" :value="1" />
              <el-option label="女" :value="2" />
            </el-select>
          </el-form-item>

          <el-form-item label="电子邮箱" prop="yonghuEmail">
            <el-input v-model="userForm.yonghuEmail" />
          </el-form-item>

          <el-form-item label="当前余额">
            <div class="balance-display">
              <span class="balance-amount">¥{{ userInfo?.newMoney || 0 }}</span>
              <el-button type="primary" @click="showRechargeDialog = true">点我充值</el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="updateUserInfo" :loading="saving">
              保存修改
            </el-button>
            <el-button @click="showPasswordDialog = true">修改密码</el-button>
            <el-button type="danger" @click="logout">退出登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 充值对话框 -->
    <el-dialog v-model="showRechargeDialog" title="用户充值" width="400px">
      <el-form
        ref="rechargeFormRef"
        :model="rechargeForm"
        :rules="rechargeRules"
        label-width="80px"
      >
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="10000"
            :precision="2"
            :step="100"
            placeholder="请输入充值金额"
          />
        </el-form-item>

        <el-form-item label="支付方式" prop="paymentType">
          <el-radio-group v-model="rechargeForm.paymentType">
            <el-radio :value="1">
              <div class="payment-method-label">
                <img :src="wechatIcon" alt="微信" class="payment-method-icon" />
                <span>微信支付</span>
              </div>
            </el-radio>
            <el-radio :value="2">
              <div class="payment-method-label">
                <img :src="zhifubaoIcon" alt="支付宝" class="payment-method-icon" />
                <span>支付宝支付</span>
              </div>
            </el-radio>
            <el-radio :value="3">
              <div class="payment-method-label">
                <img :src="yinlianIcon" alt="银行卡" class="payment-method-icon" />
                <span>银行卡支付</span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showRechargeDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="recharging">
          确认支付
        </el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入旧密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">
          确定修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
const fileInput = ref(null)
const authStore = useAuthStore()

// 使用计算属性直接引用store中的响应式用户数据
const userInfo = computed(() => authStore.user)
const userForm = ref({})
const saving = ref(false)
const recharging = ref(false)
const changingPassword = ref(false)
const showRechargeDialog = ref(false)
const showPasswordDialog = ref(false)

import zhifubaoIcon from '@/assets/img/zhifubao.png'
import wechatIcon from '@/assets/img/wechat.png'
import yinlianIcon from '@/assets/img/yinlian.png'


const rechargeForm = ref({
  amount: 100,
  paymentType: 1
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const userRules = {
  yonghuName: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  yonghuPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  yonghuIdNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  yonghuEmail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  sexTypes: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
}

const rechargeRules = {
  amount: [
    { required: true, type: 'number', min: 1, max: 10000, message: '充值金额必须在1-10000之间', trigger: 'blur' }
  ],
  paymentType: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

function validateConfirmPassword(rule, value, callback) {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://via.placeholder.com/100x100?text=用户'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const loadUserInfo = async () => {
  try {
    await authStore.getUserInfo()
    // 直接使用store中的用户数据
    if (authStore.user) {
      userForm.value = { ...authStore.user }
    }
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

const updateUserInfo = async () => {
  try {
    saving.value = true
    await authStore.updateUserInfo(userForm.value)
    ElMessage.success('信息更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}

const handleRecharge = async () => {
  try {
    recharging.value = true
    await authStore.recharge(userInfo.value.newMoney+rechargeForm.value.amount)

    showRechargeDialog.value = false
    ElMessage.success(`充值成功：¥${rechargeForm.value.amount}`)
  } catch (error) {
    ElMessage.error('充值失败')
  } finally {
    recharging.value = false
  }
}

const changePassword = async () => {
  try {
    // 验证确认密码
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
      ElMessage.error('两次输入密码不一致')
      return
    }

    changingPassword.value = true
    // 传递完整的密码表单数据
    await authStore.changePassword(passwordForm.value)

    showPasswordDialog.value = false
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    ElMessage.success('密码修改成功')
  } catch (error) {
    ElMessage.error(error.message || '密码修改失败')
  } finally {
    changingPassword.value = false
  }
}


const triggerFileUpload = () => {
  fileInput.value?.click()
}

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await authStore.uploadAvatar(formData)
    console.log(response)
    userForm.value.yonghuPhoto = 'http://localhost:8080/wuliuguanlixitong/upload/' + response.file
    userInfo.value.yonghuPhoto = 'http://localhost:8080/wuliuguanlixitong/upload/' + response.file
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}

const logout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadUserInfo()
})

// 监听用户信息变化
watch(() => authStore.userInfo, (newVal) => {
  if (newVal) {
    userInfo.value = newVal
    userForm.value = { ...newVal }
  }
}, { deep: true })
</script>

<style scoped>
.user-profile {
  padding: 20px;
  max-width: 800px;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h3 {
  margin: 0;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.profile-content {
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.avatar-upload {
  position: relative;
  display: inline-block;
}

.avatar-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #eee;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-upload:hover .upload-overlay {
  opacity: 1;
}

.upload-overlay .el-icon {
  font-size: 20px;
  margin-bottom: 5px;
}

.upload-overlay span {
  font-size: 12px;
}

.info-section {
  max-width: 500px;
  margin: 0 auto;
}

.balance-display {
  display: flex;
  align-items: center;
  gap: 15px;
}

.balance-amount {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}


/* 支付方式图标样式 */
.payment-method-icon {
  width: 20px;
  height: 20px;
  vertical-align: middle;
  margin-right: 8px;
}

.payment-method-label {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .user-profile {
    padding: 10px;
  }
  
  .profile-content {
    padding: 20px;
  }

  /* 移动端适配 */
  .payment-method-icon {
    width: 16px;
    height: 16px;
  }
}
</style>