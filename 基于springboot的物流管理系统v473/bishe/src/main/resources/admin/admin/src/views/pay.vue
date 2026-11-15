<template>
  <div class="container">
    <el-alert title="确认支付前请先核对订单信息" type="success" :closable="false"></el-alert>
    <div class="pay-type-content">
      <div class="pay-type-item">
        <el-radio v-model="type" label="微信支付"></el-radio>
        <img src="@/assets/img/test/weixin.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="支付宝支付"></el-radio>
        <img src="@/assets/img/test/zhifubao.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="建设银行"></el-radio>
        <img src="@/assets/img/test/jianshe.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="农业银行"></el-radio>
        <img src="@/assets/img/test/nongye.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="中国银行"></el-radio>
        <img src="@/assets/img/test/zhongguo.png" alt>
      </div>
      <div class="pay-type-item">
        <el-radio v-model="type" label="交通银行"></el-radio>
        <img src="@/assets/img/test/jiaotong.png" alt>
      </div>
    </div>
    <div class="buton-content">
      <el-button @click="submitTap" type="primary">确认支付</el-button>
      <el-button @click="back">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const { proxy } = getCurrentInstance();

// 响应式数据
const type = ref('');
const table = ref('');
const obj = ref({});

// 生命周期钩子
onMounted(() => {
  table.value = proxy.$storage.get("paytable");
  obj.value = proxy.$storage.getObj("payObject");
});

// 提交支付
const submitTap = () => {
  if (!type.value) {
    ElMessage.error("请选择支付方式");
    return;
  }

  ElMessageBox.confirm(`确定支付?`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    obj.value.ispay = "已支付";

    proxy.$http({
      url: `${table.value}/update`,
      method: "post",
      data: obj.value
    }).then(({ data }) => {
      if (data && data.code === 0) {
        ElMessage.success({
          message: "支付成功",
          duration: 1500,
          onClose: () => {
            router.go(-1);
          }
        });
      } else {
        ElMessage.error(data.msg);
      }
    });
  }).catch(() => {
    // 用户取消支付
  });
};

// 返回上一页
const back = () => {
  router.go(-1);
};
</script>

<style lang="scss" scoped>
.container {
  margin: 10px;
  font-size: 14px;

  span {
    width: 60px;
  }

  .top-content {
    display: flex;
    align-items: center;
    padding: 20px;
  }

  .price-content {
    display: flex;
    align-items: center;
    margin-top: 20px;
    padding-bottom: 20px;
    padding: 20px;
    border-bottom: 1px solid #eeeeee;
    font-size: 20px;
    font-weight: bold;
    color: red;
  }

  .pay-type-content {
    display: flex;
    align-items: center;
    margin-top: 20px;
    flex-wrap: wrap;

    span {
      width: 100px;
    }

    .pay-type-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 300px;
      margin: 20px;
      border: 1px solid #eeeeee;
      padding: 20px;

      img {
        width: 60px;
        height: 60px;
        margin-left: 10px;
      }
    }
  }

  .buton-content {
    margin: 20px;
    display: flex;
    justify-content: center;
    gap: 20px;

    .el-button {
      width: 120px;
      height: 44px;
      font-size: 16px;
    }
  }
}
</style>
