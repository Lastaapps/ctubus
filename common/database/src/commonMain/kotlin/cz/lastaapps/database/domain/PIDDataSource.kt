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

package cz.lastaapps.database.domain

import cz.lastaapps.core.domain.model.DepartureInfo
import cz.lastaapps.core.domain.model.StopPair
import cz.lastaapps.core.domain.model.TransportConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface PIDDataSource {

    suspend fun getConnections(): Flow<List<StopPair>>

    suspend fun getData(
        from: Instant, connection: TransportConnection,
    ): Flow<List<DepartureInfo>>
}