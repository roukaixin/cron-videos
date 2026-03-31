/// <reference types="../../node_modules/.vue-global-types/vue_3.5_0.d.ts" />
import { computed, onMounted, ref } from 'vue';
import { dayjs, ElMessage } from 'element-plus';
import { Delete, Download, Edit, Plus, QuestionFilled, Refresh, Share } from '@element-plus/icons-vue';
import { mediaApi } from "@/api/views/media";
import { cloudShareApi } from "@/api/views/cloud_share";
defineOptions({
    name: "Media"
});
const mediaList = ref([]);
const shareList = ref([]);
const loading = ref(true);
const shareLoading = ref(false);
const shareDialogVisible = ref(false);
const addMediaDialogVisible = ref(false);
const addShareDialogVisible = ref(false);
const currentMediaId = ref();
const newMedia = ref({
    name: '',
    type: '',
    typeAlias: '',
    seasonNumber: 0,
    totalEpisode: 0,
    startEpisode: null,
    releaseDate: '',
    tmdbId: ''
});
const newShare = ref({
    provider: undefined,
    shareId: '',
    shareCode: '',
    expiredAt: '',
    fileRegex: '',
    onlyInDir: '',
    excludedDir: [],
    isLapse: 0,
    lapseCause: ''
});
const downloadTasksDialogVisible = ref(false);
const downloadTasks = ref([]);
const currentMedia = ref(null);
const mediaTypes = [
    { label: 'movie', value: 'movie', icon: 'Film' },
    { label: 'tv', value: 'tv', icon: 'VideoCamera' }
];
const getProviderName = (provider) => {
    const providers = {
        1: '夸克',
        2: '阿里云盘',
        3: '百度网盘'
    };
    return providers[provider] || '未知';
};
const getProviderTagType = (provider) => {
    const types = {
        1: 'success', // 夸克
        2: 'primary', // 阿里云盘
        3: 'warning' // 百度网盘
    };
    return types[provider] || 'info';
};
const loadMediaList = async () => {
    try {
        const response = await mediaApi.getList();
        if (response.data.code === 200) {
            mediaList.value = response.data.data.map((item) => ({
                id: item.id,
                name: item.name,
                type: item.type,
                typeAlias: item.typeAlias,
                seasonNumber: item.seasonNumber,
                totalEpisode: item.totalEpisode,
                startEpisode: item.startEpisode,
                releaseDate: item.releaseDate,
                tmdbId: item.tmdbId
            }));
        }
        else {
            ElMessage.error('加载影视列表失败: ' + (response.data.message || '未知错误'));
        }
    }
    catch (error) {
        ElMessage.error('加载影视列表失败');
    }
};
onMounted(async () => {
    loading.value = true;
    try {
        await loadMediaList();
    }
    finally {
        loading.value = false;
    }
});
const refreshMediaList = async () => {
    if (loading.value)
        return;
    loading.value = true;
    await new Promise(resolve => {
        requestAnimationFrame(async () => {
            await loadMediaList();
            requestAnimationFrame(() => {
                loading.value = false;
                resolve();
            });
        });
    });
};
const isEdit = ref(false);
const editingId = ref();
const dialogTitle = computed(() => isEdit.value ? '编辑影视' : '添加影视');
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
    };
    isEdit.value = false;
    editingId.value = undefined;
};
const handleEdit = (row) => {
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
    };
    editingId.value = row.id;
    isEdit.value = true;
    addMediaDialogVisible.value = true;
};
const submitForm = async () => {
    try {
        let response;
        // 编辑时只提交允许修改的字段
        const submitData = isEdit.value ? {
            typeAlias: newMedia.value.typeAlias || '',
            totalEpisode: newMedia.value.totalEpisode || 0,
            releaseDate: newMedia.value.releaseDate,
            startEpisode: newMedia.value.startEpisode === undefined ? null : newMedia.value.startEpisode
        } : {
            // 新增时提交所有字段
            ...newMedia.value,
        };
        if (isEdit.value && editingId.value) {
            response = await mediaApi.updateMedia(editingId.value, submitData);
        }
        else {
            response = await mediaApi.addMedia(submitData);
        }
        if (response.data.code === 200) {
            ElMessage.success(isEdit.value ? '编辑成功' : '添加成功');
            addMediaDialogVisible.value = false;
            await loadMediaList();
            resetForm();
        }
        else {
            ElMessage.error(response.data.message || '操作失败');
        }
    }
    catch (error) {
        ElMessage.error('操作失败');
    }
};
const handleClose = () => {
    resetForm();
    addMediaDialogVisible.value = false;
};
const manageShares = async (media) => {
    if (!media || !media.id)
        return;
    if (currentMediaId.value !== media.id) {
        currentMediaId.value = media.id;
        shareLoading.value = true;
        try {
            const response = await cloudShareApi.getShares(media.id);
            if (response.data.code === 200) {
                shareList.value = response.data.data || [];
            }
            else {
                ElMessage.error('加载分享列表失败: ' + (response.data.message || '未知错误'));
                shareList.value = [];
            }
        }
        catch (error) {
            ElMessage.error('加载分享列表失败');
            shareList.value = [];
        }
        finally {
            shareLoading.value = false;
        }
    }
    shareDialogVisible.value = true;
};
const isEditShare = ref(false);
const editingShareId = ref();
const handleEditShare = (row) => {
    isEditShare.value = true;
    editingShareId.value = row.id;
    addShareDialogVisible.value = true;
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
    };
};
const handleAddShare = () => {
    isEditShare.value = false;
    editingShareId.value = undefined;
    addShareDialogVisible.value = true;
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
    };
};
const submitShare = async () => {
    if (!shareFormRef.value)
        return;
    try {
        // 校验表单
        await shareFormRef.value.validate();
        if (!currentMediaId.value) {
            ElMessage.error('未找到媒体ID');
            return;
        }
        let response;
        if (isEditShare.value && editingShareId.value) {
            response = await cloudShareApi.updateShare(editingShareId.value, {
                ...newShare.value,
                mediaId: currentMediaId.value
            });
        }
        else {
            response = await cloudShareApi.addShare({
                ...newShare.value,
                mediaId: currentMediaId.value
            });
        }
        if (response.data.code === 200) {
            ElMessage.success(isEditShare.value ? '编辑成功' : '添加成功');
            addShareDialogVisible.value = false;
            await manageShares(mediaList.value.find(m => m.id === currentMediaId.value));
        }
        else {
            ElMessage.error(response.data.message || '操作失败');
        }
    }
    catch (error) {
        ElMessage.error('操作失败');
    }
};
const deleteShare = async (id) => {
    try {
        const response = await cloudShareApi.deleteShare(id);
        if (response.data.code === 200) {
            ElMessage.success('删除成功');
            if (currentMediaId.value) {
                await manageShares(mediaList.value.find(m => m.id === currentMediaId.value));
            }
        }
        else {
            ElMessage.error(response.data.message || '删除失败');
        }
    }
    catch (error) {
        ElMessage.error('删除失败');
    }
};
const taskStats = computed(() => {
    const total = downloadTasks.value.length;
    const waiting = downloadTasks.value.filter(t => t.status === 0).length;
    const downloading = downloadTasks.value.filter(t => t.status === 1).length;
    const completed = downloadTasks.value.filter(t => t.status === 2).length;
    const failed = downloadTasks.value.filter(t => t.status === 3).length;
    return { total, waiting, downloading, completed, failed };
});
const viewDownloadTasks = async (media) => {
    currentMedia.value = media;
    downloadTasksDialogVisible.value = true;
    try {
        const response = await fetch(`/api/download/task/${media.id}`);
        const data = await response.json();
        if (data.code === 200) {
            downloadTasks.value = data.data;
        }
        else {
            ElMessage.error(data.message || '获取下载任务失败');
        }
    }
    catch (error) {
        ElMessage.error('获取下载任务失败');
    }
};
const getStatusType = (status) => {
    const types = {
        0: 'info', // 等待中
        1: 'primary', // 下载中
        2: 'success', // 已完成
        3: 'danger' // 失败
    };
    return types[status] || 'info';
};
const getStatusText = (status) => {
    const texts = {
        0: '等待中',
        1: '下载中',
        2: '已完成',
        3: '已失败'
    };
    return texts[status] || '未知';
};
const formatExpiredTime = (expiredAt) => {
    if (!expiredAt)
        return '永久有效';
    return new Date(expiredAt).toLocaleString();
};
const isExpired = (expiredAt) => {
    if (!expiredAt)
        return false;
    return new Date(expiredAt).getTime() < Date.now();
};
const shareFormRules = {
    provider: [
        { required: true, message: '请选择网盘类型', trigger: 'change' }
    ],
    shareId: [
        { required: true, message: '请输入分享链接', trigger: 'blur' }
    ],
    episodeRegex: [
        { required: true, message: '请输入文件匹配规则', trigger: 'blur' }
    ]
};
const shareFormRef = ref();
const excludedDirInput = ref('');
const handleAddExcludedDir = () => {
    const dir = excludedDirInput.value.trim();
    if (dir && !newShare.value.excludedDir?.includes(dir)) {
        newShare.value.excludedDir = [...(newShare.value.excludedDir || []), dir];
        excludedDirInput.value = '';
    }
};
const handleRemoveExcludedDir = (index) => {
    newShare.value.excludedDir?.splice(index, 1);
};
const formatReleaseDate = (date) => {
    if (!date)
        return '-';
    return dayjs(date).format("YYYY-MM-DD");
};
const formatFileSize = (bytes) => {
    if (!bytes)
        return '0 B';
    const units = ['B', 'KB', 'MB', 'GB', 'TB'];
    let size = bytes;
    let unitIndex = 0;
    while (size >= 1024 && unitIndex < units.length - 1) {
        size /= 1024;
        unitIndex++;
    }
    return `${size.toFixed(1)} ${units[unitIndex]}`;
};
function handleAddMedia() {
    isEdit.value = false;
    resetForm();
    addMediaDialogVisible.value = true;
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {
    ...{},
    ...{},
};
let __VLS_components;
let __VLS_directives;
/** @type {__VLS_StyleScopedClasses['task-id']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-content']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
/** @type {__VLS_StyleScopedClasses['table-container']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['data-table']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({});
const __VLS_0 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
ElCard;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({}));
const __VLS_2 = __VLS_1({}, ...__VLS_functionalComponentArgsRest(__VLS_1));
const { default: __VLS_4 } = __VLS_3.slots;
{
    const { header: __VLS_5 } = __VLS_3.slots;
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "card-header" },
    });
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "header-title" },
    });
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "operation-buttons" },
    });
    const __VLS_6 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_7 = __VLS_asFunctionalComponent(__VLS_6, new __VLS_6({
        ...{ 'onClick': {} },
        type: "primary",
        plain: true,
    }));
    const __VLS_8 = __VLS_7({
        ...{ 'onClick': {} },
        type: "primary",
        plain: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_7));
    let __VLS_10;
    let __VLS_11;
    const __VLS_12 = ({ click: {} },
        { onClick: (__VLS_ctx.handleAddMedia) });
    const { default: __VLS_13 } = __VLS_9.slots;
    // @ts-ignore
    [handleAddMedia,];
    const __VLS_14 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_15 = __VLS_asFunctionalComponent(__VLS_14, new __VLS_14({}));
    const __VLS_16 = __VLS_15({}, ...__VLS_functionalComponentArgsRest(__VLS_15));
    const { default: __VLS_18 } = __VLS_17.slots;
    const __VLS_19 = {}.Plus;
    /** @type {[typeof __VLS_components.Plus, ]} */ ;
    // @ts-ignore
    Plus;
    // @ts-ignore
    const __VLS_20 = __VLS_asFunctionalComponent(__VLS_19, new __VLS_19({}));
    const __VLS_21 = __VLS_20({}, ...__VLS_functionalComponentArgsRest(__VLS_20));
    var __VLS_17;
    var __VLS_9;
    const __VLS_24 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_25 = __VLS_asFunctionalComponent(__VLS_24, new __VLS_24({
        ...{ 'onClick': {} },
        type: "success",
        plain: true,
    }));
    const __VLS_26 = __VLS_25({
        ...{ 'onClick': {} },
        type: "success",
        plain: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_25));
    let __VLS_28;
    let __VLS_29;
    const __VLS_30 = ({ click: {} },
        { onClick: (__VLS_ctx.refreshMediaList) });
    const { default: __VLS_31 } = __VLS_27.slots;
    // @ts-ignore
    [refreshMediaList,];
    const __VLS_32 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_33 = __VLS_asFunctionalComponent(__VLS_32, new __VLS_32({}));
    const __VLS_34 = __VLS_33({}, ...__VLS_functionalComponentArgsRest(__VLS_33));
    const { default: __VLS_36 } = __VLS_35.slots;
    const __VLS_37 = {}.Refresh;
    /** @type {[typeof __VLS_components.Refresh, ]} */ ;
    // @ts-ignore
    Refresh;
    // @ts-ignore
    const __VLS_38 = __VLS_asFunctionalComponent(__VLS_37, new __VLS_37({}));
    const __VLS_39 = __VLS_38({}, ...__VLS_functionalComponentArgsRest(__VLS_38));
    var __VLS_35;
    var __VLS_27;
}
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "table-container" },
});
const __VLS_42 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
ElTable;
// @ts-ignore
const __VLS_43 = __VLS_asFunctionalComponent(__VLS_42, new __VLS_42({
    data: (__VLS_ctx.mediaList),
    ...{ class: "data-table" },
    border: true,
    stripe: true,
}));
const __VLS_44 = __VLS_43({
    data: (__VLS_ctx.mediaList),
    ...{ class: "data-table" },
    border: true,
    stripe: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_43));
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.loading) }, null, null);
const { default: __VLS_46 } = __VLS_45.slots;
// @ts-ignore
[mediaList, vLoading, loading,];
const __VLS_47 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_48 = __VLS_asFunctionalComponent(__VLS_47, new __VLS_47({
    prop: "name",
    label: "媒体名称",
    align: "center",
}));
const __VLS_49 = __VLS_48({
    prop: "name",
    label: "媒体名称",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_48));
