package OscarGracia.recyckerview.Dialogs
import OscarGrC.tfccliente.Models.Producto
import OscarGracia.recyckerview.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class DialogAddProducto(private val producto: Producto) : DialogFragment() {
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private val db = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference
    private var fotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Obtén la URI de la imagen seleccionada
            fotoUri = data.data!!
            // Muestra la imagen seleccionada en el ImageView utilizando Glide
            val imageView = dialog!!.findViewById<ImageView>(R.id.producaddimg)
            Glide.with(requireContext())
                .load(fotoUri)
                .placeholder(R.drawable.logo) // Imagen de relleno mientras se carga la imagen real
                .into(imageView)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.dialog_add_producto, container, false)

        val nombreInput = dialogView.findViewById<TextInputEditText>(R.id.nombre)
        val precioInput = dialogView.findViewById<TextInputEditText>(R.id.precio)
        val stockInput = dialogView.findViewById<TextInputEditText>(R.id.stock)
        val unidadStockInput = dialogView.findViewById<TextInputEditText>(R.id.unidadstock)
        val imageView = dialogView.findViewById<ImageView>(R.id.producaddimg)
        val subirFotoButton = dialogView.findViewById<Button>(R.id.subirFoto)
        val guardarButton = dialogView.findViewById<Button>(R.id.button3)
        val eliminarButton = dialogView.findViewById<Button>(R.id.button4)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinner)
        cargarCategoriasEnSpinner(spinner)
        // Set the product data into the input fields
        nombreInput.setText(producto.nombre)
        precioInput.setText(producto.precio.toString())
        stockInput.setText(producto.stock.toString())
        unidadStockInput.setText("0")

        // Load the product photo if available
        producto.foto?.let { foto ->
            Glide.with(requireContext())
                .load(foto)
                .placeholder(R.drawable.logo) // Placeholder image while loading the actual image
                .into(imageView)
        }

        subirFotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
            Glide.with(requireContext())
                .load(fotoUri)
                .placeholder(R.drawable.logo) // Placeholder image while loading the actual image
                .into(imageView)
        }

        guardarButton.setOnClickListener {
            val nombre = nombreInput.text.toString()
            val precio = precioInput.text.toString().toIntOrNull() ?: 0
            val stock = stockInput.text.toString().toIntOrNull() ?: 0
            val unidadStock = unidadStockInput.text.toString().toIntOrNull() ?: 0
            val categoriaSeleccionada = spinner.selectedItem.toString()
            var foto = producto.foto
            var id = producto.id
            if (id.isEmpty()) {
                id = UUID.randomUUID().toString()
            }
            if (fotoUri == null) {
                guardarProductoEnFirestore(nombre, precio, stock, unidadStock, foto, categoriaSeleccionada)
            }
            val fotoRef = storageReference.child("Productos/${toProductoName(nombre)}.jpg")
            fotoUri?.let { uri ->
                fotoRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        fotoRef.downloadUrl.addOnSuccessListener { uri ->
                            val fotoUrl = uri.toString()

                            if(producto.nombre != ""){
                                db.collection("Productos").document(toProductoName(producto.nombre)).delete()
                            }
                            guardarProductoEnFirestore(nombre, precio, stock, unidadStock, fotoUrl, categoriaSeleccionada)
                        }
                    }
            }
        }

        eliminarButton.setOnClickListener {
            if(producto.nombre != ""){
                db.collection("Productos").document(toProductoName(producto.nombre)).delete()
            }
            dismiss()
        }

        return dialogView
    }


    private fun toProductoName(nombre: String): String {
        val nombresplit = nombre.split(" ")
        if (nombresplit.size == 1) {
            return nombre.toLowerCase()
        }
        return nombresplit[0].toLowerCase() + nombresplit.drop(1).joinToString("") { it.capitalize() }
    }

    private fun guardarProductoEnFirestore(nombre: String, precio: Int, stock: Int, unidadStock: Int, fotoUrl: String, categoria: String) {
        // Guardar el producto en Firestore
        val productoData = hashMapOf(
            "nombre" to nombre,
            "precio" to precio,
            "stock" to stock,
            "unidadStock" to unidadStock,
            "fotoId" to fotoUrl,
            "categoria" to categoria
        )

        db.collection("Productos")
            .document(toProductoName(nombre))
            .set(productoData)

        dismiss()
    }
    private fun cargarCategoriasEnSpinner(spinner: Spinner) {
        val categorias = mutableListOf<String>()

        db.collection("CategoriasProductos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val categoria = document.getString("nombre").orEmpty()
                    categorias.add(categoria)
                }

                val adapter =
                    context?.let {
                        ArrayAdapter(
                            it,
                            android.R.layout.simple_spinner_item,
                            categorias
                        )
                    }
                if (adapter != null) {
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                spinner.adapter = adapter
                    if (producto.categoria.isNotBlank()) {
                        for ((index, cat) in categorias.withIndex()) {
                            if (cat == producto.categoria) {
                                spinner.setSelection(index)
                                break
                            }
                        }
                    }

            }
    }
}
