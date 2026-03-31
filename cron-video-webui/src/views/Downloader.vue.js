/// <reference types="../../node_modules/.vue-global-types/vue_3.5_0.d.ts" />
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, Refresh, Edit, Delete, CircleCheck, CircleClose } from '@element-plus/icons-vue';
import { downloaderApi } from "@/api/views/downloader";
defineOptions({
    name: "Downloader"
});
onMounted(() => {
    loadConnections();
});
// 下载器列表
const downloaderList = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);
const connectionForm = ref({
    type: 0,
    protocol: '',
    host: '',
    port: 6800,
    secret: '',
    weight: 1
});
const formRules = {
    ip: [
        { required: true, message: '请输入服务器地址', trigger: 'blur' }
    ],
    port: [
        { required: true, message: '请输入端口号', trigger: 'blur' },
        { type: 'number', min: 1, max: 65535, message: '端口号范围1-65535', trigger: 'blur' }
    ],
    weight: [
        { required: true, message: '请输入权重', trigger: 'blur' },
        { type: 'number', min: 1, max: 100, message: '权重范围1-100', trigger: 'blur' }
    ]
};
const handleAddConnection = () => {
    connectionForm.value = {
        type: 0,
        protocol: '',
        host: '',
        port: 6800,
        secret: '',
        weight: 1
    };
    isEdit.value = false;
    dialogVisible.value = true;
};
const handleEdit = (row) => {
    connectionForm.value = { ...row };
    isEdit.value = true;
    dialogVisible.value = true;
};
const handleSubmit = async () => {
    if (!formRef.value)
        return;
    await formRef.value.validate(async (valid) => {
        if (!valid)
            return;
        try {
            let response;
            if (isEdit.value) {
                response = await downloaderApi.updateDownloader(connectionForm.value.id, {
                    type: connectionForm.value.type,
                    protocol: connectionForm.value.protocol,
                    host: connectionForm.value.host,
                    port: connectionForm.value.port,
                    secret: connectionForm.value.secret,
                    weight: connectionForm.value.weight
                });
            }
            else {
                response = await downloaderApi.addDownloader({
                    type: connectionForm.value.type,
                    protocol: connectionForm.value.protocol,
                    host: connectionForm.value.host,
                    port: connectionForm.value.port,
                    secret: connectionForm.value.secret,
                    weight: connectionForm.value.weight
                });
            }
            if (response.data.code === 200) {
                ElMessage.success('操作成功');
                dialogVisible.value = false;
                await loadConnections();
            }
            else {
                ElMessage.error(response.data.message || '操作失败');
            }
        }
        catch (error) {
            console.error('操作失败:', error);
            ElMessage.error('操作失败，请重试');
        }
    });
};
const handleDelete = async (row) => {
    try {
        const response = await downloaderApi.deleteDownloader(row.id);
        if (response.data.code === 200) {
            ElMessage.success('删除成功');
            await loadConnections();
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
const copySecret = async (secret) => {
    try {
        await navigator.clipboard.writeText(secret);
        ElMessage.success('密钥已复制');
    }
    catch (err) {
        ElMessage.error('复制失败');
    }
};
const downloaderType = [
    {
        value: 0,
        label: "aria2"
    },
    {
        value: 1,
        label: "qbittorrent"
    }
];
const getDownloaderType = (type) => {
    const item = downloaderType.find(item => item.value === type);
    return item ? item.label : '未知';
};
const loadConnections = async () => {
    loading.value = true;
    try {
        const response = await downloaderApi.getDownloaderList();
        if (response.data.code === 200) {
            downloaderList.value = response.data.data;
        }
        else {
            ElMessage.error(response.data.message || '加载连接列表失败');
        }
    }
    catch (error) {
        console.error('加载失败:', error);
        ElMessage.error('加载连接列表失败');
    }
    finally {
        loading.value = false;
    }
};
onMounted(() => {
    loadConnections();
});
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {
    ...{},
    ...{},
};
let __VLS_components;
let __VLS_directives;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "page-container" },
});
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
        { onClick: (__VLS_ctx.handleAddConnection) });
    const { default: __VLS_13 } = __VLS_9.slots;
    // @ts-ignore
    [handleAddConnection,];
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
        { onClick: (__VLS_ctx.loadConnections) });
    const { default: __VLS_31 } = __VLS_27.slots;
    // @ts-ignore
    [loadConnections,];
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
    data: (__VLS_ctx.downloaderList),
    border: true,
    stripe: true,
    ...{ class: "data-table" },
}));
const __VLS_44 = __VLS_43({
    data: (__VLS_ctx.downloaderList),
    border: true,
    stripe: true,
    ...{ class: "data-table" },
}, ...__VLS_functionalComponentArgsRest(__VLS_43));
const { default: __VLS_46 } = __VLS_45.slots;
// @ts-ignore
[downloaderList,];
const __VLS_47 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_48 = __VLS_asFunctionalComponent(__VLS_47, new __VLS_47({
    prop: "type",
    label: "下载器类型",
    minWidth: "140",
    align: "center",
}));
const __VLS_49 = __VLS_48({
    prop: "type",
    label: "下载器类型",
    minWidth: "140",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_48));
