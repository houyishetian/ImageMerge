package com.lin.controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.stage.DirectoryChooser

class ImageMergePaneController {

    @FXML
    lateinit var directoryInputField: TextField

    @FXML
    lateinit var directorySelectBtn: Button

    fun onPathSelectedDragOver(dragEvent: DragEvent) {
        dragEvent.acceptTransferModes(*TransferMode.ANY)
    }

    fun onPathSelectedDragDropped(dragEvent: DragEvent) {
        dragEvent.dragboard.let {
            if (it.hasFiles()) {
                val file = it.files[0]
                if (file.isDirectory) {
                    directoryInputField.text = file.absolutePath
                }
            }
        }
    }

    fun onSelectPathClicked() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "请选择图片所在文件夹"
        val directory = directoryChooser.showDialog(directorySelectBtn.scene.window)
        directoryInputField.text = directory.absolutePath
    }
}