<template>
  <div class="password-container">
    <el-card class="password-card modern-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">安全设置</span>
          <p class="header-desc">为了您的账号安全，请定期修改登录密码</p>
        </div>
      </template>

      <div class="password-form-wrapper">
        <el-form
          ref="ruleFormRef"
          :model="ruleForm"
          :rules="rules"
          label-position="top"
          class="modern-password-form"
        >
          <el-form-item label="当前密码" prop="password">
            <el-input 
              v-model="ruleForm.password" 
              placeholder="请输入当前正在使用的密码" 
              show-password 
              class="modern-input"
            >
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item label="新登录密码" prop="newpassword">
            <el-input 
              type="password" 
              v-model="ruleForm.newpassword" 
              placeholder="请输入 6-20 位新密码" 
              show-password 
              class="modern-input"
            >
              <template #prefix><el-icon><Key /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item label="确认新密码" prop="repassword">
            <el-input 
              type="password" 
              v-model="ruleForm.repassword" 
              placeholder="请再次输入新密码" 
              show-password 
              class="modern-input"
            >
              <template #prefix><el-icon><Finished /></el-icon></template>
            </el-input>
          </el-form-item>

          <div class="security-tips">
            <el-alert
              title="密码安全建议"
              type="info"
              :closable="false"
              show-icon
            >
              <p>1. 密码建议包含字母、数字和特殊符号。</p>
              <p>2. 请勿使用与其他网站相同的密码。</p>
            </el-alert>
          </div>

          <div class="form-footer">
            <el-button type="primary" class="update-btn" @click="onUpdateHandler" :loading="loading">
              重置登录密码
            </el-button>
          </div>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Lock, Key, Finished } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const { proxy } = getCurrentInstance();
const ruleFormRef = ref(null);
const loading = ref(false);
const user = ref({});

const ruleForm = reactive({
  password: '',
  newpassword: '',
  repassword: '',
});

const rules = {
  password: [{ required: true, message: "原密码不能为空", trigger: "blur" }],
  newpassword: [
    { required: true, message: "新密码不能为空", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" }
  ],
  repassword: [
    { required: true, message: "确认密码不能为空", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== ruleForm.newpassword) {
          callback(new Error("两次输入密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
};

onMounted(() => {
  proxy.$http({
    url: `${proxy.$storage.get("sessionTable")}/session`,
    method: "get"
  }).then(({ data }) => {
    if (data && data.code === 0) {
      user.value = data.data;
    }
  });
});

const onUpdateHandler = () => {
  ruleFormRef.value.validate(valid => {
    if (valid) {
      const originalPassword = user.value.mima || user.value.password;
      
      if (ruleForm.password !== originalPassword) {
        ElMessage.error("原密码验证失败，请重新输入");
        return;
      }

      loading.value = true;
      const updateData = { ...user.value };
      updateData.password = ruleForm.newpassword;
      updateData.mima = ruleForm.newpassword;

      proxy.$http({
        url: `${proxy.$storage.get("sessionTable")}/update`,
        method: "post",
        data: updateData
      }).then(({ data }) => {
        loading.value = false;
        if (data && data.code === 0) {
          ElMessage.success("密码修改成功，新密码将在下次登录时生效");
          ruleForm.password = '';
          ruleForm.newpassword = '';
          ruleForm.repassword = '';
        } else {
          ElMessage.error(data.msg);
        }
      }).catch(() => loading.value = false);
    }
  });
};
</script>

<style lang="scss" scoped>
.password-container {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.password-card {
  width: 500px;
  border-radius: 20px;
  border: none;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08) !important;

  .card-header {
    .header-title {
      font-size: 20px;
      font-weight: 800;
      color: #1e293b;
      display: block;
      margin-bottom: 4px;
    }
    .header-desc {
      font-size: 13px;
      color: #94a3b8;
      margin: 0;
    }
  }
}

.password-form-wrapper {
  padding: 10px 0;
}

.modern-password-form {
  :deep(.el-form-item__label) {
    font-weight: 700;
    color: #475569;
    margin-bottom: 8px;
  }

  .modern-input {
    :deep(.el-input__wrapper) {
      padding: 5px 15px;
      border-radius: 10px;
    }
  }
}

.security-tips {
  margin: 25px 0;
  p {
    font-size: 12px;
    margin: 5px 0;
    color: #64748b;
  }
}

.form-footer {
  margin-top: 30px;
  text-align: center;
  
  .update-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 700;
    border-radius: 12px;
    box-shadow: 0 8px 16px rgba(59, 130, 246, 0.25);
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 12px 20px rgba(59, 130, 246, 0.35);
    }
  }
}
</style>