const { default: __VLS_51 } = __VLS_50.slots;
{
    const { default: __VLS_52 } = __VLS_50.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_52);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "tag-wrapper" },
    });
    const __VLS_53 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    ElTag;
    // @ts-ignore
    const __VLS_54 = __VLS_asFunctionalComponent(__VLS_53, new __VLS_53({
        type: "primary",
        ...{ class: "status-tag" },
    }));
    const __VLS_55 = __VLS_54({
        type: "primary",
        ...{ class: "status-tag" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_54));
    const { default: __VLS_57 } = __VLS_56.slots;
    (__VLS_ctx.getDownloaderType(row.type));
    // @ts-ignore
    [getDownloaderType,];
    var __VLS_56;
}
var __VLS_50;
const __VLS_58 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_59 = __VLS_asFunctionalComponent(__VLS_58, new __VLS_58({
    prop: "protocol",
    label: "协议",
    minWidth: "100",
    align: "center",
}));
const __VLS_60 = __VLS_59({
    prop: "protocol",
    label: "协议",
    minWidth: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_59));
const { default: __VLS_62 } = __VLS_61.slots;
{
    const { default: __VLS_63 } = __VLS_61.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_63);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "server-address" },
    });
    (row.protocol);
}
var __VLS_61;
const __VLS_64 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_65 = __VLS_asFunctionalComponent(__VLS_64, new __VLS_64({
    prop: "host",
    label: "下载器主机地址",
    minWidth: "140",
    align: "center",
}));
const __VLS_66 = __VLS_65({
    prop: "host",
    label: "下载器主机地址",
    minWidth: "140",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_65));
const { default: __VLS_68 } = __VLS_67.slots;
{
    const { default: __VLS_69 } = __VLS_67.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_69);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "server-address" },
    });
    (row.host);
}
var __VLS_67;
const __VLS_70 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_71 = __VLS_asFunctionalComponent(__VLS_70, new __VLS_70({
    prop: "port",
    label: "端口",
    minWidth: "100",
    align: "center",
}));
const __VLS_72 = __VLS_71({
    prop: "port",
    label: "端口",
    minWidth: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_71));
const { default: __VLS_74 } = __VLS_73.slots;
{
    const { default: __VLS_75 } = __VLS_73.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_75);
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "port-text" },
    });
    (row.port);
}
var __VLS_73;
const __VLS_76 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_77 = __VLS_asFunctionalComponent(__VLS_76, new __VLS_76({
    prop: "secret",
    label: "密钥",
    minWidth: "120",
    align: "center",
}));
const __VLS_78 = __VLS_77({
    prop: "secret",
    label: "密钥",
    minWidth: "120",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_77));
