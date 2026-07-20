import request from '@/utils/request'

// 上传文件
export function uploadFile(data) {
    return request({
        url: '/file/upload',
        method: 'post',
        data: data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 删除文件
export function deleteFile(fileUrl) {
    return request({
        url: '/file/delete',
        method: 'delete',
        params: {fileUrl}
    })
}
