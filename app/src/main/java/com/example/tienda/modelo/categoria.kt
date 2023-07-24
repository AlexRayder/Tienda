package com.example.tienda.modelo

class categoria constructor(id: Int, nombre: String){
    var nombre = nombre
    var id = id
    override fun toString(): String {
        return nombre
    }
}