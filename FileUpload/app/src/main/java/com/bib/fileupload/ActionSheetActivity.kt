package com.bib.fileupload

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.main_content.*

class ActionSheetActivity : AppCompatActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_sheet)

        sheetBehavior = BottomSheetBehavior.from<LinearLayout>(bottom_sheet)

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        btBottomSheet.text = "Close Bottom Sheet"
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        btBottomSheet.text = "Expand Bottom Sheet"
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

//        btBottomSheet.setOnClickListener(View.OnClickListener {
//            expandCloseSheet()
//        })

        btBottomSheetDialog.setOnClickListener(View.OnClickListener {
//            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
////            view.setBackgroundColor(Color.GREEN)
//            val dialog = BottomSheetDialog(this)
//            dialog.setContentView(view)
//            dialog.show()
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)

            val share = bottomSheetDialog.findViewById<LinearLayout>(R.id.bottom_sheet_dialog_share)
            val upload = bottomSheetDialog.findViewById<LinearLayout>(R.id.bottom_sheet_dialog_upload)
            val download = bottomSheetDialog.findViewById<LinearLayout>(R.id.bottom_sheet_dialog_download)
            bottomSheetDialog.show()

            share?.setOnClickListener {
                Toast.makeText(getApplicationContext(), "Share Click", Toast.LENGTH_LONG).show()
                bottomSheetDialog.dismiss();
            }
            upload?.setOnClickListener {
                Toast.makeText(getApplicationContext(), "Upload Click", Toast.LENGTH_LONG).show()
                bottomSheetDialog.dismiss();
            }

            download?.setOnClickListener {
                Toast.makeText(getApplicationContext(), "download Click", Toast.LENGTH_LONG).show()
                bottomSheetDialog.dismiss();
            }
        })
    }

//    private fun expandCloseSheet() {
//        if (sheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
//            sheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
//            btBottomSheet.text = "Close Bottom Sheet"
//        } else {
//            sheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
//            btBottomSheet.text = "Expand Bottom Sheet"
//        }
//    }
}