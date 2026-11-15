<template>
  <div class="alert-manage">
    <div class="page-header">
      <div>
        <h3>我的预警</h3>
        <p>查看和本人订单相关的冷链异常、发货超时、配送超时预警。</p>
      </div>
      <el-radio-group v-model="statusFilter" @change="loadAlerts">
        <el-radio-button label="active">待处理</el-radio-button>
        <el-radio-button label="all">全部</el-radio-button>
      </el-radio-group>
    </div>

    <div class="summary-row">
      <el-card shadow="hover" class="summary-card danger">
        <div class="summary-label">活动预警</div>
        <div class="summary-value">{{ activeCount }}</div>
      </el-card>
      <el-card shadow="hover" class="summary-card">
        <div class="summary-label">预警总数</div>
        <div class="summary-value">{{ total }}</div>
      </el-card>
    </div>

    <el-card shadow="hover" class="rule-card">
      <template #header>
        <div class="rule-header">
          <div>
            <h4>预警说明</h4>
            <p>系统会自动巡检与本人订单相关的冷链风险，并在满足条件时生成预警。</p>
          </div>
        </div>
      </template>

      <div class="rule-grid">
        <div v-for="rule in alertRules" :key="rule.name" class="rule-item">
          <div class="rule-top">
            <strong>{{ rule.name }}</strong>
            <el-tag size="small" :type="rule.tagType">{{ rule.scope }}</el-tag>
          </div>
          <p class="rule-trigger">触发规则：{{ rule.trigger }}</p>
          <p class="rule-note">{{ rule.note }}</p>
        </div>
      </div>
    </el-card>

    <div class="alert-list" v-loading="loading">
      <el-card v-for="item in pagedAlerts" :key="item.id" class="alert-card" shadow="hover">
        <div class="alert-top">
          <div class="title-wrap">
            <h4>{{ item.jiankongyujingName }}</h4>
            <p>{{ item.jiankongObject }}</p>
          </div>
          <div class="tag-wrap">
            <el-tag :type="levelTagType(item.yujingLevelTypes)">{{ item.yujingLevelValue }}</el-tag>
            <el-tag :type="statusTagType(item.yujingStatusTypes)">{{ item.yujingStatusValue }}</el-tag>
          </div>
        </div>

        <div class="meta-grid">
          <div><span>预警编号</span>{{ item.jiankongyujingUuidNumber }}</div>
          <div><span>预警方式</span>{{ item.yujingModeValue || '-' }}</div>
          <div><span>预警来源</span>{{ item.yujingSource || '-' }}</div>
          <div><span>监控数据</span>{{ item.jiankongData || '-' }}</div>
          <div><span>预警阈值</span>{{ item.yujingThreshold || '-' }}</div>
          <div><span>监控时间</span>{{ formatDate(item.jiankongTime) }}</div>
          <div v-if="item.shangpinOrderUuidNumber"><span>关联订单</span>{{ item.shangpinOrderUuidNumber }}</div>
        </div>

        <div class="content-block">
          <label>预警说明</label>
          <p>{{ item.yujingContent || '暂无说明' }}</p>
        </div>

        <div v-if="item.chuliContent" class="content-block result">
          <label>处理说明</label>
          <p>{{ item.chuliContent }}</p>
        </div>

        <div class="alert-actions" v-if="item.shangpinOrderId">
          <el-button
            type="primary"
            round
            class="view-order-btn"
            @click="router.push('/user/orders')"
          >
            查看关联订单
          </el-button>
        </div>
      </el-card>

      <el-empty v-if="!loading && alerts.length === 0" description="暂无预警消息" />
    </div>

    <div class="pagination-section" v-if="pagedAlerts.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, jumper"
        @current-change="loadAlerts"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const alerts = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusFilter = ref('active')
