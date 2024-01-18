package com.example.proyecto_contactos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.proyecto_contactos.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UsuarioAdapter.OnItemClicked {

    lateinit var binding: ActivityMainBinding

    lateinit var adatador: UsuarioAdapter

    var listaUsuarios = arrayListOf<Usuario>()

    var usuario = Usuario(-1,"","","","")

    var isEditando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvContactos.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

        obtenerUsuarios()

        binding.btnAddUpdate.setOnClickListener {
            validarCampos()

            if (binding.etNombre.error == null &&
                binding.etApellidos.error == null &&
                binding.etEmail.error == null &&
                binding.etTelefono.error == null
            ) {
                if (!isEditando) {
                    agregarUsuario()
                } else {
                    actualizarUsuario()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            limpiarCampos()
        }
    }

    fun validarCampos() {
        if (binding.etNombre.text.isNullOrEmpty()) {
            binding.etNombre.error = "Ingrese Nombre"
        }
        if (binding.etApellidos.text.isNullOrEmpty()) {
            binding.etApellidos.error = "Ingrese Apellidos"
        }
        if (binding.etEmail.text.isNullOrEmpty()) {
            binding.etEmail.error = "Ingrese Correo"
        }
        if (binding.etTelefono.text.isNullOrEmpty()) {
            binding.etTelefono.error = "Ingrese Teléfono"
        }
    }


    fun obtenerUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerContactos()
            runOnUiThread {
                if (call.isSuccessful) {
                    listaUsuarios = call.body()!!.listaUsuarios
                    setupRecyclerView()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR CONSULTAR TODOS", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun agregarUsuario() {

        this.usuario.idUsuario = -1
        this.usuario.nombre = binding.etNombre.text.toString()
        this.usuario.apellidos = binding.etApellidos.text.toString()
        this.usuario.email = binding.etEmail.text.toString()
        this.usuario.telefono = binding.etTelefono.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.agregarUsuario(usuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()

                } else {
                    Toast.makeText(this@MainActivity, "ERROR ADD", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun actualizarUsuario() {

        this.usuario.nombre = binding.etNombre.text.toString()
        this.usuario.apellidos = binding.etApellidos.text.toString()
        this.usuario.email = binding.etEmail.text.toString()
        this.usuario.telefono = binding.etTelefono.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.actualizarUsuario(usuario.idUsuario, usuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()

                    binding.btnAddUpdate.setText("Listo") //Para agregar otro contacto una vez registrado
                    binding.btnAddUpdate.backgroundTintList = resources.getColorStateList(R.color.green)
                    isEditando = false
                }
            }
        }
    }

    fun limpiarCampos() {
        binding.etNombre.setText("")
        binding.etApellidos.setText("")
        binding.etEmail.setText("")
        binding.etTelefono.setText("")
    }

    fun limpiarObjeto() {
        this.usuario.idUsuario = -1
        this.usuario.nombre = ""
        this.usuario.apellidos = ""
        this.usuario.email = ""
        this.usuario.telefono = ""
    }

    fun setupRecyclerView() {
        adatador = UsuarioAdapter(this, listaUsuarios)
        adatador.setOnClick(this@MainActivity)
        binding.rvContactos.adapter = adatador

    }

    override fun editarUsuario(usuario: Usuario) {
        binding.etNombre.setText(usuario.nombre)
        binding.etApellidos.setText(usuario.apellidos)
        binding.etEmail.setText(usuario.email)
        binding.etTelefono.setText(usuario.telefono)
        binding.btnAddUpdate.setText("Listo")//Cuando se quiere registrar contacto
        binding.btnAddUpdate.backgroundTintList = resources.getColorStateList(R.color.green)
        this.usuario = usuario
        isEditando = true
    }

    override fun borrarUsuario(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.borrarUsuario(idUsuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                }
            }
        }
    }
}