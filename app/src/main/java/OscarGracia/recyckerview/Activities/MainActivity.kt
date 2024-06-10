package OscarGracia.recyckerview.Activities

import OscarGracia.recyckerview.Adapters.DatoAdapter
import OscarGracia.recyckerview.Models.Data
import OscarGracia.recyckerview.ElementoOnClick
import OscarGracia.recyckerview.databinding.ActivityMainBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity(), ElementoOnClick {
    //variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: DatoAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private val listaDatos:MutableList<Data> = getData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //zona de bindings
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // crear funcion para el recycleView
        setRecyclerView()
    }

    fun setRecyclerView(){
        mAdapter = DatoAdapter(listaDatos,this)
        mLayoutManager = LinearLayoutManager(this)
        //vincular
        binding.listaLayout.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
    }
//implementar el click en elemento
    override fun onClickElemento(elementoData: Data) {
        when(elementoData.nombre){
            "Productos"->    startActivity(Intent(this, ProductosActivity::class.java))
            "Categorias"->   startActivity(Intent(this, CategoriaActivity::class.java))
     //       "Ingredientes"-> startActivity(Intent(this, ProductosActivity::class.java))
            "LeerQr"->       startActivity(Intent(this, QrActivity::class.java))
        }
     }

   private fun getData():MutableList<Data>{
       var out:MutableList<Data> = mutableListOf()
       out.add(Data("Productos","https://firebasestorage.googleapis.com/v0/b/tfccliente-e7029.appspot.com/o/Controll%2Fagregar-producto.png?alt=media&token=5200195d-d271-49ef-b22e-b1fb7143e4ca"))
       out.add(Data("Categorias","https://firebasestorage.googleapis.com/v0/b/tfccliente-e7029.appspot.com/o/Controll%2Fcategorias.png?alt=media&token=42f02d39-836b-4cd9-9b4a-d779b511173e"))
   //    out.add(Data("Ingredientes","https://firebasestorage.googleapis.com/v0/b/tfccliente-e7029.appspot.com/o/Controll%2Fingredientes.png?alt=media&token=dd6c291f-26d7-4430-a968-cd927d84ae20"))
       out.add(Data("LeerQr","https://firebasestorage.googleapis.com/v0/b/tfccliente-e7029.appspot.com/o/Controll%2Fcamara-reflex-digital.png?alt=media&token=6bb525fd-45d3-4374-8cdb-940ba788ea4f"))
       return  out
   }
}

