import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.imageio.ImageIO

object ImageCompressUtil {

    fun compress(imageFiles: List<File>, scale: Float): List<BufferedImage?>? {
        try {
            val pool = Executors.newFixedThreadPool(8)
            val futureList = mutableListOf<Future<BufferedImage?>>()

            imageFiles.forEach {
                val compressImage = Callable { compressImage(it, scale) }
                val compressResult = pool.submit(compressImage)
                futureList.add(compressResult)
            }
            return futureList.map { it.get() }
        } catch (e: Exception) {
            e.printStackTrace()
            println("线程池处理异常:${e.message}")
            return null
        }
    }

    /**
     * 根据比例压缩图片
     * @param imageFile 图片文件
     * @param scale，压缩比例
     * @return 压缩后的结果，null 表示压缩失败
     */
    private fun compressImage(imageFile: File, scale: Float): BufferedImage? {
        try {
            // 读取图片
            val bufferedImage = ImageIO.read(imageFile)
            // 计算压缩后的尺寸
            val newWidth = (bufferedImage.width * scale).toInt()
            val newHeight = (bufferedImage.height * scale).toInt()

            val outputImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
            outputImage.graphics.run {
                // 写入图片
                val image = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
                drawImage(image, 0, 0, null)
                this.dispose()
            }
            return outputImage
        } catch (e: IOException) {
            e.printStackTrace()
            println("压缩图片失败 -> ${imageFile.absolutePath}")
            return null
        }
    }
}