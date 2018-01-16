@file:Suppress("unused", "MemberVisibilityCanPrivate")
@file:JvmName("Maths")
@file:JvmMultifileClass

package ir.hossainco.commons.utils

const val nanoToSec = 1 / 1000000000f

const val FLOAT_ROUNDING_ERROR = 0.000001f // 32 bits
const val PI = 3.1415927f
const val PI2 = PI * 2
const val E = 2.7182818f

private const val SIN_BITS = 14 // 16KB. Adjust for accuracy.
private const val SIN_MASK = (-1 shl SIN_BITS).inv()
private const val SIN_COUNT = SIN_MASK + 1

private const val radFull = PI * 2
private const val degFull = 360f
private const val radToIndex = SIN_COUNT / radFull
private const val degToIndex = SIN_COUNT / degFull


/** multiply by this to convert from radians to degrees  */
const val radiansToDegrees = 180f / PI

/** multiply by this to convert from radians to degrees  */
const val radDeg = radiansToDegrees


/** multiply by this to convert from degrees to radians  */
const val degreesToRadians = PI / 180

/** multiply by this to convert from degrees to radians  */
const val degRad = degreesToRadians


//fun random() =
//	0f

//fun random(from: Float, to: Float) =
//	from


fun sin(radians: Float) =
	Math.sin(radians.toDouble()).toFloat()

fun cos(radians: Float) =
	Math.cos(radians.toDouble()).toFloat()

fun sinDeg(degrees: Float) =
	sin(degrees * degreesToRadians)

fun cosDeg(degrees: Float) =
	cos(degrees * degreesToRadians)


fun isZero(value: Float, tolerance: Float = FLOAT_ROUNDING_ERROR) =
	Math.abs(value) <= tolerance

fun isEqual(a: Float, b: Float, tolerance: Float = FLOAT_ROUNDING_ERROR) =
	Math.abs(a - b) <= tolerance
