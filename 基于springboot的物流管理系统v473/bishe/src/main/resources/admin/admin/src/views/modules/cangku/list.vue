<template>
  <div class="main-content">
    <!-- 条件查询 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{justifyContent:contents.searchBoxPosition=='1'?'flex-start':contents.searchBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item :label="contents.inputTitle == 1 ? '仓库名称' : ''">
            <el-input
              prefix-icon="el-icon-search"
              v-model="searchForm.cangkuName"
              placeholder="仓库名称"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item :label="contents.inputTitle == 1 ? '仓库类型' : ''">
            <el-select v-model="searchForm.cangkuTypes" placeholder="全部">
              <el-option label="全部" value=""></el-option>
              <el-option
                v-for="(item,index) in cangkuTypesSelectSearch"
                :key="index"
                :label="item.indexName"
                :value="item.codeIndex"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="success" @click="search()">
              查询<i class="el-icon-search el-icon--right"/>
            </el-button>
          </el-form-item>
        </el-row>

        <el-row class="ad" :style="{justifyContent:contents.btnAdAllBoxPosition=='1'?'flex-start':contents.btnAdAllBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item>
            <el-button
              v-if="isAuth('cangku','新增')"
              type="success"
              class="el-icon-plus"
              @click="addOrUpdateHandler()"
            >新增</el-button>

            <el-button
              v-if="isAuth('cangku','删除')"
              :disabled="dataListSelections.length <= 0"
              type="danger"
              class="el-icon-delete"
              @click="deleteHandler()"
            >删除</el-button>

            <el-button
              v-if="isAuth('cangku','报表')"
              type="success"
              icon="el-icon-pie-chart"
              @click="chartDialog()"
            >报表</el-button>

            <a style="text-decoration:none" class="el-button el-button--success el-icon-download"
               v-if="isAuth('cangku','导入导出')"
               :href="baseUrl + '/file/download?fileName=cangkuMuBan.xls'"
            >批量导入仓库数据模板</a>

            <el-upload
              v-if="isAuth('cangku','导入导出')"
              style="display: inline-block"
              :action="baseUrl + '/file/upload'"
              :on-success="cangkuUploadSuccess"
              :on-error="cangkuUploadError"
              :show-file-list="false"
            >
              <el-button
                type="success"
                icon="el-icon-upload2"
              >批量导入仓库数据</el-button>
            </el-upload>

            <!-- 导出excel -->
            <download-excel
              v-if="isAuth('cangku','导入导出')"
              style="display: inline-block"
              class="export-excel-wrapper"
              :data="dataList"
              :fields="json_fields"
              name="cangku.xls"
            >
              <el-button type="success" icon="el-icon-download">导出</el-button>
            </download-excel>
          </el-form-item>
        </el-row>
      </el-form>

      <div class="table-content">
        <el-table
          class="tables"
          :size="contents.tableSize"
          :show-header="contents.tableShowHeader"
          :header-row-style="headerRowStyle"
          :header-cell-style="headerCellStyle"
          :border="contents.tableBorder"
          :fit="contents.tableFit"
          :stripe="contents.tableStripe"
          :row-style="rowStyle"
          :cell-style="cellStyle"
          :style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}"
          v-if="isAuth('cangku','查看')"
          :data="dataList"
          v-loading="dataListLoading"
          @selection-change="selectionChangeHandler"
        >
          <el-table-column v-if="contents.tableSelection" type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" v-if="contents.tableIndex" type="index" width="50" />

          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="cangkuName" header-align="center" label="仓库名称">
            <template #default="scope">{{ scope.row.cangkuName }}</template>
          </el-table-column>

          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="cangkuUuidNumber" header-align="center" label="仓库编号">
            <template #default="scope">{{ scope.row.cangkuUuidNumber }}</template>
          </el-table-column>

          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="cangkuPhoto" header-align="center" width="200" label="仓库照片">
            <template #default="scope">
              <div v-if="scope.row.cangkuPhoto">
                <el-image
                  :src="getImageUrl(scope.row.cangkuPhoto)"
                  :preview-src-list="[getImageUrl(scope.row.cangkuPhoto)]"
                  style="width: 100px; height: 100px"
                  fit="cover"
                ></el-image>
              </div>
              <div v-else>无图片</div>
            </template>
          </el-table-column>

          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="cangkuAddress" header-align="center" label="仓库地点">
            <template #default="scope">{{ scope.row.cangkuAddress }}</template>
          </el-table-column>

          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="cangkuValue" header-align="center" label="仓库类型">
            <template #default="scope">{{ scope.row.cangkuValue }}</template>
          </el-table-column>

          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="insertTime" header-align="center" label="录入时间">
            <template #default="scope">{{ scope.row.insertTime }}</template>
          </el-table-column>

          <el-table-column width="300" :align="contents.tableAlign" header-align="center" label="操作">
            <template #default="scope">
              <el-button v-if="sessionTable=='false'" type="primary" size="mini" @click="wuyong(scope.row.id)">无用按钮</el-button>
              <el-button v-if="sessionTable=='false'" type="success" icon="el-icon-printer" size="mini" @click="dayinOpen(scope.row)">打印</el-button>
              <el-button v-if="isAuth('cangku','查看')" type="success" class="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('cangku','修改')" type="primary" class="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('cangku','删除')" type="danger" class="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          :layout="layouts"
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="pageIndex"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="Number(contents.pageEachNum)"
          :total="totalPage"
          :small="contents.pageStyle"
          class="pagination-content"
          :background="contents.pageBtnBG"
          :style="{textAlign:contents.pagePosition==1?'left':contents.pagePosition==2?'center':'right'}"
        ></el-pagination>
      </div>
    </div>

    <!-- 添加/修改页面 -->
    <add-or-update v-if="addOrUpdateFlag" :parent="parentExposed" ref="addOrUpdate"></add-or-update>

    <!-- 打印对话框 -->
    <el-dialog title="打印" v-model="printVisiable" width="500px">
      <el-form ref="printContent" :model="printFrom" label-width="auto">
        <el-form-item label="发件人">
          <div style="display: flex">
            <el-input v-model="printFrom.kuaidiFajianName" disabled></el-input>
            :
            <el-input v-model="printFrom.kuaidiFajianShoujihao" disabled></el-input>
          </div>
        </el-form-item>
        <el-form-item label="发件人地址">
          <el-input v-model="printFrom.kuaidiFajianAddress" disabled></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="printContent()" icon="el-icon-printer" type="success">打印</el-button>
          <el-button @click="printVisiable = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 统计报表对话框 -->
    <el-dialog title="统计报表" v-model="chartVisiable" width="80%">
      <el-date-picker v-model="echartsDate" type="year" placeholder="选择年"></el-date-picker>
      <el-button @click="chartDialog">查询</el-button>
      <div id="statistic" style="width:100%;height:600px;"></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="chartVisiable = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted, nextTick, computed } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import AddOrUpdate from "./add-or-update.vue";
