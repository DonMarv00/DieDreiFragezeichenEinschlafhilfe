package de.msdevs.einschlafhilfe.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import de.msdevs.einschlafhilfe.databinding.FragmentPrivacyBinding



class PrivacyFragment(override val isPolicyRespected: Boolean) : Fragment(), SlidePolicy {

    private lateinit var webView: WebView
    lateinit var binding : FragmentPrivacyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = binding.webview
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.visibility = View.VISIBLE
            }
        }
        webView.loadUrl("file:///android_asset/index.html")
    }


    companion object {
        fun newInstance() : PrivacyFragment {
            return PrivacyFragment(true)
        }
    }

    override fun onUserIllegallyRequestedNextPage() {
        TODO("Not yet implemented")
    }

}


