package com.koduok.compose.navigation.sample

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Clickable
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutWidth.Fill
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Card
import androidx.ui.unit.dp
import com.koduok.compose.navigation.sample.AppRoute.BottomTabsRoute
import com.koduok.compose.navigation.sample.AppRoute.MultipleStartRoute
import com.koduok.compose.navigation.sample.AppRoute.SimpleRoute
import com.koduok.compose.navigation.sample.AppRoute.SplitScreenRoute

@Composable
fun HomeScreen(onShowSample: (AppRoute) -> Unit) {
    val data = listOf(
        SimpleRoute,
        MultipleStartRoute,
        BottomTabsRoute,
        SplitScreenRoute
    )

    AdapterList(data = data) {
        Clickable(onClick = { onShowSample(it) }) {
            Card(
                modifier = Fill + LayoutPadding(
                    8.dp
                )
            ) {
                Text(
                    text = it.toString(),
                    modifier = LayoutPadding(16.dp),
                    style = MaterialTheme.typography().h5
                )
            }
        }
    }
}