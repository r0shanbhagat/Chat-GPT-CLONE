package com.codentmind.gemlens.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codentmind.gemlens.R
import com.codentmind.gemlens.presentation.theme.ErrorBgColor
import com.codentmind.gemlens.presentation.theme.ErrorTextColor

@Composable
fun ErrorMessageUI(
    onRetryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // Error Card
        Card(
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFCF6F3),
                contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)

        ) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info, // Using a refresh icon that seems suitable
                    contentDescription = "Error",
                    tint = ErrorBgColor,
                    modifier = Modifier.padding(end = 14.dp)
                )
                Text(
                    text = stringResource(R.string.error_msg_text),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = ErrorTextColor
                    ),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onRetryClick) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh, // Using a refresh icon that seems suitable
                        contentDescription = "Error",
                        tint = ErrorBgColor,
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ErrorMessageUI({})
}
