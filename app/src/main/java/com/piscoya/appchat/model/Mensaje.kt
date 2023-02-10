package com.piscoya.appchat.model

class Mensaje {
    var id: String? = null
    var mensaje: String? = null
    var senderId: String? = null
    var imagenUrl: String? = null
    var tiempo: String? = null

    constructor()

    constructor(
        mensaje: String?,
        senderId: String?,
        tiempo: String?
    ) {
        this.mensaje = mensaje
        this.senderId = senderId
        this.tiempo = tiempo
    }


}