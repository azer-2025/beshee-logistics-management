<template>
  <div class="product-detail-container" v-if="product">
    <div class="product-header">
      <el-button @click="$router.go(-1)" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
    
    <div class="product-content">
      <div class="product-images">
        <img 
          :src="getImageUrl(product.shangpinPhoto)" 
          :alt="product.shangpinName"
          class="main-image"
        />
      </div>
      
      <div class="product-info">
        <h1>{{ product.shangpinName }}</h1>
        <p class="product-number">编号: {{ product.shangpinUuidNumber }}</p>
        
        <div class="price-section">
          <span class="current-price">¥{{ product.shangpinNewMoney }}</span>
          <span v-if="product.shangpinOldMoney" class="old-price">¥{{ product.shangpinOldMoney }}</span>
        </div>
        
        <div class="product-details">
          <div class="detail-item">
            <span class="label">商品类型:</span>
            <span class="value">{{ getTypeName(product.shangpinTypes) }}</span>
          </div>
          
          <div class="detail-item">
            <span class="label">库存数量:</span>
            <span class="value">{{ product.shangpinKucunNumber }} 件</span>
          </div>
          
          <div class="detail-item">
            <span class="label">商品介绍:</span>
            <span class="value">{{ product.shangpinContent }}</span>
          </div>
        </div>
        
        <div class="action-buttons">

          
          <el-button 
            type="warning" 
            size="large" 
            @click="buyNow"
            :disabled="product.shangpinKucunNumber <= 0"
          >
            立即购买
          </el-button>
          
          <el-button 
            :type="isCollected ? 'danger' : 'default'"
            size="large"
            @click="toggleCollection"
          >
            <el-icon><Star /></el-icon>
            {{ isCollected ? '取消收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const cartStore = useCartStore()

const product = ref(null)
const loading = ref(false)

const isCollected = computed(() => {
  // 需要检查返回数据的结构，根据实际返回的数据结构调整比较方式
  return productStore.collectedProducts.some(p =>
      p.shangpinId === product.value?.id
  )
})

const loadProductDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    await productStore.fetchProductDetail(id)
    product.value = productStore.currentProduct
  } catch (error) {
    ElMessage.error('加载商品详情失败')
  } finally {
    loading.value = false
  }
}

const getImageUrl = (imagePath) => {
  if (!imagePath) return 'https://via.placeholder.com/400x400?text=商品图片'
  if (imagePath.startsWith('http')) return imagePath
  return `${import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong'}/${imagePath}`
}

const getTypeName = (typeId) => {
  const type = productStore.productTypes.find(t => t.codeIndex === typeId)
  return type ? type.indexName : '未知类型'
}


const buyNow = () => {
  if (!product.value) return
  
  // 直接跳转到订单确认页面
  router.push({
    name: 'OrderConfirm',
    query: {
      goodsId: product.value.id,
      buyNumber: 1
    }
  })
}

const toggleCollection = async () => {
  if (!product.value) return

  try {
    if (isCollected.value) {
      // 需要找到对应的收藏记录ID来删除
      const collectedItem = productStore.collectedProducts.find(p =>
          p.shangpinId === product.value.id
      )
      if (collectedItem) {
        await productStore.removeFavorite(collectedItem.id)
        ElMessage.success('已取消收藏')
      }
    } else {
      await productStore.collectProduct(product.value.id)
      ElMessage.success('收藏成功')
    }
    // 重新获取收藏列表以更新状态
    await productStore.fetchFavorites()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}


onMounted(async () => {
  await loadProductDetail()
  // 初始化收藏列表
  await productStore.fetchFavorites()
})
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-header {
  margin-bottom: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 5px;
}

.product-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: white;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.product-images .main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 10px;
}

.product-info h1 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #333;
}

.product-number {
  color: #666;
  margin-bottom: 20px;
}

.price-section {
  margin-bottom: 30px;
}

.current-price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
  margin-right: 15px;
}

.old-price {
  font-size: 20px;
  color: #999;
  text-decoration: line-through;
}

.product-details {
  margin-bottom: 40px;
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

.action-buttons {
  display: flex;
  gap: 15px;
}

@media (max-width: 768px) {
  .product-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .product-images .main-image {
    height: 300px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>