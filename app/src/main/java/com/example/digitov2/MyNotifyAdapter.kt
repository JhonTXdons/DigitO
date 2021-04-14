package com.example.digitov2

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
class MyNotifyAdapter(
    options: FirebaseRecyclerOptions<Notify>,
    private var listener: onItemClickListener
) : FirebaseRecyclerAdapter<Notify, MyNotifyAdapter.NotifyViewHolder>(options) {
    override fun onBindViewHolder(
        holder: NotifyViewHolder,
        position: Int, model: Notify
    ) {
        holder.title.text = model.title
        holder.description.text = model.description
        holder.date.text = model.date
        when (getProperty(model)) {
           // "E" -> holder.priority.setBackgroundColor(Color.parseColor("#00FF00"))
            "E" -> holder.priority.setImageResource(R.drawable.ic_baseline_add_circle_outline_24)
            "S" -> holder.priority.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24)
            else -> holder.priority.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
        }
        holder.time.text = model.time
        model.id = getRef(position).key
        holder.id.text = model.id

    }

    fun getProperty(model: Notify): String? {
        return model.priority
    }


    override fun getItemCount(): Int {
        Log.d("NB", "getItemCount: " + super.getItemCount());
        return super.getItemCount();
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotifyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.singleitem, parent, false)
        return NotifyViewHolder(view)
    }


    inner class NotifyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var title: TextView = itemView.findViewById(R.id.textTitle)
        var description: TextView = itemView.findViewById(R.id.textDesc)
        var date: TextView = itemView.findViewById(R.id.textDate)
        var time: TextView = itemView.findViewById(R.id.textTime)
        var priority: ImageView = itemView.findViewById(R.id.imageView2)
        var id: TextView = itemView.findViewById(R.id.textId)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }

        }
    }

    interface onItemClickListener {
        fun onItemClick(position: Int);
    }

}