<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="header-title">网盘认证管理</span>
          <div class="operation-buttons">
            <el-button class="toolbar-btn" type="primary" @click="handleAddAuth" plain>
              <el-icon>
                <Plus/>
              </el-icon>
              添加认证
            </el-button>
            <el-button class="toolbar-btn" type="success" @click="loadAuths" plain>
              <el-icon>
                <Refresh/>
              </el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div class="card-body">
        <div class="table-container">
          <el-table
              :data="authList"
              v-loading="loading"
              border
              stripe
              empty-text="暂无认证数据"
              class="data-table"
              height="100%"
          >
            <el-table-column
                label="网盘类型"
                prop="provider"
                width="120"
                align="center"
            >
              <template #default="{ row }">
                <div class="tag-wrapper">
                  <el-tag
                      :type="getProviderTagType(row.provider)"
                      class="status-tag"
                  >
                    {{ getProviderName(row.provider) }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column
                label="访问令牌"
                min-width="200"
            >
              <template #default="{ row }">
                <el-popover
                    v-if="row.access_token"
                    placement="bottom"
                    trigger="hover"
                    :width="500"
                    popper-class="token-popover"
                    :offset="8"
                >
                  <template #reference>
                    <span class="token-text">{{ row.access_token }}</span>
                  </template>
                  <div class="token-content">{{ row.access_token }}</div>
                </el-popover>
                <span v-else class="token-text no-content">-</span>
              </template>
            </el-table-column>

            <el-table-column
                label="刷新令牌"
                min-width="200"
            >
              <template #default="{ row }">
                <el-popover
                    v-if="row.refresh_token"
                    placement="bottom"
                    trigger="hover"
                    :width="500"
                    popper-class="token-popover"
                    :offset="8"
                >
                  <template #reference>
                    <span class="token-text">{{ row.refresh_token }}</span>
                  </template>
                  <div class="token-content">{{ row.refresh_token }}</div>
                </el-popover>
                <span v-else class="token-text no-content">-</span>
              </template>
            </el-table-column>

            <el-table-column
                label="Cookie"
                min-width="200"
            >
              <template #default="{ row }">
                <el-popover
                    v-if="row.cookie"
                    placement="bottom"
                    trigger="hover"
                    :width="500"
                    popper-class="token-popover"
                    :offset="8"
                >
                  <template #reference>
                    <span class="token-text">{{ row.cookie }}</span>
                  </template>
                  <div class="token-content">{{ row.cookie }}</div>
                </el-popover>
                <span v-else class="token-text no-content">-</span>
              </template>
            </el-table-column>

            <el-table-column
                label="操作"
                width="180"
                align="center"
                fixed="right"
            >
              <template #default="{ row }">
                <div class="auth-operation">
                  <el-button
                      @click="handleEdit(row)"
                      type="primary"
                      text
                  >
                    <el-icon>
                      <Edit/>
                    </el-icon>
                    编辑
                  </el-button>
                  <el-divider direction="vertical"/>
                  <el-button
                      @click="handleDelete(row)"
                      type="danger"
                      text
                  >
                    <el-icon>
                      <Delete/>
                    </el-icon>
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>

    <!-- 添加/编辑认证对话框 -->
    <el-dialog
        :title="isEdit ? '编辑认证' : '添加认证'"
        v-model="dialogVisible"
        width="600px"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog"
    >
      <el-form
          :model="authForm"
          ref="authFormRef"
          label-width="100px"
          :rules="formRules"
      >
        <el-form-item
            label="网盘类型"
            prop="provider"
        >
          <el-select
              v-model="authForm.provider"
              placeholder="请选择网盘类型"
              class="form-select"
          >
            <el-option label="夸克" :value="1"></el-option>
            <el-option label="阿里云盘" :value="2"></el-option>
            <el-option label="百度网盘" :value="3"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item
            label="访问令牌"
            prop="access_token"
            v-if="showAccessToken"
        >
          <el-input
              v-model="authForm.access_token"
              placeholder="请输入访问令牌"
              type="textarea"
              :rows="2"
          ></el-input>
        </el-form-item>

        <el-form-item
            label="刷新令牌"
            prop="refresh_token"
            v-if="showRefreshToken"
        >
          <el-input
              v-model="authForm.refresh_token"
              placeholder="请输入刷新令牌"
              type="textarea"
              :rows="2"
          ></el-input>
        </el-form-item>

        <el-form-item
            label="Cookie"
            prop="cookie"
        >
          <el-input
              v-model="authForm.cookie"
              placeholder="请输入Cookie"
              type="textarea"
              :rows="3"
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import {ref, onMounted, computed} from 'vue'
import {ElMessage, type FormInstance} from 'element-plus'
import type {CloudStorageAuth} from '@/types'
import {Plus, Refresh, Edit, Delete} from '@element-plus/icons-vue'
import {cloudStorageAuthApi} from "@/api/views/cloud_storage_auth";

defineOptions({
  name: "CloudStorageAuth"
})

const authList = ref<CloudStorageAuth[]>([])
const loading = ref(false)
const dialogVisible = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const authFormRef = ref<FormInstance | null>(null)

const authForm = ref<Partial<CloudStorageAuth>>({
  provider: 1,
  cookie: '',
  access_token: null,
  refresh_token: null
})

const showAccessToken = computed(() => authForm.value.provider !== 1)
const showRefreshToken = computed(() => authForm.value.provider !== 1)

// 修改表单验证规则
const formRules = {
  provider: [
    {required: true, message: '请选择网盘类型', trigger: 'change'}
  ],
  cookie: [
    {required: true, message: '请输入Cookie', trigger: 'blur'}
  ],
  access_token: [
    {
      required: computed(() => authForm.value.provider !== 1),
      message: '请输入访问令牌',
      trigger: 'blur'
    }
  ],
  refresh_token: [
    {
      required: computed(() => authForm.value.provider !== 1),
      message: '请输入刷新令牌',
      trigger: 'blur'
    }
  ]
}

const getProviderName = (provider: number) => {
  const providers = ['夸克', '阿里云盘', '百度网盘']
  return providers[provider - 1] || '未知'
}

const getProviderTagType = (provider: number) => {
  const types = ['', 'success', 'info', 'warning']
  return types[provider] || 'info'
}

const loadAuths = async () => {
  loading.value = true
  try {
    const response = await cloudStorageAuthApi.getAuths()
    if (response.data.code === 200) {
      authList.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '加载认证信息失败')
    }
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载认证信息失败')
  } finally {
    loading.value = false
  }
}

