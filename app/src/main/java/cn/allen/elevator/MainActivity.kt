package cn.allen.elevator

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RadioGroup
import cn.allen.elevator.Extension.showToast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find

/**
 * Author: AllenWen
 * CreateTime: 2017/12/6
 * Email: wenxueguo@medlinker.com
 * Description:
 */

class MainActivity : AppCompatActivity() {
    private var elevatorNum: Int = 0
    private var floorNum: Int = 10

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
        start.setOnClickListener { _ -> startElevator() }
        stop.setOnClickListener { _ -> stopElevator() }
    }

    /**
     * 停止电梯
     */
    private fun stopElevator() {

    }

    /**
     * 启动电梯
     */
    private fun startElevator() {

    }

    private fun showSettings() {
        val view = layoutInflater.inflate(R.layout.dialog_settings, null)
        val elevatorView: RadioGroup = view.find(R.id.elevator_num)
        elevatorView.setOnCheckedChangeListener { _, checkedId ->
            elevatorNum = when (checkedId) {
                R.id.elevator_one -> 1
                R.id.elevator_two -> 2
                R.id.elevator_three -> 3
                else -> 0
            }
        }
        val floorView: EditText = view.find(R.id.floor_num)
        val builder = AlertDialog.Builder(this@MainActivity, R.style.dialog_style)
        builder.setTitle(R.string.select_param)
        builder.setView(view)
        builder.setNegativeButton(R.string.cancel, null)
        builder.setPositiveButton(R.string.confirm, { _, _ ->
            val floorStr: String? = floorView.text.trim().toString()
            floorNum = if (floorStr.isNullOrEmpty()) 0 else floorStr!!.toInt()
            if (elevatorNum > 0 && floorNum > 0) {
                resetElevator()
            } else {
                showToast(R.string.set_param_error)
            }
        })
        builder.show()
    }

    private fun resetElevator() {
        stopElevator()
        container.removeAllViews()
        for (i in 0 until elevatorNum) {
            val recyclerView = RecyclerViewCompat(this)
            val list: MutableList<Int> = MutableList(floorNum, { return@MutableList it + 1 }).asReversed()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = FloorAdapter(list)
            container.addView(recyclerView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
