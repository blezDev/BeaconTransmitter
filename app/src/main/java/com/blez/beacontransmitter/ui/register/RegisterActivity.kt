package com.blez.beacontransmitter.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.blez.beacontransmitter.R
import com.blez.beacontransmitter.data.RegisterStatus
import com.blez.beacontransmitter.data.RegisterUser
import com.blez.beacontransmitter.databinding.ActivityRegisterBinding
import com.blez.beacontransmitter.`interface`.BeaconAPI
import com.blez.beacontransmitter.network.Retrofit
import com.blez.beacontransmitter.ui.SetupActivity
import com.blez.beacontransmitter.utils.CredentialManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    var options = arrayOf("A+ ","A-","B+","B-","O+","O-","AB+","AB-")
    private lateinit var credentialManager: CredentialManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register)
        val retService = Retrofit.getRetrofitInstance().create(BeaconAPI::class.java)
        credentialManager = CredentialManager(this)
        binding.editBloodGroup.adapter =  ArrayAdapter<String>(this,R.layout.drawable_spinner,options)



    binding.button.setOnClickListener {
        val email = binding.editEmail.text.toString()
        val name = binding.editName.text.toString()
        val employee_Id = binding.editEmpId.text.toString()
        val con1 = binding.editPh1.text.toString()
        val con2 = binding.editPh2.text.toString()
        val con3 = binding.editPh3.text.toString()
        val blood = binding.editBloodGroup.selectedItem.toString()

        if(email.isEmpty() || name.isEmpty() || employee_Id.isEmpty() || con1.isEmpty() || con2.isEmpty() || con3.isEmpty() || blood.isEmpty() ){
            Toast.makeText(this, "All fields need to be filled", Toast.LENGTH_SHORT).show()
        }else{
            val contacts = listOf(con3,con2,con1)
            val registerData = RegisterUser(email = email, name = name, bloodGroup = blood, contacts = contacts, empid = employee_Id)
        retService.registerUser(registerData).enqueue(object  : Callback<RegisterStatus>{
            override fun onResponse(
                call: Call<RegisterStatus>,
                response: Response<RegisterStatus>
            ) {
              when(response.code()){
                  200->{
                      Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT).show()
                      credentialManager.saveUUID(response.body()?.serial.toString())
                      credentialManager.saveName(name)
                      val intent = Intent(this@RegisterActivity,SetupActivity::class.java)
                      startActivity(intent)
                      finish()

                  }
                  500->{
                      Toast.makeText(this@RegisterActivity, "Server error", Toast.LENGTH_SHORT).show()

                  }
              }
            }

            override fun onFailure(call: Call<RegisterStatus>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Something went wrong pls try later", Toast.LENGTH_SHORT).show()
            }
        })


        }
    }


    }
}