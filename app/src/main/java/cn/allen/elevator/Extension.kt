package cn.allen.elevator

import android.app.Activity
import android.widget.Toast

/**
 * Author: AllenWen
 * CreateTime: 2017/12/7
 * Email: wenxueguo@medlinker.com
 * Description:常用扩展函数
 */
object Extension {

    fun Activity.showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Activity.showToast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Activity.showLongToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}