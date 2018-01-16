@file:Suppress("MemberVisibilityCanPrivate", "unused")
@file:JvmName("Times")
@file:JvmMultifileClass

package ir.hossainco.commons.utils

/** Wrapper around System.nanoTime() and System.currentTimeMillis(). Use this if you want to be compatible across all platforms! */
private val nanosPerMilli = 1000000L
/** The current value of the system timer, in nanoseconds. */
val nanoTime
	get() = System.nanoTime()

/** the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC. */
val millis
	get() = System.currentTimeMillis()

/** Convert nanoseconds time to milliseconds
 * @param nanos must be nanoseconds
 * @return time value in milliseconds
 */
fun nanosToMillis(nanos: Long): Long =
	nanos / nanosPerMilli

/** Convert milliseconds time to nanoseconds
 * @param millis must be milliseconds
 * @return time value in nanoseconds
 */
fun millisToNanos(millis: Long): Long =
	millis * nanosPerMilli

/** Get the time in nanos passed since a previous time
 * @param prevTime - must be nanoseconds
 * @return - time passed since prevTime in nanoseconds
 */
fun timeSinceNanos(prevTime: Long): Long =
	nanoTime - prevTime

/** Get the time in millis passed since a previous time
 * @param prevTime - must be milliseconds
 * @return - time passed since prevTime in milliseconds
 */
fun timeSinceMillis(prevTime: Long): Long =
	millis - prevTime
