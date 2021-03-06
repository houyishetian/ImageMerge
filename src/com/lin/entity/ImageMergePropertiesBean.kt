package com.lin.entity

import com.lin.exceptions.InputException
import com.lin.utils.MergeImageUtil

class ImageMergePropertiesBean(
        val directoryPath: String,
        val imageFormats: List<String>,
        val imageMargin: Int,
        val eachLineNum: Int,
        val imageQuality: Float,
        val arrangeMode: MergeImageUtil.ArrangeMode,
        val outputName: String
) {
    companion object {
        fun safeObject(
                directoryPath: String?,
                imageFormats: List<String>?,
                imageMargin: Int?,
                eachLineNum: Int?,
                imageQuality: Float?,
                arrangeMode: MergeImageUtil.ArrangeMode?,
                outputName: String?
        ): ImageMergePropertiesBean {
            if (directoryPath == null || directoryPath.isEmpty()) {
                throw InputException("请选择图片所在文件夹!")
            }
            if (imageFormats == null || imageFormats.isEmpty()) {
                throw InputException("请至少选择一个图片格式!")
            }
            if (imageMargin == null) {
                throw InputException("请选择图片之间的间隔!")
            }
            if (imageMargin == 0) {
                throw InputException("请输入正确的图片间距!")
            }
            if (eachLineNum == null) {
                throw InputException("请选择每行的图片数量!")
            }
            if (eachLineNum == 0) {
                throw InputException("请输入正确的每行显示数量!")
            }
            if (imageQuality == null) {
                throw InputException("请选择合并后图片的质量!")
            }
            if (arrangeMode == null) {
                throw InputException("请选择图片排列方式!")
            }
            if (outputName == null || outputName.isEmpty()) {
                throw InputException("请输入合并后结果的文件名!")
            }
            return ImageMergePropertiesBean(
                    directoryPath = directoryPath,
                    imageFormats = imageFormats,
                    imageMargin = imageMargin,
                    eachLineNum = eachLineNum,
                    imageQuality = imageQuality,
                    arrangeMode = arrangeMode,
                    outputName = outputName
            )
        }
    }
}