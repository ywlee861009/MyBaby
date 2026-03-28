package com.mybaby.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mybaby.app.ui.theme.PumTheme

/** 주요 액션 버튼. fill=primary, radius=8, height=52 */
@Composable
fun PumPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.primary,
            contentColor = PumTheme.colors.onPrimary,
            disabledContainerColor = PumTheme.colors.outline,
            disabledContentColor = PumTheme.colors.onSurfaceSubtle
        ),
        contentPadding = PaddingValues(horizontal = PumTheme.spacing.medium)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** 보조 버튼. outlined 형태, stroke=primary 1.5, height=52 */
@Composable
fun PumSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PumTheme.colors.primary,
            disabledContentColor = PumTheme.colors.onSurfaceSubtle
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (enabled) PumTheme.colors.primary else PumTheme.colors.outline
        ),
        contentPadding = PaddingValues(horizontal = PumTheme.spacing.medium)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** 텍스트 버튼. 배경 없음, text=primary, height=44 */
@Composable
fun PumTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = PumTheme.colors.primary
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** 아이콘 버튼. circle 48dp, fill=surface, shadow */
@Composable
fun PumIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = PumTheme.colors.primary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .shadow(elevation = 4.dp, shape = CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.surface,
            contentColor = tint
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = tint
        )
    }
}

/** 플로팅 액션 버튼. circle 56dp, fill=primary, shadow */
@Composable
fun PumFab(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .shadow(elevation = 6.dp, shape = CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.primary,
            contentColor = PumTheme.colors.onPrimary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

/** 파괴적 액션 버튼. fill=error, height=52 (삭제 등 위험 액션) */
@Composable
fun PumDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.error,
            contentColor = Color.White,
            disabledContainerColor = PumTheme.colors.outline,
            disabledContentColor = PumTheme.colors.onSurfaceSubtle
        ),
        contentPadding = PaddingValues(horizontal = PumTheme.spacing.medium)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** @deprecated PumPrimaryButton을 사용하세요 */
@Composable
fun PumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) = PumPrimaryButton(text = text, onClick = onClick, modifier = modifier, enabled = enabled)

/** 로딩 버튼 상태: Normal → Loading → Success */
enum class PumLoadingButtonState { NORMAL, LOADING, SUCCESS }

/**
 * 저장/제출 액션에 사용하는 로딩 버튼.
 * - NORMAL: 일반 primary 버튼
 * - LOADING: 회전 스피너 + 로딩 텍스트, opacity 65%
 * - SUCCESS: 체크 아이콘 + 완료 텍스트, 진한 핑크
 */
@Composable
fun PumLoadingButton(
    text: String,
    state: PumLoadingButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loadingText: String = "저장 중...",
    successText: String = "완료!"
) {
    val containerColor by animateColorAsState(
        targetValue = if (state == PumLoadingButtonState.SUCCESS) Color(0xFFE5607A)
                      else PumTheme.colors.primary,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "containerColor"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (state == PumLoadingButtonState.LOADING) 0.65f else 1f,
        animationSpec = tween(300),
        label = "alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val spinnerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Button(
        onClick = onClick,
        enabled = state == PumLoadingButtonState.NORMAL,
        modifier = modifier
            .height(52.dp)
            .alpha(buttonAlpha),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White,
            disabledContainerColor = containerColor,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = PumTheme.spacing.medium)
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                (fadeIn(tween(200, delayMillis = 100)) +
                 scaleIn(tween(200, delayMillis = 100), initialScale = 0.8f)) togetherWith
                (fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.8f))
            },
            label = "buttonContent"
        ) { currentState ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (currentState) {
                    PumLoadingButtonState.NORMAL ->
                        Text(text = text, style = PumTheme.typography.labelLarge)

                    PumLoadingButtonState.LOADING -> {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(spinnerRotation),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = loadingText, style = PumTheme.typography.labelLarge)
                    }

                    PumLoadingButtonState.SUCCESS -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = successText, style = PumTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
