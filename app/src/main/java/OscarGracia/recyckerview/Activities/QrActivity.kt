package OscarGracia.recyckerview.Activities

import OscarGrC.tfccliente.Models.Pedido
import OscarGracia.recyckerview.Adapters.DetalleAdapter
import OscarGracia.recyckerview.Models.LineaDetalle
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import OscarGracia.recyckerview.databinding.ActivityQrBinding
import android.content.Intent
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding
    private lateinit var pedido: Pedido
    private var cargado:Boolean= false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.leerqr.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }

        binding.button5.setOnClickListener {
            if (cargado) {
                actualizarEstadoPedido("pagado")
            }
        }

        binding.button6.setOnClickListener {
            if (cargado) {
                actualizarEstadoPedido("entregado")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            } else {
                // El contenido escaneado es el ID del pedido
                val pedidoId = result.contents.split("/")[1].split(",")[0]
                cargarPedido(pedidoId)
                cargado= true
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun cargarPedido(pedidoId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Pedido").document(pedidoId).get()
            .addOnSuccessListener { ped ->
                pedido = Pedido(
                    carrito = (ped.get("carrito") as? List<String>) ?: listOf(),
                    horaPedido = ped.get("horaPedido").toString(),
                    horaRecepcion = ped.get("horaEntrega").toString(),
                    fecha = ped.get("fecha").toString(),
                    metodoDepago = ped.get("metodoPago").toString(),
                    numeroPedido = pedidoId,
                    precio = ped.get("precio").toString(),
                    IsPagado = ped.getBoolean("pagado") ?:false,
                    IsEntregado = ped.getBoolean("entregado") ?:false
                )

                // Actualizar la UI con los datos del pedido
                binding.FPedido.text = "Fecha Pedido: " + pedido.fecha
                binding.FEntrega.text = "Hora Entrega: " + pedido.horaRecepcion
                binding.Hora.text = "Hora Pedido: " + pedido.horaPedido
                binding.NumPedido.text = "NºPedido: " + pedido.numeroPedido.split("-")[0]
                binding.MetodoPago.text = "M.Pago " + pedido.metodoDepago
                binding.Entregado.text = if (pedido.IsPagado) "Pagado: SI" else "Pagado: NO"
                binding.textView12.text =if (pedido.IsEntregado) "Entregado: SI" else "Entregado: NO"
                // Configurar el adaptador para la lista de productos
                val lineasCarritoDetalle: MutableList<LineaDetalle> = mutableListOf()
                val adapter = DetalleAdapter(this, lineasCarritoDetalle)
                binding.listCarrito.adapter = adapter

                pedido.carrito.forEach { linea ->
                    val nombre = linea.split("$$$")[0]
                    val cantidad = linea.split("$$$")[1]
                    val nombreConsulta = dbName(nombre)

                    db.collection("Productos").document(nombreConsulta).get().addOnSuccessListener {
                        val newLinea = LineaDetalle(nombre, cantidad, it.getString("fotoId").toString())
                        lineasCarritoDetalle.add(newLinea)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun actualizarEstadoPedido(estado: String) {
        val db = FirebaseFirestore.getInstance()
        val pedidoRef = db.collection("Pedido").document(pedido.numeroPedido)
         when (estado) {
            "pagado" ->  pedidoRef.update(mapOf("pagado" to true))
            "entregado" -> pedidoRef.update(mapOf("entregado" to true))
            else -> return
        }
        cargarPedido(pedido.numeroPedido)  // Actualizar UI después de cambiar el estado

    }

    private fun dbName(nombre: String): String {
        val lista = nombre.split(" ")
        return if (lista.size == 1) {
            lista[0]
        } else {
            lista[0].lowercase() + lista.drop(1).joinToString("")
        }
    }
}

