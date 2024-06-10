package OscarGracia.recyckerview.Dialogs

import OscarGracia.recyckerview.Models.Data
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
import java.util.UUID




class DialogAddCategoria(private val categoria: Data) : DialogFragment() {
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
            val imageView = dialog!!.findViewById<ImageView>(R.id.producaddimg2)
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
        val dialogView = inflater.inflate(R.layout.dialog_add_categoria, container, false)

        val nombreInput = dialogView.findViewById<TextInputEditText>(R.id.nombre2)
        val imageView = dialogView.findViewById<ImageView>(R.id.producaddimg2)
        val subirFotoButton = dialogView.findViewById<Button>(R.id.subirFoto2)
        val guardarButton = dialogView.findViewById<Button>(R.id.button31)
        val eliminarButton = dialogView.findViewById<Button>(R.id.button41)

        nombreInput.setText(categoria.nombre)


        // Load the product photo if available
        categoria.imagen?.let { foto ->
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
            if(categoria.nombre != nombreInput.text.toString()){
                db.collection("CategoriasProductos").document(categoria.nombre).delete()
            }
            var foto = categoria.imagen
            if (fotoUri == null) {
                guardarProductoEnFirestore(nombre, foto)
            }
            val fotoRef = storageReference.child("Categorias/${toProductoName(nombre)}.jpg")
            fotoUri?.let { uri ->
                fotoRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        fotoRef.downloadUrl.addOnSuccessListener { uri ->
                            val fotoUrl = uri.toString()

                            if(categoria.nombre != ""){
                                db.collection("CategoriasProductos").document(toProductoName(categoria.nombre)).delete()
                            }
                            guardarProductoEnFirestore(nombre,fotoUrl)
                        }
                    }
            }
        }

        eliminarButton.setOnClickListener {
            if(categoria.nombre != ""){
                db.collection("CategoriasProductos").document(toProductoName(categoria.nombre)).delete()
            }
            dismiss()
        }

        return dialogView
    }

    private fun toProductoName(nombre: String): String {
        val nombresplit = nombre.split(" ")

        return  nombresplit.joinToString("") { it.capitalize() }
    }

    private fun guardarProductoEnFirestore(nombre: String, fotoUrl: String) {
        // Guardar el producto en Firestore
        val productoData = hashMapOf(
            "nombre" to nombre,
            "fotoId" to fotoUrl
        )

        db.collection("CategoriasProductos")
            .document(toProductoName(nombre))
            .set(productoData)

        dismiss()
    }


}
