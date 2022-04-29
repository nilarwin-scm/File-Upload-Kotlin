package com.bib.backactionbar

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class GenerateExcel_PDF : AppCompatActivity() {
    lateinit var hssfCell: HSSFCell
    lateinit var hssfRow: HSSFRow
    lateinit var myEditText: EditText
    lateinit var generatePDFbtn: Button
    lateinit var generateExcelbtn: Button
    val PERMISSION_REQUEST_CODE: Int? = 200
    private val hdlr = Handler()
    // Create and register notification channel api 26+
    val channelId = "My_Channel_ID"
    val notificationId = 1
    private val filePath = File(Environment.getExternalStorageDirectory().toString() + "/Demo.xls")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_excel__p_d_f)
        generatePDFbtn = findViewById(R.id.generatePDFbtn);
        generateExcelbtn = findViewById(R.id.generateExcelbtn)
        createNotificationChannel(channelId)

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        myEditText = findViewById(R.id.myEditText);

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PackageManager.PERMISSION_GRANTED
        )

        generatePDFbtn.setOnClickListener {
            var progressDialog: ProgressDialog
            progressDialog = ProgressDialog(this)
            progressDialog.max = 100 // Progress Dialog Max Value
            progressDialog.setMessage("Loading...") // Setting Message
            progressDialog.setTitle("ProgressDialog") // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) // Progress Dialog Style Horizontal
            progressDialog.show() // Display Progress Dialog
            progressDialog.setCancelable(false)
            generatePDF()
            Thread {
                try {
                    while (progressDialog.progress <= progressDialog.max) {
                        progressDialog.incrementProgressBy(2)
                        Thread.sleep(200)
                        hdlr.sendMessage(hdlr.obtainMessage())
                        if (progressDialog.progress == progressDialog.max) {
                            progressDialog.dismiss()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        generateExcelbtn.setOnClickListener{
            var progressDialog: ProgressDialog
            progressDialog = ProgressDialog(this)
            progressDialog.max = 100 // Progress Dialog Max Value
            progressDialog.setMessage("Loading...") // Setting Message
            progressDialog.setTitle("ProgressDialog") // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) // Progress Dialog Style Horizontal
            progressDialog.show() // Display Progress Dialog
            progressDialog.setCancelable(false)
            generateExcel()
            Thread {
                try {
                    while (progressDialog.progress <= progressDialog.max) {
                        progressDialog.incrementProgressBy(2)
                        Thread.sleep(200)
                        hdlr.sendMessage(hdlr.obtainMessage())
                        if (progressDialog.progress == progressDialog.max) {
                            progressDialog.dismiss()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }.start()
        }


    }

    fun generateExcel(){
        val hssfWorkbook:Workbook = HSSFWorkbook()
        val hssfSheet = hssfWorkbook.createSheet("Custom Sheet")
        val ROWS_COUNT = 20
        val COLS_COUNT = 10
        val mergeCellStyle: CellStyle = hssfWorkbook.createCellStyle()
        mergeCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        mergeCellStyle.setVerticalAlignment(
            CellStyle.VERTICAL_CENTER
        );

        val row: Row = hssfSheet.createRow(0)
        row.height = 800.toShort()
        val cell: Cell = row.createCell(1)
        cell.setCellValue("This is a test of merging")
        cell.setCellStyle(mergeCellStyle);
        hssfSheet.addMergedRegion(CellRangeAddress(0, 0, 1, 8))

        var cellStyle : CellStyle = hssfWorkbook.createCellStyle()
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        for (i in 1..ROWS_COUNT) {
            hssfRow = hssfSheet.createRow(i) as HSSFRow
            for (j in 1..COLS_COUNT) {
                val cell: Cell = hssfRow.createCell(j)
                cell.setCellValue("Test $i")
                cell.setCellStyle(cellStyle)
            }
        }
        try {
            if (!filePath.exists()) {
                filePath.createNewFile()
            }
            val fileOutputStream: FileOutputStream = FileOutputStream(filePath)
            hssfWorkbook.write(fileOutputStream)
            Toast.makeText(this, "Generate Excel successfully", Toast.LENGTH_SHORT).show();
            if (fileOutputStream != null) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun generatePDF() {
        val headers = arrayOf("No", "Username", "First Name", "Last Name")
        val rows = arrayOf(
            arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "2",
                "stiger",
                "Scott",
                "Tiger"
            ), arrayOf("3", "fbar", "Foo", "Bar"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "1",
                "jdow",
                "John",
                "Dow"
            ), arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            ),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "1",
                "jdow",
                "John",
                "Dow"
            ), arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            ),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "1",
                "jdow",
                "John",
                "Dow"
            ), arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            ),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            ),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "1",
                "jdow",
                "John",
                "Dow"
            ), arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            ),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "1",
                "jdow",
                "John",
                "Dow"
            ), arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            ),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "1",
                "jdow",
                "John",
                "Dow"
            ), arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            ),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"), arrayOf(
                "1",
                "jdow",
                "John",
                "Dow"
            ), arrayOf("1", "jdow", "John", "Dow"), arrayOf("1", "jdow", "John", "Dow"),
            arrayOf("1", "jdow", "John", "Dow"), arrayOf("2", "stiger", "Scott", "Tiger"), arrayOf(
                "3",
                "fbar",
                "Foo",
                "Bar"
            )
        )
        val myPdfDocument = Document()
        try{
            val filePathPDF = File(
                Environment.getExternalStorageDirectory().toString() + "/SamplePDF.pdf"
            )
            PdfWriter.getInstance(myPdfDocument, FileOutputStream(filePathPDF));
            myPdfDocument.open()
            val paragraph = Paragraph("Sample Testing PDF")
            paragraph.setLeading(1F, 1F);
            paragraph.setAlignment(Element.ALIGN_CENTER);

            val table = PdfPTable(headers.size)
            for (header in headers) {
                val cell = PdfPCell()
                //cell.grayFill = 0.9f
                cell.phrase = Phrase(header.toUpperCase())
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell)
            }
            table.completeRow()
            for (row in rows) {
                for (data in row) {
                    table.addCell(PdfPCell(Phrase(data.toString())))
                }
                table.completeRow()
            }
            myPdfDocument.addTitle("PDF Table Demo");
            paragraph.add(table)
            myPdfDocument.add(paragraph);
            Toast.makeText(this, "Generate PDF successfully", Toast.LENGTH_SHORT).show();
        }catch (e: Exception) {
            e.printStackTrace()
        } finally {
            myPdfDocument.close();
        }
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 =
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        val permission2 =
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE!!
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    //Start() is called when the buttons is pressed.
     fun start(view: View){
        Log.d("Click", "start: ")
        val builder = NotificationCompat.Builder(this,channelId)
                .setContentTitle("Download Task")
                .setContentText("Downloading your file...")
                .setSmallIcon(R.drawable.ic_baseline_file_download_24)

        val max = 10
        var progress = 0
        var percentage = 0
        val handler = Handler()

        with(NotificationManagerCompat.from(this)) {
            builder.setProgress(max, progress, true)
            notify(notificationId, builder.build())
            generateExcel()
            Thread(Runnable {
                while (progress < max) {
                    progress += 1

                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    handler.post(Runnable {
                        if (progress == max) {
                            builder.setContentText("Download complete.")
                            builder.setProgress(0, 0, false)
//                            val intent = Intent(Intent.ACTION_GET_CONTENT)
//                            intent.addCategory(Intent.CATEGORY_OPENABLE)
//                            val excelFile = FileOutputStream(filePath)
//                            val uri = Uri.parse(excelFile.toString())
//                            intent.data = uri
//                            intent.type = "*/*"
//                            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
//                            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
//                            startActivity(Intent.createChooser(intent, "Open folder"))
                           // startActivity( Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        } else {
                            // Calculate the percentage comple
                            percentage = (progress * 100) / max
                            builder.setContentText("$percentage% complete $progress of $max")
                            builder.setProgress(max, progress, true)
                        }
                        notify(notificationId, builder.build())
                    })
                }
            }).start()
        }
    }

    fun createNotificationChannel(channelId:String) {
        // Create the NotificationChannel, but only on API 26+ (Android 8.0) because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "My Channel"
            val channelDescription = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId,name,importance)
            channel.apply {
                description = channelDescription
            }

            // Finally register the channel with system
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}