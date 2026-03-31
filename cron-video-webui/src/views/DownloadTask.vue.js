/// <reference types="../../node_modules/.vue-global-types/vue_3.5_0.d.ts" />
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Refresh, Search, Delete, VideoPause, RefreshRight } from '@element-plus/icons-vue';
import { aria2DownloadTaskApi } from "@/api/views/aria2_download_task";
defineOptions({
    name: "DownloadTask"
});
const aria2DownloadTaskPageList = ref([]);
const loading = ref(false);
const total = ref(0);
const selectedTasks = ref([]);
// 查询参数
const queryParams = ref({
    page: 1,
    pageSize: 10,
    name: '',
    status: undefined
});
const getStatusText = (status) => {
    const statusMap = {
        0: '等待中',
        1: '下载中',
        2: '已完成',
        3: '失败'
    };
    return statusMap[status] || '未知';
};
const getStatusType = (status) => {
    const typeMap = {
        0: 'info',
        1: 'warning',
        2: 'success',
        3: 'danger'
    };
    return typeMap[status] || 'info';
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
// 获取分页列表
const loadDownloads = async () => {
    loading.value = true;
    try {
        const response = await aria2DownloadTaskApi.getTasks({
            page: queryParams.value.page,
            pageSize: queryParams.value.pageSize,
            name: queryParams.value.name || "",
            status: queryParams.value.status
        });
        if (response.data.code === 200) {
            aria2DownloadTaskPageList.value = response.data.data.list;
            total.value = response.data.data.total;
        }
        else {
            ElMessage.error(response.data.message || '加载下载任务列表失败');
        }
    }
    catch (error) {
        console.error('加载失败:', error);
        ElMessage.error('加载下载任务列表失败');
    }
    finally {
        loading.value = false;
    }
};
const handleSearch = () => {
    queryParams.value.page = 1;
    loadDownloads();
};
const handleSizeChange = (val) => {
    queryParams.value.pageSize = val;
    loadDownloads();
};
const handleCurrentChange = (val) => {
    queryParams.value.page = val;
    loadDownloads();
};
const handleRetry = async (task) => {
    try {
        const response = await aria2DownloadTaskApi.updateTask(task.id, { status: 0 });
        if (response.data.code === 200) {
            ElMessage.success('重试任务已添加');
            await loadDownloads();
        }
        else {
            ElMessage.error(response.data.message || '重试失败');
        }
    }
    catch (error) {
        console.error('重试失败:', error);
        ElMessage.error('重试失败');
    }
};
const handleDelete = async (id) => {
    try {
        const response = await aria2DownloadTaskApi.deleteTask(id);
        if (response.data.code === 200) {
            ElMessage.success('删除成功');
            await loadDownloads();
        }
        else {
            ElMessage.error(response.data.message || '删除失败');
        }
    }
    catch (error) {
        console.error('删除失败:', error);
        ElMessage.error('删除失败');
    }
};
const handleSelectionChange = (selection) => {
    selectedTasks.value = selection;
};
const handleBatchDelete = async () => {
    if (!selectedTasks.value.length)
        return;
    try {
        await ElMessageBox.confirm(`确定要删除选中的 ${selectedTasks.value.length} 个任务吗？`, '批量删除', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        const deletePromises = selectedTasks.value.map((task) => aria2DownloadTaskApi.deleteTask(task.id));
        await Promise.all(deletePromises);
        ElMessage.success('批量删除成功');
        await loadDownloads();
    }
    catch (error) {
        if (error !== 'cancel') {
            console.error('批量删除失败:', error);
            ElMessage.error('批量删除失败');
        }
    }
};
const handlePause = (task) => {
    // 暂停
    console.log(task);
};
onMounted(() => {
    loadDownloads();
});
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {
    ...{},
    ...{},
};
let __VLS_components;
let __VLS_directives;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['table-container']} */ ;
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
    const __VLS_6 = {}.ElInput;
    /** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
    // @ts-ignore
    ElInput;
    // @ts-ignore
    const __VLS_7 = __VLS_asFunctionalComponent(__VLS_6, new __VLS_6({
        ...{ 'onClear': {} },
        ...{ 'onKeyup': {} },
        modelValue: (__VLS_ctx.queryParams.name),
        placeholder: "搜索影视标题",
        ...{ class: "search-input" },
        clearable: true,
    }));
    const __VLS_8 = __VLS_7({
        ...{ 'onClear': {} },
        ...{ 'onKeyup': {} },
        modelValue: (__VLS_ctx.queryParams.name),
        placeholder: "搜索影视标题",
        ...{ class: "search-input" },
        clearable: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_7));
    let __VLS_10;
    let __VLS_11;
    const __VLS_12 = ({ clear: {} },
        { onClear: (__VLS_ctx.handleSearch) });
    const __VLS_13 = ({ keyup: {} },
        { onKeyup: (__VLS_ctx.handleSearch) });
    const { default: __VLS_14 } = __VLS_9.slots;
    // @ts-ignore
    [queryParams, handleSearch, handleSearch,];
    {
        const { prefix: __VLS_15 } = __VLS_9.slots;
        const __VLS_16 = {}.ElIcon;
        /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
        // @ts-ignore
        ElIcon;
        // @ts-ignore
        const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({}));
        const __VLS_18 = __VLS_17({}, ...__VLS_functionalComponentArgsRest(__VLS_17));
        const { default: __VLS_20 } = __VLS_19.slots;
        const __VLS_21 = {}.Search;
        /** @type {[typeof __VLS_components.Search, ]} */ ;
        // @ts-ignore
        Search;
        // @ts-ignore
        const __VLS_22 = __VLS_asFunctionalComponent(__VLS_21, new __VLS_21({}));
        const __VLS_23 = __VLS_22({}, ...__VLS_functionalComponentArgsRest(__VLS_22));
        var __VLS_19;
    }
    var __VLS_9;
    const __VLS_26 = {}.ElSelect;
    /** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
    // @ts-ignore
    ElSelect;
    // @ts-ignore
    const __VLS_27 = __VLS_asFunctionalComponent(__VLS_26, new __VLS_26({
        ...{ 'onChange': {} },
        modelValue: (__VLS_ctx.queryParams.status),
        placeholder: "状态筛选",
        clearable: true,
        ...{ class: "status-select" },
    }));
    const __VLS_28 = __VLS_27({
        ...{ 'onChange': {} },
        modelValue: (__VLS_ctx.queryParams.status),
        placeholder: "状态筛选",
        clearable: true,
        ...{ class: "status-select" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_27));
    let __VLS_30;
    let __VLS_31;
    const __VLS_32 = ({ change: {} },
        { onChange: (__VLS_ctx.handleSearch) });
    const { default: __VLS_33 } = __VLS_29.slots;
    // @ts-ignore
    [queryParams, handleSearch,];
    const __VLS_34 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    ElOption;
    // @ts-ignore
    const __VLS_35 = __VLS_asFunctionalComponent(__VLS_34, new __VLS_34({
        label: "等待中",
        value: (0),
    }));
    const __VLS_36 = __VLS_35({
        label: "等待中",
        value: (0),
    }, ...__VLS_functionalComponentArgsRest(__VLS_35));
    const __VLS_39 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    ElOption;
    // @ts-ignore
    const __VLS_40 = __VLS_asFunctionalComponent(__VLS_39, new __VLS_39({
        label: "下载中",
        value: (1),
    }));
    const __VLS_41 = __VLS_40({
        label: "下载中",
        value: (1),
    }, ...__VLS_functionalComponentArgsRest(__VLS_40));
    const __VLS_44 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    ElOption;
    // @ts-ignore
    const __VLS_45 = __VLS_asFunctionalComponent(__VLS_44, new __VLS_44({
        label: "已完成",
        value: (2),
    }));
    const __VLS_46 = __VLS_45({
        label: "已完成",
        value: (2),
    }, ...__VLS_functionalComponentArgsRest(__VLS_45));
    const __VLS_49 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    ElOption;
    // @ts-ignore
    const __VLS_50 = __VLS_asFunctionalComponent(__VLS_49, new __VLS_49({
        label: "已失败",
        value: (3),
    }));
    const __VLS_51 = __VLS_50({
        label: "已失败",
        value: (3),
    }, ...__VLS_functionalComponentArgsRest(__VLS_50));
    var __VLS_29;
    const __VLS_54 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_55 = __VLS_asFunctionalComponent(__VLS_54, new __VLS_54({
        ...{ 'onClick': {} },
        type: "danger",
        disabled: (!__VLS_ctx.selectedTasks.length),
        plain: true,
    }));
    const __VLS_56 = __VLS_55({
        ...{ 'onClick': {} },
        type: "danger",
        disabled: (!__VLS_ctx.selectedTasks.length),
        plain: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_55));
    let __VLS_58;
    let __VLS_59;
    const __VLS_60 = ({ click: {} },
        { onClick: (__VLS_ctx.handleBatchDelete) });
    const { default: __VLS_61 } = __VLS_57.slots;
    // @ts-ignore
    [selectedTasks, handleBatchDelete,];
    const __VLS_62 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_63 = __VLS_asFunctionalComponent(__VLS_62, new __VLS_62({}));
    const __VLS_64 = __VLS_63({}, ...__VLS_functionalComponentArgsRest(__VLS_63));
    const { default: __VLS_66 } = __VLS_65.slots;
    const __VLS_67 = {}.Delete;
    /** @type {[typeof __VLS_components.Delete, ]} */ ;
    // @ts-ignore
    Delete;
    // @ts-ignore
    const __VLS_68 = __VLS_asFunctionalComponent(__VLS_67, new __VLS_67({}));
    const __VLS_69 = __VLS_68({}, ...__VLS_functionalComponentArgsRest(__VLS_68));
    var __VLS_65;
    var __VLS_57;
    const __VLS_72 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_73 = __VLS_asFunctionalComponent(__VLS_72, new __VLS_72({
        ...{ 'onClick': {} },
        type: "primary",
        loading: (__VLS_ctx.loading),
        plain: true,
    }));
    const __VLS_74 = __VLS_73({
        ...{ 'onClick': {} },
        type: "primary",
        loading: (__VLS_ctx.loading),
        plain: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_73));
    let __VLS_76;
    let __VLS_77;
    const __VLS_78 = ({ click: {} },
        { onClick: (__VLS_ctx.loadDownloads) });
    const { default: __VLS_79 } = __VLS_75.slots;
    // @ts-ignore
    [loading, loadDownloads,];
    const __VLS_80 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_81 = __VLS_asFunctionalComponent(__VLS_80, new __VLS_80({}));
    const __VLS_82 = __VLS_81({}, ...__VLS_functionalComponentArgsRest(__VLS_81));
    const { default: __VLS_84 } = __VLS_83.slots;
    const __VLS_85 = {}.Refresh;
    /** @type {[typeof __VLS_components.Refresh, ]} */ ;
    // @ts-ignore
    Refresh;
    // @ts-ignore
    const __VLS_86 = __VLS_asFunctionalComponent(__VLS_85, new __VLS_85({}));
    const __VLS_87 = __VLS_86({}, ...__VLS_functionalComponentArgsRest(__VLS_86));
    var __VLS_83;
    var __VLS_75;
}
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "table-container" },
});
const __VLS_90 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
ElTable;
// @ts-ignore
const __VLS_91 = __VLS_asFunctionalComponent(__VLS_90, new __VLS_90({
    ...{ 'onSelectionChange': {} },
    data: (__VLS_ctx.aria2DownloadTaskPageList),
    border: true,
    stripe: true,
}));
const __VLS_92 = __VLS_91({
    ...{ 'onSelectionChange': {} },
    data: (__VLS_ctx.aria2DownloadTaskPageList),
    border: true,
    stripe: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_91));
