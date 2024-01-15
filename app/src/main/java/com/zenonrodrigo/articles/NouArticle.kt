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

class NouArticle : AppCompatActivity() {
    private lateinit var codiEditText: EditText
    private lateinit var descripcioEditText: EditText
    private lateinit var estocEditText: EditText
    private lateinit var estocActiu: Switch
    private lateinit var preuEditText: EditText
    private lateinit var articleDao: ArticleDao
    private lateinit var familia: Spinner
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card)
        articleDao = (applicationContext as App).db.ArticleDao()
        codiEditText = findViewById(R.id.edCodi)
        descripcioEditText = findViewById(R.id.etDescripcio)
        estocEditText = findViewById(R.id.etEstocActiu)
        preuEditText = findViewById(R.id.etPreuSenseIva)
        familia = findViewById(R.id.spinner)
        estocActiu = findViewById(R.id.estocActiu)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val opciones = resources.getStringArray(R.array.familia)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)

        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adaptador

        val enviarButton: Button = findViewById(R.id.btnEnviar)
        enviarButton.setOnClickListener {
            validarYGuardarArticulo()
        }
    }

    private fun validarYGuardarArticulo() {
        val selectedFamilia = familia.selectedItem.toString()
        val codi = codiEditText.text.toString()
        val descripcio = descripcioEditText.text.toString()
        val estoc = estocEditText.text.toString().toFloat()
        val preu = preuEditText.text.toString().toFloatOrNull() ?: 0.0f
        val estocActiu = estocActiu.isChecked

        if (codi.isBlank() || descripcio.isBlank() || (estoc == null)) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        } else {

            if (estocActiu && estoc < 0) {
                Toast.makeText(this, "El stock debe ser positivo", Toast.LENGTH_SHORT).show()
                return
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
                    if (existingArticle != null) {
                        Toast.makeText(
                            this@NouArticle,
                            "Ya existe un artículo con el mismo código",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val nuevoArticulo = Article(
                            codi_article = codi,
                            descripcio = descripcio,
                            estocActiu = estocActiu,
                            estoc = estoc,
                            familia = selectedFamilia,
                            preusenseiva = preu,
                            preuambiva = 0.0f
                        )
                        guardarArticuloEnBaseDeDatos(nuevoArticulo)
                    }
                }
            }
        }
    }
    private fun guardarArticuloEnBaseDeDatos(articulo: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            articleDao.insert(articulo)
            val articles = articleDao.getArticles()
            withContext(Dispatchers.Main) {
                val articles = articleDao.getArticles()
                articleAdapter.setData(articles)
            }
            withContext(Dispatchers.Main) {
                articleAdapter.setData(articles)
                Toast.makeText(this@NouArticle, "Artículo agregado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}