<template>
  <div class="notice-detail-container" v-if="notice">
    <div class="page-header">
      <el-button @click="$router.go(-1)" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <div class="notice-content">
      <div class="notice-header">
        <h1>{{ notice.gonggaoName }}</h1>
        <div class="notice-meta">
          <span class="time">发布时间: {{ formatDate(notice.insertTime) }}</span>
          <span class="type">{{ getTypeName(notice.gonggaoTypes) }}</span>
        </div>
      </div>

      <div class="notice-image-container" v-if="notice.gonggaoPhoto">
        <img
            :src="getImageUrl(notice.gonggaoPhoto)"
            :alt="notice.gonggaoName"
            class="notice-image"
            @error="handleImageError"
        />
      </div>


      <div class="notice-body">
        <div class="notice-content-text" v-html="notice.gonggaoContent"></div>
      </div>

      <div class="notice-footer">
        <div class="footer-info">
          <p>发布人: 系统管理员</p>
        </div>


      </div>
    </div>

    <div class="related-notices">
      <h3>相关公告</h3>
      <div class="notice-list">
        <el-card
          v-for="relatedNotice in relatedNotices"
          :key="relatedNotice.id"
          class="notice-card image-hover"
          @click="$router.push(`/gonggao/${relatedNotice.id}`)"
        >
          <div class="notice-info">
            <h4>{{ relatedNotice.gonggaoName }}</h4>
            <p class="time">{{ formatDate(relatedNotice.insertTime) }}</p>
            <p class="summary">{{ relatedNotice.gonggaoContent?.substring(0, 100) }}...</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useNoticeStore } from '@/stores/notice'
import { ElMessage } from 'element-plus'


const route = useRoute()
const router = useRouter()
const noticeStore = useNoticeStore()

const notice = ref(null)
const relatedNotices = ref([])
const loading = ref(false)

const loadNoticeDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    await noticeStore.fetchNoticeDetail(id)
    notice.value = noticeStore.currentNotice

    // 加载相关公告
    await loadRelatedNotices()
  } catch (error) {
    ElMessage.error('加载公告详情失败')
  } finally {
    loading.value = false
  }
}

const loadRelatedNotices = async () => {
  try {
    await noticeStore.fetchNotices({ limit: 6 })
    relatedNotices.value = noticeStore.notices
      .filter(n => n.id !== notice.value?.id)
      .slice(0, 4)
  } catch (error) {
    console.error('加载相关公告失败:', error)
  }
}

const getTypeName = (typeId) => {
  const type = noticeStore.noticeTypes.find(t => t.codeIndex === typeId)
  return type ? type.indexName : '未知类型'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取图片完整URL（添加到 script setup 部分）
const getImageUrl = (imagePath) => {
  if (!imagePath) return ''
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/wuliuguanlixitong'}/${imagePath}`
}

// 处理图片加载错误（添加到 script setup 部分）
const handleImageError = (event) => {
  event.target.src = 'https://via.placeholder.com/600x300?text=公告图片'
}

watch(
    () => route.params.id,
    (newId, oldId) => {
      if (newId !== oldId) {
        loadNoticeDetail()
      }
    }
)


onMounted(() => {
  loadNoticeDetail()
})
</script>

<style scoped>
.notice-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 5px;
}

.notice-content {
  background: white;
  border-radius: 10px;
  padding: 40px;
  margin-bottom: 40px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.notice-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.notice-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 15px;
}

.notice-meta {
  display: flex;
  justify-content: center;
  gap: 30px;
  color: #666;
  font-size: 14px;
}

.notice-body {
  margin-bottom: 40px;
}

.notice-content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  text-align: justify;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}


.related-notices {
  margin-top: 40px;
}

.related-notices h3 {
  margin-bottom: 20px;
  color: #333;
}

.notice-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.notice-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.notice-card:hover {
  transform: translateY(-3px);
}

.notice-info h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notice-info .time {
  color: #666;
  font-size: 12px;
  margin-bottom: 10px;
}

.notice-info .summary {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  height: 60px;
  overflow: hidden;
}

@media (max-width: 768px) {
  .notice-content {
    padding: 20px;
  }

  .notice-header h1 {
    font-size: 22px;
  }

  .notice-meta {
    flex-direction: column;
    gap: 10px;
  }

  .notice-footer {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .notice-list {
    grid-template-columns: 1fr;
  }
}
/* 添加到 style scoped 部分 */
.notice-image-container {
  text-align: center;
  margin-bottom: 30px;
}

.notice-image {
  max-width: 100%;
  height: auto;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}


</style>