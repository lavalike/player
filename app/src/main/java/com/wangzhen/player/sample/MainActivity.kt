package com.wangzhen.player.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wangzhen.player.PlayerManager

/**
 * MainActivity
 * Created by wangzhen on 12/4/20.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PlayerManager.play(HostConfig.URL, findViewById(R.id.container))
    }
}