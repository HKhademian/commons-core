@file:Suppress("NOTHING_TO_INLINE", "unused")
@file:JvmName("Providers")
@file:JvmMultifileClass

package ir.hossainco.commons.types

typealias DoubleProvider<R, P1, P2> = (P1, P2) -> R

inline fun <R, P1, P2> DoubleProvider<R, P1, P2>.provide(p1: P1, p2: P2) = this(p1, p2)
