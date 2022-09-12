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

package cz.lastaapps.database.di

import cz.lastaapps.database.DriverFactory
import cz.lastaapps.database.createDatabase
import cz.lastaapps.database.data.PIDDataSourceImpl
import cz.lastaapps.database.data.UpdateDataSourceImpl
import cz.lastaapps.database.domain.PIDDataSource
import cz.lastaapps.database.domain.UpdateDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal expect val platformModule: Module

val databaseModule = module {
    includes(platformModule)

    factory { createDatabase(get<DriverFactory>().createDriver()) }
    factoryOf(::PIDDataSourceImpl) { bind<PIDDataSource>() }
    factoryOf(::UpdateDataSourceImpl) { bind<UpdateDataSource>() }
}