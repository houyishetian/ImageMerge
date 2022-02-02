package com.lin.controller

import com.lin.entity.eachLineNumMap
import com.lin.entity.imageFormatMap
import com.lin.entity.imageMarginMap
import com.lin.entity.imageQualityMap
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.paint.Paint
import javafx.stage.DirectoryChooser

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

        showSuccessMergeStatus("合成结束!")
    }

    fun initVaribles() {
        tgImageMargin = ToggleGroup()
        bindToggleGroupAndItsChildren(tgImageMargin, rbImageMargin10, rbImageMargin30, rbImageMargin50, rbImageMargin70, rbImageMarginCustomize)
        bindCustomizedRadioBtnAndTextField(rbImageMarginCustomize, tfImageMarginCustomize)

        tgEachLine = ToggleGroup()
        bindToggleGroupAndItsChildren(tgEachLine, rbEachLine1, rbEachLine3, rbEachLine5, rbEachLineCustomize)
        bindCustomizedRadioBtnAndTextField(rbEachLineCustomize, tfEachLineCustomize)

        tgImageQuality = ToggleGroup()
        bindToggleGroupAndItsChildren(tgImageQuality, rbImageQualityHigh, rbImageQualityMiddle, rbImageQualityNormal, rbImageQualityLow)
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

    private fun getDirectoryOfImages(): String? {
        return tfImageDirectory.text.takeIf { it.isNotEmpty() }
    }

    private fun getImageFormat(): List<String>? {
        return listOf(cbImageFormatPng, cbImageFormatJpg, cbImageFormatJpeg).map {
            imageFormatMap[it.takeIf { it.isSelected }?.run { it.text }]
        }.filterNotNull().takeIf { it.isNotEmpty() }
    }

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

    private fun getMergeQuality(): Double? {
        return imageQualityMap[(tgImageQuality.selectedToggle as? RadioButton)?.text]
    }

    private fun getOutputName(): String? {
        return tfOutputName.text.takeIf { it.isNotEmpty() }
    }

    private fun showSuccessMergeStatus(result: String) {
        tfMergeStatus.textFill = Paint.valueOf("#0ddd2a") // 显示绿色
        tfMergeStatus.text = result
    }

    private fun showFailedMergeStatus(errorStatus: String) {
        tfMergeStatus.textFill = Paint.valueOf("#e00d0d") // 显示红色
        tfMergeStatus.text = errorStatus
    }
}