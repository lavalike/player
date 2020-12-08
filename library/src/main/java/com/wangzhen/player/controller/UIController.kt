package com.wangzhen.player.controller

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.exoplayer2.Player
import com.wangzhen.player.R

/**
 * UIController
 * Created by wangzhen on 12/4/20.
 */
class UIController(private val container: FrameLayout) : Controller() {
    private var isPlaying: Boolean = false
    private lateinit var btnPlayPause: ImageView
    private lateinit var btnReplay: ImageView
    private lateinit var ivLoading: ImageView

    override fun run() {
        val view = View.inflate(container.context, R.layout.player_ui_controller_layout, null)
        container.addView(view)
        initViews(view)
        playerView?.player?.let { player ->
            player.removeListener(listener)
            player.addListener(listener)
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
                playerView?.replay()
                updateState(Player.STATE_BUFFERING)
            }
        }
        view.findViewById<ImageView>(R.id.iv_loading).apply {
            ivLoading = this
            val animation = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 359f)
            animation.repeatCount = ValueAnimator.INFINITE
            animation.repeatMode = ValueAnimator.RESTART
            animation.interpolator = LinearInterpolator()
            animation.duration = 1000
            animation.start()
        }
        view.removeOnAttachStateChangeListener(lifecycleListener)
        view.addOnAttachStateChangeListener(lifecycleListener)
        updateState(Player.STATE_BUFFERING)
    }

    private fun updateState(state: Int) {
        ivLoading.visibility = View.GONE
        btnPlayPause.visibility = View.GONE
        btnReplay.visibility = View.GONE
        when (state) {
            Player.STATE_BUFFERING -> {
                ivLoading.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                isPlaying = true
                btnPlayPause.visibility = View.VISIBLE
            }
            Player.STATE_ENDED -> {
                isPlaying = false
                btnReplay.visibility = View.VISIBLE
            }
        }
    }

    private val lifecycleListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {

        }

        override fun onViewDetachedFromWindow(v: View) {
            playerView?.stop()
        }

    }

    private val listener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            updateState(playbackState)
        }
    }
}