const __VLS_52 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_53 = __VLS_asFunctionalComponent(__VLS_52, new __VLS_52({
    prop: "type",
    label: "媒体类型",
    width: "100",
    align: "center",
}));
const __VLS_54 = __VLS_53({
    prop: "type",
    label: "媒体类型",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_53));
const __VLS_57 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_58 = __VLS_asFunctionalComponent(__VLS_57, new __VLS_57({
    prop: "typeAlias",
    label: "媒体类别",
    width: "100",
    align: "center",
}));
const __VLS_59 = __VLS_58({
    prop: "typeAlias",
    label: "媒体类别",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_58));
const __VLS_62 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_63 = __VLS_asFunctionalComponent(__VLS_62, new __VLS_62({
    prop: "seasonNumber",
    label: "季数",
    width: "100",
    align: "center",
}));
const __VLS_64 = __VLS_63({
    prop: "seasonNumber",
    label: "季数",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_63));
const __VLS_67 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_68 = __VLS_asFunctionalComponent(__VLS_67, new __VLS_67({
    prop: "totalEpisode",
    label: "总集数",
    width: "100",
    align: "center",
}));
const __VLS_69 = __VLS_68({
    prop: "totalEpisode",
    label: "总集数",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_68));
const __VLS_72 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_73 = __VLS_asFunctionalComponent(__VLS_72, new __VLS_72({
    prop: "releaseDate",
    label: "首播日期",
    align: "center",
}));
const __VLS_74 = __VLS_73({
    prop: "releaseDate",
    label: "首播日期",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_73));
const { default: __VLS_76 } = __VLS_75.slots;
{
    const { default: __VLS_77 } = __VLS_75.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_77);
    (__VLS_ctx.formatReleaseDate(row.releaseDate));
    // @ts-ignore
    [formatReleaseDate,];
}
var __VLS_75;
const __VLS_78 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_79 = __VLS_asFunctionalComponent(__VLS_78, new __VLS_78({
    label: "操作",
    width: "360",
    align: "center",
    fixed: "right",
}));
const __VLS_80 = __VLS_79({
    label: "操作",
    width: "360",
    align: "center",
    fixed: "right",
}, ...__VLS_functionalComponentArgsRest(__VLS_79));
const { default: __VLS_82 } = __VLS_81.slots;
{
    const { default: __VLS_83 } = __VLS_81.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_83);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "operation-buttons" },
    });
    const __VLS_84 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_85 = __VLS_asFunctionalComponent(__VLS_84, new __VLS_84({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
    }));
    const __VLS_86 = __VLS_85({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_85));
    let __VLS_88;
    let __VLS_89;
    const __VLS_90 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleEdit(row);
                // @ts-ignore
                [handleEdit,];
            } });
    const { default: __VLS_91 } = __VLS_87.slots;
    const __VLS_92 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_93 = __VLS_asFunctionalComponent(__VLS_92, new __VLS_92({}));
    const __VLS_94 = __VLS_93({}, ...__VLS_functionalComponentArgsRest(__VLS_93));
    const { default: __VLS_96 } = __VLS_95.slots;
    const __VLS_97 = {}.Edit;
    /** @type {[typeof __VLS_components.Edit, ]} */ ;
    // @ts-ignore
    Edit;
    // @ts-ignore
    const __VLS_98 = __VLS_asFunctionalComponent(__VLS_97, new __VLS_97({}));
    const __VLS_99 = __VLS_98({}, ...__VLS_functionalComponentArgsRest(__VLS_98));
    var __VLS_95;
    var __VLS_87;
    const __VLS_102 = {}.ElDivider;
    /** @type {[typeof __VLS_components.ElDivider, typeof __VLS_components.elDivider, ]} */ ;
    // @ts-ignore
    ElDivider;
    // @ts-ignore
    const __VLS_103 = __VLS_asFunctionalComponent(__VLS_102, new __VLS_102({
        direction: "vertical",
    }));
    const __VLS_104 = __VLS_103({
        direction: "vertical",
    }, ...__VLS_functionalComponentArgsRest(__VLS_103));
    const __VLS_107 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_108 = __VLS_asFunctionalComponent(__VLS_107, new __VLS_107({
        ...{ 'onClick': {} },
        type: "warning",
        text: true,
    }));
    const __VLS_109 = __VLS_108({
        ...{ 'onClick': {} },
        type: "warning",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_108));
    let __VLS_111;
    let __VLS_112;
    const __VLS_113 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.manageShares(row);
                // @ts-ignore
                [manageShares,];
            } });
    const { default: __VLS_114 } = __VLS_110.slots;
    const __VLS_115 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_116 = __VLS_asFunctionalComponent(__VLS_115, new __VLS_115({}));
    const __VLS_117 = __VLS_116({}, ...__VLS_functionalComponentArgsRest(__VLS_116));
    const { default: __VLS_119 } = __VLS_118.slots;
    const __VLS_120 = {}.Share;
    /** @type {[typeof __VLS_components.Share, ]} */ ;
    // @ts-ignore
    Share;
    // @ts-ignore
    const __VLS_121 = __VLS_asFunctionalComponent(__VLS_120, new __VLS_120({}));
    const __VLS_122 = __VLS_121({}, ...__VLS_functionalComponentArgsRest(__VLS_121));
    var __VLS_118;
    var __VLS_110;
    const __VLS_125 = {}.ElDivider;
    /** @type {[typeof __VLS_components.ElDivider, typeof __VLS_components.elDivider, ]} */ ;
    // @ts-ignore
    ElDivider;
    // @ts-ignore
    const __VLS_126 = __VLS_asFunctionalComponent(__VLS_125, new __VLS_125({
        direction: "vertical",
    }));
    const __VLS_127 = __VLS_126({
        direction: "vertical",
    }, ...__VLS_functionalComponentArgsRest(__VLS_126));
    const __VLS_130 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_131 = __VLS_asFunctionalComponent(__VLS_130, new __VLS_130({
        ...{ 'onClick': {} },
        type: "success",
        text: true,
    }));
    const __VLS_132 = __VLS_131({
        ...{ 'onClick': {} },
        type: "success",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_131));
    let __VLS_134;
    let __VLS_135;
    const __VLS_136 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.viewDownloadTasks(row);
                // @ts-ignore
                [viewDownloadTasks,];
            } });
    const { default: __VLS_137 } = __VLS_133.slots;
    const __VLS_138 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_139 = __VLS_asFunctionalComponent(__VLS_138, new __VLS_138({}));
    const __VLS_140 = __VLS_139({}, ...__VLS_functionalComponentArgsRest(__VLS_139));
    const { default: __VLS_142 } = __VLS_141.slots;
    const __VLS_143 = {}.Download;
    /** @type {[typeof __VLS_components.Download, ]} */ ;
    // @ts-ignore
    Download;
    // @ts-ignore
    const __VLS_144 = __VLS_asFunctionalComponent(__VLS_143, new __VLS_143({}));
    const __VLS_145 = __VLS_144({}, ...__VLS_functionalComponentArgsRest(__VLS_144));
    var __VLS_141;
    var __VLS_133;
}
var __VLS_81;
var __VLS_45;
const __VLS_148 = {}.ElDialog;
/** @type {[typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, ]} */ ;
// @ts-ignore
ElDialog;
// @ts-ignore
const __VLS_149 = __VLS_asFunctionalComponent(__VLS_148, new __VLS_148({
    ...{ 'onClose': {} },
    title: (__VLS_ctx.dialogTitle),
    modelValue: (__VLS_ctx.addMediaDialogVisible),
    width: "800px",
    closeOnClickModal: (false),
    destroyOnClose: true,
    alignCenter: true,
}));
const __VLS_150 = __VLS_149({
    ...{ 'onClose': {} },
    title: (__VLS_ctx.dialogTitle),
    modelValue: (__VLS_ctx.addMediaDialogVisible),
    width: "800px",
    closeOnClickModal: (false),
    destroyOnClose: true,
    alignCenter: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_149));
