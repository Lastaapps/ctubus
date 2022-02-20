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

package cz.lastaapps.cvutbus.components.license.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import cz.lastaapps.cvutbus.R
import org.lighthousegames.logging.logging

@Composable
fun LibraryDetailWrapper(library: Library?, modifier: Modifier = Modifier) {
    if (library == null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.license_none_selected), textAlign = TextAlign.Center)
        }
    } else {
        Surface(modifier, shape = RoundedCornerShape(16.dp)) {
            LibraryDetail(library, Modifier.verticalScroll(rememberScrollState()))
        }
    }
}

@Composable
fun LibraryDetail(library: Library, modifier: Modifier = Modifier) {
    SelectionContainer(modifier) {
        Column(
            Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            val titleStyle = MaterialTheme.typography.titleMedium
            val bodyStyle = MaterialTheme.typography.bodyMedium

            Text(library.name, style = MaterialTheme.typography.headlineSmall)
            Text(library.artifactId, style = titleStyle)

            if (library.openSource) {
                Text(stringResource(R.string.license_detail_opensource_yes), style = bodyStyle)
            } else {
                Text(stringResource(R.string.license_detail_opensource_no), style = bodyStyle)
            }

            if (library.developers.isNotEmpty()) {
                Column {
                    Text(stringResource(R.string.license_detail_developer), style = titleStyle)
                    library.developers.forEach { developer ->
                        developer.name?.let { Text(it, style = bodyStyle) }
                    }
                }
            }

            library.website?.let { website ->
                Column {
                    Text(stringResource(R.string.license_detail_link), style = titleStyle)
                    Uri(website, style = bodyStyle)
                }
            }

            library.organization?.let { organization ->
                Column {
                    Text(
                        stringResource(R.string.license_detail_organization),
                        style = titleStyle
                    )
                    Text(organization.name, style = bodyStyle)
                }
            }

            library.description?.let { description ->
                Column {
                    Text(
                        stringResource(R.string.license_detail_description),
                        style = titleStyle
                    )
                    Text(description, style = bodyStyle)
                }
            }

            if (library.licenses.isEmpty()) {
                Text(stringResource(R.string.license_detail_no_license))
            } else {
                library.licenses.forEach { license ->
                    LicenseDetail(license, titleStyle = titleStyle, bodyStyle = bodyStyle)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LicenseDetail(
    license: License,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    bodyStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(license.name, style = titleStyle)

            license.year?.takeIf { it.isNotBlank() }?.let { Text(it, style = bodyStyle) }
            license.url?.takeIf { it.isNotBlank() }?.let { Uri(it, style = bodyStyle) }

            license.licenseContent?.takeIf { it.isNotBlank() }?.let { Text(it, style = bodyStyle) }
        }
    }
}

@Composable
private fun Uri(
    link: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    val handler = LocalUriHandler.current
    Text(
        link, textDecoration = TextDecoration.Underline,
        style = style,
        modifier = modifier.clickable {
            logging("UriComposable").i { "Opening $link" }
            handler.openUri(link)
        },
    )
}