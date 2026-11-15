<template>
  <div class="order-confirm-container" v-if="orderData">
    <div class="page-header">
      <h2>确认订单</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/shangpin' }">商品</el-breadcrumb-item>
        <el-breadcrumb-item>确认订单</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="order-content">
      <!-- 商品信息 -->
      <div class="section-card">
        <h3>商品信息</h3>
        <div class="product-info">
          <img 
            :src="getImageUrl(orderData.product.shangpinPhoto)" 
            :alt="orderData.product.shangpinName"
            class="product-image"
          />
          <div class="product-details">
            <h4>{{ orderData.product.shangpinName }}</h4>
            <p class="product-number">编号: {{ orderData.product.shangpinUuidNumber }}</p>
            <p class="product-price">单价: ¥{{ orderData.product.shangpinNewMoney }}</p>
          </div>
          
          <div class="quantity-section">
            <span class="label">购买数量:</span>
            <div class="quantity-controls">
              <el-button 
                @click="decreaseQuantity" 
                :disabled="quantity <= 1"
                size="small"
              >-</el-button>
              <span class="quantity">{{ quantity }}</span>
              <el-button 
                @click="increaseQuantity" 
                :disabled="quantity >= orderData.product.shangpinKucunNumber"
                size="small"
              >+</el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 收货地址 -->
      <div class="section-card">
        <div class="section-header">
          <h3>收货地址</h3>
          <el-button type="text" @click="showAddressDialog = true">管理地址</el-button>
        </div>
        
        <div v-if="selectedAddress" class="address-info">
          <div class="address-item">
            <span class="name">{{ selectedAddress.addressName }}</span>
            <span class="phone">{{ selectedAddress.addressPhone }}</span>
            <span v-if="selectedAddress.isdefaultTypes === 1" class="default-tag">默认</span>
          </div>
          <div class="address-detail">{{ selectedAddress.addressDizhi }}</div>
        </div>
        
        <div v-else class="no-address">
          <p>暂无收货地址，请添加地址</p>
          <el-button type="primary" @click="showAddressDialog = true">添加地址</el-button>
        </div>
      </div>
      
      <!-- 订单金额 -->
      <div class="section-card">
        <h3>订单金额</h3>
        <div class="amount-details">
          <div class="amount-item">
            <span>商品金额:</span>
            <span>¥{{ totalAmount }}</span>
          </div>
          
          <div class="amount-item total">
            <span>应付金额:</span>
            <span class="amount">¥{{ totalAmount }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 底部操作栏 -->
    <div class="order-footer">
      <div class="total-info">
        <span>应付金额:</span>
        <span class="total-amount">¥{{ totalAmount }}</span>
      </div>
      
      <el-button 
        type="primary" 
        size="large"
        @click="submitOrder"
        :disabled="!selectedAddress || submitting"
        :loading="submitting"
      >
        提交订单
      </el-button>
    </div>
    
    <!-- 地址选择对话框 -->
    <el-dialog 
      v-model="showAddressDialog" 
      title="选择收货地址"
      width="600px"
    >
      <div class="address-list">
        <div 
          v-for="address in addresses" 
          :key="address.id"
          class="address-item-card"
          :class="{ active: selectedAddress?.id === address.id }"
          @click="selectAddress(address)"
        >
          <div class="address-info">
            <div class="address-header">
              <span class="name">{{ address.addressName }}</span>
              <span class="phone">{{ address.addressPhone }}</span>
              <span v-if="address.isdefaultTypes === 1" class="default-tag">默认</span>
            </div>
            <div class="address-text">{{ address.addressDizhi }}</div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddress">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const authStore = useAuthStore()

const orderData = ref(null)
const quantity = ref(1)
const selectedAddress = ref(null)
const addresses = ref([])
const showAddressDialog = ref(false)
const submitting = ref(false)

const totalAmount = computed(() => {
  if (!orderData.value) return 0
  return (orderData.value.product.shangpinNewMoney * quantity.value).toFixed(2)
})

const loadOrderData = async () => {
  try {
    const goodsId = route.query.goodsId
    const buyNumber = route.query.buyNumber
    
    if (buyNumber) {
      quantity.value = parseInt(buyNumber)
    }
    
    await productStore.fetchProductDetail(goodsId)
    orderData.value = {
      product: productStore.currentProduct
    }
  } catch (error) {
    ElMessage.error('加载订单数据失败')
  }
}

const loadAddresses = async () => {
  try {
    const userData = await authStore.getUserInfo()
    if (userData && userData.id) {
      const response = await authStore.getAddresses(userData.id)
      addresses.value = response || []
      
      // 设置默认地址
      const defaultAddress = addresses.value.find(addr => addr.isdefaultTypes === 1)
      if (defaultAddress) {
        selectedAddress.value = defaultAddress
      } else if (addresses.value.length > 0) {
        selectedAddress.value = addresses.value[0]
      }
    }
  } catch (error) {
    ElMessage.error('加载地址失败')
  }
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://via.placeholder.com/100x100?text=商品图片'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const increaseQuantity = () => {
  if (quantity.value < orderData.value.product.shangpinKucunNumber) {
    quantity.value++
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const selectAddress = (address) => {
  selectedAddress.value = address
}

const confirmAddress = () => {
  showAddressDialog.value = false
}

const submitOrder = async () => {
  console.log('提交订单数据:', orderData)


  if (!selectedAddress.value) {
    ElMessage.error('请选择收货地址')
    return
  }

  submitting.value = true
  try {
    // 构造订单数据，参考 confirm.html 的实现
    const orderDataBody = {
      addressId: selectedAddress.value.id,
      buyNumber: quantity.value,
      shangpinId: orderData.value.product.id,
      yonghuId: (await authStore.getUserInfo()).id
    }

    // 调用 authStore 中的提交订单方法
    const result = await authStore.createOrder(orderDataBody)

    if (result.code === 0) {
      ElMessage.success('订单提交成功')
      // 跳转到订单列表页（根据你的路由配置调整路径）
      router.push('/user/orders')
    } else {
      ElMessage.error(result.msg || '订单提交失败')
    }
  } catch (error) {
    ElMessage.error('订单提交失败: ' + error.message)
  } finally {
    submitting.value = false
  }
}


onMounted(() => {
  loadOrderData()
  loadAddresses()
})
</script>

<style scoped>
.order-confirm-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  margin-bottom: 10px;
  color: #333;
}

.order-content {
  margin-bottom: 80px;
}

.section-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.section-card h3 {
  margin-bottom: 20px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.product-details h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.product-number {
  color: #666;
  margin: 5px 0;
}

.product-price {
  color: #f56c6c;
  font-weight: bold;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 15px;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity {
  min-width: 30px;
  text-align: center;
  font-weight: bold;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.address-info {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.address-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.name {
  font-weight: bold;
}

.phone {
  color: #666;
}

.default-tag {
  background: #f56c6c;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.address-detail {
  color: #333;
}

.no-address {
  text-align: center;
  padding: 40px;
  color: #666;
}

.amount-details {
  padding: 20px;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 16px;
}

.amount-item.total {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.order-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 20px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-info {
  font-size: 18px;
}

.total-amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 24px;
  margin-left: 10px;
}

.address-list {
  max-height: 400px;
  overflow-y: auto;
}

.address-item-card {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item-card:hover,
.address-item-card.active {
  border-color: #409eff;
  background: #f0f9ff;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .order-confirm-container {
    padding: 10px;
  }
  
  .product-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .order-footer {
    flex-direction: column;
    gap: 10px;
  }
}
</style>