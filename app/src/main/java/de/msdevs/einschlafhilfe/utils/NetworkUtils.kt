package de.msdevs.einschlafhilfe.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtils {
    fun isConnected(context : Context) : Boolean{
        val currentNetwork = context.getSystemService(ConnectivityManager::class.java).activeNetwork
        return currentNetwork != null
    }

}