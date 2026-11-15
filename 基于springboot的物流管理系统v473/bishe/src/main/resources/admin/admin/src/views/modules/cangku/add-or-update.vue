<template>
  <div class="addEdit-block">
    <el-form
      class="detail-form-content"
      ref="ruleFormRef"
      :model="ruleForm"
      :rules="rules"
      label-width="auto"
    >
      <el-row>
        <input id="updateId" name="id" type="hidden">
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="仓库名称" prop="cangkuName">
            <el-input v-model="ruleForm.cangkuName"
                      placeholder="仓库名称" clearable  :readonly="ro.cangkuName"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="仓库名称" prop="cangkuName">
              <el-input v-model="ruleForm.cangkuName"
                        placeholder="仓库名称" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="仓库编号" prop="cangkuUuidNumber">
            <el-input v-model="ruleForm.cangkuUuidNumber"
                      placeholder="仓库编号" clearable  :readonly="ro.cangkuUuidNumber"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="仓库编号" prop="cangkuUuidNumber">
              <el-input v-model="ruleForm.cangkuUuidNumber"
                        placeholder="仓库编号" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="upload" v-if="type!='info' && !ro.cangkuPhoto" label="仓库照片" prop="cangkuPhoto">
            <file-upload
              tip="点击上传仓库照片"
              action="file/upload"
              :limit="3"
              :multiple="true"
              :fileUrls="ruleForm.cangkuPhoto?ruleForm.cangkuPhoto:''"
              @change="cangkuPhotoUploadChange"
            ></file-upload>
          </el-form-item>
          <div v-else>
            <el-form-item v-if="ruleForm.cangkuPhoto" label="仓库照片" prop="cangkuPhoto">
              <img style="margin-right:20px;" v-bind:key="index" v-for="(item,index) in (ruleForm.cangkuPhoto || '').split(',')" :src="item.startsWith('http')?item:'http://localhost:8080/wuliuguanlixitong/'+item" width="100" height="100">
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="仓库地点" prop="cangkuAddress">
            <el-input v-model="ruleForm.cangkuAddress"
                      placeholder="仓库地点" clearable  :readonly="ro.cangkuAddress"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="仓库地点" prop="cangkuAddress">
              <el-input v-model="ruleForm.cangkuAddress"
                        placeholder="仓库地点" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="select" v-if="type!='info'"  label="仓库类型" prop="cangkuTypes">
            <el-select v-model="ruleForm.cangkuTypes" :disabled="ro.cangkuTypes" placeholder="请选择仓库类型">
              <el-option
                v-for="(item,index) in cangkuTypesOptions"
                v-bind:key="item.codeIndex"
                :label="item.indexName"
                :value="item.codeIndex">
              </el-option>
            </el-select>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="仓库类型" prop="cangkuValue">
              <el-input v-model="ruleForm.cangkuValue"
                        placeholder="仓库类型" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="24">
          <el-form-item v-if="type!='info' && !ro.cangkuContent"  label="仓库介绍" prop="cangkuContent">
            <editor style="min-width: 200px; max-width: 600px;"
                    v-model:content="ruleForm.cangkuContent"
                    class="editor"
                    action="file/upload">
            </editor>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item v-if="ruleForm.cangkuContent" label="仓库介绍" prop="cangkuContent">
              <span v-html="ruleForm.cangkuContent"></span>
            </el-form-item>
          </div>
        </el-col>
      </el-row>
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" class="btn-success" @click="onSubmit">提交</el-button>
        <el-button v-if="type!='info'" class="btn-close" @click="back()">取消</el-button>
        <el-button v-if="type=='info'" class="btn-close" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import styleJs from "../../../utils/style.js";
import { isNumber, isIntNumer, isEmail, isPhone, isMobile, isURL, checkIdCard } from "@/utils/validate";
import { getYearFormat, getMonthFormat, getDateFormat, getDatetimeFormat } from "../../../utils/utils.js";

const { proxy } = getCurrentInstance();
const props = defineProps(["parent"]);

const addEditForm = ref(styleJs.addStyle());
const ruleFormRef = ref(null);
const id = ref('');
const type = ref('');
const sessionTable = ref(proxy.$storage.get("sessionTable"));
const role = ref(proxy.$storage.get("role"));
const userId = ref(proxy.$storage.get("userId"));

