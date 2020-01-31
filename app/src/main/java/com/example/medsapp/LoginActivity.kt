package com.example.medsapp

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medsapp.service.ServiceFactory
import com.example.medsapp.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_log_in.*

class LoginActivity : AppCompatActivity() {

    private val userService by lazy {
        val factory = ServiceFactory.getInstance("http://192.168.1.9:5050", "admin", "admin")

        //val factory = ServiceFactory.getInstance("http://172.30.114.204:5050", "admin", "admin")
        factory.build(UserService::class.java)
    }

    private var disposable: Disposable? = null
    private lateinit var wifiM: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        wifiM = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_log_in)

        var slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        var fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        this.id_logo.animation = slide_down
        this.linear_layout_bottom.animation = fade_in


        button_log_in.setOnClickListener {
            isPasswordValid(password_edit_text.text!!)
        }
    }

    private fun isPasswordValid(text: Editable?) {
        val email = email_edit_text.text.toString()
        val password = text.toString()
        var passwordOfThisEmail: String

        Log.d("Email = ", email)
        Log.d("Password = ", password)

        if(wifiM.isWifiEnabled)
            this.disposable = this.userService.read(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    run {
                        passwordOfThisEmail = user[0].password
                        Log.d("password of this email", passwordOfThisEmail)

                        if (password == passwordOfThisEmail) {
                            password_text_input.error = null
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("EXTRA_CURRENT_USER", email)
                            startActivity(intent)
                            this.finish()
                        } else {
                            password_text_input.error = getString(R.string.error_password)
                            showResult(passwordOfThisEmail)
                        }
                    }
                }, { password_text_input.error = getString(R.string.error_password)
                    showResult("smth wrong")})
        else showResult("Wifi is off. Cannot login.")

    }

    private fun showResult(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}