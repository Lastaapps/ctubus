/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * Menza is free software: you can redistribute it and/or modify
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

package cz.lastaapps.cvutbus.di

/*
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseInfoStore(app: Application): DatabaseInfoStore {
        return DatabaseInfoStore(app)
    }

    @Provides
    @Singleton
    fun provideDatabaseProvider(app: Application, store: DatabaseInfoStore): DatabaseProvider {
        return DatabaseProvider(app, store, Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideRepoProvider(databaseProvider: DatabaseProvider): PIDRepoProvider {
        return PIDRepoProvider(databaseProvider)
    }
}
 */