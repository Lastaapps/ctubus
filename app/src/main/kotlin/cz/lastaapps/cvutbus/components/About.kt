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

package cz.lastaapps.cvutbus.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.common.Communication
import cz.lastaapps.common.DeveloperInfo
import cz.lastaapps.common.R
import cz.lastaapps.cvutbus.BuildConfig
import cz.lastaapps.cvutbus.navigation.Dests

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUi(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Box(modifier.verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Box(Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(cz.lastaapps.cvutbus.R.string.about_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .width(IntrinsicSize.Max)
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DataSource(Modifier.fillMaxWidth())

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Dests.Routes.license)
                        }
                    ) {
                        IconAndText({
                            Icon(Icons.Default.Description, null)
                        }, {
                            Text(
                                text = stringResource(cz.lastaapps.cvutbus.R.string.about_license_notices),
                                textAlign = TextAlign.Center
                            )
                        })
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Dests.Routes.osturak)
                        }
                    ) {
                        IconAndText({
                            Icon(Icons.Default.LocalFireDepartment, null)
                        }, {
                            Text(
                                text = stringResource(cz.lastaapps.cvutbus.R.string.about_osturak),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        })
                    }
                    ViewSource(Modifier.fillMaxWidth())
                }
            }
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Socials(Modifier.padding(16.dp))
            }
            AppInfo(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun DataSource(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(cz.lastaapps.cvutbus.R.string.about_data_source),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        OutlinedButton(
            modifier = modifier.fillMaxWidth(),
            onClick = {
                uriHandler.openUri("https://pid.cz/o-systemu/opendata")
            }
        ) {
            IconAndText({
                Icon(Icons.Default.Language, null)
            }, {
                Text(text = "pid.cz", textAlign = TextAlign.Center)
            })
        }
    }
}

@Composable
private fun ViewSource(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    OutlinedButton(
        modifier = modifier,
        onClick = { Communication.openProjectsGithub(context, "cvutbus") },
    ) {
        IconAndText(
            icon = {
                Icon(
                    Icons.Default.Code,
                    stringResource(R.string.content_description_github_project)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.github_project),
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

@Composable
private fun IconAndText(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        icon()
        text()
    }
}

@Composable
private fun Socials(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val icons = listOf(
        SocialItem(
            R.drawable.ic_facebook,
            R.string.content_description_facebook,
        ) { Communication.openFacebook(context) },
        SocialItem(
            R.drawable.ic_telegram,
            R.string.content_description_telegram,
        ) { Communication.openTelegram(context) },
        SocialItem(
            R.drawable.ic_github,
            R.string.content_description_github,
        ) { Communication.openGithub(context) },
        SocialItem(
            R.drawable.ic_play_store,
            R.string.content_description_play_store,
        ) { Communication.openPlayStore(context) },
    )

    Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(cz.lastaapps.cvutbus.R.string.about_developer_contact),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icons.forEach {
                IconButton(onClick = it.onClick) {
                    Image(
                        painterResource(id = it.drawableId),
                        contentDescription = stringResource(id = it.contentDescriptionId)
                    )
                }
            }
        }
    }
}

private data class SocialItem(
    @DrawableRes val drawableId: Int,
    @StringRes val contentDescriptionId: Int,
    val onClick: () -> Unit
)

@Composable
private fun AppInfo(modifier: Modifier = Modifier) {
    val developer = DeveloperInfo.getNameAndBuildYear(LocalContext.current)
    val appInfo = "${BuildConfig.VERSION_NAME} - ${BuildConfig.VERSION_CODE}"
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(developer, textAlign = TextAlign.Center)
        Text(appInfo, textAlign = TextAlign.Center)
    }
}
