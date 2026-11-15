<template>
  <div class="add-edit-container">
    <el-card class="modern-form-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">{{ headerTitle }}</span>
        </div>
      </template>

      <el-form
        ref="ruleFormRef"
        class="modern-form"
        :model="ruleForm"
        :rules="rules"
        label-width="100px"
        label-position="top"
      >
        <el-row :gutter="40">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model.trim="ruleForm.username"
                placeholder="请输入用户名"
                :readonly="isDetail"
                autocomplete="off"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="ruleForm.password"
                placeholder="请输入密码"
                :readonly="isDetail"
                autocomplete="new-password"
                show-password
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-actions">
          <el-button
            v-if="!isDetail"
            type="primary"
            class="action-btn"
            @click="onSubmit"
          >
            提交保存
          </el-button>
          <el-button class="action-btn" @click="back">
            {{ isDetail ? '返回列表' : '取消' }}
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, nextTick, reactive, ref } from 'vue';

const { proxy } = getCurrentInstance();
const props = defineProps(['parent']);

const ruleFormRef = ref(null);
const id = ref('');
const type = ref('add');
const ruleForm = reactive({
  id: '',
  username: '',
  password: '',
});

const isDetail = computed(() => type.value === 'info');
const headerTitle = computed(() => {
  if (!id.value) {
    return '新增管理员';
  }
  return isDetail.value ? '管理员详情' : '修改管理员';
});

const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
};

const resetFormState = () => {
  id.value = '';
  type.value = 'add';
  ruleForm.id = '';
  ruleForm.username = '';
  ruleForm.password = '';
};

const clearFormValidate = () => {
  nextTick(() => {
    ruleFormRef.value?.clearValidate();
  });
};

const init = (targetId, targetType = 'add') => {
  resetFormState();
  type.value = targetType;

  if (targetId) {
    id.value = targetId;
    info(targetId);
    return;
  }

  clearFormValidate();
};

const info = (targetId) => {
  proxy.$http({
    url: `users/info/${targetId}`,
    method: 'get',
  }).then(({ data }) => {
    if (data && data.code === 0) {
      Object.assign(ruleForm, data.data || {});
      clearFormValidate();
    } else {
      proxy.$message.error(data.msg);
    }
  });
};

const onSubmit = () => {
  ruleFormRef.value?.validate((valid) => {
    if (!valid) {
      return;
    }

    proxy.$http({
      url: `users/${ruleForm.id ? 'update' : 'save'}`,
      method: 'post',
      data: ruleForm,
    }).then(({ data }) => {
      if (data && data.code === 0) {
        proxy.$message.success('操作成功');
        back();
        props.parent.search();
      } else {
        proxy.$message.error(data.msg);
      }
    });
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
  animation: fadeIn 0.4s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
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
      position: relative;
      padding-left: 12px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 18px;
        background: #3b82f6;
        border-radius: 2px;
      }
    }
  }
}

.modern-form {
  padding: 20px 0;

  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #475569;
    margin-bottom: 8px;
  }
}

.form-actions {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 30px;
  border-top: 1px solid #f1f5f9;

  .action-btn {
    min-width: 120px;
    height: 44px;
    font-size: 15px;
    border-radius: 10px;
  }
}
</style>
