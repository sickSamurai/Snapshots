package com.blackpanthers.snapshots.db

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Snapshot(@get: Exclude var id: String = "",
                    var title: String = "",
                    var photoURL: String = "",
                    var likeList: Map<String, Boolean> = mutableMapOf())
