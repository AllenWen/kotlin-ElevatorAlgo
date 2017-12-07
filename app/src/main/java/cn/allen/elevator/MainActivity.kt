package cn.allen.elevator

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast


/**
 * Author: AllenWen
 * CreateTime: 2017/12/6
 * Email: wenxueguo@medlinker.com
 * Description:
 */

class MainActivity : AppCompatActivity() {

    var mList: MutableList<Int> = MutableList(2, { return@MutableList it })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setOnMenuItemClickListener { item: MenuItem? ->
            if (item?.itemId == R.id.action_settings) showSettings()
            return@setOnMenuItemClickListener true
        }
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = FloorAdapter(mList)
    }

    private fun showSettings() {
        val builder = AlertDialog.Builder(this@MainActivity, R.style.dialog_style)
        builder.setTitle(R.string.select_param)
        val view = layoutInflater.inflate(R.layout.dialog_settings, null)
        val elevator_num: EditText = view.find(R.id.elevator_num)
        val floor_num: EditText = view.find(R.id.floor_num)
        builder.setView(view)
        builder.setNegativeButton(R.string.cancel, null)
        builder.setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { dialog, which ->
            val str1: String = elevator_num.text.trim() as String
            val num1 = str1.toInt()
            val str2: String = floor_num.text.trim() as String
            val num2 = str2.toInt()
            if (num1 > 0 && num2 > 0) {
                //TODO
            } else {
                toast(R.string.set_param_error)
            }
        })
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


}
