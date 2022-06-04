package com.example.proyectoemtaja.utilities

object ConversorCodigoEMT {

    /**
     * Convierte codigo de numeros en codigo con letras.
     *
     * https://es.wikipedia.org/wiki/Anexo:L%C3%ADneas_de_la_EMT_Madrid#N%C3%BAmeros_usados_internamente
     *
     * @param codInicial codigo a transformar
     * @return codigo transformado
     */
    fun pasarALetras(codInicial: String): String {
        return when (codInicial.toInt()) {
            //circulares
            68 -> "C1"
            69 -> "C2"
            //universitarias
            90 -> "E"
            91 -> "F"
            92 -> "G"
            93 -> "A"
            96 -> "H"
            99 -> "U"

            //TODO: 203	Línea Exprés Aeropuerto (servicio diurno)

            //lineas cero
            361 -> "001"
            362 -> "002"
            363 -> "C03" //ojo, pone C no 0
            372 -> "172"

            //401 a 405	Líneas exprés: E1 a E5
            in 401..405 -> "E" + (codInicial.toInt() - 400).toString()

            //451 a 457	Líneas Trabajo: T11, T23, T32, T31, T61, T41, T62; en ese orden
            451 -> "T11"
            452 -> "T23"
            453 -> "T32"
            454 -> "T31"
            455 -> "T61"
            456 -> "T41"
            457 -> "T62"
            481 -> "H1"

            //501 a 528	Líneas nocturnas: N1 a N28, siendo la N27 (=527) la línea Exprés Aeropuerto en su servicio nocturno
            in 501..528 -> "N" + (codInicial.toInt() - 500).toString()
            601 -> "M1"

            //TODO: 700 a 799	Servicios Especiales, casi siempre denominados simplemente «SE»

            //Si no es ninguno, es que es asi
            else -> codInicial
        }
    }

    /**
     * Convierte codigo con letras en codigo de numeros
     *
     * https://es.wikipedia.org/wiki/Anexo:L%C3%ADneas_de_la_EMT_Madrid#N%C3%BAmeros_usados_internamente
     *
     * @param codInicial codigo a transformar
     * @param codigo transformado
     */
    fun pasarANumeros(codInicial: String): String {
        return when (codInicial) {
            //circulares
            "C1" -> "68"
            "C2" -> "69"
            //universitarias
            "E" -> "90"
            "F" -> "91"
            "G" -> "92"
            "A" -> "93"
            "H" -> "96"
            "U" -> "99"

            //TODO: 203	Línea Exprés Aeropuerto (servicio diurno)

            //lineas cero
            "001" -> "361"
            "002" -> "362"
            "C03" -> "363" //ojo, pone C no 0
            "172" -> "372"
            //401 a 405	Líneas exprés: E1 a E5
            "E1" -> "401"
            "E2" -> "402"
            "E3" -> "403"
            "E4" -> "404"
            "E5" -> "405"
            //T
            "T11" -> "451"
            "T23" -> "452"
            "T32" -> "453"
            "T31" -> "454"
            "T61" -> "455"
            "T41" -> "456"
            "T62" -> "457"
            "H1" -> "481"

            // todo 501 a 528	Líneas nocturnas: N1 a N28, siendo la N27 (=527) la línea Exprés Aeropuerto en su servicio nocturno
            "N1" -> "501"
            "N2" -> "502"
            "N3" -> "503"
            "N4" -> "504"
            "N5" -> "505"
            "N6" -> "506"
            "N7" -> "507"
            "N8" -> "508"
            "N9" -> "509"
            "N10" -> "510"
            "N11" -> "511"
            "N12" -> "512"
            "N13" -> "513"
            "N14" -> "514"
            "N15" -> "515"
            "N16" -> "516"
            "N17" -> "517"
            "N18" -> "518"
            "N19" -> "519"
            "N20" -> "520"
            "N21" -> "521"
            "N22" -> "522"
            "N23" -> "523"
            "N24" -> "524"
            "N25" -> "525"
            "N26" -> "526"
            "N27" -> "527"
            "N28" -> "528"

            //in 501..528 -> codFinal = "N" + (intInicial-500).toString()
            "M1" ->  "601"

            //TODO: 700 a 799	Servicios Especiales, casi siempre denominados simplemente «SE»

            //Si no es ninguno, es que es asi
            else -> codInicial
        }
    }

}