package com.wangzhen.player.controller

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.wangzhen.player.R
import com.wangzhen.player.type.PlayerState
import com.wangzhen.player.utils.FormatUtils

/**
 * default ui controller
 * Created by wangzhen on 12/4/20.
 */
class DefaultUIController : Controller() {
    private lateinit var rootView: View
    private lateinit var containerPlaying: View
    private lateinit var btnReplay: ImageView
    private lateinit var btnPlayPause: ImageView
    private lateinit var bufferLoading: View
    private lateinit var btnRetry: View
    private lateinit var seekBar: SeekBar
    private lateinit var playerTime: TextView
    private var isPlaying: Boolean = false

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
                checkScheduleTask()
            }
        }
        containerPlaying = view.findViewById<View>(R.id.container_playing).apply {
            findViewById<ImageView>(R.id.btn_play_pause).apply {
                btnPlayPause = this
                setOnClickListener {
                    playerView?.player?.let { player ->
                        if (player.playWhenReady) {
                            performPause()
                        } else {
                            performResume()
                        }
                    }
                }
            }
            findViewById<SeekBar>(R.id.player_seek_bar).apply {
                seekBar = this
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        playerView?.player?.let { player ->
                            playerTime.text =
                                FormatUtils.formatDuration(player.duration * seekBar.progress / seekBar.max)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        unScheduleDisappear()
                        unScheduleProgress()
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        scheduleDisappear()
                        scheduleProgress()
                        playerView?.player?.let { player ->
                            player.seekTo(player.duration * seekBar.progress / seekBar.max)
                            performResume()
                        }
                    }
                })
            }
            findViewById<TextView>(R.id.tv_player_time).apply {
                playerTime = this
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

    private fun performResume() {
        playerView?.resume()
        btnPlayPause.setImageResource(R.mipmap.module_player_controls_pause)
    }

    private fun performPause() {
        playerView?.pause()
        btnPlayPause.setImageResource(R.mipmap.module_player_controls_play)
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
                checkScheduleTask()
            }
            PlayerState.END -> {
                btnReplay.visibility = View.VISIBLE
            }
            PlayerState.ERROR -> {
                btnRetry.visibility = View.VISIBLE
            }
        }
    }

    private fun checkScheduleTask() {
        playerView?.player?.let {
            if (it.playWhenReady) {
                scheduleDisappear()
                scheduleProgress()
            } else {
                unScheduleDisappear()
                unScheduleProgress()
            }
        }
    }

    private fun unScheduleDisappear() {
        handler.removeMessages(MSG_DISAPPEAR)
    }

    private fun scheduleDisappear() {
        unScheduleDisappear()
        handler.sendEmptyMessageDelayed(MSG_DISAPPEAR, 5000L)
    }

    private fun unScheduleProgress() {
        handler.removeMessages(MSG_UPDATE_PROGRESS)
    }

    private fun scheduleProgress() {
        unScheduleProgress()
        handler.sendEmptyMessage(MSG_UPDATE_PROGRESS)
    }

    private fun hideAll() {
        bufferLoading.visibility = View.GONE
        containerPlaying.visibility = View.GONE
        btnReplay.visibility = View.GONE
        btnRetry.visibility = View.GONE
    }

    private val lifecycleListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {

        }

        override fun onViewDetachedFromWindow(v: View) {
            unScheduleDisappear()
            unScheduleProgress()
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

        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)
            scheduleProgress()
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            super.onTimelineChanged(timeline, manifest, reason)
            scheduleProgress()
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            super.onPlayerError(error)
            updateState(PlayerState.ERROR)
        }
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_DISAPPEAR -> {
                    containerPlaying.visibility = View.GONE
                }
                MSG_UPDATE_PROGRESS -> {
                    playerView?.player?.let { player ->
                        val progress =
                            (player.currentPosition * seekBar.max / player.duration + 0.5f).toInt()
                        seekBar.progress = progress
                        if (player.currentPosition < player.duration) {
                            sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 1000L)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MSG_DISAPPEAR = 0
        const val MSG_UPDATE_PROGRESS = 1
    }
}