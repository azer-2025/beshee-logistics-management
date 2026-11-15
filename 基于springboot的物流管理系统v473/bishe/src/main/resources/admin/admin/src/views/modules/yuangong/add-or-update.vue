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
          <el-form-item class="input" v-if="type!='info'"  label="账户" prop="username">
            <el-input v-model="ruleForm.username"
                      placeholder="账户" clearable  :readonly="ro.username"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="账户" prop="极光username">
              <el-input v-model="ruleForm.username"
                        placeholder="账户" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="员工编号" prop="yuangongUuidNumber">
            <el-input v-model="ruleForm.yuangongUuidNumber"
                      placeholder="员工编号" clearable  :readonly="ro.yuangongUuidNumber"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="员工编号" prop="yuangongUuidNumber">
              <el-input v-model="ruleForm.yuangongUuidNumber"
                        placeholder="员工编号" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="员工姓名" prop="yuangongName">
            <el-input v-model="ruleForm.yuangongName"
                      placeholder="员工姓名" clearable  :readonly="ro.yuangongName"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="员工姓名" prop="yuangongName">
              <el-input v-model="ruleForm.yuangongName"
                        placeholder="员工姓名" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="员工手机号" prop="yuangongPhone">
            <el-input v-model="ruleForm.yuangongPhone"
                      placeholder="员工手机号" clearable  :readonly="ro.yuangongPhone"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="员工手机号" prop="yuangongPhone">
              <el-input v-model="ruleForm.yuangongPhone"
                        placeholder="员工手机号" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="员工身份证号" prop="yuangongIdNumber">
            <el-input v-model="ruleForm.yuangongIdNumber"
                      placeholder="员工身份证号" clearable  :readonly="ro.yuangongIdNumber"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="员工身份证号" prop="yuangongIdNumber">
              <el-input v-model="ruleForm.yuangongIdNumber"
                        placeholder="员工身份证号" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="upload" v-if="type!='info' && !ro.yuangongPhoto" label="员工头像" prop="yuangongPhoto">
            <file-upload
              tip="点击上传员工头像"
              action="file/upload"
              :limit="3"
              :multiple="true"
              :fileUrls="ruleForm.yuangongPhoto?ruleForm.yuangongPhoto:''"
              @change="yuangongPhotoUploadChange"
            ></file-upload>
          </el-form-item>
          <div v-else>
            <el-form-item v-if="ruleForm.yuangongPhoto" label="员工头像" prop="yuangongPhoto">
              <img style="margin-right:20px;" v-bind:key="index" v-for="(item,index) in (ruleForm.yuangongPhoto || '').split(',')" :src="item.startsWith('http')?item:'http://localhost:8080/wuliuguanlixitong/'+item" width="100" height="100">
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="select" v-if="type!='info'"  label="性别" prop="sexTypes">
            <el-select v-model="ruleForm.sexTypes" :disabled="ro.sexTypes" placeholder="请选择性别">
              <el-option
                v-for="(item,index) in sexTypesOptions"
                v-bind:key="item.codeIndex"
                :label="item.indexName"
                :value="item.codeIndex">
              </el-option>
            </el-select>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="性别" prop="sexValue">
              <el-input v-model="ruleForm.sexValue"
                        placeholder="性别" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'"  label="员工邮箱" prop="yuangongEmail">
            <el-input v-model="ruleForm.yuangongEmail"
                      placeholder="员工邮箱" clearable  :readonly="ro.yuangongEmail"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="员工邮箱" prop="yuangongEmail">
              <el-input v-model="ruleForm.yuangongEmail"
                        placeholder="员工邮箱" readonly></el-input>
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
  username: false,
  password: false,
  yuangongUuidNumber: true,
  yuangongName极光: false,
  yuangongPhone: false,
  yuangongIdNumber: false,
  yuangongPhoto: false,
  sexTypes: false,
  yuangongEmail: false,
});

const ruleForm = reactive({
  username: '',
  password: '',
  yuangongUuidNumber: new Date().getTime(),
  yuangongName: '',
  yuangongPhone: '',
  yuangongIdNumber: '',
  yuangongPhoto: '',
  sexTypes: '',
  yuangongEmail: '',
});

const sexTypesOptions = ref([]);

const rules = reactive({
  username: [
    { required: true, message: '账户不能为空', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
  ],
  yuangongUuidNumber: [
    { required: true, message: '员工编号不能为空', trigger: 'blur' },
  ],
  yuangongName: [
    { required: true, message: '员工姓名不能为空', trigger: 'blur' },
  ],
  yuangongPhone: [
    { required: true, message: '员工手机号不能为空', trigger: 'blur' },
    { pattern: /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
      message: '员工手机号格式不对',
      trigger: 'blur'
    }
  ],
  yuangongIdNumber: [
    { required: true, message: '员工身份证号不能为空', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
      message: '员工身份证号格式有误！',
      trigger: 'blur'
    }
  ],
  yuangongPhoto: [
    { required: true, message: '员工头像不能为空', trigger: 'blur' },
  ],
  sexTypes: [
    { required: true, message: '性别不能为空', trigger: 'blur' },
    { pattern: /^[0-9]*$/,
      message: '只允许输入整数',
      trigger: 'blur'
    }
  ],
  yuangongEmail: [
    { required: true, message: '员工邮箱不能为空', trigger: 'blur' },
    { pattern: /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/,
      message: '员工邮箱只能是邮箱格式',
      trigger: 'blur'
    }
  ],
});

onMounted(() => {
  // 获取下拉框信息
  proxy.$http({
    url: `dictionary/page?page=1&limit=100&sort=&order=&dicCode=sex_types`,
    method: "get"
  }).then(({ data }) => {
    if (data && data.code === 0) {
      sexTypesOptions.value = data.data.list;
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
    url: `yuangong/info/${id}`,
    method: 'get'
  }).then(({ data }) => {
    if (data && data.code === 0) {
      Object.assign(ruleForm, data.data);
    } else {
      ElMessage.error(data.msg);
    }
  });
}

// 提交
function onSubmit() {
  ruleFormRef.value.validate(valid => {
    if (valid) {
      proxy.$http({
        url: `yuangong/${!ruleForm.id ? "save" : "update"}`,
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
              props.parent.yuangongCrossAddOrUpdateFlag = false;
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
  props.parent.yuangongCrossAddOrUpdateFlag = false;
  props.parent.contentStyleChange();
}

// 图片上传处理
function yuangongPhotoUploadChange(fileUrls) {
  ruleForm.yuangongPhoto = fileUrls;
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
