package com.example.todo

import android.annotation.SuppressLint
import android.os.Bundle
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
import java.util.Date

private const val ARG_PARAM1 = "task"

class AddFragment : Fragment() {
    private var task: Task? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddBinding.inflate(inflater, container, false)

        binding.addTimePicker.setIs24HourView(true)
        binding.addAddBtn.isEnabled = false
        var isToday = true

        if (task != null){
            binding.addAddBtn.text = "Update"
            binding.addEdittext.setText(task!!.text)
            binding.addDatepicker.init(task!!.year, task!!.month-1, task!!.day, null)
            binding.addTimePicker.minute = task!!.min
            binding.addTimePicker.hour = task!!.hour
            binding.addAddBtn.isEnabled = true
        }





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

            if (task == null){

                appDatabase.getTaskDao().addTask(Task(0, day, month, year, text, hour, min))
            }
            else{
                val task1 = task!!
                task1.day = day
                task1.month = month
                task1.year = year
                task1.hour=hour
                task1.min=min
                task1.text = text
                appDatabase.getTaskDao().updateTask(task1)
            }

            requireActivity().onBackPressed()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            task = it.getSerializable(ARG_PARAM1) as Task
        }
    }

    companion object {
        fun newInstance(task: Task) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, task)
                }
            }
    }
}