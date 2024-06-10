package OscarGracia.recyckerview.Activities

import OscarGracia.recyckerview.Adapters.CategoriaAdapter
import OscarGracia.recyckerview.Dialogs.DialogAddCategoria
import OscarGracia.recyckerview.Models.Data
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import OscarGracia.recyckerview.databinding.ActivityCategoriaBinding
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

class CategoriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriaBinding
    private lateinit var mAdapter: CategoriaAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var listaCategorias: MutableList<Data>
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()

        binding = ActivityCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()

        binding.addCategoria.setOnClickListener {
           val dialog = DialogAddCategoria(Data("",""))
            dialog.show(supportFragmentManager, "DialogAddCategoria")
        }
    }

    private fun setRecyclerView() {
        mAdapter = CategoriaAdapter(listaCategorias) { categoria ->

            val dialog = DialogAddCategoria(Data(categoria.nombre,categoria.imagen))
            dialog.show(supportFragmentManager, "DialogAddCategoria")
        }
        mLayoutManager = LinearLayoutManager(this)

        binding.listaLayout2.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun getData() {
        listaCategorias = mutableListOf()
        db.collection("CategoriasProductos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val categoria = Data(
                        document.getString("nombre").orEmpty(),
                        document.getString("fotoId").orEmpty()
                    )
                    listaCategorias.add(categoria)
                }
                mAdapter.notifyDataSetChanged()
            }
    }
}
