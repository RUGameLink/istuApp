package com.example.istuapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.SearchView.OnQueryTextListener as OnQueryTextListener


class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var restartActivity: Button
    private lateinit var errorText: TextView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var request = "https://www.istu.edu/schedule/"
        init()
        loadPage(this, request)

        restartActivity.setOnClickListener(restartActivityListener)

        swipeContainer.setOnRefreshListener(swipeContainerListener)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                var text = query.replace(" ", "+", false)
                var request = "https://www.istu.edu/schedule/?search=" + text
                loadPage(this@MainActivity, request)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
            /*    var text = newText.replace(" ", "+", false)
                var request = "https://www.istu.edu/schedule/?search=" + text
                loadPage(this@MainActivity, request) */
                return false
            }
        })
    }

    private var swipeContainerListener: SwipeRefreshLayout.OnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        if(applicationContext.isConnectedToNetwork()){
            webView.reload()
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    elementVisible(true)
                },
                5000 // value in milliseconds
            )


        }
        else{
            Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
            swipeContainer.isRefreshing = false
    }

    private var restartActivityListener: View.OnClickListener = View.OnClickListener {
        if(applicationContext.isConnectedToNetwork()){
            var request = "https://www.istu.edu/schedule/"
            loadPage(this, request)
            webView.reload()
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    elementVisible(true)
                },
                5000 // value in milliseconds
            )
        }
        else{
            Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if(applicationContext.isConnectedToNetwork()) {
            if (webView.canGoBack()) {
                webView.goBack()
            }
            else{
                super.onBackPressed()
            }
        }
        else{
            Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPage(context: Context, request: String){
        if (context.isConnectedToNetwork()) {
            elementVisible(true)
            webView.webViewClient = WebViewClient()
            webView.webChromeClient = WebChromeClient()
            // включаем поддержку JavaScript
            webView.settings.javaScriptEnabled = true
            // указываем страницу загрузки
        //    webView.loadUrl("https://www.istu.edu/schedule/")
            webView.loadUrl(request)

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
        restartActivity = findViewById(R.id.restartActivity)
        errorText = findViewById(R.id.errorText)
        swipeContainer = findViewById(R.id.swipeContainer)
        searchView = findViewById(R.id.searchView)
    }
}