const handleAddAuth = () => {
  authForm.value = {
    provider: 1,
    cookie: '',
    access_token: null,
    refresh_token: null,
    id: undefined
  }
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: CloudStorageAuth) => {
  authForm.value = {...row}
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!authFormRef.value) return
  await authFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      let response
      const requestData = {
        provider: authForm.value.provider,
        cookie: authForm.value.cookie,
        access_token: authForm.value.access_token || '',
        refresh_token: authForm.value.refresh_token || ''
      } as CloudStorageAuth;

      if (isEdit.value) {
        response = await cloudStorageAuthApi.updateAuth(authForm.value.id!, requestData)
      } else {
        response = await cloudStorageAuthApi.addAuth(requestData)
      }
      if (response.data.code === 200) {
        ElMessage.success('操作成功')
        dialogVisible.value = false
        await loadAuths()
      } else {
        ElMessage.error(response.data.message || '操作失败')
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  })
}

const handleDelete = async (row: CloudStorageAuth) => {
  if (!row.id) {
    ElMessage.error('认证ID无效')
    return
  }
  try {
    const response = await cloudStorageAuthApi.deleteAuth(row.id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      await loadAuths()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadAuths()
})

</script>

<style lang="scss" scoped>
.auth-page {
  height: 100%;
}

.auth-card {
  height: 100%;
  border: none;
  border-radius: 10px;
}

:deep(.auth-card > .el-card__header) {
  padding: 14px 16px 10px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

:deep(.auth-card > .el-card__body) {
  height: calc(100% - 57px);
  box-sizing: border-box;
  padding: 12px 16px 16px;
}

.card-body {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.token-text {
  font-family: monospace;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  cursor: pointer;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.token-text.no-content {
  cursor: default;
  color: var(--el-text-color-placeholder);
}

.token-content {
  font-family: monospace;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-all;
  white-space: pre-wrap;
  max-height: 250px;
  overflow-y: auto;
  padding-right: 8px; /* 为滚动条预留空间 */
}

/* 美化滚动条样式 */
.token-content::-webkit-scrollbar {
  width: 4px; /* 滚动条宽度 */
}

.token-content::-webkit-scrollbar-thumb {
  background-color: var(--el-border-color-darker); /* 滚动条颜色 */
  border-radius: 2px; /* 圆角 */
}

.token-content::-webkit-scrollbar-track {
  background-color: var(--el-fill-color-lighter); /* 滚动条轨道颜色 */
  border-radius: 2px;
}

.form-select {
  width: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  gap: 12px;
  flex-wrap: wrap;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 8px;

  &::before {
    content: '';
    width: 3px;
    height: 16px;
    background: var(--el-color-primary);
    border-radius: 2px;
  }
}

.operation-buttons {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  margin-left: auto;
  flex-wrap: wrap;
}

.toolbar-btn {
  min-width: 92px;
}

.table-container {
  flex: 1;
  min-height: 0;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 10px;
  overflow: hidden;
  background: var(--el-bg-color);
}

.data-table {
  height: 100%;
  width: 100%;
}

:deep(.el-table__header th) {
  background-color: var(--el-fill-color-lighter) !important;
  font-weight: 500;
  color: var(--el-text-color-primary);
  font-size: 14px;
}

:deep(.el-table__cell) {
  padding: 10px 0;
}

.tag-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 32px;
  width: 100%;
}

.provider-tag {
  min-width: 80px;
  padding: 0 12px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  transform: none !important;
}

.auth-operation {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  :deep(.auth-card > .el-card__header) {
    padding: 10px 12px 8px;
  }

  :deep(.auth-card > .el-card__body) {
    height: calc(100% - 50px);
    padding: 8px 12px 12px;
  }

  .header-title {
    font-size: 15px;
  }

  .operation-buttons {
    width: 100%;
    margin-left: 0;
    justify-content: flex-start;
  }

  .toolbar-btn {
    min-width: 84px;
  }
}
</style>
