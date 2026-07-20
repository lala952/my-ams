import {ref} from 'vue'
import {getUser} from '@/api/system/user'

/**
 * 申请人信息加载
 */
export function useApplicantInfo() {
    const applicantInfo = ref({nickName: '-', deptName: '-'})

    /**
     * 加载申请人信息
     * @param {number} userId - 用户ID
     * @returns {Promise<Object>} 申请人信息
     */
    async function loadApplicantInfo(userId) {
        if (!userId) {
            return {nickName: '-', deptName: '-'}
        }

        try {
            const res = await getUser(userId)
            if (res.code === 200 && res.data) {
                const info = {
                    nickName: res.data.nickName || '-',
                    deptName: res.data.dept?.deptName || '-'
                }
                applicantInfo.value = info
                return info
            }
        } catch (error) {
            console.error('获取申请人信息失败:', error)
        }

        return {nickName: '-', deptName: '-'}
    }

    return {
        applicantInfo,
        loadApplicantInfo
    }
}
