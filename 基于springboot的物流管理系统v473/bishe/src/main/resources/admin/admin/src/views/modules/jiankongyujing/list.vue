<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-card shadow="never" class="search-card">
        <el-form :inline="true" :model="searchForm" class="modern-search-form">
          <el-form-item label="预警标题">
            <el-input v-model="searchForm.jiankongyujingName" clearable placeholder="请输入预警标题" />
          </el-form-item>
          <el-form-item label="监控类型">
            <el-select v-model="searchForm.jiankongTypes" clearable placeholder="请选择监控类型">
              <el-option v-for="item in jiankongTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
            </el-select>
          </el-form-item>
          <el-form-item label="预警级别">
            <el-select v-model="searchForm.yujingLevelTypes" clearable placeholder="请选择预警级别">
              <el-option v-for="item in yujingLevelTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
            </el-select>
          </el-form-item>
          <el-form-item label="预警状态">
            <el-select v-model="searchForm.yujingStatusTypes" clearable placeholder="请选择预警状态">
              <el-option v-for="item in yujingStatusTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
            </el-select>
          </el-form-item>
          <el-form-item label="预警方式">
            <el-select v-model="searchForm.yujingModeTypes" clearable placeholder="请选择预警方式">
              <el-option v-for="item in yujingModeTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
            </el-select>
          </el-form-item>
          <el-form-item label="监控对象">
            <el-input v-model="searchForm.jiankongObject" clearable placeholder="请输入监控对象" />
          </el-form-item>
          <el-form-item label="预警来源">
            <el-input v-model="searchForm.yujingSource" clearable placeholder="请输入预警来源" />
          </el-form-item>
          <div class="search-actions">
            <el-button type="primary" @click="search">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
        </el-form>
        <div class="toolbar">
          <el-button v-if="isAuth('jiankongyujing','新增')" type="primary" @click="addOrUpdateHandler()">新增预警</el-button>
          <el-button
            v-if="isAuth('jiankongyujing','删除')"
            type="danger"
            :disabled="dataListSelections.length <= 0"
            @click="deleteHandler()"
          >
            批量删除
          </el-button>
        </div>
      </el-card>

      <el-card shadow="never" class="table-card">
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" width="55" />
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="jiankongyujingUuidNumber" label="预警编号" min-width="180" show-overflow-tooltip />
          <el-table-column prop="jiankongyujingName" label="预警标题" min-width="180" show-overflow-tooltip />
          <el-table-column prop="jiankongValue" label="监控类型" min-width="120" />
          <el-table-column label="预警级别" min-width="100">
            <template #default="scope">
              <el-tag :type="levelTagType(scope.row.yujingLevelTypes)">{{ scope.row.yujingLevelValue }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="预警状态" min-width="100">
            <template #default="scope">
              <el-tag :type="statusTagType(scope.row.yujingStatusTypes)">{{ scope.row.yujingStatusValue }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="yujingModeValue" label="预警方式" min-width="100" />
          <el-table-column prop="yujingSource" label="预警来源" min-width="140" show-overflow-tooltip />
          <el-table-column prop="shangpinOrderUuidNumber" label="关联订单" min-width="150" show-overflow-tooltip />
          <el-table-column prop="yonghuName" label="关联用户" min-width="120" show-overflow-tooltip />
          <el-table-column prop="jiankongObject" label="监控对象" min-width="180" show-overflow-tooltip />
          <el-table-column prop="jiankongData" label="监控数据" min-width="120" show-overflow-tooltip />
          <el-table-column prop="yujingThreshold" label="预警阈值" min-width="130" show-overflow-tooltip />
          <el-table-column prop="jiankongTime" label="监控时间" min-width="170" />
          <el-table-column prop="chuliTime" label="处理时间" min-width="170" />
          <el-table-column label="操作" fixed="right" width="220">
            <template #default="scope">
              <el-button v-if="isAuth('jiankongyujing','查看')" type="success" link @click="addOrUpdateHandler(scope.row.id, 'info')">
                详情
              </el-button>
              <el-button v-if="isAuth('jiankongyujing','修改')" type="primary" link @click="addOrUpdateHandler(scope.row.id)">
                修改
              </el-button>
              <el-button v-if="isAuth('jiankongyujing','删除')" type="danger" link @click="deleteHandler(scope.row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :current-page="pageIndex"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalPage"
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
          />
        </div>
      </el-card>
    </div>

    <add-or-update v-if="addOrUpdateFlag" ref="addOrUpdate" :parent="this" />
  </div>
</template>

<script>
import AddOrUpdate from './add-or-update'

export default {
  components: { AddOrUpdate },
  data() {
    return {
      showFlag: true,
      addOrUpdateFlag: false,
      dataListLoading: false,
      dataList: [],
      dataListSelections: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      searchForm: {
        jiankongyujingName: '',
        jiankongTypes: '',
        yujingLevelTypes: '',
        yujingStatusTypes: '',
        yujingModeTypes: '',
        jiankongObject: '',
        yujingSource: '',
      },
      jiankongTypesOptions: [],
      yujingLevelTypesOptions: [],
      yujingStatusTypesOptions: [],
      yujingModeTypesOptions: [],
    }
  },
  created() {
    this.loadSelectOptions()
    this.getDataList()
  },
  methods: {
    loadSelectOptions() {
      this.$http({ url: 'dictionary/page?dicCode=jiankong_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.jiankongTypesOptions = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=yujing_level_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.yujingLevelTypesOptions = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=yujing_status_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.yujingStatusTypesOptions = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=yujing_mode_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.yujingModeTypesOptions = data.data.list
      })
    },
    search() {
      this.pageIndex = 1
      this.getDataList()
    },
    resetSearch() {
      this.searchForm = {
        jiankongyujingName: '',
        jiankongTypes: '',
        yujingLevelTypes: '',
        yujingStatusTypes: '',
        yujingModeTypes: '',
        jiankongObject: '',
        yujingSource: '',
      }
      this.search()
    },
    getDataList() {
      this.dataListLoading = true
      const params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
        order: 'desc',
      }
      if (this.searchForm.jiankongyujingName) params.jiankongyujingName = this.searchForm.jiankongyujingName
      if (this.searchForm.jiankongTypes) params.jiankongTypes = this.searchForm.jiankongTypes
      if (this.searchForm.yujingLevelTypes) params.yujingLevelTypes = this.searchForm.yujingLevelTypes
      if (this.searchForm.yujingStatusTypes) params.yujingStatusTypes = this.searchForm.yujingStatusTypes
      if (this.searchForm.yujingModeTypes) params.yujingModeTypes = this.searchForm.yujingModeTypes
      if (this.searchForm.jiankongObject) params.jiankongObject = this.searchForm.jiankongObject
      if (this.searchForm.yujingSource) params.yujingSource = this.searchForm.yujingSource

      this.$http({
        url: 'jiankongyujing/page',
        method: 'get',
        params,
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list
          this.totalPage = data.data.total
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      }).catch(() => {
        this.dataList = []
        this.totalPage = 0
        this.dataListLoading = false
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
        this.$refs.addOrUpdate.init(id, type || 'edit')
      })
    },
    deleteHandler(id) {
      const ids = id ? [Number(id)] : this.dataListSelections.map((item) => Number(item.id))
      this.$confirm(`确定进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.$http({
          url: 'jiankongyujing/delete',
          method: 'post',
          data: ids,
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              type: 'success',
              message: '操作成功',
              duration: 1500,
              onClose: () => {
                this.search()
              },
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      })
    },
    levelTagType(level) {
      if (level === 3) return 'danger'
      if (level === 2) return 'warning'
      return 'info'
    },
    statusTagType(status) {
      if (status === 3) return 'success'
      if (status === 2) return 'warning'
      return 'danger'
    },
  },
}
</script>

<style lang="scss" scoped>
.main-content {
  padding: 20px;
  background: #f8fafc;
  min-height: calc(100vh - 84px);
}

.search-card {
  margin-bottom: 20px;
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.03);
  background: #fff;

  :deep(.el-card__body) {
    padding: 24px;
  }

  .modern-search-form {
    display: flex;
    flex-wrap: wrap;
    gap: 16px 24px;

    :deep(.el-form-item) {
      margin-right: 0;
      margin-bottom: 0;
      display: flex;
      align-items: center;

      .el-form-item__label {
        font-weight: 600;
        color: #475569;
        padding-right: 12px;
        font-size: 14px;
      }

      .el-input,
      .el-select {
        width: 180px;

        .el-input__wrapper {
          background-color: #f1f5f9;
          box-shadow: none !important;
          border: 1px solid transparent;
          transition: all 0.2s;
          border-radius: 8px;

          &:hover {
            background-color: #e2e8f0;
          }

          &.is-focus {
            background-color: #fff;
            border-color: #0f766e;
            box-shadow: 0 0 0 1px #0f766e !important;
          }
        }
      }
    }

    .search-actions {
      margin-left: auto;
      display: flex;
      gap: 12px;

      .el-button {
        padding: 8px 20px;
        border-radius: 8px;
        font-weight: 600;
        transition: all 0.2s;

        &--primary {
          background: #0f766e;
          border-color: #0f766e;

          &:hover {
            background: #134e4a;
            border-color: #134e4a;
            transform: translateY(-1px);
            box-shadow: 0 4px 6px -1px rgba(15, 118, 110, 0.2);
          }
        }

        &:not(.el-button--primary) {
          color: #64748b;
          border-color: #e2e8f0;

          &:hover {
            color: #1e293b;
            background: #f1f5f9;
            border-color: #cbd5e1;
          }
        }
      }
    }
  }
}

.toolbar {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
  display: flex;
  gap: 12px;

  .el-button {
    border-radius: 8px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 6px;

    &--primary {
      background: #0f766e;
      border-color: #0f766e;
    }
  }
}

.table-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  :deep(.el-table) {
    border-radius: 8px;
    overflow: hidden;

    th.el-table__cell {
      background-color: #f8fafc !important;
      color: #475569;
      font-weight: 700;
      height: 48px;
    }

    .el-table__row {
      transition: all 0.2s;
      &:hover > td.el-table__cell {
        background-color: #f1f5f9 !important;
      }
    }
  }
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding: 0 4px;
}
</style>
