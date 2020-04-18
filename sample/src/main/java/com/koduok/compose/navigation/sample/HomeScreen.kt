package com.koduok.compose.navigation.sample

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.koduok.compose.navigation.sample.AppRoute.BottomTabsRoute
import com.koduok.compose.navigation.sample.AppRoute.ComplexRoute
import com.koduok.compose.navigation.sample.AppRoute.MultipleStartRoute
import com.koduok.compose.navigation.sample.AppRoute.SimpleRoute
import com.koduok.compose.navigation.sample.AppRoute.SplitScreenRoute

@Composable
fun HomeScreen(onShowSample: (AppRoute) -> Unit) {
    val data = listOf(
        SimpleRoute,
        MultipleStartRoute,
        BottomTabsRoute,
        SplitScreenRoute,
        ComplexRoute
    )

    AdapterList(data = data) {
        Clickable(onClick = { onShowSample(it) }) {
            Card(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Text(
                    text = it.toString(),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.h5
                )
            }
        }
    }
}