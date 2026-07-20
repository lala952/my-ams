import {ref} from 'vue'
import {listUser} from '@/api/system/user'
import {loadAllParams} from '@/api/page'

/**
 * 用户选项加载
 */
export function useUserOptions() {
    const userOptions = ref([])

    /**
     * 加载用户选项
     */
    async function loadUserOptions() {
        try {
            const res = await listUser(loadAllParams)
            userOptions.value = res.rows || []
        } catch (error) {
            console.warn('加载用户选项失败:', error.message || error)
            // 不阻断程序执行，返回空数组
            userOptions.value = []
        }
    }

    return {
        userOptions,
        loadUserOptions
    }
}
