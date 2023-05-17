package com.example.havadurumuapp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DeadObjectException
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    var tvSehir:TextView?=null
    var location : SimpleLocation?=null
    var latitude :String?= null
    var longitude:String?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.sehirler,R.layout.simple_spinner_tek_satir)
        searchableSpinner.setTitle("Şehir seçin")
        searchableSpinner.adapter=spinnerAdapter
        searchableSpinner.setOnItemSelectedListener(this)
        searchableSpinner.setSelection(6)
        verileriGetir("Ankara")

    }

    private fun oankiSehriGetir(lat: String?,long:String?) {
        var sehirAdi :String? =null
        var url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+long+"&appid=2f486d31ac1b60c7629402f9eaaf4b97&lang=tr&units=metric"
        var havaDurumuObjeRequest2 =JsonObjectRequest(Request.Method.GET,url,null,object: Response.Listener<JSONObject>{
            override fun onResponse(response: JSONObject?) {
                var main = response!!.getJSONObject("main")
                var sicaklik = main.getInt("temp")
                tvSicaklik.text=sicaklik.toString()
                var nem = main.getInt("humidity")
                tvNem.text="Nem : %${nem.toString()}"
                sehirAdi = response.getString("name")
                tvSehir?.setText(sehirAdi)
                var weather = response.getJSONArray("weather")
                var aciklama = weather.getJSONObject(0).getString("description")
                tvAciklama.text=aciklama.capitalize()
                var icon = weather.getJSONObject(0).getString("icon")
                var resimDosyaAdi =resources.getIdentifier("icon_"+icon,"drawable",packageName)
                imgHavaDurumu.setImageResource(resimDosyaAdi)
                if(icon.last()== 'd'){
                    root_layout.background=getDrawable(R.drawable.gunduzz)
                    tvAciklama.setTextColor(resources.getColor(R.color.purple_500))
                    tvNem.setTextColor(resources.getColor(R.color.purple_500))
                    tvTarih.setTextColor(resources.getColor(R.color.purple_500))
                    tvSicaklik.setTextColor(resources.getColor(R.color.purple_500))
                    textView4.setTextColor(resources.getColor(R.color.purple_500))
                }else {
                    root_layout.background = getDrawable(R.drawable.geceee)
                    tvAciklama.setTextColor(resources.getColor(R.color.purple_500))
                    tvNem.setTextColor(resources.getColor(R.color.purple_500))
                    tvTarih.setTextColor(resources.getColor(R.color.purple_500))
                    tvSicaklik.setTextColor(resources.getColor(R.color.purple_500))
                    textView4.setTextColor(resources.getColor(R.color.purple_500))
                }
                tvTarih.text=tarihYazdir()
            }
        },object :Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
            }
        })
        MySingleton.getInstance(this).addToRequestQueue(havaDurumuObjeRequest2)

    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        tvSehir = view as TextView
        if (position==0){
            location=SimpleLocation(this)
            if (!location!!.hasLocationEnabled()){
                Toast.makeText(this,"GPS aç!!!",Toast.LENGTH_LONG).show()
                SimpleLocation.openSettings(this)
            }else{
                if (ContextCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) != 0){
                    ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION),60)
                }else{
                    location=SimpleLocation(this)
                    latitude=String.format("%.6f",location?.latitude)
                    longitude=String.format("%.6f",location?.longitude)
                    Log.e("bbbbbbbbbb",""+latitude)
                    Log.e("bbbbbbbbbb",""+longitude)
                    oankiSehriGetir(latitude,longitude)
                }
            }

        }else{
            var secilenSehir =parent!!.getItemAtPosition(position).toString()
            verileriGetir(secilenSehir)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode==60){
            if(grantResults.size > 0 && grantResults[0]==0 ){
                location=SimpleLocation(this)
                latitude=String.format("½.6f",location?.latitude)
                longitude=String.format("½.6f",location?.longitude)
                Log.e("bbbbbbbbbb",""+latitude)
                Log.e("bbbbbbbbbb",""+longitude)
                oankiSehriGetir(latitude,longitude)

            }else {
                searchableSpinner.setSelection(6)
                Toast.makeText(this,"İzin ver sanki çalacaz telefonu!!!",Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
    fun tarihYazdir():String{

        var takvim = Calendar.getInstance().time
        var formatlayici= SimpleDateFormat("d MMMM\n EEEE,\n HH:mm", Locale("tr"))
        var tarih = formatlayici.format(takvim)
        return tarih
    }
    fun verileriGetir(sehir:String){
        var url = "https://api.openweathermap.org/data/2.5/weather?q="+sehir.capitalize()+"&appid=2f486d31ac1b60c7629402f9eaaf4b97&lang=tr&units=metric"
        var havaDurumuObjeRequest =JsonObjectRequest(Request.Method.GET,url,null,object: Response.Listener<JSONObject>{
            override fun onResponse(response: JSONObject?) {
                var main = response!!.getJSONObject("main")
                var sicaklik = main.getInt("temp")
                tvSicaklik.text=sicaklik.toString()
                var nem = main.getInt("humidity")
                tvNem.text="Nem : %${nem.toString()}"
                var sehirAdi = response.getString("name")
                var weather = response.getJSONArray("weather")
                var aciklama = weather.getJSONObject(0).getString("description")
                tvAciklama.text=aciklama.capitalize()
                var icon = weather.getJSONObject(0).getString("icon")
                var resimDosyaAdi =resources.getIdentifier("icon_"+icon,"drawable",packageName)
                imgHavaDurumu.setImageResource(resimDosyaAdi)
                if(icon.last()== 'd'){
                    root_layout.background=getDrawable(R.drawable.gunduzz)
                    tvAciklama.setTextColor(resources.getColor(R.color.purple_500))
                    tvNem.setTextColor(resources.getColor(R.color.purple_500))
                    tvTarih.setTextColor(resources.getColor(R.color.purple_500))
                    tvSicaklik.setTextColor(resources.getColor(R.color.purple_500))
                    textView4.setTextColor(resources.getColor(R.color.purple_500))
                }else {
                    root_layout.background = getDrawable(R.drawable.geceee)
                    tvAciklama.setTextColor(resources.getColor(R.color.purple_500))
                    tvNem.setTextColor(resources.getColor(R.color.purple_500))
                    tvTarih.setTextColor(resources.getColor(R.color.purple_500))
                    tvSicaklik.setTextColor(resources.getColor(R.color.purple_500))
                    textView4.setTextColor(resources.getColor(R.color.purple_500))

                }

                tvTarih.text=tarihYazdir()

            }
        },object :Response.ErrorListener {

            override fun onErrorResponse(error: VolleyError?) {

            }
        })
        MySingleton.getInstance(this).addToRequestQueue(havaDurumuObjeRequest)
    }

}


