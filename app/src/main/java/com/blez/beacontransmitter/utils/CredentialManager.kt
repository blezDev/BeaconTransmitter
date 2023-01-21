package com.blez.beacontransmitter.utils

import android.content.Context
import android.content.SharedPreferences
import com.blez.beacontransmitter.utils.Constants.PREFS_TOKEN_FILE
import com.blez.beacontransmitter.utils.Constants.USER_NAME
import com.blez.beacontransmitter.utils.Constants.USER_UUID

class CredentialManager(context: Context) {
    private var prefs : SharedPreferences = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveUUID(uuid : String)
    {
        val editor = prefs.edit()
        editor.putString(USER_UUID,uuid)
        editor.apply()
    }
    fun saveName(name : String){
        val editor = prefs.edit()
        editor.putString(USER_NAME,name)
        editor.apply()
    }
    fun getName() : String?{
        return prefs.getString(USER_NAME,null)
    }

    fun getUUID() : String?{
        return prefs.getString(USER_UUID,null)
    }
}