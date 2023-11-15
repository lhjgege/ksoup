package com.fleeksoft.ksoup.ported

import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class System {
    companion object {
        fun nanoTime(): Long {
            return Clock.System.now().epochSeconds.seconds.inWholeNanoseconds
        }

        fun currentTimeMillis(): Long {
            return Clock.System.now().epochSeconds.seconds.inWholeMilliseconds
        }
    }
}
