package com.mybaby.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MyBabyTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 31.sp,
    ),
    headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 26.sp,
    ),
    headlineSmall = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 23.sp,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 21.sp,
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
    ),
)
