package com.example.medsapp.medAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medsapp.MainActivity
import com.example.medsapp.R
import com.example.medsapp.service.Model;
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
        holder.med_best_before.text = current.exp_date
        holder.med_pieces.text = current.pieces.toString()
        holder.med_base_substance.text = current.base_subst
        holder.med_base_substance_quantity.text = current.quantity
        holder.med_description.text = current.description

        holder.itemView.update_button_list.setOnClickListener {
            index = position

            context.tool_title.text = "Update tool"
            context.update_button.text = "Update"
            context.updateTool_layout.visibility = View.VISIBLE
            context.meds_recyclerView.visibility = View.GONE

            context.fab.visibility = View.GONE
            context.remove_button.visibility = View.VISIBLE

            context.id_edittext.setText(current.id.toString())
            context.name_edittext.setText(current.name)
            context.best_before_edittext.setText(current.exp_date)
            context.pieces_edittext.setText(current.pieces.toString())
            context.base_substance_edittext.setText(current.base_subst)
            context.base_substance_quantity_edittext.setText(current.quantity)
            context.description_edittext.setText(current.description)
            context.user_email_edittext.setText(current.userEmail)
        }
    }

    internal fun setMeds(meds: List<Model.Med>) {
        this.meds = meds
        notifyDataSetChanged()
    }

    override fun getItemCount() = meds.size
}