package com.example.conocimientosbasicosv0.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.Cuenta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopupDuenoActivity : AppCompatActivity() {
    private lateinit var cuenta: Cuenta
    var apiService = RetrofitClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_dueno)
        Log.d("PopupDuenoActivity", "onCreate called")

        // Verificar si se recibió el extra y asignarlo a la variable cuenta
        cuenta = intent.getSerializableExtra("EXTRA_CUENTA") as? Cuenta
            ?: run {
                Toast.makeText(this, "No se pudo obtener la cuenta", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        val closeButton = findViewById<Button>(R.id.close_button)
        closeButton.setOnClickListener {
            addDueñoAccess();
            navegarHomeActivity()
        }

        val textViewSuggestion = findViewById<TextView>(R.id.textViewSuggestion)
        textViewSuggestion.setOnClickListener {
            addDueñoAccess();
            navegarCrearMascotaFragment()
        }


    }

    private fun addDueñoAccess() {
        apiService.addDueñoAccess(cuenta.idCuenta!!).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@PopupDuenoActivity, "Error al actualizar acceso del dueño", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PopupDuenoActivity, "Error en la llamada al endpoint de actualización del dueño: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navegarHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("EXTRA_CUENTA", cuenta)
        }
        startActivity(intent)
        finish()
    }

    private fun navegarCrearMascotaFragment() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("EXTRA_CUENTA", cuenta)
            putExtra("SHOW_CREAR_MASCOTA", true)
        }
        startActivity(intent)
        finish()
    }
}