let __VLS_94;
let __VLS_95;
const __VLS_96 = ({ selectionChange: {} },
    { onSelectionChange: (__VLS_ctx.handleSelectionChange) });
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.loading) }, null, null);
const { default: __VLS_97 } = __VLS_93.slots;
// @ts-ignore
[loading, aria2DownloadTaskPageList, handleSelectionChange, vLoading,];
const __VLS_98 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_99 = __VLS_asFunctionalComponent(__VLS_98, new __VLS_98({
    type: "selection",
    width: "50",
    align: "center",
}));
const __VLS_100 = __VLS_99({
    type: "selection",
    width: "50",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_99));
const __VLS_103 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_104 = __VLS_asFunctionalComponent(__VLS_103, new __VLS_103({
    label: "媒体名称",
    prop: "name",
    showOverflowTooltip: true,
    align: "center",
}));
const __VLS_105 = __VLS_104({
    label: "媒体名称",
    prop: "name",
    showOverflowTooltip: true,
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_104));
const { default: __VLS_107 } = __VLS_106.slots;
{
    const { default: __VLS_108 } = __VLS_106.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_108);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "media-title" },
    });
    (row.name);
}
var __VLS_106;
const __VLS_109 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_110 = __VLS_asFunctionalComponent(__VLS_109, new __VLS_109({
    prop: "gid",
    label: "任务ID",
    width: "180",
    align: "center",
}));
const __VLS_111 = __VLS_110({
    prop: "gid",
    label: "任务ID",
    width: "180",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_110));
