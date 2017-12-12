package cn.allen.elevator

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RadioGroup
import cn.allen.elevator.Extension.showToast
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import java.util.concurrent.TimeUnit

/**
 * Author: AllenWen
 * CreateTime: 2017/12/6
 * Email: wenxueguo@medlinker.com
 * Description:
 */

class MainActivity : AppCompatActivity() {
    private var elevatorNum: Int = 0
    private var floorNum: Int = 10
    private var mList: MutableList<Elevator> = MutableList(floorNum, {
        if (it == 0) {
            Elevator(it + 1, true, 0)
        } else {
            Elevator(it + 1, false, 0)
        }
    }).asReversed()
    private var mAdapter: FloorAdapter = FloorAdapter(mList)
    private var currentFloor: Int = floorNum - 1
    private var disposable: Disposable? = null

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
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
    }

    /**
     * 停止电梯
     */
    private fun stopElevator() {
        if (disposable != null && !disposable!!.isDisposed) disposable!!.dispose()
        start.isEnabled=true
        stop.isEnabled=false
    }

    /**
     * 启动电梯
     */
    private fun startElevator() {
        //电梯运行
        disposable = Flowable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (currentFloor > floorNum - 1) {
                        currentFloor = floorNum - 1
                    }
                    if (currentFloor < 0) {
                        currentFloor = 0
                    }
                    var elevator: Elevator = mList[currentFloor]
                    if (currentFloor == floorNum - 1) {
                        elevator.direction = 0
                    } else if (currentFloor == 0) {
                        elevator.direction = 1
                    }
                    mList[currentFloor].isRunning = false
                    if (elevator.direction == 0) {
                        currentFloor -= 1
                    } else {
                        currentFloor += 1
                    }
                    mList[currentFloor].isRunning = true
                    mList[currentFloor].direction = elevator.direction
                }
                .doOnNext { mAdapter.notifyDataSetChanged() }
                .subscribe()

//        var random: Random = Random()
//        while (true){
//            sleep(500)
//            var s: Int = random.nextInt(floorNum)+1
//            println(s)
//        }

        start.isEnabled=false
        stop.isEnabled=true
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
        mList.clear()
        mList.addAll(MutableList(floorNum, {
            if (it == 0) {
                Elevator(it + 1, true, 0)
            } else {
                Elevator(it + 1, false, 0)
            }
        }).asReversed())
        mAdapter.notifyDataSetChanged()
        currentFloor = floorNum - 1
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
