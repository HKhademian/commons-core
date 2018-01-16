@file:Suppress("NOTHING_TO_INLINE", "unused")
@file:JvmName("Providers")
@file:JvmMultifileClass

package ir.hossainco.commons.types

typealias TripleProvider<R, P1, P2, P3> = (P1, P2, P3) -> R

inline fun <R, P1, P2, P3> TripleProvider<R, P1, P2, P3>.provide(p1: P1, p2: P2, p3: P3) = this(p1, p2, p3)