const { default: __VLS_113 } = __VLS_112.slots;
{
    const { default: __VLS_114 } = __VLS_112.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_114);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "task-id" },
        title: (row.gid),
    });
    (row.gid);
}
var __VLS_112;
const __VLS_115 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_116 = __VLS_asFunctionalComponent(__VLS_115, new __VLS_115({
    prop: "episodeNumber",
    label: "集数",
    width: "80",
    align: "center",
}));
const __VLS_117 = __VLS_116({
    prop: "episodeNumber",
    label: "集数",
    width: "80",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_116));
const { default: __VLS_119 } = __VLS_118.slots;
{
    const { default: __VLS_120 } = __VLS_118.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_120);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "episode-number" },
    });
    (row.episodeNumber);
}
var __VLS_118;
const __VLS_121 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_122 = __VLS_asFunctionalComponent(__VLS_121, new __VLS_121({
    prop: "outName",
    label: "文件路径",
    minWidth: "300",
    showOverflowTooltip: true,
}));
const __VLS_123 = __VLS_122({
    prop: "outName",
    label: "文件路径",
    minWidth: "300",
    showOverflowTooltip: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_122));
const { default: __VLS_125 } = __VLS_124.slots;
{
    const { default: __VLS_126 } = __VLS_124.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_126);
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
var __VLS_124;
const __VLS_127 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_128 = __VLS_asFunctionalComponent(__VLS_127, new __VLS_127({
    prop: "shortName",
    label: "分辨率",
    width: "120",
    align: "center",
}));
const __VLS_129 = __VLS_128({
    prop: "shortName",
    label: "分辨率",
    width: "120",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_128));
