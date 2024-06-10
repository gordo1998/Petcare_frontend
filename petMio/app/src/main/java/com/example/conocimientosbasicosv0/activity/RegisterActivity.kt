package com.example.conocimientosbasicosv0.activity

import android.content.Intent
import android.os.Bundle
import retrofit2.Call

import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.api.RetrofitClient
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val firstNameEditText: EditText = findViewById(R.id.editTextFirstName)
        val lastName1EditText: EditText = findViewById(R.id.editTextLastName1)
        val lastName2EditText: EditText = findViewById(R.id.editTextLastName2)
        val usernameEditText: EditText = findViewById(R.id.editTextUsername)
        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val confirmPasswordEditText: EditText = findViewById(R.id.editTextConfirmPassword)
        val registerButton: Button = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName1 = lastName1EditText.text.toString()
            val lastName2 = lastName2EditText.text.toString()
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (validateRegistrationData(firstName, lastName1, lastName2, username, email, password, confirmPassword)) {
                registerUser(firstName, lastName1, lastName2, username, email, password)

            } else {
                // Los mensajes de error se manejan dentro de validateRegistrationData
            }
        }
    }

    fun openLoginActivity(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun registerUser(firstName: String, lastName1: String, lastName2: String, username: String, email: String, password: String) {
        // Construir el ArrayList con los datos del usuario
        val datosUser = arrayListOf(firstName, lastName1, lastName2, username, email, password)

        val apiService = RetrofitClient.create()
        apiService.registrarCuenta(datosUser).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    // Registro exitoso, redirigir al LoginActivity
                    Toast.makeText(this@RegisterActivity, "Registro exitoso.", Toast.LENGTH_SHORT).show()

                    addDueño(email)

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Manejar el error de registro
                    Toast.makeText(this@RegisterActivity, "Registro fallido: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // Manejar fallo en la llamada
                Toast.makeText(this@RegisterActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addDueño(email: String) {
        val apiService = RetrofitClient.create()

        // Crear una lista con el email
        val emailList = listOf(email)

        apiService.addDueño(emailList).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Manejar éxito si es necesario
                    Toast.makeText(this@RegisterActivity, "Dueño añadido exitosamente.", Toast.LENGTH_SHORT).show()
                } else {
                    // Manejar el error
                    Toast.makeText(this@RegisterActivity, "Fallo al añadir dueño: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Manejar fallo en la llamada
                Toast.makeText(this@RegisterActivity, "Error de conexión al añadir dueño: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun validateRegistrationData(firstName: String, lastName1: String, lastName2: String, username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu nombre", Toast.LENGTH_SHORT).show()
            return false
        }

        if (lastName1.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu primer apellido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (lastName2.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu segundo apellido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "La contraseña no cumple con los requisitos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        //val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        //return password.matches(passwordPattern.toRegex())
        return true
    }
}

