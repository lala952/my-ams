import {ref} from 'vue'
import {listCategory} from '@/api/asset/category'
import {loadAllParams} from '@/api/page'

/**
 * 资产分类选项加载
 */
export function useCategoryOptions() {
    const categoryOptions = ref([])

    /**
     * 加载分类选项
     */
    async function loadCategoryOptions() {
        try {
            const res = await listCategory(loadAllParams)
            categoryOptions.value = res.data || []
        } catch (error) {
            console.error('加载分类选项失败:', error)
            categoryOptions.value = []
        }
    }

    return {
        categoryOptions,
        loadCategoryOptions
    }
}