let __VLS_152;
let __VLS_153;
const __VLS_154 = ({ close: {} },
    { onClose: (__VLS_ctx.handleClose) });
const { default: __VLS_155 } = __VLS_151.slots;
// @ts-ignore
[dialogTitle, addMediaDialogVisible, handleClose,];
const __VLS_156 = {}.ElForm;
/** @type {[typeof __VLS_components.ElForm, typeof __VLS_components.elForm, typeof __VLS_components.ElForm, typeof __VLS_components.elForm, ]} */ ;
// @ts-ignore
ElForm;
// @ts-ignore
const __VLS_157 = __VLS_asFunctionalComponent(__VLS_156, new __VLS_156({
    model: (__VLS_ctx.newMedia),
    ref: "form",
    labelWidth: "100px",
    ...{ class: "add-media-form" },
}));
const __VLS_158 = __VLS_157({
    model: (__VLS_ctx.newMedia),
    ref: "form",
    labelWidth: "100px",
    ...{ class: "add-media-form" },
}, ...__VLS_functionalComponentArgsRest(__VLS_157));
/** @type {typeof __VLS_ctx.form} */ ;
var __VLS_160 = {};
const { default: __VLS_162 } = __VLS_159.slots;
// @ts-ignore
[newMedia, form,];
const __VLS_163 = {}.ElRow;
/** @type {[typeof __VLS_components.ElRow, typeof __VLS_components.elRow, typeof __VLS_components.ElRow, typeof __VLS_components.elRow, ]} */ ;
// @ts-ignore
ElRow;
// @ts-ignore
const __VLS_164 = __VLS_asFunctionalComponent(__VLS_163, new __VLS_163({
    gutter: (20),
}));
const __VLS_165 = __VLS_164({
    gutter: (20),
}, ...__VLS_functionalComponentArgsRest(__VLS_164));
const { default: __VLS_167 } = __VLS_166.slots;
const __VLS_168 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_169 = __VLS_asFunctionalComponent(__VLS_168, new __VLS_168({
    span: (24),
}));
const __VLS_170 = __VLS_169({
    span: (24),
}, ...__VLS_functionalComponentArgsRest(__VLS_169));
const { default: __VLS_172 } = __VLS_171.slots;
const __VLS_173 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_174 = __VLS_asFunctionalComponent(__VLS_173, new __VLS_173({
    label: "媒体名称",
    prop: "name",
    rules: ([{ required: true, message: '请输入标题', trigger: 'blur' }]),
}));
const __VLS_175 = __VLS_174({
    label: "媒体名称",
    prop: "name",
    rules: ([{ required: true, message: '请输入标题', trigger: 'blur' }]),
}, ...__VLS_functionalComponentArgsRest(__VLS_174));
const { default: __VLS_177 } = __VLS_176.slots;
const __VLS_178 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_179 = __VLS_asFunctionalComponent(__VLS_178, new __VLS_178({
    modelValue: (__VLS_ctx.newMedia.name),
    disabled: (__VLS_ctx.isEdit),
    placeholder: "请输入影视标题",
    clearable: true,
}));
const __VLS_180 = __VLS_179({
    modelValue: (__VLS_ctx.newMedia.name),
    disabled: (__VLS_ctx.isEdit),
    placeholder: "请输入影视标题",
    clearable: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_179));
// @ts-ignore
[newMedia, isEdit,];
var __VLS_176;
var __VLS_171;
const __VLS_183 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_184 = __VLS_asFunctionalComponent(__VLS_183, new __VLS_183({
    span: (12),
}));
const __VLS_185 = __VLS_184({
    span: (12),
}, ...__VLS_functionalComponentArgsRest(__VLS_184));
const { default: __VLS_187 } = __VLS_186.slots;
const __VLS_188 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_189 = __VLS_asFunctionalComponent(__VLS_188, new __VLS_188({
    label: "媒体类型",
    prop: "type",
    rules: ([{ required: true, message: '请选择类型', trigger: 'change' }]),
}));
const __VLS_190 = __VLS_189({
    label: "媒体类型",
    prop: "type",
    rules: ([{ required: true, message: '请选择类型', trigger: 'change' }]),
}, ...__VLS_functionalComponentArgsRest(__VLS_189));
const { default: __VLS_192 } = __VLS_191.slots;
const __VLS_193 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
ElSelect;
// @ts-ignore
const __VLS_194 = __VLS_asFunctionalComponent(__VLS_193, new __VLS_193({
    modelValue: (__VLS_ctx.newMedia.type),
    disabled: (__VLS_ctx.isEdit),
    placeholder: "请选择类型",
    ...{ class: "full-width" },
}));
const __VLS_195 = __VLS_194({
    modelValue: (__VLS_ctx.newMedia.type),
    disabled: (__VLS_ctx.isEdit),
    placeholder: "请选择类型",
    ...{ class: "full-width" },
}, ...__VLS_functionalComponentArgsRest(__VLS_194));
const { default: __VLS_197 } = __VLS_196.slots;
// @ts-ignore
[newMedia, isEdit,];
for (const [option] of __VLS_getVForSourceType((__VLS_ctx.mediaTypes))) {
    // @ts-ignore
    [mediaTypes,];
    const __VLS_198 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    ElOption;
    // @ts-ignore
    const __VLS_199 = __VLS_asFunctionalComponent(__VLS_198, new __VLS_198({
        key: (option.value),
        label: (option.label),
        value: (option.value),
    }));
    const __VLS_200 = __VLS_199({
        key: (option.value),
        label: (option.label),
        value: (option.value),
    }, ...__VLS_functionalComponentArgsRest(__VLS_199));
    const { default: __VLS_202 } = __VLS_201.slots;
    const __VLS_203 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_204 = __VLS_asFunctionalComponent(__VLS_203, new __VLS_203({
        ...{ class: "option-icon" },
    }));
    const __VLS_205 = __VLS_204({
        ...{ class: "option-icon" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_204));
    const { default: __VLS_207 } = __VLS_206.slots;
    const __VLS_208 = ((option.icon));
    // @ts-ignore
    const __VLS_209 = __VLS_asFunctionalComponent(__VLS_208, new __VLS_208({}));
    const __VLS_210 = __VLS_209({}, ...__VLS_functionalComponentArgsRest(__VLS_209));
    var __VLS_206;
    (option.label);
    var __VLS_201;
}
var __VLS_196;
var __VLS_191;
var __VLS_186;
const __VLS_213 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_214 = __VLS_asFunctionalComponent(__VLS_213, new __VLS_213({
    span: (12),
}));
const __VLS_215 = __VLS_214({
    span: (12),
}, ...__VLS_functionalComponentArgsRest(__VLS_214));
const { default: __VLS_217 } = __VLS_216.slots;
const __VLS_218 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_219 = __VLS_asFunctionalComponent(__VLS_218, new __VLS_218({
    label: "类型别名",
}));
const __VLS_220 = __VLS_219({
    label: "类型别名",
}, ...__VLS_functionalComponentArgsRest(__VLS_219));
const { default: __VLS_222 } = __VLS_221.slots;
const __VLS_223 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_224 = __VLS_asFunctionalComponent(__VLS_223, new __VLS_223({
    modelValue: (__VLS_ctx.newMedia.typeAlias),
    placeholder: "请输入类型别名",
    clearable: true,
}));
const __VLS_225 = __VLS_224({
    modelValue: (__VLS_ctx.newMedia.typeAlias),
    placeholder: "请输入类型别名",
    clearable: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_224));
// @ts-ignore
[newMedia,];
var __VLS_221;
var __VLS_216;
const __VLS_228 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_229 = __VLS_asFunctionalComponent(__VLS_228, new __VLS_228({
    span: (8),
}));
const __VLS_230 = __VLS_229({
    span: (8),
}, ...__VLS_functionalComponentArgsRest(__VLS_229));
const { default: __VLS_232 } = __VLS_231.slots;
const __VLS_233 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_234 = __VLS_asFunctionalComponent(__VLS_233, new __VLS_233({
    label: "季数",
}));
const __VLS_235 = __VLS_234({
    label: "季数",
}, ...__VLS_functionalComponentArgsRest(__VLS_234));
const { default: __VLS_237 } = __VLS_236.slots;
const __VLS_238 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
ElInputNumber;
// @ts-ignore
const __VLS_239 = __VLS_asFunctionalComponent(__VLS_238, new __VLS_238({
    modelValue: (__VLS_ctx.newMedia.seasonNumber),
    disabled: (__VLS_ctx.isEdit),
    min: (0),
    precision: (0),
    ...{ class: "full-width" },
}));
const __VLS_240 = __VLS_239({
    modelValue: (__VLS_ctx.newMedia.seasonNumber),
    disabled: (__VLS_ctx.isEdit),
    min: (0),
    precision: (0),
    ...{ class: "full-width" },
}, ...__VLS_functionalComponentArgsRest(__VLS_239));
// @ts-ignore
[newMedia, isEdit,];
var __VLS_236;
var __VLS_231;
const __VLS_243 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_244 = __VLS_asFunctionalComponent(__VLS_243, new __VLS_243({
    span: (8),
}));
const __VLS_245 = __VLS_244({
    span: (8),
}, ...__VLS_functionalComponentArgsRest(__VLS_244));
const { default: __VLS_247 } = __VLS_246.slots;
const __VLS_248 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_249 = __VLS_asFunctionalComponent(__VLS_248, new __VLS_248({
    label: "总集数",
}));
const __VLS_250 = __VLS_249({
    label: "总集数",
}, ...__VLS_functionalComponentArgsRest(__VLS_249));
const { default: __VLS_252 } = __VLS_251.slots;
const __VLS_253 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
ElInputNumber;
// @ts-ignore
const __VLS_254 = __VLS_asFunctionalComponent(__VLS_253, new __VLS_253({
    modelValue: (__VLS_ctx.newMedia.totalEpisode),
    min: (0),
    precision: (0),
    ...{ class: "full-width" },
}));
const __VLS_255 = __VLS_254({
    modelValue: (__VLS_ctx.newMedia.totalEpisode),
    min: (0),
    precision: (0),
    ...{ class: "full-width" },
}, ...__VLS_functionalComponentArgsRest(__VLS_254));
// @ts-ignore
[newMedia,];
var __VLS_251;
var __VLS_246;
const __VLS_258 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_259 = __VLS_asFunctionalComponent(__VLS_258, new __VLS_258({
    span: (8),
}));
const __VLS_260 = __VLS_259({
    span: (8),
}, ...__VLS_functionalComponentArgsRest(__VLS_259));
const { default: __VLS_262 } = __VLS_261.slots;
const __VLS_263 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_264 = __VLS_asFunctionalComponent(__VLS_263, new __VLS_263({
    label: "开始集数",
}));
const __VLS_265 = __VLS_264({
    label: "开始集数",
}, ...__VLS_functionalComponentArgsRest(__VLS_264));
const { default: __VLS_267 } = __VLS_266.slots;
const __VLS_268 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
ElInputNumber;
// @ts-ignore
const __VLS_269 = __VLS_asFunctionalComponent(__VLS_268, new __VLS_268({
    modelValue: (__VLS_ctx.newMedia.startEpisode),
    min: (0),
    precision: (0),
    ...{ class: "full-width" },
}));
const __VLS_270 = __VLS_269({
    modelValue: (__VLS_ctx.newMedia.startEpisode),
    min: (0),
    precision: (0),
    ...{ class: "full-width" },
}, ...__VLS_functionalComponentArgsRest(__VLS_269));
// @ts-ignore
[newMedia,];
var __VLS_266;
var __VLS_261;
const __VLS_273 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_274 = __VLS_asFunctionalComponent(__VLS_273, new __VLS_273({
    span: (12),
}));
const __VLS_275 = __VLS_274({
    span: (12),
}, ...__VLS_functionalComponentArgsRest(__VLS_274));
const { default: __VLS_277 } = __VLS_276.slots;
const __VLS_278 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_279 = __VLS_asFunctionalComponent(__VLS_278, new __VLS_278({
    label: "首播日期",
}));
const __VLS_280 = __VLS_279({
    label: "首播日期",
}, ...__VLS_functionalComponentArgsRest(__VLS_279));
const { default: __VLS_282 } = __VLS_281.slots;
const __VLS_283 = {}.ElDatePicker;
/** @type {[typeof __VLS_components.ElDatePicker, typeof __VLS_components.elDatePicker, typeof __VLS_components.ElDatePicker, typeof __VLS_components.elDatePicker, ]} */ ;
// @ts-ignore
ElDatePicker;
// @ts-ignore
const __VLS_284 = __VLS_asFunctionalComponent(__VLS_283, new __VLS_283({
    modelValue: (__VLS_ctx.newMedia.releaseDate),
    type: "date",
    placeholder: "请选择首播日期",
    ...{ class: "full-width" },
    valueFormat: "x",
}));
const __VLS_285 = __VLS_284({
    modelValue: (__VLS_ctx.newMedia.releaseDate),
    type: "date",
    placeholder: "请选择首播日期",
    ...{ class: "full-width" },
    valueFormat: "x",
}, ...__VLS_functionalComponentArgsRest(__VLS_284));
// @ts-ignore
[newMedia,];
var __VLS_281;
var __VLS_276;
var __VLS_166;
var __VLS_159;
{
    const { footer: __VLS_288 } = __VLS_151.slots;
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "dialog-footer" },
    });
    const __VLS_289 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_290 = __VLS_asFunctionalComponent(__VLS_289, new __VLS_289({
        ...{ 'onClick': {} },
    }));
    const __VLS_291 = __VLS_290({
        ...{ 'onClick': {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_290));
    let __VLS_293;
    let __VLS_294;
    const __VLS_295 = ({ click: {} },
        { onClick: (__VLS_ctx.handleClose) });
    const { default: __VLS_296 } = __VLS_292.slots;
    // @ts-ignore
    [handleClose,];
    var __VLS_292;
    const __VLS_297 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_298 = __VLS_asFunctionalComponent(__VLS_297, new __VLS_297({
        ...{ 'onClick': {} },
        type: "primary",
    }));
    const __VLS_299 = __VLS_298({
        ...{ 'onClick': {} },
        type: "primary",
    }, ...__VLS_functionalComponentArgsRest(__VLS_298));
    let __VLS_301;
    let __VLS_302;
    const __VLS_303 = ({ click: {} },
        { onClick: (__VLS_ctx.submitForm) });
    const { default: __VLS_304 } = __VLS_300.slots;
    // @ts-ignore
    [submitForm,];
    var __VLS_300;
}
var __VLS_151;
const __VLS_305 = {}.ElDialog;
/** @type {[typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, ]} */ ;
// @ts-ignore
ElDialog;
// @ts-ignore
const __VLS_306 = __VLS_asFunctionalComponent(__VLS_305, new __VLS_305({
    title: "分享管理",
    modelValue: (__VLS_ctx.shareDialogVisible),
    width: "1200px",
    closeOnClickModal: (false),
    ...{ class: "share-dialog" },
    destroyOnClose: true,
    alignCenter: true,
}));
const __VLS_307 = __VLS_306({
    title: "分享管理",
    modelValue: (__VLS_ctx.shareDialogVisible),
    width: "1200px",
    closeOnClickModal: (false),
    ...{ class: "share-dialog" },
    destroyOnClose: true,
    alignCenter: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_306));
const { default: __VLS_309 } = __VLS_308.slots;
// @ts-ignore
[shareDialogVisible,];
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "share-header" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
    ...{ class: "share-title" },
});
const __VLS_310 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
ElButton;
// @ts-ignore
const __VLS_311 = __VLS_asFunctionalComponent(__VLS_310, new __VLS_310({
    ...{ 'onClick': {} },
    type: "primary",
    plain: true,
}));
const __VLS_312 = __VLS_311({
    ...{ 'onClick': {} },
    type: "primary",
    plain: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_311));
