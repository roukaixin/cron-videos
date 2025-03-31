<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">下载管理</span>
          <div class="operation-buttons">
            <el-input
                v-model="queryParams.name"
                placeholder="搜索影视标题"
                class="search-input"
                clearable
                @clear="handleSearch"
                @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon>
                  <Search/>
                </el-icon>
              </template>
            </el-input>
            <el-select
                v-model="queryParams.status"
                placeholder="状态筛选"
                clearable
                class="status-select"
                @change="handleSearch"
            >
              <el-option label="等待中" :value="0"/>
              <el-option label="下载中" :value="1"/>
              <el-option label="已完成" :value="2"/>
              <el-option label="已失败" :value="3"/>
            </el-select>
            <el-button
                type="danger"
                :disabled="!selectedTasks.length"
                @click="handleBatchDelete"
                plain
            >
              <el-icon>
                <Delete/>
              </el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="refresh" :loading="loading" plain>
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
            :data="aria2DownloadTaskPageList"
            v-loading="loading"
            border
            stripe
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" align="center"/>

          <el-table-column
              label="媒体名称"
              prop="name"
              show-overflow-tooltip
              align="center"
          >
            <template #default="{ row }">
              <span class="media-title">{{ row.name }}</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="gid"
              label="任务ID"
              width="180"
              align="center"
          >
            <template #default="{ row }">
                  <span class="task-id" :title="row.gid">
                    {{ row.gid }}
                  </span>
            </template>
          </el-table-column>

          <el-table-column
              prop="episodeNumber"
              label="集数"
              width="80"
              align="center"
          >
            <template #default="{ row }">
              <span class="episode-number">第{{ row.episodeNumber }}集</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="outName"
              label="文件路径"
              min-width="300"
              show-overflow-tooltip
          >
            <template #default="{ row }">
              <div class="file-path">
                <span class="save-path">{{ row.savePath }}</span>
                <span class="path-separator">/</span>
                <span class="file-name">{{ row.outName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column
              prop="shortName"
              label="分辨率"
              width="120"
              align="center"
          ></el-table-column>

          <el-table-column
              label="文件大小"
              width="120"
              align="center"
          >
            <template #default="{ row }">
              <span class="monospace-text">{{ formatFileSize(row.size) }}</span>
            </template>
          </el-table-column>

          <el-table-column
              label="状态"
              width="100"
              align="center"
          >
            <template #default="{ row }">
              <el-tag
                  :type="getStatusType(row.status)"
                  class="status-tag"
              >
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
              label="操作"
              width="360"
              align="center"
              fixed="right"
          >
            <template #default="{ row }">
              <div class="operation-buttons">
                <el-button
                    :disabled="row.status !== 1"
                    @click="handlePause(row)"
                    type="warning"
                    text
                >
                  <el-icon>
                    <VideoPause/>
                  </el-icon>
                  暂停
                </el-button>
                <el-divider direction="vertical"/>
                <el-button
                    :disabled="row.status !== 3"
                    @click="handleRetry(row)"
                    type="primary"
                    text
                >
                  <el-icon>
                    <RefreshRight/>
                  </el-icon>
                  重试
                </el-button>
                <el-divider direction="vertical"/>
                <el-button
                    @click="handleDelete(row.id)"
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
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="queryParams.page"
              v-model:page-size="queryParams.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import {defineComponent, ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Refresh, Search, Delete, VideoPause, RefreshRight} from '@element-plus/icons-vue'
import {Aria2DownloadTaskPage, DownloadTask} from '@/types'
import {aria2DownloadTaskApi} from "@/api/views/aria2_download_task";

export default defineComponent({
  name: 'Aria2DownloadTask',
  components: {
    Refresh,
    Search,
    Delete,
    VideoPause,
    RefreshRight
  },
  setup() {
    const aria2DownloadTaskPageList = ref<Aria2DownloadTaskPage[]>([])
    const loading = ref(false)
    const total = ref(0)
    const selectedTasks = ref<Aria2DownloadTaskPage[]>([])

    // 查询参数
    const queryParams = ref({
      page: 1,
      pageSize: 10,
      name: '',
      status: undefined as number | undefined
    })

    // 修改状态相关的类型定义
    type TaskStatus = 0 | 1 | 2 | 3

    const getStatusText = (status: TaskStatus) => {
      const statusMap: Record<TaskStatus, string> = {
        0: '等待中',
        1: '下载中',
        2: '已完成',
        3: '失败'
      }
      return statusMap[status] || '未知'
    }

    const getStatusType = (status: TaskStatus) => {
      const typeMap: Record<TaskStatus, string> = {
        0: 'info',
        1: 'warning',
        2: 'success',
        3: 'danger'
      }
      return typeMap[status] || 'info'
    }

    const formatFileSize = (bytes: number) => {
      if (!bytes) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB', 'TB']
      let size = bytes
      let unitIndex = 0

      while (size >= 1024 && unitIndex < units.length - 1) {
        size /= 1024
        unitIndex++
      }

      return `${size.toFixed(1)} ${units[unitIndex]}`
    }

    // 获取分页列表
    const loadDownloads = async () => {
      loading.value = true
      try {
        const response = await aria2DownloadTaskApi.getTasks({
          page: queryParams.value.page,
          pageSize: queryParams.value.pageSize,
          name: queryParams.value.name || "",
          status: queryParams.value.status
        })
        if (response.data.code === 200) {
          aria2DownloadTaskPageList.value = response.data.data.list
          total.value = response.data.data.total
        } else {
          ElMessage.error(response.data.message || '加载下载任务列表失败')
        }
      } catch (error) {
        console.error('加载失败:', error)
        ElMessage.error('加载下载任务列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      queryParams.value.page = 1
      loadDownloads()
    }

    const handleSizeChange = (val: number) => {
      queryParams.value.pageSize = val
      loadDownloads()
    }

    const handleCurrentChange = (val: number) => {
      queryParams.value.page = val
      loadDownloads()
    }

    const handleRetry = async (task: DownloadTask) => {
      try {
        const response = await aria2DownloadTaskApi.updateTask(task.id, {status: 0})
        if (response.data.code === 200) {
          ElMessage.success('重试任务已添加')
          await loadDownloads()
        } else {
          ElMessage.error(response.data.message || '重试失败')
        }
      } catch (error) {
        console.error('重试失败:', error)
        ElMessage.error('重试失败')
      }
    }

    const handleDelete = async (id: string) => {
      try {
        const response = await aria2DownloadTaskApi.deleteTask(id)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          await loadDownloads()
        } else {
          ElMessage.error(response.data.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }

    const handleSelectionChange = (selection: Aria2DownloadTaskPage[]) => {
      selectedTasks.value = selection
    }

    const handleBatchDelete = async () => {
      if (!selectedTasks.value.length) return

      try {
        await ElMessageBox.confirm(
            `确定要删除选中的 ${selectedTasks.value.length} 个任务吗？`,
            '批量删除',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
        )

        const deletePromises = selectedTasks.value.map((task: Aria2DownloadTaskPage) =>
            aria2DownloadTaskApi.deleteTask(task.id)
        )

        await Promise.all(deletePromises)
        ElMessage.success('批量删除成功')
        await loadDownloads()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除失败:', error)
          ElMessage.error('批量删除失败')
        }
      }
    }

    const handlePause = (task: DownloadTask) => {
      // 暂停
      console.log(task)
    }

    onMounted(() => {
      loadDownloads()
    })

    return {
      aria2DownloadTaskPageList,
      loading,
      total,
      queryParams,
      selectedTasks,
      getStatusText,
      getStatusType,
      formatFileSize,
      handleRetry,
      handleDelete,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      handleBatchDelete,
      refresh: loadDownloads,
      handlePause
    }
  }
})
</script>

<style scoped>

.save-path {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  padding-left: 8px;
  text-align: left;
}

.path-separator {
  color: var(--el-text-color-placeholder);
  padding: 0; /* 移除内边距 */
}

.file-name {
  color: var(--el-text-color-primary);
  font-weight: 500;
}

/* 搜索框样式 */
.search-input {
  width: 200px;
  margin-right: 12px;
}

/* 状态选择器样式 */
.status-select {
  width: 120px;
  margin-right: 12px;
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

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-color-primary);
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

/* 操作按钮组样式 */
.operation-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px; /* 增加按钮之间的间距 */
  min-width: 340px; /* 确保有足够的最小宽度 */
}

/* 表格容器 */
.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-top: 0; /* 移除表格顶部间距 */
}

/* 表格相关样式 */
.table-container .operation-buttons {
  display: flex;
  align-items: center;
  justify-content: flex-end; /* 改为靠右对齐 */
  gap: 8px;
  padding: 0 4px;
  width: 100%;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 0 20px;
}

</style>