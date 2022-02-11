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

package cz.lastaapps.cvutbus.components.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.common.Communication
import cz.lastaapps.cvutbus.R

@Composable
fun PrivacyDialog(
    shown: Boolean,
    onDismissRequest: () -> Unit,
    showAccept: Boolean,
    onAccept: () -> Unit,
) {
    if (shown) {
        Dialog(onDismissRequest = onDismissRequest) {
            PrivacyDialogContent(showAccept = showAccept, onAccept = onAccept)
        }
    }
}

@Composable
fun PrivacyDialogContent(
    showAccept: Boolean,
    onAccept: () -> Unit = {},
) {
    Surface(shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.privacy_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(stringResource(R.string.privacy_text))

            val context = LocalContext.current
            OutlinedButton(onClick = { Communication.openProjectsGithub(context, "ctubus") }) {
                Text(stringResource(R.string.privacy_view_source))
            }
            if (showAccept)
                Button(onClick = onAccept) {
                    Text(stringResource(R.string.privacy_accept))
                }
        }
    }
}