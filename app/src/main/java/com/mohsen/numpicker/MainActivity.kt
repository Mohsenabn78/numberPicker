package com.mohsen.numpicker

import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.mohsen.numpicker.ui.theme.NumPickerTheme
import kotlinx.coroutines.launch

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
fun indicatorUpdateHeightAnimation(
    number: Int,
    pickerStyle: PickerStyle = PickerStyle(),
    duration: Int = 500
): Float {
    var animVal by remember {
        mutableStateOf(Animatable(0f))
    }
    LaunchedEffect(key1 = number) {
        launch {
            animVal = Animatable(0f)
            animVal.animateTo(
                targetValue = pickerStyle.reducerAmount,
                animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
            )
        }
    }

    return animVal.value
}


@Composable
fun textTranslationAnimation(
    number: Int,
    duration: Int = 300,
    pickerStyle: PickerStyle = PickerStyle()
): Float {
    var animVal by remember {
        mutableStateOf(Animatable(-pickerStyle.translateRate))
    }
    LaunchedEffect(key1 = number) {
        launch {
            animVal = Animatable(-pickerStyle.translateRate)
            animVal.animateTo(
                targetValue = pickerStyle.translateRate,
                animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
            )
        }
    }

    return animVal.value
}

@Composable
fun textColorAnimation(
    number: Int,
    duration: Int = 300,
    pickerStyle: PickerStyle = PickerStyle()
): Color {
    var animVal by remember {
        mutableStateOf(Animatable(Color(red = 28, green = 29, blue = 36)))
    }
    LaunchedEffect(key1 = number) {
        launch {
            animVal = Animatable(Color(red = 28, green = 29, blue = 36))
            animVal.animateTo(
                targetValue = pickerStyle.textColor,
                animationSpec = tween(
                    durationMillis = duration, easing = FastOutSlowInEasing
                )
            )
        }
    }

    return animVal.value
}

