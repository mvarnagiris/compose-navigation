package com.koduok.compose.navigation.sample

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.lazy.LazyColumnItems
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
import com.koduok.compose.navigation.sample.AppRoute.TranslucentRoute

@Composable
fun HomeScreen(onShowSample: (AppRoute) -> Unit) {
    val data = listOf(
        SimpleRoute,
        MultipleStartRoute,
        BottomTabsRoute,
        SplitScreenRoute,
        ComplexRoute,
        TranslucentRoute
    )

    LazyColumnItems(data) {
        Card(modifier = Modifier.fillMaxSize().padding(8.dp).clickable(onClick = { onShowSample(it) })) {
            Text(
                text = it.toString(),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h5
            )
        }
    }
}