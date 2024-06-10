package com.example.conocimientosbasicosv0.utils

import android.content.Context
import android.util.Log
import com.example.conocimientosbasicosv0.model.Cuenta
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private val gson = Gson()

    companion object {
        const val USER_ACCOUNT_KEY = "user_account"
    }

    fun saveLoggedInAccount(cuenta: Cuenta) {
        val cuentaJson = gson.toJson(cuenta)
        editor.putString(USER_ACCOUNT_KEY, cuentaJson)
        editor.apply()
        Log.d("SESSION MANAGER", "${cuenta}")

    }

    fun getLoggedInAccount(): Cuenta? {
        val cuentaJson = prefs.getString(USER_ACCOUNT_KEY, null)
        return if (cuentaJson != null) gson.fromJson(cuentaJson, Cuenta::class.java) else null
    }

    fun isLoggedIn(): Boolean {
        return prefs.contains(USER_ACCOUNT_KEY)
    }

    fun clearSession() {
        editor.remove(USER_ACCOUNT_KEY)
        editor.apply()
    }

    fun getIdCuenta(): Int {
        val cuenta = getLoggedInAccount()
        return cuenta?.idCuenta ?: 0
    }

    fun getEmail(): String? {
        val cuenta = getLoggedInAccount()
        return cuenta?.email
    }

    fun getTipoPerfil(): Byte {
        val cuenta = getLoggedInAccount()
        return cuenta?.tipoPerfil ?: 0
    }

    fun getUrlImagenes(): String? {
        val cuenta = getLoggedInAccount()
        return cuenta?.urlImagenes
    }


}
