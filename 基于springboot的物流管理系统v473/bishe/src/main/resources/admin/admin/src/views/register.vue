<template>
  <div class="register-wrapper">
    <div class="register-bg" :style="{ backgroundImage: 'url(/wuliuguanlixitong/img/back-img-bg.jpg)' }">
      <div class="dark-overlay"></div>
      
      <div class="register-card">
        <!-- 左侧：品牌展示 -->
        <div class="register-side">
          <div class="side-content">
            <h2 class="side-title">加入我们</h2>
            <div class="side-divider"></div>
            <p class="side-text">开启您的智慧物流之旅，体验实时监控与智能预警的便捷。</p>
            <div class="side-steps">
              <div class="step-item">
                <div class="step-icon">1</div>
                <span>填写基础信息</span>
              </div>
              <div class="step-item">
                <div class="step-icon">2</div>
                <span>完善个人资料</span>
              </div>
              <div class="step-item">
                <div class="step-icon">3</div>
                <span>开启智能管理</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：注册表单 -->
        <div class="register-main">
          <div class="form-header">
            <h1 class="form-title">创建账号</h1>
            <p class="form-subtitle">请填写以下信息完成注册</p>
          </div>

          <el-form class="actual-register-form" :model="ruleForm" label-position="top">
            <div class="form-scroll-area">
              <el-form-item label="账号" class="custom-item">
                <el-input v-model="ruleForm.username" placeholder="请输入账号" class="modern-input" />
              </el-form-item>

              <div class="form-row">
                <el-form-item label="密码" class="custom-item">
                  <el-input type="password" v-model="ruleForm.password" placeholder="请输入密码" show-password class="modern-input" />
                </el-form-item>
                <el-form-item label="重复密码" class="custom-item">
                  <el-input type="password" v-model="ruleForm.repetitionPassword" placeholder="请确认密码" show-password class="modern-input" />
                </el-form-item>
              </div>

              <!-- 动态字段：用户 -->
              <template v-if="tableName == 'yonghu'">
                <el-form-item label="用户姓名" class="custom-item">
                  <el-input v-model="ruleForm.yonghuName" placeholder="请输入姓名" class="modern-input" />
                </el-form-item>
                <div class="form-row">
                  <el-form-item label="手机号" class="custom-item">
                    <el-input v-model="ruleForm.yonghuPhone" placeholder="请输入手机号" class="modern-input" />
                  </el-form-item>
                  <el-form-item label="电子邮箱" class="custom-item">
                    <el-input v-model="ruleForm.yonghuEmail" placeholder="请输入邮箱" class="modern-input" />
                  </el-form-item>
                </div>
                <el-form-item label="身份证号" class="custom-item">
                  <el-input v-model="ruleForm.yonghuIdNumber" placeholder="请输入身份证号" class="modern-input" />
                </el-form-item>
              </template>

              <!-- 动态字段：员工 -->
              <template v-if="tableName == 'yuangong'">
                <el-form-item label="员工姓名" class="custom-item">
                  <el-input v-model="ruleForm.yuangongName" placeholder="请输入姓名" class="modern-input" />
                </el-form-item>
                <div class="form-row">
                  <el-form-item label="手机号" class="custom-item">
                    <el-input v-model="ruleForm.yuangongPhone" placeholder="请输入手机号" class="modern-input" />
                  </el-form-item>
                  <el-form-item label="电子邮箱" class="custom-item">
                    <el-input v-model="ruleForm.yuangongEmail" placeholder="请输入邮箱" class="modern-input" />
                  </el-form-item>
                </div>
                <el-form-item label="身份证号" class="custom-item">
                  <el-input v-model="ruleForm.yuangongIdNumber" placeholder="请输入身份证号" class="modern-input" />
                </el-form-item>
              </template>
            </div>

            <div class="button-area">
              <el-button class="submit-btn" type="primary" @click="login()">立即注册</el-button>
              <el-button class="cancel-btn" @click="close()">返回登录</el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      ruleForm: {
        username: '',
        password: '',
        repetitionPassword: '',
      },
      tableName: "",
      rules: {},
    };
  },
  mounted() {
    let table = this.$storage.get("loginTable");
    this.tableName = table || 'yonghu';
  },
  methods: {
    close() {
      this.$router.push({ path: "/login" });
    },
    login() {
      if (!this.ruleForm.username) {
        this.$message.error('账号不能为空');
        return;
      }
      if (!this.ruleForm.password) {
        this.$message.error('密码不能为空');
        return;
      }
      if (this.ruleForm.repetitionPassword != this.ruleForm.password) {
        this.$message.error('两次输入的密码不一致');
        return;
      }

      if (this.tableName == 'yonghu') {
        if (!this.ruleForm.yonghuName) { this.$message.error('姓名不能为空'); return; }
        if (this.ruleForm.yonghuPhone && !this.$validate.isMobile(this.ruleForm.yonghuPhone)) { this.$message.error('手机号格式不正确'); return; }
        if (!this.ruleForm.yonghuIdNumber) { this.$message.error('身份证号不能为空'); return; }
        if (this.ruleForm.yonghuEmail && !this.$validate.isEmail(this.ruleForm.yonghuEmail)) { this.$message.error('邮箱格式不正确'); return; }
      }

      if (this.tableName == 'yuangong') {
        if (!this.ruleForm.yuangongName) { this.$message.error('姓名不能为空'); return; }
        if (this.ruleForm.yuangongPhone && !this.$validate.isMobile(this.ruleForm.yuangongPhone)) { this.$message.error('手机号格式不正确'); return; }
        if (!this.ruleForm.yuangongIdNumber) { this.$message.error('身份证号不能为空'); return; }
        if (this.ruleForm.yuangongEmail && !this.$validate.isEmail(this.ruleForm.yuangongEmail)) { this.$message.error('邮箱格式不正确'); return; }
      }

      this.$http({
        url: `${this.tableName}/register`,
        method: "post",
        data: this.ruleForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: "注册成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              this.$router.replace({ path: "/login" });
            }
          });
        } else {
          this.$message.error(data.msg);
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.register-wrapper {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
}

.register-bg {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.dark-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  z-index: 1;
}

.register-card {
  position: relative;
  z-index: 10;
  width: 1000px;
  height: 700px;
  background: #fff;
  border-radius: 24px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

// 左侧品牌
.register-side {
  width: 35%;
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
  color: #fff;
  padding: 60px 40px;
  display: flex;
  align-items: center;

  .side-title {
    font-size: 32px;
    font-weight: 800;
    margin-bottom: 20px;
  }
  .side-divider {
    width: 60px;
    height: 4px;
    background: #93c5fd;
    border-radius: 2px;
    margin-bottom: 30px;
  }
  .side-text {
    font-size: 16px;
    line-height: 1.6;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 50px;
  }

  .step-item {
    display: flex;
    align-items: center;
    margin-bottom: 25px;
    .step-icon {
      width: 32px;
      height: 32px;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 15px;
      font-weight: 700;
    }
    span {
      font-size: 15px;
      font-weight: 500;
    }
  }
}

// 右侧主区
.register-main {
  width: 65%;
  padding: 40px 60px;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.form-header {
  margin-bottom: 30px;
  .form-title {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 8px;
  }
  .form-subtitle {
    font-size: 14px;
    color: #64748b;
  }
}

.actual-register-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .form-scroll-area {
    flex: 1;
    overflow-y: auto;
    padding-right: 10px;
    margin-bottom: 20px;

    &::-webkit-scrollbar { width: 5px; }
    &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
  }

  .form-row {
    display: flex;
    gap: 20px;
    .custom-item { flex: 1; }
  }

  .custom-item {
    margin-bottom: 18px !important;
    :deep(.el-form-item__label) {
      padding-bottom: 4px;
      font-weight: 600;
      color: #475569;
      font-size: 14px;
    }
  }

  .modern-input {
    :deep(.el-input__wrapper) {
      background-color: #f8fafc !important;
      border: 1.5px solid #e2e8f0 !important;
      border-radius: 12px !important;
      padding: 5px 15px !important;
      box-shadow: none !important;
      transition: all 0.2s;

      &.is-focus {
        background-color: #fff !important;
        border-color: #3b82f6 !important;
        box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1) !important;
      }
    }
  }

  .button-area {
    display: flex;
    gap: 15px;
    padding-top: 20px;
    border-top: 1px solid #f1f5f9;

    .submit-btn {
      flex: 2;
      height: 50px;
      border-radius: 12px;
      font-weight: 700;
      font-size: 16px;
      background: #2563eb;
      border: none;
      box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.3);
      &:hover { transform: translateY(-2px); box-shadow: 0 20px 25px -5px rgba(37, 99, 235, 0.4); }
    }

    .cancel-btn {
      flex: 1;
      height: 50px;
      border-radius: 12px;
      font-weight: 600;
      color: #64748b;
      border: 1.5px solid #e2e8f0;
      &:hover { color: #1e293b; border-color: #94a3b8; background: #f8fafc; }
    }
  }
}

@media screen and (max-width: 1100px) {
  .register-card {
    width: 90%;
    height: 90%;
    .register-side { display: none; }
    .register-main { width: 100%; padding: 40px; }
  }
}
</style>
