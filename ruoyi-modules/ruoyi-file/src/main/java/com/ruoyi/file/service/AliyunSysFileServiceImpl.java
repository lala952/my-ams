package com.ruoyi.file.service;

import com.alibaba.nacos.common.utils.IoUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * aliyun 文件存储
 *
 * @author wangqin
 */
@Primary
@Service
public class AliyunSysFileServiceImpl implements ISysFileService {
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * aliyun 文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        InputStream inputStream = null;
        try {
            FileInfo fileInfo = fileStorageService.of(file)
                    .setPlatform("aliyun-oss-1")  // 指定使用阿里云OSS
                    .upload();
            return fileInfo.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Aliyun OSS  Failed to upload file", e);
        } finally {
            IoUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Aliyun OSS文件删除接口
     *
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    @Override
    public void deleteFile(String fileUrl) throws Exception {
        try {
            fileStorageService.delete(fileUrl);
        } catch (Exception e) {
            throw new RuntimeException("Aliyun OSS Failed to delete file", e);
        }
    }
}
