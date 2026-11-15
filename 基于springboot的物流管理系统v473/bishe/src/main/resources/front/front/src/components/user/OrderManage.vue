<template>
  <div class="order-manage">
    <div class="page-header">
      <h3>我的订单</h3>
    </div>

    <div class="filter-tabs">
      <el-radio-group v-model="selectedStatus" @change="handleStatusChange">
        <el-radio-button label="">全部订单</el-radio-button>
        <el-radio-button
          v-for="status in orderStatusTypes"
          :key="status.codeIndex"
          :label="status.codeIndex.toString()"
        >
          {{ status.indexName }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <div class="order-list">
      <el-card
        v-for="order in orders"
        :key="order.id"
        class="order-card"
      >
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号: {{ order.shangpinOrderUuidNumber }}</span>
            <span class="order-time">{{ formatDate(order.insertTime) }}</span>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.shangpinOrderTypes)">
              {{ getStatusText(order.shangpinOrderTypes) }}
            </el-tag>
            <span class="order-amount">¥{{ Number(order.shangpinOrderTruePrice || 0).toFixed(2) }}</span>
          </div>
        </div>

        <div v-if="getOrderAlerts(order.id).length" class="order-alerts">
          <div class="alert-banner">
            <el-icon><Bell /></el-icon>
            <span>该订单存在 {{ getOrderAlerts(order.id).length }} 条预警，请及时关注。</span>
          </div>
          <div
            v-for="alert in getOrderAlerts(order.id)"
            :key="alert.id"
            class="alert-item"
          >
            <div class="alert-main">
              <strong>{{ alert.jiankongyujingName }}</strong>
              <p>{{ alert.yujingContent }}</p>
            </div>
            <div class="alert-tags">
              <el-tag size="small" :type="levelTagType(alert.yujingLevelTypes)">{{ alert.yujingLevelValue }}</el-tag>
              <el-tag size="small" :type="statusTagType(alert.yujingStatusTypes)">{{ alert.yujingStatusValue }}</el-tag>
            </div>
          </div>
        </div>

        <div class="order-items">
          <div class="item">
            <img
              :src="getImageUrl(order.shangpinPhoto)"
              :alt="order.shangpinName"
              class="item-image"
            />
            <div class="item-info">
              <h4>{{ order.shangpinName }}</h4>
              <p class="item-type">{{ order.shangpinValue }}</p>
              <p class="item-quantity">数量: {{ order.buyNumber }}</p>
            </div>
            <div class="item-total">
              小计: ¥{{ Number(order.shangpinOrderTruePrice || 0).toFixed(2) }}
            </div>
          </div>
        </div>

        <div class="order-footer">
          <div class="delivery-info">
            <p><strong>收货地址:</strong> {{ order.addressDizhi }}</p>
            <p><strong>收货人:</strong> {{ order.addressName }} {{ order.addressPhone }}</p>
            <p v-if="order.shangpinOrderCourierName">
              <strong>物流公司:</strong> {{ order.shangpinOrderCourierName }}
            </p>
            <p v-if="order.shangpinOrderCourierNumber">
              <strong>物流单号:</strong> {{ order.shangpinOrderCourierNumber }}
            </p>
          </div>

          <div class="order-actions">
            <el-button
              v-if="order.shangpinOrderTypes === 101"
              type="primary"
              @click="refundOrder(order)"
            >
              退款
            </el-button>

            <el-button
              v-if="canReceiveOrder(order)"
              type="success"
              @click="receivingOrder(order)"
            >
              验收
            </el-button>

            <el-button
              v-if="order.shangpinOrderTypes === 1"
              type="warning"
              @click="cancelOrder(order)"
            >
              取消订单
            </el-button>

            <el-button
              v-if="order.shangpinOrderTypes === 1"
              type="primary"
              @click="payOrder(order)"
            >
              立即付款
            </el-button>
          </div>
        </div>
      </el-card>

      <el-empty v-if="orders.length === 0" description="暂无订单" />
    </div>

    <div class="pagination-section" v-if="orders.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/request'

const authStore = useAuthStore()

const orders = ref([])
const orderStatusTypes = ref([])
const selectedStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const alerts = ref([])

const orderAlertMap = computed(() => {
  return alerts.value.reduce((map, item) => {
    if (!item.shangpinOrderId) return map
    if (!map[item.shangpinOrderId]) {
      map[item.shangpinOrderId] = []
    }
    map[item.shangpinOrderId].push(item)
    return map
  }, {})
})

