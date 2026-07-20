import {ref} from 'vue'
import {listSupplier} from '@/api/asset/supplier'
import {loadAllParams} from '@/api/page'

/**
 * 供应商选项加载
 */
export function useSupplierOptions() {
    const supplierOptions = ref([])

    /**
     * 加载供应商选项
     */
    async function loadSupplierOptions() {
        try {
            const res = await listSupplier(loadAllParams)
            supplierOptions.value = res.rows || []
        } catch (error) {
            console.error('加载供应商选项失败:', error)
            supplierOptions.value = []
        }
    }

    return {
        supplierOptions,
        loadSupplierOptions
    }
}
