package OscarGracia.recyckerview.Adapters

import OscarGracia.recyckerview.ElementoOnClick
import OscarGracia.recyckerview.Models.Data
import OscarGracia.recyckerview.R
import OscarGracia.recyckerview.databinding.ItemDatoBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DatoAdapter (private val listDato:List<Data>
//aqui metemos el listener del interfaz para el click en elementos
,private var listener: ElementoOnClick
):RecyclerView.Adapter<DatoAdapter.ViewHolder>(){

    //clase interna para decir como se visualiza
    inner class ViewHolder (view:View):RecyclerView.ViewHolder(view){
    //aqui unimos
        val binding = ItemDatoBinding.bind(view)
        fun bind(dato: Data){
            binding.textView.text = dato.nombre
            Glide.with(binding.imageView).load(dato.imagen)
                                         .into(binding.imageView)
        }
        // ejecutamos la escucha  en la clase interna
        fun setListener(dato: Data){
            //aqui definimos si es solo la imagen imagenView el texto etc...
          //  binding.textView.setOnClickListener { listener.onClickElemento(dato) }
            binding.root.setOnClickListener{listener.onClickElemento(dato)}
        }
    }

    //infla layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_dato,parent,false)
        return ViewHolder(view)

        }
    // cuenta numero elementos
    override fun getItemCount(): Int {
        return listDato.count()
    }
    //muestra en holder dato
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = listDato[position]
    holder.bind(item)
        //añadir
    holder.setListener(item)
    }




}