package com.codentmind.gemlens.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codentmind.gemlens.R
import com.codentmind.gemlens.domain.model.AssistChipModel
import com.codentmind.gemlens.presentation.theme.BorderColor
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel

@Composable
fun ChatScreenDefault(viewModel: MessageViewModel) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4285F4), Color(0xFFEA4335))
    )
    val context = LocalContext.current
    val onChipItemClick = { text: String ->
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(14.dp)
    ) {
        Text(
            text = stringResource(R.string.help_text),
            style = TextStyle(
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                //  color = Color.Gray
            ),
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(gradient, blendMode = BlendMode.SrcAtop)
                    }
                }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Suggestions manually wrapped in rows
        SuggestionRow(
            listOf(
                AssistChipModel(R.drawable.tell_me_what_can_i_do, "Tell me what you can do"),
                AssistChipModel(R.drawable.help_me_plan, "Help me plan")
            ), onChipItemClick
        )
        Spacer(modifier = Modifier.height(5.dp))
        SuggestionRow(
            listOf(
                AssistChipModel(R.drawable.research_on_topic, "Research a topic"),
                AssistChipModel(0, "More")
            ), onChipItemClick
        )
    }
}

@Composable
fun SuggestionRow(labels: List<AssistChipModel>, onClick: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
    ) {
        labels.forEach { label ->
            AssistChip(
                onClick = {
                    onClick.invoke(label.text)
                },
                modifier = Modifier
                    .height(50.dp)
                    .padding(top = 5.dp, bottom = 5.dp)
                    .align(Alignment.CenterVertically),
                label = {
                    Text(
                        text = label.text, style = TextStyle(
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                },
                leadingIcon = {
                    if (label.icon > 0) {
                        Image(
                            painter = painterResource(id = label.icon),
                            contentDescription = "Add",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(start = 4.dp),
                        )
                    }
                },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, BorderColor),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenDefaultPreview() {
}
