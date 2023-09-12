package com.example.todo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.todo.database.AppDatabase
import com.example.todo.database.Task
import com.example.todo.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddBinding.inflate(inflater, container, false)

        binding.addTimePicker.setIs24HourView(true)
        binding.addAddBtn.isEnabled = false

        binding.addEdittext.addTextChangedListener {
            binding.addAddBtn.isEnabled = binding.addEdittext.text.toString().isNotEmpty()
        }

        binding.addAddBtn.setOnClickListener {
            val day :Int = binding.addDatepicker.dayOfMonth
            val month:Int = binding.addDatepicker.month + 1
            val year:Int = binding.addDatepicker.dayOfMonth

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