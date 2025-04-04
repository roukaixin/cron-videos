<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">Aria2 配置</span>
          <div class="operation-buttons">
            <el-button type="primary" @click="handleAddConnection" plain>
              <el-icon>
                <Plus/>
              </el-icon>
              添加配置
            </el-button>
            <el-button type="success" @click="loadConnections" plain>
              <el-icon>
                <Refresh/>
              </el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div class="table-container">
        <el-table
            :data="downloaderList"
            border
            stripe
            class="data-table"
        >
          <el-table-column
              prop="type"
              label="下载器类型"
              min-width="140"
              align="center"
          >
            <template #default="{ row }">
              <div class="tag-wrapper">
                <el-tag
                    type="primary"
                    class="status-tag"
                >
                  {{ getDownloaderType(row.type) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column
              prop="protocol"
              label="协议"
              min-width="100"
              align="center"
          >
            <template #default="{ row }">
              <span class="server-address">{{ row.protocol }}</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="host"
              label="下载器主机地址"
              min-width="140"
              align="center"
          >
            <template #default="{ row }">
              <span class="server-address">{{ row.host }}</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="port"
              label="端口"
              min-width="100"
              align="center"
          >
            <template #default="{ row }">
              <span class="port-text">{{ row.port }}</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="secret"
              label="密钥"
              min-width="120"
              align="center"
          >
            <template #default="{ row }">
              <el-popover
                  v-if="row.secret"
                  placement="top"
                  trigger="click"
                  :width="200"
                  popper-class="secret-popover"
              >
                <template #reference>
                  <span class="secret-text cursor-pointer">••••••</span>
                </template>
                <div class="secret-content">
                  <span class="secret-value">{{ row.secret }}</span>
                  <el-button
                      type="primary"
                      link
                      size="small"
                      @click="copySecret(row.secret)"
                  >复制
                  </el-button>
                </div>
              </el-popover>
              <span v-else class="no-secret">未设置</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="weight"
              label="权重"
              min-width="100"
              align="center"
          >
            <template #default="{ row }">
              <div class="tag-wrapper">
                <el-tag
                    type="warning"
                    effect="light"
                    class="weight-tag"
                    size="large"
                >
                  {{ row.weight }}
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column
              label="状态"
              min-width="100"
              align="center"
          >
            <template #default="{ row }">
              <div class="tag-wrapper">
                <el-tag
                    :type="row.isOnline ? 'success' : 'danger'"
                    effect="light"
                    class="status-tag"
                    size="large"
                >
                  <span class="status-content">
                    <el-icon v-if="row.isOnline" class="status-icon"><CircleCheck/></el-icon>
                    <el-icon v-else class="status-icon"><CircleClose/></el-icon>
                    <span class="status-text">{{ row.isOnline ? '在线' : '离线' }}</span>
                  </span>
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column
              label="操作"
              min-width="220"
              align="center"
          >
            <template #default="{ row }">
              <div class="connection-operation">
                <el-button
                    @click="handleEdit(row)"
                    type="primary"
                    text
                    style="transform: none"
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
                    style="transform: none"
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

      <!-- 添加/编辑服务对话框 -->
      <el-dialog
          :title="isEdit ? '编辑服务' : '添加服务'"
          v-model="dialogVisible"
          width="500px"
          :close-on-click-modal="false"
          destroy-on-close
      >
        <el-form
            :model="connectionForm"
            ref="formRef"
            label-width="100px"
            :rules="formRules"
        >
          <el-form-item label="下载器类型" prop="type">
            <el-select
                v-model="connectionForm.type"
                placeholder="请选择下载器类型"
                class="form-select"
            >
              <el-option
                  v-for="item in downloaderType"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="协议" prop="protocol">
            <el-select
                v-model="connectionForm.protocol"
                placeholder="请输入协议"
                class="form-select"
            >
              <el-option label="ws" value="ws"></el-option>
              <el-option label="wss" value="wss"></el-option>
              <el-option label="http" value="http"></el-option>
              <el-option label="https" value="https"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="下载器主机地址" prop="host">
            <el-input
                v-model="connectionForm.host"
                placeholder="请输入下载器主机地址"
            ></el-input>
          </el-form-item>

          <el-form-item label="端口" prop="port">
            <el-input-number
                v-model="connectionForm.port"
                :min="1"
                :max="65535"
                class="port-input"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="密钥" prop="secret">
            <el-input
                v-model="connectionForm.secret"
                placeholder="请输入RPC密钥（可选）"
                type="password"
                show-password
            ></el-input>
          </el-form-item>

          <el-form-item label="权重" prop="weight">
            <el-input-number
                v-model="connectionForm.weight"
                :min="1"
                :max="100"
                class="weight-input"
            ></el-input-number>
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script lang="ts">
import {defineComponent, ref, onMounted} from 'vue'
import {ElMessage, type FormInstance} from 'element-plus'
import {Plus, Refresh, Edit, Delete, CircleCheck, CircleClose} from '@element-plus/icons-vue'
import {Downloader} from '@/types'
import {downloaderApi} from "@/api/views/downloader";

export default defineComponent({
  name: 'Downloader',
  components: {
    Plus,
    Refresh,
    Edit,
    Delete,
    CircleCheck,
    CircleClose
  },
  setup() {
    // 下载器列表
    const downloaderList = ref<Downloader[]>([])
    const loading = ref(false)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref<FormInstance | null>(null)

    const connectionForm = ref<Partial<Downloader>>({
      type: 0,
      protocol: '',
      host: '',
      port: 6800,
      secret: '',
      weight: 1
    })

    const formRules = {
      ip: [
        {required: true, message: '请输入服务器地址', trigger: 'blur'}
      ],
      port: [
        {required: true, message: '请输入端口号', trigger: 'blur'},
        {type: 'number', min: 1, max: 65535, message: '端口号范围1-65535', trigger: 'blur'}
      ],
      weight: [
        {required: true, message: '请输入权重', trigger: 'blur'},
        {type: 'number', min: 1, max: 100, message: '权重范围1-100', trigger: 'blur'}
      ]
    }

    const loadConnections = async () => {
      loading.value = true
      try {
        const response = await downloaderApi.getDownloaderList()
        if (response.data.code === 200) {
          downloaderList.value = response.data.data
        } else {
          ElMessage.error(response.data.message || '加载连接列表失败')
        }
      } catch (error) {
        console.error('加载失败:', error)
        ElMessage.error('加载连接列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleAddConnection = () => {
      connectionForm.value = {
        type: 0,
        protocol: '',
        host: '',
        port: 6800,
        secret: '',
        weight: 1
      }
      isEdit.value = false
      dialogVisible.value = true
    }

    const handleEdit = (row: Downloader) => {
      connectionForm.value = {...row}
      isEdit.value = true
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (!valid) return
        try {
          let response
          if (isEdit.value) {
            response = await downloaderApi.updateDownloader(connectionForm.value.id!, {
              type: connectionForm.value.type!,
              protocol: connectionForm.value.protocol!,
              host: connectionForm.value.host!,
              port: connectionForm.value.port!,
              secret: connectionForm.value.secret!,
              weight: connectionForm.value.weight!
            })
          } else {
            response = await downloaderApi.addDownloader({
              type: connectionForm.value.type!,
              protocol: connectionForm.value.protocol!,
              host: connectionForm.value.host!,
              port: connectionForm.value.port!,
              secret: connectionForm.value.secret!,
              weight: connectionForm.value.weight!
            })
          }

          if (response.data.code === 200) {
            ElMessage.success('操作成功')
            dialogVisible.value = false
            await loadConnections()
          } else {
            ElMessage.error(response.data.message || '操作失败')
          }
        } catch (error) {
          console.error('操作失败:', error)
          ElMessage.error('操作失败，请重试')
        }
      })
    }

    const handleDelete = async (row: Downloader) => {
      try {
        const response = await downloaderApi.deleteDownloader(row.id)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          await loadConnections()
        } else {
          ElMessage.error(response.data.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }

    const copySecret = async (secret: string) => {
      try {
        await navigator.clipboard.writeText(secret)
        ElMessage.success('密钥已复制')
      } catch (err) {
        ElMessage.error('复制失败')
      }
    }

    const downloaderType = [
      {
        value: 0,
        label: "aria2"
      },
      {
        value: 1,
        label: "qbittorrent"
      }
    ]

    const getDownloaderType = (type: number) => {
      const item = downloaderType.find(item => item.value === type);
      return item ? item.label : '未知'
    }

    onMounted(() => {
      loadConnections()
    })

    return {
      downloaderList,
      loading,
      dialogVisible,
      isEdit,
      connectionForm,
      formRef,
      formRules,
      handleAddConnection,
      handleEdit,
      handleDelete,
      handleSubmit,
      copySecret,
      loadConnections,
      getDownloaderType,
      downloaderType
    }
  }
})
</script>

<style scoped>

.server-address,
.port-text {
  font-family: monospace;
  font-size: 13px;
  color: var(--el-text-color-regular);
  font-weight: 600;
}

.secret-text {
  color: var(--el-text-color-secondary);
  font-family: monospace;
  letter-spacing: 2px;
  font-weight: 600;
}

.secret-value {
  font-family: monospace;
  color: var(--el-text-color-regular);
  font-size: 13px;
  word-break: break-all;
  font-weight: 600;
}

.cursor-pointer {
  cursor: pointer;
}

.no-secret {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.secret-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.tag-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.weight-tag,
.status-tag {
  min-width: 90px;
  height: 32px;
  padding: 0 12px;
}

.status-content {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 100%;
}

.status-icon {
  font-size: 16px;
}

.status-text {
  font-size: 14px;
  font-weight: 500;
  line-height: 1;
}

.connection-operation {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  white-space: nowrap;
}

/* 头部按钮组样式 */
.card-header > .operation-buttons {
  display: flex;
  align-items: center;
  justify-content: flex-end; /* 改为靠右对齐 */
  gap: 12px;
  margin-left: auto; /* 确保靠右 */
}
</style>