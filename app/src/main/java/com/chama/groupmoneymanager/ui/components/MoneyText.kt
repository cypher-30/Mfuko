package com.chama.groupmoneymanager.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.chama.groupmoneymanager.ui.theme.Inter
import com.chama.groupmoneymanager.ui.theme.MfukoExtraType
import com.chama.groupmoneymanager.ui.util.formatKes

/**
 * The single component for rendering money anywhere in the app —
 * DESIGN_SYSTEM.md §6.1/§10. Wraps [formatKes] (kept as-is) and renders the
 * "KES" prefix smaller/muted, with the numeric figure in the requested
 * [MfukoExtraType] money style (tabular numerals). This is the fix for the
 * mixed currency-formatting defect (§2, item 4) — every monetary value,
 * including the one that used to bypass [formatKes] in RepayLoanDialog,
 * renders through this component.
 */
@Composable
fun MoneyText(
    amount: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MfukoExtraType.moneyMedium,
    color: Color = LocalContentColor.current,
    textAlign: TextAlign? = null
) {
    val formatted = formatKes(amount)
    val numeric = formatted.removePrefix("KES ")
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = color.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = Inter
            )
        ) {
            append("KES ")
        }
        withStyle(SpanStyle(color = color)) {
            append(numeric)
        }
    }
    Text(text = text, style = style, modifier = modifier, textAlign = textAlign)
}
