package com.lin.utils

import java.awt.image.BufferedImage

class MergeImageUtil(private val imageFiles: List<BufferedImage>,
                     private val columnCount: Int,
                     marginPxBetweenImage: Int,
                     private val arrangeMode: ArrangeMode,
                     defaultColumnCount: Int = 1,
                     defaultImageMargin: Int = 30) {

    enum class ArrangeMode {
        /**
         * 按照表格方式合并，即先找出所有图片中最大的宽度 maxWidth 和最大的高度 maxHeight，然后合并时给每个图片都分配 maxWidth * maxHeight 的空间
         */
        FORM,
        /**
         * 按照实际尺寸合并，为了显示整齐一些，会计算出两个极值：
         * 1. 每行的宽度和间距之和都计算出来，取最大值作为最终合并图的宽度
         * 2. 每一行图片的高度都可能不同，每行的高度为该行最大高度，最终合并图的高度为每行的最高值之和
         */
        SIZE
    }

    // 分组尺寸
    private var lineNum: Int = 0
    private var columnNum: Int = 0

    private var realMargin: Int = 0

    // 分组图片数据
    private val splitGroup: List<List<BufferedImage>>

    init {
        imageFiles.takeIf { it.isNotEmpty() }?.let {
            // 计算要分组的列数
            columnNum = when {
                columnCount <= 0 -> defaultColumnCount// 如果 column count 小于等于0，默认为 1
                columnCount > it.size -> it.size // 如果 column count 大于 size，使用 size
                else -> columnCount
            }
            // 计算要分组的行数
            lineNum = if (it.size % columnNum == 0) {
                it.size / columnNum
            } else {
                it.size / columnNum + 1
            }
        }
        // 如果图片间隔过大或非法，取30
        realMargin = if (marginPxBetweenImage in 0 until 100) marginPxBetweenImage else defaultImageMargin
        splitGroup = mutableListOf()
        for (line in 1..lineNum) {
            val (startIndex, endIndex) = if (line != lineNum) {
                (line - 1) * columnNum to line * columnNum
            } else {
                (line - 1) * columnNum to imageFiles.size
            }
            val currentLineImages = imageFiles.subList(startIndex, endIndex)
            splitGroup.add(currentLineImages)
        }
    }

    fun mergeImage(): BufferedImage? {
        return when (arrangeMode) {
            ArrangeMode.FORM -> mergeImageByForm()
            ArrangeMode.SIZE -> mergeImageBySize()
        }
    }

    /**
     * 整齐排列
     */
    private fun mergeImageByForm(): BufferedImage? {
        // 将 image list 按设置好的行数分组
        return takeIf { lineNum > 0 && columnNum > 0 }.run {
            // 取出图片最大宽度和最大高度
            val maxWidth = imageFiles.maxBy { it.width }?.width ?: 0
            val maxHeight = imageFiles.maxBy { it.height }?.height ?: 0
            // 按照每行的个数和总行数，再根据最宽和最高为标准，这样可以使每张图片在各自的格子里居中
            val realColumnNum = splitGroup[0].size
            val desWidth = realColumnNum * maxWidth + (realColumnNum - 1) * realMargin
            val desHeight = lineNum * maxHeight + (lineNum - 1) * realMargin
            val resultImage = BufferedImage(desWidth, desHeight, BufferedImage.TYPE_INT_ARGB)
            // 写入图片
            var currentImageX = 0
            var currentImageY = 0
            splitGroup.forEach {
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

    /**
     * 实际尺寸
     */
    private fun mergeImageBySize(): BufferedImage? {
        // 将 image list 按设置好的行数分组
        return takeIf { lineNum > 0 && columnNum > 0 }.run {
            // 按照每行的个数和总行数，再根据最宽和最高为标准，这样可以使每张图片在各自的格子里居中
            val realColumnNum = splitGroup[0].size
            // 计算最终图片的尺寸
            // 宽度为所有行图片的宽度和的最大值
            val desWidth = (splitGroup.map { it.sumBy { it.width } }.max() ?: 0) + (realColumnNum - 1) * realMargin
            // 高度为每行图片最大高度之和
            val eachHeightList = splitGroup.map { it.maxBy { it.height }?.height ?: 0 }
            val desHeight = eachHeightList.sum() + (lineNum - 1) * realMargin

            val resultImage = BufferedImage(desWidth, desHeight, BufferedImage.TYPE_INT_ARGB)
            // 写入图片
            var currentImageX = 0
            var currentImageY = 0
            splitGroup.forEachIndexed { index, list ->
                list.forEach { image ->
                    val width = image.width
                    val height = image.height
                    val imageData = IntArray(width * height)
                    image.getRGB(0, 0, width, height, imageData, 0, width)
                    // 写入
                    resultImage.setRGB(currentImageX, currentImageY, width, height, imageData, 0, width)
                    currentImageX += width + realMargin
                }
                currentImageX = 0
                currentImageY += eachHeightList[index] + realMargin
            }
            resultImage
        }
    }

}