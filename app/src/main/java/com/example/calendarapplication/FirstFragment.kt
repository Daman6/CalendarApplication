package com.example.calendarapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.CalendarContract
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.calendarapplication.databinding.FragmentFirstBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class FirstFragment : Fragment() {

     lateinit var sharedPreferences: SharedPreferences
    private var binding: FragmentFirstBinding? = null
    private var hour: Int = 0;
    private var minutes: Int = 0;
    private var date: Int = 0;
    private var month: Int = 0;
    private var year: Int = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =FragmentFirstBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences("myPref",Context.MODE_PRIVATE)

        binding!!.button.setOnClickListener {
            openDatePicker()
        }

        binding!!.btnSelectTime.setOnClickListener {
            openTimePicker()
        }

        binding!!.brnSave.setOnClickListener {
            createEvent()
        }
        return binding!!.root
    }

    private fun createEvent(){
        val title:String = binding!!.EtTitle.text.toString()
        val location:String = binding!!.EtLocation.text.toString()
        val description:String = binding!!.EtDescription.text.toString()


        val editor :SharedPreferences.Editor= sharedPreferences.edit()
        editor.putString("TITLE",title)
        editor.putString("DESCRIPTION",description)
        editor.putString("LOCATION",location)
        editor.apply()


       if(title.isEmpty()){
           Toast.makeText(context,"Please fill the Title Field",Toast.LENGTH_SHORT).show()
       }else if (location.isEmpty()){
           Toast.makeText(context,"Please fill the Location Field",Toast.LENGTH_SHORT).show()
       }else if (description.isEmpty()){
           Toast.makeText(context,"Please fill the Description Field",Toast.LENGTH_SHORT).show()
       }else if (binding!!.TvTime.text.isEmpty()){
           Toast.makeText(context,"Please Select the Time Field",Toast.LENGTH_SHORT).show()
       }else if (binding!!.TvTime.text.isEmpty()){
           Toast.makeText(context,"Please Select the Date Fieldte",Toast.LENGTH_SHORT).show()
       }else{
            event()
        }
    }

    private fun event(){
        sharedPreferences = requireContext().getSharedPreferences("myPref",Context.MODE_PRIVATE)

        val title = sharedPreferences.getString("TITLE","")
        val location = sharedPreferences.getString("DESCRIPTION","")
        val description = sharedPreferences.getString("LOCATION","")



            val startMillis: Long = Calendar.getInstance().run {
                set(year,--month, date, hour, minutes)
                timeInMillis

            }
            val endMillis: Long = Calendar.getInstance().run {
                set(year,--month, date, ++hour, minutes)
                timeInMillis
            }
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = (CalendarContract.Events.CONTENT_URI)
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(
                    CalendarContract.Events.DESCRIPTION,
                   description
                )
                putExtra(
                    CalendarContract.Events.EVENT_LOCATION,
                   location
                )
                putExtra(CalendarContract.Events.ALL_DAY, false)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            }
            if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
                startActivity(intent)
                val editor :SharedPreferences.Editor= sharedPreferences.edit()
                editor.clear()
            }else{
                Toast.makeText(context,"N0 Supported Google Calendar APP",Toast.LENGTH_LONG).show()
            }

    }

    private fun openTimePicker() {

        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Set Time")
            .build()
        picker.show(childFragmentManager,"TAG")

        picker.addOnPositiveButtonClickListener {
                hour = picker.hour
                minutes = picker.minute

                binding!!.TvTime.text = "TIME: "+hour+":"+minutes
        }
        picker.addOnNegativeButtonClickListener {
            // call back code
        }
        picker.addOnCancelListener {
            // call back code
        }
        picker.addOnDismissListener {
            // call back code
        }
    }
    
    private fun openDatePicker(){

        val dpicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        dpicker.show(childFragmentManager,"TAG")

        dpicker.addOnPositiveButtonClickListener {
            val resultDate = dpicker.selection
            val resultDateTv = dpicker.headerText
            binding!!.TvDate.text = "DATE: "+resultDateTv

            Log.d("TAG","timestamp"+resultDate)

            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = resultDate!!
            date = Integer.parseInt(DateFormat.format("dd", cal).toString())
            month = Integer.parseInt(DateFormat.format("MM", cal).toString())
            year = Integer.parseInt(DateFormat.format("yyyy", cal).toString())

        }
        dpicker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
        }
        dpicker.addOnCancelListener {
            // Respond to cancel button click.
        }
        dpicker.addOnDismissListener {
            // Respond to dismiss events.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

    }

}
