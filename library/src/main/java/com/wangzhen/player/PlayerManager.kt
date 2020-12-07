package com.wangzhen.player

import android.widget.FrameLayout
import com.wangzhen.player.controller.Controllers
import com.wangzhen.player.controller.UIController
import com.wangzhen.player.ui.PlayerView

/**
 * player manager
 * Created by wangzhen on 12/4/20.
 */
class PlayerManager {
    companion object {
        var playerView: PlayerView? = null

        fun play(url: String, container: FrameLayout) {
            play(url, container, null)
        }

        fun play(url: String, container: FrameLayout, controllers: Controllers?) {
            container.removeAllViews()
            container.addView(
                PlayerView(container.context).apply {
                    playerView = this
                    play(url)
                },
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            var localControllers = controllers
            if (localControllers == null) {
                localControllers = Controllers().apply {
                    enqueue(UIController(container))
                }
            }
            localControllers.setPlayerView(playerView).run()
        }
    }
}