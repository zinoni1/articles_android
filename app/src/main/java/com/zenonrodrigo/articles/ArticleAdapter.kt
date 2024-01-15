package com.zenonrodrigo.articles

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zenonrodrigo.articles.room.Article
import android.view.ViewGroup
import android.widget.ImageView
import com.zenonrodrigo.articles.room.ArticleDao
import com.zenonrodrigo.articles.room.SettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleAdapter(
    var articles: MutableList<Article>,
    private val activity: AppCompatActivity,
    private val onDeleteClickListener: OnDeleteClickListener,
    private val settingsDao: SettingsDao,
    private val articleDao: ArticleDao
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>(), CoroutineScope {
    var isOrderedByCode: Boolean = false
    override val coroutineContext = Dispatchers.Main
    private var filteredArticles: MutableList<Article> = ArrayList()

    init {
        filteredArticles.addAll(articles)
    }

    interface OnDeleteClickListener {
        fun onConfirmDeleteClick(article: Article)
    }

    fun filtrarEstoc(){
        CoroutineScope(Dispatchers.IO).launch {
            val articles = articleDao.filterArticlesByEstocActiu()
            withContext(Dispatchers.Main) {
                setData(articles)
            }
        }
    }
    fun ordenarCodis() {
        CoroutineScope(Dispatchers.IO).launch {
            val articles = if (isOrderedByCode) {
                articleDao.getArticles()
            } else {
                articleDao.getArticlesOrdenar()
            }
            withContext(Dispatchers.Main) {
                isOrderedByCode = !isOrderedByCode
                setData(articles)
            }
        }
    }

    fun resetFilter() {
        Intent(activity, MainActivity::class.java).also {
            activity.startActivity(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.llista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, EditarArticleActivity::class.java)
            intent.putExtra("ARTICLE_ID", article.codi_article)
            activity.startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val iva = settingsDao.getIva()?.iva ?: 0.0f
            withContext(Dispatchers.Main) {
                holder.preuambiva.text = (article.preusenseiva * (1 + iva / 100)).toString()
            }
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClickListener.onConfirmDeleteClick(articles[position])
        }
        when (article.familia) {
            activity.getString(R.string.roba) -> holder.itemView.setBackgroundColor(activity.resources.getColor(R.color.red))
            activity.getString(R.string.electronica) -> holder.itemView.setBackgroundColor(activity.resources.getColor(R.color.yellow))
            activity.getString(R.string.alimentacio) -> holder.itemView.setBackgroundColor(activity.resources.getColor(R.color.green))
            else -> holder.itemView.setBackgroundColor(activity.resources.getColor(R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun setData(articles: MutableList<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val codi_article: TextView = view.findViewById(R.id.textView8)
        private val descripcio: TextView = view.findViewById(R.id.TextViewDescripcio)
        private val familia: TextView = view.findViewById(R.id.TextViewFamilia)
        private val preusenseiva: TextView = view.findViewById(R.id.tvPreusenseIvaLlista)
        val preuambiva: TextView = view.findViewById(R.id.tvPreuIvaLlista)
        private val estoc: TextView = view.findViewById(R.id.tvEstocList)
        private val textview7: TextView = view.findViewById(R.id.textView7)
        val btnEliminar: ImageView = view.findViewById(R.id.eliminar)

        fun bind(article: Article) {
            codi_article.text = article.codi_article
            descripcio.text = article.descripcio
            familia.text = article.familia
            preusenseiva.text = article.preusenseiva.toString()

            if (article.estocActiu) {
                estoc.visibility = View.VISIBLE
                textview7.visibility = View.VISIBLE
                estoc.text = article.estoc.toString()
            } else {
                estoc.visibility = View.GONE
                textview7.visibility = View.GONE
            }
        }
    }
}