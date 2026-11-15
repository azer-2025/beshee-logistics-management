<template>
  <div class="home-container">
    <!-- 顶部欢迎区 & 轮播图 -->
    <div class="hero-section">
      <el-carousel height="460px" indicator-position="inside" class="modern-carousel">
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <div class="carousel-item"
               :style="{ backgroundImage: `url(${getImageUrl(item.img || item.image)})` }"
          >
            <div class="carousel-overlay">
              <div class="carousel-content" v-if="item.title || item.description">
                <h2 class="animate-title">{{ item.title || '智慧冷链物流监控系统' }}</h2>
                <p class="animate-desc">{{ item.description || '保障生鲜品质，全程可视可控' }}</p>
                <el-button type="primary" size="large" class="hero-btn" @click="$router.push('/cangku')">探索仓库</el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 仓库展示区 -->
    <section class="modern-section">
      <div class="section-header">
        <div class="title-group">
          <div class="accent-bar"></div>
          <h2>精选仓库</h2>
          <span class="sub-title">Warehouse</span>
        </div>
        <el-button text class="more-btn" @click="$router.push('/cangku')">
          查看全部 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="modern-grid warehouse-grid">
        <el-card
          v-for="warehouse in warehouses"
          :key="warehouse.id"
          class="modern-card warehouse-card"
          :body-style="{ padding: '0px' }"
          @click="$router.push(`/cangku/${warehouse.id}`)"
        >
          <div class="card-image-wrapper">
            <img
              :src="getImageUrl(warehouse.cangkuPhoto)"
              :alt="warehouse.cangkuName"
              class="card-image"
            />
            <div class="image-overlay">
              <span class="view-btn">查看详情</span>
            </div>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ warehouse.cangkuName }}</h3>
            <div class="card-tags">
              <el-tag size="small" effect="light" type="info" class="modern-tag">
                {{ getTypeName(warehouse.cangkuTypes, warehouseTypes) }}
              </el-tag>
            </div>
            <div class="card-meta">
              <span class="location"><el-icon><Location /></el-icon> {{ warehouse.cangkuAddress }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <!-- 商品展示区 -->
    <section class="modern-section bg-light">
      <div class="section-header">
        <div class="title-group">
          <div class="accent-bar"></div>
          <h2>推荐商品</h2>
          <span class="sub-title">Products</span>
        </div>
        <el-button text class="more-btn" @click="$router.push('/shangpin')">
          查看全部 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="modern-grid product-grid">
        <el-card
          v-for="product in products"
          :key="product.id"
          class="modern-card product-card"
          :body-style="{ padding: '0px' }"
          @click="$router.push(`/shangpin/${product.id}`)"
        >
          <div class="card-image-wrapper product-img-wrapper">
            <img
              :src="getImageUrl(product.shangpinPhoto)"
              :alt="product.shangpinName"
              class="card-image product-img"
            />
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ product.shangpinName }}</h3>
            <div class="product-price-row">
              <span class="price"><span class="currency">¥</span>{{ product.shangpinNewMoney }}</span>
              <span class="stock">库存: {{ product.shangpinKucunNumber }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <!-- 公告展示区 -->
    <section class="modern-section">
      <div class="section-header">
        <div class="title-group">
          <div class="accent-bar"></div>
          <h2>系统公告</h2>
          <span class="sub-title">Notices</span>
        </div>
        <el-button text class="more-btn" @click="$router.push('/gonggao')">
          查看全部 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="notice-grid">
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
                @error="handleNoticeImageError"
            />
          </div>
          <div class="notice-info">
            <div class="notice-date">
              <span class="day">{{ new Date(notice.insertTime).getDate().toString().padStart(2, '0') }}</span>
              <span class="month-year">{{ new Date(notice.insertTime).getFullYear() }}-{{ (new Date(notice.insertTime).getMonth() + 1).toString().padStart(2, '0') }}</span>
            </div>
            <div class="notice-text">
              <h3 class="notice-title">{{ notice.gonggaoName }}</h3>
              <p class="notice-desc">{{ truncateNoticeContent(notice.gonggaoContent, 60) }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useWarehouseStore } from '@/stores/warehouse'
import { useProductStore } from '@/stores/product'
import { useNoticeStore } from '@/stores/notice'
import request from '@/utils/request'

const router = useRouter()
const warehouseStore = useWarehouseStore()
const productStore = useProductStore()
const noticeStore = useNoticeStore()

const carouselItems = ref([])
const warehouses = ref([])
const products = ref([])
const notices = ref([])
const warehouseTypes = ref([])

const loadCarouselData = async () => {
  try {
    const response = await request.get('/config/list', { params: { page: 1, limit: 5 } })
    if (response.data && response.data.list) {
      const swiperList = response.data.list.filter(item => item.value).map(item => ({ img: item.value }))
      if (swiperList.length > 0) {
        carouselItems.value = swiperList;
        return;
      }
    }
    // Fallback if no images
    throw new Error('No images');
  } catch (error) {
    carouselItems.value = [
      {
        img: 'https://images.unsplash.com/photo-1586528116311-ad8ed7c83a54?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
        title: '冷链物流 领航未来',
        description: '提供全方位、高标准的生鲜冷链监控解决方案'
      },
      {
        img: 'https://images.unsplash.com/photo-1553413077-190dd305871c?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
        title: '智能仓储 精准温控',
        description: '实时数据采集，异常秒级预警，保障货物安全'
      },
      {
        img: 'https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80',
        title: '高效配送 鲜达万家',
        description: '优化物流链路，让新鲜触手可及'
      }
    ]
  }
}

const handleNoticeImageError = (event) => {
  event.target.src = 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'
}

const truncateNoticeContent = (content, length) => {
  if (!content) return ''
  const cleanContent = content.replace(/<[^>]*>/g, '')
  if (cleanContent.length > length) {
    return cleanContent.substring(0, length) + '...'
  }
  return cleanContent
}

const loadHomeData = async () => {
  await loadCarouselData()
  
  await warehouseStore.fetchWarehouses({ limit: 6 })
  warehouses.value = warehouseStore.warehouses

  await productStore.fetchProducts({ limit: 8 })
  products.value = productStore.products

  await noticeStore.fetchNotices({ limit: 4 })
  notices.value = noticeStore.notices

  await warehouseStore.fetchWarehouseTypes()
  warehouseTypes.value = warehouseStore.warehouseTypes
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://images.unsplash.com/photo-1586528116311-ad8ed7c83a54?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const getTypeName = (typeId, types) => {
  const type = types.find(t => t.codeIndex === typeId)
  return type ? type.indexName : '其他类型'
}

onMounted(() => {
  loadHomeData()
})
</script>

<style scoped>
.home-container {
  width: 100%;
  padding-bottom: 60px;
}

/* Hero Section */
.hero-section {
  max-width: 1400px;
  margin: 20px auto 60px;
  padding: 0 20px;
}

.modern-carousel {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.carousel-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.carousel-overlay {
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, rgba(15, 23, 42, 0.8) 0%, rgba(15, 23, 42, 0.4) 50%, rgba(15, 23, 42, 0.1) 100%);
  display: flex;
  align-items: center;
  padding: 0 80px;
}

.carousel-content {
  max-width: 600px;
  color: #fff;
  animation: fadeIn 1s ease-out;
}

.animate-title {
  font-size: 42px;
  font-weight: 800;
  margin-bottom: 20px;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.animate-desc {
  font-size: 18px;
  margin-bottom: 30px;
  color: rgba(255,255,255,0.9);
  line-height: 1.6;
}

.hero-btn {
  padding: 12px 36px;
  font-size: 16px;
  border-radius: 30px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Section Common */
.modern-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
}

.bg-light {
  background: #f8fafc;
  max-width: 100%;
  padding: 60px 20px;
}

.bg-light > * {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
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

.more-btn {
  font-size: 15px;
  font-weight: 500;
  color: #64748b;
  transition: color 0.3s;
}

.more-btn:hover {
  color: #3b82f6;
}

/* Grid & Cards */
.modern-grid {
  display: grid;
  gap: 30px;
}

.warehouse-grid {
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
}

.product-grid {
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
}

.modern-card {
  border: none;
  border-radius: 16px;
  background: #fff;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
}

.modern-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.card-image-wrapper {
  position: relative;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-img {
  height: 260px;
}

.modern-card:hover .card-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-card:hover .image-overlay {
  opacity: 1;
}

.view-btn {
  color: #fff;
  border: 2px solid #fff;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 12px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-tags {
  margin-bottom: 12px;
}

.modern-tag {
  border-radius: 6px;
  font-weight: 500;
}

.card-meta {
  display: flex;
  color: #64748b;
  font-size: 14px;
}

.location {
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.price {
  color: #ef4444;
  font-size: 22px;
  font-weight: 800;
}

.currency {
  font-size: 14px;
  margin-right: 2px;
}

.stock {
  font-size: 13px;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 4px 8px;
  border-radius: 6px;
}

/* Notice List */
.notice-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.modern-notice-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
}

.modern-notice-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  border-color: #e2e8f0;
}

.notice-img-box {
  width: 160px;
  min-width: 160px;
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

.notice-info {
  padding: 20px;
  display: flex;
  gap: 20px;
}

.notice-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 60px;
  padding-right: 20px;
  border-right: 2px solid #f1f5f9;
}

.notice-date .day {
  font-size: 32px;
  font-weight: 800;
  color: #3b82f6;
  line-height: 1;
}

.notice-date .month-year {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
  font-weight: 600;
}

.notice-text {
  flex: 1;
}

.notice-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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

@media (max-width: 1024px) {
  .notice-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 0;
    margin-top: 0;
  }
  .modern-carousel {
    border-radius: 0;
  }
  .carousel-overlay {
    padding: 0 30px;
  }
  .animate-title {
    font-size: 28px;
  }
  .modern-notice-card {
    flex-direction: column;
  }
  .notice-img-box {
    width: 100%;
    height: 180px;
  }
  .notice-date {
    border-right: none;
    border-bottom: 2px solid #f1f5f9;
    padding-right: 0;
    padding-bottom: 15px;
    margin-bottom: 15px;
    flex-direction: row;
    gap: 10px;
    align-items: baseline;
  }
}
</style>
