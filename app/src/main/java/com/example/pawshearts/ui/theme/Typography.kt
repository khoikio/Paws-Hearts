package com.example.pawshearts.ui.theme

import androidx.compose.material3.Typography

// Custom Typography sử dụng font mặc định của Material 3
// Nếu muốn dùng font Archivo, uncomment và sửa lại như sau:

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pawshearts.R
import java.time.format.TextStyle

val Archivo = FontFamily(
     Font(R.font.archivo_regular, FontWeight.Normal),
     Font(R.font.archivo_bold, FontWeight.Bold)
 )


val AppTypography = Typography(
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    )
)