import styleJs from "../../../utils/style.js";

// 获取当前实例的上下文
const { proxy } = getCurrentInstance();
const router = useRouter();

// 响应式状态
const searchForm = reactive({
  cangkuName: '',
  cangkuTypes: ''
});

const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const showFlag = ref(true);
const addOrUpdateFlag = ref(false);
const chartVisiable = ref(false);
const echartsDate = ref(new Date());
const printVisiable = ref(false);
const printFrom = reactive({});
const cangkuTypesSelectSearch = ref([]);
const baseUrl = ref('http://localhost:8080/wuliuguanlixitong/');

// 样式配置
const contents = reactive(styleJs.listStyle());
const layouts = ref('');

// 导出Excel字段配置
const json_fields = reactive({
  '仓库名称': "cangkuName",
  '仓库编号': "cangkuUuidNumber",
  '仓库照片': "cangkuPhoto",
  '仓库地点': "cangkuAddress",
  '仓库类型': "cangkuValue",
  '录入时间': "insertTime",
});

// 计算属性
const sessionTable = computed(() => proxy.$storage.get("sessionTable"));
const role = computed(() => proxy.$storage.get("role"));
const userId = computed(() => proxy.$storage.get("userId"));

// 全局方法访问
const isAuth = (key, val) => {
  return proxy.isAuth(key, val);
};

// 获取图片URL
const getImageUrl = (url) => {
  return url && url.startsWith('http') ? url : baseUrl.value + url;
};

// 方法
const contentStyleChange = () => {
  contentSearchStyleChange();
  contentBtnAdAllStyleChange();
  contentSearchBtnStyleChange();
  contentTableBtnStyleChange();
  contentPageStyleChange();
};

