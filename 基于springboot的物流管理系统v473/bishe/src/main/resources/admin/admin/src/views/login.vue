<template>
  <div class="login-wrapper-unique">
    <div class="login-bg-layer" :style="{ backgroundImage: 'url(/wuliuguanlixitong/img/back-img-bg.jpg)' }">
      <div class="dark-mask"></div>
      
      <div class="login-card-box">
        <!-- 左侧：品牌区 -->
        <div class="side-branding">
          <div class="brand-inner">
            <h2 class="brand-name">智慧物流系统</h2>
            <div class="brand-line"></div>
            <p class="brand-slogan">智慧冷链 · 实时监控 · 安全预警</p>
            <div class="brand-tags">
              <span>高效</span>
              <span>精准</span>
              <span>智能</span>
            </div>
          </div>
        </div>

        <!-- 右侧：表单区 -->
        <div class="main-form-section">
          <div class="form-title-group">
            <h1 class="welcome-text">欢迎登录</h1>
            <p class="welcome-sub">请使用您的账号和密码进行访问</p>
          </div>

          <el-form class="actual-login-form" :model="rulesForm" @keyup.enter="login">
            <div class="input-group">
              <label class="label-text">用户名</label>
              <el-input
                v-model="rulesForm.username"
                placeholder="请输入您的账号"
                class="modern-input-field"
              >
                <template #prefix>
                  <svg-icon icon-class="user" class="field-icon" />
                </template>
              </el-input>
            </div>

            <div class="input-group">
              <label class="label-text">密码</label>
              <el-input
                v-model="rulesForm.password"
                type="password"
                placeholder="请输入您的密码"
                show-password
                class="modern-input-field"
              >
                <template #prefix>
                  <svg-icon icon-class="password" class="field-icon" />
                </template>
              </el-input>
            </div>

            <div v-if="roleOptions.length > 1" class="role-group">
              <el-radio-group v-model="rulesForm.role" class="modern-role-radios">
                <el-radio
                  v-for="item in roleOptions"
                  :key="item.value"
                  :label="item.value"
                >
                  {{ item.key }}
                </el-radio>
              </el-radio-group>
            </div>

            <div class="btn-group">
              <el-button 
                type="primary" 
                class="grand-login-btn" 
                @click="login"
                :loading="loading"
              >
                登 录 系 统
              </el-button>
            </div>

            <div class="form-bottom">
              <span class="reg-tip" @click="register('yuangong')">还没有账号？点此注册</span>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getCurrentInstance } from 'vue';
import { ElMessage } from 'element-plus';
import menu from "@/utils/menu";

const { proxy } = getCurrentInstance();
const router = useRouter();

const loading = ref(false);
const rulesForm = reactive({
  username: "",
  password: "",
  role: "",
});

const menus = ref([]);
const roleOptions = ref([]);
const tableName = ref("");

const register = (tableName) => {
  proxy.$storage.set("loginTable", tableName);
  router.push({ path: '/register' });
};

const login = () => {
  if (!rulesForm.username) {
    ElMessage.error("请输入用户名");
    return;
  }
  if (!rulesForm.password) {
    ElMessage.error("请输入密码");
    return;
  }
  if (roleOptions.value.length > 1 && !rulesForm.role) {
    ElMessage.error("请选择角色");
    return;
  }

  loading.value = true;
  let menuList = menus.value;
  if (roleOptions.value.length > 1) {
    for (let i = 0; i < menuList.length; i++) {
      if (menuList[i].tableName == rulesForm.role) {
        tableName.value = menuList[i].tableName;
        rulesForm.role = menuList[i].roleName;
      }
    }
  } else if (roleOptions.value.length === 1) {
    tableName.value = roleOptions.value[0].value;
    rulesForm.role = roleOptions.value[0].key;
  }

  proxy.$http({
    url: `${tableName.value}/login?username=${rulesForm.username}&password=${rulesForm.password}`,
    method: "post"
  }).then(({ data }) => {
    loading.value = false;
    if (data && data.code === 0) {
      proxy.$storage.set("Token", data.token);
      proxy.$storage.set("userId", data.userId);
      proxy.$storage.set("role", rulesForm.role);
      proxy.$storage.set("sessionTable", tableName.value);
      proxy.$storage.set("adminName", rulesForm.username);

      ElMessage.success({
        message: "登录成功",
        duration: 1500,
        onClose: () => {
          router.replace({ path: "/" });
        }
      });
    } else {
      ElMessage.error(data.msg);
    }
  }).catch(() => {
    loading.value = false;
  });
};

onMounted(() => {
  menus.value = menu.list();
  for (let i = 0; i < menus.value.length; i++) {
    if (menus.value[i].hasBackLogin === '是') {
      let menuItem = {};
      menuItem["key"] = menus.value[i].roleName;
      menuItem["value"] = menus.value[i].tableName;
      roleOptions.value.push(menuItem);
    }
  }
  if (roleOptions.value.length > 0) {
    rulesForm.role = roleOptions.value[0].value;
  }
});
</script>

