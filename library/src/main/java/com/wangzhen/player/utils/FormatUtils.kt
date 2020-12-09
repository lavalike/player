package com.wangzhen.player.utils

/**
 * FormatUtils
 * Created by wangzhen on 2020/12/9.
 */
class FormatUtils {
    companion object {
        fun formatDuration(timeMs: Long): String {
            if (timeMs < 0) {
                return "00:00"
            }
            val totalSeconds = ((timeMs + 500) / 1000).toInt()
            val seconds = totalSeconds % 60
            val minutes = totalSeconds / 60 % 60
            val hours = totalSeconds / 3600
            return if (hours > 0) String.format(
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
            ) else String.format("%02d:%02d", minutes, seconds)
        }
    }
}