@file:Suppress("unused", "MemberVisibilityCanPrivate")
@file:JvmName("Collections")
@file:JvmMultifileClass

package ir.hossainco.commons.utils

import ir.hossainco.commons.text.SerializationException

/** Removes and returns the last item.  */
fun <T> MutableList<T>.removeLast(): T =
	if (isEmpty()) throw SerializationException("List is empty.")
	else removeAt(size - 1)

/** Returns the first item. */
fun <T> MutableList<T>.removeFirst(): T =
	if (isEmpty()) throw IllegalStateException("List is empty.")
	else removeAt(0)


/** @see [removeLast] */
fun <T> MutableList<T>.pop(): T =
	removeLast()

/** @see [Iterable.last] */
fun <T> Iterable<T>.pick(): T =
	last()

/** @see [List.last] */
fun <T> List<T>.pick(): T =
	last()
