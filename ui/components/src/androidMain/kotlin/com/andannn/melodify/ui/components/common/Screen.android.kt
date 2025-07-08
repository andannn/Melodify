/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.common

import android.os.Parcel
import android.os.Parcelable
import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
actual data object HomeScreen : Screen

@Parcelize
actual data object LibraryScreen : Screen

actual data class LibraryContentListScreen(
    actual val datasource: LibraryDataSource,
) : Screen {
    constructor(parcel: Parcel) : this(
        LibraryDataSource.parseFromString(parcel.readString()!!),
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(datasource.toStringCode())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LibraryContentListScreen> {
        override fun createFromParcel(parcel: Parcel): LibraryContentListScreen {
            return LibraryContentListScreen(parcel)
        }

        override fun newArray(size: Int): Array<LibraryContentListScreen?> {
            return arrayOfNulls(size)
        }
    }
}

actual fun newLibraryContentListScreen(datasource: LibraryDataSource): LibraryContentListScreen {
    return LibraryContentListScreen(datasource)
}

@Parcelize
actual object SearchScreen : Screen
