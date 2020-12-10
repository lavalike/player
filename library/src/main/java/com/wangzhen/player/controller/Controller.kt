package com.wangzhen.player.controller

import android.widget.FrameLayout
import com.wangzhen.player.ui.PlayerView

/**
 * Controller
 * Created by wangzhen on 12/4/20.
 */
abstract class Controller {
    lateinit var container: FrameLayout
    var playerView: PlayerView? = null
    abstract fun run()
}