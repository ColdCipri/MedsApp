package com.example.medsapp.medAdapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medsapp.MainActivity
import com.example.medsapp.R
import com.example.medsapp.dialog.EditDialog
import com.example.medsapp.service.Model;
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_layout.view.*

class MedListAdapter (private val context: MainActivity, private var index: Int = -1):
    RecyclerView.Adapter<MedListAdapter.MedViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var meds = emptyList<Model.Med>() // Cached copy of meds

    inner class MedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val med_id : TextView = itemView.findViewById(R.id.id_textview)
        val med_name: TextView = itemView.findViewById(R.id.name_textview)
        val med_best_before: TextView = itemView.findViewById(R.id.best_before_textview)
        val med_pieces: TextView = itemView.findViewById(R.id.pieces_textview)
        val med_base_substance: TextView = itemView.findViewById(R.id.base_substance_textview)
        val med_base_substance_quantity: TextView = itemView.findViewById(R.id.quantity_base_substance_textview)
        val med_description: TextView = itemView.findViewById(R.id.description_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedViewHolder {
        val itemView = inflater.inflate(R.layout.list_layout, parent, false)
        return MedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedViewHolder, position: Int) {
        val current = meds[position]
        holder.med_id.text = current.id.toString()
        holder.med_name.text = current.name
        holder.med_best_before.text = current.best_before
        holder.med_pieces.text = current.pieces.toString()
        holder.med_base_substance.text = current.base_substance
        holder.med_base_substance_quantity.text = current.base_substance_quantity
        holder.med_description.text = current.description


        /*if (current.contributor == context.currentUser)
            holder.itemView.setBackgroundColor(Color.parseColor("#03fc0b"))*/ //??

        holder.itemView.setOnClickListener {
            context.id_edittext.setText(current.id)
            context.name_edittext.setText(current.name)
            context.best_before_edittext.setText(current.best_before)
            context.pieces_edittext.setText(current.pieces)
            context.base_substance_edittext.setText(current.base_substance)
            context.base_substance_quantity_edittext.setText(current.base_substance_quantity)
            context.description_edittext.setText(current.description)

            index = position
            notifyDataSetChanged()

        }

        holder.itemView.update_button.setOnClickListener {
            val id = meds[position]?.id
            val intent = Intent(it.context, EditDialog::class.java).apply{
                putExtra("id", id)
            }
            it.context.startActivity(intent)
        }

        if (index == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#BDBDBD"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    internal fun setMeds(meds: List<Model.Med>) {
        this.meds = meds
        notifyDataSetChanged()
    }

    override fun getItemCount() = meds.size
}