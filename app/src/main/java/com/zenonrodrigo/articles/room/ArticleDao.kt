package com.zenonrodrigo.articles.room;

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Dao
interface ArticleDao {

    @Query("SELECT * FROM article ORDER BY codi_article ASC")
    fun getArticles(): MutableList<Article>

    @Query("SELECT * FROM article ORDER BY CAST(codi_article AS INTEGER) ASC")
    fun getArticlesOrdenar(): MutableList<Article>


    @Query("SELECT * FROM article WHERE codi_article = :codi LIMIT 1")
    fun getArticleByCode(codi: String): Article?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(article: Article): Long

    @Delete
    fun delete(article: Article)

    @Update
    fun update(article: Article)

    @Query("DELETE FROM Article")
    fun deleteAll()

    @Query("select coalesce(max(codi_article),0) from article")
    fun getLastId(): Int

    @Query("SELECT * FROM article WHERE descripcio LIKE :query")
    fun filterArticlesByDescription(query: String): MutableList<Article>

    @Query("SELECT * FROM article WHERE estocActiu = 1 ORDER BY codi_article ASC")
    fun filterArticlesByEstocActiu(): MutableList<Article>

    @Query("SELECT * FROM article WHERE descripcio LIKE :query ORDER BY codi_article ASC")
    fun filterArticlesByDescriptionAndCodi(query: String): MutableList<Article>

}
