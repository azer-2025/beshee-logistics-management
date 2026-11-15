<template>
  <div class="center-container">
    <el-card class="profile-card modern-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">个人中心</span>
          <p class="header-desc">查看并完善您的个人资料</p>
        </div>
      </template>

      <div class="profile-layout">
        <!-- 左侧：头像与摘要 -->
        <div class="profile-sidebar">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar :size="120" :src="getUserAvatar()" class="main-avatar">
                {{ ruleForm.username ? ruleForm.username.charAt(0).toUpperCase() : 'U' }}
              </el-avatar>
              <div class="avatar-mask" v-if="flag !== 'users'">
                <file-upload
                  tip=""
                  action="file/upload"
                  :limit="1"
                  :multiple="false"
                  @change="handleAvatarChange"
                  class="avatar-uploader"
                >
                  <el-icon><Camera /></el-icon>
                </file-upload>
              </div>
            </div>
            <h2 class="display-name">{{ ruleForm.yonghuName || ruleForm.yuangongName || ruleForm.username }}</h2>
            <el-tag size="small" effect="light" type="primary" class="role-badge">{{ flag === 'users' ? '系统管理员' : flag === 'yonghu' ? '普通用户' : '员工' }}</el-tag>
          </div>

          <div class="sidebar-stats" v-if="flag === 'yonghu'">
            <div class="stat-item">
              <span class="stat-label">账户余额</span>
              <span class="stat-value">¥{{ ruleForm.newMoney || '0.00' }}</span>
              <div class="stat-actions">
                <el-button type="primary" size="small" link @click="chongzhi">充值</el-button>
                <el-divider direction="vertical" />
                <el-button type="danger" size="small" link @click="tixian">提现</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：详细表单 -->
        <div class="profile-main">
          <el-form
            ref="ruleFormRef"
            :model="ruleForm"
            label-position="top"
            class="modern-profile-form"
          >
            <el-row :gutter="30">
              <!-- 管理员仅有用户名 -->
              <el-col :span="24" v-if="flag === 'users'">
                <el-form-item label="管理员账号" prop="username">
                  <el-input v-model="ruleForm.username" placeholder="用户名" />
                </el-form-item>
              </el-col>

              <!-- 用户/员工共有字段 -->
              <template v-if="flag === 'yonghu' || flag === 'yuangong'">
                <el-col :span="12">
                  <el-form-item :label="flag === 'yonghu' ? '用户编号' : '员工编号'">
                    <el-input v-model="ruleForm[flag + 'UuidNumber']" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item :label="flag === 'yonghu' ? '用户姓名' : '员工姓名'" :prop="flag + 'Name'">
                    <el-input v-model="ruleForm[flag + 'Name']" placeholder="请输入姓名" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="联系电话" :prop="flag + 'Phone'">
                    <el-input v-model="ruleForm[flag + 'Phone']" placeholder="请输入手机号" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="电子邮箱" :prop="flag + 'Email'">
                    <el-input v-model="ruleForm[flag + 'Email']" placeholder="请输入邮箱" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="身份证号" :prop="flag + 'IdNumber'">
                    <el-input v-model="ruleForm[flag + 'IdNumber']" placeholder="请输入身份证号" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="性别" prop="sexTypes">
                    <el-select v-model="ruleForm.sexTypes" placeholder="请选择性别" style="width: 100%">
                      <el-option
                        v-for="item in sexTypesOptions"
                        :key="item.codeIndex"
                        :label="item.indexName"
                        :value="item.codeIndex"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </template>
            </el-row>

            <div class="form-footer">
              <el-button type="primary" class="save-btn" @click="onUpdateHandler" :loading="loading">
                保存修改
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Camera, User, Management } from '@element-plus/icons-vue';
import { isMobile, isEmail } from "@/utils/validate";

const { proxy } = getCurrentInstance();
const router = useRouter();

const ruleForm = reactive({});
const flag = ref('');
const sexTypesOptions = ref([]);
const loading = ref(false);

const getUserAvatar = () => {
  const photo = ruleForm.yonghuPhoto || ruleForm.yuangongPhoto;
  if (!photo) return '';
  if (photo.startsWith('http')) return photo;
  return proxy.$base.url + photo;
};

const handleAvatarChange = (fileUrls) => {
  if (flag.value === 'yonghu') ruleForm.yonghuPhoto = fileUrls;
  else if (flag.value === 'yuangong') ruleForm.yuangongPhoto = fileUrls;
};

const loadSession = () => {
  proxy.$http({
    url: `${proxy.$storage.get("sessionTable")}/session`,
    method: "get"
  }).then(({ data }) => {
    if (data && data.code === 0) {
      Object.assign(ruleForm, data.data);
    }
  });
};

