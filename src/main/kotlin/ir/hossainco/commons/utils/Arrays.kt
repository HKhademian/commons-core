@file:Suppress("unused")
@file:JvmName("Arrays")
@file:JvmMultifileClass

package ir.hossainco.commons.utils

import ir.hossainco.commons.types.Provider
import ir.hossainco.commons.types.SingleProvider
import ir.hossainco.commons.types.provide

inline fun <T> Array<out T>.firstOrDefault(default: T, predicate: (T) -> Boolean) =
	this.firstOrNull(predicate) ?: default

inline fun <T> Array<out T>.firstOrDefault(default: Provider<T>, predicate: (T) -> Boolean) =
	this.firstOrNull(predicate) ?: default.provide()

fun <T> Array<T?>.fill(fromIndex: Int, toIndex: Int, value: T) {
	if (fromIndex !in 0..size) throw RuntimeException("fromIndex is out of boundary")
	if (toIndex !in 0..size) throw RuntimeException("toIndex is out of boundary")
	for (i in fromIndex until toIndex)
		this[i] = value
}

fun <T> Array<T>.fill(fromIndex: Int, toIndex: Int, provider: Provider<T>) {
	if (fromIndex !in 0..size) throw RuntimeException("fromIndex is out of boundary")
	if (toIndex !in 0..size) throw RuntimeException("toIndex is out of boundary")
	for (i in fromIndex until toIndex)
		this[i] = provider.provide()
}

fun <T> Array<T>.fill(fromIndex: Int, toIndex: Int, provider: SingleProvider<T, Int>) {
	if (fromIndex !in 0..size) throw RuntimeException("fromIndex is out of boundary")
	if (toIndex !in 0..size) throw RuntimeException("toIndex is out of boundary")
	for (i in fromIndex until toIndex)
		this[i] = provider.provide(i)
}

fun <T> Array<T?>.fillNull(value: T) {
	indices
		.filter { this[it] == null }
		.forEach { this[it] = value }
}

fun <T> Array<T?>.fillNull(provider: Provider<T>) {
	indices
		.filter { this[it] == null }
		.forEach { this[it] = provider.provide() }
}

fun <T> Array<T?>.fillNull(provider: SingleProvider<T, Int>) {
	indices
		.filter { this[it] == null }
		.forEach { this[it] = provider.provide(it) }
}
