package OscarGracia.recyckerview.Activities

import OscarGrC.tfccliente.Models.Producto
import OscarGracia.recyckerview.Adapters.ProductoAdapter
import OscarGracia.recyckerview.Dialogs.DialogAddProducto
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import OscarGracia.recyckerview.databinding.ActivityProductosBinding
import com.google.firebase.firestore.FirebaseFirestore


class ProductosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductosBinding
    private lateinit var adapterProductos: ProductoAdapter
    private var listaDeProductos: MutableList<Producto> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura el ListView
        adapterProductos = ProductoAdapter(this, listaDeProductos)
        binding.productos.adapter = adapterProductos

        // Carga los productos desde Firestore
        cargarProductos()

        binding.addProducto.setOnClickListener {
            val dialogo = DialogAddProducto(Producto("","","",0,0))
            dialogo.show(supportFragmentManager, "DialogAddProducto")
            listaDeProductos = mutableListOf()
            cargarProductos()
        }


    }

    private fun cargarProductos() {
        db.collection("Productos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val producto = Producto(
                        document.getString("nombre").orEmpty(),
                        document.getString("id").orEmpty(),
                        document.getString("fotoId").orEmpty(),
                        document.getLong("precio")?.toInt() ?: 0,
                        document.getLong("stock")?.toInt() ?: 0
                    )
                    producto.categoria = document.getString("categoria").orEmpty()
                    listaDeProductos.add(producto)
                }
                // Notifica al adaptador que los datos han cambiado
                adapterProductos.notifyDataSetChanged()
            }
    }
}