let __VLS_314;
let __VLS_315;
const __VLS_316 = ({ click: {} },
    { onClick: (__VLS_ctx.handleAddShare) });
const { default: __VLS_317 } = __VLS_313.slots;
// @ts-ignore
[handleAddShare,];
const __VLS_318 = {}.ElIcon;
/** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
// @ts-ignore
ElIcon;
// @ts-ignore
const __VLS_319 = __VLS_asFunctionalComponent(__VLS_318, new __VLS_318({}));
const __VLS_320 = __VLS_319({}, ...__VLS_functionalComponentArgsRest(__VLS_319));
const { default: __VLS_322 } = __VLS_321.slots;
const __VLS_323 = {}.Plus;
/** @type {[typeof __VLS_components.Plus, ]} */ ;
// @ts-ignore
Plus;
// @ts-ignore
const __VLS_324 = __VLS_asFunctionalComponent(__VLS_323, new __VLS_323({}));
const __VLS_325 = __VLS_324({}, ...__VLS_functionalComponentArgsRest(__VLS_324));
var __VLS_321;
var __VLS_313;
const __VLS_328 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
ElTable;
// @ts-ignore
const __VLS_329 = __VLS_asFunctionalComponent(__VLS_328, new __VLS_328({
    data: (__VLS_ctx.shareList),
    border: true,
    stripe: true,
    ...{ class: "share-table" },
}));
const __VLS_330 = __VLS_329({
    data: (__VLS_ctx.shareList),
    border: true,
    stripe: true,
    ...{ class: "share-table" },
}, ...__VLS_functionalComponentArgsRest(__VLS_329));
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.shareLoading) }, null, null);
const { default: __VLS_332 } = __VLS_331.slots;
// @ts-ignore
[vLoading, shareList, shareLoading,];
const __VLS_333 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_334 = __VLS_asFunctionalComponent(__VLS_333, new __VLS_333({
    label: "网盘类型",
    prop: "provider",
    width: "150",
    align: "center",
}));
const __VLS_335 = __VLS_334({
    label: "网盘类型",
    prop: "provider",
    width: "150",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_334));
const { default: __VLS_337 } = __VLS_336.slots;
{
    const { default: __VLS_338 } = __VLS_336.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_338);
    const __VLS_339 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    ElTag;
    // @ts-ignore
    const __VLS_340 = __VLS_asFunctionalComponent(__VLS_339, new __VLS_339({
        type: (__VLS_ctx.getProviderTagType(row.provider)),
        effect: "plain",
        ...{ class: "provider-tag" },
    }));
    const __VLS_341 = __VLS_340({
        type: (__VLS_ctx.getProviderTagType(row.provider)),
        effect: "plain",
        ...{ class: "provider-tag" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_340));
    const { default: __VLS_343 } = __VLS_342.slots;
    // @ts-ignore
    [getProviderTagType,];
    (__VLS_ctx.getProviderName(row.provider));
    // @ts-ignore
    [getProviderName,];
    var __VLS_342;
}
var __VLS_336;
const __VLS_344 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_345 = __VLS_asFunctionalComponent(__VLS_344, new __VLS_344({
    prop: "shareId",
    label: "分享链接",
    minWidth: "300",
    showOverflowTooltip: true,
}));
const __VLS_346 = __VLS_345({
    prop: "shareId",
    label: "分享链接",
    minWidth: "300",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_345));
const __VLS_349 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_350 = __VLS_asFunctionalComponent(__VLS_349, new __VLS_349({
    prop: "shareCode",
    label: "提取码",
    width: "100",
    align: "center",
}));
const __VLS_351 = __VLS_350({
    prop: "shareCode",
    label: "提取码",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_350));
const __VLS_354 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_355 = __VLS_asFunctionalComponent(__VLS_354, new __VLS_354({
    prop: "expiredAt",
    label: "过期时间",
    width: "180",
    align: "center",
}));
const __VLS_356 = __VLS_355({
    prop: "expiredAt",
    label: "过期时间",
    width: "180",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_355));
const { default: __VLS_358 } = __VLS_357.slots;
{
    const { default: __VLS_359 } = __VLS_357.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_359);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: ({ 'expired': __VLS_ctx.isExpired(row.expiredAt) }) },
    });
    // @ts-ignore
    [isExpired,];
    (__VLS_ctx.formatExpiredTime(row.expiredAt));
    // @ts-ignore
    [formatExpiredTime,];
}
var __VLS_357;
const __VLS_360 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_361 = __VLS_asFunctionalComponent(__VLS_360, new __VLS_360({
    prop: "fileRegex",
    label: "文件匹配规则",
    minWidth: "200",
    showOverflowTooltip: true,
}));
const __VLS_362 = __VLS_361({
    prop: "fileRegex",
    label: "文件匹配规则",
    minWidth: "200",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_361));
const __VLS_365 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_366 = __VLS_asFunctionalComponent(__VLS_365, new __VLS_365({
    prop: "episodeRegex",
    label: "匹配集数规则",
    minWidth: "200",
    showOverflowTooltip: true,
}));
const __VLS_367 = __VLS_366({
    prop: "episodeRegex",
    label: "匹配集数规则",
    minWidth: "200",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_366));
const __VLS_370 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_371 = __VLS_asFunctionalComponent(__VLS_370, new __VLS_370({
    prop: "onlyInDir",
    label: "有效目录",
    minWidth: "200",
    showOverflowTooltip: true,
}));
const __VLS_372 = __VLS_371({
    prop: "onlyInDir",
    label: "有效目录",
    minWidth: "200",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_371));
const { default: __VLS_374 } = __VLS_373.slots;
{
    const { default: __VLS_375 } = __VLS_373.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_375);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "only-in-dir" },
    });
    (row.onlyInDir || '-');
}
var __VLS_373;
const __VLS_376 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_377 = __VLS_asFunctionalComponent(__VLS_376, new __VLS_376({
    prop: "excludedDir",
    label: "排除目录",
    minWidth: "200",
    showOverflowTooltip: true,
}));
const __VLS_378 = __VLS_377({
    prop: "excludedDir",
    label: "排除目录",
    minWidth: "200",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_377));
