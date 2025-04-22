package dk.itu.moapd.copenhagenbuzz.msem.ViewModel


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FavoriteRowItemBinding



class FavoriteEventAdapter (private var data: List<Event>) :
    RecyclerView.Adapter<FavoriteEventAdapter.ViewHolder>() {

    class ViewHolder(private val binding: FavoriteRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.favoriteEventName.text = event.eventName
            binding.favoriteEventType.text = event.eventType

            binding.FavoriteEventImage.setImageResource(R.drawable.nyhavn)


        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoriteRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<Event>) {
        this.data = newData
        notifyDataSetChanged()
    }
}