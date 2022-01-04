package com.lin.entrances

import com.lin.utils.FileUtil
import com.lin.utils.ImageCompressUtil
import com.lin.utils.MergeImageUtil
import java.io.File
import java.util.*

object Entrance {

    private fun mergeImage(path: String, desName: String) {
        val extension = listOf("png", "jpg", "jpeg")
        // 扫描所有符合扩展名的文件
        FileUtil.getAllPics(path, extension)?.let {
            // 将所有图片按 0.5 倍压缩
            val readImagesList = ImageCompressUtil.compress(it, 0.5f)
            // 去掉压缩失败的部分
            readImagesList?.filterNotNull()?.let {
                // 将所有压缩后的额图片合并，间距 30px，每行最多5个
                MergeImageUtil(it, columnCount = 5).mergeImage(30)?.let {
                    val desFile = File(File(path), "$desName.png")
                    // 写入
                    FileUtil.writeImageToFile(it, desFile.absolutePath)
                    println("合成成功，检查:${desFile.absolutePath}")
                }
            }
        }
        println()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        println()
        println("输入图片所在文件夹")
        val path = scanner.next()
        println()
        println("输入合成后的图片名称，不需要扩展名，默认png格式：")
        val desName = scanner.next()
        println()
        scanner.close()
        val currentTime = System.currentTimeMillis()
        mergeImage(path, desName)
        val cost = System.currentTimeMillis() - currentTime
        val mill = cost % 1000
        val second = cost / 1000
        println("耗时: $second.${mill}秒")
    }
}