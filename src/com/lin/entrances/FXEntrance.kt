package com.lin.entrances

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
    private val screenHeight = 500.0

    override fun start(primaryStage: Stage?) {
        primaryStage?.run {

            icons.add(Image("image/image_merge.png"))
            title = "ImageMerge"


            val pane = FXMLLoader.load<Pane>(this@FXEntrance.javaClass.classLoader.getResource("com/lin/fx/pane_image_merge.fxml"))

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