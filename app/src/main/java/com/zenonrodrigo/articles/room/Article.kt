package com.zenonrodrigo.articles.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article")
data class Article(
    @PrimaryKey
    var codi_article: String,
    var descripcio: String,
    var familia: String?,
    var preusenseiva: Float,
    var preuambiva: Float,
    @ColumnInfo(name = "estocActiu")
    var estocActiu: Boolean,
    @ColumnInfo(name = "estoc")
    var estoc: Float,
)