const ro = reactive({
  cangkuName: false,
  cangkuUuidNumber: true,
  cangkuPhoto: false,
  cangkuAddress: false,
  cangkuTypes: false,
  cangkuContent: false,
  cangkuDelete: false,
  insertTime: false,
});

const ruleForm = reactive({
  cangkuName: '',
  cangkuUuidNumber: new Date().getTime(),
  cangkuPhoto: '',
  cangkuAddress: '',
  cangkuTypes: '',
  cangkuContent: '',
  cangkuDelete: '',
  insertTime: '',
});

const cangkuTypesOptions = ref([]);

const rules = reactive({
  cangkuName: [
    { required: true, message: '仓库名称不能为空', trigger: 'blur' },
  ],
  cangkuUuidNumber: [
    { required: true, message: '仓库编号不能为空', trigger: 'blur' },
  ],
  cangkuPhoto: [
    { required: true, message: '仓库照片不能为空', trigger: 'blur' },
  ],
  cangkuAddress: [
    { required: true, message: '仓库地点不能为空', trigger: 'blur' },
  ],
  cangkuTypes: [
    { required: true, message: '仓库类型不能为空', trigger: 'blur' },
    { pattern: /^[0-9]*$/,
      message: '只允许输入整数',
      trigger: 'blur'
    }
  ],
  cangkuContent: [
    { required: true, message: '仓库介绍不能为空', trigger: 'blur' },
  ],
  cangkuDelete: [
    { required: true, message: '逻辑删除不能为空', trigger: 'blur' },
    { pattern: /^[0-9]*$/,
      message: '只允许输入整数',
      trigger: 'blur'
    }
  ],
  insertTime: [
    { required: true, message: '录入时间不能为空', trigger: 'blur' },
  ],
});

onMounted(() => {
  // 获取下拉框信息
  proxy.$http({
    url: `dictionary/page?page=1&limit=100&sort=&order=&dicCode=cangku_types`,
    method: "get"
  }).then(({ data }) => {
    if (data && data.code === 0) {
      cangkuTypesOptions.value = data.data.list;
    }
  });

  addEditStyleChange();
  addEditUploadStyleChange();
});

// 下载
function download(file) {
  window.open(`${file}`);
}

// 初始化
function init(initId, initType) {
  id.value = initId;
  type.value = initType;

  if (id.value) {
    if (type.value === 'info' || type.value === 'else') {
      info(id.value);
    }
  }

  // 获取用户信息
  proxy.$http({
    url: `${proxy.$storage.get("sessionTable")}/session`,
    method: "get"
  }).then(({ data }) => {
    if (data && data.code !== 0) {
      ElMessage.error(data.msg);
    }
  });
}

// 获取详情
function info(id) {
  proxy.$http({
    url: `cangku/info/${id}`,
    method: 'get'
  }).then(({ data }) => {
    if (data && data.code === 0) {
      Object.assign(ruleForm, data.data);
      ruleForm.cangkuContent = ruleForm.cangkuContent.replaceAll("src=\"upload/", "src=\"" + proxy.$base.url + "upload/");
    } else {
      ElMessage.error(data.msg);
    }
  });
}

// 提交
function onSubmit() {
  ruleFormRef.value.validate(valid => {
    if (valid) {
      ruleForm.cangkuContent = ruleForm.cangkuContent.replaceAll(proxy.$base.url, "");
      proxy.$http({
        url: `cangku/${!ruleForm.id ? "save" : "update"}`,
        method: "post",
        data: ruleForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          ElMessage({
            message: "操作成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              props.parent.showFlag = true;
              props.parent.addOrUpdateFlag = false;
              props.parent.cangkuCrossAddOrUpdateFlag = false;
              props.parent.search();
              props.parent.contentStyleChange();
            }
          });
        } else {
          ElMessage.error(data.msg);
        }
      });
    }
  });
}

// 获取UUID
function getUUID() {
  return new Date().getTime();
}

// 返回
function back() {
  props.parent.showFlag = true;
  props.parent.addOrUpdateFlag = false;
  props.parent.cangkuCrossAddOrUpdateFlag = false;
  props.parent.contentStyleChange();
}

