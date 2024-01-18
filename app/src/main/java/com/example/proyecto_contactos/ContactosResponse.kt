package com.example.proyecto_contactos

import com.google.gson.annotations.SerializedName

data class ContactosResponse (
    @SerializedName("listaUsuarios") var listaUsuarios: ArrayList<Usuario>
)