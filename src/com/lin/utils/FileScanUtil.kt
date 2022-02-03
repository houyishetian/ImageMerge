package com.lin.utils

import com.lin.exceptions.FileNotDirectoryException
import com.lin.exceptions.WriteImageFailedException
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FileUtil {

    /**
     * 扫描所有 pic file
     * @param path 扫描路径
     * @param picExtensionList 需要扫描的扩展名，如果为null，表示扫描所有文件
     * @return 扫描结果，如果 [path] 不合法，返回 null;
     */
    fun getAllPics(path: String, picExtensionList: List<String>?): List<File> {
        File(path).takeIf { it.isDirectory }?.let {
            val result = mutableListOf<File>()
            val subFiles = it.listFiles().filter { it.isFile }
            if (picExtensionList == null || picExtensionList.isEmpty()) {
                // 没有任何扩展名要求
                result.addAll(subFiles)
            } else {
                val picFiles = subFiles.filter { it.extension in picExtensionList }
                result.addAll(picFiles)
            }
            result.sortBy { it.nameWithoutExtension }
            return result
        } ?: let {
            throw FileNotDirectoryException(path)
        }
    }

    fun writeImageToFile(image: BufferedImage, desPath: String) {
        try {
            ImageIO.write(image, "png", File(desPath))
        } catch (e: Exception) {
            throw WriteImageFailedException(e.message ?: "")
        }
    }
}