const loadOrders = async () => {
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      ...(selectedStatus.value && { shangpinOrderTypes: selectedStatus.value })
    }

    const response = await authStore.getUserOrders(params)
    orders.value = response.list || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

const loadAlerts = async () => {
  try {
    const response = await authStore.getUserAlerts({
      page: 1,
      limit: 200,
      sort: 'id',
      order: 'desc'
    })
    alerts.value = (response.list || []).filter((item) => item.shangpinOrderId && [1, 2].includes(Number(item.yujingStatusTypes)))
  } catch (error) {
    ElMessage.error('加载预警失败')
  }
}

const loadOrderStatusTypes = async () => {
  try {
    const response = await authStore.getOrderStatusTypes()
    orderStatusTypes.value = response.list || []
  } catch (error) {
    ElMessage.error('加载订单状态失败')
  }
}

const getOrderAlerts = (orderId) => orderAlertMap.value[orderId] || []

const getStatusType = (status) => {
  const types = {
    1: 'warning',
    101: 'primary',
    102: 'info',
    103: 'primary',
    104: 'success',
    105: 'info',
    106: 'primary'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const statusObj = orderStatusTypes.value.find((item) => item.codeIndex == status)
  return statusObj ? statusObj.indexName : '未知状态'
}

const canReceiveOrder = (order) => [103, 106].includes(Number(order.shangpinOrderTypes))

const levelTagType = (level) => {
  if (Number(level) === 3) return 'danger'
  if (Number(level) === 2) return 'warning'
  return 'info'
}

const statusTagType = (status) => {
  if (Number(status) === 3) return 'success'
  if (Number(status) === 2) return 'warning'
  return 'danger'
}

const handleStatusChange = () => {
  currentPage.value = 1
  loadOrders()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadOrders()
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://via.placeholder.com/80x80?text=商品'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/wuliuguanlixitong'}/${imagePath}`
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

const refreshData = async () => {
  await loadOrders()
  await loadAlerts()
}

const payOrder = async (order) => {
  try {
    await authStore.payOrder(order.id)
    ElMessage.success('支付成功')
    refreshData()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

const receivingOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要验收这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.get(`/shangpinOrder/receiving?id=${order.id}`)
    if (response.code === 0) {
      ElMessage.success('验收成功')
      refreshData()
    } else {
      throw new Error(response.msg)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '验收失败')
    }
  }
}

const refundOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要申请退款吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.get(`/shangpinOrder/refund?id=${order.id}`)
    if (response.code === 0) {
      ElMessage.success('退款申请成功')
      refreshData()
    } else {
      throw new Error(response.msg)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '退款申请失败')
    }
  }
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await authStore.cancelOrder(order.id)
    ElMessage.success('订单已取消')
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(async () => {
  await loadOrders()
  await loadOrderStatusTypes()
  await loadAlerts()
})
</script>

<style scoped>
.order-manage {
  padding: 20px;
  max-height: 100vh;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h3 {
  margin: 0;
  color: #333;
}

.filter-tabs {
  margin-bottom: 30px;
}

.order-list {
  display: grid;
  gap: 20px;
}

.order-card {
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.order-number {
  font-weight: bold;
  color: #333;
}

.order-time {
  color: #666;
  font-size: 14px;
}

.order-status {
  display: flex;
  align-items: center;
  gap: 15px;
}

.order-amount {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.order-alerts {
  margin-bottom: 16px;
  padding: 14px;
  border-radius: 14px;
  background: linear-gradient(135deg, #fff7ed, #fffbeb);
}

.alert-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #c2410c;
  font-weight: 600;
}

.alert-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 0;
  border-top: 1px dashed rgba(194, 65, 12, 0.18);
}

.alert-item:first-of-type {
  border-top: none;
  padding-top: 0;
}

.alert-main strong {
  display: block;
  margin-bottom: 6px;
  color: #7c2d12;
}

.alert-main p {
  margin: 0;
  color: #9a3412;
  line-height: 1.6;
}

.alert-tags {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.order-items {
  margin-bottom: 15px;
}

.item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.item-type,
.item-quantity {
  margin: 2px 0;
  color: #666;
  font-size: 14px;
}

.item-total {
  font-weight: bold;
  color: #f56c6c;
  font-size: 16px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.delivery-info {
  flex: 1;
}

.delivery-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.order-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .order-header,
  .alert-item,
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .order-actions {
    justify-content: flex-end;
  }
}
</style>
