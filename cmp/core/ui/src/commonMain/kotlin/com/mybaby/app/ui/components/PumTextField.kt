package com.mybaby.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** 텍스트 입력 필드. 포커스 시 label/border가 primary 색상으로 변경. */
@Composable
fun PumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = PumTheme.colors

    val labelColor = when {
        isError -> colors.error
        isFocused -> colors.primary
        else -> colors.onSurfaceSubtle
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = singleLine,
            isError = isError,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(text = placeholder, color = colors.onSurfaceSubtle)
                }
            },
            trailingIcon = when {
                isError -> ({
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = colors.error,
                        modifier = Modifier.size(20.dp)
                    )
                })
                else -> trailingIcon
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = if (isError) colors.error else colors.outline,
                errorBorderColor = colors.error,
                focusedContainerColor = if (isError) colors.errorLight else colors.surface,
                unfocusedContainerColor = if (isError) colors.errorLight else colors.surface,
                errorContainerColor = colors.errorLight,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
            ),
            interactionSource = interactionSource,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = colors.error,
                fontSize = 12.sp
            )
        }
    }
}

/** 여러 줄 텍스트 입력. 편지 작성 등에 활용. */
@Composable
fun PumTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minLines: Int = 4
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = PumTheme.colors

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = if (isFocused) colors.primary else colors.onSurfaceSubtle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = minLines,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(text = placeholder, color = colors.onSurfaceSubtle, lineHeight = 24.sp)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
            ),
            interactionSource = interactionSource
        )
    }
}

/**
 * 숫자 증감 입력.
 * value/step 관리는 호출자가 담당.
 */
@Composable
fun PumNumberInput(
    value: String,
    unit: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onDecrease,
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.surface,
                contentColor = colors.onSurface
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.outline),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Remove, contentDescription = "감소", modifier = Modifier.size(18.dp))
        }

        Box(
            modifier = Modifier.weight(1f).height(52.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, color = colors.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = " $unit", color = colors.onSurfaceSubtle, fontSize = 14.sp)
            }
        }

        Button(
            onClick = onIncrease,
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "증가", modifier = Modifier.size(18.dp))
        }
    }
}

/**
 * 날짜 선택 필드. 탭 시 DatePickerDialog 표시.
 * [selectedDateMillis]: 선택된 날짜 (epoch millis), null이면 placeholder 표시.
 */
@Composable
fun PumDatePickerField(
    label: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "날짜를 선택하세요"
) {
    var showDialog by remember { mutableStateOf(false) }
    val colors = PumTheme.colors
    val displayText = selectedDateMillis?.toDisplayDate() ?: ""

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = colors.onSurfaceSubtle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { showDialog = true }
                )
        ) {
            OutlinedTextField(
                value = displayText,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().height(52.dp),
                readOnly = true,
                enabled = false,
                singleLine = true,
                placeholder = {
                    Text(text = placeholder, color = colors.onSurfaceSubtle)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = null,
                        tint = colors.onSurfaceSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = colors.outline,
                    disabledContainerColor = colors.surface,
                    disabledTextColor = colors.onSurface,
                    disabledTrailingIconColor = colors.onSurfaceSubtle,
                    disabledPlaceholderColor = colors.onSurfaceSubtle
                )
            )
        }
    }

    if (showDialog) {
        PumDatePickerDialog(
            initialDateMillis = selectedDateMillis,
            onDateSelected = { millis ->
                onDateSelected(millis)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PumDatePickerDialog(
    initialDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            PumTextButton(
                text = "확인",
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                }
            )
        },
        dismissButton = {
            PumTextButton(text = "취소", onClick = onDismiss)
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun Long.toDisplayDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dt.year}년 ${dt.monthNumber}월 ${dt.dayOfMonth}일"
}
