package com.example.smart_energy_optimizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Palette ───────────────────────────────────────────────────────────────────
val Purple      = Color(0xFF8B5CF6)
val PurpleLight = Color(0xFFA78BFA)
val PurpleFaint = Color(0xFF8B5CF6).copy(alpha = 0.10f)
val RedColor    = Color(0xFFEF4444)
val GreenColor  = Color(0xFF22C55E)
val BlueColor   = Color(0xFF3B82F6)
val OrangeColor = Color(0xFFF97316)
val CyanColor   = Color(0xFF06B6D4)

// ── Dark theme colours ────────────────────────────────────────────────────────
val DarkBg      = Color(0xFF0D0D0F)
val DarkCard    = Color(0xFF1A1A1F)
val DarkBorder  = Color(0xFFFFFFFF).copy(alpha = 0.08f)
val DarkMuted   = Color(0xFF888888)

// ── Light theme colours ───────────────────────────────────────────────────────
val LightBg     = Color(0xFFF5F5F7)
val LightCard   = Color(0xFFFFFFFF)
val LightBorder = Color(0xFF000000).copy(alpha = 0.08f)
val LightMuted  = Color(0xFF666666)

// ── Theme wrappers ─────────────────────────────────────────────────────────────
@Composable
fun AppTheme(darkMode: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkMode) darkColorScheme(
        background = DarkBg,
        surface = DarkCard,
        onBackground = Color.White,
        onSurface = Color.White,
        primary = Purple,
        onPrimary = Color.White,
        outline = DarkBorder
    ) else lightColorScheme(
        background = LightBg,
        surface = LightCard,
        onBackground = Color(0xFF111111),
        onSurface = Color(0xFF111111),
        primary = Purple,
        onPrimary = Color.White,
        outline = LightBorder
    )
    MaterialTheme(colorScheme = colors, content = content)
}

// ── Helper: card ──────────────────────────────────────────────────────────────
@Composable
fun EnergyCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(contentPadding)
    ) { content() }
}

// ── Helper: section label ──────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = DarkMuted,
        letterSpacing = 1.sp
    )
}

// ── Helper: purple toggle ─────────────────────────────────────────────────────
@Composable
fun PurpleSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Purple,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Purple.copy(alpha = 0.25f)
        )
    )
}

// ── Helper: icon circle/rounded ───────────────────────────────────────────────
@Composable
fun IconBox(
    emoji: String,
    size: Dp = 42.dp,
    cornerRadius: Dp = 12.dp,
    bgColor: Color = PurpleFaint
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 20.sp)
    }
}

// ── Category → emoji ─────────────────────────────────────────────────────────
fun categoryEmoji(category: String) = when (category) {
    "AC"          -> "❄️"
    "Fan"         -> "🌀"
    "Light"       -> "💡"
    "Heater"      -> "🔥"
    "Electronics" -> "📺"
    else          -> "⚡"
}