<template>
  <div class="warehouse-detail-container" v-if="warehouse">
    <div class="page-header">
      <el-button @click="$router.go(-1)" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <div class="warehouse-content">
      <div class="warehouse-images">
        <img
            :src="getImageUrl(warehouse.cangkuPhoto)"
            :alt="warehouse.cangkuName"
            class="main-image"
        />
      </div>

      <div class="warehouse-info">
        <h1>{{ warehouse.cangkuName }}</h1>
        <p class="warehouse-number">编号: {{ warehouse.cangkuUuidNumber }}</p>

        <div class="warehouse-details">
          <div class="detail-item">
            <span class="label">仓库类型:</span>
            <span class="value">{{ getTypeName(warehouse.cangkuTypes) }}</span>
          </div>

          <div class="detail-item">
            <span class="label">仓库地址:</span>
            <span class="value">{{ warehouse.cangkuAddress }}</span>
          </div>

          <div class="detail-item">
            <span class="label">创建时间:</span>
            <span class="value">{{ formatDate(warehouse.insertTime) }}</span>
          </div>
        </div>

        <div class="contact-section">
          <h3>联系信息</h3>
          <div class="contact-info">
            <div class="contact-item">
              <el-icon><Phone /></el-icon>
              <span>联系电话: 400-123-4567</span>
            </div>
            <div class="contact-item">
              <el-icon><Message /></el-icon>
              <span>客服邮箱: service@logistics.com</span>
            </div>
            <div class="contact-item">
              <el-icon><Location /></el-icon>
              <span>工作时间: 周一至周日 8:00-18:00</span>
            </div>
          </div>
        </div>

        <!-- 使用 Popover 显示仓库介绍 -->
        <div class="warehouse-introduction">
          <el-popover
              placement="top"
              :width="500"
              trigger="click"
          >
            <template #reference>
              <el-button
                  plain
                  :type="null"
                  class="detail-button"
              >
                查看仓库详细介绍
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </template>

            <div class="popover-content">
              <h3>仓库详细介绍</h3>
              <div class="warehouse-content-text" v-html="warehouse.cangkuContent"></div>
            </div>
          </el-popover>
        </div>

      </div>
    </div>

    <!-- 修改 related-products 部分 -->
    <div class="related-products">
      <h3>仓库商品</h3>
      <div class="products-grid">
        <div
            v-for="product in paginatedProducts"
            :key="product.id"
            class="product-card image-hover"
            @click="$router.push(`/shangpin/${product.id}`)"
        >
          <img
              :src="getImageUrl(product.shangpinPhoto)"
              :alt="product.shangpinName"
              class="product-image"
          />
          <div class="product-info">
            <h4>{{ product.shangpinName }}</h4>
            <p class="price">¥{{ product.shangpinNewMoney }}</p>
          </div>
        </div>
      </div>

      <!-- 添加分页组件 -->
      <div class="pagination-section">
        <el-pagination
            v-model:current-page="relatedCurrentPage"
            :page-size="relatedPageSize"
            :total="relatedProducts.length"
            layout="prev, pager, next, jumper"
            @current-change="handleRelatedPageChange"
        />
      </div>
    </div>

  </div>
</template>




<script setup>
import { ref, onMounted, computed } from 'vue' // 添加 computed 引入
import { useRoute, useRouter } from 'vue-router'
import { useWarehouseStore } from '@/stores/warehouse'
import { useProductStore } from '@/stores/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const warehouseStore = useWarehouseStore()
const productStore = useProductStore()

const warehouse = ref(null)
const relatedProducts = ref([])
const loading = ref(false)

// 相关商品分页状态
const relatedCurrentPage = ref(1)
const relatedPageSize = ref(10)

// 计算当前页的商品
const paginatedProducts = computed(() => {
  const start = (relatedCurrentPage.value - 1) * relatedPageSize.value
  const end = start + relatedPageSize.value
  return relatedProducts.value.slice(start, end)
})

const loadWarehouseDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    await warehouseStore.fetchWarehouseDetail(id)
    warehouse.value = warehouseStore.currentWarehouse

    // 加载相关商品
    await loadRelatedProducts()
  } catch (error) {
    ElMessage.error('加载仓库详情失败')
  } finally {
    loading.value = false
  }
}

