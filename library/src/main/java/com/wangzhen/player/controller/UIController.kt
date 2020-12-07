package com.wangzhen.player.controller

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.exoplayer2.Player
import com.wangzhen.player.R

/**
 * UIController
 * Created by wangzhen on 12/4/20.
 */
class UIController(private val container: FrameLayout) : Controller() {
    private var isPlaying: Boolean = true
    private lateinit var btnPlayPause: ImageView

    override fun run() {
        val view = View.inflate(container.context, R.layout.player_ui_controller_layout, null)
        container.addView(view)
        view.findViewById<ImageView>(R.id.btn_play_pause).apply {
            btnPlayPause = this
            setOnClickListener {
                if (isPlaying) {
                    playerView?.pause()
                    setImageResource(R.mipmap.module_player_controls_play)
                } else {
                    playerView?.resume()
                    setImageResource(R.mipmap.module_player_controls_pause)
                }
                isPlaying = !isPlaying
            }
        }

        playerView?.player?.let { player ->
            player.removeListener(listener)
            player.addListener(listener)
        }
    }

    private val listener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            when (playbackState) {
                Player.STATE_BUFFERING -> {

                }
                Player.STATE_READY -> {

                }
                Player.STATE_ENDED -> {
                    playerView?.stop()
                    btnPlayPause.setImageResource(R.mipmap.module_player_controls_play)
                }
            }
        }
    }
}