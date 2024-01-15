package com.zenonrodrigo.articles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.zenonrodrigo.articles.room.Settings
import com.zenonrodrigo.articles.room.SettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var posarIvaEditText: EditText
    private lateinit var settingsDao: SettingsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        settingsDao = (applicationContext as App).db.SettingsDao()

        posarIvaEditText = findViewById(R.id.posarIva)
        val enviarIvaButton: Button = findViewById(R.id.enviarIva)

        enviarIvaButton.setOnClickListener {
            val ivaValue = posarIvaEditText.text.toString().toFloat()

            val settings = Settings(iva = ivaValue)
            CoroutineScope(Dispatchers.IO).launch {
                settingsDao.insertOrUpdateSettings(settings)
            }
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }
    }
}