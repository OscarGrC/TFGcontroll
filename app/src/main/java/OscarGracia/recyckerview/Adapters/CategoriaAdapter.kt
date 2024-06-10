package OscarGracia.recyckerview.Adapters

import OscarGracia.recyckerview.Models.Data
import OscarGracia.recyckerview.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class CategoriaAdapter(private val categorias: List<Data>, private val onItemClick: (Data) -> Unit) :
    RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dato, parent, false)
        return CategoriaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val currentItem = categorias[position]
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        // Cargar la imagen
        Glide.with(holder.itemView.context)
            .load(currentItem.imagen)
            .apply(requestOptions)
            .into(holder.imageView)

        holder.textView.text = currentItem.nombre

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount() = categorias.size

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}
