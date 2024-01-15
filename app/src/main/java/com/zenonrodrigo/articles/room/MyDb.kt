package com.zenonrodrigo.articles.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
@Database(entities = [Article::class, Settings::class], version = 1)
abstract class MyDb : RoomDatabase() {
    abstract fun ArticleDao(): ArticleDao
    abstract fun SettingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: MyDb? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): MyDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDb::class.java,
                    "article-db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
               INSTANCE = instance
                instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}
