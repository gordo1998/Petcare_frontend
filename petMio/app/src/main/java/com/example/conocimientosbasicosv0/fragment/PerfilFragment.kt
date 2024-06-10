package com.example.conocimientosbasicosv0.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.activity.HomeActivity
import com.example.conocimientosbasicosv0.activity.HomeCuidadorActivity
import com.example.conocimientosbasicosv0.activity.LoginActivity
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.Cuenta
import com.example.conocimientosbasicosv0.utils.SessionManager
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var imageViewPerfil: ImageView
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellidoUno: EditText
    private lateinit var editTextApellidoDos: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var textViewEmail: TextView
    private lateinit var editTextNumMovil: EditText
    private lateinit var editTextNumTelefono: EditText
    private lateinit var editTextPasswd: EditText
    private lateinit var buttonEdit: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonLogout: Button
    private lateinit var buttonDeleteAccount: Button
    private var imageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val cuenta = sessionManager.getLoggedInAccount()

        imageViewPerfil = view.findViewById(R.id.imageViewPerfil)
        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextApellidoUno = view.findViewById(R.id.editTextApellidoUno)
        editTextApellidoDos = view.findViewById(R.id.editTextApellidoDos)
        editTextUsername = view.findViewById(R.id.editTextUsername)
        //textViewEmail = view.findViewById(R.id.textViewEmail)
        editTextNumMovil = view.findViewById(R.id.editTextNumMovil)
        editTextNumTelefono = view.findViewById(R.id.editTextNumTelefono)
        editTextPasswd = view.findViewById(R.id.editTextPasswd)
        buttonEdit = view.findViewById(R.id.buttonEdit)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonLogout = view.findViewById(R.id.buttonLogout)
        buttonDeleteAccount = view.findViewById(R.id.buttonDeleteAccount)
        val switchPerfil = view.findViewById<Switch>(R.id.switchPerfil)

        // Configurar el switch
        switchPerfil.isChecked = cuenta?.tipoPerfil == 1.toByte()
        switchPerfil.setOnCheckedChangeListener { _, isChecked ->
            cuenta?.tipoPerfil = if (isChecked) 1 else 0
            if (cuenta != null) {
                sessionManager.saveLoggedInAccount(cuenta)
            }
            actualizarUI(cuenta)
            if (isChecked) {
                // Cambiar a HomeCuidadorActivity
                val intent = Intent(activity, HomeCuidadorActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                // Cambiar a HomeActivity
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        // Cargar datos del usuario
        cuenta?.let {
            editTextNombre.setText(it.nombre)
            editTextApellidoUno.setText(it.apellidoPrimero)
            editTextApellidoDos.setText(it.apellidoDos)
            editTextUsername.setText(it.username)
            //textViewEmail.text = it.email
            editTextNumMovil.setText(it.movil?.toString())
            editTextNumTelefono.setText(it.telefono?.toString())
            editTextPasswd.setText(it.passwd)
            if (!it.urlImagenes.isNullOrEmpty()) {
                Picasso.get().load(it.urlImagenes).into(imageViewPerfil)
            }
        }

        buttonEdit.setOnClickListener {
            enableEditing(true)
        }

        buttonSave.setOnClickListener {
            val cuentaMap = mutableMapOf<String, String>()

            cuentaMap["idcuenta"] = sessionManager.getIdCuenta().toString()
            cuentaMap["email"] = sessionManager.getEmail() ?: ""
            cuentaMap["apellidoDos"] = editTextApellidoDos.text.toString()
            cuentaMap["apellidoPrimero"] = editTextApellidoUno.text.toString()
            cuentaMap["movil"] = editTextNumMovil.text.toString()
            cuentaMap["nombre"] = editTextNombre.text.toString()
            cuentaMap["passwd"] = editTextPasswd.text.toString()
            cuentaMap["telefono"] = editTextNumTelefono.text.toString()
            cuentaMap["username"] = editTextUsername.text.toString()

            val apiService = RetrofitClient.create()

            apiService.updateC(cuentaMap).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Crear el objeto Cuenta con los datos actualizados
                        val updatedCuenta = Cuenta(
                            idCuenta = cuentaMap["idcuenta"]?.toIntOrNull(),
                            email = cuentaMap["email"],
                            apellidoDos = cuentaMap["apellidoDos"],
                            apellidoPrimero = cuentaMap["apellidoPrimero"],
                            movil = cuentaMap["movil"]?.toIntOrNull(),
                            nombre = cuentaMap["nombre"],
                            passwd = cuentaMap["passwd"],
                            telefono = cuentaMap["telefono"]?.toIntOrNull(),
                            username = cuentaMap["username"],
                            tipoPerfil = sessionManager.getTipoPerfil(),
                            urlImagenes = sessionManager.getUrlImagenes()
                        )

                        // Guardar la cuenta actualizada en SessionManager
                        sessionManager.saveLoggedInAccount(updatedCuenta)

                        Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        enableEditing(false)
                    } else {
                        Log.d("ActualizarCuenta", "Error al actualizar la cuenta: $cuentaMap")
                        Toast.makeText(requireContext(), "Error al actualizar la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        buttonLogout.setOnClickListener {
            cerrarSesion()
        }

        buttonDeleteAccount.setOnClickListener {
            cuenta?.idCuenta?.let { id ->
                mostrarConfirmacionEliminarCuenta(id)
            }
        }

        imageViewPerfil.setOnClickListener {
            if (buttonSave.isEnabled) {
                openImageChooser()
            }
        }
    }

    private fun actualizarUI(cuenta: Cuenta?) {
        // Aquí puedes actualizar la interfaz según el perfil seleccionado.
        // Por ejemplo, cambiar colores o mostrar/ocultar opciones específicas para dueños o cuidadores.
    }

    private fun enableEditing(enable: Boolean) {
        editTextNombre.isEnabled = enable
        editTextApellidoUno.isEnabled = enable
        editTextApellidoDos.isEnabled = enable
        editTextUsername.isEnabled = enable
        editTextNumMovil.isEnabled = enable
        editTextNumTelefono.isEnabled = enable
        editTextPasswd.isEnabled = enable
        buttonSave.isEnabled = enable
        buttonEdit.isEnabled = !enable
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Picasso.get().load(imageUri).into(imageViewPerfil)
        }
    }

    private fun cerrarSesion() {
        // Limpia los datos de sesión
        sessionManager.clearSession()

        // Crea un Intent para iniciar LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)

        // Limpiar la pila de actividades para evitar que el usuario regrese a la pantalla de perfil
        // después de haber cerrado sesión y ser redirigido al login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Inicia LoginActivity
        startActivity(intent)

        activity?.finish()
    }

    private fun mostrarConfirmacionEliminarCuenta(idCuenta: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ -> eliminarCuenta(idCuenta) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCuenta(idCuenta: Int) {
        RetrofitClient.create().deleteCuenta(idCuenta).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show()
                    cerrarSesion() // Cerrar sesión después de eliminar la cuenta
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                    Log.e("PerfilFragment", "Error al eliminar la cuenta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("PerfilFragment", "Fallo en la conexión", t)
            }
        })
    }
}
