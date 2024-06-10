package com.example.proyectom13.retrofit


import com.example.conocimientosbasicosv0.model.MascotaInfo
import com.example.conocimientosbasicosv0.model.Raza
import com.example.conocimientosbasicosv0.model.ReservaMascotaRequest
import com.example.conocimientosbasicosv0.model.ReservaRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.PUT
import retrofit2.http.Path


interface APIService {

    @POST("registroCuenta")
    fun registrarCuenta(@Body datosUser: ArrayList<String>): Call<Boolean>

    @POST("loginCuenta")
    fun loginCuenta(@Body datosUser: List<String>): Call<Map<String, Any>>
    @GET("getServicios")
    fun getServicios(): Call<Map<String, String>>

    @GET("getServiciosCuidador/{id}")
    fun getServiciosCuidador(@Path("id") id: Int): Call<Map<String, List<String>>>

    @GET("getServicios/{idCuidador}")
    fun getServicios(@Path("idCuidador") idCuidador: Int): Call<Map<String, Map<String, Any>>>

    @PUT("updateC") //update cuenta
    fun updateC(@Body cuentaMap: Map<String, String>): Call<Void>

    @PUT("upCuidador")
    fun updateCuidador(@Body datosCuidador: Map<String, @JvmSuppressWildcards Any>): Call<Void>

    @GET("getTimeAccessDueño/{idDueno}")
    fun getTimeAccessDueño(@Path("idDueno") idDueno: Int): Call<Int>

    @PUT("addDueñoAccess/{idDueno}")
    fun addDueñoAccess(@Path("idDueno") idDueno: Int): Call<Void>

    @GET("getAccesTimes/{idCuidador}")
    fun getAccesTimes(@Path("idCuidador") idCuidador: Int): Call<Int>

    @PUT("addCuidadorAccess/{idCuidador}")
    fun addCuidadorAccess(@Path("idCuidador") idCuidador: Int): Call<Void>

    @GET("getAnimales")
    fun getAnimales(): Call<List<String>>
    @GET("getRazas/{idAnimal}")
    fun getRazas(@Path("idAnimal") idAnimal: Int): Call<Map<String, Raza>>


    @POST("addMascota")
    fun addMascota(
        @Body mascotaInfo: List<String>
    ): Call<Void>

    @POST("addDueño")
    fun addDueño(@Body emails: List<String>): Call<Void>

    @GET("getMascotasReservadas/{idDueno}")
    fun getMascotasReservadas(@Path("idDueno") idDueño: Int): Call<Map<String, String>>

    @GET("getReservasDueño/{idDueno}")
    fun getReservasDueño(@Path("idDueno") idDueño: Int): Call<Map<String, Map<String, Any>>>

    @GET("getReservasCuidador/{idCuidador}")
    fun getReservasCuidador(@Path("idCuidador") idCuidador: Int): Call<Map<String, Map<String, Any>>>

    @GET("getMascotasDueño/{idDueno}")
    fun getMascotasByDueño(@Path("idDueno") idDueno: Int): Call<Map<String, MascotaInfo>>

    @POST("addReserva")
    fun addReserva(@Body datosReserva: ReservaRequest): Call<Int>

    @POST("setMascotaReservada")
    fun setMascotaReservada(@Body idsReservaMascotas: ReservaMascotaRequest): Call<Void>

    @POST("setCuidadorFavorito")
    fun setCuidadorFavorito(@Body idsCuidadores: Map<String, Int>): Call<Void>

    @GET("getCuidadoresFavoritos/{idDueno}")
    fun getCuidadoresFavoritos(@Path("idDueno") idDueno: Int): Call<Map<String, Map<String, Any>>>

    @HTTP(method = "DELETE", path = "deleteCuidadorFavorito", hasBody = true)
    fun deleteCuidadorFavorito(@Body idsCuidadoresFavoritos: Map<String, Int>): Call<Void>

    @DELETE("deleteCuentaId/{id}")
    fun deleteCuenta(@Path("id") id: Int): Call<Void>

    @DELETE("deleteMascota/{idMascota}")
    fun deleteMascota(@Path("idMascota") idMascota: Int): Call<Void>
}


