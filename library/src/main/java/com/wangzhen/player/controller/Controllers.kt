package com.wangzhen.player.controller

/**
 * Controllers
 * Created by wangzhen on 12/7/20.
 */
class Controllers {
    private val list = arrayListOf<Controller>()

    fun enqueue(task: Controller): Controllers {
        list.add(task)
        return this
    }

    fun getItems(): ArrayList<Controller> {
        return list
    }

    fun run() {
        list.forEach {
            it.run()
        }
    }
}