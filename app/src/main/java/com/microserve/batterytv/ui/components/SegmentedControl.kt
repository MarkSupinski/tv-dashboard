package com.microserve.batterytv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A D-pad friendly segmented control (e.g. Hour / Day / Week) for choosing
 * the history range on the detail screen.
 */
@Composable
fun SegmentedControl(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        options.forEach { option ->
            var focused by remember { mutableStateOf(false) }
            val isSelected = option == selected
            val shape = RoundedCornerShape(50)
            val bg = when {
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                focused -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            }
            val textColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onBackground
            }
            Text(
                text = option.replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier
                    .clip(shape)
                    .background(bg)
                    .border(
                        width = if (focused) 2.dp else 1.dp,
                        color = if (focused) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = shape,
                    )
                    .focusable()
                    .onFocusChanged { focused = it.isFocused }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(option) },
                    )
                    .padding(horizontal = 22.dp, vertical = 10.dp),
            )
        }
    }
}
