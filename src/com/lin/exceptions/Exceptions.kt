package com.lin.exceptions

class FileNotDirectoryException(private val path: String) : Exception() {
    override val message: String?
        get() = "$path 不是文件夹!"
}

class WriteImageFailedException(private val failMessage: String) : Exception() {
    override val message: String?
        get() = "保存图片失败:$failMessage"
}

class ThreadPoolFailException(private val failMessage: String) : Exception() {
    override val message: String?
        get() = "压缩线程池处理异常:$failMessage"
}

class CompressImageIOException(private val path: String, private val failMessage: String) : Exception() {
    override val message: String?
        get() = "\"$path\"压缩出现异常: $failMessage"
}

class DirectoryIsEmptyException(private val path: String, private val formats:String) : Exception() {
    override val message: String?
        get() = "$path 下未找到格式为 ${formats} 的图片，请检查路径或设置中的图片格式!"
}

class InputException(private val failMessage: String) : Exception() {
    override val message: String?
        get() = failMessage
}

class OutputFileAlreadyExistException(private val outputName: String) : Exception() {
    override val message: String?
        get() = "文件 \"$outputName\" 已存在，请返回修改输出文件名或修改已存在文件名!"
}