package com.example.medsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medsapp.medAdapter.MedListAdapter
import com.example.medsapp.service.MedService
import com.example.medsapp.service.Model
import com.example.medsapp.service.ServiceFactory
import com.example.medsapp.viewmodel.MedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_update.*
import java.lang.Exception
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private var noWifiMedBuffer: MutableList<Model.Med> = mutableListOf()
    private lateinit var medViewModel: MedViewModel
    private var disposable: Disposable? = null
    private lateinit var adapter: MedListAdapter
    private lateinit var wifiM: WifiManager
    private lateinit var wifiBroadcastReceiver: BroadcastReceiver
    private var medsHaveBeenReadFromServer: Boolean = false

    private var wifiEnabled: Boolean = true
    var currentUser: String? = ""


    private val medCodeService by lazy {
        val factory = ServiceFactory.getInstance("http://192.168.1.9:5050", "admin", "admin")
        //val factory = ServiceFactory.getInstance("http://172.30.114.204:5050", "admin", "admin")
        factory.build(MedService::class.java)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(wifiBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(wifiBroadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        /*super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_meds, R.id.nav_settings,
                R.id.nav_contact
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/



        wifiM = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        class MyBroadcastReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val wifiStateExtra: Int =
                    intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)

                when (wifiStateExtra) {
                    WifiManager.WIFI_STATE_ENABLED -> {
                        wifiEnabled = true; onWifiEnable()
                    }
                    WifiManager.WIFI_STATE_DISABLED -> wifiEnabled = false
                }
            }
        }

        wifiBroadcastReceiver = MyBroadcastReceiver()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)


        adapter = MedListAdapter(this)
        val recyclerView = findViewById<RecyclerView>(R.id.meds_recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        medViewModel = ViewModelProviders.of(this)[MedViewModel::class.java]

        currentUser = intent.getStringExtra("EXTRA_CURRENT_USER")?.toString()
        medViewModel.allMeds.observe(this, Observer { meds ->
            // Update the cached copy of the words in the adapter.
            meds?.let { adapter.setMeds(it) }
        })

        readAllMedsFromServer()

        fab.setOnClickListener {
            this.updateTool_layout.visibility = View.VISIBLE
            this.tool_title.text = "Add tool"
            this.update_button.text = "Add"

            this.meds_recyclerView.visibility = View.GONE
            this.fab.visibility = View.GONE
            this.remove_button.visibility = View.GONE

            this.name_edittext.text.clear()
            this.best_before_edittext.text.clear()
            this.pieces_edittext.text .clear()
            this.base_substance_edittext.text.clear()
            this.base_substance_quantity_edittext.text.clear()
            this.description_edittext.text.clear()

            this.id_edittext.text.clear()
            this.user_email_edittext.text.clear()
        }

        update_button.setOnClickListener{
            if (this.update_button.text == "Add"){
                if (name_edittext.text.isEmpty() or
                    best_before_edittext.text.isEmpty() or
                    pieces_edittext.text.isEmpty() or
                    base_substance_edittext.text.isEmpty() or
                    base_substance_quantity_edittext.text.isEmpty() or
                    description_edittext.text.isEmpty()) {
                    AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("One of the input is empty!")
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                        this.remove_button.visibility = View.GONE
                } else {
                    val rand = Random.nextInt(0,100)
                    val med = Model.Med(
                        rand,
                        this.name_edittext.text.toString(),
                        this.best_before_edittext.text.toString(),
                        this.pieces_edittext.text.toString().toInt(),
                        this.base_substance_edittext.text.toString(),
                        this.base_substance_quantity_edittext.text.toString(),
                        this.description_edittext.text.toString(),
                        this.currentUser.toString()
                    )
                    if (med.userEmail == currentUser){
                        if (wifiEnabled) {
                            this.disposable = this.medCodeService.create(med)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    showResult("Successfully created the new med ${med.name}")
                                    medViewModel.insert(med)
                                    adapter.notifyDataSetChanged()
                                },
                                    { showResult("Failed to create the new med!")})
                        } else {
                            showResult("Wifi is off. Med will be pushed to server when connection is established.")
                            noWifiMedBuffer.add(med)
                            medViewModel.insert(med)
                            adapter.notifyDataSetChanged()
                        }
                        this.updateTool_layout.visibility = View.GONE
                        this.meds_recyclerView.visibility = View.VISIBLE
                        this.fab.visibility = View.VISIBLE
                        this.remove_button.visibility = View.VISIBLE
                    }
                }
            }
            else
            {
                if (name_edittext.text.isEmpty() or
                    best_before_edittext.text.isEmpty() or
                    pieces_edittext.text.isEmpty() or
                    base_substance_edittext.text.isEmpty() or
                    base_substance_quantity_edittext.text.isEmpty() or
                    description_edittext.text.isEmpty()) {
                        AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("One of the input is empty!")
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show()
                } else {
                    var id = this.id_edittext.text.toString().toInt()
                    var name = this.name_edittext.text.toString()
                    var best_before = this.best_before_edittext.text.toString()
                    var pieces = this.pieces_edittext.text.toString().toInt()
                    var base_substance = this.base_substance_edittext.text.toString()
                    var base_susbtance_quantity = this.base_substance_quantity_edittext.text.toString()
                    var description = this.description_edittext.text.toString()
                    var user_email = this.user_email_edittext.text.toString()
                    val med = Model.Med(
                        id,
                        name,
                        best_before,
                        pieces,
                        base_substance,
                        base_susbtance_quantity,
                        description,
                        user_email
                    )
                    if (med.userEmail == currentUser){
                        if (wifiEnabled) {
                            this.disposable = this.medCodeService.update(id, med)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    showResult("Successfully updated the med ${med.name}")
                                    medViewModel.update(med)
                                    adapter.notifyDataSetChanged()
                                },
                                    { showResult("Failed to update the med!")})
                        } else showResult("Cannot connect to server. Cannot update until a server connection is established.")
                        this.updateTool_layout.visibility = View.GONE
                        this.meds_recyclerView.visibility = View.VISIBLE
                        this.fab.visibility = View.VISIBLE
                        this.remove_button.visibility = View.VISIBLE
                    }
                    else {
                        showResult("Can only delete meds created by you.")
                        this.updateTool_layout.visibility = View.GONE
                        this.meds_recyclerView.visibility = View.VISIBLE
                        this.fab.visibility = View.VISIBLE
                        this.remove_button.visibility = View.VISIBLE
                    }

                }
            }
        }

        remove_button.setOnClickListener{
            var id = this.id_edittext.text.toString().toInt()
            var name = this.name_edittext.text.toString()
            var best_before = this.best_before_edittext.text.toString()
            var pieces = this.pieces_edittext.text.toString().toInt()
            var base_substance = this.base_substance_edittext.text.toString()
            var base_susbtance_quantity = this.base_substance_quantity_edittext.text.toString()
            var description = this.description_edittext.text.toString()
            var user_email = this.user_email_edittext.text.toString()
            val med = Model.Med(
                id,
                name,
                best_before,
                pieces,
                base_substance,
                base_susbtance_quantity,
                description,
                user_email
            )
            if (med.userEmail== currentUser)
                if (wifiEnabled)
                    this.disposable = this.medCodeService.delete(med.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                showResult("Successfully deleted the med ${med.name}")
                                medViewModel.delete(med)
                                adapter.notifyDataSetChanged()
                            },
                            { showResult("Failed to delete the med!") })
                else showResult("Cannot connect to server. Cannot delete until a server connection is established.")
            else showResult("Can only delete meds created by you.")
            this.updateTool_layout.visibility = View.GONE
            this.meds_recyclerView.visibility = View.VISIBLE
            this.fab.visibility = View.VISIBLE
            this.remove_button.visibility = View.VISIBLE
        }

        cancel_button.setOnClickListener{
            this.updateTool_layout.visibility = View.GONE
            this.meds_recyclerView.visibility = View.VISIBLE
            this.remove_button.visibility = View.VISIBLE
            this.fab.visibility = View.VISIBLE
            this.remove_button.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        return true
    }

    fun onWifiEnable() {
        if (!medsHaveBeenReadFromServer) {
            readAllMedsFromServer()
        }

        if (noWifiMedBuffer.size > 0) {
            Thread.sleep(3000)

            noWifiMedBuffer.forEach {
                try {
                    this.disposable = this.medCodeService.create(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            showResult("Pushed to server med!")
                            readAllMedsFromServer()

                            adapter.notifyDataSetChanged()
                        },
                            { showResult("Failed to push med to server!") })
                } catch (e: Exception) {
                    showResult(e.toString())
                }
            }
            noWifiMedBuffer = mutableListOf()

        }
    }

    private fun readAllMedsFromServer() {
        if (wifiEnabled and !medsHaveBeenReadFromServer)
            this.disposable = this.medCodeService.readAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items: List<Model.Med> ->
                    run {
                        medViewModel.deleteAll()
                        medViewModel.insertAll(items)
                        adapter.notifyDataSetChanged()
                    }
                }, { })
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    fun APASA(view: View) {
        this.currentUser?.let { showResult(it) }
        adapter.notifyDataSetChanged()
    }

    private fun showResult(message: String) {
        Log.d("Item ->", message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
}
