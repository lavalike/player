package com.wangzhen.player.ui

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.SurfaceView
import android.widget.FrameLayout
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

/**
 * player view
 * Created by wangzhen on 12/4/20.
 */
class PlayerView : FrameLayout {
    var surfaceView: SurfaceView? = null
    var player: SimpleExoPlayer? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        setBackgroundColor(Color.BLACK)
        addView(SurfaceView(context).apply {
            surfaceView = this
        }, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun play(url: String) {
        val player = ExoPlayerFactory.newSimpleInstance(context)
        surfaceView?.let {
            player.clearVideoSurfaceView(it)
            player.setVideoSurfaceView(it)
        }
        player.prepare(buildMediaSource(Uri.parse(url)))
        player.playWhenReady = true
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("exoplayer"))
            .createMediaSource(uri)
    }
}