onMounted(() => {
  flag.value = proxy.$storage.get("sessionTable");
  loadSession();

  proxy.$http({
    url: `dictionary/page?page=1&limit=100&dicCode=sex_types`,
    method: "get"
  }).then(({ data }) => {
    if (data && data.code === 0) sexTypesOptions.value = data.data.list;
  });
});

const chongzhi = () => router.push({ path: '/pay' });

const tixian = () => {
  ElMessageBox.confirm('确定要提现全部余额吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ruleForm.newMoney = 0;
    onUpdateHandler();
  });
};

const onUpdateHandler = () => {
  // 验证逻辑
  if (flag.value === 'yonghu' || flag.value === 'yuangong') {
    const f = flag.value;
    if (!ruleForm[f + 'Name']) return ElMessage.error('姓名不能为空');
    if (ruleForm[f + 'Phone'] && !isMobile(ruleForm[f + 'Phone'])) return ElMessage.error('手机格式不正确');
    if (!ruleForm[f + 'IdNumber']) return ElMessage.error('身份证号不能为空');
    if (ruleForm[f + 'Email'] && !isEmail(ruleForm[f + 'Email'])) return ElMessage.error('邮箱格式不正确');
    if (!ruleForm.sexTypes) return ElMessage.error('请选择性别');
  }

  if (flag.value === 'users' && !ruleForm.username) return ElMessage.error('用户名不能为空');

  loading.value = true;
  proxy.$http({
    url: `${proxy.$storage.get("sessionTable")}/update`,
    method: "post",
    data: ruleForm
  }).then(({ data }) => {
    loading.value = false;
    if (data && data.code === 0) {
      ElMessage.success("修改信息成功");
      loadSession();
    } else {
      ElMessage.error(data.msg);
    }
  }).catch(() => loading.value = false);
};
</script>

<style lang="scss" scoped>
.center-container {
  padding: 0;
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.profile-card {
  border-radius: 20px;
  overflow: hidden;
  border: none;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05) !important;

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

.profile-layout {
  display: flex;
  gap: 60px;
  padding: 20px 0;
}

/* 左侧边栏 */
.profile-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-right: 1px solid #f1f5f9;
  padding-right: 40px;

  .avatar-section {
    text-align: center;
    margin-bottom: 30px;

    .avatar-wrapper {
      position: relative;
      width: 120px;
      height: 120px;
      margin: 0 auto 20px;
      
      .main-avatar {
        box-shadow: 0 8px 16px rgba(59, 130, 246, 0.2);
        border: 4px solid #fff;
        background: #3b82f6;
        font-size: 40px;
        font-weight: 800;
      }

      .avatar-mask {
        position: absolute;
        bottom: 0;
        right: 0;
        width: 36px;
        height: 36px;
        background: #fff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        cursor: pointer;
        z-index: 10;
        
        .avatar-uploader {
          :deep(.el-upload) {
            display: flex;
            align-items: center;
            justify-content: center;
          }
          .el-icon {
            font-size: 18px;
            color: #3b82f6;
          }
        }
      }
    }

    .display-name {
      font-size: 22px;
      font-weight: 700;
      color: #1e293b;
      margin: 0 0 8px 0;
    }
    
    .role-badge {
      padding: 4px 12px;
      border-radius: 20px;
      font-weight: 600;
    }
  }

  .sidebar-stats {
    width: 100%;
    .stat-item {
      background: #f8fafc;
      padding: 20px;
      border-radius: 16px;
      text-align: center;
      border: 1px solid #e2e8f0;

      .stat-label {
        font-size: 12px;
        color: #64748b;
        font-weight: 600;
        display: block;
        margin-bottom: 8px;
        text-transform: uppercase;
      }
      .stat-value {
        font-size: 24px;
        font-weight: 800;
        color: #1e293b;
        display: block;
        margin-bottom: 15px;
      }
      .stat-actions {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 5px;
      }
    }
  }
}

/* 右侧主表单 */
.profile-main {
  flex: 1;

  .modern-profile-form {
    :deep(.el-form-item__label) {
      font-weight: 700;
      color: #475569;
      margin-bottom: 8px;
      padding: 0;
    }

    .form-footer {
      margin-top: 40px;
      padding-top: 30px;
      border-top: 1px solid #f1f5f9;
      
      .save-btn {
        padding: 12px 40px;
        height: auto;
        font-size: 16px;
        font-weight: 700;
        border-radius: 12px;
        box-shadow: 0 8px 16px rgba(59, 130, 246, 0.3);
        transition: all 0.3s;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 12px 20px rgba(59, 130, 246, 0.4);
        }
      }
    }
  }
}

@media (max-width: 1024px) {
  .profile-layout {
    flex-direction: column;
    align-items: center;
    gap: 40px;
  }
  .profile-sidebar {
    width: 100%;
    padding-right: 0;
    border-right: none;
    border-bottom: 1px solid #f1f5f9;
    padding-bottom: 40px;
  }
}
</style>
