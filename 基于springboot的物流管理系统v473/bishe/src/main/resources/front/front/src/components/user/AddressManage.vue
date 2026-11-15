<template>
  <div class="address-manage">
    <div class="page-header">
      <h3>收货地址管理</h3>
      <el-button type="primary" @click="handleAddClick">
        <el-icon><Plus /></el-icon>
        添加新地址
      </el-button>
    </div>

    <div class="address-list">
      <el-card
        v-for="address in addresses"
        :key="address.id"
        class="address-card"
        :class="{ 'default-address': address.isdefaultTypes === 1 }"
      >
        <div class="address-content">
          <div class="address-info">
            <div class="address-header">
              <span class="name">{{ address.addressName }}</span>
              <span class="phone">{{ address.addressPhone }}</span>
              <el-tag v-if="address.isdefaultTypes === 1" type="success" size="small">
                默认
              </el-tag>
            </div>
            <div class="address-detail">{{ address.addressDizhi }}</div>
          </div>

          <div class="address-actions">
            <el-button type="primary" text @click="editAddress(address)">
              编辑
            </el-button>
            <el-button
              v-if="address.isdefaultTypes !== 1"
              type="success"
              text
              @click="setDefault(address)"
            >
              设为默认
            </el-button>
            <el-button type="danger" text @click="deleteAddress(address)">
              删除
            </el-button>
          </div>
        </div>
      </el-card>

      <el-empty v-if="addresses.length === 0" description="暂无收货地址" />
    </div>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingAddress ? '编辑地址' : '添加新地址'"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="addressName">
          <el-input v-model="addressForm.addressName" placeholder="请输入收货人姓名" />
        </el-form-item>

        <el-form-item label="手机号" prop="addressPhone">
          <el-input v-model="addressForm.addressPhone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="详细地址" prop="addressDizhi">
          <el-input
            v-model="addressForm.addressDizhi"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isdefaultTypes" :active-value="1" :inactive-value="2" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const authStore = useAuthStore()

const addresses = ref([])
const showAddDialog = ref(false)
const editingAddress = ref(null)
const saving = ref(false)

const addressForm = ref({
  addressName: '',
  addressPhone: '',
  addressDizhi: '',
  isdefaultTypes: 2
})

const addressRules = {
  addressName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  addressPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  addressDizhi: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 1, max: 300, message: '长度在 1 到 300 个字符', trigger: 'blur' }
  ]
}

const handleAddClick = () => {
  editingAddress.value = null
  resetForm()
  showAddDialog.value = true
}


const loadAddresses = async () => {
  try {
    const userData = await authStore.getUserInfo()
    if (userData && userData.id) {
      const response = await authStore.getAddresses(userData.id)
      addresses.value = response || []
    }
  } catch (error) {
    ElMessage.error('加载地址失败')
  }
}

const editAddress = (address) => {
  editingAddress.value = address
  addressForm.value = { ...address }
  showAddDialog.value = true
}

const setDefault = async (address) => {
  try {
    await authStore.updateAddress({
      ...address,
      isdefaultTypes: 1
    })
    
    // 更新其他地址为非默认
    addresses.value.forEach(addr => {
      if (addr.id !== address.id) {
        addr.isdefaultTypes = 2
      }
    })
    
    address.isdefaultTypes = 1
    ElMessage.success('已设为默认地址')
  } catch (error) {
    ElMessage.error('设置默认地址失败')
  }
}

const deleteAddress = async (address) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await authStore.deleteAddress(address.id)
    addresses.value = addresses.value.filter(addr => addr.id !== address.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveAddress = async () => {
  const form = document.querySelector('.address-manage').querySelector('form')
  if (!form) return

  try {
    saving.value = true
    const userData = await authStore.getUserInfo()
    
    const addressData = {
      ...addressForm.value,
      yonghuId: userData.id
    }

    if (editingAddress.value) {
      await authStore.updateAddress({
        ...addressData,
        id: editingAddress.value.id
      })
      
      const index = addresses.value.findIndex(addr => addr.id === editingAddress.value.id)
      if (index !== -1) {
        addresses.value[index] = { ...addressData, id: editingAddress.value.id }
      }
      
      ElMessage.success('更新成功')
    } else {
      const response = await authStore.addAddress(addressData)
      // console.log(response)
      // addresses.value.push(response)
      console.log(addresses.value)
      ElMessage.success('添加成功')
      loadAddresses()
    }

    showAddDialog.value = false
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  editingAddress.value = null
  addressForm.value = {
    addressName: '',
    addressPhone: '',
    addressDizhi: '',
    isdefaultTypes: 2
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.address-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h3 {
  margin: 0;
  color: #333;
}

.address-list {
  display: grid;
  gap: 20px;
}

.address-card {
  transition: all 0.3s;
}

.address-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.address-card.default-address {
  border-left: 4px solid #67c23a;
}

.address-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.phone {
  color: #666;
}

.address-detail {
  color: #666;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .address-content {
    flex-direction: column;
    gap: 15px;
  }

  .address-actions {
    justify-content: flex-end;
  }
}
</style>