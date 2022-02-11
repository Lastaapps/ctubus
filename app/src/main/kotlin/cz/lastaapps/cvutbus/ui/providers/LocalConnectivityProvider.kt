/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * ČVUT Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ČVUT Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ČVUT Bus.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.cvutbus.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import org.lighthousegames.logging.logging

val LocalConnectivityProvider = compositionLocalOf<NetworkCapabilities?> { null }

@Composable
fun WithConnectivity(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val manager = remember(context) {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    var state by remember {
        val network = if (Build.VERSION.SDK_INT >= 23)
            manager.activeNetwork else null

        mutableStateOf(manager.getNetworkCapabilities(network))
    }

    DisposableEffect(manager) {

        val request = NetworkRequest.Builder().build()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                state = networkCapabilities
            }
        }
        manager.registerNetworkCallback(request, callback)

        onDispose {
            manager.unregisterNetworkCallback(callback)
        }
    }

    LaunchedEffect(state) {
        logging("LocalConnectionProvider").i { "metered: ${state?.isMetered()}" }
    }

    CompositionLocalProvider(LocalConnectivityProvider provides state) {
        content()
    }
}

fun NetworkCapabilities?.isMetered(): Boolean {
    return !(this?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) ?: true)
}