const alertRules = [
  {
    name: '订单发货超时预警',
    scope: '订单类',
    tagType: 'warning',
    trigger: '订单支付后超过 2 小时仍未发货时触发。',
    note: '提醒用户关注订单是否长时间停留在待发货状态。'
  },
  {
    name: '订单配送超时预警',
    scope: '订单类',
    tagType: 'danger',
    trigger: '订单发货后超过 6 小时仍未完成配送时触发。',
    note: '用于提示配送链路可能存在延误或异常。'
  },
  {
    name: '订单冷链温控预警',
    scope: '冷链类',
    tagType: 'danger',
    trigger: '订单对应仓库巡检发现温度超过冷链建议范围 2°C 至 8°C 时触发。',
    note: '说明该订单在待发货阶段可能存在冷链温控风险。'
  }
]

const activeCount = computed(() => alerts.value.filter((item) => [1, 2].includes(Number(item.yujingStatusTypes))).length)
const pagedAlerts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return alerts.value.slice(start, start + pageSize.value)
})

const loadAlerts = async () => {
  loading.value = true
  try {
    const data = await authStore.getUserAlerts({
      page: 1,
      limit: 200,
    })
    const list = data.list || []
    alerts.value = statusFilter.value === 'active'
      ? list.filter((item) => [1, 2].includes(Number(item.yujingStatusTypes)))
      : list
    total.value = alerts.value.length
    currentPage.value = 1
  } catch (error) {
    ElMessage.error('加载预警失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (value) => {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN')
}

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

onMounted(() => {
  loadAlerts()
})
</script>

<style scoped>
.alert-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.page-header h3 {
  margin: 0 0 8px;
  color: #1f2937;
}

.page-header p {
  margin: 0;
  color: #6b7280;
}

.summary-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.summary-card {
  border-radius: 16px;
}

.summary-card.danger {
  background: linear-gradient(135deg, #fff1f2, #ffe4e6);
}

.summary-label {
  color: #6b7280;
  font-size: 14px;
}

.summary-value {
  margin-top: 8px;
  color: #111827;
  font-size: 32px;
  font-weight: 700;
}

.rule-card {
  margin-bottom: 24px;
  border-radius: 16px;
}

.rule-header h4 {
  margin: 0 0 6px;
  color: #111827;
  font-size: 18px;
}

.rule-header p {
  margin: 0;
  color: #64748b;
}

.rule-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.rule-item {
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fbff 0%, #f8fafc 100%);
  border: 1px solid #e5eef9;
}

.rule-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.rule-top strong {
  color: #1f2937;
  font-size: 15px;
}

.rule-trigger,
.rule-note {
  margin: 0;
  line-height: 1.7;
  font-size: 13px;
}

.rule-trigger {
  color: #334155;
}

.rule-note {
  margin-top: 8px;
  color: #64748b;
}

.alert-list {
  display: grid;
  gap: 16px;
}

.alert-card {
  border-radius: 16px;
}

.alert-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.title-wrap h4 {
  margin: 0 0 8px;
  color: #111827;
  font-size: 18px;
}

.title-wrap p {
  margin: 0;
  color: #6b7280;
}

.tag-wrap {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.meta-grid div {
  padding: 12px;
  background: #f8fafc;
  border-radius: 12px;
  color: #334155;
}

.meta-grid span {
  display: block;
  margin-bottom: 6px;
  color: #94a3b8;
  font-size: 12px;
}

.content-block {
  margin-bottom: 12px;
}

.content-block label {
  display: block;
  margin-bottom: 6px;
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}

.content-block p {
  margin: 0;
  line-height: 1.7;
  color: #334155;
}

.content-block.result {
  padding: 12px;
  border-radius: 12px;
  background: #f0fdf4;
}

.alert-actions {
  margin-top: 8px;
  text-align: right;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
  }

  .summary-row {
    grid-template-columns: 1fr;
  }

  .rule-grid {
    grid-template-columns: 1fr;
  }

  .alert-top {
    flex-direction: column;
  }

  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
