/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.screens.edit_task

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.common.composable.*
import com.example.makeitso.common.ext.card
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.common.ext.spacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.model.Priority
import com.example.makeitso.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.R.string as AppText

@Composable
@ExperimentalMaterialApi
fun EditTaskScreen(popUpScreen: () -> Unit, taskId: String) {
    val viewModel = hiltViewModel<EditTaskViewModel>()
    val task = viewModel.task.value

    LaunchedEffect(Unit) {
        viewModel.initialize(taskId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionToolbar(
            title = AppText.edit_task,
            modifier = Modifier.toolbarActions(),
            endActionIcon = AppIcon.ic_check,
            endAction = { viewModel.onDoneClick(popUpScreen) }
        )

        Spacer(modifier = Modifier.spacer())
        BasicFields(task, viewModel)

        Spacer(modifier = Modifier.spacer())
        CardEditors(task, viewModel)
        CardSelectors(task, viewModel)

        Spacer(modifier = Modifier.spacer())
    }
}

@Composable
private fun BasicFields(task: Task, viewModel: EditTaskViewModel) {
    BasicField(AppText.title, task.title, Modifier.fieldModifier(), viewModel::onTitleChange)
    BasicField(AppText.description, task.description, Modifier.fieldModifier(), viewModel::onDescriptionChange)
    BasicField(AppText.url, task.url, Modifier.fieldModifier(), viewModel::onUrlChange)
}

@ExperimentalMaterialApi
@Composable
private fun CardEditors(task: Task, viewModel: EditTaskViewModel) {
    val activity = LocalContext.current as AppCompatActivity

    RegularCardEditor(AppText.date, AppIcon.ic_calendar, task.dueDate, Modifier.card()) {
        showDatePicker(activity, viewModel)
    }

    RegularCardEditor(AppText.time, AppIcon.ic_clock, task.dueTime, Modifier.card()) {
        showTimePicker(activity, viewModel)
    }
}

@Composable
@ExperimentalMaterialApi
private fun CardSelectors(task: Task, viewModel: EditTaskViewModel) {
    val prioritySelection = Priority.getByName(task.priority).name
    CardSelector(AppText.priority, Priority.getOptions(), prioritySelection, Modifier.card()) { newValue ->
        viewModel.onPriorityChange(newValue)
    }

    val flagSelection = EditFlagOption.getByCheckedState(task.flag).name
    CardSelector(AppText.flag, EditFlagOption.getOptions(), flagSelection, Modifier.card()) { newValue ->
        viewModel.onFlagToggle(newValue)
    }
}

private fun showDatePicker(activity: AppCompatActivity?, viewModel: EditTaskViewModel) {
    val picker = MaterialDatePicker.Builder.datePicker().build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { timeInMillis ->
            viewModel.onDateChange(timeInMillis)
        }
    }
}

private fun showTimePicker(activity: AppCompatActivity?, viewModel: EditTaskViewModel) {
    val picker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            viewModel.onTimeChange(picker.hour, picker.minute)
        }
    }
}