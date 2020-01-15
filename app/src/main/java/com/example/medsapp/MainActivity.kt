package com.example.medsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medsapp.dialog.AddDialog
import com.example.medsapp.medAdapter.MedListAdapter
import com.example.medsapp.service.MedService
import com.example.medsapp.service.Model
import com.example.medsapp.service.ServiceFactory
import com.example.medsapp.viewmodel.MedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception


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
            val intent = Intent(it.context, AddDialog::class.java)
            it.context.startActivity(intent)
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
                            showResult("Pushed to server med ${it.name}")
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
                .subscribe({ items: ArrayList<Model.Med> ->
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
}