const { default: __VLS_80 } = __VLS_79.slots;
{
    const { default: __VLS_81 } = __VLS_79.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_81);
    if (row.secret) {
        const __VLS_82 = {}.ElPopover;
        /** @type {[typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, ]} */ ;
        // @ts-ignore
        ElPopover;
        // @ts-ignore
        const __VLS_83 = __VLS_asFunctionalComponent(__VLS_82, new __VLS_82({
            placement: "top",
            trigger: "click",
            width: (200),
            popperClass: "secret-popover",
        }));
        const __VLS_84 = __VLS_83({
            placement: "top",
            trigger: "click",
            width: (200),
            popperClass: "secret-popover",
        }, ...__VLS_functionalComponentArgsRest(__VLS_83));
        const { default: __VLS_86 } = __VLS_85.slots;
        {
            const { reference: __VLS_87 } = __VLS_85.slots;
            __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
                ...{ class: "secret-text cursor-pointer" },
            });
        }
        __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
            ...{ class: "secret-content" },
        });
        __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
            ...{ class: "secret-value" },
        });
        (row.secret);
        const __VLS_88 = {}.ElButton;
        /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
        // @ts-ignore
        ElButton;
        // @ts-ignore
        const __VLS_89 = __VLS_asFunctionalComponent(__VLS_88, new __VLS_88({
            ...{ 'onClick': {} },
            type: "primary",
            link: true,
            size: "small",
        }));
        const __VLS_90 = __VLS_89({
            ...{ 'onClick': {} },
            type: "primary",
            link: true,
            size: "small",
        }, ...__VLS_functionalComponentArgsRest(__VLS_89));
        let __VLS_92;
        let __VLS_93;
        const __VLS_94 = ({ click: {} },
            { onClick: (...[$event]) => {
                    if (!(row.secret))
                        return;
                    __VLS_ctx.copySecret(row.secret);
                    // @ts-ignore
                    [copySecret,];
                } });
        const { default: __VLS_95 } = __VLS_91.slots;
        var __VLS_91;
        var __VLS_85;
    }
    else {
        __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
            ...{ class: "no-secret" },
        });
    }
}
var __VLS_79;
const __VLS_96 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_97 = __VLS_asFunctionalComponent(__VLS_96, new __VLS_96({
    prop: "weight",
    label: "权重",
    minWidth: "100",
    align: "center",
}));
const __VLS_98 = __VLS_97({
    prop: "weight",
    label: "权重",
    minWidth: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_97));
const { default: __VLS_100 } = __VLS_99.slots;
{
    const { default: __VLS_101 } = __VLS_99.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_101);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "tag-wrapper" },
    });
    const __VLS_102 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    ElTag;
    // @ts-ignore
    const __VLS_103 = __VLS_asFunctionalComponent(__VLS_102, new __VLS_102({
        type: "warning",
        effect: "light",
        ...{ class: "weight-tag" },
        size: "large",
    }));
    const __VLS_104 = __VLS_103({
        type: "warning",
        effect: "light",
        ...{ class: "weight-tag" },
        size: "large",
    }, ...__VLS_functionalComponentArgsRest(__VLS_103));
    const { default: __VLS_106 } = __VLS_105.slots;
    (row.weight);
    var __VLS_105;
}
var __VLS_99;
const __VLS_107 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_108 = __VLS_asFunctionalComponent(__VLS_107, new __VLS_107({
    label: "状态",
    minWidth: "100",
    align: "center",
}));
const __VLS_109 = __VLS_108({
    label: "状态",
    minWidth: "100",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_108));