const { default: __VLS_380 } = __VLS_379.slots;
{
    const { default: __VLS_381 } = __VLS_379.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_381);
    if (row.excludedDir && row.excludedDir.length) {
        const __VLS_382 = {}.ElTooltip;
        /** @type {[typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, ]} */ ;
        // @ts-ignore
        ElTooltip;
        // @ts-ignore
        const __VLS_383 = __VLS_asFunctionalComponent(__VLS_382, new __VLS_382({
            content: (row.excludedDir.join('\n')),
            placement: "top",
            effect: "dark",
        }));
        const __VLS_384 = __VLS_383({
            content: (row.excludedDir.join('\n')),
            placement: "top",
            effect: "dark",
        }, ...__VLS_functionalComponentArgsRest(__VLS_383));
        const { default: __VLS_386 } = __VLS_385.slots;
        __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
            ...{ class: "excluded-dir" },
        });
        (row.excludedDir.join(', '));
        var __VLS_385;
    }
    else {
        __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({});
    }
}
var __VLS_379;
const __VLS_387 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_388 = __VLS_asFunctionalComponent(__VLS_387, new __VLS_387({
    prop: "isLapse",
    label: "是否失效",
    width: "100",
    align: "center",
}));
const __VLS_389 = __VLS_388({
    prop: "isLapse",
    label: "是否失效",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_388));
const { default: __VLS_391 } = __VLS_390.slots;
{
    const { default: __VLS_392 } = __VLS_390.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_392);
    const __VLS_393 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    ElTag;
    // @ts-ignore
    const __VLS_394 = __VLS_asFunctionalComponent(__VLS_393, new __VLS_393({
        type: (row.isLapse === 1 ? 'danger' : 'success'),
        effect: "plain",
    }));
    const __VLS_395 = __VLS_394({
        type: (row.isLapse === 1 ? 'danger' : 'success'),
        effect: "plain",
    }, ...__VLS_functionalComponentArgsRest(__VLS_394));
    const { default: __VLS_397 } = __VLS_396.slots;
    (row.isLapse === 1 ? '已失效' : '有效');
    var __VLS_396;
}
var __VLS_390;
const __VLS_398 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_399 = __VLS_asFunctionalComponent(__VLS_398, new __VLS_398({
    prop: "lapseCause",
    label: "失效原因",
    minWidth: "150",
    showOverflowTooltip: true,
}));
const __VLS_400 = __VLS_399({
    prop: "lapseCause",
    label: "失效原因",
    minWidth: "150",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_399));
const { default: __VLS_402 } = __VLS_401.slots;
{
    const { default: __VLS_403 } = __VLS_401.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_403);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: ({ 'lapse-cause': row.isLapse === 1 }) },
    });
    (row.lapseCause || '-');
}
var __VLS_401;
const __VLS_404 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_405 = __VLS_asFunctionalComponent(__VLS_404, new __VLS_404({
    label: "操作",
    width: "180",
    align: "center",
    fixed: "right",
}));
const __VLS_406 = __VLS_405({
    label: "操作",
    width: "180",
    align: "center",
    fixed: "right",
}, ...__VLS_functionalComponentArgsRest(__VLS_405));
const { default: __VLS_408 } = __VLS_407.slots;
{
    const { default: __VLS_409 } = __VLS_407.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_409);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "share-operation" },
    });
    const __VLS_410 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_411 = __VLS_asFunctionalComponent(__VLS_410, new __VLS_410({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
        ...{ class: "edit-btn" },
    }));
    const __VLS_412 = __VLS_411({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
        ...{ class: "edit-btn" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_411));
    let __VLS_414;
    let __VLS_415;
    const __VLS_416 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleEditShare(row);
                // @ts-ignore
                [handleEditShare,];
            } });
    const { default: __VLS_417 } = __VLS_413.slots;
    const __VLS_418 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_419 = __VLS_asFunctionalComponent(__VLS_418, new __VLS_418({}));
    const __VLS_420 = __VLS_419({}, ...__VLS_functionalComponentArgsRest(__VLS_419));
    const { default: __VLS_422 } = __VLS_421.slots;
    const __VLS_423 = {}.Edit;
    /** @type {[typeof __VLS_components.Edit, ]} */ ;
    // @ts-ignore
    Edit;
    // @ts-ignore
    const __VLS_424 = __VLS_asFunctionalComponent(__VLS_423, new __VLS_423({}));
    const __VLS_425 = __VLS_424({}, ...__VLS_functionalComponentArgsRest(__VLS_424));
    var __VLS_421;
    var __VLS_413;
    const __VLS_428 = {}.ElDivider;
    /** @type {[typeof __VLS_components.ElDivider, typeof __VLS_components.elDivider, ]} */ ;
    // @ts-ignore
    ElDivider;
    // @ts-ignore
    const __VLS_429 = __VLS_asFunctionalComponent(__VLS_428, new __VLS_428({
        direction: "vertical",
    }));
    const __VLS_430 = __VLS_429({
        direction: "vertical",
    }, ...__VLS_functionalComponentArgsRest(__VLS_429));
    const __VLS_433 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_434 = __VLS_asFunctionalComponent(__VLS_433, new __VLS_433({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
        ...{ class: "delete-btn" },
    }));
    const __VLS_435 = __VLS_434({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
        ...{ class: "delete-btn" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_434));
    let __VLS_437;
    let __VLS_438;
    const __VLS_439 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.deleteShare(row.id);
                // @ts-ignore
                [deleteShare,];
            } });
    const { default: __VLS_440 } = __VLS_436.slots;
    const __VLS_441 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_442 = __VLS_asFunctionalComponent(__VLS_441, new __VLS_441({}));
    const __VLS_443 = __VLS_442({}, ...__VLS_functionalComponentArgsRest(__VLS_442));
    const { default: __VLS_445 } = __VLS_444.slots;
    const __VLS_446 = {}.Delete;
    /** @type {[typeof __VLS_components.Delete, ]} */ ;
    // @ts-ignore
    Delete;
    // @ts-ignore
    const __VLS_447 = __VLS_asFunctionalComponent(__VLS_446, new __VLS_446({}));
    const __VLS_448 = __VLS_447({}, ...__VLS_functionalComponentArgsRest(__VLS_447));
    var __VLS_444;
    var __VLS_436;
}
var __VLS_407;
var __VLS_331;
var __VLS_308;
const __VLS_451 = {}.ElDialog;
/** @type {[typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, ]} */ ;
// @ts-ignore
ElDialog;
// @ts-ignore
const __VLS_452 = __VLS_asFunctionalComponent(__VLS_451, new __VLS_451({
    modelValue: (__VLS_ctx.addShareDialogVisible),
    title: (__VLS_ctx.isEditShare ? '编辑分享' : '添加分享'),
    width: "500px",
    appendToBody: true,
    closeOnClickModal: (false),
    destroyOnClose: true,
    alignCenter: true,
}));
const __VLS_453 = __VLS_452({
    modelValue: (__VLS_ctx.addShareDialogVisible),
    title: (__VLS_ctx.isEditShare ? '编辑分享' : '添加分享'),
    width: "500px",
    appendToBody: true,
    closeOnClickModal: (false),
    destroyOnClose: true,
    alignCenter: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_452));
