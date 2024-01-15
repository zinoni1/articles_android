package com.zenonrodrigo.articles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zenonrodrigo.articles.room.ArticleDao
import com.zenonrodrigo.articles.room.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.zenonrodrigo.articles.room.SettingsDao


class MainActivity : AppCompatActivity(), ArticleAdapter.OnDeleteClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleDao: ArticleDao
    private lateinit var settingsDao: SettingsDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        articleDao = (applicationContext as App).db.ArticleDao()
        settingsDao = (applicationContext as App).db.SettingsDao()
        articleAdapter = ArticleAdapter(mutableListOf(),this,this,settingsDao,articleDao)

        setupRecyclerView()
        CoroutineScope(Dispatchers.IO).launch {
            val articles = articleDao.getArticles()
            withContext(Dispatchers.Main) {
                articleAdapter.setData(articles)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = articleAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.buscar_desc)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if(articleAdapter.isOrderedByCode){
                    CoroutineScope(Dispatchers.IO).launch {
                        val articles = articleDao.filterArticlesByDescriptionAndCodi("%$query%")
                        withContext(Dispatchers.Main) {
                            articleAdapter.setData(articles)
                        }
                    }
                    return true
                }else
                CoroutineScope(Dispatchers.IO).launch {
                    val articles = articleDao.filterArticlesByDescription("%$query%")
                    withContext(Dispatchers.Main) {
                        articleAdapter.setData(articles)
                    }
                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ajustes -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.afegir -> {
                startActivity(Intent(this, NouArticle::class.java))
                true
            }
            R.id.filtrar_estoc -> {
                articleAdapter.filtrarEstoc()
                return true
            }
            R.id.ordenar_codigo -> {

              articleAdapter.ordenarCodis()
            return true
            }
            R.id.reset -> {

                articleAdapter.resetFilter()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfirmDeleteClick(article: Article) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
            .setMessage("¿Estás seguro de que quieres eliminar este artículo?")
            .setPositiveButton("Sí") { _, _ ->
                // Usuario confirmó, eliminar el artículo
                CoroutineScope(Dispatchers.IO).launch {
                    articleDao.delete(article)
                    val articles = articleDao.getArticles()
                    withContext(Dispatchers.Main) {
                        articleAdapter.setData(articles)
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

