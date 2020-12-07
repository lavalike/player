package com.wangzhen.player.controller

import com.wangzhen.player.ui.PlayerView

/**
 * Controller
 * Created by wangzhen on 12/4/20.
 */
abstract class Controller {
    var playerView: PlayerView? = null
    abstract fun run()
}