const { default: __VLS_455 } = __VLS_454.slots;
// @ts-ignore
[addShareDialogVisible, isEditShare,];
const __VLS_456 = {}.ElForm;
/** @type {[typeof __VLS_components.ElForm, typeof __VLS_components.elForm, typeof __VLS_components.ElForm, typeof __VLS_components.elForm, ]} */ ;
// @ts-ignore
ElForm;
// @ts-ignore
const __VLS_457 = __VLS_asFunctionalComponent(__VLS_456, new __VLS_456({
    model: (__VLS_ctx.newShare),
    ref: "shareFormRef",
    labelWidth: "100px",
    rules: (__VLS_ctx.shareFormRules),
}));
const __VLS_458 = __VLS_457({
    model: (__VLS_ctx.newShare),
    ref: "shareFormRef",
    labelWidth: "100px",
    rules: (__VLS_ctx.shareFormRules),
}, ...__VLS_functionalComponentArgsRest(__VLS_457));
/** @type {typeof __VLS_ctx.shareFormRef} */ ;
var __VLS_460 = {};
const { default: __VLS_462 } = __VLS_459.slots;
// @ts-ignore
[newShare, shareFormRules, shareFormRef,];
const __VLS_463 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_464 = __VLS_asFunctionalComponent(__VLS_463, new __VLS_463({
    label: "网盘类型",
    prop: "provider",
    rules: (__VLS_ctx.shareFormRules.provider),
}));
const __VLS_465 = __VLS_464({
    label: "网盘类型",
    prop: "provider",
    rules: (__VLS_ctx.shareFormRules.provider),
}, ...__VLS_functionalComponentArgsRest(__VLS_464));
const { default: __VLS_467 } = __VLS_466.slots;
// @ts-ignore
[shareFormRules,];
const __VLS_468 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
ElSelect;
// @ts-ignore
const __VLS_469 = __VLS_asFunctionalComponent(__VLS_468, new __VLS_468({
    modelValue: (__VLS_ctx.newShare.provider),
    placeholder: "请选择网盘类型",
}));
const __VLS_470 = __VLS_469({
    modelValue: (__VLS_ctx.newShare.provider),
    placeholder: "请选择网盘类型",
}, ...__VLS_functionalComponentArgsRest(__VLS_469));
const { default: __VLS_472 } = __VLS_471.slots;
// @ts-ignore
[newShare,];
const __VLS_473 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_474 = __VLS_asFunctionalComponent(__VLS_473, new __VLS_473({
    label: "夸克",
    value: (1),
}));
const __VLS_475 = __VLS_474({
    label: "夸克",
    value: (1),
}, ...__VLS_functionalComponentArgsRest(__VLS_474));
const __VLS_478 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_479 = __VLS_asFunctionalComponent(__VLS_478, new __VLS_478({
    label: "阿里云盘",
    value: (2),
}));
const __VLS_480 = __VLS_479({
    label: "阿里云盘",
    value: (2),
}, ...__VLS_functionalComponentArgsRest(__VLS_479));
const __VLS_483 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_484 = __VLS_asFunctionalComponent(__VLS_483, new __VLS_483({
    label: "百度网盘",
    value: (3),
}));
const __VLS_485 = __VLS_484({
    label: "百度网盘",
    value: (3),
}, ...__VLS_functionalComponentArgsRest(__VLS_484));
var __VLS_471;
var __VLS_466;
const __VLS_488 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_489 = __VLS_asFunctionalComponent(__VLS_488, new __VLS_488({
    label: "分享链接",
    prop: "shareId",
    rules: (__VLS_ctx.shareFormRules.shareId),
}));
const __VLS_490 = __VLS_489({
    label: "分享链接",
    prop: "shareId",
    rules: (__VLS_ctx.shareFormRules.shareId),
}, ...__VLS_functionalComponentArgsRest(__VLS_489));
const { default: __VLS_492 } = __VLS_491.slots;
// @ts-ignore
[shareFormRules,];
const __VLS_493 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_494 = __VLS_asFunctionalComponent(__VLS_493, new __VLS_493({
    modelValue: (__VLS_ctx.newShare.shareId),
    placeholder: "请输入分享链接",
}));
const __VLS_495 = __VLS_494({
    modelValue: (__VLS_ctx.newShare.shareId),
    placeholder: "请输入分享链接",
}, ...__VLS_functionalComponentArgsRest(__VLS_494));
// @ts-ignore
[newShare,];
var __VLS_491;
const __VLS_498 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_499 = __VLS_asFunctionalComponent(__VLS_498, new __VLS_498({
    label: "提取码",
    prop: "shareCode",
}));
const __VLS_500 = __VLS_499({
    label: "提取码",
    prop: "shareCode",
}, ...__VLS_functionalComponentArgsRest(__VLS_499));
const { default: __VLS_502 } = __VLS_501.slots;
const __VLS_503 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_504 = __VLS_asFunctionalComponent(__VLS_503, new __VLS_503({
    modelValue: (__VLS_ctx.newShare.shareCode),
    placeholder: "请输入提取码（如有）",
}));
const __VLS_505 = __VLS_504({
    modelValue: (__VLS_ctx.newShare.shareCode),
    placeholder: "请输入提取码（如有）",
}, ...__VLS_functionalComponentArgsRest(__VLS_504));
// @ts-ignore
[newShare,];
var __VLS_501;
const __VLS_508 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_509 = __VLS_asFunctionalComponent(__VLS_508, new __VLS_508({
    label: "过期时间",
    prop: "expiredAt",
}));
const __VLS_510 = __VLS_509({
    label: "过期时间",
    prop: "expiredAt",
}, ...__VLS_functionalComponentArgsRest(__VLS_509));
const { default: __VLS_512 } = __VLS_511.slots;
const __VLS_513 = {}.ElDatePicker;
/** @type {[typeof __VLS_components.ElDatePicker, typeof __VLS_components.elDatePicker, typeof __VLS_components.ElDatePicker, typeof __VLS_components.elDatePicker, ]} */ ;
// @ts-ignore
ElDatePicker;
// @ts-ignore
const __VLS_514 = __VLS_asFunctionalComponent(__VLS_513, new __VLS_513({
    modelValue: (__VLS_ctx.newShare.expiredAt),
    type: "datetime",
    placeholder: "请选择过期时间（可选）",
    valueFormat: "YYYY-MM-DD HH:mm:ss",
}));
const __VLS_515 = __VLS_514({
    modelValue: (__VLS_ctx.newShare.expiredAt),
    type: "datetime",
    placeholder: "请选择过期时间（可选）",
    valueFormat: "YYYY-MM-DD HH:mm:ss",
}, ...__VLS_functionalComponentArgsRest(__VLS_514));
// @ts-ignore
[newShare,];
var __VLS_511;
const __VLS_518 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_519 = __VLS_asFunctionalComponent(__VLS_518, new __VLS_518({
    label: "文件匹配规则",
    prop: "fileRegex",
}));
const __VLS_520 = __VLS_519({
    label: "文件匹配规则",
    prop: "fileRegex",
}, ...__VLS_functionalComponentArgsRest(__VLS_519));
const { default: __VLS_522 } = __VLS_521.slots;
const __VLS_523 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_524 = __VLS_asFunctionalComponent(__VLS_523, new __VLS_523({
    modelValue: (__VLS_ctx.newShare.fileRegex),
    placeholder: "请输入文件匹配规则",
}));
const __VLS_525 = __VLS_524({
    modelValue: (__VLS_ctx.newShare.fileRegex),
    placeholder: "请输入文件匹配规则",
}, ...__VLS_functionalComponentArgsRest(__VLS_524));
const { default: __VLS_527 } = __VLS_526.slots;
// @ts-ignore
[newShare,];
{
    const { append: __VLS_528 } = __VLS_526.slots;
    const __VLS_529 = {}.ElTooltip;
    /** @type {[typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, ]} */ ;
    // @ts-ignore
    ElTooltip;
    // @ts-ignore
    const __VLS_530 = __VLS_asFunctionalComponent(__VLS_529, new __VLS_529({
        content: "\u7528\u4e8e\u5339\u914d\u5206\u4eab\u94fe\u63a5\u4e2d\u7684\u6587\u4ef6\uff0c\u4f8b\u5982\uff1a\u002e\u002a\u005c\u002e\u006d\u0070\u0034",
        placement: "top",
    }));
    const __VLS_531 = __VLS_530({
        content: "\u7528\u4e8e\u5339\u914d\u5206\u4eab\u94fe\u63a5\u4e2d\u7684\u6587\u4ef6\uff0c\u4f8b\u5982\uff1a\u002e\u002a\u005c\u002e\u006d\u0070\u0034",
        placement: "top",
    }, ...__VLS_functionalComponentArgsRest(__VLS_530));
    const { default: __VLS_533 } = __VLS_532.slots;
    const __VLS_534 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_535 = __VLS_asFunctionalComponent(__VLS_534, new __VLS_534({}));
    const __VLS_536 = __VLS_535({}, ...__VLS_functionalComponentArgsRest(__VLS_535));
    const { default: __VLS_538 } = __VLS_537.slots;
    const __VLS_539 = {}.QuestionFilled;
    /** @type {[typeof __VLS_components.QuestionFilled, typeof __VLS_components.questionFilled, ]} */ ;
    // @ts-ignore
    QuestionFilled;
    // @ts-ignore
    const __VLS_540 = __VLS_asFunctionalComponent(__VLS_539, new __VLS_539({}));
    const __VLS_541 = __VLS_540({}, ...__VLS_functionalComponentArgsRest(__VLS_540));
    var __VLS_537;
    var __VLS_532;
}
var __VLS_526;
var __VLS_521;
const __VLS_544 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_545 = __VLS_asFunctionalComponent(__VLS_544, new __VLS_544({
    label: "集数匹配规则",
    prop: "episodeRegex",
    rules: (__VLS_ctx.shareFormRules.episodeRegex),
}));
const __VLS_546 = __VLS_545({
    label: "集数匹配规则",
    prop: "episodeRegex",
    rules: (__VLS_ctx.shareFormRules.episodeRegex),
}, ...__VLS_functionalComponentArgsRest(__VLS_545));
const { default: __VLS_548 } = __VLS_547.slots;
// @ts-ignore
[shareFormRules,];
const __VLS_549 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_550 = __VLS_asFunctionalComponent(__VLS_549, new __VLS_549({
    modelValue: (__VLS_ctx.newShare.episodeRegex),
    placeholder: "请输入匹配集数规则",
}));
const __VLS_551 = __VLS_550({
    modelValue: (__VLS_ctx.newShare.episodeRegex),
    placeholder: "请输入匹配集数规则",
}, ...__VLS_functionalComponentArgsRest(__VLS_550));
const { default: __VLS_553 } = __VLS_552.slots;
// @ts-ignore
[newShare,];
{
    const { append: __VLS_554 } = __VLS_552.slots;
    const __VLS_555 = {}.ElTooltip;
    /** @type {[typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, ]} */ ;
    // @ts-ignore
    ElTooltip;
    // @ts-ignore
    const __VLS_556 = __VLS_asFunctionalComponent(__VLS_555, new __VLS_555({
        content: "用于匹配集数规则（用于匹配出重命名后文件名），例如：01",
        placement: "top",
    }));
    const __VLS_557 = __VLS_556({
        content: "用于匹配集数规则（用于匹配出重命名后文件名），例如：01",
        placement: "top",
    }, ...__VLS_functionalComponentArgsRest(__VLS_556));
    const { default: __VLS_559 } = __VLS_558.slots;
    const __VLS_560 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_561 = __VLS_asFunctionalComponent(__VLS_560, new __VLS_560({}));
    const __VLS_562 = __VLS_561({}, ...__VLS_functionalComponentArgsRest(__VLS_561));
    const { default: __VLS_564 } = __VLS_563.slots;
    const __VLS_565 = {}.QuestionFilled;
    /** @type {[typeof __VLS_components.QuestionFilled, typeof __VLS_components.questionFilled, ]} */ ;
    // @ts-ignore
    QuestionFilled;
    // @ts-ignore
    const __VLS_566 = __VLS_asFunctionalComponent(__VLS_565, new __VLS_565({}));
    const __VLS_567 = __VLS_566({}, ...__VLS_functionalComponentArgsRest(__VLS_566));
    var __VLS_563;
    var __VLS_558;
}
var __VLS_552;
var __VLS_547;
const __VLS_570 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_571 = __VLS_asFunctionalComponent(__VLS_570, new __VLS_570({
    label: "有效目录",
    prop: "onlyInDir",
}));
const __VLS_572 = __VLS_571({
    label: "有效目录",
    prop: "onlyInDir",
}, ...__VLS_functionalComponentArgsRest(__VLS_571));
const { default: __VLS_574 } = __VLS_573.slots;
const __VLS_575 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_576 = __VLS_asFunctionalComponent(__VLS_575, new __VLS_575({
    modelValue: (__VLS_ctx.newShare.onlyInDir),
    placeholder: "请输入有效目录（可选）",
}));
const __VLS_577 = __VLS_576({
    modelValue: (__VLS_ctx.newShare.onlyInDir),
    placeholder: "请输入有效目录（可选）",
}, ...__VLS_functionalComponentArgsRest(__VLS_576));
const { default: __VLS_579 } = __VLS_578.slots;
// @ts-ignore
[newShare,];
{
    const { append: __VLS_580 } = __VLS_578.slots;
    const __VLS_581 = {}.ElTooltip;
    /** @type {[typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, ]} */ ;
    // @ts-ignore
    ElTooltip;
    // @ts-ignore
    const __VLS_582 = __VLS_asFunctionalComponent(__VLS_581, new __VLS_581({
        content: "指定在哪个目录下搜索文件，留空则搜索整个分享",
        placement: "top",
    }));
    const __VLS_583 = __VLS_582({
        content: "指定在哪个目录下搜索文件，留空则搜索整个分享",
        placement: "top",
    }, ...__VLS_functionalComponentArgsRest(__VLS_582));
    const { default: __VLS_585 } = __VLS_584.slots;
    const __VLS_586 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_587 = __VLS_asFunctionalComponent(__VLS_586, new __VLS_586({}));
    const __VLS_588 = __VLS_587({}, ...__VLS_functionalComponentArgsRest(__VLS_587));
    const { default: __VLS_590 } = __VLS_589.slots;
    const __VLS_591 = {}.QuestionFilled;
    /** @type {[typeof __VLS_components.QuestionFilled, typeof __VLS_components.questionFilled, ]} */ ;
    // @ts-ignore
    QuestionFilled;
    // @ts-ignore
    const __VLS_592 = __VLS_asFunctionalComponent(__VLS_591, new __VLS_591({}));
    const __VLS_593 = __VLS_592({}, ...__VLS_functionalComponentArgsRest(__VLS_592));
    var __VLS_589;
    var __VLS_584;
}
var __VLS_578;
var __VLS_573;
const __VLS_596 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_597 = __VLS_asFunctionalComponent(__VLS_596, new __VLS_596({
    label: "排除目录",
    prop: "excludedDir",
}));
const __VLS_598 = __VLS_597({
    label: "排除目录",
    prop: "excludedDir",
}, ...__VLS_functionalComponentArgsRest(__VLS_597));
const { default: __VLS_600 } = __VLS_599.slots;
const __VLS_601 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_602 = __VLS_asFunctionalComponent(__VLS_601, new __VLS_601({
    ...{ 'onKeyup': {} },
    modelValue: (__VLS_ctx.excludedDirInput),
    placeholder: "请输入要排除的目录，按回车添加",
    clearable: true,
}));
const __VLS_603 = __VLS_602({
    ...{ 'onKeyup': {} },
    modelValue: (__VLS_ctx.excludedDirInput),
    placeholder: "请输入要排除的目录，按回车添加",
    clearable: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_602));
