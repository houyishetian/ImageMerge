package com.lin.entity

import com.lin.utils.MergeImageUtil

val imageFormatMap = mapOf(
        "png" to "png",
        "jpg" to "jpg",
        "jpeg" to "jpeg"
)

val imageMarginMap = mapOf(
        "10px" to 10,
        "30px" to 30,
        "50px" to 50,
        "70px" to 70
)

val eachLineNumMap = mapOf(
        "1张" to 1,
        "3张" to 3,
        "5张" to 5
)

val imageQualityMap = mapOf(
        "高(1.0)" to 1.0f,
        "中(0.75)" to 0.75f,
        "一般(0.5)" to 0.5f,
        "低(0.35)" to 0.35f
)

val arrangeModeMap = mapOf(
        "整齐表格" to MergeImageUtil.ArrangeMode.FORM,
        "实际尺寸" to MergeImageUtil.ArrangeMode.SIZE
)