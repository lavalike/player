# player
> 基于exoplayer的播放器

[![Platform](https://img.shields.io/badge/Platform-Android-00CC00.svg?style=flat)](https://www.android.com)
[![](https://jitpack.io/v/lavalike/player.svg)](https://jitpack.io/#lavalike/player)

![Screenshot_1607580726.png](https://i.loli.net/2020/12/10/XNoYAV9HZtgIvOU.png)

### 依赖导入

项目根目录

``` gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

模块目录

``` gradle
dependencies {
	implementation 'com.github.lavalike:player:0.0.1'
}
```

### 快速使用

调用PlayerManager传入url和FrameLayout即可，无需额外处理生命周期

``` kotlin
class PlayerManager {
    companion object {
        fun play(url: String, container: FrameLayout)
        fun play(url: String, container: FrameLayout, controllers: Controllers?)
    }
}
```

代码示例

``` kotlin
PlayerManager.play(HostConfig.URL, findViewById(R.id.container))
```

### 自定义控制界面

继承 **Controller** 实现自定义Controller，详细实现请参照 **DefaultUIController**

``` kotlin
abstract class Controller {
    lateinit var container: FrameLayout
    var playerView: PlayerView? = null
    abstract fun run()
}

class CustomUIController : Controller() {
    override fun run() {

    }
}

val controllers = Controllers()
controllers.enqueue(CustomUIController())
PlayerManager.play(.., .., controllers)
```