let __VLS_605;
let __VLS_606;
const __VLS_607 = ({ keyup: {} },
    { onKeyup: (__VLS_ctx.handleAddExcludedDir) });
const { default: __VLS_608 } = __VLS_604.slots;
// @ts-ignore
[excludedDirInput, handleAddExcludedDir,];
{
    const { append: __VLS_609 } = __VLS_604.slots;
    const __VLS_610 = {}.ElTooltip;
    /** @type {[typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, ]} */ ;
    // @ts-ignore
    ElTooltip;
    // @ts-ignore
    const __VLS_611 = __VLS_asFunctionalComponent(__VLS_610, new __VLS_610({
        content: "输入目录路径后按回车添加，可添加多个",
        placement: "top",
    }));
    const __VLS_612 = __VLS_611({
        content: "输入目录路径后按回车添加，可添加多个",
        placement: "top",
    }, ...__VLS_functionalComponentArgsRest(__VLS_611));
    const { default: __VLS_614 } = __VLS_613.slots;
    const __VLS_615 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_616 = __VLS_asFunctionalComponent(__VLS_615, new __VLS_615({}));
    const __VLS_617 = __VLS_616({}, ...__VLS_functionalComponentArgsRest(__VLS_616));
    const { default: __VLS_619 } = __VLS_618.slots;
    const __VLS_620 = {}.QuestionFilled;
    /** @type {[typeof __VLS_components.QuestionFilled, typeof __VLS_components.questionFilled, ]} */ ;
    // @ts-ignore
    QuestionFilled;
    // @ts-ignore
    const __VLS_621 = __VLS_asFunctionalComponent(__VLS_620, new __VLS_620({}));
    const __VLS_622 = __VLS_621({}, ...__VLS_functionalComponentArgsRest(__VLS_621));
    var __VLS_618;
    var __VLS_613;
}
var __VLS_604;
if (__VLS_ctx.newShare.excludedDir?.length) {
    // @ts-ignore
    [newShare,];
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "excluded-dir-tags" },
    });
    for (const [dir, index] of __VLS_getVForSourceType((__VLS_ctx.newShare.excludedDir))) {
        // @ts-ignore
        [newShare,];
        const __VLS_625 = {}.ElTag;
        /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
        // @ts-ignore
        ElTag;
        // @ts-ignore
        const __VLS_626 = __VLS_asFunctionalComponent(__VLS_625, new __VLS_625({
            ...{ 'onClose': {} },
            key: (index),
            closable: true,
            ...{ class: "excluded-dir-tag" },
        }));
        const __VLS_627 = __VLS_626({
            ...{ 'onClose': {} },
            key: (index),
            closable: true,
            ...{ class: "excluded-dir-tag" },
        }, ...__VLS_functionalComponentArgsRest(__VLS_626));
        let __VLS_629;
        let __VLS_630;
        const __VLS_631 = ({ close: {} },
            { onClose: (...[$event]) => {
                    if (!(__VLS_ctx.newShare.excludedDir?.length))
                        return;
                    __VLS_ctx.handleRemoveExcludedDir(index);
                    // @ts-ignore
                    [handleRemoveExcludedDir,];
                } });
        const { default: __VLS_632 } = __VLS_628.slots;
        (dir);
        var __VLS_628;
    }
}
var __VLS_599;
var __VLS_459;
{
    const { footer: __VLS_633 } = __VLS_454.slots;
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "dialog-footer" },
    });
    const __VLS_634 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_635 = __VLS_asFunctionalComponent(__VLS_634, new __VLS_634({
        ...{ 'onClick': {} },
    }));
    const __VLS_636 = __VLS_635({
        ...{ 'onClick': {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_635));
    let __VLS_638;
    let __VLS_639;
    const __VLS_640 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.addShareDialogVisible = false;
                // @ts-ignore
                [addShareDialogVisible,];
            } });
    const { default: __VLS_641 } = __VLS_637.slots;
    var __VLS_637;
    const __VLS_642 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_643 = __VLS_asFunctionalComponent(__VLS_642, new __VLS_642({
        ...{ 'onClick': {} },
        type: "primary",
    }));
    const __VLS_644 = __VLS_643({
        ...{ 'onClick': {} },
        type: "primary",
    }, ...__VLS_functionalComponentArgsRest(__VLS_643));
    let __VLS_646;
    let __VLS_647;
    const __VLS_648 = ({ click: {} },
        { onClick: (__VLS_ctx.submitShare) });
    const { default: __VLS_649 } = __VLS_645.slots;
    // @ts-ignore
    [submitShare,];
    var __VLS_645;
}
var __VLS_454;
const __VLS_650 = {}.ElDialog;
/** @type {[typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, ]} */ ;
// @ts-ignore
ElDialog;
// @ts-ignore
const __VLS_651 = __VLS_asFunctionalComponent(__VLS_650, new __VLS_650({
    modelValue: (__VLS_ctx.downloadTasksDialogVisible),
    title: (`${__VLS_ctx.currentMedia?.name || ''} - 下载任务`),
    width: "900px",
    closeOnClickModal: (false),
    ...{ class: "download-tasks-dialog" },
    alignCenter: true,
}));
const __VLS_652 = __VLS_651({
    modelValue: (__VLS_ctx.downloadTasksDialogVisible),
    title: (`${__VLS_ctx.currentMedia?.name || ''} - 下载任务`),
    width: "900px",
    closeOnClickModal: (false),
    ...{ class: "download-tasks-dialog" },
    alignCenter: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_651));
const { default: __VLS_654 } = __VLS_653.slots;
// @ts-ignore
[downloadTasksDialogVisible, currentMedia,];
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "tasks-content" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "tasks-overview" },
});
const __VLS_655 = {}.ElRow;
/** @type {[typeof __VLS_components.ElRow, typeof __VLS_components.elRow, typeof __VLS_components.ElRow, typeof __VLS_components.elRow, ]} */ ;
// @ts-ignore
ElRow;
// @ts-ignore
const __VLS_656 = __VLS_asFunctionalComponent(__VLS_655, new __VLS_655({
    gutter: (12),
}));
const __VLS_657 = __VLS_656({
    gutter: (12),
}, ...__VLS_functionalComponentArgsRest(__VLS_656));
const { default: __VLS_659 } = __VLS_658.slots;
const __VLS_660 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_661 = __VLS_asFunctionalComponent(__VLS_660, new __VLS_660({
    span: (24),
}));
const __VLS_662 = __VLS_661({
    span: (24),
}, ...__VLS_functionalComponentArgsRest(__VLS_661));
const { default: __VLS_664 } = __VLS_663.slots;
const __VLS_665 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
ElCard;
// @ts-ignore
const __VLS_666 = __VLS_asFunctionalComponent(__VLS_665, new __VLS_665({
    shadow: "hover",
    ...{ class: "status-card total" },
}));
const __VLS_667 = __VLS_666({
    shadow: "hover",
    ...{ class: "status-card total" },
}, ...__VLS_functionalComponentArgsRest(__VLS_666));
const { default: __VLS_669 } = __VLS_668.slots;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-content" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-label" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-value" },
});
(__VLS_ctx.taskStats.total);
// @ts-ignore
[taskStats,];
var __VLS_668;
var __VLS_663;
var __VLS_658;
const __VLS_670 = {}.ElRow;
/** @type {[typeof __VLS_components.ElRow, typeof __VLS_components.elRow, typeof __VLS_components.ElRow, typeof __VLS_components.elRow, ]} */ ;
// @ts-ignore
ElRow;
// @ts-ignore
const __VLS_671 = __VLS_asFunctionalComponent(__VLS_670, new __VLS_670({
    gutter: (12),
    ...{ style: {} },
}));
const __VLS_672 = __VLS_671({
    gutter: (12),
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_671));
const { default: __VLS_674 } = __VLS_673.slots;
const __VLS_675 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_676 = __VLS_asFunctionalComponent(__VLS_675, new __VLS_675({
    span: (6),
}));
const __VLS_677 = __VLS_676({
    span: (6),
}, ...__VLS_functionalComponentArgsRest(__VLS_676));
const { default: __VLS_679 } = __VLS_678.slots;
const __VLS_680 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
ElCard;
// @ts-ignore
const __VLS_681 = __VLS_asFunctionalComponent(__VLS_680, new __VLS_680({
    shadow: "hover",
    ...{ class: "status-card waiting" },
}));
const __VLS_682 = __VLS_681({
    shadow: "hover",
    ...{ class: "status-card waiting" },
}, ...__VLS_functionalComponentArgsRest(__VLS_681));
const { default: __VLS_684 } = __VLS_683.slots;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-content" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-label" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-value" },
});
(__VLS_ctx.taskStats.waiting);
// @ts-ignore
[taskStats,];
var __VLS_683;
var __VLS_678;
const __VLS_685 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_686 = __VLS_asFunctionalComponent(__VLS_685, new __VLS_685({
    span: (6),
}));
const __VLS_687 = __VLS_686({
    span: (6),
}, ...__VLS_functionalComponentArgsRest(__VLS_686));
const { default: __VLS_689 } = __VLS_688.slots;
const __VLS_690 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
ElCard;
// @ts-ignore
const __VLS_691 = __VLS_asFunctionalComponent(__VLS_690, new __VLS_690({
    shadow: "hover",
    ...{ class: "status-card downloading" },
}));
const __VLS_692 = __VLS_691({
    shadow: "hover",
    ...{ class: "status-card downloading" },
}, ...__VLS_functionalComponentArgsRest(__VLS_691));
const { default: __VLS_694 } = __VLS_693.slots;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-content" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-label" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-value" },
});
(__VLS_ctx.taskStats.downloading);
// @ts-ignore
[taskStats,];
var __VLS_693;
var __VLS_688;
const __VLS_695 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_696 = __VLS_asFunctionalComponent(__VLS_695, new __VLS_695({
    span: (6),
}));
const __VLS_697 = __VLS_696({
    span: (6),
}, ...__VLS_functionalComponentArgsRest(__VLS_696));
const { default: __VLS_699 } = __VLS_698.slots;
const __VLS_700 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
ElCard;
// @ts-ignore
const __VLS_701 = __VLS_asFunctionalComponent(__VLS_700, new __VLS_700({
    shadow: "hover",
    ...{ class: "status-card success" },
}));
const __VLS_702 = __VLS_701({
    shadow: "hover",
    ...{ class: "status-card success" },
}, ...__VLS_functionalComponentArgsRest(__VLS_701));
const { default: __VLS_704 } = __VLS_703.slots;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-content" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-label" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-value" },
});
(__VLS_ctx.taskStats.completed);
// @ts-ignore
[taskStats,];
var __VLS_703;
var __VLS_698;
const __VLS_705 = {}.ElCol;
/** @type {[typeof __VLS_components.ElCol, typeof __VLS_components.elCol, typeof __VLS_components.ElCol, typeof __VLS_components.elCol, ]} */ ;
// @ts-ignore
ElCol;
// @ts-ignore
const __VLS_706 = __VLS_asFunctionalComponent(__VLS_705, new __VLS_705({
    span: (6),
}));
const __VLS_707 = __VLS_706({
    span: (6),
}, ...__VLS_functionalComponentArgsRest(__VLS_706));
const { default: __VLS_709 } = __VLS_708.slots;
const __VLS_710 = {}.ElCard;
/** @type {[typeof __VLS_components.ElCard, typeof __VLS_components.elCard, typeof __VLS_components.ElCard, typeof __VLS_components.elCard, ]} */ ;
// @ts-ignore
ElCard;
// @ts-ignore
const __VLS_711 = __VLS_asFunctionalComponent(__VLS_710, new __VLS_710({
    shadow: "hover",
    ...{ class: "status-card error" },
}));
const __VLS_712 = __VLS_711({
    shadow: "hover",
    ...{ class: "status-card error" },
}, ...__VLS_functionalComponentArgsRest(__VLS_711));
const { default: __VLS_714 } = __VLS_713.slots;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-content" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-label" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "status-value" },
});
(__VLS_ctx.taskStats.failed);
// @ts-ignore
[taskStats,];
var __VLS_713;
var __VLS_708;
var __VLS_673;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "tasks-table-wrapper" },
});
const __VLS_715 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
ElTable;
// @ts-ignore
const __VLS_716 = __VLS_asFunctionalComponent(__VLS_715, new __VLS_715({
    data: (__VLS_ctx.downloadTasks),
    border: true,
    stripe: true,
    ...{ class: "download-tasks-table" },
    ...{ style: {} },
}));
const __VLS_717 = __VLS_716({
    data: (__VLS_ctx.downloadTasks),
    border: true,
    stripe: true,
    ...{ class: "download-tasks-table" },
    ...{ style: {} },
}, ...__VLS_functionalComponentArgsRest(__VLS_716));
const { default: __VLS_719 } = __VLS_718.slots;
// @ts-ignore
[downloadTasks,];
const __VLS_720 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_721 = __VLS_asFunctionalComponent(__VLS_720, new __VLS_720({
    prop: "gid",
    label: "任务ID",
    width: "180",
    align: "center",
}));
const __VLS_722 = __VLS_721({
    prop: "gid",
    label: "任务ID",
    width: "180",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_721));
