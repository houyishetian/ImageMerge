package com.lin.controller

import com.lin.entity.eachLineNumMap
import com.lin.entity.imageFormatMap
import com.lin.entity.imageMarginMap
import com.lin.entity.imageQualityMap
import com.lin.utils.FileUtil
import com.lin.utils.ImageCompressUtil
import com.lin.utils.MergeImageUtil
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.paint.Paint
import javafx.stage.DirectoryChooser
import java.io.File

class ImageMergePaneController {

    // 路径选择的 view
    @FXML
    lateinit var tfImageDirectory: TextField // 路径显示文本
    @FXML
    lateinit var btnSelectImageDirectory: Button // 路径选择文本

    //图片格式的 checkbox
    @FXML
    lateinit var cbImageFormatPng: CheckBox
    @FXML
    lateinit var cbImageFormatJpg: CheckBox
    @FXML
    lateinit var cbImageFormatJpeg: CheckBox
    private val imageFormatCbList: List<CheckBox> by lazy {
        listOf(cbImageFormatPng, cbImageFormatJpg, cbImageFormatJpeg)
    }

    // 图片间距的 views
    @FXML
    lateinit var rbImageMargin10: RadioButton
    @FXML
    lateinit var rbImageMargin30: RadioButton
    @FXML
    lateinit var rbImageMargin50: RadioButton
    @FXML
    lateinit var rbImageMargin70: RadioButton
    @FXML
    lateinit var rbImageMarginCustomize: RadioButton
    @FXML
    lateinit var tfImageMarginCustomize: TextField
    private lateinit var tgImageMargin: ToggleGroup

    // 每行显示的 views
    @FXML
    lateinit var rbEachLine1: RadioButton
    @FXML
    lateinit var rbEachLine3: RadioButton
    @FXML
    lateinit var rbEachLine5: RadioButton
    @FXML
    lateinit var rbEachLineCustomize: RadioButton
    @FXML
    lateinit var tfEachLineCustomize: TextField
    private lateinit var tgEachLine: ToggleGroup

    // 合成质量的 views
    @FXML
    lateinit var rbImageQualityHigh: RadioButton
    @FXML
    lateinit var rbImageQualityMiddle: RadioButton
    @FXML
    lateinit var rbImageQualityNormal: RadioButton
    @FXML
    lateinit var rbImageQualityLow: RadioButton
    private lateinit var tgImageQuality: ToggleGroup

    // 输出文件名
    @FXML
    lateinit var tfOutputName: TextField

    // 合成状态
    @FXML
    lateinit var tfMergeStatus: Label

    // 开始合成按钮
    lateinit var btnStartMerge: Button

    fun onPathSelectedDragOver(dragEvent: DragEvent) {
        dragEvent.acceptTransferModes(*TransferMode.ANY)
    }

    fun onPathSelectedDragDropped(dragEvent: DragEvent) {
        dragEvent.dragboard.let {
            if (it.hasFiles()) {
                val file = it.files[0]
                if (file.isDirectory) {
                    tfImageDirectory.text = file.absolutePath
                }
            }
        }
    }

