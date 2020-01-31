package com.example.medsapp.dialog

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.medsapp.R
import com.example.medsapp.service.Model
import com.example.medsapp.viewmodel.MedViewModel
import kotlinx.android.synthetic.main.fragment_update.*

class EditDialog : AppCompatActivity(){

    private lateinit var medViewModel: MedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_update)

        medViewModel = ViewModelProviders.of(this)[MedViewModel::class.java]

        val id = intent.getStringExtra("id").toString().toInt()

        init(id)

        button_edit.setOnClickListener{
            this.saveMed(id)
        }

        button_cancel.setOnClickListener{
            super.onBackPressed()
        }
    }

    private fun saveMed(id: Int) {
        val med = medViewModel.getMed(id)
        if(med != null){
            if (edit_name.text.isEmpty() or
                edit_best_before.text.isEmpty() or
                edit_pieces.text.isEmpty() or
                edit_base_substance.text.isEmpty() or
                edit_base_substance_quantity.text.isEmpty() or
                edit_description.text.isEmpty())
            {
                AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("One of the input is empty!")
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()

            } else {//to-do. If not connected to wifi then not add.
                    var name = edit_name.text.toString()
                    var best_before = edit_best_before.text.toString()
                    var pieces = edit_pieces.text.toString().toInt()
                    var base_substance = edit_base_substance.text.toString()
                    var base_substance_quantity = edit_base_substance_quantity.text.toString()
                    var description = edit_description.text.toString()

                medViewModel.update(Model.Med(
                    3,
                    name,
                    best_before,
                    pieces,
                    base_substance,
                    base_substance_quantity,
                    description,
                    med.userEmail
                ))
                }
            }
        }


    fun init(id: Int) {
        val med = medViewModel.getMed(id)

        Log.d("EDIT PAGE", med.toString())

        if (med != null) {
            edit_name.text = Editable.Factory.getInstance().newEditable(med.name)
            edit_best_before.text = Editable.Factory.getInstance().newEditable(med.exp_date)
            edit_pieces.text = Editable.Factory.getInstance().newEditable(med.pieces.toString())
            edit_base_substance.text = Editable.Factory.getInstance().newEditable(med.base_subst)
            edit_base_substance_quantity.text = Editable.Factory.getInstance().newEditable(med.quantity)
            edit_description.text = Editable.Factory.getInstance().newEditable(med.description)
        }
    }
}