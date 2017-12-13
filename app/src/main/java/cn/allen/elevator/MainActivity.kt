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
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Author: AllenWen
 * CreateTime: 2017/12/6
 * Email: wenxueguo@medlinker.com
 * Description:
 */

class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
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
    private var currentIndex: Int = floorNum - 1
    private var elevatorRunner: Disposable? = null//运行电梯
    private var taskRunner: Disposable? = null//派发任务
    private var canTakeNew: Boolean = true

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
        if (elevatorRunner != null && !elevatorRunner!!.isDisposed) elevatorRunner!!.dispose()
        canTakeNew = false
        start.isEnabled = true
        stop.isEnabled = false
    }

    /**
     * 启动电梯
     */
    private fun startElevator() {
        canTakeNew = true
        taskRunner = Flowable.interval(4000, TimeUnit.MILLISECONDS)
                .filter { canTakeNew }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    canTakeNew = false
                    //真实世界楼层
                    val floor: Int = Random().nextInt(floorNum) + 1
                    var destination: Int = Random().nextInt(floorNum) + 1
                    //不允许当前楼层和目的楼层相同
                    while (floor == destination) {
                        destination = Random().nextInt(floorNum) + 1
                    }
                    //实际楼层index
                    val floorIndex: Int = floorNum - floor
                    val destinationIndex: Int = floorNum - destination
                    //计算出运行方向
                    val direction: Int = if (destinationIndex - floorIndex > 0) 1 else 0
                    showToast("现在有人在 $floor 楼，他想去 $destination 楼")
                    //执行任务
                    executeTask(EleTask(floor, floorIndex, destination, destinationIndex, direction))
                }
                .doOnNext { mAdapter.notifyDataSetChanged() }
                .subscribe()

        start.isEnabled = false
        stop.isEnabled = true
    }

    private fun executeTask(task: EleTask) {
        when {
            currentIndex - task.floorIndex > 0 -> //向上接人
                mList[currentIndex].direction = 0
            currentIndex - task.floorIndex < 0 -> //向下接人
                mList[currentIndex].direction = 1
            else -> {

            }
        }
        //电梯运行
        elevatorRunner = Flowable.interval(750, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    //如果送到，就停止运行
                    if (currentIndex == task.destinationIndex && task.isPickup) {
                        showToast("已将此人从 ${task.floor} 楼送到 ${task.destination} 楼")
                        elevatorRunner!!.dispose()
                        canTakeNew = true
                        false
                    } else {
                        true
                    }
                }
                .doOnNext {
                    //边界控制
                    if (currentIndex > floorNum - 1) {
                        currentIndex = floorNum - 1
                    }
                    if (currentIndex < 0) {
                        currentIndex = 0
                    }
                    //到达边界后方向调整
                    val elevator: Elevator = mList[currentIndex]
                    if (currentIndex == floorNum - 1) {
                        elevator.direction = 0
                    } else if (currentIndex == 0) {
                        elevator.direction = 1
                    }
                    mList[currentIndex].isRunning = false
                    //计划电梯的下一步
                    if (currentIndex == task.floorIndex) {//如果到达乘客的位置
                        task.isPickup = true
                        if (task.direction == 0) {
                            currentIndex -= 1
                        } else {
                            currentIndex += 1
                        }
                        mList[currentIndex].isRunning = true
                        mList[currentIndex].direction = task.direction
                    } else {//尚未到达乘客的位置
                        if (elevator.direction == 0) {
                            currentIndex -= 1
                        } else {
                            currentIndex += 1
                        }
                        mList[currentIndex].isRunning = true
                        mList[currentIndex].direction = elevator.direction
                    }
                }
                .doOnNext { mAdapter.notifyDataSetChanged() }
                .subscribe()
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
        currentIndex = floorNum - 1
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
