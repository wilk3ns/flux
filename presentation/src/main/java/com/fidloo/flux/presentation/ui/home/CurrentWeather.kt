/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fidloo.flux.presentation.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fidloo.flux.domain.base.Result
import com.fidloo.flux.domain.model.CurrentWeather
import com.fidloo.flux.presentation.ui.component.ExpandableSectionHeader
import com.fidloo.flux.presentation.ui.component.GenericErrorMessage
import com.fidloo.flux.presentation.ui.component.SectionProgressBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentWeather(currentWeatherResult: Result<CurrentWeather>) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        ExpandableSectionHeader(
            title = "Details",
            subtitle = "Weather now",
            expanded = expanded,
            onToggleState = { expanded = !expanded }
        )
        Spacer(Modifier.height(8.dp))

        when (currentWeatherResult) {
            is Result.Error -> GenericErrorMessage()
            Result.Loading -> SectionProgressBar()
            is Result.Success -> {
                val currentWeather = currentWeatherResult.data
                val weatherFacts = currentWeather.extractFacts()

                val itemsPerRow = 2
                weatherFacts.chunked(itemsPerRow)
                    .run { if (!expanded) take(2) else this }
                    .forEach { factsPerRow ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            factsPerRow.forEachIndexed { index, fact ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(1f / (itemsPerRow - index))
                                ) {
                                    WeatherFact(fact)
                                }
                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun WeatherFact(item: WeatherFact) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = item.label,
                style = MaterialTheme.typography.h3,
            )
            Spacer(Modifier.height(4.dp))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = item.value,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}