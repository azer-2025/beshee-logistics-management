<template>
  <div class="notice-container">
    <div class="page-header">
      <div class="title-group">
        <div class="accent-bar"></div>
        <h2>系统公告</h2>
        <span class="sub-title">Notices</span>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="请输入公告名称进行搜索..."
          class="modern-search-input"
          @input="handleSearch"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="filter-tags">
        <span class="filter-label">公告类型：</span>
        <el-tag
          :type="selectedType === '' ? 'primary' : 'info'"
          :effect="selectedType === '' ? 'dark' : 'plain'"
          @click="selectType('')"
          class="type-tag"
          round
        >
          全部
        </el-tag>
        <el-tag
          v-for="type in noticeTypes"
          :key="type.codeIndex"
          :type="selectedType === type.codeIndex ? 'primary' : 'info'"
          :effect="selectedType === type.codeIndex ? 'dark' : 'plain'"
          @click="selectType(type.codeIndex)"
          class="type-tag"
          round
        >
          {{ type.indexName }}
        </el-tag>
      </div>
    </div>

    <!-- 公告列表 -->
    <div class="modern-grid notice-grid">
      <div
        v-for="notice in notices"
        :key="notice.id"
        class="modern-notice-card"
        @click="$router.push(`/gonggao/${notice.id}`)"
      >
        <div class="notice-img-box">
          <img
            :src="getImageUrl(notice.gonggaoPhoto)"
            :alt="notice.gonggaoName"
            class="notice-img"
            @error="handleImageError"
          />
          <div class="image-overlay">
            <span class="view-btn">阅读全文</span>
          </div>
        </div>
        
        <div class="notice-info">
          <div class="notice-date">
            <span class="day">{{ new Date(notice.insertTime).getDate().toString().padStart(2, '0') }}</span>
            <span class="month-year">{{ new Date(notice.insertTime).getFullYear() }}-{{ (new Date(notice.insertTime).getMonth() + 1).toString().padStart(2, '0') }}</span>
          </div>
          <div class="notice-text">
            <div class="title-row">
              <h3 class="notice-title" :title="notice.gonggaoName">{{ notice.gonggaoName }}</h3>
              <el-tag size="small" type="primary" effect="light" class="modern-tag">
                {{ getTypeName(notice.gonggaoTypes) }}
              </el-tag>
            </div>
            <p class="notice-desc">{{ truncateContent(notice.gonggaoContent, 80) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 暂无数据 -->
    <el-empty v-if="notices.length === 0" description="暂未找到相关公告" class="custom-empty"></el-empty>

    <!-- 分页 -->
    <div class="pagination-section" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        background
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useNoticeStore } from '@/stores/notice'

const noticeStore = useNoticeStore()

const searchQuery = ref('')
const selectedType = ref('')
const currentPage = ref(1)
const pageSize = ref(10) // 每页10条更适合列表

const notices = ref([])
const noticeTypes = ref([])
const total = ref(0)

const loadNotices = async () => {
  const params = {
    page: currentPage.value,
    limit: pageSize.value,
    ...(searchQuery.value && { gonggaoName: searchQuery.value }),
    ...(selectedType.value && { gonggaoTypes: selectedType.value })
  }

  await noticeStore.fetchNotices(params)
  notices.value = noticeStore.notices
  total.value = noticeStore.pagination.total
}

const loadNoticeTypes = async () => {
  await noticeStore.fetchNoticeTypes()
  noticeTypes.value = noticeStore.noticeTypes
}

const handleSearch = () => {
  currentPage.value = 1
  loadNotices()
}

const selectType = (type) => {
  selectedType.value = type
  currentPage.value = 1
  loadNotices()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadNotices()
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getTypeName = (typeId) => {
  const type = noticeTypes.value.find(t => t.codeIndex === typeId)
  return type ? type.indexName : '系统通知'
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const handleImageError = (event) => {
  event.target.src = 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'
}

const truncateContent = (content, length) => {
  if (!content) return ''
  const cleanContent = content.replace(/<[^>]*>/g, '')
  if (cleanContent.length > length) {
    return cleanContent.substring(0, length) + '...'
  }
  return cleanContent
}

onMounted(() => {
  loadNoticeTypes()
  loadNotices()
})

watch([searchQuery, selectedType], () => {
  currentPage.value = 1
  loadNotices()
})
</script>

<style scoped>
.notice-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  margin-bottom: 30px;
}

.title-group {
  display: flex;
  align-items: baseline;
  gap: 15px;
}

.accent-bar {
  width: 6px;
  height: 28px;
  background: #3b82f6;
  border-radius: 4px;
}

.title-group h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.sub-title {
  font-size: 16px;
  color: #94a3b8;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Filter Section */
.filter-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 40px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
}

.search-box {
  margin-bottom: 20px;
}

.modern-search-input {
  max-width: 400px;
}

.modern-search-input :deep(.el-input__wrapper) {
  border-radius: 20px !important;
  padding: 8px 20px !important;
  background: #f8fafc !important;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  margin-right: 8px;
}

.type-tag {
  cursor: pointer;
  padding: 6px 16px;
  height: auto;
  font-size: 13px;
  transition: all 0.3s;
}

.type-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

/* Grid & Cards */
.modern-grid {
  display: grid;
  gap: 24px;
}

.notice-grid {
  grid-template-columns: repeat(2, 1fr);
}

.modern-notice-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #f1f5f9;
}

.modern-notice-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.05);
  border-color: #e2e8f0;
}

.notice-img-box {
  width: 220px;
  min-width: 220px;
  position: relative;
  overflow: hidden;
}

.notice-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.modern-notice-card:hover .notice-img {
  transform: scale(1.08);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-notice-card:hover .image-overlay {
  opacity: 1;
}

.view-btn {
  color: #fff;
  border: 2px solid #fff;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  backdrop-filter: blur(4px);
}

.notice-info {
  padding: 24px;
  display: flex;
  gap: 24px;
  flex: 1;
}

.notice-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 60px;
  padding-right: 24px;
  border-right: 2px solid #f1f5f9;
}

.notice-date .day {
  font-size: 36px;
  font-weight: 800;
  color: #3b82f6;
  line-height: 1;
}

.notice-date .month-year {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 6px;
  font-weight: 600;
}

.notice-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 15px;
}

.notice-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.modern-tag {
  border-radius: 6px;
  font-weight: 500;
  flex-shrink: 0;
}

.notice-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Pagination & Empty */
.custom-empty {
  margin: 60px 0;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 50px;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #3b82f6;
}

@media (max-width: 1024px) {
  .notice-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .modern-notice-card {
    flex-direction: column;
  }
  .notice-img-box {
    width: 100%;
    height: 200px;
  }
  .notice-info {
    flex-direction: column;
    gap: 16px;
  }
  .notice-date {
    border-right: none;
    border-bottom: 2px solid #f1f5f9;
    padding-right: 0;
    padding-bottom: 16px;
    flex-direction: row;
    gap: 12px;
    align-items: baseline;
  }
  .notice-date .month-year {
    margin-top: 0;
  }
}
</style>