const contentSearchStyleChange = () => {
  nextTick(() => {
    document.querySelectorAll('.form-content .slt .el-input__inner').forEach(el => {
      let textAlign = 'left'
      if (contents.inputFontPosition == 2) textAlign = 'center'
      if (contents.inputFontPosition == 3) textAlign = 'right'
      el.style.textAlign = textAlign
      el.style.height = contents.inputHeight
      el.style.lineHeight = contents.inputHeight
      el.style.color = contents.inputFontColor
      el.style.fontSize = contents.inputFontSize
      el.style.borderWidth = contents.inputBorderWidth
      el.style.borderStyle = contents.inputBorderStyle
      el.style.borderColor = contents.inputBorderColor
      el.style.borderRadius = contents.inputBorderRadius
      el.style.backgroundColor = contents.inputBgColor
    });

    if (contents.inputTitle) {
      document.querySelectorAll('.form-content .slt .el-form-item__label').forEach(el => {
        el.style.color = contents.inputTitleColor
        el.style.fontSize = contents.inputTitleSize
        el.style.lineHeight = contents.inputHeight
      });
    }

    setTimeout(() => {
      document.querySelectorAll('.form-content .slt .el-input__prefix').forEach(el => {
        el.style.color = contents.inputIconColor
        el.style.lineHeight = contents.inputHeight
      });
      document.querySelectorAll('.form-content .slt .el-input__suffix').forEach(el => {
        el.style.color = contents.inputIconColor
        el.style.lineHeight = contents.inputHeight
      });
      document.querySelectorAll('.form-content .slt .el-input__icon').forEach(el => {
        el.style.lineHeight = contents.inputHeight
      });
    }, 10);
  });
};

const contentSearchBtnStyleChange = () => {
  nextTick(() => {
    document.querySelectorAll('.form-content .slt .el-button--success').forEach(el => {
      el.style.height = contents.searchBtnHeight
      el.style.color = contents.searchBtnFontColor
      el.style.fontSize = contents.searchBtnFontSize
      el.style.borderWidth = contents.searchBtnBorderWidth
      el.style.borderStyle = contents.searchBtnBorderStyle
      el.style.borderColor = contents.searchBtnBorderColor
      el.style.borderRadius = contents.searchBtnBorderRadius
      el.style.backgroundColor = contents.searchBtnBgColor
    });
  });
};

const contentBtnAdAllStyleChange = () => {
  nextTick(() => {
    document.querySelectorAll('.form-content .ad .el-button--success').forEach(el => {
      el.style.height = contents.btnAdAllHeight
      el.style.color = contents.btnAdAllAddFontColor
      el.style.fontSize = contents.btnAdAllFontSize
      el.style.borderWidth = contents.btnAdAllBorderWidth
      el.style.borderStyle = contents.btnAdAllBorderStyle
      el.style.borderColor = contents.btnAdAllBorderColor
      el.style.borderRadius = contents.btnAdAllBorderRadius
      el.style.backgroundColor = contents.btnAdAllAddBgColor
    });

    document.querySelectorAll('.form-content .ad .el-button--danger').forEach(el => {
      el.style.height = contents.btnAdAllHeight
      el.style.color = contents.btnAdAllDelFontColor
      el.style.fontSize = contents.btnAdAllFontSize
      el.style.borderWidth = contents.btnAdAllBorderWidth
      el.style.borderStyle = contents.btnAdAllBorderStyle
      el.style.borderColor = contents.btnAdAllBorderColor
      el.style.borderRadius = contents.btnAdAllBorderRadius
      el.style.backgroundColor = contents.btnAdAllDelBgColor
    });

    document.querySelectorAll('.form-content .ad .el-button--warning').forEach(el => {
      el.style.height = contents.btnAdAllHeight
      el.style.color = contents.btnAdAllWarnFontColor
      el.style.fontSize = contents.btnAdAllFontSize
      el.style.borderWidth = contents.btnAdAllBorderWidth
      el.style.borderStyle = contents.btnAdAllBorderStyle
      el.style.borderColor = contents.btnAdAllBorderColor
      el.style.borderRadius = contents.btnAdAllBorderRadius
      el.style.backgroundColor = contents.btnAdAllWarnBgColor
    });
  });
};

const contentTableBtnStyleChange = () => {
  // 表格按钮样式更改逻辑
};

const contentPageStyleChange = () => {
  let arr = [];
  if (contents.pageTotal) arr.push('total');
  if (contents.pageSizes) arr.push('sizes');
  if (contents.pagePrevNext) {
    arr.push('prev');
    if (contents.pagePager) arr.push('pager');
    arr.push('next');
  }
  if (contents.pageJumper) arr.push('jumper');
  layouts.value = arr.join();
  contents.pageEachNum = 10;
};

