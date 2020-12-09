package com.wangzhen.player.controller

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.wangzhen.player.R
import com.wangzhen.player.type.PlayerState

/**
 * UIController
 * Created by wangzhen on 12/4/20.
 */
class UIController(private val container: FrameLayout) : Controller() {
    private var isPlaying: Boolean = false
    private lateinit var rootView: View
    private lateinit var containerPlaying: View
    private lateinit var btnReplay: ImageView
    private lateinit var bufferLoading: View
    private lateinit var btnRetry: View
    private lateinit var seekBar: SeekBar

    private val handler = Handler(Looper.getMainLooper())

    override fun run() {
        rootView = View.inflate(container.context, R.layout.player_ui_controller_layout, null)
        container.addView(rootView)
        initViews(rootView)
        playerView?.player?.let { player ->
            player.removeListener(eventListener)
            player.addListener(eventListener)
        }
    }

    private fun initViews(view: View) {
        view.setOnClickListener {
            if (isPlaying) {
                containerPlaying.visibility = if (containerPlaying.visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE
                checkScheduleDisappear()
            }
        }
        containerPlaying = view.findViewById<View>(R.id.container_playing).apply {
            findViewById<ImageView>(R.id.btn_play_pause).apply {
                setOnClickListener {
                    playerView?.player?.let { player ->
                        if (player.playWhenReady) {
                            playerView?.pause()
                            setImageResource(R.mipmap.module_player_controls_play)
                        } else {
                            playerView?.resume()
                            setImageResource(R.mipmap.module_player_controls_pause)
                        }
                    }
                }
            }
            findViewById<SeekBar>(R.id.player_seek_bar).apply {
                seekBar = this
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        unScheduleDisappear()
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        scheduleDisappear()
                        playerView?.player?.let { player ->
                            player.seekTo(player.duration * seekBar.progress / seekBar.max)
                        }
                    }
                })
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

        hideAll()
        updateState(PlayerState.BUFFERING)
    }

    private fun updateState(state: Int) {
        hideAll()
        isPlaying = false
        when (state) {
            PlayerState.BUFFERING -> {
                bufferLoading.visibility = View.VISIBLE
            }
            PlayerState.READY -> {
                isPlaying = true
                containerPlaying.visibility = View.VISIBLE
                checkScheduleDisappear()
            }
            PlayerState.END -> {
                btnReplay.visibility = View.VISIBLE
            }
            PlayerState.ERROR -> {
                btnRetry.visibility = View.VISIBLE
            }
        }
    }

    private fun checkScheduleDisappear() {
        playerView?.player?.let {
            if (it.playWhenReady) {
                scheduleDisappear()
            } else {
                unScheduleDisappear()
            }
        }
    }

    private fun unScheduleDisappear() {
        handler.removeCallbacks(disappearRunnable)
    }

    private fun scheduleDisappear() {
        unScheduleDisappear()
        handler.postDelayed(disappearRunnable, 5000L)
    }

    private fun hideAll() {
        bufferLoading.visibility = View.GONE
        containerPlaying.visibility = View.GONE
        btnReplay.visibility = View.GONE
        btnRetry.visibility = View.GONE
    }

    private val disappearRunnable = Runnable {
        containerPlaying.visibility = View.GONE
    }

    private val lifecycleListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {

        }

        override fun onViewDetachedFromWindow(v: View) {
            unScheduleDisappear()
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
}