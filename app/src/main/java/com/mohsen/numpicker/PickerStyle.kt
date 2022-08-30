package com.mohsen.numpicker

import androidx.compose.ui.graphics.Color


data class PickerStyle(
    val lineBetweenSpace:Float=90f,
    val lineHeight:Float=100f,
    val arrowHeight:Float=20f,
    val borderStroke:Float=3f,
    val borderColor:Color= Color.White,
    val textColor:Color= Color(1,254,173),
    val textSize:Int= 50,
    val arcRadius:Float=90f,
    val initialValue:Int=0,
    val targetValue:Int=4,
    val reducerAmount:Float=30f,
    val translateRate:Float=40f
)
