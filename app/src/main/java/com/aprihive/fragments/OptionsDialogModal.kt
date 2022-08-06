package com.aprihive.fragments

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aprihive.R

class OptionsDialogModal {
    var dialog: Dialog? = null
    var optionView1: TextView
    var optionView2: TextView
    var optionView3: TextView
    var optionView4: TextView
    var optionView5: TextView
    var action1: Runnable? = null
    var action2: Runnable? = null
    var action3: Runnable? = null
    var action4: Runnable? = null
    var action5: Runnable? = null

    constructor(context: Context?) {
        dialog = Dialog(context!!)
        dialog!!.setContentView(R.layout.custom_option_dialog)
        dialog!!.window!!.setBackgroundDrawable(null)
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        optionView1 = dialog!!.findViewById(R.id.option1)
        optionView2 = dialog!!.findViewById(R.id.option2)
        optionView3 = dialog!!.findViewById(R.id.option3)
        optionView4 = dialog!!.findViewById(R.id.option4)
        optionView5 = dialog!!.findViewById(R.id.option5)
    }

    constructor(context: Context?, option1: String?, action1: Runnable?, option2: String?, action2: Runnable?) {
        dialog = Dialog(context!!)
        dialog!!.setContentView(R.layout.custom_option_dialog)

        dialog!!.window!!.setBackgroundDrawable(null)
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        optionView1 = dialog!!.findViewById(R.id.option1)
        optionView2 = dialog!!.findViewById(R.id.option2)
        optionView3 = dialog!!.findViewById(R.id.option3)
        optionView4 = dialog!!.findViewById(R.id.option4)
        optionView5 = dialog!!.findViewById(R.id.option5)

        this.action1 = action1
        this.action2 = action2

        optionView1.text = option1
        optionView2.text = option2
        optionView1.visibility = View.VISIBLE
        optionView2.visibility = View.VISIBLE

        optionView1.setOnClickListener(listener)
        optionView2.setOnClickListener(listener)

        dialog!!.show()
    }

    constructor(context: Context?, option1: String?, action1: Runnable?, option2: String?, action2: Runnable?, option3: String?, action3: Runnable?) {
        dialog = Dialog(context!!)
        dialog!!.setContentView(R.layout.custom_option_dialog)
        dialog!!.window!!.setBackgroundDrawable(null)
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        optionView1 = dialog!!.findViewById(R.id.option1)
        optionView2 = dialog!!.findViewById(R.id.option2)
        optionView3 = dialog!!.findViewById(R.id.option3)
        optionView4 = dialog!!.findViewById(R.id.option4)
        optionView5 = dialog!!.findViewById(R.id.option5)
        this.action1 = action1
        this.action2 = action2
        this.action3 = action3
        optionView1.text = option1
        optionView2.text = option2
        optionView3.text = option3
        optionView1.visibility = View.VISIBLE
        optionView2.visibility = View.VISIBLE
        optionView3.visibility = View.VISIBLE
        optionView1.setOnClickListener(listener)
        optionView2.setOnClickListener(listener)
        optionView3.setOnClickListener(listener)
        dialog!!.show()
    }

    constructor(context: Context?, option1: String?, action1: Runnable?, option2: String?, action2: Runnable?, option3: String?, action3: Runnable?, option4: String?, action4: Runnable?) {
        dialog = Dialog(context!!)
        dialog!!.setContentView(R.layout.custom_option_dialog)
        dialog!!.window!!.setBackgroundDrawable(null)
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        optionView1 = dialog!!.findViewById(R.id.option1)
        optionView2 = dialog!!.findViewById(R.id.option2)
        optionView3 = dialog!!.findViewById(R.id.option3)
        optionView4 = dialog!!.findViewById(R.id.option4)
        optionView5 = dialog!!.findViewById(R.id.option5)
        this.action1 = action1
        this.action2 = action2
        this.action3 = action3
        this.action4 = action4
        optionView1.text = option1
        optionView2.text = option2
        optionView3.text = option3
        optionView4.text = option4
        optionView1.visibility = View.VISIBLE
        optionView2.visibility = View.VISIBLE
        optionView3.visibility = View.VISIBLE
        optionView4.visibility = View.VISIBLE
        optionView1.setOnClickListener(listener)
        optionView2.setOnClickListener(listener)
        optionView3.setOnClickListener(listener)
        optionView4.setOnClickListener(listener)
        dialog!!.show()
    }

    constructor(context: Context?, option1: String?, action1: Runnable?, option2: String?, action2: Runnable?, option3: String?, action3: Runnable?, option4: String?, action4: Runnable?, option5: String?, action5: Runnable?) {
        dialog = Dialog(context!!)
        dialog!!.setContentView(R.layout.custom_option_dialog)
        dialog!!.window!!.setBackgroundDrawable(null)
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        optionView1 = dialog!!.findViewById(R.id.option1)
        optionView2 = dialog!!.findViewById(R.id.option2)
        optionView3 = dialog!!.findViewById(R.id.option3)
        optionView4 = dialog!!.findViewById(R.id.option4)
        optionView5 = dialog!!.findViewById(R.id.option5)
        this.action1 = action1
        this.action2 = action2
        this.action3 = action3
        this.action4 = action4
        this.action5 = action5
        optionView1.text = option1
        optionView2.text = option2
        optionView3.text = option3
        optionView4.text = option4
        optionView5.text = option5
        optionView1.visibility = View.VISIBLE
        optionView2.visibility = View.VISIBLE
        optionView3.visibility = View.VISIBLE
        optionView4.visibility = View.VISIBLE
        optionView5.visibility = View.VISIBLE
        optionView1.setOnClickListener(listener)
        optionView2.setOnClickListener(listener)
        optionView3.setOnClickListener(listener)
        optionView4.setOnClickListener(listener)
        optionView5.setOnClickListener(listener)
        dialog!!.show()
    }

    var listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.option1 -> action1!!.run()
            R.id.option2 -> action2!!.run()
            R.id.option3 -> action3!!.run()
            R.id.option4 -> action4!!.run()
            R.id.option5 -> action5!!.run()
            else -> {}
        }
        dialog!!.dismiss()
    }
}