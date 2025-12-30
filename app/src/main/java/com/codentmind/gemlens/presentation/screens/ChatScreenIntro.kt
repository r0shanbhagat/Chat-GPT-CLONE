package com.codentmind.gemlens.presentation.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codentmind.gemlens.R
import com.codentmind.gemlens.core.UiState
import com.codentmind.gemlens.data.dataSource.remote.RemoteConfigHelper
import com.codentmind.gemlens.domain.model.AssistChipModel
import com.codentmind.gemlens.presentation.theme.BorderColor
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel
import com.codentmind.gemlens.utils.AppSession
import com.codentmind.gemlens.utils.Constant.CONFIG_QUICK_PROMPTS
import com.codentmind.gemlens.utils.Constant.QUICK_PROMPT_MORE
import org.koin.compose.koinInject

@Composable
fun ChatScreenIntro(viewModel: MessageViewModel) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4285F4), Color(0xFFEA4335))
    )
    val sessionData = koinInject<AppSession>()
    val remoteConfig = koinInject<RemoteConfigHelper>()
    val activity = LocalActivity.current
    var state by remember { mutableStateOf<UiState>(UiState.Loading()) }
    val quickPromptStateList = remember { mutableStateListOf<AssistChipModel>() }

    LaunchedEffect(Unit) {
        if (sessionData.quickPromptList.isEmpty()) {
            remoteConfig.fetchConfigData(CONFIG_QUICK_PROMPTS) {
                sessionData.setQuickPromptsList(it)
                state = UiState.Success(sessionData.quickPromptList)
            }
        } else {
            state = UiState.Success(sessionData.quickPromptList)
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(14.dp)
    ) {

        if (state is UiState.Loading) {
            CircularProgressIndicator(
                strokeWidth = 4.dp,
                color = Color.DarkGray,
                modifier = Modifier.size(45.dp),
            )
        } else {
            quickPromptStateList.apply {
                addAll(sessionData.quickPromptList.take(3))
                add(AssistChipModel(QUICK_PROMPT_MORE, "", "More", ""))
            }

            Text(
                text = stringResource(R.string.help_text),
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    fontSize = 24.sp,
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

            SuggestionRow(quickPromptStateList) {
                if (it.id == QUICK_PROMPT_MORE) {
                    quickPromptStateList.clear()
                    quickPromptStateList.addAll(sessionData.quickPromptList)
                    return@SuggestionRow
                }
                viewModel.quickPromptQuery(it)
            }
        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionRow(labels: List<AssistChipModel>, onClick: (AssistChipModel) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        labels.forEach { item ->
            AssistChip(
                onClick = {
                    onClick.invoke(item)
                },
                modifier = Modifier
                    .height(50.dp)
                    .padding(top = 5.dp, bottom = 5.dp)
                    .align(Alignment.CenterVertically),
                label = {
                    Text(
                        text = item.label, style = TextStyle(
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                },
                leadingIcon = {
                    if (item.drawableId > 0) {
                        Image(
                            painter = painterResource(id = item.drawableId),
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
