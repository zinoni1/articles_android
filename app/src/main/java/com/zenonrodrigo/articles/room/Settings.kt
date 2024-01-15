package com.zenonrodrigo.articles.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taulaiva")
data class Settings(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "iva")
    val iva: Float
)
