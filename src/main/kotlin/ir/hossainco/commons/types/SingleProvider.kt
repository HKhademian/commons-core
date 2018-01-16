@file:Suppress("NOTHING_TO_INLINE", "unused")
@file:JvmName("Providers")
@file:JvmMultifileClass

package ir.hossainco.commons.types

typealias SingleProvider<R, P1> = (P1) -> R

inline fun <R, P1> SingleProvider<R, P1>.provide(p: P1) = this(p)
