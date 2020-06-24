package com.lishunyi.minioservice.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.lishunyi.minioservice.enums.FileTypeEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author 李顺仪
 * @version 1.0
 * @since 2020/6/24 20:43
 **/
@UtilityClass
public class OSSUtils {

    /**
     * 时间戳构造文件名
     *
     * @param originFileName 文件名
     * @return 新文件名
     */
    public String timeFileName(String originFileName) {
        // 毫秒级时间戳
        String str = String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        if (originFileName.length() > 30) {
            return str;
        }
        return originFileName + "_" + str;
    }

    /**
     * uuid构造文件名称
     *
     * @param originFileName 文件名
     * @return 新文件名
     */
    public String uuidFileName(String originFileName) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (originFileName.length() > 30) {
            return uuid;
        }
        return originFileName + "_" + uuid;
    }

    /**
     * 获取去除后缀的文件名
     *
     * @param originFileName 文件名
     * @return 去除后缀的文件名
     */
    public String getFileName(String originFileName) {
        if (!originFileName.contains(".")) {
            return originFileName;
        }
        return originFileName.substring(0, originFileName.lastIndexOf('.'));
    }

    /**
     * 根据文件后缀获取存储类型
     *
     * @param fileSuffix 文件后缀
     * @return 存储类型枚举
     */
    public FileTypeEnum getFileType(String fileSuffix) {
        // 如果文件后缀为空返回默认文件
        if (StringUtils.isEmpty(fileSuffix)) {
            return FileTypeEnum.DEFAULT;
        }
        FileReader fileReader = new FileReader("static/file-type.json");
        String readString = fileReader.readString();
        Map<String, Object> map = JSONUtil.parseObj(readString);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            List list = (List) entry.getValue();
            if (list.contains(fileSuffix)) {
                return FileTypeEnum.valuesOf(entry.getKey());
            }
        }
        return FileTypeEnum.DEFAULT;
    }

    /**
     * 获取文件名后缀
     * 不带.
     *
     * @param originFileName 文件名
     * @return 文件后缀
     */
    public String getFileSuffix(String originFileName) {
        if (originFileName.contains(".")) {
            return originFileName.substring(originFileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return null;
    }

    /**
     * 获取类型
     * 仿照hutool里面的`FileTypeUtil.getType`
     *
     * @param multipartFile
     * @return
     * @throws IORuntimeException
     * @throws IOException
     */
    public String getType(MultipartFile multipartFile) throws IORuntimeException, IOException {
        String typeName;
        FileInputStream in = null;
        try {
            in = (FileInputStream) multipartFile.getInputStream();
            typeName = FileTypeUtil.getType(in);
        } finally {
            IoUtil.close(in);
        }

        if (null == typeName) {
            // 未成功识别类型，扩展名辅助识别
            typeName = OSSUtils.getFileSuffix(multipartFile.getOriginalFilename());
        } else if ("xls".equals(typeName)) {
            // xls、doc、msi的头一样，使用扩展名辅助判断
            final String extName = OSSUtils.getFileSuffix(multipartFile.getOriginalFilename());
            if ("doc".equalsIgnoreCase(extName)) {
                typeName = "doc";
            } else if ("msi".equalsIgnoreCase(extName)) {
                typeName = "msi";
            }
        } else if ("zip".equals(typeName)) {
            // zip可能为docx、xlsx、pptx、jar、war等格式，扩展名辅助判断
            final String extName = OSSUtils.getFileSuffix(multipartFile.getOriginalFilename());
            if ("docx".equalsIgnoreCase(extName)) {
                typeName = "docx";
            } else if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
                typeName = "pptx";
            } else if ("jar".equalsIgnoreCase(extName)) {
                typeName = "jar";
            } else if ("war".equalsIgnoreCase(extName)) {
                typeName = "war";
            }
        }
        return typeName;
    }
}
