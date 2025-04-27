package com.codentmind.gemlens.utils

import android.content.Context
import com.codentmind.gemlens.domain.model.AssistChipModel
import com.codentmind.gemlens.utils.NetworkUtil.Companion.jsonToModel


/**
 * @Details :AppSession
 * @Author Roshan Bhagat
 */
class AppSession(private val context: Context) {
    var quickPromptList: List<AssistChipModel> = emptyList()
        private set

    var apiKey: String = ""

    /**
     * Retrieves quick prompts from a JSON file in the Remote Config
     * This function reads the quick prompts from a JSON file, maps them to [AssistChipModel] objects,
     * and sets their drawable resource IDs.
     */
    fun setQuickPromptsList(quickPromptsJson: String) {
        this.quickPromptList = jsonToModel<List<AssistChipModel>>(quickPromptsJson).map { item ->
            item.copy(drawableId = context.getDrawableResId(item.drawableName))
        }

    }


    internal fun clear() {

    }
}