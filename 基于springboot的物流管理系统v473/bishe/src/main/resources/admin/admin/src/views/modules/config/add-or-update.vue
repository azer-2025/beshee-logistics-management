<template>
  <div class="add-edit-container">
    <el-card class="modern-form-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">{{ !id ? '新增轮播图' : type === 'info' ? '轮播图详情' : '编辑轮播图' }}</span>
        </div>
      </template>
      
      <el-form
        class="modern-form"
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="rules"
        label-width="120px"
        label-position="top"
      >
        <el-row :gutter="40">
          <el-col :span="24">
            <el-form-item label="轮播图名称" prop="name">
              <el-input 
                v-model="ruleForm.name" 
                placeholder="请输入轮播图名称（如：首页轮播1）" 
                :readonly="type === 'info'"
                clearable
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="24">
            <el-form-item label="轮播图资源" prop="value">
              <div v-if="type !== 'info'" class="upload-wrapper">
                <file-upload
                  tip="建议尺寸：1920x450px，支持 jpg/png 格式"
                  action="file/upload"
                  :limit="1"
                  :multiple="false"
                  :fileUrls="ruleForm.value"
                  @change="valueUploadChange"
                ></file-upload>
              </div>
              <div v-else class="detail-images">
                <div v-if="ruleForm.value" class="image-grid">
                  <el-image 
                    v-for="(item, index) in ruleForm.value.split(',')" 
                    :key="index"
                    :src="getImageUrl(item)" 
                    class="preview-img"
                    fit="contain"
                    :preview-src-list="[getImageUrl(item)]"
                  />
                </div>
                <el-empty v-else :image-size="60" description="未上传图片" />
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-actions">
          <el-button v-if="type !== 'info'" type="primary" @click="onSubmit" class="action-btn submit-btn">
            保存发布
          </el-button>
          <el-button @click="back" class="action-btn">
            {{ type === 'info' ? '返回列表' : '取消' }}
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, getCurrentInstance } from 'vue';

const { proxy } = getCurrentInstance();
const props = defineProps(["parent"]);

const id = ref('');
const type = ref('');
const ruleFormRef = ref(null);

const ruleForm = reactive({
  id: '',
  name: '',
  value: '',
});

const rules = {
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
  value: [{ required: true, message: '请上传轮播图片', trigger: 'change' }],
};

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return proxy.$base.url + url;
};

const valueUploadChange = (fileUrls) => {
  ruleForm.value = fileUrls;
};

const init = (targetId, targetType) => {
  if (targetId) {
    id.value = targetId;
    type.value = targetType;
    info(targetId);
  } else {
    // Reset form for new record
    ruleForm.id = '';
    ruleForm.name = '';
    ruleForm.value = '';
  }
};

const info = (targetId) => {
  proxy.$http({
    url: `config/info/${targetId}`,
    method: "get"
  }).then(({ data }) => {
    if (data && data.code === 0) {
      Object.assign(ruleForm, data.data);
    } else {
      proxy.$message.error(data.msg);
    }
  });
};

const onSubmit = () => {
  ruleFormRef.value.validate(valid => {
    if (valid) {
      proxy.$http({
        url: `config/${!ruleForm.id ? "save" : "update"}`,
        method: "post",
        data: ruleForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          proxy.$message.success("操作成功");
          back();
          props.parent.search();
        } else {
          proxy.$message.error(data.msg);
        }
      });
    }
  });
};

const back = () => {
  props.parent.showFlag = true;
  props.parent.addOrUpdateFlag = false;
};

defineExpose({ init });
</script>

<style lang="scss" scoped>
.add-edit-container {
  padding: 0;
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.modern-form-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  
  .card-header {
    .header-title {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
      border-left: 4px solid #3b82f6;
      padding-left: 15px;
    }
  }
}

.modern-form {
  padding: 20px 0;
  
  :deep(.el-form-item__label) {
    font-weight: 700;
    color: #475569;
    margin-bottom: 10px;
  }
}

.upload-wrapper {
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
  border: 1px dashed #cbd5e1;
}

.detail-images {
  .image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    
    .preview-img {
      width: 100%;
      height: 180px;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
  }
}

.form-actions {
  margin-top: 50px;
  padding-top: 30px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: center;
  gap: 20px;
  
  .action-btn {
    min-width: 140px;
    height: 46px;
    border-radius: 10px;
    font-size: 15px;
  }
  
  .submit-btn {
    box-shadow: 0 8px 16px rgba(59, 130, 246, 0.3);
  }
}
</style>