// 图片上传处理
function cangkuPhotoUploadChange(fileUrls) {
  ruleForm.cangkuPhoto = fileUrls;
  addEditUploadStyleChange();
}

// 样式调整方法
function addEditStyleChange() {
  nextTick(() => {
    // input
    document.querySelectorAll('.addEdit-block .input .el-input__inner').forEach(el => {
      el.style.height = addEditForm.value.inputHeight
      el.style.color = addEditForm.value.inputFontColor
      el.style.fontSize = addEditForm.value.inputFontSize
      el.style.borderWidth = addEditForm.value.inputBorderWidth
      el.style.borderStyle = addEditForm.value.inputBorderStyle
      el.style.borderColor = addEditForm.value.inputBorderColor
      el.style.borderRadius = addEditForm.value.inputBorderRadius
      el.style.backgroundColor = addEditForm.value.inputBgColor
    })

    document.querySelectorAll('.addEdit-block .input .el-form-item__label').forEach(el => {
      el.style.lineHeight = addEditForm.value.inputHeight
      el.style.color = addEditForm.value.inputLableColor
      el.style.fontSize = addEditForm.value.inputLableFontSize
    })

    // select
    document.querySelectorAll('.addEdit-block .select .el-input__inner').forEach(el => {
      el.style.height = addEditForm.value.selectHeight
      el.style.color = addEditForm.value.selectFontColor
      el.style.fontSize = addEditForm.value.selectFontSize
      el.style.borderWidth = addEditForm.value.selectBorderWidth
      el.style.borderStyle = addEditForm.value.selectBorderStyle
      el.style.borderColor = addEditForm.value.selectBorderColor
      el.style.borderRadius = addEditForm.value.selectBorderRadius
      el.style.backgroundColor = addEditForm.value.selectBgColor
    })

    document.querySelectorAll('.addEdit-block .select .el-form-item__label').forEach(el => {
      el.style.lineHeight = addEditForm.value.selectHeight
      el.style.color = addEditForm.value.selectLableColor
      el.style.fontSize = addEditForm.value.selectLableFontSize
    })

    document.querySelectorAll('.addEdit-block .select .el-select__caret').forEach(el => {
      el.style.color = addEditForm.value.selectIconFontColor
      el.style.fontSize = addEditForm.value.selectIconFontSize
    })

    // date
    document.querySelectorAll('.addEdit-block .date .el-input__inner').forEach(el => {
      el.style.height = addEditForm.value.dateHeight
      el.style.color = addEditForm.value.dateFontColor
      el.style.fontSize = addEditForm.value.dateFontSize
      el.style.borderWidth = addEditForm.value.dateBorderWidth
      el.style.borderStyle = addEditForm.value.dateBorderStyle
      el.style.borderColor = addEditForm.value.dateBorderColor
      el.style.borderRadius = addEditForm.value.dateBorderRadius
      el.style.backgroundColor = addEditForm.value.dateBgColor
    })

    document.querySelectorAll('.addEdit-block .date .el-form-item__label').forEach(el => {
      el.style.lineHeight = addEditForm.value.dateHeight
      el.style.color = addEditForm.value.dateLableColor
      el.style.fontSize = addEditForm.value.dateLableFontSize
    })

    document.querySelectorAll('.addEdit-block .date .el-input__icon').forEach(el => {
      el.style.color = addEditForm.value.dateIconFontColor
      el.style.fontSize = addEditForm.value.dateIconFontSize
      el.style.lineHeight = addEditForm.value.dateHeight
    })

    // upload
    const uploadHeight = addEditForm.value.uploadHeight;
    const uploadBorderWidth = addEditForm.value.uploadBorderWidth;
    const iconLineHeight = parseInt(uploadHeight) - parseInt(uploadBorderWidth) * 2 + 'px'

    document.querySelectorAll('.addEdit-block .upload .el-upload--picture-card').forEach(el => {
      el.style.width = uploadHeight
      el.style.height = uploadHeight
      el.style.borderWidth = uploadBorderWidth
      el.style.borderStyle = addEditForm.value.uploadBorderStyle
      el.style.borderColor = addEditForm.value.uploadBorderColor
      el.style.borderRadius = addEditForm.value.uploadBorderRadius
      el.style.backgroundColor = addEditForm.value.uploadBgColor
    })

    document.querySelectorAll('.addEdit-block .upload .el-form-item__label').forEach(el => {
      el.style.lineHeight = uploadHeight
      el.style.color = addEditForm.value.uploadLableColor
      el.style.fontSize = addEditForm.value.uploadLableFontSize
    })

    document.querySelectorAll('.addEdit-block .upload .el-icon-plus').forEach(el => {
      el.style.color = addEditForm.value.uploadIconFontColor
      el.style.fontSize = addEditForm.value.uploadIconFontSize
      el.style.lineHeight = iconLineHeight
      el.style.display = 'block'
    })

    // 多文本输入框
    document.querySelectorAll('.addEdit-block .textarea .el-textarea__inner').forEach(el => {
      el.style.height = addEditForm.value.textareaHeight
      el.style.color = addEditForm.value.textareaFontColor
      el.style.fontSize = addEditForm.value.textareaFontSize
      el.style.borderWidth = addEditForm.value.textareaBorderWidth
      el.style.borderStyle = addEditForm.value.textareaBorderStyle
      el.style.borderColor = addEditForm.value.textareaBorderColor
      el.style.borderRadius = addEditForm.value.textareaBorderRadius
      el.style.backgroundColor = addEditForm.value.textareaBgColor
    })

    document.querySelectorAll('.addEdit-block .textarea .el-form-item__label').forEach(el => {
      el.style.color = addEditForm.value.textareaLableColor
      el.style.fontSize = addEditForm.value.textareaLableFontSize
    })

    // 保存
    document.querySelectorAll('.addEdit-block .btn .btn-success').forEach(el => {
      el.style.width = addEditForm.value.btnSaveWidth
      el.style.height = addEditForm.value.btnSaveHeight
      el.style.color = addEditForm.value.btnSaveFontColor
      el.style.fontSize = addEditForm.value.btnSaveFontSize
      el.style.borderWidth = addEditForm.value.btnSaveBorderWidth
      el.style.borderStyle = addEditForm.value.btnSaveBorderStyle
      el.style.borderColor = addEditForm.value.btnSaveBorderColor
      el.style.borderRadius = addEditForm.value.btnSaveBorderRadius
      el.style.backgroundColor = addEditForm.value.btnSaveBgColor
    })

    // 返回
    document.querySelectorAll('.addEdit-block .btn .btn-close').forEach(el => {
      el.style.width = addEditForm.value.btnCancelWidth
      el.style.height = addEditForm.value.btnCancelHeight
      el.style.color = addEditForm.value.btnCancelFontColor
      el.style.fontSize = addEditForm.value.btnCancelFontSize
      el.style.borderWidth = addEditForm.value.btnCancelBorderWidth
      el.style.borderStyle = addEditForm.value.btnCancelBorderStyle
      el.style.borderColor = addEditForm.value.btnCancelBorderColor
      el.style.borderRadius = addEditForm.value.btnCancelBorderRadius
      el.style.backgroundColor = addEditForm.value.btnCancelBgColor
    })
  });
}

function addEditUploadStyleChange() {
  nextTick(() => {
    document.querySelectorAll('.addEdit-block .upload .el-upload-list--picture-card .el-upload-list__item').forEach(el => {
      el.style.width = addEditForm.value.uploadHeight
      el.style.height = addEditForm.value.uploadHeight
      el.style.borderWidth = addEditForm.value.uploadBorderWidth
      el.style.borderStyle = addEditForm.value.uploadBorderStyle
      el.style.borderColor = addEditForm.value.uploadBorderColor
      el.style.borderRadius = addEditForm.value.uploadBorderRadius
      el.style.backgroundColor = addEditForm.value.uploadBgColor
    })
  });
}

// 暴露方法给父组件
defineExpose({
  init
});
</script>

<style lang="scss" scoped>
.editor{
  height: 500px;

  & :deep( .ql-container ){
    height: 310px;
  }
}
.amap-wrapper {
  width: 100%;
  height: 500px;
}
.search-box {
  position: absolute;
}
.addEdit-block {
  margin: -10px;
}
.detail-form-content {
  padding: 12px;
  background-color: transparent;
}
.btn .el-button {
  padding: 0;
}
</style>