<style lang="scss" scoped>
.login-wrapper-unique {
  width: 100vw !important;
  height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  overflow: hidden !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  z-index: 1000 !important;
}

.login-bg-layer {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  position: relative;
}

.dark-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(5px);
  z-index: 1;
}

.login-card-box {
  position: relative !important;
  z-index: 10 !important;
  width: 900px !important;
  height: 580px !important;
  background: #ffffff !important;
  border-radius: 24px !important;
  display: flex !important;
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5) !important;
}

// 左侧品牌区
.side-branding {
  width: 40% !important;
  height: 100% !important;
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  color: #fff !important;
  padding: 40px !important;
  box-sizing: border-box !important;

  .brand-inner {
    text-align: left;
    .brand-name {
      font-size: 34px;
      font-weight: 800;
      margin: 0 0 15px 0;
      letter-spacing: 2px;
    }
    .brand-line {
      width: 50px;
      height: 4px;
      background: #93c5fd;
      border-radius: 2px;
      margin-bottom: 25px;
    }
    .brand-slogan {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.8);
      margin-bottom: 30px;
      line-height: 1.5;
    }
    .brand-tags {
      display: flex;
      gap: 10px;
      span {
        padding: 4px 12px;
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.3);
        border-radius: 20px;
        font-size: 12px;
      }
    }
  }
}

// 右侧表单区
.main-form-section {
  width: 60% !important;
  height: 100% !important;
  padding: 50px 70px !important;
  box-sizing: border-box !important;
  background: #fff !important;
  display: flex !important;
  flex-direction: column !important;
  justify-content: center !important;
  position: relative !important;
}

.form-title-group {
  margin-bottom: 35px;
  .welcome-text {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 10px 0;
  }
  .welcome-sub {
    font-size: 14px;
    color: #64748b;
  }
}

.actual-login-form {
  position: relative !important;
  width: 100% !important;
  margin: 0 !important;
  right: auto !important;
  left: auto !important;
  top: auto !important;
  bottom: auto !important;

  .input-group {
    width: 100% !important;
    margin-bottom: 22px;
    .label-text {
      display: block;
      font-size: 14px;
      font-weight: 600;
      color: #475569;
      margin-bottom: 8px;
    }
  }

  .modern-input-field {
    width: 100% !important;
    display: block !important;
    :deep(.el-input__wrapper) {
      width: 100% !important;
      box-sizing: border-box !important;
      background-color: #f8fafc !important;
      border: 1.5px solid #e2e8f0 !important;
      border-radius: 12px !important;
      padding: 8px 15px !important;
      box-shadow: none !important;
      transition: all 0.2s !important;
      
      &.is-focus {
        background-color: #fff !important;
        border-color: #3b82f6 !important;
        box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1) !important;
      }
    }
    .field-icon {
      font-size: 18px;
      color: #94a3b8;
    }
  }

  .role-group {
    width: 100% !important;
    margin: 10px 0 30px 0;
    .modern-role-radios {
      display: flex !important;
      flex-wrap: wrap !important;
      gap: 15px !important;
      width: 100% !important;
      :deep(.el-radio) {
        flex: 1 !important;
        min-width: 100px !important;
        margin: 0 !important;
        padding: 10px 16px !important;
        border-radius: 12px !important;
        border: 1.5px solid #e2e8f0 !important;
        transition: all 0.2s !important;
        display: flex !important;
        justify-content: center !important;
        align-items: center !important;
        height: auto !important;

        &.is-checked {
          background: rgba(59, 130, 246, 0.05) !important;
          border-color: #3b82f6 !important;
        }
        .el-radio__input { display: none !important; }
        .el-radio__label { 
          padding: 0 !important; 
          font-weight: 600 !important;
          color: #64748b !important;
        }
        &.is-checked .el-radio__label { color: #3b82f6 !important; }
      }
    }
  }

  .btn-group {
    width: 100% !important;
  }

  .grand-login-btn {
    width: 100% !important;
    height: 54px !important;
    border-radius: 12px !important;
    background: #2563eb !important;
    border: none !important;
    font-size: 16px !important;
    font-weight: 700 !important;
    letter-spacing: 2px !important;
    box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.3) !important;
    transition: all 0.3s !important;

    &:hover {
      background: #1d4ed8 !important;
      transform: translateY(-2px);
      box-shadow: 0 20px 25px -5px rgba(37, 99, 235, 0.4) !important;
    }
  }

  .form-bottom {
    margin-top: 25px;
    text-align: center;
    .reg-tip {
      font-size: 14px;
      color: #64748b;
      cursor: pointer;
      font-weight: 500;
      &:hover {
        color: #3b82f6;
        text-decoration: underline;
      }
    }
  }
}

// 响应式调整
@media screen and (max-width: 950px) {
  .login-card-box {
    width: 450px !important;
    height: auto !important;
    min-height: 500px !important;
    .side-branding { display: none !important; }
    .main-form-section { 
      width: 100% !important; 
      padding: 40px !important; 
    }
  }
}
</style>
