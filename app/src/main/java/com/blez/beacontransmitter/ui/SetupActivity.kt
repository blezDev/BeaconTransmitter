package com.blez.beacontransmitter.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.input.InputManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blez.beacontransmitter.R
import com.blez.beacontransmitter.databinding.ActivitySetupBinding
import com.blez.beacontransmitter.ui.service.MyService
import com.blez.beacontransmitter.utils.CredentialManager
import com.permissionx.guolindev.PermissionX
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*
import kotlin.collections.ArrayList

class SetupActivity : AppCompatActivity(){

    private val TAG = "MyAdvertiser"
    private val CHANNEL_ID = "beacon_channel"

    private lateinit var binding: ActivitySetupBinding
    private var emition = false
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var bluetoothAdapter : BluetoothAdapter
    var beacon__service : MyService? = null
    private lateinit var credentialManager: CredentialManager


    private val beaconSericeConnection  = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.MyBinder
            beacon__service = binder.currentService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            beacon__service = null
        }
    }







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup)
        binding.StopIBeaconBTN.visibility = View.INVISIBLE
        binding.StopIBeaconBTN.isEnabled = false
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        val bluetoothAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser

        //For Starting Service
         var intentService = Intent(this@SetupActivity,MyService::class.java)
        bindService(intentService,beaconSericeConnection, BIND_AUTO_CREATE)
        credentialManager = CredentialManager(this)


        Log.e("TAG","onCreate is called")
        Log.e("TAG","onCreate ble state : ${beacon__service?.beaconTransmitter.toString()} ")
        askPermission()



        if (!isBluetoothEnabled()){
            Toast.makeText(this, "Please Enable Bluetooth", Toast.LENGTH_LONG).show()
        }


              binding.StartIBeaconBTN.setOnClickListener {
                  hideKeyboard(it)
                  askPermission()

                  val major_value = "3"
                  val minor_value = "3"
                  val measured = "-59"
                  val major = if (major_value.isEmpty()) "3" else major_value
                  val minor = if(minor_value.isEmpty()) "3" else minor_value

                 if (!isBluetoothEnabled())
                  {
                      Toast.makeText(this, "Please Enable Bluetooth", Toast.LENGTH_LONG).show()
                  }
                  else if (major.toInt() > 65535 || minor.toInt()> 65535){
                      Toast.makeText(this, "Value must be less than or equal to 65535", Toast.LENGTH_LONG).show()
                  }
                  else{
                   val  measured_value = measured.toInt()

                      beacon__service?.beacon = Beacon.Builder()

                          .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
                          .setId2(major)
                          .setId3(minor)
                          .setManufacturer(0x004C)
                          .setTxPower(-59)
                          /*.setDataFields(arrayListOf(0L))*/
                          .build()

                      beacon__service?.beaconParser = BeaconParser()
                          .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
               /*           .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")*/
                      beacon__service?.beaconTransmitter = BeaconTransmitter(applicationContext,  beacon__service?.beaconParser)


                      beacon__service?.beaconTransmitter?.startAdvertising( beacon__service?.beacon, object : AdvertiseCallback() {
                          override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                              super.onStartSuccess(settingsInEffect)
                              Toast.makeText(this@SetupActivity, "Beacon service is started", Toast.LENGTH_SHORT).show()
                                beacon__service!!.startForeground_Service()
                              emition = true
                              binding.StopIBeaconBTN.visibility = View.VISIBLE
                              binding.StopIBeaconBTN.isEnabled = true
                              binding.StartIBeaconBTN.visibility = View.INVISIBLE
                              binding.StartIBeaconBTN.isEnabled = false
                              binding.rippleAnimation.visibility = View.VISIBLE
                          }

                          override fun onStartFailure(errorCode: Int) {
                              super.onStartFailure(errorCode)
                              Toast.makeText(this@SetupActivity, "falied", Toast.LENGTH_SHORT).show()
                          }
                      })


                  }



            }
        binding.StopIBeaconBTN.setOnClickListener {

            hideKeyboard(it)
            emition = false
            askPermission()
            beacon__service?. beaconTransmitter?.stopAdvertising()
            Toast.makeText(this@SetupActivity, "Beacon service is stopped", Toast.LENGTH_SHORT).show()
           beacon__service?.stopforegroundservice()
            binding.StartIBeaconBTN.visibility = View.VISIBLE
            binding.StartIBeaconBTN.isEnabled = true
            binding.StopIBeaconBTN.visibility = View.INVISIBLE
            binding.StopIBeaconBTN.isEnabled = false
            binding.rippleAnimation.visibility = View.INVISIBLE

        }




    }

    fun buttonSwitch(emition : Boolean){
        when(emition){
            true->{
                binding.StopIBeaconBTN.visibility = View.VISIBLE
                binding.StopIBeaconBTN.isEnabled = true
                binding.StartIBeaconBTN.visibility = View.INVISIBLE
                binding.StartIBeaconBTN.isEnabled = false
                binding.rippleAnimation.visibility = View.VISIBLE
            }
            false->{
                binding.StartIBeaconBTN.visibility = View.VISIBLE
                binding.StartIBeaconBTN.isEnabled = true
                binding.StopIBeaconBTN.visibility = View.INVISIBLE
                binding.StopIBeaconBTN.isEnabled = false
                binding.rippleAnimation.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        bindService(Intent(this@SetupActivity,MyService::class.java),beaconSericeConnection, BIND_AUTO_CREATE)


        Log.e("TAG","onStop is called")
        Log.e("TAG","onStop ${beacon__service?.beaconTransmitter?.toString()} + ${beacon__service?.beaconTransmitter?.isStarted}")
    }

    override fun onResume() {
        super.onResume()
        buttonSwitch(emition)
        Log.e("TAG","onResume is called")
        Log.e("TAG","onResume ${beacon__service?.beaconTransmitter?.toString()} + ${beacon__service?.beaconTransmitter?.isStarted}")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("TAG","onRestart is called")
        Log.e("TAG","onRestart ${beacon__service?.beaconTransmitter?.toString()} + ${beacon__service?.beaconTransmitter?.isStarted}")
    }

    override fun onPause() {
        super.onPause()
        Log.e("TAG","onPause ${beacon__service?.beaconTransmitter?.toString()} + ${beacon__service?.beaconTransmitter?.isStarted}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG","OnDestroyed is called")
        unbindService(beaconSericeConnection)

    }


    private fun hideKeyboard(v : View){
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(v.applicationWindowToken,0)

    }

    private fun isBluetoothEnabled() : Boolean{
    val mBlul = bluetoothAdapter.isEnabled
    return mBlul
}
    private fun askPermission(){
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.S) {
            PermissionX.init(this)
                .permissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN
                )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "These permissions are denied: $deniedList",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
        else{
            PermissionX.init(this)
                .permissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_ADVERTISE,

                    )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "These permissions are denied: $deniedList",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }



}