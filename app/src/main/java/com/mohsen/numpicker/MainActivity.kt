package com.mohsen.numpicker

import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohsen.numpicker.ui.theme.NumPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NumPickerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(red = 28, green = 29, blue = 36)
                ) {
                    PickerScreen(pickerStyle = PickerStyle())
                }
            }
        }
    }
}

@Composable
fun PickerScreen(modifier: Modifier=Modifier,pickerStyle: PickerStyle) {

    var number by remember { mutableStateOf(1) }

    val topIndicatorPath by lazy { Path() }
    val bottomIndicatorPath by lazy { Path() }
    val parallelLinePath by lazy { Path() }
    val textPaint by lazy { Paint() }

    val parallelRect by lazy { RectF() }

    var topIndicatorRect by remember {
        mutableStateOf(Rect(Offset.Zero, Size.Zero))
    }
    var bottomIndicatorRect by remember {
        mutableStateOf(Rect(Offset.Zero, Size.Zero))
    }

    val textRect by remember {
        mutableStateOf(android.graphics.Rect())
    }

    var clickRect by remember {
        mutableStateOf(Rect(Offset.Zero, Size.Zero))
    }

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(true) {
            detectTapGestures {
                clickRect = Rect(it, pickerStyle.arcRadius.dp.toPx() / 2)
                if (topIndicatorRect.contains(clickRect.center)) {
                    number++
                }
                if (bottomIndicatorRect.contains(clickRect.center)) {
                    number--
                }
            }
        }){

        parallelRect.apply {
            top = center.y-pickerStyle.lineHeight.dp.toPx()/2
            bottom = center.y+pickerStyle.lineHeight.dp.toPx()/2
            left = center.x-pickerStyle.lineBetweenSpace.dp.toPx()/2
            right = center.x+pickerStyle.lineBetweenSpace.dp.toPx()/2

        }

         topIndicatorRect=Rect(Offset((parallelRect.left+parallelRect.right)/2,(parallelRect.top)),pickerStyle.arcRadius.dp.toPx()/2)
         bottomIndicatorRect=Rect(Offset((parallelRect.left+parallelRect.right)/2,(parallelRect.bottom)),pickerStyle.arcRadius.dp.toPx()/2)


        parallelLinePath.apply {
            moveTo(parallelRect.right,parallelRect.bottom)
            lineTo(parallelRect.right,parallelRect.top)
            moveTo(parallelRect.left,parallelRect.bottom)
            lineTo(parallelRect.left,parallelRect.top)

        }

        topIndicatorPath.apply {
            addArc(topIndicatorRect,180f,180f)
            moveTo(topIndicatorRect.center.x,topIndicatorRect.center.y-pickerStyle.arrowHeight.dp.toPx())
            lineTo(topIndicatorRect.center.x-pickerStyle.arrowHeight.dp.toPx(),topIndicatorRect.center.y+pickerStyle.arrowHeight.dp.toPx()-pickerStyle.arrowHeight.dp.toPx())
            moveTo(topIndicatorRect.center.x,topIndicatorRect.center.y-pickerStyle.arrowHeight.dp.toPx())
            lineTo(topIndicatorRect.center.x+pickerStyle.arrowHeight.dp.toPx(),topIndicatorRect.center.y+pickerStyle.arrowHeight.dp.toPx()-pickerStyle.arrowHeight.dp.toPx())
        }

       bottomIndicatorPath.apply {
           addArc(bottomIndicatorRect,0f,180f)
           moveTo(bottomIndicatorRect.center.x,bottomIndicatorRect.center.y+pickerStyle.arrowHeight.dp.toPx())
           lineTo(bottomIndicatorRect.center.x-pickerStyle.arrowHeight.dp.toPx(),bottomIndicatorRect.center.y-pickerStyle.arrowHeight.dp.toPx()+pickerStyle.arrowHeight.dp.toPx())
           moveTo(bottomIndicatorRect.center.x,bottomIndicatorRect.center.y+pickerStyle.arrowHeight.dp.toPx())
           lineTo(bottomIndicatorRect.center.x+pickerStyle.arrowHeight.dp.toPx(),bottomIndicatorRect.center.y-pickerStyle.arrowHeight.dp.toPx()+pickerStyle.arrowHeight.dp.toPx())
       }

        drawPath(path = topIndicatorPath, color = pickerStyle.borderColor, style = Stroke(width = pickerStyle.borderStroke.dp.toPx(), cap = StrokeCap.Round))
        drawPath(path = bottomIndicatorPath, color = pickerStyle.borderColor, style = Stroke(width = pickerStyle.borderStroke.dp.toPx(), cap = StrokeCap.Round))
        drawPath(path = parallelLinePath, color = pickerStyle.borderColor, style = Stroke(width = pickerStyle.borderStroke.dp.toPx()))

        drawContext.canvas.nativeCanvas.apply {
              textPaint.apply {
                this.color=pickerStyle.textColor.toArgb()
                this.textSize=pickerStyle.textSize.sp.toPx()
                this.isAntiAlias=true
            }
            textPaint.getTextBounds(number.toString(),0,number.toString().length,textRect)
            Log.e("TAG", "PickerScreen: ${textRect.width()}" )
            drawText(number.toString(),(parallelRect.centerX()-(textRect.width()/2f)),(parallelRect.centerY()+textRect.height()/2f),textPaint)
            Log.e("TAG", "PickerScreen: ${parallelRect.centerX()}  ${(parallelRect.centerX()-(textRect.width()/2f))}" )
        }

    }
}
