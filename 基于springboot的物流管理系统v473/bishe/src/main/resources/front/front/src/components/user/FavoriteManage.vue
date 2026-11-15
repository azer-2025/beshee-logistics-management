<template>
  <div class="favorite-manage">
    <div class="page-header">
      <h3>我的收藏</h3>
      <el-button type="danger" @click="clearAllFavorites" v-if="favorites.length > 0">
        <el-icon><Delete /></el-icon>
        清空收藏
      </el-button>
    </div>

    <!-- 搜索 -->
    <div class="filter-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索收藏商品"
        style="max-width: 300px"
        @input="handleSearch"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 收藏列表 -->
    <div class="favorites-grid">
      <el-card
        v-for="item in favorites"
        :key="item.id"
        class="favorite-card image-hover"
      >
        <div class="favorite-content">
          <img
            :src="getImageUrl(item.shangpinPhoto)"
            :alt="item.shangpinName"
            class="product-image"
            @click="$router.push(`/shangpin/${item.shangpinId}`)"
          />
          
          <div class="product-info">
            <h4 @click="$router.push(`/shangpin/${item.shangpinId}`)">{{ item.shangpinName }}</h4>
            <p class="product-price">¥{{ item.shangpinNewMoney }}</p>
            <p class="product-stock">库存: {{ item.shangpinKucunNumber }} 件</p>
            <p class="add-time">收藏时间: {{ formatDate(item.insertTime) }}</p>
          </div>

          <div class="favorite-actions">
            <el-button type="danger" size="small" @click="removeFavorite(item.id)">
              取消收藏
            </el-button>
          </div>
        </div>
      </el-card>

      <el-empty v-if="favorites.length === 0" description="暂无收藏商品">
        <el-button type="primary" @click="$router.push('/shangpin')">
          去逛逛
        </el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination-section" v-if="favorites.length > 0">
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
import { ref, onMounted } from 'vue'
import { useProductStore } from '@/stores/product'
import { useCartStore } from '@/stores/cart'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Search } from '@element-plus/icons-vue'

const productStore = useProductStore()
const cartStore = useCartStore()

const favorites = ref([])
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadFavorites = async () => {
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      ...(searchQuery.value && { shangpinName: searchQuery.value })
    }
    
    const response = await productStore.fetchFavorites(params)
    favorites.value = response.list || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('加载收藏失败')
  }
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://via.placeholder.com/200x200?text=商品图片'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const handleSearch = () => {
  currentPage.value = 1
  loadFavorites()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadFavorites()
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const removeFavorite = async (id) => {
  try {
    await productStore.removeFavorite(id)
    favorites.value = favorites.value.filter(item => item.id !== id)
    total.value--
    ElMessage.success('已取消收藏')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const clearAllFavorites = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有收藏吗？此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await productStore.clearAllFavorites()
    favorites.value = []
    total.value = 0
    ElMessage.success('已清空收藏')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorite-manage {
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

.filter-section {
  margin-bottom: 30px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.favorite-card {
  transition: transform 0.3s;
}

.favorite-card:hover {
  transform: translateY(-3px);
}

.favorite-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}

.product-info {
  flex: 1;
  padding: 15px 0;
}

.product-info h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
  cursor: pointer;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-info h4:hover {
  color: #409eff;
}

.product-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
  margin: 5px 0;
}

.product-stock {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
}

.add-time {
  color: #999;
  font-size: 12px;
  margin: 5px 0;
}

.favorite-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>