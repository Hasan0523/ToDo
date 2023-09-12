package com.example.todo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.todo.database.AppDatabase
import com.example.todo.database.Task
import com.example.todo.databinding.FragmentAddBinding
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Date

class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddBinding.inflate(inflater, container, false)

        binding.addTimePicker.setIs24HourView(true)
        binding.addAddBtn.isEnabled = false
        var isToday = true
        binding.addEdittext.addTextChangedListener {
            binding.addAddBtn.isEnabled = binding.addEdittext.text.toString().isNotEmpty()
        }
        binding.addDatepicker.minDate = Date().time
        binding.addDatepicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            val today = LocalDate.now()
            if (dayOfMonth == today.dayOfMonth && monthOfYear+1 == today.monthValue && year == today.year){
                val now = LocalTime.now()
                binding.addTimePicker.hour = now.hour
                binding.addTimePicker.minute = now.minute
                isToday = true
            }else{
                isToday = false
            }
        }
        binding.addTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            val now = LocalTime.now()
            if (isToday){
                if (hourOfDay < now.hour || (hourOfDay == now.hour && minute < now.minute)){
                    binding.addTimePicker.hour = now.hour
                    binding.addTimePicker.minute = now.minute
                }
            }
        }

        binding.addAddBtn.setOnClickListener {

            val day :Int = binding.addDatepicker.dayOfMonth
            val month:Int = binding.addDatepicker.month + 1
            val year:Int = binding.addDatepicker.year

            val hour = binding.addTimePicker.hour
            val min = binding.addTimePicker.minute

            val text = binding.addEdittext.text.toString().trim()

            val appDatabase = AppDatabase.getInstance(requireContext())
            appDatabase.getTaskDao().addTask(Task(0, day, month, year, text, hour, min))

            requireActivity().onBackPressed()
        }

        return binding.root
    }

}