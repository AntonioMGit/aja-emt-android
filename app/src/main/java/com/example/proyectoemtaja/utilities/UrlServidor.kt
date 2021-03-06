package com.example.proyectoemtaja.utilities

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * URL del servidor
 */
class UrlServidor {

    companion object {

        const val URL_BASE: String = "http://ec2-35-181-63-163.eu-west-3.compute.amazonaws.com"
        //const val URL_BASE: String = "http://192.168.1.132:8080"

        fun urlTiempoAutobus (parada: String): String {
            return "/controladores-emt/consultar-parada/$parada"
        }

        fun urlParadasLinea (linea: String, dir: String): String {
            return "/controladores-emt/consultar-linea/$linea/$dir"
        }

        const val URL_LISTAR_PARADAS = "/controladores-emt/listar-paradas"

        const val URL_INSERTAR_USUARIO = "/usuario/insertar"

        const val URL_LOGIN = "/usuario/login"

        const val URL_PROBAR_TOKEN = "/usuario/probar-token"

        const val URL_LISTAR_FAVORITOS = "/favorito/obtener-favoritos"

        const val URL_ACTUALIZAR_USUARIO = "/usuario/actualizar"

        const val URL_BUSCAR_USUARIO = "/usuario/buscar"

        const val URL_INSERTAR_FAVORITO = "/favorito/insertar"

        const val URL_ACTUALIZAR_FAVORITO = "/favorito/actualizar"

        const val URL_BORRAR_FAVORITO = "/favorito/borrar"

        const val URL_PEDIR_CODIGO = "/usuario/codigo-recuperacion"

        const val URL_CAMBIAR_PASSWORD = "/usuario/cambiar-clave"

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).build()

        }
    }
}