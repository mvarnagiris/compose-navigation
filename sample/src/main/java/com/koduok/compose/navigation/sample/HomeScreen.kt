package com.koduok.compose.navigation.sample

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    LazyColumnFor(data) {
        Card(modifier = Modifier.fillParentMaxWidth().padding(8.dp).clickable(onClick = { onShowSample(it) })) {
            Text(
                text = it.toString(),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h5
            )
        }
    }
}