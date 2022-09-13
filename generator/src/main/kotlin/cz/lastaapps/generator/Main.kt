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

@file:Suppress("BlockingMethodInNonBlockingContext")

package cz.lastaapps.generator

import cz.lastaapps.core.util.CET
import cz.lastaapps.database.JvmDatabaseDriverFactoryImpl
import cz.lastaapps.database.MemoryDriverFactory
import cz.lastaapps.database.createUpdateDatabaseSource
import cz.lastaapps.database.domain.UpdateDataSource
import cz.lastaapps.generator.model.Config
import cz.lastaapps.generator.model.ConfigV1
import cz.lastaapps.generator.model.StopPairs
import cz.lastaapps.generator.parsers.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

val genLog = logging("General")
val downLog = logging("Download")
val zipLog = logging("Unzipping")
val memLog = logging("Memory db")
val parseLog = logging("Parsing")
val dbLog = logging("Database")
val jsonLog = logging("Json")
val cleanLog = logging("Clean up")

const val dirPath = "pid_data"
const val archiveName = "PID_GTFS.zip"
const val databaseName = "piddatabase.db"
const val jsonName = "config.json"

/**
 * Filters all the non-important trips form the dara provided by PID,
 * so a small database can be uploaded to the net
 */
fun main(): Unit = runBlocking {

    val now = Clock.System.now()

    val skipDownload = false
    val skipZip = false
    val skipCleanup = false

    val dir = File(dirPath)
    dir.mkdirs()
    dir.mkdir()

    genLog.i { "Working directory: " + System.getProperty("user.dir") }
    genLog.i { "Output directory: " + dir.absolutePath }

    val archive = File(dir, archiveName)
    if ((!archive.exists() || Instant.fromEpochMilliseconds(archive.lastModified() + 24 * 3600 * 1000) < now) && !skipDownload) {
        downLog.i { "Downloading from https://data.pid.cz/PID_GTFS.zip" }

        if (!archive.exists())
            archive.createNewFile()

        // CIO stopped working
        val succeeded = HttpClient(CIO) {
            CurlUserAgent()
        }.downloadFile(archive, "https://data.pid.cz/PID_GTFS.zip") {
            //print("\rDownload progress: ${it * 100} %")
        }
        if (!succeeded) {
            downLog.i { "Download failed!" }
            try {
                archive.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            exitProcess(1)
        }
        downLog.i { "Downloaded" }
    } else {
        downLog.i { "Download skipped" }
    }

    zipLog.i { "Unzipping..." }
    if (!skipZip)
        unzip(archive, dir)
    else
        zipLog.i { "Unzipped skipped" }
    zipLog.i { "Unzipped!" }


    val completeDatabase = createUpdateDatabaseSource(MemoryDriverFactory().createDriver())
    memLog.i { "Filling temp database" }
    loadData(dir, completeDatabase)
    memLog.i { "Loading done" }

    val databaseFile = File(dir, databaseName)
    if (databaseFile.exists()) databaseFile.delete()
    databaseFile.createNewFile()

    val database =
        createUpdateDatabaseSource(JvmDatabaseDriverFactoryImpl(databaseFile).createDriver())
    database.inTransaction {
        StopPairs.allStops.forEach {
            dbLog.i { "Querying data for ${it.stop1.name} - ${it.stop2.name}" }
            completeDatabase.getAllRecords(it)
                .also { records ->
                    dbLog.i { "Connections found: ${records.size}" }
                    database.insertStopPair(it)
                    database.insertRecords(records)
                }
        }
    }
    dbLog.i { "Data saved" }

    jsonLog.i { "Creating json configuration" }
    val json = File(dir, jsonName)
    val jsonOut = PrintWriter(OutputStreamWriter(json.outputStream()))
    val nowDate = now.toLocalDateTime(CET).date
    val config = Config(
        ConfigV1(
            releaseDate = nowDate,
            validUntil = nowDate.plus(10, DateTimeUnit.DAY),
            fileSize = databaseFile.length(),
            link = "https://raw.githubusercontent.com/Lastaapps/cvutbus/cloud_data/cloud_data/piddatabase.db",
        )
    )
    jsonOut.print(Json.encodeToString(Config.serializer(), config))
    jsonOut.flush()
    jsonOut.close()

    cleanLog.i { "Cleaning up..." }
    if (!skipCleanup)
        cleanup(dir)

    genLog.i { "Done, bye" }
}

@OptIn(InternalAPI::class)
internal suspend fun HttpClient.downloadFile(
    file: File, url: String, onProgress: (Float) -> Unit
): Boolean {
    val response = get { url(url) }

    val data = ByteArray(response.contentLength()!!.toInt())
    var offset = 0

    do {
        //TODO download progress
        val currentRead = response.content.readAvailable(data, offset, data.size)
        offset += currentRead
        onProgress(1f * offset / data.size)
    } while (currentRead > 0)

    if (!response.status.isSuccess()) {
        return false
    }
    file.writeBytes(data)
    return true
}

internal fun unzip(archive: File, dir: File) {
    val buffer = ByteArray(1024)
    val zis = ZipInputStream(FileInputStream(archive))
    var zipEntry = zis.nextEntry
    while (zipEntry != null) {
        zipLog.i { "Unzipping ${zipEntry.name}" }
        val newFile = newFile(dir, zipEntry)
        if (zipEntry.isDirectory) {
            if (!newFile.isDirectory && !newFile.mkdirs()) {
                throw IOException("Failed to create directory $newFile")
            }
        } else {
            // fix for Windows-created archives
            val parent = newFile.parentFile!!
            if (!parent.isDirectory && !parent.mkdirs()) {
                throw IOException("Failed to create directory $parent")
            }

            // write file content
            val fos = FileOutputStream(newFile)
            var len: Int
            while (zis.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }
            fos.close()
        }
        zipEntry = zis.nextEntry
    }
    zis.closeEntry()
    zis.close()
}

internal fun newFile(dest: File, zipEntry: ZipEntry): File {
    val destFile = File(dest, zipEntry.name)

    val destDirPath = dest.canonicalPath
    val destFilePath = destFile.canonicalPath

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
        throw IOException("Entry is outside of the target dir: " + zipEntry.name)
    }

    return destFile
}

internal fun cleanup(dest: File) {
    dest.listFiles()!!.forEach {
        if (it.name.endsWith(".txt"))
            it.delete()
    }
}

internal fun loadData(dir: File, database: UpdateDataSource) {

    fun stream(name: String): InputStream = File(dir, "$name.txt").inputStream()

    database.inTransaction {
        parseLog.i { "Parsing calendar.txt" }
        CalendarParser.parse(stream("calendar")) {
            database.insertCalendar(it.serviceId, it.days, it.start, it.end)
        }
        parseLog.i { "Parsing routes.txt" }
        RoutesParser.parse(stream("routes")) {
            database.insertRoute(it.routeId, it.shortName, it.longName)
        }
        parseLog.i { "Parsing stops.txt" }
        StopsParser.parse(stream("stops")) {
            database.insertStop(it.stopId, it.name)
        }
        parseLog.i { "Parsing stop_times.txt" }
        StopTimesParser.parse(stream("stop_times")) {
            database.insertStopTime(it.stopId, it.tripId, it.arrival, it.departure)
        }
        parseLog.i { "Parsing trips.txt" }
        TripsParser.parse(stream("trips")) {
            database.insertTrip(it.tripId, it.routeId, it.serviceId, it.headSign)
        }
    }
}


