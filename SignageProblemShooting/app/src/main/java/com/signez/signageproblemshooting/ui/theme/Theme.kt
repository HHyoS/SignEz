package com.signez.signageproblemshooting.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val OneLightColorPalette = lightColors(

    // 튜토리얼 버튼
    primary = OneBGBlue,
    // 외부 이동 버튼
    primaryVariant = OneBtnGrey,
    // 입력창, 선택된 리스트
    secondary = OneBGDarkGrey,
    // 버튼 아웃라인
    secondaryVariant = OneOutLineGrey,
    // 기본 백그라운드 색상
    background = OneBGGrey,
    // 컴포넌트들 기본 색상
    surface = OneBGWhite,

    // 버튼 위 텍스트
    onPrimary = OneTextWhite,
    // 기본 텍스트 색상, 검정
    onSurface = OneTextBlack,
    // 회색 글씨
    onBackground = OneTextGrey,
    // 얕은 검정
    onSecondary =  OneTextLightBlack
    )

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200
)

@Composable
fun SignEzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        OneLightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}