const __VLS_132 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_133 = __VLS_asFunctionalComponent(__VLS_132, new __VLS_132({
    label: "文件大小",
    width: "120",
    align: "center",
}));
const __VLS_134 = __VLS_133({
    label: "文件大小",
    width: "120",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_133));
const { default: __VLS_136 } = __VLS_135.slots;
{
    const { default: __VLS_137 } = __VLS_135.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_137);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "monospace-text" },
    });
    (__VLS_ctx.formatFileSize(row.size));
    // @ts-ignore
    [formatFileSize,];
}
var __VLS_135;
const __VLS_138 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_139 = __VLS_asFunctionalComponent(__VLS_138, new __VLS_138({
    label: "状态",
    width: "100",
    align: "center",
}));
const __VLS_140 = __VLS_139({
    label: "状态",
    width: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_139));
const { default: __VLS_142 } = __VLS_141.slots;
{
    const { default: __VLS_143 } = __VLS_141.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_143);
    const __VLS_144 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    ElTag;
    // @ts-ignore
    const __VLS_145 = __VLS_asFunctionalComponent(__VLS_144, new __VLS_144({
        type: (__VLS_ctx.getStatusType(row.status)),
        ...{ class: "status-tag" },
    }));
    const __VLS_146 = __VLS_145({
        type: (__VLS_ctx.getStatusType(row.status)),
        ...{ class: "status-tag" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_145));
    const { default: __VLS_148 } = __VLS_147.slots;
    // @ts-ignore
    [getStatusType,];
    (__VLS_ctx.getStatusText(row.status));
    // @ts-ignore
    [getStatusText,];
    var __VLS_147;
}
var __VLS_141;
const __VLS_149 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_150 = __VLS_asFunctionalComponent(__VLS_149, new __VLS_149({
    label: "操作",
    width: "360",
    align: "center",
    fixed: "right",
}));
const __VLS_151 = __VLS_150({
    label: "操作",
    width: "360",
    align: "center",
    fixed: "right",
}, ...__VLS_functionalComponentArgsRest(__VLS_150));
const { default: __VLS_153 } = __VLS_152.slots;
{
    const { default: __VLS_154 } = __VLS_152.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_154);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "operation-buttons" },
    });
    const __VLS_155 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_156 = __VLS_asFunctionalComponent(__VLS_155, new __VLS_155({
        ...{ 'onClick': {} },
        disabled: (row.status !== 1),
        type: "warning",
        text: true,
    }));
    const __VLS_157 = __VLS_156({
        ...{ 'onClick': {} },
        disabled: (row.status !== 1),
        type: "warning",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_156));
    let __VLS_159;
    let __VLS_160;
    const __VLS_161 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handlePause(row);
                // @ts-ignore
                [handlePause,];
            } });
    const { default: __VLS_162 } = __VLS_158.slots;
    const __VLS_163 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_164 = __VLS_asFunctionalComponent(__VLS_163, new __VLS_163({}));
    const __VLS_165 = __VLS_164({}, ...__VLS_functionalComponentArgsRest(__VLS_164));
    const { default: __VLS_167 } = __VLS_166.slots;
    const __VLS_168 = {}.VideoPause;
    /** @type {[typeof __VLS_components.VideoPause, ]} */ ;
    // @ts-ignore
    VideoPause;
    // @ts-ignore
    const __VLS_169 = __VLS_asFunctionalComponent(__VLS_168, new __VLS_168({}));
    const __VLS_170 = __VLS_169({}, ...__VLS_functionalComponentArgsRest(__VLS_169));
    var __VLS_166;
    var __VLS_158;
    const __VLS_173 = {}.ElDivider;
    /** @type {[typeof __VLS_components.ElDivider, typeof __VLS_components.elDivider, ]} */ ;
    // @ts-ignore
    ElDivider;
    // @ts-ignore
    const __VLS_174 = __VLS_asFunctionalComponent(__VLS_173, new __VLS_173({
        direction: "vertical",
    }));
    const __VLS_175 = __VLS_174({
        direction: "vertical",
    }, ...__VLS_functionalComponentArgsRest(__VLS_174));
    const __VLS_178 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_179 = __VLS_asFunctionalComponent(__VLS_178, new __VLS_178({
        ...{ 'onClick': {} },
        disabled: (row.status !== 3),
        type: "primary",
        text: true,
    }));
    const __VLS_180 = __VLS_179({
        ...{ 'onClick': {} },
        disabled: (row.status !== 3),
        type: "primary",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_179));
    let __VLS_182;
    let __VLS_183;
    const __VLS_184 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleRetry(row);
                // @ts-ignore
                [handleRetry,];
            } });
    const { default: __VLS_185 } = __VLS_181.slots;
    const __VLS_186 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_187 = __VLS_asFunctionalComponent(__VLS_186, new __VLS_186({}));
    const __VLS_188 = __VLS_187({}, ...__VLS_functionalComponentArgsRest(__VLS_187));
    const { default: __VLS_190 } = __VLS_189.slots;
    const __VLS_191 = {}.RefreshRight;
    /** @type {[typeof __VLS_components.RefreshRight, ]} */ ;
    // @ts-ignore
    RefreshRight;
    // @ts-ignore
    const __VLS_192 = __VLS_asFunctionalComponent(__VLS_191, new __VLS_191({}));
    const __VLS_193 = __VLS_192({}, ...__VLS_functionalComponentArgsRest(__VLS_192));
    var __VLS_189;
    var __VLS_181;
    const __VLS_196 = {}.ElDivider;
    /** @type {[typeof __VLS_components.ElDivider, typeof __VLS_components.elDivider, ]} */ ;
    // @ts-ignore
    ElDivider;
    // @ts-ignore
    const __VLS_197 = __VLS_asFunctionalComponent(__VLS_196, new __VLS_196({
        direction: "vertical",
    }));
    const __VLS_198 = __VLS_197({
        direction: "vertical",
    }, ...__VLS_functionalComponentArgsRest(__VLS_197));
    const __VLS_201 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_202 = __VLS_asFunctionalComponent(__VLS_201, new __VLS_201({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
    }));
    const __VLS_203 = __VLS_202({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_202));
    let __VLS_205;
    let __VLS_206;
    const __VLS_207 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleDelete(row.id);
                // @ts-ignore
                [handleDelete,];
            } });
    const { default: __VLS_208 } = __VLS_204.slots;
    const __VLS_209 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_210 = __VLS_asFunctionalComponent(__VLS_209, new __VLS_209({}));
    const __VLS_211 = __VLS_210({}, ...__VLS_functionalComponentArgsRest(__VLS_210));
    const { default: __VLS_213 } = __VLS_212.slots;
    const __VLS_214 = {}.Delete;
    /** @type {[typeof __VLS_components.Delete, ]} */ ;
    // @ts-ignore
    Delete;
    // @ts-ignore
    const __VLS_215 = __VLS_asFunctionalComponent(__VLS_214, new __VLS_214({}));
    const __VLS_216 = __VLS_215({}, ...__VLS_functionalComponentArgsRest(__VLS_215));
    var __VLS_212;
    var __VLS_204;
}
var __VLS_152;
var __VLS_93;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "pagination-container" },
});
const __VLS_219 = {}.ElPagination;
/** @type {[typeof __VLS_components.ElPagination, typeof __VLS_components.elPagination, ]} */ ;
// @ts-ignore
ElPagination;
// @ts-ignore
const __VLS_220 = __VLS_asFunctionalComponent(__VLS_219, new __VLS_219({
    ...{ 'onSizeChange': {} },
    ...{ 'onCurrentChange': {} },
    currentPage: (__VLS_ctx.queryParams.page),
    pageSize: (__VLS_ctx.queryParams.pageSize),
    pageSizes: ([10, 20, 50, 100]),
    total: (__VLS_ctx.total),
    layout: "total, sizes, prev, pager, next, jumper",
}));
const __VLS_221 = __VLS_220({
    ...{ 'onSizeChange': {} },
    ...{ 'onCurrentChange': {} },
    currentPage: (__VLS_ctx.queryParams.page),
    pageSize: (__VLS_ctx.queryParams.pageSize),
    pageSizes: ([10, 20, 50, 100]),
    total: (__VLS_ctx.total),
    layout: "total, sizes, prev, pager, next, jumper",
}, ...__VLS_functionalComponentArgsRest(__VLS_220));
let __VLS_223;
let __VLS_224;
const __VLS_225 = ({ sizeChange: {} },
    { onSizeChange: (__VLS_ctx.handleSizeChange) });
