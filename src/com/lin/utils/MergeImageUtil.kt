package com.lin.utils

import java.awt.image.BufferedImage

class MergeImageUtil(private val imageFiles: List<BufferedImage>,
                     private val columnCount: Int = 5) {

    private data class ColumnAndLine(val columnNum: Int, val lineNum: Int)

    private val columnAndLine: ColumnAndLine?

    init {
        columnAndLine = imageFiles.takeIf { it.isNotEmpty() }?.let {
            // 计算要分组的列数
            val columnNum = if (columnCount <= 0) {
                // 如果 column count 小于等于0，默认为 1
                1
            } else if (columnCount > it.size) {
                // 如果 column count 大于 size，使用 size
                it.size
            } else {
                columnCount
            }
            // 计算要分组的行数
            val lineNum = if (it.size % columnNum == 0) {
                it.size / columnNum
            } else {
                it.size / columnNum + 1
            }
            ColumnAndLine(columnNum, lineNum)
        } ?: let {
            println("该文件夹下没有图片，分组失败！")
            null
        }
    }

    fun mergeImage(marginPxBetweenImage: Int): BufferedImage? {
        // 将 image list 按设置好的行数分组
        return columnAndLine?.run {
            val groupList = mutableListOf<List<BufferedImage>>()
            for (line in 1..lineNum) {
                val (startIndex, endIndex) = if (line != lineNum) {
                    (line - 1) * columnNum to line * columnNum
                } else {
                    (line - 1) * columnNum to imageFiles.size
                }
                val columnData = mutableListOf<BufferedImage>()
                for (column in startIndex until endIndex) {
                    columnData.add(imageFiles[column])
                }
                groupList.add(columnData)
            }
            // 如果图片间隔过大或非法，取30
            val realMargin = if (marginPxBetweenImage in 0..50) marginPxBetweenImage else 30
            // 取出图片最大宽度和最大高度
            val maxWidth = imageFiles.maxBy { it.width }?.width ?: 0
            val maxHeight = imageFiles.maxBy { it.height }?.height ?: 0
            // 按照每行的个数和总行数，再根据最宽和最高为标准，这样可以使每张图片在各自的格子里居中
            val realColumnNum = groupList[0].size
            val desWidth = realColumnNum * maxWidth + realColumnNum * realMargin
            val desHeight = lineNum * maxHeight + lineNum * realMargin
            val resultImage = BufferedImage(desWidth, desHeight, BufferedImage.TYPE_INT_ARGB)
            // 写入图片
            var currentImageX = 0
            var currentImageY = 0
            groupList.forEach {
                it.forEach { image ->
                    val width = image.width
                    val height = image.height
                    val imageData = IntArray(width * height)
                    image.getRGB(0, 0, width, height, imageData, 0, width)
                    // 为了保证居中，计算上下偏移量
                    val startX = currentImageX + (maxWidth - width) / 2
                    val startY = currentImageY + (maxHeight - height) / 2
                    // 写入
                    resultImage.setRGB(startX, startY, width, height, imageData, 0, width)
                    currentImageX += maxWidth + realMargin
                }
                currentImageX = 0
                currentImageY += maxHeight + realMargin
            }
            resultImage
        }
    }
}