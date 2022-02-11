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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.common.Communication
import cz.lastaapps.common.R
import cz.lastaapps.cvutbus.BuildConfig
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

sealed class ReportMode {
    object Telegram : ReportMode()
    object GitHub : ReportMode()
    object Facebook : ReportMode()
    object Email : ReportMode()
}

@Composable
fun ReportDialog(
    shown: Boolean,
    onDismissRequest: () -> Unit,
    onModeSelected: (ReportMode) -> Unit,
) {
    if (shown) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(cz.lastaapps.cvutbus.R.string.report_title),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = { onModeSelected(ReportMode.Telegram) },
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(R.drawable.ic_telegram),
                                null,
                                Modifier.size(24.dp)
                            )
                            Text(stringResource(cz.lastaapps.cvutbus.R.string.report_telegram))
                        }
                    }
                    Button(
                        onClick = { onModeSelected(ReportMode.GitHub) },
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(id = R.drawable.ic_github),
                                null,
                                Modifier.size(24.dp)
                            )
                            Text(stringResource(cz.lastaapps.cvutbus.R.string.report_github))
                        }
                    }
                    Button(
                        onClick = { onModeSelected(ReportMode.Facebook) },
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painterResource(R.drawable.ic_facebook),
                                null,
                                Modifier.size(24.dp)
                            )
                            Text(stringResource(cz.lastaapps.cvutbus.R.string.report_facebook))
                        }
                    }
                    Button(
                        onClick = { onModeSelected(ReportMode.Email) },
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(Icons.Default.Email, null, Modifier.size(24.dp))
                            Text(stringResource(cz.lastaapps.cvutbus.R.string.report_email))
                        }
                    }
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(cz.lastaapps.cvutbus.R.string.report_cancel))
                    }
                }
            }
        }
    }
}

fun sendReport(context: Context, mode: ReportMode, throwable: Throwable? = null) {
    val text = """
        |${context.getString(cz.lastaapps.cvutbus.R.string.report_add_description)}
        |
        |
        |${getPhoneInfo(context)}
        |
        |${throwable?.stackTraceToString() ?: ""}
    """.trimMargin()

    copyToClipboard(context, text)

    when (mode) {
        ReportMode.Telegram -> sendTelegram(context, text)
        ReportMode.GitHub -> sendGitHub(context, text)
        ReportMode.Facebook -> sendFacebook(context, text)
        ReportMode.Email -> sendEmail(context, text)
    }
}

private fun getPhoneInfo(context: Context): String {
    val metrics = context.resources.displayMetrics
    return """
        |Android version:    ${Build.VERSION.SDK_INT}
        |App version name:   ${BuildConfig.VERSION_NAME}
        |App version code:   ${BuildConfig.VERSION_CODE}
        |Phone model:        ${Build.MODEL}
        |Phone manufacturer: ${Build.MANUFACTURER} 
        |Date and Time:      ${ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}
        |Screen size:        ${metrics.widthPixels} x ${metrics.heightPixels} px
    """.trimMargin()
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(
        context.getString(cz.lastaapps.cvutbus.R.string.report_clipboard_title),
        text
    )
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, cz.lastaapps.cvutbus.R.string.report_clipboard, Toast.LENGTH_LONG)
        .show()
}

private fun sendTelegram(context: Context, text: String) {
    //val myId = Uri.encode("-1001494132666") //group id
    val groupName = Uri.encode("lastaapps")
    val encoded = Uri.encode(text)

    val intent = Intent(
        Intent.ACTION_VIEW,
        //Uri.parse("tg://msg?to=$myId&text=$encoded"),
        Uri.parse("tg://resolve?domain=$groupName&text=$encoded"),
    )
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            //Uri.parse("https://t.me/share/msg?to=$myId&text=$encoded"),
            Uri.parse("http://www.telegram.me/$groupName"),
        )
        context.startActivity(webIntent)
    }
}

private fun sendGitHub(context: Context, @Suppress("UNUSED_PARAMETER") text: String) {
    val intent =
        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Lastaapps/cvutbus/issues/new"))
    context.startActivity(intent)
}

private fun sendFacebook(context: Context, @Suppress("UNUSED_PARAMETER") text: String) {
    Communication.openFacebook(context)
}

private fun sendEmail(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, "lastaappsdev@gmail.com")
    intent.putExtra(Intent.EXTRA_SUBJECT, "ČVUT Bus problem report")
    intent.putExtra(Intent.EXTRA_TEXT, text)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            context,
            cz.lastaapps.cvutbus.R.string.report_email_no_app,
            Toast.LENGTH_LONG
        )
            .show()
    }
}

