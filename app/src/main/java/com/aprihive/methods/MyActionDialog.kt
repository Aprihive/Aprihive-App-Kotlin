package com.aprihive.methods

import android.app.Dialog
import com.aprihive.R
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.aprihive.methods.MySnackBar
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences
import android.view.View
import android.widget.ImageView

class MyActionDialog {
    private val dialog: Dialog

    constructor(context: Context?, title: String?, msg: String?, image: Int, action: Runnable) {
        dialog = Dialog(context!!)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window!!.setBackgroundDrawable(null)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        val actionText = dialog.findViewById<TextView>(R.id.dialogText)
        val actionTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val actionImage = dialog.findViewById<ImageView>(R.id.dialogImage)
        val positiveBtn = dialog.findViewById<TextView>(R.id.positiveButton)
        val negativeBtn = dialog.findViewById<TextView>(R.id.negativeButton)
        actionText.text = msg
        actionTitle.text = title
        actionImage.setImageResource(image)
        positiveBtn.setOnClickListener {
            action.run()
            dialog.dismiss()
        }
        negativeBtn.setOnClickListener { dialog.dismiss() }
    }

    constructor(context: Context, title: String?, msg: String?, image: Int, action: Runnable, btnText: String?, btnColor: Int) {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window!!.setBackgroundDrawable(null)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(true)
        val actionText = dialog.findViewById<TextView>(R.id.dialogText)
        val actionTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val actionImage = dialog.findViewById<ImageView>(R.id.dialogImage)
        val positiveBtn = dialog.findViewById<TextView>(R.id.positiveButton)
        val negativeBtn = dialog.findViewById<TextView>(R.id.negativeButton)
        actionText.text = msg
        actionTitle.text = title
        actionImage.setImageResource(image)
        positiveBtn.text = btnText
        positiveBtn.setBackgroundColor(context.resources.getColor(btnColor))
        negativeBtn.visibility = View.GONE
        positiveBtn.setOnClickListener {
            action.run()
            dialog.dismiss()
        }
        negativeBtn.setOnClickListener { dialog.dismiss() }
    }

    //get notification
    constructor(context: Context, title: String?, msg: String?, image: Int, imageColor: Int, action: Runnable, btnText: String?, cancelable: Boolean?) {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window!!.setBackgroundDrawable(null)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(cancelable!!)
        val actionText = dialog.findViewById<TextView>(R.id.dialogText)
        val actionTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val actionImage = dialog.findViewById<ImageView>(R.id.dialogImage)
        val positiveBtn = dialog.findViewById<TextView>(R.id.positiveButton)
        val negativeBtn = dialog.findViewById<TextView>(R.id.negativeButton)
        actionText.text = msg
        actionTitle.text = title
        actionImage.setColorFilter(context.resources.getColor(imageColor))
        actionImage.setImageResource(image)
        positiveBtn.text = btnText
        positiveBtn.setBackgroundColor(context.resources.getColor(R.color.color_text_blue_500))
        negativeBtn.visibility = View.GONE
        positiveBtn.setOnClickListener {
            action.run()
            dialog.dismiss()
        }
        negativeBtn.setOnClickListener { dialog.dismiss() }
    }

    constructor(context: Context?, title: String?, msg: String?, image: Int, action: Runnable, positiveBtnText: String?, negativeBtnText: String?) {
        dialog = Dialog(context!!)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window!!.setBackgroundDrawable(null)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(true)
        val actionText = dialog.findViewById<TextView>(R.id.dialogText)
        val actionTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val actionImage = dialog.findViewById<ImageView>(R.id.dialogImage)
        val positiveBtn = dialog.findViewById<TextView>(R.id.positiveButton)
        val negativeBtn = dialog.findViewById<TextView>(R.id.negativeButton)
        actionText.text = msg
        actionTitle.text = title
        actionImage.setImageResource(image)
        positiveBtn.text = positiveBtnText
        negativeBtn.text = negativeBtnText
        positiveBtn.setOnClickListener {
            action.run()
            dialog.dismiss()
        }
        negativeBtn.setOnClickListener { dialog.dismiss() }
    }

    //with icon color
    constructor(context: Context, title: String?, msg: String?, image: Int, imageColor: Int, action: Runnable, positiveBtnText: String?, negativeBtnText: String?) {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window!!.setBackgroundDrawable(null)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(true)
        val actionText = dialog.findViewById<TextView>(R.id.dialogText)
        val actionTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val actionImage = dialog.findViewById<ImageView>(R.id.dialogImage)
        val positiveBtn = dialog.findViewById<TextView>(R.id.positiveButton)
        val negativeBtn = dialog.findViewById<TextView>(R.id.negativeButton)
        actionText.text = msg
        actionTitle.text = title
        actionImage.setImageResource(image)
        actionImage.setColorFilter(context.resources.getColor(imageColor))
        positiveBtn.text = positiveBtnText
        negativeBtn.text = negativeBtnText
        positiveBtn.setOnClickListener {
            action.run()
            dialog.dismiss()
        }
        negativeBtn.setOnClickListener { dialog.dismiss() }
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}