const __VLS_226 = ({ currentChange: {} },
    { onCurrentChange: (__VLS_ctx.handleCurrentChange) });
// @ts-ignore
[queryParams, queryParams, total, handleSizeChange, handleCurrentChange,];
var __VLS_222;
var __VLS_3;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
/** @type {__VLS_StyleScopedClasses['header-title']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['search-input']} */ ;
/** @type {__VLS_StyleScopedClasses['status-select']} */ ;
/** @type {__VLS_StyleScopedClasses['table-container']} */ ;
/** @type {__VLS_StyleScopedClasses['media-title']} */ ;
/** @type {__VLS_StyleScopedClasses['task-id']} */ ;
/** @type {__VLS_StyleScopedClasses['episode-number']} */ ;
/** @type {__VLS_StyleScopedClasses['file-path']} */ ;
/** @type {__VLS_StyleScopedClasses['save-path']} */ ;
/** @type {__VLS_StyleScopedClasses['path-separator']} */ ;
/** @type {__VLS_StyleScopedClasses['file-name']} */ ;
/** @type {__VLS_StyleScopedClasses['monospace-text']} */ ;
/** @type {__VLS_StyleScopedClasses['status-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['pagination-container']} */ ;
const __VLS_export = (await import('vue')).defineComponent({});
export default {};
