<template>
  <div class="goods-manage">
    <div class="page-header">
      <div class="header-left">
        <div class="accent-bar"></div>
        <h3>我的货物管理</h3>
      </div>
    </div>

    <div class="filter-section modern-filter">
      <el-input
        v-model="searchQuery"
        placeholder="搜索货物名称..."
        class="modern-search"
        clearable
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="selectedType"
        placeholder="货物类型筛选"
        class="modern-select"
        clearable
        @change="handleSearch"
      >
        <el-option label="全部类型" value="" />
        <el-option
          v-for="type in huowuTypesList"
          :key="type.codeIndex"
          :label="type.indexName"
          :value="type.codeIndex"
        />
      </el-select>
    </div>

    <div class="goods-list">
      <el-card
        v-for="goods in goodsList"
        :key="goods.id"
        class="goods-card modern-card"
        :body-style="{ padding: '24px' }"
      >
        <div class="goods-content">
          <div class="goods-info">
            <div class="goods-header">
              <h4 class="goods-name">{{ goods.huowuName }}</h4>
              <el-tag :type="getTypeTag(goods.huowuTypes)" effect="light" round>
                {{ getTypeText(goods.huowuTypes) }}
              </el-tag>
            </div>

            <div class="goods-details-grid">
              <div class="detail-cell">
                <span class="detail-label">货物编号</span>
                <span class="detail-value mono">{{ goods.huowuUuidNumber }}</span>
              </div>
              <div class="detail-cell">
                <span class="detail-label">录入时间</span>
                <span class="detail-value">{{ formatDate(goods.insertTime) }}</span>
              </div>
            </div>
          </div>

          <div class="goods-actions">
            <el-button
              type="primary"
              plain
              class="action-btn"
              @click="viewContent(goods)"
            >
              <el-icon><Document /></el-icon>
              货物介绍
            </el-button>
            <el-button
              type="success"
              plain
              class="action-btn"
              @click="viewDriverInfo(goods.id)"
            >
              <el-icon><UserFilled /></el-icon>
              司机信息
            </el-button>
            <el-button
              type="warning"
              class="action-btn"
              @click="editGoods(goods)"
            >
              <el-icon><Edit /></el-icon>
              编辑修改
            </el-button>
          </div>
        </div>
      </el-card>

      <el-empty v-if="goodsList.length === 0" description="暂无相关货物信息" />
    </div>

    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        background
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog
      v-model="showContentModal"
      title="相关货物详情"
      width="760px"
      class="modern-dialog"
    >
      <div v-loading="contentLoading" class="goods-detail-dialog">
        <template v-if="detailInfo">
          <div class="detail-hero">
            <div class="detail-image-box">
              <el-image
                v-if="detailImage"
                :src="detailImage"
                class="detail-image"
                fit="cover"
                :preview-src-list="[detailImage]"
              />
              <div v-else class="detail-image-fallback">暂无图片</div>
            </div>

            <div class="detail-hero-content">
              <div class="hero-title-row">
                <h4>{{ detailInfo.shangpinName || detailInfo.huowuName || '货物详情' }}</h4>
                <el-tag :type="getTypeTag(detailInfo.huowuTypes)" effect="light" round>
                  {{ getTypeText(detailInfo.huowuTypes) }}
                </el-tag>
              </div>
              <p class="hero-subtitle">
                订单编号：{{ detailInfo.shangpinOrderUuidNumber || '暂无关联订单' }}
              </p>
              <div class="hero-metrics">
                <div class="metric-chip">
                  <span class="metric-label">仓库</span>
                  <span class="metric-value">{{ detailInfo.cangkuName || '暂无' }}</span>
                </div>
                <div class="metric-chip">
                  <span class="metric-label">数量</span>
                  <span class="metric-value">{{ detailInfo.buyNumber ?? '暂无' }}</span>
                </div>
                <div class="metric-chip">
                  <span class="metric-label">订单金额</span>
                  <span class="metric-value">{{ formatAmount(detailInfo.shangpinOrderTruePrice) }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="detail-grid">
            <div class="detail-panel info-panel">
              <div class="panel-header">
                <el-icon><InfoFilled /></el-icon>
                <span>基本档案</span>
              </div>
              <div class="panel-body">
                <div class="panel-row">
                  <span class="row-label">货物名称</span>
                  <span class="row-value">{{ detailInfo.huowuName || '暂无' }}</span>
                </div>
                <div class="panel-row">
                  <span class="row-label">货物编号</span>
                  <span class="row-value highlight-mono">{{ detailInfo.huowuUuidNumber || '暂无' }}</span>
                </div>
                <div class="panel-row">
                  <span class="row-label">货物类型</span>
                  <span class="row-value"><el-tag size="small" :type="getTypeTag(detailInfo.huowuTypes)">{{ getTypeText(detailInfo.huowuTypes) }}</el-tag></span>
                </div>
                <div class="panel-row">
                  <span class="row-label">录入时间</span>
                  <span class="row-value">{{ formatDate(detailInfo.insertTime) || '暂无' }}</span>
                </div>
              </div>
            </div>

            <div class="detail-panel order-panel">
              <div class="panel-header">
                <el-icon><List /></el-icon>
                <span>关联业务</span>
              </div>
              <div class="panel-body">
                <div class="panel-row">
                  <span class="row-label">订单编号</span>
                  <span class="row-value highlight-mono">{{ detailInfo.shangpinOrderUuidNumber || '暂无' }}</span>
                </div>
                <div class="panel-row">
                  <span class="row-label">购买数量</span>
                  <span class="row-value badge">{{ detailInfo.buyNumber ?? '暂无' }}</span>
                </div>
                <div class="panel-row">
                  <span class="row-label">结算金额</span>
                  <span class="row-value price-text">{{ formatAmount(detailInfo.shangpinOrderTruePrice) }}</span>
                </div>
                <div class="panel-row">
                  <span class="row-label">关联仓库</span>
                  <span class="row-value">{{ detailInfo.cangkuName || '暂无' }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="detail-panel detail-description">
            <div class="panel-title">相关货物介绍</div>
            <div
              v-if="detailContentHtml"
              class="detail-rich-content"
              v-html="detailContentHtml"
            ></div>
            <el-empty v-else description="暂无相关货物介绍" :image-size="90" />
          </div>
        </template>

        <div v-else class="loading-box">
          <el-skeleton :rows="6" animated />
        </div>
      </div>

      <template #footer>
        <el-button type="primary" @click="showContentModal = false">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showDriverModal"
      title="配送人员与车辆档案"
      width="750px"
      class="modern-dialog"
    >
      <div class="driver-profile" v-if="driverInfo">
        <el-row :gutter="30">
          <el-col :span="12">
            <div class="info-card">
              <div class="section-title">
                <el-icon><Van /></el-icon>
                <span>车辆信息</span>
              </div>
              <div class="profile-item">
                <span class="p-label">车辆名称</span>
                <span class="p-value">{{ driverInfo.cheliangName || '未指定' }}</span>
              </div>
              <div class="profile-item">
                <span class="p-label">车辆牌照</span>
                <span class="p-value highlight">{{ driverInfo.cheliangPaizhao || '待登记' }}</span>
              </div>
              <div class="profile-img-box" v-if="driverInfo.cheliangPhoto">
                <el-image
                  :src="toAssetUrl(driverInfo.cheliangPhoto)"
                  class="profile-img"
                  fit="cover"
                  :preview-src-list="[toAssetUrl(driverInfo.cheliangPhoto)]"
                />
              </div>
            </div>
          </el-col>

          <el-col :span="12">
            <div class="info-card">
              <div class="section-title">
                <el-icon><User /></el-icon>
                <span>司机档案</span>
              </div>
              <div class="profile-item">
                <span class="p-label">司机姓名</span>
                <span class="p-value">{{ driverInfo.sijiName || '未分配' }}</span>
              </div>
              <div class="profile-item">
                <span class="p-label">联系电话</span>
                <span class="p-value">{{ driverInfo.sijiPhone || '暂无' }}</span>
              </div>
              <div class="profile-img-box" v-if="driverInfo.sijiPhoto">
                <el-image
                  :src="toAssetUrl(driverInfo.sijiPhoto)"
                  class="profile-img"
                  fit="cover"
                  :preview-src-list="[toAssetUrl(driverInfo.sijiPhoto)]"
                />
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div v-else class="loading-box">
        <el-skeleton :rows="5" animated />
      </div>

      <template #footer>
        <el-button type="primary" plain @click="showDriverModal = false">关闭窗口</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showAddDialog"
      title="编辑货物档案"
      width="550px"
      class="modern-dialog"
    >
      <el-form
        :model="goodsForm"
        :rules="goodsRules"
        label-position="top"
        class="modern-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物名称" prop="huowuName">
              <el-input v-model="goodsForm.huowuName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物类型" prop="huowuTypes">
              <el-select v-model="goodsForm.huowuTypes" disabled style="width: 100%">
                <el-option
                  v-for="type in huowuTypesList"
                  :key="type.codeIndex"
                  :label="type.indexName"
                  :value="type.codeIndex"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="货物编号" prop="huowuUuidNumber">
          <el-input v-model="goodsForm.huowuUuidNumber" disabled />
        </el-form-item>

        <el-form-item label="货物详细描述" prop="huowuContent">
          <el-input
            v-model="goodsForm.huowuContent"
            type="textarea"
            :rows="5"
            placeholder="请输入货物补充说明或详细介绍"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-btns">
          <el-button round @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" round :loading="saving" @click="saveGoods">同步更新</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { Search, Document, UserFilled, Edit, Van, User, InfoFilled, List } from '@element-plus/icons-vue'

const authStore = useAuthStore()

const goodsList = ref([])
const huowuTypesList = ref([])
const searchQuery = ref('')
const selectedType = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showAddDialog = ref(false)
const showContentModal = ref(false)
const showDriverModal = ref(false)
const editingGoods = ref(null)
const saving = ref(false)
const driverInfo = ref(null)
const detailInfo = ref(null)
const contentLoading = ref(false)
const baseUrl = ref(import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/wuliuguanlixitong')

const goodsForm = ref({
  huowuName: '',
  huowuUuidNumber: '',
  huowuTypes: '',
  huowuContent: ''
})

const goodsRules = {
  huowuName: [{ required: true, message: '请输入货物名称', trigger: 'blur' }],
  huowuUuidNumber: [{ required: true, message: '请输入货物编号', trigger: 'blur' }],
  huowuTypes: [{ required: true, message: '请选择货物类型', trigger: 'change' }]
}

const detailImage = computed(() => toAssetUrl(detailInfo.value?.shangpinPhoto))

const detailContentHtml = computed(() =>
  formatRichContent(detailInfo.value?.shangpinContent || detailInfo.value?.huowuContent || '')
)

const loadGoods = async () => {
  try {
    const userData = await authStore.getUserInfo()
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      yonghuId: userData.id,
      ...(searchQuery.value && { huowuName: searchQuery.value }),
      ...(selectedType.value && { huowuTypes: selectedType.value })
    }

    const response = await authStore.getUserGoods(params)
    goodsList.value = response.list || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('加载货物失败')
  }
}

const loadHuowuTypes = async () => {
  try {
    const response = await authStore.getDictionaryPage({
      page: 1,
      limit: 100,
      dicCode: 'huowu_types'
    })
    huowuTypesList.value = response.list || []
  } catch (error) {
    ElMessage.error('加载货物类型失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadGoods()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadGoods()
}

const getTypeText = (typeCode) => {
  if (!typeCode) return '未知'
  const type = huowuTypesList.value.find((item) => String(item.codeIndex) === String(typeCode))
  return type ? type.indexName : '未知'
}

const getTypeTag = (typeCode) => {
  const types = { 1: 'warning', 2: 'danger', 3: 'success', 4: 'info' }
  return types[typeCode] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const formatAmount = (amount) => {
  if (amount === null || amount === undefined || amount === '') return '暂无'
  return `￥${Number(amount).toFixed(2)}`
}

const toAssetUrl = (path) => {
  if (!path) return ''
  if (/^(https?:)?\/\//i.test(path) || path.startsWith('data:')) {
    return path
  }
  return `${baseUrl.value}${path.startsWith('/') ? '' : '/'}${path}`
}

const formatRichContent = (content) => {
  if (!content) return ''
  return content
    .replace(/\n/g, '<br>')
    .replace(
      /<img\s+([^>]*?)src\s*=\s*["']([^"']*)["']([^>]*?)>/gi,
      (match, beforeSrc, src, afterSrc) => `<img ${beforeSrc}src="${toAssetUrl(src)}"${afterSrc}>`
    )
}

const viewContent = async (goods) => {
  try {
    showContentModal.value = true
    contentLoading.value = true
    detailInfo.value = { ...goods }
    const response = await authStore.getHuowuInfo(goods.id)
    if (response && response.code === 0) {
      detailInfo.value = { ...goods, ...(response.data || {}) }
      return
    }
    ElMessage.info('暂无相关货物信息')
  } catch (error) {
    ElMessage.error('获取货物详情失败')
    showContentModal.value = false
  } finally {
    contentLoading.value = false
  }
}

const viewDriverInfo = async (id) => {
  try {
    showDriverModal.value = true
    driverInfo.value = null
    const response = await authStore.getHuowuInfo(id)
    if (response && response.code === 0) {
      driverInfo.value = response.data
      return
    }
    ElMessage.error('获取司机信息失败')
  } catch (error) {
    ElMessage.error('获取司机信息失败')
    showDriverModal.value = false
  }
}

const editGoods = (goods) => {
  editingGoods.value = goods
  goodsForm.value = { ...goods }
  showAddDialog.value = true
}

const saveGoods = async () => {
  try {
    saving.value = true
    const userData = await authStore.getUserInfo()
    const goodsData = { ...goodsForm.value, yonghuId: userData.id }
    await authStore.updateGoods({ ...goodsData, id: editingGoods.value.id })
    ElMessage.success('货物档案已同步更新')
    showAddDialog.value = false
    loadGoods()
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadGoods()
  loadHuowuTypes()
})
</script>

<style scoped>
.goods-manage {
  min-height: calc(100vh - 100px);
  padding: 30px;
  background: #f8fafc;
}

.page-header {
  margin-bottom: 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.accent-bar {
  width: 6px;
  height: 24px;
  border-radius: 4px;
  background: #3b82f6;
}

.page-header h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
}

.modern-filter {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.modern-search,
.modern-select {
  flex: 1;
}

.modern-search :deep(.el-input__wrapper),
.modern-select :deep(.el-input__wrapper) {
  border-radius: 10px;
}

.goods-list {
  display: grid;
  gap: 20px;
}

.modern-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.modern-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.goods-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.goods-info {
  flex: 1;
}

.goods-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.goods-name {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.goods-details-grid {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}

.detail-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-label {
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
  letter-spacing: 0.5px;
}

.detail-value {
  font-size: 15px;
  font-weight: 500;
  color: #475569;
}

.mono {
  font-family: 'JetBrains Mono', monospace;
  color: #2563eb;
}

.goods-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 600;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 50px;
}

.modern-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 25px 30px;
  border-bottom: 1px solid #f1f5f9;
}

.modern-dialog :deep(.el-dialog__title) {
  font-weight: 800;
  color: #1e293b;
}

.modern-dialog :deep(.el-dialog__body) {
  padding: 30px;
}

.goods-detail-dialog {
  min-height: 240px;
}

.detail-hero {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.detail-image-box {
  height: 220px;
  overflow: hidden;
  border-radius: 18px;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
}

.detail-image,
.profile-img {
  width: 100%;
  height: 100%;
}

.detail-image-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-weight: 700;
  color: #64748b;
}

.detail-hero-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-title-row h4 {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
}

.hero-subtitle {
  margin: 0;
  color: #64748b;
}

.hero-metrics {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.metric-chip {
  min-width: 120px;
  padding: 12px 14px;
  border: 1px solid #dbeafe;
  border-radius: 14px;
  background: #f8fbff;
}

.metric-label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.metric-value {
  font-weight: 700;
  color: #1e3a8a;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-bottom: 18px;
}

.detail-panel {
  padding: 0;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  overflow: hidden;
  transition: all 0.3s;
}

.detail-panel:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.08);
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f8fafc;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
}

.panel-header .el-icon {
  color: #3b82f6;
  font-size: 16px;
}

.panel-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.panel-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13.5px;
}

.row-label {
  color: #64748b;
  font-weight: 500;
}

.row-value {
  color: #1e293b;
  font-weight: 600;
}

.highlight-mono {
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
  color: #2563eb;
  font-size: 13px;
}

.price-text {
  color: #ef4444;
  font-size: 16px;
  font-family: 'DIN Alternate', sans-serif;
}

.badge {
  background: #eff6ff;
  color: #1d4ed8;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
}

.font-bold {
  font-weight: 800;
}

.info-card {
  padding: 20px;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: #fff;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 800;
  color: #1e293b;
}

.profile-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.p-label {
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
}

.p-value {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.highlight {
  color: #2563eb;
}

.detail-description {
  background: #f8fafc;
}

.detail-rich-content {
  line-height: 1.85;
  color: #475569;
  word-break: break-word;
}

.detail-rich-content :deep(img) {
  max-width: 100%;
  border-radius: 12px;
}

.driver-profile {
  min-height: 200px;
}

.profile-img-box {
  height: 160px;
  margin-top: 15px;
  overflow: hidden;
  border-radius: 12px;
  background: #f8fafc;
}

.loading-box {
  padding: 12px 0;
}

.modern-form :deep(.el-form-item__label) {
  font-weight: 700;
  color: #475569;
}

.dialog-btns {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 992px) {
  .modern-filter,
  .goods-content {
    flex-direction: column;
    align-items: stretch;
  }

  .goods-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .detail-hero,
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
