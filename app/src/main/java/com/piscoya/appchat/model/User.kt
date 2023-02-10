package com.piscoya.appchat.model

class User {
    var id: String? = null
    var nombre: String? = null
    var numero: String? = null
    var imgPerfil: String? = null

    constructor() {}

    constructor(id: String, nombre: String, numero: String, imgPerfil: String) {
        this.id = id
        this.nombre = nombre
        this.numero = numero
        this.imgPerfil = imgPerfil
    }
}