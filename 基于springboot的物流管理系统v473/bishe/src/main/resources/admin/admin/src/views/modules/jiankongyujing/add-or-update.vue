<template>
  <div class="add-edit-block">
    <el-card shadow="never">
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预警编号" prop="jiankongyujingUuidNumber">
              <el-input v-model="ruleForm.jiankongyujingUuidNumber" :readonly="type === 'info'" placeholder="请输入预警编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预警标题" prop="jiankongyujingName">
              <el-input v-model="ruleForm.jiankongyujingName" :readonly="type === 'info'" placeholder="请输入预警标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监控类型" prop="jiankongTypes">
              <el-select v-model="ruleForm.jiankongTypes" :disabled="type === 'info'" placeholder="请选择监控类型" style="width: 100%">
                <el-option v-for="item in jiankongTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预警级别" prop="yujingLevelTypes">
              <el-select v-model="ruleForm.yujingLevelTypes" :disabled="type === 'info'" placeholder="请选择预警级别" style="width: 100%">
                <el-option v-for="item in yujingLevelTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预警状态" prop="yujingStatusTypes">
              <el-select v-model="ruleForm.yujingStatusTypes" :disabled="type === 'info'" placeholder="请选择预警状态" style="width: 100%">
                <el-option v-for="item in yujingStatusTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预警方式" prop="yujingModeTypes">
              <el-select v-model="ruleForm.yujingModeTypes" :disabled="type === 'info'" placeholder="请选择预警方式" style="width: 100%">
                <el-option v-for="item in yujingModeTypesOptions" :key="item.codeIndex" :label="item.indexName" :value="item.codeIndex" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预警来源" prop="yujingSource">
              <el-input v-model="ruleForm.yujingSource" :readonly="type === 'info'" placeholder="请输入预警来源" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监控对象" prop="jiankongObject">
              <el-input v-model="ruleForm.jiankongObject" :readonly="type === 'info'" placeholder="请输入监控对象" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监控数据" prop="jiankongData">
              <el-input v-model="ruleForm.jiankongData" :readonly="type === 'info'" placeholder="请输入监控数据" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预警阈值" prop="yujingThreshold">
              <el-input v-model="ruleForm.yujingThreshold" :readonly="type === 'info'" placeholder="请输入预警阈值" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监控时间">
              <el-input v-model="ruleForm.jiankongTime" readonly placeholder="系统自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理时间">
              <el-input v-model="ruleForm.chuliTime" readonly placeholder="状态变为已解除后自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="预警说明" prop="yujingContent">
              <el-input
                v-model="ruleForm.yujingContent"
                type="textarea"
                :rows="4"
                :readonly="type === 'info'"
                placeholder="请输入预警说明"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理说明">
              <el-input
                v-model="ruleForm.chuliContent"
                type="textarea"
                :rows="4"
                :readonly="type === 'info'"
                placeholder="请输入处理说明"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="form-btns">
          <el-button v-if="type !== 'info'" type="primary" @click="onSubmit">提交</el-button>
          <el-button @click="back">{{ type === 'info' ? '返回' : '取消' }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  props: ['parent'],
  data() {
    return {
      id: '',
      type: 'edit',
      jiankongTypesOptions: [],
      yujingLevelTypesOptions: [],
      yujingStatusTypesOptions: [],
      yujingModeTypesOptions: [],
      ruleForm: {
        id: '',
        jiankongyujingUuidNumber: '',
        jiankongyujingName: '',
        jiankongTypes: '',
        yujingLevelTypes: 2,
        yujingStatusTypes: 1,
        yujingModeTypes: 2,
        yujingSource: '人工录入',
        jiankongObject: '',
        jiankongData: '',
        yujingThreshold: '',
        jiankongTime: '',
        yujingContent: '',
        chuliContent: '',
        chuliTime: '',
      },
      rules: {
        jiankongyujingUuidNumber: [{ required: true, message: '预警编号不能为空', trigger: 'blur' }],
        jiankongyujingName: [{ required: true, message: '预警标题不能为空', trigger: 'blur' }],
        jiankongTypes: [{ required: true, message: '监控类型不能为空', trigger: 'change' }],
        yujingLevelTypes: [{ required: true, message: '预警级别不能为空', trigger: 'change' }],
        yujingStatusTypes: [{ required: true, message: '预警状态不能为空', trigger: 'change' }],
        yujingModeTypes: [{ required: true, message: '预警方式不能为空', trigger: 'change' }],
        yujingSource: [{ required: true, message: '预警来源不能为空', trigger: 'blur' }],
        jiankongObject: [{ required: true, message: '监控对象不能为空', trigger: 'blur' }],
        jiankongData: [{ required: true, message: '监控数据不能为空', trigger: 'blur' }],
        yujingThreshold: [{ required: true, message: '预警阈值不能为空', trigger: 'blur' }],
        yujingContent: [{ required: true, message: '预警说明不能为空', trigger: 'blur' }],
      },
    }
  },
  created() {
    this.loadSelectOptions()
  },
  methods: {
    loadSelectOptions() {
      this.$http({ url: 'dictionary/page?dicCode=jiankong_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.jiankongTypesOptions = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=yujing_level_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.yujingLevelTypesOptions = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=yujing_status_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.yujingStatusTypesOptions = data.data.list
      })
      this.$http({ url: 'dictionary/page?dicCode=yujing_mode_types&page=1&limit=100', method: 'get' }).then(({ data }) => {
        if (data && data.code === 0) this.yujingModeTypesOptions = data.data.list
      })
    },
    init(id, type) {
      this.type = type || 'edit'
      this.id = id || ''
      if (id) {
        this.info(id)
      } else {
        this.resetForm()
      }
    },
    resetForm() {
      this.ruleForm = {
        id: '',
        jiankongyujingUuidNumber: this.generateCode(),
        jiankongyujingName: '',
        jiankongTypes: '',
        yujingLevelTypes: 2,
        yujingStatusTypes: 1,
        yujingModeTypes: 2,
        yujingSource: '人工录入',
        jiankongObject: '',
        jiankongData: '',
        yujingThreshold: '',
        jiankongTime: '',
        yujingContent: '',
        chuliContent: '',
        chuliTime: '',
      }
      this.$nextTick(() => {
        this.$refs.ruleFormRef && this.$refs.ruleFormRef.clearValidate()
      })
    },
    info(id) {
      this.$http({
        url: `jiankongyujing/info/${id}`,
        method: 'get',
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = {
            yujingModeTypes: 2,
            yujingSource: '人工录入',
            ...data.data,
          }
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    onSubmit() {
      this.$refs.ruleFormRef.validate((valid) => {
        if (!valid) return
        this.$http({
          url: `jiankongyujing/${this.ruleForm.id ? 'update' : 'save'}`,
          method: 'post',
          data: this.ruleForm,
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              type: 'success',
              message: '操作成功',
              duration: 1500,
              onClose: () => {
                this.parent.showFlag = true
                this.parent.addOrUpdateFlag = false
                this.parent.search()
              },
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      })
    },
    back() {
      this.parent.showFlag = true
      this.parent.addOrUpdateFlag = false
    },
    generateCode() {
      return `YJ${new Date().getTime()}`
    },
  },
}
</script>

<style lang="scss" scoped>
.add-edit-block {
  .el-card {
    border-radius: 12px;
  }
}

.form-btns {
  margin-top: 12px;
}
</style>
