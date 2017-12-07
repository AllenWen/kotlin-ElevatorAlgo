package cn.allen.elevator

import android.content.Context
import android.widget.Toast

/**
 * Author: AllenWen
 * CreateTime: 2017/12/7
 * Email: wenxueguo@medlinker.com
 * Description:常用扩展函数
 */
class Extension {
    fun Context.toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Context.toast(msg: Int) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}