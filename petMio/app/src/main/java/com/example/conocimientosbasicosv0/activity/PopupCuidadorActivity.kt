package com.example.conocimientosbasicosv0.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.Cuenta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopupCuidadorActivity : AppCompatActivity() {
    private lateinit var cuenta: Cuenta


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_cuidador)

        cuenta = intent.getSerializableExtra("EXTRA_CUENTA") as Cuenta
        var apiService = RetrofitClient.create()

        val closeButton = findViewById<Button>(R.id.close_button)
        closeButton.setOnClickListener {
            apiService.addCuidadorAccess(cuenta.idCuenta!!).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        navegarHomeActivity()
                    } else {
                        Toast.makeText(this@PopupCuidadorActivity, "Error al actualizar acceso del cuidador", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@PopupCuidadorActivity, "Error en la llamada al endpoint de actualización del cuidador: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun navegarHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("EXTRA_CUENTA", cuenta)
        }
        startActivity(intent)
        finish()
    }
}
