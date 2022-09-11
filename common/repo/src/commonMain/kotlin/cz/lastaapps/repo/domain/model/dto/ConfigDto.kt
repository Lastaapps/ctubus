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

package cz.lastaapps.repo.domain.model.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
internal data class ConfigDto(
    val v1: ConfigV1?,
)

@Serializable
internal data class ConfigV1(
    // data should match reality
    val releaseDate: LocalDate,
    // times are still shown, but there may already be a change from PID
    val validUntil: LocalDate,
    val link: String,
    val fileSize: Long,
)
