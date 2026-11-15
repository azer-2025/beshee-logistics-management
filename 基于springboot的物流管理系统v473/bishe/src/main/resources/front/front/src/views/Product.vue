<template>
  <div class="product-container">
    <div class="page-header">
      <div class="title-group">
        <div class="accent-bar"></div>
        <h2>商品中心</h2>
        <span class="sub-title">Products</span>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="请输入商品名称进行搜索..."
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
        <span class="filter-label">商品分类：</span>
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
          v-for="type in productTypes"
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

    <!-- 商品列表 -->
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
          <div class="image-overlay">
            <span class="view-btn">查看详情</span>
          </div>
        </div>
        
        <div class="card-content">
          <h3 class="card-title" :title="product.shangpinName">{{ product.shangpinName }}</h3>
          <div class="card-tags">
            <el-tag size="small" effect="light" type="success" class="modern-tag">
              {{ getTypeName(product.shangpinTypes) }}
            </el-tag>
            <span class="product-number">No. {{ product.shangpinUuidNumber }}</span>
          </div>
          
          <div class="product-price-row">
            <span class="price"><span class="currency">¥</span>{{ product.shangpinNewMoney }}</span>
            <span class="stock" :class="{'low-stock': product.shangpinKucunNumber < 10}">
              库存: {{ product.shangpinKucunNumber }}
            </span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 暂无数据 -->
    <el-empty v-if="products.length === 0" description="暂未找到符合条件的商品" class="custom-empty"></el-empty>

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
import { useProductStore } from '@/stores/product'

const productStore = useProductStore()

const searchQuery = ref('')
const selectedType = ref('')
const currentPage = ref(1)
const pageSize = ref(12)

const products = ref([])
const productTypes = ref([])
const total = ref(0)

const loadProducts = async () => {
  const params = {
    page: currentPage.value,
    limit: pageSize.value,
    ...(searchQuery.value && { shangpinName: searchQuery.value }),
    ...(selectedType.value && { shangpinTypes: selectedType.value })
  }
  
  await productStore.fetchProducts(params)
  products.value = productStore.products
  total.value = productStore.pagination.total
}

const loadProductTypes = async () => {
  await productStore.fetchProductTypes()
  productTypes.value = productStore.productTypes
}

const handleSearch = () => {
  currentPage.value = 1
  loadProducts()
}

const selectType = (type) => {
  selectedType.value = type
  currentPage.value = 1
  loadProducts()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadProducts()
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://images.unsplash.com/photo-1586528116311-ad8ed7c83a54?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const getTypeName = (typeId) => {
  const type = productTypes.value.find(t => t.codeIndex === typeId)
  return type ? type.indexName : '未知分类'
}

onMounted(() => {
  loadProductTypes()
  loadProducts()
})

watch([searchQuery, selectedType], () => {
  currentPage.value = 1
  loadProducts()
})
</script>

<style scoped>
.product-container {
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
  gap: 30px;
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
  height: 240px;
  object-fit: cover;
  transition: transform 0.5s ease;
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
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 12px 0;
  line-height: 1.4;
  height: 44px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.modern-tag {
  border-radius: 6px;
  font-weight: 500;
}

.product-number {
  font-size: 12px;
  color: #94a3b8;
  font-family: monospace;
}

.product-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px dashed #e2e8f0;
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
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 500;
}

.low-stock {
  color: #ea580c;
  background: #ffedd5;
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

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
