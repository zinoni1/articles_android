package com.zenonrodrigo.articles

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zenonrodrigo.articles.room.Article
import com.zenonrodrigo.articles.room.ArticleDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarArticleActivity : AppCompatActivity() {

    private lateinit var articleDao: ArticleDao
    private var articleId: String = "0"
    private lateinit var articleAdapter: ArticleAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card)
        val codiEditText = findViewById<EditText>(R.id.edCodi)
        val codi = codiEditText.text.toString()
        articleDao = (applicationContext as App).db.ArticleDao()

        val codiet = findViewById<EditText>(R.id.edCodi)
        codiet.setText(codi)
        codiet.setEnabled(false)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        btnEnviar.setOnClickListener {
            validarYGuardarArticulo()
        }
        cargarInformacionArticulo()
    }

    private fun cargarInformacionArticulo() {
        articleId = intent.getStringExtra("ARTICLE_ID") ?: "0"
        CoroutineScope(Dispatchers.IO).launch {
            val existingArticle = articleDao.getArticleByCode(articleId)

            withContext(Dispatchers.Main) {
                existingArticle?.let {
                    val edCodi = findViewById<EditText>(R.id.edCodi)
                    val etDescripcio = findViewById<EditText>(R.id.etDescripcio)
                    val etEstocActiu = findViewById<EditText>(R.id.etEstocActiu)
                    val etPreuSenseIva = findViewById<EditText>(R.id.etPreuSenseIva)
                    val estocActiu = findViewById<Switch>(R.id.estocActiu)
                    edCodi.setText(it.codi_article)
                    etDescripcio.setText(it.descripcio)
                    etEstocActiu.setText(it.estoc.toString())
                    etPreuSenseIva.setText(it.preusenseiva.toString())

                    val spinner = findViewById<Spinner>(R.id.spinner)
                    val familiaArray = resources.getStringArray(R.array.familia)

                    if (familiaArray.isNotEmpty()) {
                        val adapter = ArrayAdapter(this@EditarArticleActivity, android.R.layout.simple_spinner_item, familiaArray)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter

                        val familiaPosition = familiaArray.indexOf(it.familia)
                        spinner.setSelection(if (familiaPosition != -1) familiaPosition else 0)
                    }
                    estocActiu.isChecked = it.estocActiu
                }
            }
        }
    }

    private fun validarYGuardarArticulo() {
        
        val familia = findViewById<Spinner>(R.id.spinner)
        val selectedFamilia = familia.selectedItem.toString()
        val codiEditText = findViewById<EditText>(R.id.edCodi)
        val codi = codiEditText.text.toString()
        val descripcioEditText = findViewById<EditText>(R.id.etDescripcio)
        val descripcio = descripcioEditText.text.toString()
        val estocEditText = findViewById<EditText>(R.id.etEstocActiu)
        val estoc = estocEditText.text.toString().toFloat()
        val preuEditText = findViewById<EditText>(R.id.etPreuSenseIva)
        val preu = preuEditText.text.toString().toFloatOrNull() ?: 0.0f
        val estocactiu= findViewById<Switch>(R.id.estocActiu)
        val estocActiu = estocactiu.isChecked

        if (codi.isBlank() || descripcio.isBlank() || (estoc == null)) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        } else {
            if (estocActiu && estoc < 0) {
                Toast.makeText(this, "El stock debe ser positivo", Toast.LENGTH_SHORT).show()
                return
            }
            if(codi != articleId){
                Toast.makeText(this, "No puedes cambiar el código del artículo", Toast.LENGTH_SHORT).show()
            }
            if (!estocActiu && estoc > 0) {
                Toast.makeText(
                    this,
                    "Para poner un valor en el stock, tienes que activarlo",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                val existingArticle = articleDao.getArticleByCode(codi)

                withContext(Dispatchers.Main) {
                    val nuevoArticulo = Article(
                        codi_article = articleId,
                        descripcio = descripcio,
                        estocActiu = estocActiu,
                        estoc = estoc,
                        familia = selectedFamilia,
                        preusenseiva = preu,
                        preuambiva = 0.0f
                    )
                    actualitzarArticuloEnBaseDeDatos(nuevoArticulo)
                }
            }
        }
    }

    private fun actualitzarArticuloEnBaseDeDatos(articulo: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            articleDao.update(articulo)
            val articles = articleDao.getArticles()
            withContext(Dispatchers.Main) {
                articleAdapter.setData(articles)
                Toast.makeText(this@EditarArticleActivity, "Artículo editado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}