const { default: __VLS_724 } = __VLS_723.slots;
{
    const { default: __VLS_725 } = __VLS_723.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_725);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "task-id" },
        title: (row.gid),
    });
    (row.gid);
}
var __VLS_723;
const __VLS_726 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_727 = __VLS_asFunctionalComponent(__VLS_726, new __VLS_726({
    prop: "episodeNumber",
    label: "集数",
    width: "80",
    align: "center",
}));
const __VLS_728 = __VLS_727({
    prop: "episodeNumber",
    label: "集数",
    width: "80",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_727));
const { default: __VLS_730 } = __VLS_729.slots;
{
    const { default: __VLS_731 } = __VLS_729.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_731);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "episode-number" },
    });
    (row.episodeNumber);
}
var __VLS_729;
const __VLS_732 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_733 = __VLS_asFunctionalComponent(__VLS_732, new __VLS_732({
    prop: "outName",
    label: "文件路径",
    minWidth: "300",
    showOverflowTooltip: true,
}));
const __VLS_734 = __VLS_733({
    prop: "outName",
    label: "文件路径",
    minWidth: "300",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_733));
const { default: __VLS_736 } = __VLS_735.slots;
{
    const { default: __VLS_737 } = __VLS_735.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_737);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "file-path" },
    });
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "save-path" },
    });
    (row.savePath);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "path-separator" },
    });
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "file-name" },
    });
    (row.outName);
}
var __VLS_735;
const __VLS_738 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_739 = __VLS_asFunctionalComponent(__VLS_738, new __VLS_738({
    prop: "shortName",
    label: "分辨率",
    maxWidth: "80",
    align: "center",
}));
const __VLS_740 = __VLS_739({
    prop: "shortName",
    label: "分辨率",
    maxWidth: "80",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_739));
const __VLS_743 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_744 = __VLS_asFunctionalComponent(__VLS_743, new __VLS_743({
    prop: "size",
    label: "大小",
    width: "100",
    align: "right",
}));
const __VLS_745 = __VLS_744({
    prop: "size",
    label: "大小",
    width: "100",
    align: "right",
}, ...__VLS_functionalComponentArgsRest(__VLS_744));
const { default: __VLS_747 } = __VLS_746.slots;
{
    const { default: __VLS_748 } = __VLS_746.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_748);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "file-size" },
    });
    (__VLS_ctx.formatFileSize(row.size));
    // @ts-ignore
    [formatFileSize,];
}
var __VLS_746;
const __VLS_749 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_750 = __VLS_asFunctionalComponent(__VLS_749, new __VLS_749({
    prop: "status",
    label: "状态",
    width: "100",
    align: "center",
}));
const __VLS_751 = __VLS_750({
    prop: "status",
    label: "状态",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_750));
const { default: __VLS_753 } = __VLS_752.slots;
{
    const { default: __VLS_754 } = __VLS_752.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_754);
    const __VLS_755 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    ElTag;
    // @ts-ignore
    const __VLS_756 = __VLS_asFunctionalComponent(__VLS_755, new __VLS_755({
        type: (__VLS_ctx.getStatusType(row.status)),
        ...{ class: "status-tag" },
        effect: (row.status === 1 ? 'dark' : 'plain'),
    }));
    const __VLS_757 = __VLS_756({
        type: (__VLS_ctx.getStatusType(row.status)),
        ...{ class: "status-tag" },
        effect: (row.status === 1 ? 'dark' : 'plain'),
    }, ...__VLS_functionalComponentArgsRest(__VLS_756));
    const { default: __VLS_759 } = __VLS_758.slots;
    // @ts-ignore
    [getStatusType,];
    (__VLS_ctx.getStatusText(row.status));
    // @ts-ignore
    [getStatusText,];
    var __VLS_758;
}
var __VLS_752;
var __VLS_718;
var __VLS_653;
var __VLS_3;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
/** @type {__VLS_StyleScopedClasses['header-title']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['table-container']} */ ;
/** @type {__VLS_StyleScopedClasses['data-table']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['add-media-form']} */ ;
/** @type {__VLS_StyleScopedClasses['full-width']} */ ;
/** @type {__VLS_StyleScopedClasses['option-icon']} */ ;
/** @type {__VLS_StyleScopedClasses['full-width']} */ ;
/** @type {__VLS_StyleScopedClasses['full-width']} */ ;
/** @type {__VLS_StyleScopedClasses['full-width']} */ ;
/** @type {__VLS_StyleScopedClasses['full-width']} */ ;
/** @type {__VLS_StyleScopedClasses['dialog-footer']} */ ;
/** @type {__VLS_StyleScopedClasses['share-dialog']} */ ;
/** @type {__VLS_StyleScopedClasses['share-header']} */ ;
/** @type {__VLS_StyleScopedClasses['share-title']} */ ;
/** @type {__VLS_StyleScopedClasses['share-table']} */ ;
/** @type {__VLS_StyleScopedClasses['provider-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['expired']} */ ;
/** @type {__VLS_StyleScopedClasses['only-in-dir']} */ ;
/** @type {__VLS_StyleScopedClasses['excluded-dir']} */ ;
/** @type {__VLS_StyleScopedClasses['lapse-cause']} */ ;
/** @type {__VLS_StyleScopedClasses['share-operation']} */ ;
/** @type {__VLS_StyleScopedClasses['edit-btn']} */ ;
/** @type {__VLS_StyleScopedClasses['delete-btn']} */ ;
/** @type {__VLS_StyleScopedClasses['excluded-dir-tags']} */ ;
/** @type {__VLS_StyleScopedClasses['excluded-dir-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['dialog-footer']} */ ;
/** @type {__VLS_StyleScopedClasses['download-tasks-dialog']} */ ;
/** @type {__VLS_StyleScopedClasses['tasks-content']} */ ;
/** @type {__VLS_StyleScopedClasses['tasks-overview']} */ ;
/** @type {__VLS_StyleScopedClasses['status-card']} */ ;
/** @type {__VLS_StyleScopedClasses['total']} */ ;
/** @type {__VLS_StyleScopedClasses['status-content']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-card']} */ ;
/** @type {__VLS_StyleScopedClasses['waiting']} */ ;
/** @type {__VLS_StyleScopedClasses['status-content']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-card']} */ ;
/** @type {__VLS_StyleScopedClasses['downloading']} */ ;
/** @type {__VLS_StyleScopedClasses['status-content']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-card']} */ ;
/** @type {__VLS_StyleScopedClasses['success']} */ ;
/** @type {__VLS_StyleScopedClasses['status-content']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['status-card']} */ ;
/** @type {__VLS_StyleScopedClasses['error']} */ ;
/** @type {__VLS_StyleScopedClasses['status-content']} */ ;
/** @type {__VLS_StyleScopedClasses['status-label']} */ ;
/** @type {__VLS_StyleScopedClasses['status-value']} */ ;
/** @type {__VLS_StyleScopedClasses['tasks-table-wrapper']} */ ;
/** @type {__VLS_StyleScopedClasses['download-tasks-table']} */ ;
/** @type {__VLS_StyleScopedClasses['task-id']} */ ;
/** @type {__VLS_StyleScopedClasses['episode-number']} */ ;
/** @type {__VLS_StyleScopedClasses['file-path']} */ ;
/** @type {__VLS_StyleScopedClasses['save-path']} */ ;
/** @type {__VLS_StyleScopedClasses['path-separator']} */ ;
/** @type {__VLS_StyleScopedClasses['file-name']} */ ;
/** @type {__VLS_StyleScopedClasses['file-size']} */ ;
/** @type {__VLS_StyleScopedClasses['status-tag']} */ ;
// @ts-ignore
var __VLS_161 = __VLS_160, __VLS_461 = __VLS_460;
const __VLS_export = (await import('vue')).defineComponent({});
export default {};
