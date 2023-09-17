package com.example.todo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.adapter.HomeRecyclerAdapter
import com.example.todo.database.AppDatabase
import com.example.todo.database.Task
import com.example.todo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        val appDatabase = AppDatabase.getInstance(requireContext())
        binding.homeAddBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }

        binding.homeRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.homeRecycler.adapter = HomeRecyclerAdapter(appDatabase.getTaskDao().getTasks() as ArrayList<Task>, appDatabase, requireContext(), object : HomeRecyclerAdapter.HomeRecyclerInterface{
            override fun onEdit(task: Task) {
                val bundle = Bundle()
                bundle.putSerializable("task", task)
                findNavController().navigate(R.id.action_homeFragment_to_addFragment, bundle)
            }

        })


        return binding.root
    }

}