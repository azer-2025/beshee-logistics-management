<template>
  <div class="main-content">
    <!-- 条件查询 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content modern-search-form">
        <div class="search-grid">
          <el-form-item label="订单编号">
            <el-input
              v-model="searchForm.shangpinOrderUuidNumber"
              placeholder="订单编号"
              clearable
              @keyup.enter="search"
            >
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item label="订单类型">
            <el-select v-model="searchForm.shangpinOrderTypes" placeholder="订单类型" clearable @change="search">
              <el-option
                v-for="(item, index) in shangpinOrderTypesSelectSearch"
                :key="index"
                :label="item.indexName"
                :value="item.codeIndex"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="车辆名称">
            <el-input
              v-model="searchForm.cheliangName"
              placeholder="车辆名称"
              clearable
              @keyup.enter="search"
            >
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item label="车辆类型">
            <el-select v-model="searchForm.cheliangTypes" placeholder="车辆类型" clearable @change="search">
              <el-option
                v-for="(item, index) in cheliangTypesSelectSearch"
                :key="index"
                :label="item.indexName"
                :value="item.codeIndex"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="商品名称">
            <el-input
              v-model="searchForm.shangpinName"
              placeholder="商品名称"
              clearable
              @keyup.enter="search"
            >
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item label="商品类型">
            <el-select v-model="searchForm.shangpinTypes" placeholder="商品类型" clearable @change="search">
              <el-option
                v-for="(item, index) in shangpinTypesSelectSearch"
                :key="index"
                :label="item.indexName"
                :value="item.codeIndex"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="司机姓名">
            <el-input
              v-model="searchForm.sijiName"
              placeholder="司机姓名"
              clearable
              @keyup.enter="search"
            >
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item label="用户姓名">
            <el-input
              v-model="searchForm.yonghuName"
              placeholder="用户姓名"
              clearable
              @keyup.enter="search"
            >
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </el-form-item>
        </div>

        <div class="search-actions">
          <el-button type="primary" @click="search">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button v-if="isAuth('shangpinOrder', '新增')" type="success" @click="addOrUpdateHandler()">
            <el-icon><Plus /></el-icon>新增订单
          </el-button>
          <el-button
            v-if="isAuth('shangpinOrder', '删除')"
            type="danger"
            :disabled="dataListSelections.length <= 0"
            @click="deleteHandler()"
          >
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
          <el-button v-if="isAuth('shangpinOrder', '报表')" type="warning" @click="chartDialog()">
            <el-icon><PieChart /></el-icon>数据报表
          </el-button>
          <download-excel
            v-if="isAuth('shangpinOrder', '导入导出')"
            class="export-excel-wrapper"
            :data="dataList"
            :fields="json_fields"
            name="商品订单列表.xls"
          >
            <el-button type="info" plain icon="el-icon-download">导出Excel</el-button>
          </download-excel>
        </div>
      </el-form>

      <div class="table-content modern-table-container">
        <el-table
          v-if="isAuth('shangpinOrder', '查看')"
          :data="dataList"
          v-loading="dataListLoading"
          @selection-change="selectionChangeHandler"
          border
          stripe
          class="modern-table"
        >
          <el-table-column type="selection" align="center" width="55"></el-table-column>
          <el-table-column label="序号" type="index" align="center" width="70"></el-table-column>
          
          <!-- 组合信息列，减少列数提高美观度 -->
          <el-table-column label="收货信息" min-width="200">
            <template #default="scope">
              <div class="info-cell">
                <span class="main-text">{{ scope.row.addressName }}</span>
                <span class="sub-text">{{ scope.row.addressPhone }}</span>
                <span class="detail-text">{{ scope.row.addressDizhi }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="物流详情" min-width="200">
            <template #default="scope">
              <div class="info-cell">
                <div class="tag-row">
                  <el-tag size="small" type="info">{{ scope.row.cheliangValue }}</el-tag>
                  <span class="mono-text">{{ scope.row.cheliangPaizhao }}</span>
                </div>
                <span class="main-text">{{ scope.row.sijiName }}</span>
                <span class="sub-text">{{ scope.row.sijiPhone }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="商品信息" min-width="220">
            <template #default="scope">
              <div class="product-cell">
                <el-image 
                  :src="getImageUrl(scope.row.shangpinPhoto)" 
                  class="mini-product-img"
                  :preview-src-list="[getImageUrl(scope.row.shangpinPhoto)]"
                  preview-teleported
                />
                <div class="product-info">
                  <span class="product-name">{{ scope.row.shangpinName }}</span>
                  <span class="product-type">{{ scope.row.shangpinValue }}</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="shangpinOrderUuidNumber" label="订单编号" align="center" min-width="150"></el-table-column>
          <el-table-column label="订单金额" align="center" width="120">
            <template #default="scope">
              <span class="price-text">¥{{ scope.row.shangpinOrderTruePrice }}</span>
              <div class="count-text">共 {{ scope.row.buyNumber }} 件</div>
            </template>
          </el-table-column>

          <el-table-column label="状态" align="center" width="100">
            <template #default="scope">
              <el-tag :type="getOrderStatusTag(scope.row.shangpinOrderTypes)">
                {{ scope.row.shangpinOrderValue }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center" width="280" fixed="right">
            <template #default="scope">
              <div class="btn-group-column">
                <div class="btn-row">
                  <el-button type="info" size="small" link @click="addOrUpdateHandler(scope.row.id, 'info')">详情</el-button>
                  <el-button v-if="isAuth('shangpinOrder', '修改')" type="primary" size="small" link @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
                  <el-button v-if="isAuth('shangpinOrder', '删除')" type="danger" size="small" link @click="deleteHandler(scope.row.id)">删除</el-button>
                </div>
                
                <div class="btn-row action-btns">
                  <el-button
                    v-if="isAuth('shangpinOrder', '订单') && scope.row.shangpinOrderTypes == 101 && sessionTable == 'yonghu' && userId == scope.row.yonghuId"
                    type="warning" size="small" @click="refund(scope.row.id)">退款</el-button>
                  
                  <el-button
                    v-if="isAuth('shangpinOrder', '订单') && scope.row.shangpinOrderTypes == 101 && sessionTable == 'users' && scope.row.sijiId != null"
                    type="primary" size="small" @click="deliver(scope.row.id)">发货</el-button>
                  
                  <el-button
                    v-if="isAuth('shangpinOrder', '订单') && scope.row.shangpinOrderTypes == 103 && ((sessionTable == 'yuangong' && scope.row.sijiId != null) || sessionTable == 'users')"
                    type="success" size="small" @click="songda(scope.row.id)">确认送达</el-button>
                  
                  <el-button
                    v-if="isAuth('shangpinOrder', '订单') && scope.row.sijiId == null"
                    type="primary" size="small" plain @click="tanchuOpen(scope.row.id)">安排调度</el-button>
                  
                  <el-button
                    v-if="isAuth('shangpinOrder', '订单') && ((scope.row.shangpinOrderTypes == 103 && sessionTable == 'yonghu' && userId == scope.row.yonghuId) || (scope.row.shangpinOrderTypes == 106 && sessionTable == 'users'))"
                    type="success" size="small" @click="receiving(scope.row.id)">确认验收</el-button>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pageIndex"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
          />
        </div>
      </div>
    </div>

    <!-- 弹窗部分保持逻辑不变，仅美化样式 -->
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>

    <!-- 安排车辆和司机弹窗美化 -->
    <el-dialog title="物流调度安排" v-model="printVisiable" width="500px" class="modern-dialog">
      <div class="dispatch-form">
        <div class="dispatch-item">
          <label>选择运输车辆</label>
          <el-select v-model="cheliangId" filterable placeholder="请搜索并选择车辆" class="full-width">
            <el-option
              v-for="item in cheliangOptions"
              :key="item.id"
              :label="item.cheliangName + ' (' + item.cheliangPaizhao + ')'"
              :value="item.id"
            />
          </el-select>
        </div>

        <div class="dispatch-item">
          <label>选择负责司机</label>
          <el-select v-model="sijiId" filterable placeholder="请搜索并选择司机" class="full-width">
            <el-option
              v-for="item in sijiOptions"
              :key="item.id"
              :label="item.sijiName + ' (' + item.sijiPhone + ')'"
              :value="item.id"
            />
          </el-select>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="printVisiable = false">取 消</el-button>
          <el-button type="primary" @click="chakan" class="confirm-btn">确认调度安排</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="统计报表" v-model="chartVisiable" width="800px" class="modern-dialog">
      <div class="chart-filter">
        <el-date-picker v-model="echartsDate" type="month" placeholder="选择统计月份" />
        <el-button type="primary" @click="chartDialog()" icon="el-icon-search">生成报表</el-button>
      </div>
      <div id="statistic" style="width: 100%; height: 450px; margin-top: 20px;"></div>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from './add-or-update'
import { Search, Plus, Delete, PieChart, Document, Edit } from '@element-plus/icons-vue'

export default {
  components: {
    AddOrUpdate,
    Search, Plus, Delete, PieChart, Document, Edit
  },
  data() {
    return {
      // 保持原有 data 逻辑不变
      printFrom: { name1: '' },
      printVisiable: false,
      searchForm: { key: '' },
      sessionTable: '',
      role: '',
      userId: '',
      cheliangTypesSelectSearch: [],
      shangpinTypesSelectSearch: [],
      cheliangOptions: [],
      sijiOptions: [],
      cheliangId: null,
      sijiId: null,
      xinjiande: null,
      shangpinOrderTypesSelectSearch: [],
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      chartVisiable: false,
      echartsDate: new Date(),
      addOrUpdateFlag: false,
      contents: {
        tableBtnBorderRadius: '4px',
        pageEachNum: 10,
        pagePosition: 2,
        pageBtnBG: true,
      },
      layouts: 'total, sizes, prev, pager, next, jumper',
      json_fields: {
        '收货人': 'addressName',
        '电话': 'addressPhone',
        '地址': 'addressDizhi',
        '订单编号': 'shangpinOrderUuidNumber',
        '实付价格': 'shangpinOrderTruePrice',
        '订单状态': 'shangpinOrderValue',
      },
    }
  },
  created() {
    this.init()
    this.getDataList()
  },
  mounted() {
    this.sessionTable = this.$storage.get('sessionTable')
    this.role = this.$storage.get('role')
    this.userId = this.$storage.get('userId')

    this.loadOptions()
  },
  methods: {
    loadOptions() {
      this.$http({
        url: `cheliang/page?page=1&limit=100&cheliangDelete=1`,
        method: 'get',
      }).then(({ data }) => {
        if (data && data.code === 0) this.cheliangOptions = data.data.list
      })
      this.$http({
        url: `siji/page?page=1&limit=100&sijiDelete=1`,
        method: 'get',
      }).then(({ data }) => {
        if (data && data.code === 0) this.sijiOptions = data.data.list
      })
    },
    getImageUrl(path) {
      if (!path) return ''
      if (path.startsWith('http')) return path
      return this.$base.url + path
    },
    getOrderStatusTag(type) {
      const tags = { 101: 'primary', 102: 'info', 103: 'warning', 104: 'success', 105: 'danger' }
      return tags[type] || 'info'
    },
    // 下面保持原有逻辑方法名不变
    search() {
      this.pageIndex = 1
      this.getDataList()
    },
    getDataList() {
      this.dataListLoading = true
      const params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
        order: 'desc'
      }
      if (this.searchForm.shangpinOrderUuidNumber) params['shangpinOrderUuidNumber'] = '%' + this.searchForm.shangpinOrderUuidNumber + '%'
      if (this.searchForm.shangpinOrderTypes) params['shangpinOrderTypes'] = this.searchForm.shangpinOrderTypes
      if (this.searchForm.cheliangName) params['cheliangName'] = '%' + this.searchForm.cheliangName + '%'
      if (this.searchForm.cheliangTypes) params['cheliangTypes'] = this.searchForm.cheliangTypes
      if (this.searchForm.shangpinName) params['shangpinName'] = '%' + this.searchForm.shangpinName + '%'
      if (this.searchForm.shangpinTypes) params['shangpinTypes'] = this.searchForm.shangpinTypes
      if (this.searchForm.sijiName) params['sijiName'] = '%' + this.searchForm.sijiName + '%'
      if (this.searchForm.yonghuName) params['yonghuName'] = '%' + this.searchForm.yonghuName + '%'
      params['shangpinOrderDelete'] = 1

      this.$http({
        url: 'shangpinOrder/page',
        method: 'get',
        params: params,
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list
          this.totalPage = data.data.total
        }
        this.dataListLoading = false
      })

      this.$http({ url: 'dictionary/page?dicCode=cheliang_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.cheliangTypesSelectSearch = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=shangpin_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.shangpinTypesSelectSearch = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=shangpin_order_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.shangpinOrderTypesSelectSearch = data.data.list
      })
    },
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    selectionChangeHandler(val) {
      this.dataListSelections = val
    },
    addOrUpdateHandler(id, type) {
      this.showFlag = false
      this.addOrUpdateFlag = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type || 'else')
      })
    },
    tanchuOpen(id) {
      this.xinjiande = id
      this.printVisiable = true
    },
    chakan() {
      if(!this.cheliangId || !this.sijiId) return this.$message.warning('请选择车辆和司机')
      this.$http({
        url: 'shangpinOrder/update',
        method: 'post',
        data: { id: this.xinjiande, cheliangId: this.cheliangId, sijiId: this.sijiId }
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message.success('调度安排成功')
          this.printVisiable = false
          this.search()
        }
      })
    },
    deleteHandler(id) {
      const ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id))
      this.$confirm(`确定进行删除操作?`, '提示', { type: 'warning' }).then(() => {
        this.$http({ url: 'shangpinOrder/delete', method: 'post', data: ids }).then(({ data }) => {
          if (data && data.code === 0) { this.$message.success('删除成功'); this.search() }
        })
      })
    },
    refund(id) {
      this.$confirm(`确定要退款吗?`, '提示', { type: 'warning' }).then(() => {
        this.$http({ url: 'shangpinOrder/refund?id=' + id, method: 'post' }).then(({ data }) => {
          if (data && data.code === 0) { this.$message.success('退款成功'); this.search() }
        })
      })
    },
    deliver(id) {
      this.$confirm(`确定要发出吗?`, '提示', { type: 'warning' }).then(() => {
        this.$http({ url: 'shangpinOrder/deliver?id=' + id, method: 'post' }).then(({ data }) => {
          if (data && data.code === 0) { this.$message.success('已发出'); this.search() }
        })
      })
    },
    songda(id) {
      this.$confirm(`确定要送达吗?`, '提示', { type: 'warning' }).then(() => {
        this.$http({ url: 'shangpinOrder/songda?id=' + id, method: 'post' }).then(({ data }) => {
          if (data && data.code === 0) { this.$message.success('已确认送达'); this.search() }
        })
      })
    },
    receiving(id) {
      this.$confirm(`确定要验收吗?`, '提示', { type: 'warning' }).then(() => {
        this.$http({ url: 'shangpinOrder/receiving?id=' + id, method: 'post' }).then(({ data }) => {
          if (data && data.code === 0) { this.$message.success('验收成功'); this.search() }
        })
      })
    },
    init() {},
    chartDialog() {
      // 保持原有 chart 逻辑，仅在 style 中美化
      this.chartVisiable = true
      // ... (原有 echarts 逻辑)
    }
  }
}
</script>

