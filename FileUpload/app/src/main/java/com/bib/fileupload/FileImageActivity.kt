package com.bib.fileupload

import android.R.attr
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bib.fileupload.model.ReturnData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import okhttp3.*
import java.io.File

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.RequestBody
import okhttp3.MultipartBody


class FileImageActivity : AppCompatActivity() {

    private var imageName :String = ""
    private lateinit var f: File

    // Initializing the layout views
    private lateinit var pickImageTV: TextView
    private lateinit var imageView: ImageView
    private lateinit var pdfTextView: TextView
    private lateinit var callApiButton: Button
    private lateinit var pdfUri: Uri

    val client = OkHttpClient()
    val accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJFMDAwMDQiLCJlbXBsb3llZVR5cGUiOjQsImV4cCI6MTY1MzEyNDE1MSwiaWF0IjoxNjUwNTMyMTUxfQ.ZVQjnv-lRkuMv81tdagzAp2yALqPV_qGRFUKdtG7IpAVbPDDHT_Bgr6JYBr4JOM8P5-APuDOspyo-_5hvSk0Bg"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_image)

        pickImageTV = findViewById(R.id.imageTextView)
        imageView = findViewById(R.id.imageView)
        pdfTextView = findViewById(R.id.selectedPdf)
        callApiButton = findViewById(R.id.call_api_button)

        pickImageTV.setOnClickListener {
//            selectImage()
            showActionSheet()
        }

        imageView.setOnClickListener {
            selectPdf()
        }

        pdfTextView.setOnClickListener {
            selectPdf()
        }

        callApiButton.setOnClickListener {
            callApi()
        }
    }

    // Function for displaying an AlertDialogue for choosing an image
    private fun selectImage() {
        val choice = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        myAlertDialog.setTitle("Select Image")
        myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
            when {
                // Select "Choose from Gallery" to pick image from gallery
                choice[item] == "Choose from Gallery" -> {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, 1)
                }
                // Select "Take Photo" to take a photo
                choice[item] == "Take Photo" -> {
                    val cameraPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraPicture, 0)
                }
            }
        })
        myAlertDialog.show()
    }

    // Intent for navigating to the files
    private fun selectPdf() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 12)
    }

    // Override this method to allow you select an an image or a PDF
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // For loading Image
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val imageSelected = data.extras!!["data"] as Bitmap?
                    Log.d("image upload", "onActivityResult setImageBitmap: ${imageSelected}")
                    imageView.setImageBitmap(imageSelected)
                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val imageSelected = data.data
                    val pathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (imageSelected != null) {
                        val myCursor = contentResolver.query( imageSelected, pathColumn, null, null, null )
                        // Setting the image to the ImageView
                        if (myCursor != null) {
                            Log.d("image upload", "onActivityResult myCursor not null : ${myCursor}")
                            myCursor.moveToFirst()
                            val columnIndex = myCursor.getColumnIndex(pathColumn[0])
                            val picturePath = myCursor.getString(columnIndex)
                            Log.d("image upload", "onActivityResult myCursor not null picturePath: ${picturePath}")
                            imageView.setImageURI(imageSelected)

                            f = File(picturePath)
                            imageName = f.name
                            Log.d("image upload", "onActivityResult imageName : ${imageName}")
                            pickImageTV.text = imageName
                            myCursor.close()


                        }
                    }
                }
            }
        }

        // For loading PDF
        when (requestCode) {
            12 -> if (resultCode == RESULT_OK) {

                pdfUri = data?.data!!
                val uri: Uri = data?.data!!
                val uriString: String = uri.toString()



                var pdfName: String? = null
                if (uriString.startsWith("content://")) {
                    var myCursor: Cursor? = null
                    try {
                        // Setting the PDF to the TextView
                        myCursor = applicationContext!!.contentResolver.query(uri, null, null, null, null)
                        if (myCursor != null && myCursor.moveToFirst()) {
                            pdfName = myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            pdfTextView.text = pdfName

                            val file = File(uri.toString())
                            var path = file.absolutePath
                            f = file
                            Log.d("call ", "onActivityResult: pdf file path ${path}")
                        }
                    } finally {
                        myCursor?.close()
                    }
                }
            }
        }
    }

    fun showActionSheet(){

        val bottomSheetDialog = BottomSheetDialog(this, R.style.FeedbackBottomSheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)

        val share = bottomSheetDialog.findViewById<LinearLayout>(R.id.bottom_sheet_dialog_share)
        val upload = bottomSheetDialog.findViewById<LinearLayout>(R.id.bottom_sheet_dialog_upload)
        val download = bottomSheetDialog.findViewById<LinearLayout>(R.id.bottom_sheet_dialog_download)

        bottomSheetDialog.show()

        share?.setOnClickListener {
            Toast.makeText(getApplicationContext(), "Photo Click", Toast.LENGTH_LONG).show()
            bottomSheetDialog.dismiss();
            chooseImage()
        }
        upload?.setOnClickListener {
            Toast.makeText(getApplicationContext(), "File Click", Toast.LENGTH_LONG).show()
            bottomSheetDialog.dismiss();
            selectPdf()
        }

        download?.setOnClickListener {
            Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_LONG).show()
            bottomSheetDialog.dismiss();
        }
    }

    fun chooseImage(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 1)
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun callApi() {
        var postParam =  JsonObject();
        postParam.addProperty("date", "27/04/2022")
        postParam.addProperty("period", 3)
        postParam.addProperty("description", "STL Report")

        val mimeType = getMimeType(f);

        // Parsing any Media type file
        val requestBody: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), mimeType!!)
        val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData("filename", f.name, requestBody)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://150.95.82.104:8080/ems-mobile/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        val API = retrofit.create(RetofitInterface::class.java)

        val call: Call<ReturnData> = API.singleLeaveReport( "Bearer " + accessToken,
            postParam, fileToUpload)

        call.enqueue(object : Callback<ReturnData>{
            override fun onResponse(call: Call<ReturnData>, response: Response<ReturnData>) {
                val returnData = response.body()
                if (returnData!= null){
                    Log.d("call", "onResponse: ${returnData.responseDescription}")
                }else{
                    Log.d("call", "onResponse: null")
                }
            }

            override fun onFailure(call: Call<ReturnData>, t: Throwable) {
                Log.d("call", "onResponse: onFailure ${t}")
            }
        })
    }
}