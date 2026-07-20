import {ref} from 'vue'
import {listDept} from '@/api/system/dept'
import {loadAllParams} from "@/api/page.js";

/**
 * 部门选项加载
 */
export function useDeptOptions() {
    const deptOptions = ref([])

    /**
     * 加载部门选项
     */
    async function loadDeptOptions() {
        try {
            const res = await listDept(loadAllParams)
            deptOptions.value = res.data || []
        } catch (error) {
            console.warn('加载部门选项失败:', error.message || error)
            // 不阻断程序执行，返回空数组
            deptOptions.value = []
        }
    }

    return {
        deptOptions,
        loadDeptOptions
    }
}
