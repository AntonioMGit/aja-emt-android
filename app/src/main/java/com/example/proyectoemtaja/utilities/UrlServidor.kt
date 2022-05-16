package com.example.proyectoemtaja.utilities

class UrlServidor {

    companion object {
        // val URL_BASE: String = "http://172.26.110.199:8080"
        const val URL_BASE: String = "http://192.168.1.132:8080"

        fun urlTiempoAutobus (parada: String): String {
            return "/controladores-emt/consultar-parada/$parada"
        }

        const val URL_LISTAR_PARADAS = "/controladores-emt/listar-paradas"

        const val URL_INSERTAR_USUARIO = "/usuario/insertar"

        const val URL_LOGIN = "/usuario/login"

        const val URL_PROBAR_TOKEN = "/usuario/probar-token"

        const val URL_LISTAR_FAVORITOS = "/favorito/obtener-favoritos"

        const val URL_ACTUALIZAR_USUARIO = "/usuario/actualizar"

        const val URL_BUSCAR_USUARIO = "/usuario/buscar"
    }
}