const { default: __VLS_111 } = __VLS_110.slots;
{
    const { default: __VLS_112 } = __VLS_110.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_112);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "tag-wrapper" },
    });
    const __VLS_113 = {}.ElTag;
    /** @type {[typeof __VLS_components.ElTag, typeof __VLS_components.elTag, typeof __VLS_components.ElTag, typeof __VLS_components.elTag, ]} */ ;
    // @ts-ignore
    ElTag;
    // @ts-ignore
    const __VLS_114 = __VLS_asFunctionalComponent(__VLS_113, new __VLS_113({
        type: (row.isOnline ? 'success' : 'danger'),
        effect: "light",
        ...{ class: "status-tag" },
        size: "large",
    }));
    const __VLS_115 = __VLS_114({
        type: (row.isOnline ? 'success' : 'danger'),
        effect: "light",
        ...{ class: "status-tag" },
        size: "large",
    }, ...__VLS_functionalComponentArgsRest(__VLS_114));
    const { default: __VLS_117 } = __VLS_116.slots;
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "status-content" },
    });
    if (row.isOnline) {
        const __VLS_118 = {}.ElIcon;
        /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
        // @ts-ignore
        ElIcon;
        // @ts-ignore
        const __VLS_119 = __VLS_asFunctionalComponent(__VLS_118, new __VLS_118({
            ...{ class: "status-icon" },
        }));
        const __VLS_120 = __VLS_119({
            ...{ class: "status-icon" },
        }, ...__VLS_functionalComponentArgsRest(__VLS_119));
        const { default: __VLS_122 } = __VLS_121.slots;
        const __VLS_123 = {}.CircleCheck;
        /** @type {[typeof __VLS_components.CircleCheck, ]} */ ;
        // @ts-ignore
        CircleCheck;
        // @ts-ignore
        const __VLS_124 = __VLS_asFunctionalComponent(__VLS_123, new __VLS_123({}));
        const __VLS_125 = __VLS_124({}, ...__VLS_functionalComponentArgsRest(__VLS_124));
        var __VLS_121;
    }
    else {
        const __VLS_128 = {}.ElIcon;
        /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
        // @ts-ignore
        ElIcon;
        // @ts-ignore
        const __VLS_129 = __VLS_asFunctionalComponent(__VLS_128, new __VLS_128({
            ...{ class: "status-icon" },
        }));
        const __VLS_130 = __VLS_129({
            ...{ class: "status-icon" },
        }, ...__VLS_functionalComponentArgsRest(__VLS_129));
        const { default: __VLS_132 } = __VLS_131.slots;
        const __VLS_133 = {}.CircleClose;
        /** @type {[typeof __VLS_components.CircleClose, ]} */ ;
        // @ts-ignore
        CircleClose;
        // @ts-ignore
        const __VLS_134 = __VLS_asFunctionalComponent(__VLS_133, new __VLS_133({}));
        const __VLS_135 = __VLS_134({}, ...__VLS_functionalComponentArgsRest(__VLS_134));
        var __VLS_131;
    }
    __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
        ...{ class: "status-text" },
    });
    (row.isOnline ? '在线' : '离线');
    var __VLS_116;
}
var __VLS_110;
const __VLS_138 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_139 = __VLS_asFunctionalComponent(__VLS_138, new __VLS_138({
    label: "操作",
    minWidth: "220",
    align: "center",
}));
const __VLS_140 = __VLS_139({
    label: "操作",
    minWidth: "220",
    align: "center",
}, ...__VLS_functionalComponentArgsRest(__VLS_139));
const { default: __VLS_142 } = __VLS_141.slots;
{
    const { default: __VLS_143 } = __VLS_141.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_143);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "connection-operation" },
    });
    const __VLS_144 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_145 = __VLS_asFunctionalComponent(__VLS_144, new __VLS_144({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
        ...{ style: {} },
    }));
    const __VLS_146 = __VLS_145({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
        ...{ style: {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_145));
    let __VLS_148;
    let __VLS_149;
    const __VLS_150 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleEdit(row);
                // @ts-ignore
                [handleEdit,];
            } });
    const { default: __VLS_151 } = __VLS_147.slots;
    const __VLS_152 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_153 = __VLS_asFunctionalComponent(__VLS_152, new __VLS_152({}));
    const __VLS_154 = __VLS_153({}, ...__VLS_functionalComponentArgsRest(__VLS_153));
    const { default: __VLS_156 } = __VLS_155.slots;
    const __VLS_157 = {}.Edit;
    /** @type {[typeof __VLS_components.Edit, ]} */ ;
    // @ts-ignore
    Edit;
    // @ts-ignore
    const __VLS_158 = __VLS_asFunctionalComponent(__VLS_157, new __VLS_157({}));
    const __VLS_159 = __VLS_158({}, ...__VLS_functionalComponentArgsRest(__VLS_158));
    var __VLS_155;
    var __VLS_147;
    const __VLS_162 = {}.ElDivider;
    /** @type {[typeof __VLS_components.ElDivider, typeof __VLS_components.elDivider, ]} */ ;
    // @ts-ignore
    ElDivider;
    // @ts-ignore
    const __VLS_163 = __VLS_asFunctionalComponent(__VLS_162, new __VLS_162({
        direction: "vertical",
    }));
    const __VLS_164 = __VLS_163({
        direction: "vertical",
    }, ...__VLS_functionalComponentArgsRest(__VLS_163));
    const __VLS_167 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_168 = __VLS_asFunctionalComponent(__VLS_167, new __VLS_167({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
        ...{ style: {} },
    }));
    const __VLS_169 = __VLS_168({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
        ...{ style: {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_168));
    let __VLS_171;
    let __VLS_172;
    const __VLS_173 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleDelete(row);
                // @ts-ignore
                [handleDelete,];
            } });
    const { default: __VLS_174 } = __VLS_170.slots;
    const __VLS_175 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_176 = __VLS_asFunctionalComponent(__VLS_175, new __VLS_175({}));
    const __VLS_177 = __VLS_176({}, ...__VLS_functionalComponentArgsRest(__VLS_176));
    const { default: __VLS_179 } = __VLS_178.slots;
    const __VLS_180 = {}.Delete;
    /** @type {[typeof __VLS_components.Delete, ]} */ ;
    // @ts-ignore
    Delete;
    // @ts-ignore
    const __VLS_181 = __VLS_asFunctionalComponent(__VLS_180, new __VLS_180({}));
    const __VLS_182 = __VLS_181({}, ...__VLS_functionalComponentArgsRest(__VLS_181));
    var __VLS_178;
    var __VLS_170;
}
var __VLS_141;
var __VLS_45;
const __VLS_185 = {}.ElDialog;
/** @type {[typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, ]} */ ;
// @ts-ignore
ElDialog;
// @ts-ignore
const __VLS_186 = __VLS_asFunctionalComponent(__VLS_185, new __VLS_185({
    title: (__VLS_ctx.isEdit ? '编辑服务' : '添加服务'),
    modelValue: (__VLS_ctx.dialogVisible),
    width: "500px",
    closeOnClickModal: (false),
    destroyOnClose: true,
}));
const __VLS_187 = __VLS_186({
    title: (__VLS_ctx.isEdit ? '编辑服务' : '添加服务'),
    modelValue: (__VLS_ctx.dialogVisible),
    width: "500px",
    closeOnClickModal: (false),
    destroyOnClose: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_186));
