package dev.weltonfelix.musy.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var debounceJob: Job? = null

fun <T> debounce(
	waitMs: Long = 300L,
	coroutineScope: CoroutineScope,
	callback: (T) -> Unit
): (T) -> Unit {
	return { param: T ->
		debounceJob?.cancel()
		debounceJob = coroutineScope.launch {
			delay(waitMs)
			callback(param)
		}
	}
}
