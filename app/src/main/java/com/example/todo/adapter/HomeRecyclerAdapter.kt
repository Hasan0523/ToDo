package com.example.todo.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.database.AppDatabase
import com.example.todo.database.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeRecyclerAdapter(val tasks:ArrayList<Task>, val appDatabase: AppDatabase, val context: Context, val homeRecyclerInterface: HomeRecyclerInterface): RecyclerView.Adapter<HomeRecyclerAdapter.MyHolder>() {
    inner class MyHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val texT : TextView = itemView.findViewById(R.id.home_recyclerit_text)
        val date : TextView = itemView.findViewById(R.id.item_date)
        val time : TextView = itemView.findViewById(R.id.item_time)
        val deleteBTN : FloatingActionButton = itemView.findViewById(R.id.home_recyclerit_axlatbtn)
        val editBTN : FloatingActionButton = itemView.findViewById(R.id.home_recyclerit_edit_btn)
        val image : ImageView = itemView.findViewById(R.id.home_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_recycler_item, parent, false))
    }

    override fun getItemCount(): Int {
        return  tasks.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val task = tasks[position]
        holder.texT.text = task.text
        holder.editBTN.setOnClickListener {

        }
        holder.editBTN.setOnClickListener {
            homeRecyclerInterface.onEdit(task)
        }
        if (task.imageUrl.isEmpty()) holder.image.visibility = View.GONE

        holder.date.text = "${task.day}.${task.month}.${task.year}"
        holder.time.text  ="${task.hour}:${task.min}"

        holder.image.setImageURI(Uri.parse(task.imageUrl))

        holder.deleteBTN.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you really want to delete this task ?")
            builder.setCancelable(true)
            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    appDatabase.getTaskDao().deleteTask(task)
                    tasks.remove(task)
                    notifyItemRemoved(position)
                } as DialogInterface.OnClickListener)
            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    dialog.cancel()
                } as DialogInterface.OnClickListener)
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
    interface HomeRecyclerInterface{
        fun onEdit(task: Task)
    }
}