// 行样式函数
const rowStyle = ({ row, rowIndex }) => {
  if (rowIndex % 2 == 1) {
    if (contents.tableStripe) {
      return { color: contents.tableStripeFontColor };
    }
  } else {
    return '';
  }
};

const cellStyle = ({ row, rowIndex }) => {
  if (rowIndex % 2 == 1) {
    if (contents.tableStripe) {
      return { backgroundColor: contents.tableStripeBgColor };
    }
  } else {
    return '';
  }
};

const headerRowStyle = ({ row, rowIndex }) => {
  return { color: contents.tableHeaderFontColor };
};

const headerCellStyle = ({ row, rowIndex }) => {
  return { backgroundColor: contents.tableHeaderBgColor };
};

// 初始化函数
const init = async () => {
  // 获取仓库类型下拉选项
  try {
    const { data } = await proxy.$http({
      url: "dictionary/page?dicCode=cangku_types&page=1&limit=100",
      method: "get"
    });

    if (data && data.code === 0) {
      cangkuTypesSelectSearch.value = data.data.list;
    }
  } catch (error) {
    console.error("获取仓库类型失败:", error);
  }
};

const search = () => {
  pageIndex.value = 1;
  getDataList();
};

const getDataList = async () => {
  dataListLoading.value = true;
  let params = {
    page: pageIndex.value,
    limit: pageSize.value,
    sort: 'id',
    cangkuDelete: 1 // 逻辑删除字段 1 未删除 2 删除
  };

  if (searchForm.cangkuName != '' && searchForm.cangkuName != undefined) {
    params['cangkuName'] = '%' + searchForm.cangkuName + '%';
  }

  if (searchForm.cangkuTypes != '' && searchForm.cangkuTypes != undefined) {
    params['cangkuTypes'] = searchForm.cangkuTypes;
  }

  try {
    const { data } = await proxy.$http({
      url: "cangku/page",
      method: "get",
      params: params
    });

    if (data && data.code === 0) {
      dataList.value = data.data.list;
      totalPage.value = data.data.total;
    } else {
      dataList.value = [];
      totalPage.value = 0;
      proxy.$message.error(data.msg);
    }
  } catch (error) {
    console.error("获取数据失败:", error);
    proxy.$message.error("获取数据失败，请稍后重试");
  } finally {
    dataListLoading.value = false;
  }
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
  if (type != 'info') {
    type = 'else';
  }
  nextTick(() => {
    proxy.$refs.addOrUpdate.init(id, type);
  });
};

const deleteHandler = async (id) => {
  let ids;

  // 处理批量删除的情况
  if (id instanceof PointerEvent || id === undefined) {
    // 如果是事件对象或undefined，说明是批量删除
    if (dataListSelections.value.length <= 0) {
      proxy.$message.warning('请选择要删除的数据');
      return;
    }
    ids = dataListSelections.value.map(item => Number(item.id));
  } else {
    // 单个删除
    ids = [Number(id)];
  }

  try {
    await proxy.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });

    const { data } = await proxy.$http({
      url: "cangku/delete",
      method: "post",
      data: ids
    });

    if (data && data.code === 0) {
      proxy.$message({
        message: "操作成功",
        type: "success",
        duration: 1500,
        onClose: () => {
          getDataList();
        }
      });
    } else {
      proxy.$message.error(data.msg);
    }
  } catch (error) {
    console.error("删除操作失败:", error);
  }
};

// 导入功能上传文件成功后调用
const cangkuUploadSuccess = async (data) => {
  try {
    const { data: res } = await proxy.$http({
      url: "cangku/batchInsert?fileName=" + data.file,
      method: "get"
    });

    if (res && res.code === 0) {
      proxy.$message({
        message: "导入仓库数据成功",
        type: "success",
        duration: 1500,
        onClose: () => {
          getDataList();
        }
      });
    } else {
      proxy.$message.error(res.msg);
    }
  } catch (error) {
    console.error("导入仓库数据失败:", error);
  }
};

// 导入功能上传文件失败后调用
const cangkuUploadError = () => {
  proxy.$message.error('上传失败');
};

// 打印功能
const dayinOpen = (item) => {
  printVisiable.value = true;
  Object.assign(printFrom, item);
};

const printContent = () => {
  proxy.$print(proxy.$refs.printContent);
  printVisiable.value = false;
};

// 无用按钮
const wuyong = (id) => {
  proxy.$message.info('这是一个无用按钮');
};