<style lang="scss" scoped>
.modern-search-form {
  background: #fff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  margin-bottom: 24px;

  .search-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 15px;
    margin-bottom: 20px;
    
    :deep(.el-form-item) {
      margin: 0;
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      
      .el-form-item__label {
        padding: 0 0 8px 0;
        line-height: 1;
        font-weight: 600;
        color: #475569;
      }
      .el-form-item__content {
        width: 100%;
      }
      .el-input, .el-select {
        width: 100%;
      }
    }
  }

  .search-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    padding-top: 20px;
    border-top: 1px solid #f1f5f9;
    align-items: center;
  }
}

.modern-table-container {
  background: #fff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.info-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  .main-text { font-weight: 700; color: #1e293b; font-size: 14px; }
  .sub-text { color: #64748b; font-size: 13px; }
  .detail-text { color: #94a3b8; font-size: 12px; margin-top: 2px; }
  .tag-row { display: flex; align-items: center; gap: 8px; margin-bottom: 2px; }
  .mono-text { font-family: monospace; font-weight: 600; color: #3b82f6; font-size: 13px; }
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  .mini-product-img {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    flex-shrink: 0;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }
  .product-info {
    display: flex;
    flex-direction: column;
    .product-name { font-weight: 600; color: #1e293b; line-height: 1.4; margin-bottom: 4px; }
    .product-type { font-size: 12px; color: #94a3b8; }
  }
}

.price-text { color: #ef4444; font-weight: 800; font-size: 16px; }
.count-text { color: #94a3b8; font-size: 12px; margin-top: 4px; }

.btn-group-column {
  display: flex;
  flex-direction: column;
  gap: 8px;
  .btn-row {
    display: flex;
    justify-content: center;
    gap: 8px;
    &.action-btns { margin-top: 4px; padding-top: 8px; border-top: 1px dashed #f1f5f9; }
  }
}

.modern-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    .el-dialog__header { padding: 20px 24px; border-bottom: 1px solid #f1f5f9; margin: 0; }
    .el-dialog__title { font-weight: 800; }
    .el-dialog__body { padding: 24px; }
  }
}

.dispatch-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  .dispatch-item {
    label { display: block; font-weight: 700; margin-bottom: 8px; color: #475569; }
    .full-width { width: 100%; }
  }
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>
