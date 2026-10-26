package com.nevoa.browser

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.text.InputType
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.KeyEvent

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var address: EditText
    private lateinit var status: TextView
    private val bg = Color.rgb(8, 12, 22)
    private val panel = Color.rgb(17, 25, 42)
    private val cyan = Color.rgb(80, 220, 235)
    private val white = Color.rgb(235, 242, 250)
    private var isClearing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        buildUi()
        configurePrivateWebView()
        clearSiteData {
            web.loadUrl("https://search.brave.com/")
        }
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(12, 10, 12, 6)
            disableAutofill(this)
        }
        val brand = TextView(this).apply {
            text = "◉  NÉVOA"
            textSize = 18f
            setTextColor(cyan)
            letterSpacing = .12f
            setPadding(5, 5, 5, 8)
        }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        address = EditText(this).apply {
            hint = "Pesquisar ou digitar endereço"
            setHintTextColor(Color.LTGRAY)
            setTextColor(white)
            textSize = 14f
            singleLine = true
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_GO
            setPadding(12, 3, 12, 3)
            setBackgroundColor(panel)
            disableAutofill(this)
        }
        val go = Button(this).apply {
            text = "IR"
            setTextColor(bg)
            setBackgroundColor(cyan)
            setOnClickListener { navigate() }
        }
        row.addView(address, LinearLayout.LayoutParams(0, 48, 1f))
        row.addView(go, LinearLayout.LayoutParams(62, 48))
        val controls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val back = smallButton("‹ Voltar") { if (this@MainActivity::web.isInitialized && web.canGoBack()) web.goBack() }
        val forward = smallButton("Avançar ›") { if (this@MainActivity::web.isInitialized && web.canGoForward()) web.goForward() }
        val clear = smallButton("✦ Limpar sessão") { clearSession() }
        controls.addView(back, LinearLayout.LayoutParams(0, 42, 1f))
        controls.addView(forward, LinearLayout.LayoutParams(0, 42, 1f))
        controls.addView(clear, LinearLayout.LayoutParams(0, 42, 1.35f))
        status = TextView(this).apply {
            text = "MODO PRIVADO • sem histórico ou cookies persistentes"
            textSize = 10f
            setTextColor(cyan)
            setPadding(5, 4, 5, 7)
        }
        web = WebView(this)
        disableAutofill(web)
        root.addView(brand)
        root.addView(row)
        root.addView(controls)
        root.addView(status)
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        address.setOnEditorActionListener { _, action, event ->
            if (action == EditorInfo.IME_ACTION_GO || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                navigate(); true
            } else false
        }
    }

    private fun disableAutofill(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }
    }

    private fun smallButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        textSize = 10f
        setTextColor(white)
        setBackgroundColor(panel)
        setOnClickListener { action() }
    }

    private fun configurePrivateWebView() {
        val cookies = CookieManager.getInstance()
        cookies.setAcceptCookie(false)
        web.apply {
            setBackgroundColor(Color.WHITE)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (!isFinishing && !isClearing) {
                        address.setText(url ?: "")
                        status.text = "MODO PRIVADO • histórico e dados locais temporários"
                    }
                }
            }
            webChromeClient = WebChromeClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = false
                databaseEnabled = false
                cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                saveFormData = false
                javaScriptCanOpenWindowsAutomatically = false
                setSupportMultipleWindows(false)
                allowFileAccess = false
                allowContentAccess = false
                builtInZoomControls = true
                displayZoomControls = false
                setGeolocationEnabled(false)
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }
            clearCache(true)
            clearHistory()
            clearFormData()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(web, false)
        }
    }

    private fun navigate() {
        var text = address.text.toString().trim()
        if (text.isEmpty()) return
        val looksLikeUrl = text.contains(".") && !text.contains(" ")
        if (looksLikeUrl && !text.startsWith("http://") && !text.startsWith("https://")) text = "https://$text"
        if (!text.startsWith("http://") && !text.startsWith("https://")) {
            text = "https://search.brave.com/search?q=" + java.net.URLEncoder.encode(text, "UTF-8")
        }
        if (text.startsWith("http://")) {
            Toast.makeText(this, "Use HTTPS para maior segurança.", Toast.LENGTH_SHORT).show()
        }
        web.loadUrl(text)
    }

    private fun clearSiteData(after: (() -> Unit)? = null) {
        isClearing = true
        web.stopLoading()
        web.clearHistory()
        web.clearCache(true)
        web.clearFormData()
        WebStorage.getInstance().deleteAllData()
        CookieManager.getInstance().removeAllCookies {
            CookieManager.getInstance().flush()
            isClearing = false
            after?.invoke()
        }
    }

    private fun clearSession() {
        clearSiteData {
            address.setText("")
            status.text = "SESSÃO LIMPA • nova navegação privada"
            web.loadUrl("https://search.brave.com/")
        }
    }

    override fun onPause() {
        if (this::web.isInitialized) {
            clearSiteData()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (this::web.isInitialized) {
            web.stopLoading()
            web.loadUrl("about:blank")
            web.clearHistory()
            web.clearCache(true)
            web.clearFormData()
            WebStorage.getInstance().deleteAllData()
            CookieManager.getInstance().removeAllCookies { CookieManager.getInstance().flush() }
            web.removeAllViews()
            web.destroy()
        }
        super.onDestroy()
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        if (this::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
