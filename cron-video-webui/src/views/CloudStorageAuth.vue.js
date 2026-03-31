/// <reference types="../../node_modules/.vue-global-types/vue_3.5_0.d.ts" />
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, Refresh, Edit, Delete } from '@element-plus/icons-vue';
import { cloudStorageAuthApi } from "@/api/views/cloud_storage_auth";
defineOptions({
    name: "CloudStorageAuth"
});
const authList = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const authFormRef = ref(null);
const authForm = ref({
    provider: 1,
    cookie: '',
    access_token: null,
    refresh_token: null
});
const showAccessToken = computed(() => authForm.value.provider !== 1);
const showRefreshToken = computed(() => authForm.value.provider !== 1);
// 修改表单验证规则
const formRules = {
    provider: [
        { required: true, message: '请选择网盘类型', trigger: 'change' }
    ],
    cookie: [
        { required: true, message: '请输入Cookie', trigger: 'blur' }
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
};
const getProviderName = (provider) => {
    const providers = ['夸克', '阿里云盘', '百度网盘'];
    return providers[provider - 1] || '未知';
};
const getProviderTagType = (provider) => {
    const types = ['', 'success', 'info', 'warning'];
    return types[provider] || 'info';
};
const loadAuths = async () => {
    loading.value = true;
    try {
        const response = await cloudStorageAuthApi.getAuths();
        if (response.data.code === 200) {
            authList.value = response.data.data;
        }
        else {
            ElMessage.error(response.data.message || '加载认证信息失败');
        }
    }
    catch (error) {
        console.error('加载失败:', error);
        ElMessage.error('加载认证信息失败');
    }
    finally {
        loading.value = false;
    }
};
const handleAddAuth = () => {
    authForm.value = {
        provider: 1,
        cookie: '',
        access_token: null,
        refresh_token: null,
        id: undefined
    };
    isEdit.value = false;
    dialogVisible.value = true;
};
const handleEdit = (row) => {
    authForm.value = { ...row };
    isEdit.value = true;
    dialogVisible.value = true;
};
const handleSubmit = async () => {
    if (!authFormRef.value)
        return;
    await authFormRef.value.validate(async (valid) => {
        if (!valid)
            return;
        try {
            let response;
            const requestData = {
                provider: authForm.value.provider,
                cookie: authForm.value.cookie,
                access_token: authForm.value.access_token || '',
                refresh_token: authForm.value.refresh_token || ''
            };
            if (isEdit.value) {
                response = await cloudStorageAuthApi.updateAuth(authForm.value.id, requestData);
            }
            else {
                response = await cloudStorageAuthApi.addAuth(requestData);
            }
            if (response.data.code === 200) {
                ElMessage.success('操作成功');
                dialogVisible.value = false;
                await loadAuths();
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
const handleDelete = async (id) => {
    try {
        const response = await cloudStorageAuthApi.deleteAuth(id);
        if (response.data.code === 200) {
            ElMessage.success('删除成功');
            await loadAuths();
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
onMounted(() => {
    loadAuths();
});
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {
    ...{},
    ...{},
};
let __VLS_components;
let __VLS_directives;
/** @type {__VLS_StyleScopedClasses['token-text']} */ ;
/** @type {__VLS_StyleScopedClasses['token-content']} */ ;
/** @type {__VLS_StyleScopedClasses['token-content']} */ ;
/** @type {__VLS_StyleScopedClasses['token-content']} */ ;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
/** @type {__VLS_StyleScopedClasses['data-table']} */ ;
/** @type {__VLS_StyleScopedClasses['provider-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['text-light']} */ ;
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
        { onClick: (__VLS_ctx.handleAddAuth) });
    const { default: __VLS_13 } = __VLS_9.slots;
    // @ts-ignore
    [handleAddAuth,];
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
        { onClick: (__VLS_ctx.loadAuths) });
    const { default: __VLS_31 } = __VLS_27.slots;
    // @ts-ignore
    [loadAuths,];
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
    ...{ class: "card-body" },
});
__VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "table-container" },
});
const __VLS_42 = {}.ElTable;
/** @type {[typeof __VLS_components.ElTable, typeof __VLS_components.elTable, typeof __VLS_components.ElTable, typeof __VLS_components.elTable, ]} */ ;
// @ts-ignore
ElTable;
// @ts-ignore
const __VLS_43 = __VLS_asFunctionalComponent(__VLS_42, new __VLS_42({
    data: (__VLS_ctx.authList),
    border: true,
    stripe: true,
    ...{ class: "data-table" },
}));
const __VLS_44 = __VLS_43({
    data: (__VLS_ctx.authList),
    border: true,
    stripe: true,
    ...{ class: "data-table" },
}, ...__VLS_functionalComponentArgsRest(__VLS_43));
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.loading) }, null, null);
const { default: __VLS_46 } = __VLS_45.slots;
// @ts-ignore
[authList, vLoading, loading,];
const __VLS_47 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_48 = __VLS_asFunctionalComponent(__VLS_47, new __VLS_47({
    label: "网盘类型",
    prop: "provider",
    width: "120",
    align: "center",
}));
const __VLS_49 = __VLS_48({
    label: "网盘类型",
    prop: "provider",
    width: "120",
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
        type: (__VLS_ctx.getProviderTagType(row.provider)),
        ...{ class: "status-tag" },
    }));
    const __VLS_55 = __VLS_54({
        type: (__VLS_ctx.getProviderTagType(row.provider)),
        ...{ class: "status-tag" },
    }, ...__VLS_functionalComponentArgsRest(__VLS_54));
    const { default: __VLS_57 } = __VLS_56.slots;
    // @ts-ignore
    [getProviderTagType,];
    (__VLS_ctx.getProviderName(row.provider));
    // @ts-ignore
    [getProviderName,];
    var __VLS_56;
}
var __VLS_50;
const __VLS_58 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_59 = __VLS_asFunctionalComponent(__VLS_58, new __VLS_58({
    label: "访问令牌",
    minWidth: "200",
}));
const __VLS_60 = __VLS_59({
    label: "访问令牌",
    minWidth: "200",
}, ...__VLS_functionalComponentArgsRest(__VLS_59));
const { default: __VLS_62 } = __VLS_61.slots;
{
    const { default: __VLS_63 } = __VLS_61.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_63);
    if (row.access_token) {
        const __VLS_64 = {}.ElPopover;
        /** @type {[typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, ]} */ ;
        // @ts-ignore
        ElPopover;
        // @ts-ignore
        const __VLS_65 = __VLS_asFunctionalComponent(__VLS_64, new __VLS_64({
            placement: "bottom",
            trigger: "hover",
            width: (500),
            popperClass: "token-popover",
            offset: (8),
        }));
        const __VLS_66 = __VLS_65({
            placement: "bottom",
            trigger: "hover",
            width: (500),
            popperClass: "token-popover",
            offset: (8),
        }, ...__VLS_functionalComponentArgsRest(__VLS_65));
        const { default: __VLS_68 } = __VLS_67.slots;
        {
            const { reference: __VLS_69 } = __VLS_67.slots;
            __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
                ...{ class: "token-text" },
            });
            (row.access_token);
        }
        __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
            ...{ class: "token-content" },
        });
        (row.access_token);
        var __VLS_67;
    }
    else {
        __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
            ...{ class: "token-text no-content" },
        });
    }
}
var __VLS_61;
const __VLS_70 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_71 = __VLS_asFunctionalComponent(__VLS_70, new __VLS_70({
    label: "刷新令牌",
    minWidth: "200",
}));
const __VLS_72 = __VLS_71({
    label: "刷新令牌",
    minWidth: "200",
}, ...__VLS_functionalComponentArgsRest(__VLS_71));
const { default: __VLS_74 } = __VLS_73.slots;
{
    const { default: __VLS_75 } = __VLS_73.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_75);
    if (row.refresh_token) {
        const __VLS_76 = {}.ElPopover;
        /** @type {[typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, ]} */ ;
        // @ts-ignore
        ElPopover;
        // @ts-ignore
        const __VLS_77 = __VLS_asFunctionalComponent(__VLS_76, new __VLS_76({
            placement: "bottom",
            trigger: "hover",
            width: (500),
            popperClass: "token-popover",
            offset: (8),
        }));
        const __VLS_78 = __VLS_77({
            placement: "bottom",
            trigger: "hover",
            width: (500),
            popperClass: "token-popover",
            offset: (8),
        }, ...__VLS_functionalComponentArgsRest(__VLS_77));
        const { default: __VLS_80 } = __VLS_79.slots;
        {
            const { reference: __VLS_81 } = __VLS_79.slots;
            __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
                ...{ class: "token-text" },
            });
            (row.refresh_token);
        }
        __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
            ...{ class: "token-content" },
        });
        (row.refresh_token);
        var __VLS_79;
    }
    else {
        __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
            ...{ class: "token-text no-content" },
        });
    }
}
var __VLS_73;
const __VLS_82 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_83 = __VLS_asFunctionalComponent(__VLS_82, new __VLS_82({
    label: "Cookie",
    minWidth: "200",
}));
const __VLS_84 = __VLS_83({
    label: "Cookie",
    minWidth: "200",
}, ...__VLS_functionalComponentArgsRest(__VLS_83));
const { default: __VLS_86 } = __VLS_85.slots;
{
    const { default: __VLS_87 } = __VLS_85.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_87);
    if (row.cookie) {
        const __VLS_88 = {}.ElPopover;
        /** @type {[typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, typeof __VLS_components.ElPopover, typeof __VLS_components.elPopover, ]} */ ;
        // @ts-ignore
        ElPopover;
        // @ts-ignore
        const __VLS_89 = __VLS_asFunctionalComponent(__VLS_88, new __VLS_88({
            placement: "bottom",
            trigger: "hover",
            width: (500),
            popperClass: "token-popover",
            offset: (8),
        }));
        const __VLS_90 = __VLS_89({
            placement: "bottom",
            trigger: "hover",
            width: (500),
            popperClass: "token-popover",
            offset: (8),
        }, ...__VLS_functionalComponentArgsRest(__VLS_89));
        const { default: __VLS_92 } = __VLS_91.slots;
        {
            const { reference: __VLS_93 } = __VLS_91.slots;
            __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
                ...{ class: "token-text" },
            });
            (row.cookie);
        }
        __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
            ...{ class: "token-content" },
        });
        (row.cookie);
        var __VLS_91;
    }
    else {
        __VLS_asFunctionalElement(__VLS_intrinsics.span, __VLS_intrinsics.span)({
            ...{ class: "token-text no-content" },
        });
    }
}
var __VLS_85;
const __VLS_94 = {}.ElTableColumn;
/** @type {[typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, typeof __VLS_components.ElTableColumn, typeof __VLS_components.elTableColumn, ]} */ ;
// @ts-ignore
ElTableColumn;
// @ts-ignore
const __VLS_95 = __VLS_asFunctionalComponent(__VLS_94, new __VLS_94({
    label: "操作",
    width: "180",
    align: "center",
    fixed: "right",
}));
const __VLS_96 = __VLS_95({
    label: "操作",
    width: "180",
    align: "center",
    fixed: "right",
}, ...__VLS_functionalComponentArgsRest(__VLS_95));
const { default: __VLS_98 } = __VLS_97.slots;
{
    const { default: __VLS_99 } = __VLS_97.slots;
    const [{ row }] = __VLS_getSlotParameters(__VLS_99);
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "auth-operation" },
    });
    const __VLS_100 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_101 = __VLS_asFunctionalComponent(__VLS_100, new __VLS_100({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
    }));
    const __VLS_102 = __VLS_101({
        ...{ 'onClick': {} },
        type: "primary",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_101));
    let __VLS_104;
    let __VLS_105;
    const __VLS_106 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleEdit(row);
                // @ts-ignore
                [handleEdit,];
            } });
    const { default: __VLS_107 } = __VLS_103.slots;
    const __VLS_108 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_109 = __VLS_asFunctionalComponent(__VLS_108, new __VLS_108({}));
    const __VLS_110 = __VLS_109({}, ...__VLS_functionalComponentArgsRest(__VLS_109));
    const { default: __VLS_112 } = __VLS_111.slots;
    const __VLS_113 = {}.Edit;
    /** @type {[typeof __VLS_components.Edit, ]} */ ;
    // @ts-ignore
    Edit;
    // @ts-ignore
    const __VLS_114 = __VLS_asFunctionalComponent(__VLS_113, new __VLS_113({}));
    const __VLS_115 = __VLS_114({}, ...__VLS_functionalComponentArgsRest(__VLS_114));
    var __VLS_111;
    var __VLS_103;
    const __VLS_118 = {}.ElDivider;
    /** @type {[typeof __VLS_components.ElDivider, typeof __VLS_components.elDivider, ]} */ ;
    // @ts-ignore
    ElDivider;
    // @ts-ignore
    const __VLS_119 = __VLS_asFunctionalComponent(__VLS_118, new __VLS_118({
        direction: "vertical",
    }));
    const __VLS_120 = __VLS_119({
        direction: "vertical",
    }, ...__VLS_functionalComponentArgsRest(__VLS_119));
    const __VLS_123 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_124 = __VLS_asFunctionalComponent(__VLS_123, new __VLS_123({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
    }));
    const __VLS_125 = __VLS_124({
        ...{ 'onClick': {} },
        type: "danger",
        text: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_124));
    let __VLS_127;
    let __VLS_128;
    const __VLS_129 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.handleDelete(row);
                // @ts-ignore
                [handleDelete,];
            } });
    const { default: __VLS_130 } = __VLS_126.slots;
    const __VLS_131 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_132 = __VLS_asFunctionalComponent(__VLS_131, new __VLS_131({}));
    const __VLS_133 = __VLS_132({}, ...__VLS_functionalComponentArgsRest(__VLS_132));
    const { default: __VLS_135 } = __VLS_134.slots;
    const __VLS_136 = {}.Delete;
    /** @type {[typeof __VLS_components.Delete, ]} */ ;
    // @ts-ignore
    Delete;
    // @ts-ignore
    const __VLS_137 = __VLS_asFunctionalComponent(__VLS_136, new __VLS_136({}));
    const __VLS_138 = __VLS_137({}, ...__VLS_functionalComponentArgsRest(__VLS_137));
    var __VLS_134;
    var __VLS_126;
}
var __VLS_97;
var __VLS_45;
var __VLS_3;
const __VLS_141 = {}.ElDialog;
/** @type {[typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, ]} */ ;
// @ts-ignore
ElDialog;
// @ts-ignore
const __VLS_142 = __VLS_asFunctionalComponent(__VLS_141, new __VLS_141({
    title: (__VLS_ctx.isEdit ? '编辑认证' : '添加认证'),
    modelValue: (__VLS_ctx.dialogVisible),
    width: "600px",
    closeOnClickModal: (false),
    destroyOnClose: true,
    ...{ class: "custom-dialog" },
}));
const __VLS_143 = __VLS_142({
    title: (__VLS_ctx.isEdit ? '编辑认证' : '添加认证'),
    modelValue: (__VLS_ctx.dialogVisible),
    width: "600px",
    closeOnClickModal: (false),
    destroyOnClose: true,
    ...{ class: "custom-dialog" },
}, ...__VLS_functionalComponentArgsRest(__VLS_142));
const { default: __VLS_145 } = __VLS_144.slots;
// @ts-ignore
[isEdit, dialogVisible,];
const __VLS_146 = {}.ElForm;
/** @type {[typeof __VLS_components.ElForm, typeof __VLS_components.elForm, typeof __VLS_components.ElForm, typeof __VLS_components.elForm, ]} */ ;
// @ts-ignore
ElForm;
// @ts-ignore
const __VLS_147 = __VLS_asFunctionalComponent(__VLS_146, new __VLS_146({
    model: (__VLS_ctx.authForm),
    ref: "authFormRef",
    labelWidth: "100px",
    rules: (__VLS_ctx.formRules),
}));
const __VLS_148 = __VLS_147({
    model: (__VLS_ctx.authForm),
    ref: "authFormRef",
    labelWidth: "100px",
    rules: (__VLS_ctx.formRules),
}, ...__VLS_functionalComponentArgsRest(__VLS_147));
/** @type {typeof __VLS_ctx.authFormRef} */ ;
var __VLS_150 = {};
const { default: __VLS_152 } = __VLS_149.slots;
// @ts-ignore
[authForm, formRules, authFormRef,];
const __VLS_153 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_154 = __VLS_asFunctionalComponent(__VLS_153, new __VLS_153({
    label: "网盘类型",
    prop: "provider",
}));
const __VLS_155 = __VLS_154({
    label: "网盘类型",
    prop: "provider",
}, ...__VLS_functionalComponentArgsRest(__VLS_154));
const { default: __VLS_157 } = __VLS_156.slots;
const __VLS_158 = {}.ElSelect;
/** @type {[typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, typeof __VLS_components.ElSelect, typeof __VLS_components.elSelect, ]} */ ;
// @ts-ignore
ElSelect;
// @ts-ignore
const __VLS_159 = __VLS_asFunctionalComponent(__VLS_158, new __VLS_158({
    modelValue: (__VLS_ctx.authForm.provider),
    placeholder: "请选择网盘类型",
    ...{ class: "form-select" },
}));
const __VLS_160 = __VLS_159({
    modelValue: (__VLS_ctx.authForm.provider),
    placeholder: "请选择网盘类型",
    ...{ class: "form-select" },
}, ...__VLS_functionalComponentArgsRest(__VLS_159));
const { default: __VLS_162 } = __VLS_161.slots;
// @ts-ignore
[authForm,];
const __VLS_163 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_164 = __VLS_asFunctionalComponent(__VLS_163, new __VLS_163({
    label: "夸克",
    value: (1),
}));
const __VLS_165 = __VLS_164({
    label: "夸克",
    value: (1),
}, ...__VLS_functionalComponentArgsRest(__VLS_164));
const __VLS_168 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_169 = __VLS_asFunctionalComponent(__VLS_168, new __VLS_168({
    label: "阿里云盘",
    value: (2),
}));
const __VLS_170 = __VLS_169({
    label: "阿里云盘",
    value: (2),
}, ...__VLS_functionalComponentArgsRest(__VLS_169));
const __VLS_173 = {}.ElOption;
/** @type {[typeof __VLS_components.ElOption, typeof __VLS_components.elOption, typeof __VLS_components.ElOption, typeof __VLS_components.elOption, ]} */ ;
// @ts-ignore
ElOption;
// @ts-ignore
const __VLS_174 = __VLS_asFunctionalComponent(__VLS_173, new __VLS_173({
    label: "百度网盘",
    value: (3),
}));
const __VLS_175 = __VLS_174({
    label: "百度网盘",
    value: (3),
}, ...__VLS_functionalComponentArgsRest(__VLS_174));
var __VLS_161;
var __VLS_156;
if (__VLS_ctx.showAccessToken) {
    // @ts-ignore
    [showAccessToken,];
    const __VLS_178 = {}.ElFormItem;
    /** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
    // @ts-ignore
    ElFormItem;
    // @ts-ignore
    const __VLS_179 = __VLS_asFunctionalComponent(__VLS_178, new __VLS_178({
        label: "访问令牌",
        prop: "access_token",
    }));
    const __VLS_180 = __VLS_179({
        label: "访问令牌",
        prop: "access_token",
    }, ...__VLS_functionalComponentArgsRest(__VLS_179));
    const { default: __VLS_182 } = __VLS_181.slots;
    const __VLS_183 = {}.ElInput;
    /** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
    // @ts-ignore
    ElInput;
    // @ts-ignore
    const __VLS_184 = __VLS_asFunctionalComponent(__VLS_183, new __VLS_183({
        modelValue: (__VLS_ctx.authForm.access_token),
        placeholder: "请输入访问令牌",
        type: "textarea",
        rows: (2),
    }));
    const __VLS_185 = __VLS_184({
        modelValue: (__VLS_ctx.authForm.access_token),
        placeholder: "请输入访问令牌",
        type: "textarea",
        rows: (2),
    }, ...__VLS_functionalComponentArgsRest(__VLS_184));
    // @ts-ignore
    [authForm,];
    var __VLS_181;
}
if (__VLS_ctx.showRefreshToken) {
    // @ts-ignore
    [showRefreshToken,];
    const __VLS_188 = {}.ElFormItem;
    /** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
    // @ts-ignore
    ElFormItem;
    // @ts-ignore
    const __VLS_189 = __VLS_asFunctionalComponent(__VLS_188, new __VLS_188({
        label: "刷新令牌",
        prop: "refresh_token",
    }));
    const __VLS_190 = __VLS_189({
        label: "刷新令牌",
        prop: "refresh_token",
    }, ...__VLS_functionalComponentArgsRest(__VLS_189));
    const { default: __VLS_192 } = __VLS_191.slots;
    const __VLS_193 = {}.ElInput;
    /** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
    // @ts-ignore
    ElInput;
    // @ts-ignore
    const __VLS_194 = __VLS_asFunctionalComponent(__VLS_193, new __VLS_193({
        modelValue: (__VLS_ctx.authForm.refresh_token),
        placeholder: "请输入刷新令牌",
        type: "textarea",
        rows: (2),
    }));
    const __VLS_195 = __VLS_194({
        modelValue: (__VLS_ctx.authForm.refresh_token),
        placeholder: "请输入刷新令牌",
        type: "textarea",
        rows: (2),
    }, ...__VLS_functionalComponentArgsRest(__VLS_194));
    // @ts-ignore
    [authForm,];
    var __VLS_191;
}
const __VLS_198 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_199 = __VLS_asFunctionalComponent(__VLS_198, new __VLS_198({
    label: "Cookie",
    prop: "cookie",
}));
const __VLS_200 = __VLS_199({
    label: "Cookie",
    prop: "cookie",
}, ...__VLS_functionalComponentArgsRest(__VLS_199));
const { default: __VLS_202 } = __VLS_201.slots;
const __VLS_203 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_204 = __VLS_asFunctionalComponent(__VLS_203, new __VLS_203({
    modelValue: (__VLS_ctx.authForm.cookie),
    placeholder: "请输入Cookie",
    type: "textarea",
    rows: (3),
}));
const __VLS_205 = __VLS_204({
    modelValue: (__VLS_ctx.authForm.cookie),
    placeholder: "请输入Cookie",
    type: "textarea",
    rows: (3),
}, ...__VLS_functionalComponentArgsRest(__VLS_204));
// @ts-ignore
[authForm,];
var __VLS_201;
var __VLS_149;
{
    const { footer: __VLS_208 } = __VLS_144.slots;
    __VLS_asFunctionalElement(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        ...{ class: "dialog-footer" },
    });
    const __VLS_209 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_210 = __VLS_asFunctionalComponent(__VLS_209, new __VLS_209({
        ...{ 'onClick': {} },
    }));
    const __VLS_211 = __VLS_210({
        ...{ 'onClick': {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_210));
    let __VLS_213;
    let __VLS_214;
    const __VLS_215 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.dialogVisible = false;
                // @ts-ignore
                [dialogVisible,];
            } });
    const { default: __VLS_216 } = __VLS_212.slots;
    var __VLS_212;
    const __VLS_217 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_218 = __VLS_asFunctionalComponent(__VLS_217, new __VLS_217({
        ...{ 'onClick': {} },
        type: "primary",
    }));
    const __VLS_219 = __VLS_218({
        ...{ 'onClick': {} },
        type: "primary",
    }, ...__VLS_functionalComponentArgsRest(__VLS_218));
    let __VLS_221;
    let __VLS_222;
    const __VLS_223 = ({ click: {} },
        { onClick: (__VLS_ctx.handleSubmit) });
    const { default: __VLS_224 } = __VLS_220.slots;
    // @ts-ignore
    [handleSubmit,];
    var __VLS_220;
}
var __VLS_144;
/** @type {__VLS_StyleScopedClasses['card-header']} */ ;
/** @type {__VLS_StyleScopedClasses['header-title']} */ ;
/** @type {__VLS_StyleScopedClasses['operation-buttons']} */ ;
/** @type {__VLS_StyleScopedClasses['card-body']} */ ;
/** @type {__VLS_StyleScopedClasses['table-container']} */ ;
/** @type {__VLS_StyleScopedClasses['data-table']} */ ;
/** @type {__VLS_StyleScopedClasses['tag-wrapper']} */ ;
/** @type {__VLS_StyleScopedClasses['status-tag']} */ ;
/** @type {__VLS_StyleScopedClasses['token-text']} */ ;
/** @type {__VLS_StyleScopedClasses['token-content']} */ ;
/** @type {__VLS_StyleScopedClasses['token-text']} */ ;
/** @type {__VLS_StyleScopedClasses['no-content']} */ ;
/** @type {__VLS_StyleScopedClasses['token-text']} */ ;
/** @type {__VLS_StyleScopedClasses['token-content']} */ ;
/** @type {__VLS_StyleScopedClasses['token-text']} */ ;
/** @type {__VLS_StyleScopedClasses['no-content']} */ ;
/** @type {__VLS_StyleScopedClasses['token-text']} */ ;
/** @type {__VLS_StyleScopedClasses['token-content']} */ ;
/** @type {__VLS_StyleScopedClasses['token-text']} */ ;
/** @type {__VLS_StyleScopedClasses['no-content']} */ ;
/** @type {__VLS_StyleScopedClasses['auth-operation']} */ ;
/** @type {__VLS_StyleScopedClasses['custom-dialog']} */ ;
/** @type {__VLS_StyleScopedClasses['form-select']} */ ;
/** @type {__VLS_StyleScopedClasses['dialog-footer']} */ ;
// @ts-ignore
var __VLS_151 = __VLS_150;
const __VLS_export = (await import('vue')).defineComponent({});
export default {};
