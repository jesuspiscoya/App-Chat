package com.piscoya.appchat

class User {
    lateinit var id: String
    lateinit var nombre: String
    lateinit var numero: String
    lateinit var imgPerfil: String

    constructor() {}

    constructor(id: String, nombre: String, numero: String, imgPerfil: String) {
        this.id = id
        this.nombre = nombre
        this.numero = numero
        this.imgPerfil = imgPerfil
    }
}