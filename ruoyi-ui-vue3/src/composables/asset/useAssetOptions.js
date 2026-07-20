import {useUserOptions} from './useUserOptions'
import {useDeptOptions} from './useDeptOptions'
import {useCategoryOptions} from './useCategoryOptions'
import {useSupplierOptions} from './useSupplierOptions'
import {useNameMapping} from './useNameMapping'
import {useApplicantInfo} from './useApplicantInfo'

/**
 * 资产模块公共选项数据（组合版）
 * 推荐使用此函数，一次性获取所有需要的选项数据和工具函数
 */
export function useAssetOptions() {
    // 基础选项
    const user = useUserOptions()
    const dept = useDeptOptions()
    const category = useCategoryOptions()
    const supplier = useSupplierOptions()

    // 名称映射工具（传入选项数据）
    const nameMapping = useNameMapping({
        userOptions: user.userOptions,
        deptOptions: dept.deptOptions,
        categoryOptions: category.categoryOptions,
        supplierOptions: supplier.supplierOptions
    })

    // 申请人信息
    const applicant = useApplicantInfo()

    /**
     * 加载所有选项数据
     */
    async function loadAll() {
        await Promise.all([
            user.loadUserOptions(),
            dept.loadDeptOptions(),
            category.loadCategoryOptions(),
            supplier.loadSupplierOptions()
        ])
    }

    return {
        // 选项数据
        userOptions: user.userOptions,
        deptOptions: dept.deptOptions,
        categoryOptions: category.categoryOptions,
        supplierOptions: supplier.supplierOptions,

        // 加载方法
        loadUserOptions: user.loadUserOptions,
        loadDeptOptions: dept.loadDeptOptions,
        loadCategoryOptions: category.loadCategoryOptions,
        loadSupplierOptions: supplier.loadSupplierOptions,
        loadAll,

        // 名称映射工具
        ...nameMapping,

        // 申请人信息
        applicantInfo: applicant.applicantInfo,
        loadApplicantInfo: applicant.loadApplicantInfo
    }
}
