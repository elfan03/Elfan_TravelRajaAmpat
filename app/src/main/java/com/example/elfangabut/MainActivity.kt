package com.example.elfangabut

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elfangabut.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private  lateinit var imageUri: Uri
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnpilihfoto.setOnClickListener {
            selectImage()
        }
        binding.btninsertdata.setOnClickListener {
            uploudImage()
        }
    }
    fun uploudImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("uplouding file . . .")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        val itemNama = binding.editTextnama.text.toString()
        val itemNomorKamar = binding.editTextnomorkamar.text.toString()
        val itemNomorWa = binding.editTextnomorwa.text.toString()
        db = FirebaseDatabase.getInstance().getReference("items")
        val item = itemDs(itemNama, itemNomorKamar, itemNomorWa,)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val id = databaseReference.push().key
        db.child(id.toString()).setValue(item).addOnSuccessListener{
            binding.editTextnama.text.clear()
            binding.editTextnomorkamar.text.clear()
            binding.editTextnomorwa.text.clear()
        }
        storageReference.putFile(imageUri).
        addOnSuccessListener {
            binding.imageViewfoto.setImageURI(null)
            Toast.makeText(this@MainActivity, "sukses uploud",Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()

        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@MainActivity, "gagal", Toast.LENGTH_SHORT).show()
        }
    }
    @Suppress("DEPRECATION")
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            binding.imageViewfoto.setImageURI(imageUri)
        }
    }
}