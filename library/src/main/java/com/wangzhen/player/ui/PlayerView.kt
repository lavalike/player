package com.wangzhen.player.ui

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceView
import android.widget.FrameLayout
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.video.VideoListener

/**
 * player view
 * Created by wangzhen on 12/4/20.
 */
class PlayerView : FrameLayout {
    private var container: AspectRatioFrameLayout? = null
    private var surfaceView: SurfaceView? = null
    var player: SimpleExoPlayer? = null
    private var url: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        setBackgroundColor(Color.BLACK)

        container = AspectRatioFrameLayout(context).apply {
            addView(SurfaceView(context).apply {
                surfaceView = this
            }, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        this@PlayerView.addView(
            container,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            })
    }

    fun play(url: String) {
        this.url = url
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(context)
        }
        player?.let { plr ->
            surfaceView?.let {
                plr.setVideoSurfaceView(it)
                plr.addVideoListener(listener)
            }
            plr.prepare(buildMediaSource(Uri.parse(url)))
            plr.playWhenReady = true
            plr.seekTo(0)
        }
    }

    fun replay() {
        this.url?.let { play(it) }
    }

    fun pause() {
        player?.playWhenReady = false
    }

    fun resume() {
        player?.playWhenReady = true
    }

    fun stop() {
        player?.let {
            it.removeVideoListener(listener)
            it.clearVideoSurfaceView(surfaceView)
            it.release()
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("exoplayer"))
            .createMediaSource(uri)
    }

    private val listener = object : VideoListener {
        override fun onVideoSizeChanged(
            width: Int,
            height: Int,
            unappliedRotationDegrees: Int,
            pixelWidthHeightRatio: Float
        ) {
            super.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio)
            container?.setAspectRatio(
                if (height == 0) {
                    1f
                } else {
                    (width * pixelWidthHeightRatio) / height
                }
            )
        }
    }
}