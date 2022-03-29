package com.example.proyectoemtaja.models

import android.media.audiofx.AudioEffect

data class IncidentData(
    val title: String,
    val guid: String,
    val description: String,
    val pubDate: String,
    val rssFrom: String,
    val rssTo: String,
    val cause: String,
    val effect: String,
    val moreInfo: MoreInfo
) {


}
