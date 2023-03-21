package com.kgh.signezprototype.ui.theme

import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kgh.signezprototype.R

val NotoSansKR = FontFamily(
    Font(R.font.noto_sans_kr_thin, FontWeight.Thin),
    Font(R.font.noto_sans_kr_light, FontWeight.Light),
    Font(R.font.noto_sans_kr_regular),
    Font(R.font.noto_sans_kr_medium, FontWeight.Medium),
    Font(R.font.noto_sans_kr_bold, FontWeight.Bold),
    Font(R.font.noto_sans_kr_black, FontWeight.Black)
)

// Set of Material typography styles to start with
val Typography = Typography(
    // Tutorial 제목
    h1 = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    // App Bar 제목
    h2 = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    // 컴포넌트 제목
    h3 = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    // 회색 소 제목
    h4 = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    body1 = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    body2 = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
    button = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    ),
)
