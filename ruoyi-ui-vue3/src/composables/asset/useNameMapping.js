/**
 * ID到名称的映射工具
 */
export function useNameMapping(options = {}) {
    const {
        userOptions = [],
        deptOptions = [],
        categoryOptions = [],
        supplierOptions = []
    } = options

    /**
     * 根据用户ID获取用户名称
     */
    function getUserName(userId) {
        if (!userId && userId !== 0) return '-'
        const users = userOptions.value || userOptions || []
        const user = users.find(item => item.userId === userId)
        return user ? user.nickName : userId
    }

    /**
     * 根据部门ID获取部门名称
     */
    function getDeptName(deptId) {
        if (!deptId && deptId !== 0) return '-'
        const depts = deptOptions.value || deptOptions || []
        const dept = depts.find(item => item.deptId === deptId)
        return dept ? dept.deptName : deptId
    }

    /**
     * 根据分类ID获取分类名称
     */
    function getCategoryName(categoryId) {
        if (!categoryId && categoryId !== 0) return '-'
        const categories = categoryOptions.value || categoryOptions || []
        const category = categories.find(item => item.id === categoryId)
        return category ? category.categoryName : '-'
    }

    /**
     * 根据供应商ID获取供应商名称
     */
    function getSupplierName(supplierId) {
        if (!supplierId && supplierId !== 0) return '-'
        const suppliers = supplierOptions.value || supplierOptions || []
        const supplier = suppliers.find(item => item.id === supplierId)
        return supplier ? supplier.supplierName : supplierId
    }

    return {
        getUserName,
        getDeptName,
        getCategoryName,
        getSupplierName
    }
}