const { default: __VLS_189 } = __VLS_188.slots;
// @ts-ignore
[isEdit, dialogVisible,];
const __VLS_190 = {}.ElForm;
/** @type {[typeof __VLS_components.ElForm, typeof __VLS_components.elForm, typeof __VLS_components.ElForm, typeof __VLS_components.elForm, ]} */ ;
// @ts-ignore
ElForm;
// @ts-ignore
const __VLS_191 = __VLS_asFunctionalComponent(__VLS_190, new __VLS_190({
    model: (__VLS_ctx.connectionForm),
    ref: "formRef",
    labelWidth: "100px",
    rules: (__VLS_ctx.formRules),
}));
const __VLS_192 = __VLS_191({
    model: (__VLS_ctx.connectionForm),
    ref: "formRef",
    labelWidth: "100px",
    rules: (__VLS_ctx.formRules),
}, ...__VLS_functionalComponentArgsRest(__VLS_191));
/** @type {typeof __VLS_ctx.formRef} */ ;
var __VLS_194 = {};
const { default: __VLS_196 } = __VLS_193.slots;
// @ts-ignore
[connectionForm, formRules, formRef,];
const __VLS_197 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_198 = __VLS_asFunctionalComponent(__VLS_197, new __VLS_197({
    label: "下载器类型",
    prop: "type",
}));
const __VLS_199 = __VLS_198({
    label: "下载器类型",
    prop: "type",
}, ...__VLS_functionalComponentArgsRest(__VLS_198));
const { default: __VLS_201 } = __VLS_200.slots;
const __VLS_202 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
ElSelect;
// @ts-ignore
const __VLS_203 = __VLS_asFunctionalComponent(__VLS_202, new __VLS_202({
    modelValue: (__VLS_ctx.connectionForm.type),
    placeholder: "请选择下载器类型",
    ...{ class: "form-select" },
}));
const __VLS_204 = __VLS_203({
    modelValue: (__VLS_ctx.connectionForm.type),
    placeholder: "请选择下载器类型",
    ...{ class: "form-select" },
}, ...__VLS_functionalComponentArgsRest(__VLS_203));
const { default: __VLS_206 } = __VLS_205.slots;
// @ts-ignore
[connectionForm,];
for (const [item] of __VLS_getVForSourceType((__VLS_ctx.downloaderType))) {
    // @ts-ignore
    [downloaderType,];
    const __VLS_207 = {}.ElOption;
    /** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
    // @ts-ignore
    ElOption;
    // @ts-ignore
    const __VLS_208 = __VLS_asFunctionalComponent(__VLS_207, new __VLS_207({
        key: (item.value),
        label: (item.label),
        value: (item.value),
    }));
    const __VLS_209 = __VLS_208({
        key: (item.value),
        label: (item.label),
        value: (item.value),
    }, ...__VLS_functionalComponentArgsRest(__VLS_208));
}
var __VLS_205;
var __VLS_200;
const __VLS_212 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_213 = __VLS_asFunctionalComponent(__VLS_212, new __VLS_212({
    label: "协议",
    prop: "protocol",
}));
const __VLS_214 = __VLS_213({
    label: "协议",
    prop: "protocol",
}, ...__VLS_functionalComponentArgsRest(__VLS_213));
const { default: __VLS_216 } = __VLS_215.slots;
const __VLS_217 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
ElSelect;
// @ts-ignore
const __VLS_218 = __VLS_asFunctionalComponent(__VLS_217, new __VLS_217({
    modelValue: (__VLS_ctx.connectionForm.protocol),
    placeholder: "请输入协议",
    ...{ class: "form-select" },
}));
const __VLS_219 = __VLS_218({
    modelValue: (__VLS_ctx.connectionForm.protocol),
    placeholder: "请输入协议",
    ...{ class: "form-select" },
}, ...__VLS_functionalComponentArgsRest(__VLS_218));
const { default: __VLS_221 } = __VLS_220.slots;
// @ts-ignore
[connectionForm,];
const __VLS_222 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_223 = __VLS_asFunctionalComponent(__VLS_222, new __VLS_222({
    label: "ws",
    value: "ws",
}));
const __VLS_224 = __VLS_223({
    label: "ws",
    value: "ws",
}, ...__VLS_functionalComponentArgsRest(__VLS_223));
const __VLS_227 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_228 = __VLS_asFunctionalComponent(__VLS_227, new __VLS_227({
    label: "wss",
    value: "wss",
}));
const __VLS_229 = __VLS_228({
    label: "wss",
    value: "wss",
}, ...__VLS_functionalComponentArgsRest(__VLS_228));
const __VLS_232 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_233 = __VLS_asFunctionalComponent(__VLS_232, new __VLS_232({
    label: "http",
    value: "http",
}));
const __VLS_234 = __VLS_233({
    label: "http",
    value: "http",
}, ...__VLS_functionalComponentArgsRest(__VLS_233));
const __VLS_237 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_238 = __VLS_asFunctionalComponent(__VLS_237, new __VLS_237({
    label: "https",
    value: "https",
}));
const __VLS_239 = __VLS_238({
    label: "https",
    value: "https",
}, ...__VLS_functionalComponentArgsRest(__VLS_238));
var __VLS_220;
var __VLS_215;
const __VLS_242 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_243 = __VLS_asFunctionalComponent(__VLS_242, new __VLS_242({
    label: "下载器主机地址",
    prop: "host",
}));
const __VLS_244 = __VLS_243({
    label: "下载器主机地址",
    prop: "host",
}, ...__VLS_functionalComponentArgsRest(__VLS_243));
const { default: __VLS_246 } = __VLS_245.slots;
const __VLS_247 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_248 = __VLS_asFunctionalComponent(__VLS_247, new __VLS_247({
    modelValue: (__VLS_ctx.connectionForm.host),
    placeholder: "请输入下载器主机地址",
}));
const __VLS_249 = __VLS_248({
    modelValue: (__VLS_ctx.connectionForm.host),
    placeholder: "请输入下载器主机地址",
}, ...__VLS_functionalComponentArgsRest(__VLS_248));
// @ts-ignore
[connectionForm,];
var __VLS_245;
const __VLS_252 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_253 = __VLS_asFunctionalComponent(__VLS_252, new __VLS_252({
    label: "端口",
    prop: "port",
}));
const __VLS_254 = __VLS_253({
    label: "端口",
    prop: "port",
}, ...__VLS_functionalComponentArgsRest(__VLS_253));
const { default: __VLS_256 } = __VLS_255.slots;
const __VLS_257 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
ElInputNumber;
// @ts-ignore
const __VLS_258 = __VLS_asFunctionalComponent(__VLS_257, new __VLS_257({
    modelValue: (__VLS_ctx.connectionForm.port),
    min: (1),
    max: (65535),
    ...{ class: "port-input" },
}));
const __VLS_259 = __VLS_258({
    modelValue: (__VLS_ctx.connectionForm.port),
    min: (1),
    max: (65535),
    ...{ class: "port-input" },
}, ...__VLS_functionalComponentArgsRest(__VLS_258));
// @ts-ignore
[connectionForm,];
var __VLS_255;
const __VLS_262 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_263 = __VLS_asFunctionalComponent(__VLS_262, new __VLS_262({
    label: "密钥",
    prop: "secret",
}));
const __VLS_264 = __VLS_263({
    label: "密钥",
    prop: "secret",
}, ...__VLS_functionalComponentArgsRest(__VLS_263));
const { default: __VLS_266 } = __VLS_265.slots;
const __VLS_267 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_268 = __VLS_asFunctionalComponent(__VLS_267, new __VLS_267({
    modelValue: (__VLS_ctx.connectionForm.secret),
    placeholder: "请输入RPC密钥（可选）",
    type: "password",
    showPassword: true,
}));
const __VLS_269 = __VLS_268({
    modelValue: (__VLS_ctx.connectionForm.secret),
    placeholder: "请输入RPC密钥（可选）",
    type: "password",
    showPassword: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_268));
// @ts-ignore
[connectionForm,];
var __VLS_265;
const __VLS_272 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_273 = __VLS_asFunctionalComponent(__VLS_272, new __VLS_272({
    label: "权重",
    prop: "weight",
}));
const __VLS_274 = __VLS_273({
    label: "权重",
    prop: "weight",
}, ...__VLS_functionalComponentArgsRest(__VLS_273));
const { default: __VLS_276 } = __VLS_275.slots;
const __VLS_277 = {}.ElInputNumber;
/** @type {[typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, typeof __VLS_components.ElInputNumber, typeof __VLS_components.elInputNumber, ]} */ ;
// @ts-ignore
ElInputNumber;
// @ts-ignore
const __VLS_278 = __VLS_asFunctionalComponent(__VLS_277, new __VLS_277({
    modelValue: (__VLS_ctx.connectionForm.weight),
    min: (1),
    max: (100),
    ...{ class: "weight-input" },
}));
const __VLS_279 = __VLS_278({
    modelValue: (__VLS_ctx.connectionForm.weight),
    min: (1),
    max: (100),
    ...{ class: "weight-input" },
}, ...__VLS_functionalComponentArgsRest(__VLS_278));
// @ts-ignore
[connectionForm,];
var __VLS_275;
var __VLS_193;
{
    const { footer: __VLS_282 } = __VLS_188.slots;
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "dialog-footer" },
    });
    const __VLS_283 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_284 = __VLS_asFunctionalComponent(__VLS_283, new __VLS_283({
        ...{ 'onClick': {} },
    }));
    const __VLS_285 = __VLS_284({
        ...{ 'onClick': {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_284));
    let __VLS_287;
    let __VLS_288;
    const __VLS_289 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.dialogVisible = false;
                // @ts-ignore
                [dialogVisible,];
            } });
    const { default: __VLS_290 } = __VLS_286.slots;
    var __VLS_286;
    const __VLS_291 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_292 = __VLS_asFunctionalComponent(__VLS_291, new __VLS_291({
        ...{ 'onClick': {} },
        type: "primary",
    }));
    const __VLS_293 = __VLS_292({
        ...{ 'onClick': {} },
        type: "primary",
    }, ...__VLS_functionalComponentArgsRest(__VLS_292));
    let __VLS_295;
    let __VLS_296;
    const __VLS_297 = ({ click: {} },
        { onClick: (__VLS_ctx.handleSubmit) });
    const { default: __VLS_298 } = __VLS_294.slots;
    // @ts-ignore
    [handleSubmit,];
    var __VLS_294;
}
var __VLS_188;
var __VLS_3;
/** @type {__VLS_StyleScopedClasses['page-container']} */ ;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
/** @type {__VLS_StyleScopedClasses['header-title']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['table-container']} */ ;
/** @type {__VLS_StyleScopedClasses['data-table']} */ ;
/** @type {__VLS_StyleScopedClasses['tag-wrapper']} */ ;
/** @type {__VLS_StyleScopedClasses['status-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['server-address']} */ ;
/** @type {__VLS_StyleScopedClasses['server-address']} */ ;
/** @type {__VLS_StyleScopedClasses['port-text']} */ ;
/** @type {__VLS_StyleScopedClasses['secret-text']} */ ;
/** @type {__VLS_StyleScopedClasses['cursor-pointer']} */ ;
/** @type {__VLS_StyleScopedClasses['secret-content']} */ ;
/** @type {__VLS_StyleScopedClasses['secret-value']} */ ;
/** @type {__VLS_StyleScopedClasses['no-secret']} */ ;
/** @type {__VLS_StyleScopedClasses['tag-wrapper']} */ ;
/** @type {__VLS_StyleScopedClasses['weight-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['tag-wrapper']} */ ;
/** @type {__VLS_StyleScopedClasses['status-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['status-content']} */ ;
/** @type {__VLS_StyleScopedClasses['status-icon']} */ ;
/** @type {__VLS_StyleScopedClasses['status-icon']} */ ;
/** @type {__VLS_StyleScopedClasses['status-text']} */ ;
/** @type {__VLS_StyleScopedClasses['connection-operation']} */ ;
/** @type {__VLS_StyleScopedClasses['form-select']} */ ;
/** @type {__VLS_StyleScopedClasses['form-select']} */ ;
/** @type {__VLS_StyleScopedClasses['port-input']} */ ;
/** @type {__VLS_StyleScopedClasses['weight-input']} */ ;
/** @type {__VLS_StyleScopedClasses['dialog-footer']} */ ;
// @ts-ignore
var __VLS_195 = __VLS_194;
const __VLS_export = (await import('vue')).defineComponent({});
export default {};
