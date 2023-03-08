package com.example.currencytoday

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.currencytoday.api.EndPoint
import com.example.currencytoday.util.NetworkUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.currencytoday.notification.permission

class MainActivity : AppCompatActivity() {

    private lateinit var spFrom : Spinner
    private lateinit var spTo : Spinner
    private lateinit var btConvert : Button
    private lateinit var tvResult : TextView
    private lateinit var etValueFrom : EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrencies()

        spFrom = findViewById(R.id.spFrom)
        spTo = findViewById(R.id.spTo)
        btConvert = findViewById(R.id.btConvert)
        tvResult = findViewById(R.id.tvResult)
        etValueFrom = findViewById(R.id.etValueFrom)
        btConvert.setOnClickListener { convertMoney() }

    }

    fun convertMoney(){


        val permissionManager = permission (this)
        if (permissionManager.NotificationPermissionOK()){          //check permission notification

            val valueFrom = etValueFrom.text.toString().trim()
            if(valueFrom.isNotBlank()){                             //check valor digitado para converter
                val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net")
                val endpoint = retrofitClient.create(EndPoint::class.java)

                endpoint.getCurrencyRate(spFrom.selectedItem.toString(), spTo.selectedItem.toString()).enqueue(object :
                    Callback<JsonObject>{

                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        var data = response.body()?.entrySet()?.find{ it.key == spTo.selectedItem.toString() }
                        val rate : Double = data?.value.toString().toDouble()
                        val conversion = etValueFrom.text.toString().toDouble() * rate

                        val roundoff = String.format("%.2f", conversion)
                        tvResult.setText(conversion.toString())
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        tvResult.setText("Erro")
                    }
                })

            } else {
                tvResult.setText("Digite um valor para converter!")   //retorno caso não tenha um valor
            }
        }
            else{
            permissionManager.requestNotificationPermission()        //retorno caso não tenha permissão de notificação
        }


    }
    fun getCurrencies(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net")
        val endpoint = retrofitClient.create(EndPoint::class.java)

        endpoint.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")

                val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)
                spFrom.adapter = adapter
                spTo.adapter = adapter

                spFrom.setSelection(posBRL)
                spTo.setSelection(posUSD)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                tvResult.setText("Erro")
            }

        })
    }
}