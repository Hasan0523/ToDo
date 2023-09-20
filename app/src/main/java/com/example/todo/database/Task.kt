package com.example.todo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var day: Int,
    var month : Int,
    var year : Int,
    var text : String,
    var hour:Int,
    var min :Int,
    @ColumnInfo(name = "image_url") var imageUrl : String
):Serializable