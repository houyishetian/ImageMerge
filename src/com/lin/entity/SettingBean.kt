package com.lin.entity

import java.io.Serializable

class SettingBean(
        val imageFormatIndexes: List<Int>,
        val imageMarginIndex: Int,
        val imageMarginValue: Int? = null,
        val eachLineNumIndex: Int,
        val eachLineNumValue: Int? = null,
        val mergeQualityIndex: Int,
        val usingPathAsOutputName: Boolean
) : Serializable