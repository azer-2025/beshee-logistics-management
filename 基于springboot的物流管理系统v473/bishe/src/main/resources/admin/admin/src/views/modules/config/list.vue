<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content modern-search-form">
        <el-form-item label="轮播图名称">
          <el-input v-model="searchForm.name" placeholder="输入名称搜索" clearable @keyup.enter="search">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button v-if="isAuth('config','新增')" type="success" @click="addOrUpdateHandler()">
            <el-icon><Plus /></el-icon>新增轮播
          </el-button>
          <el-button v-if="isAuth('config','删除')" type="danger" :disabled="dataListSelections.length <= 0" @click="deleteHandler()">
            <el-icon><Delete /></el-icon>批量删除
          </el-button>
        </el-form-item>
      </el-form>

      <div class="table-content modern-table-container">
        <el-table
          v-if="isAuth('config','查看')"
          :data="dataList"
          v-loading="dataListLoading"
          @selection-change="selectionChangeHandler"
          style="width: 100%"
          border
          stripe
          class="modern-table"
        >
          <el-table-column type="selection" align="center" width="55"></el-table-column>
          <el-table-column label="序号" type="index" align="center" width="70"></el-table-column>
          <el-table-column prop="name" label="名称" align="center" min-width="150"></el-table-column>
          <el-table-column label="预览图" align="center" width="220">
            <template #default="scope">
              <div class="image-preview-box" v-if="scope.row.value">
                <el-image 
                  :src="getImageUrl(scope.row.value)" 
                  fit="cover" 
                  class="table-img"
                  :preview-src-list="[getImageUrl(scope.row.value)]"
                  preview-teleported
                />
              </div>
              <el-tag v-else type="info" size="small">暂无图片</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="管理操作" align="center" width="280" fixed="right">
            <template #default="scope">
              <el-button v-if="isAuth('config','查看')" type="info" size="small" plain @click="addOrUpdateHandler(scope.row.id,'info')">
                <el-icon><Document /></el-icon>详情
              </el-button>
              <el-button v-if="isAuth('config','修改')" type="primary" size="small" plain @click="addOrUpdateHandler(scope.row.id)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-button v-if="isAuth('config','删除')" type="danger" size="small" plain @click="deleteHandler(scope.row.id)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
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
    <!-- 修复 parent 引用 -->
    <add-or-update v-if="addOrUpdateFlag" :parent="parentInstance" ref="addOrUpdate"></add-or-update>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue';
import AddOrUpdate from "./add-or-update.vue";
import { Search, Plus, Delete, Edit, Document } from '@element-plus/icons-vue';
import { getCurrentInstance } from 'vue';

const { proxy } = getCurrentInstance();

const searchForm = reactive({ name: "" });
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const showFlag = ref(true);
const addOrUpdateFlag = ref(false);

// 创建一个模拟 parent 实例的对象，供子组件调用
const parentInstance = reactive({
  get showFlag() { return showFlag.value },
  set showFlag(val) { showFlag.value = val },
  get addOrUpdateFlag() { return addOrUpdateFlag.value },
  set addOrUpdateFlag(val) { addOrUpdateFlag.value = val },
  search: () => search()
});

const getImageUrl = (value) => {
  if (!value) return '';
  const firstImg = value.split(',')[0];
  if (firstImg.startsWith('http')) return firstImg;
  return proxy.$base.url + firstImg;
};

const search = () => {
  pageIndex.value = 1;
  getDataList();
};

const getDataList = () => {
  dataListLoading.value = true;
  let params = {
    page: pageIndex.value,
    limit: pageSize.value,
    sort: 'id',
  };
  if (searchForm.name) params['name'] = '%' + searchForm.name + '%';
  
  proxy.$http({
    url: "config/page",
    method: "get",
    params: params
  }).then(({ data }) => {
    if (data && data.code === 0) {
      dataList.value = data.data.list;
      totalPage.value = data.data.total;
    } else {
      dataList.value = [];
      totalPage.value = 0;
    }
    dataListLoading.value = false;
  });
};

const sizeChangeHandle = (val) => {
  pageSize.value = val;
  pageIndex.value = 1;
  getDataList();
};

const currentChangeHandle = (val) => {
  pageIndex.value = val;
  getDataList();
};

const selectionChangeHandler = (val) => {
  dataListSelections.value = val;
};

const addOrUpdateHandler = (id, type) => {
  showFlag.value = false;
  addOrUpdateFlag.value = true;
  nextTick(() => {
    proxy.$refs.addOrUpdate.init(id, type === 'info' ? 'info' : 'else');
  });
};

const deleteHandler = (id) => {
  let ids = id ? [Number(id)] : dataListSelections.value.map(item => Number(item.id));
  proxy.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    proxy.$http({
      url: "config/delete",
      method: "post",
      data: ids
    }).then(({ data }) => {
      if (data && data.code === 0) {
        proxy.$message.success("操作成功");
        search();
      } else {
        proxy.$message.error(data.msg);
      }
    });
  });
};

onMounted(() => {
  getDataList();
});
</script>

<style lang="scss" scoped>
.modern-search-form {
  margin-bottom: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.modern-table-container {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.image-preview-box {
  padding: 8px;
  .table-img {
    width: 160px;
    height: 90px;
    border-radius: 8px;
    cursor: zoom-in;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    transition: transform 0.3s;
    &:hover {
      transform: scale(1.05);
    }
  }
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table__header) {
  th {
    background-color: #f8fafc !important;
    color: #475569 !important;
    font-weight: 700;
    height: 50px;
  }
}

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
}
</style>
