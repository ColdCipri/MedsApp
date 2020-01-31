package com.example.medsapp.dialog

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.medsapp.R
import com.example.medsapp.service.MedService
import com.example.medsapp.service.Model
import com.example.medsapp.service.ServiceFactory
import com.example.medsapp.viewmodel.MedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add.*

class AddDialog : AppCompatActivity() {

    private lateinit var medViewModel: MedViewModel
    private var disposable: Disposable? = null
    private var wifiEnabled: Boolean = true
    private lateinit var wifiM: WifiManager
    private lateinit var wifiBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {

        wifiM = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        class MyBroadcastReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val wifiStateExtra: Int =
                    intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)

                when (wifiStateExtra) {
                    WifiManager.WIFI_STATE_ENABLED -> {
                        wifiEnabled = true;
                    }
                    WifiManager.WIFI_STATE_DISABLED -> wifiEnabled = false
                }
            }
        }

        wifiBroadcastReceiver = MyBroadcastReceiver()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add)

        //val id = intent.getStringExtra("id").toString().toInt()

        medViewModel = ViewModelProviders.of(this)[MedViewModel::class.java]

        button_add.setOnClickListener {
            this.saveMed()
        }

        button_cancel.setOnClickListener {
            super.onBackPressed()
        }

    }

    private val medCodeService by lazy {
        val factory = ServiceFactory.getInstance("http://192.168.1.9:5050", "admin", "admin")
        factory.build(MedService::class.java)
    }

    private fun saveMed() {

        if (add_name.text.isEmpty() or
            add_best_before.text.isEmpty() or
            add_pieces.text.isEmpty() or
            add_base_substance.text.isEmpty() or
            add_base_substance_quantity.text.isEmpty() or
            add_description.text.isEmpty())
        {
            AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("One of the input is empty!")
                .setNegativeButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        } else {
            var name = add_name.text.toString()
            var best_before = add_best_before.text.toString()
            var pieces = add_pieces.text.toString().toInt()
            var base_substance = add_base_substance.text.toString()
            var base_substance_quantity = add_base_substance_quantity.text.toString()
            var description = add_description.text.toString()

            val med = Model.Med(
                1,
                name,
                best_before,
                pieces,
                base_substance,
                base_substance_quantity,
                description,
                "admin"
            )

            this.disposable = this.medCodeService.create(med)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    medViewModel.insert(med)
                })
            }
        }
    }
