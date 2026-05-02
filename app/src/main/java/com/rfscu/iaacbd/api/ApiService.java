package com.rfscu.iaacbd.api;

import com.rfscu.iaacbd.model.Instrumento;
import com.rfscu.iaacbd.model.InstrumentoCreateRequest;
import com.rfscu.iaacbd.model.InstrumentoUpdateRequest;
import com.rfscu.iaacbd.model.Certificado;
import com.rfscu.iaacbd.model.CertificadoRequest;
import com.rfscu.iaacbd.model.HistorialAcceso;
import com.rfscu.iaacbd.model.LoginRequest;
import com.rfscu.iaacbd.model.LoginResponse;
import com.rfscu.iaacbd.model.User;
import com.rfscu.iaacbd.model.UserRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    // Usuarios
    @GET("/usuarios")
    Call<List<User>> getUsers();
    
    @POST("/usuarios")
    Call<User> createUser(@Body UserRequest request);
    
    @PUT("/usuarios/{id}")
    Call<User> updateUser(@Path("id") String id, @Body UserRequest request);
    
    @DELETE("/usuarios/{id}")
    Call<Void> deleteUser(@Path("id") String id);

    // Instrumentos
    @GET("/instrumentos")
    Call<List<Instrumento>> getInstrumentos();

    @GET("/instrumentos")
    Call<List<Instrumento>> searchInstrumentos(@retrofit2.http.QueryMap java.util.Map<String, String> filters);

    @GET("/instrumentos/filtros/distintos")
    Call<java.util.Map<String, List<String>>> getDistinctValues();

    @POST("/instrumentos")
    Call<Instrumento> createInstrumento(@Body InstrumentoCreateRequest instrument);

    @PUT("/instrumentos/{id}")
    Call<Instrumento> updateInstrumento(@Path("id") int id, @Body InstrumentoUpdateRequest request);

    @DELETE("/instrumentos/{id}")
    Call<Void> deleteInstrumento(@Path("id") int id);

    @GET("/historial")
    Call<List<HistorialAcceso>> getHistorial();

    @GET("/historial")
    Call<List<HistorialAcceso>> getFilteredHistorial(
        @retrofit2.http.Query("username") String username,
        @retrofit2.http.Query("fecha") String fecha
    );

    @GET("/certificados")
    Call<List<Certificado>> getCertificados(
        @retrofit2.http.Query("no_certificado") String noCertificado,
        @retrofit2.http.Query("no_serie") String noSerie
    );

    @POST("/certificados")
    Call<Certificado> createCertificado(@Body CertificadoRequest request);
}