package com.wangzhen.player

import android.widget.FrameLayout
import com.wangzhen.player.ui.PlayerView

/**
 * player manager
 * Created by wangzhen on 12/4/20.
 */
class PlayerManager {
    companion object {
        fun play(url: String, container: FrameLayout) {
            val context = container.context

            val playerView = PlayerView(context)
            playerView.play(url)

            container.removeAllViews()
            container.addView(
                playerView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
    }
}