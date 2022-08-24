package com.example.istuapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var backButton: FloatingActionButton
    private lateinit var restartButton: FloatingActionButton
    private lateinit var restartActivity: Button
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        loadPage(this)

        backButton.setOnClickListener(backListener)
        restartButton.setOnClickListener(restartListener)
        restartActivity.setOnClickListener(restartActivityListener)
    }

    private var backListener: View.OnClickListener = View.OnClickListener{
        if(applicationContext.isConnectedToNetwork()) {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }
        else{
            Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }

    }

    private var restartListener: View.OnClickListener = View.OnClickListener {
        if(applicationContext.isConnectedToNetwork()){
            webView.reload()
        }
        else{
            Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private var restartActivityListener: View.OnClickListener = View.OnClickListener {
        loadPage(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPage(context: Context){
        if (context.isConnectedToNetwork()) {
            elementVisible(true)
            webView.webViewClient = WebViewClient()
            webView.webChromeClient = WebChromeClient()
            // включаем поддержку JavaScript
            webView.settings.javaScriptEnabled = true
            // указываем страницу загрузки
            webView.loadUrl("https://www.istu.edu/schedule/")

            val builder = AlertDialog.Builder(this@MainActivity)
            val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            val dialog = builder.create()

            webView.webViewClient = object: WebViewClient(){

                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

                    dialog.show()
                    dialog.getWindow()?.setLayout(240, 240)
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    dialog.dismiss()
                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    elementVisible(false)
                    super.onReceivedError(view, request, error)
                }
            }
        } else {
            elementVisible(false)
        }
    }
    private fun elementVisible(state: Boolean){
        webView.isVisible = state
        backButton.isVisible = state
        restartButton.isVisible = state

        restartActivity.isVisible = !state
        errorText.isVisible = !state
    }

    @SuppressLint("MissingPermission")
    private fun Context.isConnectedToNetwork(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

    private fun init(){
        webView = findViewById(R.id.webView)
        backButton = findViewById(R.id.backButton)
        restartButton = findViewById(R.id.restartButton)
        restartActivity = findViewById(R.id.restartActivity)
        errorText = findViewById(R.id.errorText)
    }
}