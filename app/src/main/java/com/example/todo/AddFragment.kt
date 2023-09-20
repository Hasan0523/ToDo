package com.example.todo

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.example.todo.database.AppDatabase
import com.example.todo.database.Task
import com.example.todo.databinding.FragmentAddBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

private const val ARG_PARAM1 = "task"

class AddFragment : Fragment() {
    private var task: Task? = null
    private lateinit var binding:FragmentAddBinding
    private lateinit var img :ImageView
    private var imageUrl = ""
    private lateinit var currentFilePath: String



    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        img = binding.itemImage

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
            imageUrl = task!!.imageUrl
            img.setImageURI(Uri.parse(imageUrl))
        }

        binding.itemCameraBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.itemGalleryBtn.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.itemDeleteBtn.setOnClickListener {
            binding.itemImage.setImageResource(R.drawable.ic_launcher_background)
            try {
                File(imageUrl).delete()
            }catch (e:Exception){
                e.printStackTrace()
            }
            imageUrl = ""
        }

        binding.addEdittext.addTextChangedListener {
            binding.addAddBtn.isEnabled = binding.addEdittext.text.toString().isNotEmpty()
        }
        binding.addDatepicker.minDate = Date().time
        binding.addDatepicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
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
        binding.addTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
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
                appDatabase.getTaskDao().addTask(Task(0, day, month, year, text, hour, min, imageUrl))
            }
            else{
                val task1 = task!!
                task1.day = day
                task1.month = month
                task1.year = year
                task1.hour=hour
                task1.min=min
                task1.imageUrl = imageUrl
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
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryUri = it
        try{
            try {
                File(imageUrl).delete()
            }catch (e:Exception){
                e.printStackTrace()
            }
            img.setImageURI(galleryUri)
            imageUrl = galleryUri.toString()

//            val openInputStream = requireActivity().contentResolver?.openInputStream(Uri.parse(imageUrl))
//            val file = File(requireActivity().filesDir, "${System.currentTimeMillis()}.jpg")
//            val fileOutputStream = FileOutputStream(file)
//            openInputStream?.copyTo(fileOutputStream)
//            currentFilePath = file.absolutePath
//            openInputStream?.close()

            requireActivity().onBackPressed()
        }catch(e:Exception){
            e.printStackTrace()
        }
    }





    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentFilePath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        photoFile?.let {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.todo",
                it
            )
            takePhotoResultCamera.launch(photoURI)
        }
    }
    val takePhotoResultCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            val uri = Uri.fromFile(File(currentFilePath))
            img.setImageURI(uri)
            imageUrl = uri.toString()
        }
    }













}