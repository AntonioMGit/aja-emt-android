package com.example.proyectoemtaja.utilities

object ConversorCodigoEMT {

    //convierte codigo de numeros en codigo con letras
    public fun pasarALetras(codInicial: String): String {
        var codFinal = ""

        var intInicial = codInicial.toInt()

        //https://es.wikipedia.org/wiki/Anexo:L%C3%ADneas_de_la_EMT_Madrid#N%C3%BAmeros_usados_internamente
        when (intInicial) {
            //circulares
            68 -> codFinal = "C1"
            69 -> codFinal = "C2"
            //universitarias
            90 -> codFinal = "E"
            91 -> codFinal = "F"
            92 -> codFinal = "G"
            93 -> codFinal = "A"
            96 -> codFinal = "H"
            99 -> codFinal = "U"

            //TODO: 203	Línea Exprés Aeropuerto (servicio diurno)

            //lineas cero
            361 -> codFinal = "001"
            362 -> codFinal = "002"
            363 -> codFinal = "C03" //ojo, pone C no 0
            372 -> codFinal = "172"
            //401 a 405	Líneas exprés: E1 a E5
            in 401..405 -> codFinal = "E" + (intInicial-400).toString()
            //451 a 457	Líneas Trabajo: T11, T23, T32, T31, T61, T41, T62; en ese orden
            451 -> codFinal = "T11"
            452 -> codFinal = "T23"
            453 -> codFinal = "T32"
            454 -> codFinal = "T31"
            455 -> codFinal = "T61"
            456 -> codFinal = "T41"
            457 -> codFinal = "T62"
            481 -> codFinal = "H1"
            //501 a 528	Líneas nocturnas: N1 a N28, siendo la N27 (=527) la línea Exprés Aeropuerto en su servicio nocturno
            in 501..528 -> codFinal = "N" + (intInicial-500).toString()
            601 -> codFinal = "M1"

            //TODO: 700 a 799	Servicios Especiales, casi siempre denominados simplemente «SE»

            else -> { //si no es ninguno
                codFinal = codInicial
            }
        }
        return codFinal
    }

    //convierte codigo con letras en codigo de numeros
    public fun pasarANumeros(codInicial: String): String {
        var codFinal = ""

        //https://es.wikipedia.org/wiki/Anexo:L%C3%ADneas_de_la_EMT_Madrid#N%C3%BAmeros_usados_internamente
        when (codInicial) {
            //circulares
            "C1" -> codFinal = "68"
            "C2" -> codFinal = "69"
            //universitarias
            "E" -> codFinal = "90"
            "F" -> codFinal = "91"
            "G" -> codFinal = "92"
            "A" -> codFinal = "93"
            "H" -> codFinal = "96"
            "U" -> codFinal = "99"

            //TODO: 203	Línea Exprés Aeropuerto (servicio diurno)

            //lineas cero
            "001" -> codFinal = "361"
            "002" -> codFinal = "361"
            "C03" -> codFinal = "362" //ojo, pone C no 0
            "172" -> codFinal = "372"
            //401 a 405	Líneas exprés: E1 a E5
            "E1" -> codFinal = "401"
            "E2" -> codFinal = "402"
            "E3" -> codFinal = "403"
            "E4" -> codFinal = "404"
            "E5" -> codFinal = "405"
            //T
            "T11" -> codFinal = "451"
            "T23" -> codFinal = "452"
            "T32" -> codFinal = "453"
            "T31" -> codFinal = "454"
            "T61" -> codFinal = "455"
            "T41" -> codFinal = "456"
            "T62" -> codFinal = "457"
            "H1" -> codFinal = "481"
            // todo 501 a 528	Líneas nocturnas: N1 a N28, siendo la N27 (=527) la línea Exprés Aeropuerto en su servicio nocturno
            "N7" -> codFinal = "507"

            //in 501..528 -> codFinal = "N" + (intInicial-500).toString()
            "M1" -> codFinal = "601"
            //TODO: 700 a 799	Servicios Especiales, casi siempre denominados simplemente «SE»

            else -> { //si no es ninguno
                codFinal = codInicial
            }
        }
        return codFinal
    }

}