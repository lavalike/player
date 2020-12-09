package com.wangzhen.player.controller

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoListener
import com.wangzhen.player.R
import com.wangzhen.player.type.PlayerState

/**
 * UIController
 * Created by wangzhen on 12/4/20.
 */
class UIController(private val container: FrameLayout) : Controller() {
    private var isPlaying: Boolean = false
    private lateinit var btnPlayPause: ImageView
    private lateinit var btnReplay: ImageView
    private lateinit var bufferLoading: View
    private lateinit var btnRetry: View

    override fun run() {
        val view = View.inflate(container.context, R.layout.player_ui_controller_layout, null)
        container.addView(view)
        initViews(view)
        hideAll()
        updateState(PlayerState.BUFFERING)
        playerView?.player?.let { player ->
            player.removeListener(eventListener)
            player.addListener(eventListener)
            player.removeVideoListener(videoListener)
            player.addVideoListener(videoListener)
        }
    }

    private fun initViews(view: View) {
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
        view.findViewById<ImageView>(R.id.btn_replay).apply {
            btnReplay = this
            setOnClickListener {
                updateState(Player.STATE_BUFFERING)
                playerView?.replay()
            }
        }
        view.findViewById<View>(R.id.buffer_loading).apply {
            bufferLoading = this
        }
        view.findViewById<View>(R.id.btn_retry).apply {
            btnRetry = this
            setOnClickListener {
                updateState(Player.STATE_BUFFERING)
                playerView?.replay()
            }
        }
        view.addOnAttachStateChangeListener(lifecycleListener)
        updateState(Player.STATE_BUFFERING)
    }

    private fun updateState(state: Int) {
        hideAll()
        when (state) {
            PlayerState.BUFFERING -> {
                bufferLoading.visibility = View.VISIBLE
            }
            PlayerState.READY -> {
                btnPlayPause.visibility = View.VISIBLE
            }
            PlayerState.END -> {
                isPlaying = false
                btnReplay.visibility = View.VISIBLE
            }
            PlayerState.ERROR -> {
                isPlaying = false
                btnRetry.visibility = View.VISIBLE
            }
        }
    }

    private fun hideAll() {
        bufferLoading.visibility = View.GONE
        btnPlayPause.visibility = View.GONE
        btnReplay.visibility = View.GONE
        btnRetry.visibility = View.GONE
    }

    private val lifecycleListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {

        }

        override fun onViewDetachedFromWindow(v: View) {
            playerView?.stop()
        }

    }

    private val eventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            when (playbackState) {
                Player.STATE_BUFFERING -> updateState(PlayerState.BUFFERING)
                Player.STATE_READY -> updateState(PlayerState.READY)
                Player.STATE_ENDED -> updateState(PlayerState.END)
            }
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            super.onPlayerError(error)
            updateState(PlayerState.ERROR)
        }
    }

    private val videoListener = object : VideoListener {
        override fun onRenderedFirstFrame() {
            isPlaying = true
        }
    }
}