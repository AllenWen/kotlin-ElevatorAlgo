package cn.allen.elevator

/**
 * Author: AllenWen
 * CreateTime: 2017/12/13
 * Email: wenxueguo@medlinker.com
 * Description: direction 0代表向上，1代表向下
 */
data class EleTask(var floor: Int, var floorIndex: Int, var destination: Int, var destinationIndex: Int, var direction: Int, var isPickup: Boolean = false) {

}