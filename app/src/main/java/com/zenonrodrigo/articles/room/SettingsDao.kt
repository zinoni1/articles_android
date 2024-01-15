package com.zenonrodrigo.articles.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM taulaiva WHERE id = 1")
    fun getIva(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateSettings(settings: Settings)
}
