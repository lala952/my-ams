/**
 * AssetLedgerPanel 组合式函数
 * 提供资产表格的核心逻辑
 */
import {computed, ref} from 'vue'
import {useNameMapping} from '@/composables/asset/useNameMapping'

export function useAssetLedgerPanel(options = {}) {
    const {
        data = [],
        deptOptions = [],
        userOptions = [],
        categoryOptions = [],
        supplierOptions = []
    } = options

    // 使用名称映射工具
    const {getDeptName, getUserName, getCategoryName, getSupplierName} = useNameMapping({
        deptOptions,
        userOptions,
        categoryOptions,
        supplierOptions
    })

    // 选中行
    const selectedRows = ref([])
    const tableRef = ref(null)

    // 计算总价值
    const totalValue = computed(() => {
        return data.reduce((sum, item) => sum + (Number(item.originalValue) || 0), 0)
    })

    // 处理选择变化
    function handleSelectionChange(selection) {
        selectedRows.value = selection
    }

    // 清空选中
    function clearSelection() {
        tableRef.value?.clearSelection()
    }

    // 获取选中行
    function getSelectionRows() {
        return selectedRows.value
    }

    return {
        // 引用
        tableRef,
        selectedRows,

        // 计算属性
        totalValue,

        // 名称映射方法
        getDeptName,
        getUserName,
        getCategoryName,
        getSupplierName,

        // 操作方法
        handleSelectionChange,
        clearSelection,
        getSelectionRows
    }
}