    fun onSelectPathClicked() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "请选择图片所在文件夹"
        val directory = directoryChooser.showDialog(btnSelectImageDirectory.scene.window)
        tfImageDirectory.text = directory.absolutePath
    }

    fun startMerge() {
        val directoryPath = getDirectoryOfImages()
        if (directoryPath == null) {
            showFailedMergeStatus("请选择图片所在文件夹!")
            return
        }

        val imageFormats = getImageFormat()
        if (imageFormats == null) {
            showFailedMergeStatus("请至少选择一个图片格式!")
            return
        }
        val imageMargin = getImageMargin()
        if (imageMargin == null) {
            showFailedMergeStatus("请选择图片之间的间隔!")
            return
        }

        val eachLineNum = getEachLineNum()
        if (eachLineNum == null) {
            showFailedMergeStatus("请选择每行的图片数量!")
            return
        }

        val imageQuality = getMergeQuality()
        if (imageQuality == null) {
            showFailedMergeStatus("请选择合成图片的质量!")
            return
        }

        val outputName = getOutputName()
        if (outputName == null) {
            showFailedMergeStatus("请输入合成结果的文件名!")
            return
        }

        mergeImage(directoryPath = directoryPath,
                imageFormats = imageFormats,
                imageMargin = imageMargin,
                eachLineNum = eachLineNum,
                imageQuality = imageQuality,
                outputName = outputName)
    }

    fun initVaribles(
            defaultImageFormats: List<String> = listOf("png"),
            defaultImageMargin: String = "30px",
            defaultEachLineNum: String = "5张",
            defaultImageQuality: String = "高(1.0)"
    ) {
        tgImageMargin = ToggleGroup()
        bindToggleGroupAndItsChildren(tgImageMargin, rbImageMargin10, rbImageMargin30, rbImageMargin50, rbImageMargin70, rbImageMarginCustomize)
        bindCustomizedRadioBtnAndTextField(rbImageMarginCustomize, tfImageMarginCustomize)

        tgEachLine = ToggleGroup()
        bindToggleGroupAndItsChildren(tgEachLine, rbEachLine1, rbEachLine3, rbEachLine5, rbEachLineCustomize)
        bindCustomizedRadioBtnAndTextField(rbEachLineCustomize, tfEachLineCustomize)

        tgImageQuality = ToggleGroup()
        bindToggleGroupAndItsChildren(tgImageQuality, rbImageQualityHigh, rbImageQualityMiddle, rbImageQualityNormal, rbImageQualityLow)

        // 设置默认选中 item
        setDefaultSelectedItems(defaultImageFormats, defaultImageMargin, defaultEachLineNum, defaultImageQuality)
    }

    /**
     * 将 toggle group 和其 child Radio button 绑定起来
     */
    private fun bindToggleGroupAndItsChildren(toggleGroup: ToggleGroup, vararg radioButtons: RadioButton) {
        radioButtons.forEach {
            it.toggleGroup = toggleGroup
        }
    }

    /**
     * 将 customize radio button 和 后边的 customize text field 绑定起来
     */
    private fun bindCustomizedRadioBtnAndTextField(radioBtn: RadioButton, textField: TextField) {
        radioBtn.selectedProperty().addListener { _, _, selected ->
            if (selected) {
                textField.isEditable = true
                textField.isMouseTransparent = false
            } else {
                textField.isEditable = false
                textField.isMouseTransparent = true
                textField.text = ""
            }
        }
    }

    /**
     * 获取user输入的文件夹
     */
    private fun getDirectoryOfImages(): String? {
        return tfImageDirectory.text.takeIf { it.isNotEmpty() }
    }

    /**
     * 获取 user 选择的图片格式
     */
    private fun getImageFormat(): List<String>? {
        return imageFormatCbList.map {
            imageFormatMap[it.takeIf { it.isSelected }?.run { it.text }]
        }.filterNotNull().takeIf { it.isNotEmpty() }
    }

    /**
     * 获取 user 选择的图片间距
     */
    private fun getImageMargin(): Int? {
        val selectedOne = tgImageMargin.selectedToggle as? RadioButton
        return when (selectedOne) {
            rbImageMarginCustomize -> {
                try {
                    tfImageMarginCustomize.text.toInt()
                } catch (exception: Exception) {
                    null
                }
            }
            else -> {
                imageMarginMap[selectedOne?.text]
            }
        }
    }

    /**
     * 获取 user 选择的每行数量
     */
    private fun getEachLineNum(): Int? {
        val selectedOne = tgEachLine.selectedToggle as? RadioButton
        return when (selectedOne) {
            rbEachLineCustomize -> {
                try {
                    tfEachLineCustomize.text.toInt()
                } catch (exception: Exception) {
                    null
                }
            }
            else -> {
                eachLineNumMap[selectedOne?.text]
            }
        }
    }

    /**
     * 获取 user 选择的图片质量
     */
    private fun getMergeQuality(): Float? {
        return imageQualityMap[(tgImageQuality.selectedToggle as? RadioButton)?.text]
    }

    /**
     * 获取 user 输入的 合成图片名
     */
    private fun getOutputName(): String? {
        return tfOutputName.text.takeIf { it.isNotEmpty() }
    }

    /**
     * 显示合成成功 status
     */
    private fun showSuccessMergeStatus(result: String) {
        tfMergeStatus.textFill = Paint.valueOf("#0ddd2a") // 显示绿色
        tfMergeStatus.text = result
    }

    /**
     * 显示合成失败 status
     */
    private fun showFailedMergeStatus(errorStatus: String) {
        tfMergeStatus.textFill = Paint.valueOf("#e00d0d") // 显示红色
        tfMergeStatus.text = errorStatus
    }

    private fun setDefaultSelectedItems(
            defaultImageFormats: List<String>,
            defaultImageMargin: String,
            defaultEachLineNum: String,
            defaultImageQuality: String
    ) {
        imageFormatCbList.filter { it.text in defaultImageFormats }.forEach {
            it.isSelected = true
        }
        listOf(rbImageMargin10, rbImageMargin30, rbImageMargin50, rbImageMargin70).find { it.text == defaultImageMargin }?.isSelected = true

        listOf(rbEachLine1, rbEachLine3, rbEachLine5).find { it.text == defaultEachLineNum }?.isSelected = true

        listOf(rbImageQualityHigh, rbImageQualityMiddle, rbImageQualityNormal, rbImageQualityLow).find { it.text == defaultImageQuality }?.isSelected = true
    }

    private fun mergeImage(directoryPath: String,
                           imageFormats: List<String>,
                           imageMargin: Int,
                           eachLineNum: Int,
                           imageQuality: Float,
                           outputName: String) {
        val startTime = System.currentTimeMillis()
        FileUtil.getAllPics(directoryPath, imageFormats)?.let {
            val readImagesList = ImageCompressUtil.compress(it, imageQuality)
            readImagesList?.filterNotNull()?.let {
                // 将所有压缩后的额图片合并，间距 30px，每行最多5个
                MergeImageUtil(it, columnCount = eachLineNum).mergeImage(imageMargin)?.let {
                    val desFile = File(File(directoryPath), "$outputName.png")
                    // 写入
                    FileUtil.writeImageToFile(it, desFile.absolutePath)
                    val cost = System.currentTimeMillis() - startTime
                    val mill = cost % 1000
                    val second = cost / 1000
                    showSuccessMergeStatus("合成成功，检查:${desFile.absolutePath}, 耗时: $second.${mill}秒")
                }
            } ?: let {
                showFailedMergeStatus("压缩失败!")
            }
        } ?: let {
            showFailedMergeStatus("$directoryPath 不合法或没有读取到符合要求的图片!")
        }
    }
}