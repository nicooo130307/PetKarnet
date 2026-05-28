package com.example.petkarnet.data.network

import com.example.petkarnet.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // usuarios
    @POST("api/usuarios/registro")
    suspend fun registro(@Body body: RegistroRequest): Response<RegistroResponse>

    @POST("api/usuarios/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("api/usuarios/perfil")
    suspend fun perfil(): Response<Usuario>

    @PATCH("api/usuarios/{id}/verificar")
    suspend fun verificarVeterinario(@Path("id") id: Int): Response<MensajeResponse>
    //para borrar la cuenta
    @DELETE("api/usuarios")
    suspend fun eliminarCuenta(): Response<MensajeResponse>

    // mascotas
    @POST("api/mascotas")
    suspend fun crearMascota(@Body body: MascotaRequest): Response<MensajeIdResponse>

    @GET("api/mascotas")
    suspend fun listarMascotas(): Response<List<Mascota>>

    @GET("api/mascotas/{id}")
    suspend fun obtenerMascota(@Path("id") id: Int): Response<Mascota>

    @PUT("api/mascotas/{id}")
    suspend fun actualizarMascota(@Path("id") id: Int, @Body body: MascotaRequest): Response<MensajeResponse>

    @DELETE("api/mascotas/{id}")
    suspend fun eliminarMascota(@Path("id") id: Int): Response<MensajeResponse>

    // historiales
    @POST("api/historial")
    suspend fun agregarVacuna(@Body body: VacunaRequest): Response<MensajeIdResponse>

    @GET("api/historial/{idMascota}")
    suspend fun obtenerHistorial(@Path("idMascota") idMascota: Int): Response<List<HistorialEntry>>

    // citas
    @POST("api/citas")
    suspend fun agendarCita(@Body body: CitaRequest): Response<MensajeIdResponse>

    @GET("api/citas")
    suspend fun listarCitas(): Response<List<Cita>>

    @PATCH("api/citas/{id}/estado")
    suspend fun cambiarEstadoCita(@Path("id") id: Int, @Body body: EstadoCitaRequest): Response<MensajeResponse>

    // dispositivos
    @POST("api/dispositivos")
    suspend fun registrarDispositivo(@Body body: TokenFCMRequest): Response<MensajeResponse>

    @GET("api/usuarios/veterinarios")
    suspend fun listarVeterinarios(): Response<List<Usuario>>

    //ubicacion del veterinario
    @GET("api/ubicaciones")
    suspend fun listarUbicaciones(): Response<List<Ubicacion>>

    //reseñas por si acaso nomas nico
    @POST("api/resenas")
    suspend fun crearResena(@Body body: ResenaRequest): Response<MensajeResponse>

    @GET("api/resenas/{idVeterinario}")
    suspend fun listarResenas(@Path("idVeterinario") idVeterinario: Int): Response<List<Resena>>
}