/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui

import android.os.Parcel
import android.os.Parcelable
import com.andannn.melodify.model.LibraryDataSource
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data object HomeScreen : Screen

@Parcelize
data object LibraryScreen : Screen

data class LibraryDetailScreen constructor(
    val datasource: LibraryDataSource,
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

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<LibraryDetailScreen> {
        override fun createFromParcel(parcel: Parcel): LibraryDetailScreen = LibraryDetailScreen(parcel)

        override fun newArray(size: Int): Array<LibraryDetailScreen?> = arrayOfNulls(size)
    }
}

@Parcelize
object SearchScreen : Screen

@Parcelize
object TabManageScreen : Screen