@Composable
fun PickerScreen(modifier: Modifier = Modifier, pickerStyle: PickerStyle) {

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

    var bottomReducer by remember {
        mutableStateOf(0f)
    }

    var topReducer by remember {
        mutableStateOf(0f)
    }


    var textTranslateAnim by remember {
        mutableStateOf(0f)
    }

    var textColor by remember {
        mutableStateOf(pickerStyle.textColor)
    }

    var clickDirection by remember {
        mutableStateOf(ClickDirection.NORMAL)
    }

    val typeFace = ResourcesCompat.getFont(LocalContext.current, R.font.number_font)

    when {
        clickDirection == ClickDirection.TOP && number == pickerStyle.targetValue -> topReducer =
            indicatorUpdateHeightAnimation(number = number)
        clickDirection == ClickDirection.BOTTOM && number == pickerStyle.initialValue -> bottomReducer =
            indicatorUpdateHeightAnimation(number = number)
        else -> {
            topReducer = 0f
            bottomReducer = 0f
        }
    }

    when {
        clickDirection == ClickDirection.TOP && number != pickerStyle.targetValue -> {
            textTranslateAnim = textTranslationAnimation(number = number)
            textColor = textColorAnimation(number = number)

        }
        clickDirection == ClickDirection.BOTTOM && number != pickerStyle.initialValue -> {
            textTranslateAnim = -textTranslationAnimation(number = number)
            textColor = textColorAnimation(number = number)
        }
    }

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(true) {
            detectTapGestures {
                clickRect = Rect(it, pickerStyle.arcRadius.dp.toPx() / 2)
                if (topIndicatorRect.contains(clickRect.center) && number < 4) {
                    clickDirection = ClickDirection.TOP
                    number++
                }
                if (bottomIndicatorRect.contains(clickRect.center) && number > 0) {
                    clickDirection = ClickDirection.BOTTOM
                    number--
                }
            }
        }) {

        parallelRect.apply {
            top = center.y - pickerStyle.lineHeight.dp.toPx() / 2 + topReducer.dp.toPx()
            bottom = center.y + pickerStyle.lineHeight.dp.toPx() / 2 - bottomReducer.dp.toPx()
            left = center.x - pickerStyle.lineBetweenSpace.dp.toPx() / 2
            right = center.x + pickerStyle.lineBetweenSpace.dp.toPx() / 2

        }

        topIndicatorRect = Rect(
            Offset((parallelRect.left + parallelRect.right) / 2, (parallelRect.top)),
            pickerStyle.arcRadius.dp.toPx() / 2
        )
        bottomIndicatorRect = Rect(
            Offset((parallelRect.left + parallelRect.right) / 2, (parallelRect.bottom)),
            pickerStyle.arcRadius.dp.toPx() / 2
        )


        parallelLinePath.apply {
            moveTo(parallelRect.right, parallelRect.bottom)
            lineTo(parallelRect.right, parallelRect.top)
            moveTo(parallelRect.left, parallelRect.bottom)
            lineTo(parallelRect.left, parallelRect.top)

        }

        topIndicatorPath.apply {
            addArc(topIndicatorRect, 180f, 180f)
            if (topReducer == 0f) {
                moveTo(
                    topIndicatorRect.center.x,
                    topIndicatorRect.center.y - pickerStyle.arrowHeight.dp.toPx()
                )
                lineTo(
                    topIndicatorRect.center.x - pickerStyle.arrowHeight.dp.toPx(),
                    topIndicatorRect.center.y + pickerStyle.arrowHeight.dp.toPx() - pickerStyle.arrowHeight.dp.toPx()
                )
                moveTo(
                    topIndicatorRect.center.x,
                    topIndicatorRect.center.y - pickerStyle.arrowHeight.dp.toPx()
                )
                lineTo(
                    topIndicatorRect.center.x + pickerStyle.arrowHeight.dp.toPx(),
                    topIndicatorRect.center.y + pickerStyle.arrowHeight.dp.toPx() - pickerStyle.arrowHeight.dp.toPx()
                )
            }
        }

        bottomIndicatorPath.apply {
            addArc(bottomIndicatorRect, 0f, 180f)
            if (bottomReducer == 0f) {
                moveTo(
                    bottomIndicatorRect.center.x,
                    bottomIndicatorRect.center.y + pickerStyle.arrowHeight.dp.toPx()
                )
                lineTo(
                    bottomIndicatorRect.center.x - pickerStyle.arrowHeight.dp.toPx(),
                    bottomIndicatorRect.center.y - pickerStyle.arrowHeight.dp.toPx() + pickerStyle.arrowHeight.dp.toPx()
                )
                moveTo(
                    bottomIndicatorRect.center.x,
                    bottomIndicatorRect.center.y + pickerStyle.arrowHeight.dp.toPx()
                )
                lineTo(
                    bottomIndicatorRect.center.x + pickerStyle.arrowHeight.dp.toPx(),
                    bottomIndicatorRect.center.y - pickerStyle.arrowHeight.dp.toPx() + pickerStyle.arrowHeight.dp.toPx()
                )
            }
        }

        drawPath(
            path = topIndicatorPath,
            color = pickerStyle.borderColor,
            style = Stroke(width = pickerStyle.borderStroke.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = bottomIndicatorPath,
            color = pickerStyle.borderColor,
            style = Stroke(width = pickerStyle.borderStroke.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = parallelLinePath,
            color = pickerStyle.borderColor,
            style = Stroke(width = pickerStyle.borderStroke.dp.toPx())
        )


        drawContext.canvas.nativeCanvas.apply {
            textPaint.apply {
                this.color = textColor.toArgb()
                this.textSize = pickerStyle.textSize.sp.toPx()
                this.typeface = typeFace
                this.isAntiAlias = true
            }
            textPaint.getTextBounds(number.toString(), 0, number.toString().length, textRect)
            val textWidth = if (number == 1) 3 * textRect.width() / 4 else textRect.width() / 2
            val textHeight = when {
                topReducer != 0f -> parallelRect.centerY()
                bottomReducer != 0f -> parallelRect.centerY() + 2 * textRect.height() / 3f
                else -> parallelRect.centerY() + textRect.height() / 2f
            }

            translate(0f, textTranslateAnim) {
                drawText(
                    number.toString(),
                    (parallelRect.centerX() - textWidth),
                    textHeight,
                    textPaint
                )
                fadeIn(animationSpec = tween(durationMillis = 300))
                textTranslateAnim = 0f
                textColor = pickerStyle.textColor
            }

        }

        topIndicatorPath.reset()
        bottomIndicatorPath.reset()
        parallelLinePath.reset()

    }
}


enum class ClickDirection {
    TOP,
    BOTTOM,
    NORMAL,
}