package com.wangzhen.player

import android.widget.FrameLayout
import com.wangzhen.player.controller.Controllers
import com.wangzhen.player.controller.DefaultUIController
import com.wangzhen.player.ui.PlayerView

/**
 * player manager
 * Created by wangzhen on 12/4/20.
 */
class PlayerManager {
    companion object {

        fun play(url: String, container: FrameLayout) {
            play(url, container, null)
        }

        fun play(url: String, container: FrameLayout, userControllers: Controllers?) {
            container.removeAllViews()
            val playerView = PlayerView(container.context).apply { play(url) }
            container.addView(
                playerView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            var controllers = userControllers
            if (controllers == null) {
                controllers = Controllers().apply {
                    enqueue(DefaultUIController())
                }
            }
            controllers.apply {
                getItems().forEach {
                    it.container = container
                    it.playerView = playerView
                }
                run()
            }
        }
    }
}