const loadRelatedProducts = async () => {
  try {
    await productStore.fetchProducts({ limit: 200 }) // 获取更多商品用于分页
    // 筛选出与当前仓库关联的商品
    if (warehouse.value && warehouse.value.id) {
      relatedProducts.value = productStore.products.filter(product =>
          product.cangkuId === warehouse.value.id
      ).slice(0, 30) // 限制最多30个相关商品
    } else {
      relatedProducts.value = productStore.products.slice(0, 30)
    }

    // 重置分页
    relatedCurrentPage.value = 1
  } catch (error) {
    console.error('加载相关商品失败:', error)
  }
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://via.placeholder.com/400x300?text=仓库图片'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const getTypeName = (typeId) => {
  const type = warehouseStore.warehouseTypes.find(t => t.codeIndex === typeId)
  return type ? type.indexName : '未知类型'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 处理相关商品分页变化
const handleRelatedPageChange = (page) => {
  relatedCurrentPage.value = page
}

onMounted(() => {
  loadWarehouseDetail()
})
</script>


<style scoped>
.warehouse-detail-container {
  max-width: 1200px;
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

.warehouse-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: white;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.warehouse-images .main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 10px;
}

.warehouse-info h1 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #333;
}

.warehouse-number {
  color: #666;
  margin-bottom: 20px;
}

.warehouse-details {
  margin-bottom: 20px;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.detail-item .label {
  font-weight: bold;
  color: #333;
  min-width: 100px;
}

.detail-item .value {
  color: #666;
  flex: 1;
}

.contact-section {
  margin-top: 20px;
}

.contact-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.contact-info {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  color: #666;
}

.related-products {
  margin-top: 40px;
}

.related-products h3 {
  margin-bottom: 20px;
  color: #333;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-3px);
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-info h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
}

.price {
  color: #f56c6c;
  font-weight: bold;
  margin: 0;
}

@media (max-width: 768px) {
  .warehouse-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .warehouse-images .main-image {
    height: 300px;
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}

/* 在样式部分添加以下内容 */
.warehouse-content-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.warehouse-content-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.warehouse-content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  text-align: justify;
}

/* 为 HTML 内容中的元素添加基础样式 */
.warehouse-content-text :deep(p) {
  margin-bottom: 15px;
}

.warehouse-content-text :deep(h1),
.warehouse-content-text :deep(h2),
.warehouse-content-text :deep(h3),
.warehouse-content-text :deep(h4),
.warehouse-content-text :deep(h5),
.warehouse-content-text :deep(h6) {
  margin: 20px 0 15px 0;
  color: #333;
}

.warehouse-content-text :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 10px 0;
}

.warehouse-content-text :deep(ul),
.warehouse-content-text :deep(ol) {
  padding-left: 20px;
  margin-bottom: 15px;
}

.warehouse-content-text :deep(li) {
  margin-bottom: 5px;
}

/* 在现有样式基础上添加以下内容 */
.warehouse-introduction {
  margin-top: 30px;
  text-align: left;
}

.popover-content {
  max-height: 400px;
  overflow-y: auto;
}

.popover-content h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
  text-align: center;
}

/* 为 Popover 内的 HTML 内容添加样式 */
.popover-content :deep(.warehouse-content-text) {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.popover-content :deep(.warehouse-content-text p) {
  margin-bottom: 10px;
}

.popover-content :deep(.warehouse-content-text h1),
.popover-content :deep(.warehouse-content-text h2),
.popover-content :deep(.warehouse-content-text h3),
.popover-content :deep(.warehouse-content-text h4),
.popover-content :deep(.warehouse-content-text h5),
.popover-content :deep(.warehouse-content-text h6) {
  margin: 15px 0 10px 0;
  color: #333;
}

.popover-content :deep(.warehouse-content-text img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 8px 0;
}

.popover-content :deep(.warehouse-content-text ul),
.popover-content :deep(.warehouse-content-text ol) {
  padding-left: 15px;
  margin-bottom: 10px;
}

.popover-content :deep(.warehouse-content-text li) {
  margin-bottom: 3px;
}

.detail-button {
  background-color: white;
  color: #333;
  border: 1px solid #dcdfe6;
  padding: 12px 20px;
  font-size: 14px;
  width: 30%;
  justify-content: space-between;
}

.detail-button:hover {
  background-color: #f5f7fa;
  border-color: #c0c4cc;
}

.detail-button:focus {
  border-color: #409eff;
}

/* 在样式部分添加以下内容 */
.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}


</style>