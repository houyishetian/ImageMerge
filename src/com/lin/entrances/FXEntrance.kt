package com.lin.entrances

import com.lin.controller.ImageMergePaneController
import com.lin.utils.SettingsUtil
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Stage

class FXEntrance : Application() {

    private val screenWidth = 800.0
    private val screenHeight = 550.0

    override fun start(primaryStage: Stage?) {
        primaryStage?.run {

            icons.add(Image("image/image_merge.png"))
            title = "ImageMerge"


            val loader = FXMLLoader()
            loader.location = this@FXEntrance.javaClass.classLoader.getResource("com/lin/fx/pane_image_merge.fxml")
            val pane = loader.load<Pane>()
            val controller = loader.getController<ImageMergePaneController>()

            controller.pane = pane
            // 设置默认值，如果有以前保存的，取出；如果没有，使用默认值
            controller.initVaribles(SettingsUtil.getSetting() ?: SettingsUtil.getDefaultSetting())

            val scene = Scene(pane, screenWidth, screenHeight)

            setScene(scene)

            width = screenWidth
            height = screenHeight

            setCloseEvent(this)

            isResizable = false

            show()
        }
    }

    private fun setCloseEvent(primaryStage: Stage) {
        Platform.setImplicitExit(false)
        primaryStage.setOnCloseRequest {
            it.consume()

            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.title = "退出程序"
            alert.headerText = null
            alert.contentText = "是否要退出程序？"

            val result = alert.showAndWait()
            if (result.get() == ButtonType.OK) {
                Platform.exit()
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(FXEntrance::class.java)
}