// 图表对话框
const chartDialog = () => {
  const params = {
    dateFormat: "%Y", //%Y-%m
    riqi: echartsDate.value.getFullYear(),
    thisTable: {
      tableName: "cangku",
      sumColum: 'cangku_number',
      date: 'insert_time',
    },
  };

  chartVisiable.value = true;

  nextTick(() => {
    const statistic = echarts.init(document.getElementById("statistic"), 'macarons');

    proxy.$http({
      url: "barSum",
      method: "get",
      params: params
    }).then(({ data }) => {
      if (data && data.code === 0) {
        const yAxisName = "数值";
        const xAxisName = "月份";
        const series = [];

        data.data.yAxis.forEach((item, index) => {
          const tempMap = {
            name: data.data.legend[index],
            type: 'bar',
            data: item
          };
          series.push(tempMap);
        });

        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              crossStyle: {
                color: '#999'
              }
            }
          },
          toolbox: {
            feature: {
              magicType: { show: true, type: ['line', 'bar'] },
              saveAsImage: { show: true }
            }
          },
          legend: {
            data: data.data.legend
          },
          xAxis: [
            {
              type: 'category',
              axisLabel: { interval: 0 },
              name: xAxisName,
              data: data.data.xAxis,
              axisPointer: {
                type: 'shadow'
              }
            }
          ],
          yAxis: [
            {
              type: 'value',
              name: yAxisName,
              axisLabel: {
                formatter: '{value}'
              }
            }
          ],
          series: series
        };

        statistic.setOption(option, true);
        window.onresize = () => {
          statistic.resize();
        };
      } else {
        proxy.$message({
          message: "报表未查询到数据",
          type: "success",
          duration: 1500,
          onClose: () => {
            search();
          }
        });
      }
    });
  });
};

// 响应式对象用于暴露给子组件
const parentExposed = reactive({
  get showFlag() {
    return showFlag.value;
  },
  set showFlag(value) {
    showFlag.value = value;
  },
  get addOrUpdateFlag() {
    return addOrUpdateFlag.value;
  },
  set addOrUpdateFlag(value) {
    addOrUpdateFlag.value = value;
  },
  search,
  contentStyleChange,
  searchForm,
  dataList,
  pageIndex,
  pageSize,
  totalPage,
  dataListLoading,
  dataListSelections,
  getDataList
});

// 生命周期钩子
onMounted(() => {
  init();
  getDataList();
  contentStyleChange();
});
</script>

<style lang="scss" scoped>
.main-content {
  .slt {
    margin: 0 !important;
    display: flex;
  }

  .ad {
    margin: 0 !important;
    display: flex;
  }

  .pages {
    :deep(.el-pagination__sizes) {
      :deep(.el-input__inner) {
        height: 22px;
        line-height: 22px;
      }
    }
  }

  .el-button + .el-button {
    margin: 0;
  }

  .tables {
    :deep(.el-button--success) {
      height: 40px;
      color: rgba(52, 51, 47, 0.93);
      font-size: 14px;
      border-width: 2px;
      border-style: dashed;
      border-color: rgba(255, 255, 255, 1);
      border-radius: 20px;
      background-color: var(--publicSubColor);
    }

    :deep(.el-button--primary) {
      height: 40px;
      color: rgba(52, 51, 47, 0.93);
      font-size: 14px;
      border-width: 2px;
      border-style: dashed;
      border-color: rgba(255, 255, 255, 1);
      border-radius: 20px;
      background-color: var(--publicSubColor);
    }

    :deep(.el-button--danger) {
      height: 40px;
      color: rgba(52, 51, 47, 0.93);
      font-size: 14px;
      border-width: 2px;
      border-style: dashed;
      border-color: rgba(255, 255, 255, 1);
      border-radius: 20px;
      background-color: var(--publicSubColor);
    }

    :deep(.el-button) {
      margin: 4px;
    }

    :deep(.el-image) {
      cursor: pointer;
      transition: transform 0.3s;

      &:hover {
        transform: scale(1.05);
      }
    }
  }

  .form-content {
    background: transparent;
  }

  .table-content {
    background: transparent;
  }

  :deep(.el-table__body tr) {
    background-color: rgba(255, 255, 255, 1) !important;
    color: rgba(92, 93, 95, 1) !important;
  }

  :deep(.el-table__body tr.el-table__row--striped td) {
    background: transparent;
  }

  :deep(.el-table__body tr.el-table__row--striped) {
    background-color: #f5f7fa !important;
    color: #606266 !important;
  }

  :deep(.el-table__body tr:hover > td) {
    background-color: #f5f5f5 !important;
    color: #333 !important;
  }
}
</style>
