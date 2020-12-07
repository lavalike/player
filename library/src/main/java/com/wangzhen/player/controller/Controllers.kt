package com.wangzhen.player.controller

import com.wangzhen.player.ui.PlayerView

/**
 * Controllers
 * Created by wangzhen on 12/7/20.
 */
class Controllers {
    private val list = arrayListOf<Controller>()
    private var playerView: PlayerView? = null

    fun enqueue(task: Controller): Controllers {
        list.add(task)
        return this
    }

    fun run() {
        list.forEach {
            it.playerView = playerView
            it.run()
        }
    }

    fun setPlayerView(playerView: PlayerView?): Controllers {
        this.playerView = playerView
        return this
    }
}