<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">影视管理</span>
          <div class="operation-buttons">
            <el-button type="primary" @click="handleAddMedia" plain>
              <el-icon>
                <Plus/>
              </el-icon>
              添加影视
            </el-button>
            <el-button type="success" @click="refreshMediaList" plain>
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
            v-loading="loading"
            :data="mediaList"
            class="data-table"
            border
            stripe
        >
          <el-table-column
              prop="name"
              label="媒体名称"
              align="center">
          </el-table-column>
          <el-table-column
              prop="type"
              label="媒体类型"
              width="100"
              align="center">
          </el-table-column>
          <el-table-column
              prop="typeAlias"
              label="媒体类别"
              width="100"
              align="center"
          ></el-table-column>
          <el-table-column
              prop="seasonNumber"
              label="季数"
              width="100"
              align="center"
          ></el-table-column>
          <el-table-column
              prop="totalEpisode"
              label="总集数"
              width="100"
              align="center"
          ></el-table-column>

          <el-table-column prop="releaseDate" label="首播日期" align="center">
            <template #default="{ row }">
              {{ formatReleaseDate(row.releaseDate) }}
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
                <el-button @click="handleEdit(row)" type="primary" text>
                  <el-icon>
                    <Edit/>
                  </el-icon>
                  编辑
                </el-button>
                <el-divider direction="vertical"/>
                <el-button @click="manageShares(row)" type="warning" text>
                  <el-icon>
                    <Share/>
                  </el-icon>
                  分享
                </el-button>
                <el-divider direction="vertical"/>
                <el-button @click="viewDownloadTasks(row)" type="success" text>
                  <el-icon>
                    <Download/>
                  </el-icon>
                  下载任务
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 添加影视对话框 -->
      <el-dialog
          :title="dialogTitle"
          v-model="addMediaDialogVisible"
          width="800px"
          :close-on-click-modal="false"
          destroy-on-close
          @close="handleClose"
          align-center
      >
        <el-form
            :model="newMedia"
            ref="form"
            label-width="100px"
            class="add-media-form"
        >
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item
                  label="媒体名称"
                  prop="name"
                  :rules="[{ required: true, message: '请输入标题', trigger: 'blur' }]"
              >
                <el-input
                    v-model="newMedia.name"
                    :disabled="isEdit"
                    placeholder="请输入影视标题"
                    clearable
                ></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item
                  label="媒体类型"
                  prop="type"
                  :rules="[{ required: true, message: '请选择类型', trigger: 'change' }]"
              >
                <el-select
                    v-model="newMedia.type"
                    :disabled="isEdit"
                    placeholder="请选择类型"
                    class="full-width"
                >
                  <el-option
                      v-for="option in mediaTypes"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                  >
                    <el-icon class="option-icon">
                      <component :is="option.icon"/>
                    </el-icon>
                    {{ option.label }}
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="类型别名">
                <el-input
                    v-model="newMedia.typeAlias"
                    placeholder="请输入类型别名"
                    clearable
                ></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="季数">
                <el-input-number
                    v-model="newMedia.seasonNumber"
                    :disabled="isEdit"
                    :min="0"
                    :precision="0"
                    class="full-width"
                ></el-input-number>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="总集数">
                <el-input-number
                    v-model="newMedia.totalEpisode"
                    :min="0"
                    :precision="0"
                    class="full-width"
                ></el-input-number>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="开始集数">
                <el-input-number
                    v-model="newMedia.startEpisode"
                    :min="0"
                    :precision="0"
                    class="full-width"
                ></el-input-number>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="首播日期">
                <el-date-picker
                    v-model="newMedia.releaseDate"
                    type="date"
                    placeholder="请选择首播日期"
                    class="full-width"
                    value-format="x"
                ></el-date-picker>
              </el-form-item>
            </el-col>

          </el-row>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handleClose">取 消</el-button>
            <el-button type="primary" @click="submitForm">确 定</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 分享管理对话框 -->
      <el-dialog
          title="分享管理"
          v-model="shareDialogVisible"
          width="1200px"
          :close-on-click-modal="false"
          class="share-dialog"
          destroy-on-close
          align-center
      >
        <div class="share-header">
          <span class="share-title">分享列表</span>
          <el-button type="primary" @click="handleAddShare" plain>
            <el-icon>
              <Plus/>
            </el-icon>
            添加分享
          </el-button>
        </div>

        <el-table
            :data="shareList"
            border
            stripe
            class="share-table"
            v-loading="shareLoading"
        >
          <el-table-column
              label="网盘类型"
              prop="provider"
              width="150"
              align="center"
          >
            <template #default="{ row }">
              <el-tag
                  :type="getProviderTagType(row.provider)"
                  effect="plain"
                  class="provider-tag"
              >
                {{ getProviderName(row.provider) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
              prop="shareId"
              label="分享链接"
              min-width="300"
              show-overflow-tooltip
          ></el-table-column>

          <el-table-column
              prop="shareCode"
              label="提取码"
              width="100"
              align="center"
          ></el-table-column>

          <el-table-column
              prop="expiredAt"
              label="过期时间"
              width="180"
              align="center"
          >
            <template #default="{ row }">
              <span :class="{ 'expired': isExpired(row.expiredAt) }">
                {{ formatExpiredTime(row.expiredAt) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column
              prop="fileRegex"
              label="文件匹配规则"
              min-width="200"
              show-overflow-tooltip
          ></el-table-column>

          <el-table-column
              prop="episodeRegex"
              label="匹配集数规则"
              min-width="200"
              show-overflow-tooltip
          ></el-table-column>

          <el-table-column
              prop="onlyInDir"
              label="有效目录"
              min-width="200"
              show-overflow-tooltip
          >
            <template #default="{ row }">
              <span class="only-in-dir">{{ row.onlyInDir || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="excludedDir"
              label="排除目录"
              min-width="200"
              show-overflow-tooltip
          >
            <template #default="{ row }">
              <el-tooltip
                  v-if="row.excludedDir && row.excludedDir.length"
                  :content="row.excludedDir.join('\n')"
                  placement="top"
                  effect="dark"
              >
                <span class="excluded-dir">{{ row.excludedDir.join(', ') }}</span>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="isLapse"
              label="是否失效"
              width="100"
              align="center"
          >
            <template #default="{ row }">
              <el-tag
                  :type="row.isLapse === 1 ? 'danger' : 'success'"
                  effect="plain"
              >
                {{ row.isLapse === 1 ? '已失效' : '有效' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
              prop="lapseCause"
              label="失效原因"
              min-width="150"
              show-overflow-tooltip
          >
            <template #default="{ row }">
              <span :class="{ 'lapse-cause': row.isLapse === 1 }">
                {{ row.lapseCause || '-' }}
              </span>
            </template>
          </el-table-column>

          <el-table-column
              label="操作"
              width="180"
              align="center"
              fixed="right"
          >
            <template #default="{ row }">
              <div class="share-operation">
                <el-button
                    @click="handleEditShare(row)"
                    type="primary"
                    text
                    class="edit-btn"
                >
                  <el-icon>
                    <Edit/>
                  </el-icon>
                  编辑
                </el-button>
                <el-divider direction="vertical"/>
                <el-button
                    @click="deleteShare(row.id)"
                    type="danger"
                    text
                    class="delete-btn"
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
      </el-dialog>

      <!-- 分享连接 添加或编辑 -->
      <el-dialog
          v-model="addShareDialogVisible"
          :title="isEditShare ? '编辑分享' : '添加分享'"
          width="500px"
          append-to-body
          :close-on-click-modal="false"
          destroy-on-close
          align-center
      >
        <el-form
            :model="newShare"
            ref="shareFormRef"
            label-width="100px"
            :rules="shareFormRules"
        >
          <el-form-item
              label="网盘类型"
              prop="provider"
              :rules="shareFormRules.provider"
          >
            <el-select v-model="newShare.provider" placeholder="请选择网盘类型">
              <el-option label="夸克" :value="1"></el-option>
              <el-option label="阿里云盘" :value="2"></el-option>
              <el-option label="百度网盘" :value="3"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item
              label="分享链接"
              prop="shareId"
              :rules="shareFormRules.shareId"
          >
            <el-input v-model="newShare.shareId" placeholder="请输入分享链接"></el-input>
          </el-form-item>

          <el-form-item label="提取码" prop="shareCode">
            <el-input v-model="newShare.shareCode" placeholder="请输入提取码（如有）"></el-input>
          </el-form-item>

          <el-form-item label="过期时间" prop="expiredAt">
            <el-date-picker
                v-model="newShare.expiredAt"
                type="datetime"
                placeholder="请选择过期时间（可选）"
                value-format="YYYY-MM-DD HH:mm:ss"
            ></el-date-picker>
          </el-form-item>

          <el-form-item
              label="文件匹配规则"
              prop="fileRegex"
          >
            <el-input
                v-model="newShare.fileRegex"
                placeholder="请输入文件匹配规则"
            >
              <template #append>
                <el-tooltip content="用于匹配分享链接中的文件，例如：.*\.mp4" placement="top">
                  <el-icon>
                    <question-filled/>
                  </el-icon>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item
              label="集数匹配规则"
              prop="episodeRegex"
              :rules="shareFormRules.episodeRegex"
          >
            <el-input
                v-model="newShare.episodeRegex"
                placeholder="请输入匹配集数规则"
            >
              <template #append>
                <el-tooltip content="用于匹配集数规则（用于匹配出重命名后文件名），例如：01" placement="top">
                  <el-icon>
                    <question-filled/>
                  </el-icon>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item
              label="有效目录"
              prop="onlyInDir"
          >
            <el-input
                v-model="newShare.onlyInDir"
                placeholder="请输入有效目录（可选）"
            >
              <template #append>
                <el-tooltip content="指定在哪个目录下搜索文件，留空则搜索整个分享" placement="top">
                  <el-icon>
                    <question-filled/>
                  </el-icon>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item
              label="排除目录"
              prop="excludedDir"
          >
            <el-input
                v-model="excludedDirInput"
                placeholder="请输入要排除的目录，按回车添加"
                @keyup.enter="handleAddExcludedDir"
                clearable
            >
              <template #append>
                <el-tooltip content="输入目录路径后按回车添加，可添加多个" placement="top">
                  <el-icon>
                    <question-filled/>
                  </el-icon>
                </el-tooltip>
              </template>
            </el-input>
            <div class="excluded-dir-tags" v-if="newShare.excludedDir?.length">
              <el-tag
                  v-for="(dir, index) in newShare.excludedDir"
                  :key="index"
                  closable
                  @close="handleRemoveExcludedDir(index)"
                  class="excluded-dir-tag"
              >
                {{ dir }}
              </el-tag>
            </div>
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="addShareDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="submitShare">确 定</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 下载任务对话框 -->
      <el-dialog
          v-model="downloadTasksDialogVisible"
          :title="`${currentMedia?.name || ''} - 下载任务`"
          width="900px"
          :close-on-click-modal="false"
          class="download-tasks-dialog"
          align-center
      >
        <div class="tasks-content">
          <div class="tasks-overview">
            <el-row :gutter="12">
              <el-col :span="24">
                <el-card shadow="hover" class="status-card total">
                  <div class="status-content">
                    <div class="status-label">总任务数</div>
                    <div class="status-value">{{ taskStats.total }}</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="12" style="margin-top: 12px;">
              <el-col :span="6">
                <el-card shadow="hover" class="status-card waiting">
                  <div class="status-content">
                    <div class="status-label">等待中</div>
                    <div class="status-value">{{ taskStats.waiting }}</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="status-card downloading">
                  <div class="status-content">
                    <div class="status-label">下载中</div>
                    <div class="status-value">{{ taskStats.downloading }}</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="status-card success">
                  <div class="status-content">
                    <div class="status-label">已完成</div>
                    <div class="status-value">{{ taskStats.completed }}</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="status-card error">
                  <div class="status-content">
                    <div class="status-label">已失败</div>
                    <div class="status-value">{{ taskStats.failed }}</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <div class="tasks-table-wrapper">
            <el-table
                :data="downloadTasks"
                border
                stripe
                class="download-tasks-table"
                style="margin-top: 20px;"
            >
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
                  max-width="80"
                  align="center"
              >
              </el-table-column>

              <el-table-column
                  prop="size"
                  label="大小"
                  width="100"
                  align="right"
              >
                <template #default="{ row }">
                  <span class="file-size">{{ formatFileSize(row.size) }}</span>
                </template>
              </el-table-column>

              <el-table-column
                  prop="status"
                  label="状态"
                  width="100"
                  align="center"
              >
                <template #default="{ row }">
                  <el-tag
                      :type="getStatusType(row.status)"
                      class="status-tag"
                      :effect="row.status === 1 ? 'dark' : 'plain'"
                  >
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script lang="ts">
import {computed, defineComponent, onMounted, ref} from 'vue'
import {dayjs, ElMessage} from 'element-plus'
import {Delete, Download, Edit, Film, Plus, QuestionFilled, Refresh, Share, VideoCamera} from '@element-plus/icons-vue'
import type {CloudShare, DownloadTask, Media} from '@/types'
import {mediaApi} from "@/api/views/media";
import {cloudShareApi} from "@/api/views/cloud_share";

export default defineComponent({
  name: 'Media',
  components: {
    VideoCamera,
    Film,
    Plus,
    Refresh,
    Edit,
    Share,
    Download,
    QuestionFilled,
    Delete,
  },
  setup() {
    const mediaList = ref<Media[]>([])
    const shareList = ref<CloudShare[]>([])
    const loading = ref(true)
    const shareLoading = ref(false)
    const shareDialogVisible = ref(false)
    const addMediaDialogVisible = ref(false)
    const addShareDialogVisible = ref(false)
    const currentMediaId = ref<string>()
    const newMedia = ref<Partial<Media>>({
      name: '',
      type: '',
      typeAlias: '',
      seasonNumber: 0,
      totalEpisode: 0,
      startEpisode: null,
      releaseDate: '',
      tmdbId: ''
    })
    const newShare = ref<Partial<CloudShare>>({
      provider: undefined,
      shareId: '',
      shareCode: '',
      expiredAt: '',
      fileRegex: '',
      onlyInDir: '',
      excludedDir: [],
      isLapse: 0,
      lapseCause: ''
    })
    const downloadTasksDialogVisible = ref(false)
    const downloadTasks = ref<DownloadTask[]>([])
    const currentMedia = ref<Media | null>(null)

    const mediaTypes = [
      {label: 'movie', value: 'movie', icon: 'Film'},
      {label: 'tv', value: 'tv', icon: 'VideoCamera'}
    ]

    const getProviderName = (provider: number) => {
      const providers: Record<number, string> = {
        1: '夸克',
        2: '阿里云盘',
        3: '百度网盘'
      }
      return providers[provider] || '未知'
    }

    const getProviderTagType = (provider: number) => {
      const types: Record<number, string> = {
        1: 'success',  // 夸克
        2: 'primary',  // 阿里云盘
        3: 'warning'   // 百度网盘
      }
      return types[provider] || 'info'
    }

    interface MediaResponse {
      id: number
      name: string
      type: string
      typeAlias?: string
      seasonNumber: number
      totalEpisode: number
      startEpisode: number | null
      releaseDate?: string
      tmdbId?: string
    }

    const loadMediaList = async () => {
      try {
        const response = await mediaApi.getList()
        if (response.data.code === 200) {
          mediaList.value = response.data.data.map((item: MediaResponse) => ({
            id: item.id,
            name: item.name,
            type: item.type,
            typeAlias: item.typeAlias,
            seasonNumber: item.seasonNumber,
            totalEpisode: item.totalEpisode,
            startEpisode: item.startEpisode,
            releaseDate: item.releaseDate,
            tmdbId: item.tmdbId
          }))
        } else {
          ElMessage.error('加载影视列表失败: ' + (response.data.message || '未知错误'))
        }
      } catch (error) {
        ElMessage.error('加载影视列表失败')
      }
    }

    onMounted(async () => {
      loading.value = true
      try {
        await loadMediaList()
      } finally {
        loading.value = false
      }
    })

    const refreshMediaList = async () => {
      if (loading.value) return
      loading.value = true

      await new Promise<void>(resolve => {
        requestAnimationFrame(async () => {
          await loadMediaList()
          requestAnimationFrame(() => {
            loading.value = false
            resolve()
          })
        })
      })
    }

    const isEdit = ref(false)
    const editingId = ref<string>()

    const dialogTitle = computed(() => isEdit.value ? '编辑影视' : '添加影视')

    const resetForm = () => {
      newMedia.value = {
        name: '',
        type: '',
        typeAlias: '',
        seasonNumber: 0,
        totalEpisode: 0,
        startEpisode: null,
        releaseDate: '',
        tmdbId: ''
      }
      isEdit.value = false
      editingId.value = undefined
    }

    const handleEdit = (row: Media) => {
      // 设置所有字段，但部分字段设为只读
      newMedia.value = {
        ...row,
        name: row.name,
        type: row.type,
        typeAlias: row.typeAlias,
        seasonNumber: row.seasonNumber,
        totalEpisode: row.totalEpisode,
        startEpisode: row.startEpisode,
        releaseDate: row.releaseDate,
        tmdbId: row.tmdbId
      }

      editingId.value = row.id
      isEdit.value = true
      addMediaDialogVisible.value = true
    }

    const submitForm = async () => {
      try {
        let response
        // 编辑时只提交允许修改的字段
        const submitData = isEdit.value ? {
          typeAlias: newMedia.value.typeAlias || '',
          totalEpisode: newMedia.value.totalEpisode || 0,
          releaseDate: newMedia.value.releaseDate,
          startEpisode: newMedia.value.startEpisode === undefined ? null : newMedia.value.startEpisode
        } : {
          // 新增时提交所有字段
          ...newMedia.value,
        }

        if (isEdit.value && editingId.value) {
          response = await mediaApi.updateMedia(editingId.value, submitData)
        } else {
          response = await mediaApi.addMedia(submitData)
        }

        if (response.data.code === 200) {
          ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
          addMediaDialogVisible.value = false
          await loadMediaList()
          resetForm()
        } else {
          ElMessage.error(response.data.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleClose = () => {
      resetForm()
      addMediaDialogVisible.value = false
    }

    const manageShares = async (media: Media) => {
      if (!media || !media.id) return

      if (currentMediaId.value !== media.id) {
        currentMediaId.value = media.id
        shareLoading.value = true
        try {
          const response = await cloudShareApi.getShares(media.id)
          if (response.data.code === 200) {
            shareList.value = response.data.data || []
          } else {
            ElMessage.error('加载分享列表失败: ' + (response.data.message || '未知错误'))
            shareList.value = []
          }
        } catch (error) {
          ElMessage.error('加载分享列表失败')
          shareList.value = []
        } finally {
          shareLoading.value = false
        }
      }

      shareDialogVisible.value = true
    }

    const isEditShare = ref(false)
    const editingShareId = ref<number>()

    const handleEditShare = (row: CloudShare) => {
      isEditShare.value = true
      editingShareId.value = row.id
      addShareDialogVisible.value = true
      newShare.value = {
        provider: row.provider,
        shareId: row.shareId,
        shareCode: row.shareCode,
        expiredAt: row.expiredAt ? dayjs(row.expiredAt).format('YYYY-MM-DD HH:mm:ss') : '',
        fileRegex: row.fileRegex,
        onlyInDir: row.onlyInDir,
        episodeRegex: row.episodeRegex,
        excludedDir: row.excludedDir || [],
        isLapse: row.isLapse || 0,
        lapseCause: row.lapseCause || ''
      }
    }

    const handleAddShare = () => {
      isEditShare.value = false
      editingShareId.value = undefined
      addShareDialogVisible.value = true
      newShare.value = {
        provider: undefined,
        shareId: '',
        shareCode: '',
        expiredAt: '',
        fileRegex: '',
        onlyInDir: '',
        excludedDir: [],
        isLapse: 0,
        lapseCause: ''
      }
    }

    const submitShare = async () => {
      if (!shareFormRef.value) return

      try {
        // 校验表单
        await shareFormRef.value.validate()

        if (!currentMediaId.value) {
          ElMessage.error('未找到媒体ID')
          return
        }

        let response
        if (isEditShare.value && editingShareId.value) {
          response = await cloudShareApi.updateShare(editingShareId.value, {
            ...newShare.value,
            mediaId: currentMediaId.value
          })
        } else {
          response = await cloudShareApi.addShare({
            ...newShare.value,
            mediaId: currentMediaId.value
          })
        }

        if (response.data.code === 200) {
          ElMessage.success(isEditShare.value ? '编辑成功' : '添加成功')
          addShareDialogVisible.value = false
          await manageShares(mediaList.value.find(m => m.id === currentMediaId.value) as Media)
        } else {
          ElMessage.error(response.data.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const deleteShare = async (id: number) => {
      try {
        const response = await cloudShareApi.deleteShare(id)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          if (currentMediaId.value) {
            await manageShares(mediaList.value.find(m => m.id === currentMediaId.value) as Media)
          }
        } else {
          ElMessage.error(response.data.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    }

    const taskStats = computed(() => {
      const total = downloadTasks.value.length
      const waiting = downloadTasks.value.filter(t => t.status === 0).length
      const downloading = downloadTasks.value.filter(t => t.status === 1).length
      const completed = downloadTasks.value.filter(t => t.status === 2).length
      const failed = downloadTasks.value.filter(t => t.status === 3).length
      return {total, waiting, downloading, completed, failed}
    })

    const getStatusIcon = (status: number) => {
      const icons = {
        0: 'Clock',        // 等待中
        1: 'Loading',      // 下载中
        2: 'CircleCheck',  // 已完成
        3: 'CircleClose'   // 失败
      }
      return icons[status as keyof typeof icons] || 'More'
    }

    const viewDownloadTasks = async (media: Media) => {
      currentMedia.value = media
      downloadTasksDialogVisible.value = true
      try {
        const response = await fetch(`/api/download/task/${media.id}`)
        const data = await response.json()
        if (data.code === 200) {
          downloadTasks.value = data.data
        } else {
          ElMessage.error(data.message || '获取下载任务失败')
        }
      } catch (error) {
        ElMessage.error('获取下载任务失败')
      }
    }

    const getStatusType = (status: number) => {
      const types: Record<number, string> = {
        0: 'info',     // 等待中
        1: 'primary',  // 下载中
        2: 'success',  // 已完成
        3: 'danger'    // 失败
      }
      return types[status] || 'info'
    }

    const getStatusText = (status: number) => {
      const texts: Record<number, string> = {
        0: '等待中',
        1: '下载中',
        2: '已完成',
        3: '已失败'
      }
      return texts[status] || '未知'
    }

    // 格式化 update days
    const formatUpdateDays = (days: number[]) => {
      if (!days || (Array.isArray(days) && days.length === 0)) {
        return '-'
      }

      const weekdays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

      return days
          .sort((a, b) => a - b)  // 确保按顺序显示
          .map(day => `<span class="weekday-tag">${weekdays[day - 1]}</span>`)
          .join('')
    }

    const formatExpiredTime = (expiredAt: string) => {
      if (!expiredAt) return '永久有效'
      return new Date(expiredAt).toLocaleString()
    }

    const isExpired = (expiredAt: string) => {
      if (!expiredAt) return false
      return new Date(expiredAt).getTime() < Date.now()
    }

    const shareFormRules = {
      provider: [
        {required: true, message: '请选择网盘类型', trigger: 'change'}
      ],
      shareId: [
        {required: true, message: '请输入分享链接', trigger: 'blur'}
      ],
      episodeRegex: [
        {required: true, message: '请输入文件匹配规则', trigger: 'blur'}
      ]
    }

    const shareFormRef = ref()

    const excludedDirInput = ref('')

    const handleAddExcludedDir = () => {
      const dir = excludedDirInput.value.trim()
      if (dir && !newShare.value.excludedDir?.includes(dir)) {
        newShare.value.excludedDir = [...(newShare.value.excludedDir || []), dir]
        excludedDirInput.value = ''
      }
    }

    const handleRemoveExcludedDir = (index: number) => {
      newShare.value.excludedDir?.splice(index, 1)
    }

    const formatReleaseDate = (date: string) => {
      if (!date) return '-'
      return dayjs(date).format("YYYY-MM-DD")
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

    return {
      mediaList,
      shareList,
      loading,
      shareLoading,
      shareDialogVisible,
      addMediaDialogVisible,
      addShareDialogVisible,
      newMedia,
      newShare,
      getProviderName,
      getProviderTagType,
      handleAddMedia: () => {
        isEdit.value = false
        resetForm()
        addMediaDialogVisible.value = true
      },
      handleEdit,
      manageShares,
      handleAddShare,
      deleteShare,
      refreshMediaList,
      mediaTypes,
      formatUpdateDays,
      Plus,
      Refresh,
      Edit,
      Share,
      Download,
      QuestionFilled,
      dialogTitle,
      handleClose,
      submitForm,
      taskStats,
      viewDownloadTasks,
      downloadTasksDialogVisible,
      downloadTasks,
      currentMedia,
      getStatusType,
      getStatusText,
      getStatusIcon,
      formatExpiredTime,
      isExpired,
      shareFormRules,
      shareFormRef,
      submitShare,
      isEdit,
      editingId,
      formatReleaseDate,
      excludedDirInput,
      handleAddExcludedDir,
      handleRemoveExcludedDir,
      isEditShare,
      editingShareId,
      handleEditShare,
      formatFileSize
    }
  }
})
</script>

<style scoped>
/* 表格容器 */
.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-top: 0; /* 移除表格顶部间距 */
}

/* 主页表格样式 */
.data-table {
  width: 100%;
  flex: 1;
  overflow: hidden;
}

/* 分享管理样式 */
.share-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.share-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.share-operation {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

:deep(.delete-btn) {
  height: 32px;
  min-width: 80px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

/* 任务 ID 样式 */
.task-id {
  font-family: monospace;
  color: var(--el-text-color-secondary);
  cursor: default;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 160px;
  display: inline-block;
}

/* 存储路径列样式 */
.save-path {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  padding-left: 8px;
  display: block;
  text-align: left;
}

/* 状态标签样式 */
.status-tag {
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
  font-size: 13px;
  min-width: 70px;
}

/* 任务 ID 样式 */
.task-id {
  font-family: monospace;
  color: var(--el-text-color-secondary);
  cursor: default;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 160px;
  display: inline-block;
}

/* 集数样式 */
.episode-number {
  font-size: 13px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}

/* 任务内容区域样式 */
.tasks-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 任务概览区域 */
.tasks-overview {
  flex-shrink: 0;
  margin-bottom: 2px; /* 最小化底部间距到2px */
}

/* 表格包装器样式 */
.tasks-table-wrapper {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  margin-top: 2px; /* 最小化顶部间距到2px */
}

/* 下载任务表格样式 */
.download-tasks-table {
  height: 440px; /* 增加表格高度 */
  overflow: auto; /* 确保内容可以滚动 */
  width: 100% !important;
}

/* 统计卡片样式优化 */
.status-card {
  text-align: center;
  transition: all 0.3s;
  margin: 0;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: #fff;
  height: auto; /* 修改: 将固定高度改为自动 */
  min-height: 80px; /* 修改: 增加最小高度 */

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }

  /* 总任务数卡片样式 */

  &.total {
    background: #f0f2ff;

    .status-content {
      padding: 16px; /* 修改: 增加内边距 */
    }

    .status-label {
      color: #666;
    }

    .status-value {
      color: #4a00e0;
      font-size: 24px; /* 减小字号 */
    }
  }

  /* 等待中卡片样式 */

  &.waiting {
    background: #f5f7fa;

    .status-label {
      color: #666;
    }

    .status-value {
      color: #909399;
    }
  }

  /* 下载中卡片样式 */

  &.downloading {
    background: #ecf5ff;

    .status-label {
      color: #666;
    }

    .status-value {
      color: #409eff;
    }
  }

  /* 已完成卡片样式 */

  &.success {
    background: #f0f9eb;

    .status-label {
      color: #666;
    }

    .status-value {
      color: #67c23a;
    }
  }

  /* 失败卡片样式 */

  &.error {
    background: #fef0f0;

    .status-label {
      color: #666;
    }

    .status-value {
      color: #f56c6c;
    }
  }
}

.status-content {
  padding: 16px; /* 修改: 增加内边距 */
  display: flex;
  flex-direction: column;
  gap: 8px; /* 增加间距 */
  align-items: center;
  height: 100%;
  justify-content: center;
}

.status-label {
  font-size: 13px; /* 减小字号 */
  font-weight: 500;
  letter-spacing: 1px;
}

.status-value {
  font-size: 20px; /* 减小字号 */
  font-weight: 600;
  line-height: 1;
}

/* 移除所有滚动条相关样式 */
:deep(*::-webkit-scrollbar) {
  display: none !important;
}

* {
  -ms-overflow-style: none !important; /* IE and Edge */
  scrollbar-width: none !important; /* Firefox */
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  white-space: nowrap;
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

/* 头部按钮组样式 */
.card-header > .operation-buttons {
  display: flex;
  align-items: center;
  justify-content: flex-end; /* 改为靠右对齐 */
  gap: 12px;
  margin-left: auto; /* 确保靠右 */
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

/* 修改表格样式以保持一致性 */
.data-table {
  width: 100%;
  flex: 1;
  overflow: hidden;
}

/* 分享对话框表格样式 */
.share-table {
  width: 100% !important;
  overflow: hidden !important;
}

/* 操作按钮组样式 */
.operation-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px; /* 增加按钮之间的间距 */
  min-width: 340px; /* 确保有足够的最小宽度 */
}

/* 表单布局样式 */
.add-media-form {
  padding: 20px 40px; /* 增加内外边距 */
}

/* 对话框底部按钮样式 */
.dialog-footer {
  padding-top: 10px; /* 增加顶部间距 */
  text-align: right;
}

/* 更新日标签样式 */
:deep(.weekday-tag) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
  height: 24px;
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  line-height: 1;
  white-space: nowrap;
}

/* 表格单元格样式调整 */

.file-name {
  color: var(--el-text-color-primary);
  font-weight: 500;
}

.file-size {
  font-family: monospace;
  font-size: 13px;
  color: var(--el-text-color-regular);
  padding-right: 8px;
}

/* 文件路径样式 */
.file-path {
  display: flex;
  align-items: center;
  gap: 0; /* 移除间距 */
  font-family: monospace;
  font-size: 13px;
  text-align: left;
  padding: 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.path-separator {
  color: var(--el-text-color-placeholder);
  padding: 0; /* 移除内边距 */
}

.only-in-dir {
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.excluded-dir {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.lapse-cause {
  color: var(--el-color-danger);
  font-size: 13px;
}

.expired {
  color: var(--el-color-danger);
}

.excluded-dir-tags {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.excluded-dir-tag {
  margin-right: 0;
}
</style> 