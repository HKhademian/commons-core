@file:JvmName("Pools")
@file:JvmMultifileClass

package ir.hossainco.commons.collection.pool
import ir.hossainco.commons.types.SingleProvider

typealias PoolProvider<T> = SingleProvider<Pool<T>, Class<T>>

inline fun <Type> pool(initialCapacity: Int = 16, max: Int = Int.MAX_VALUE, crossinline provider: () -> Type): Pool<Type> =
	object : Pool<Type>(initialCapacity, max) {
		override fun newObject(): Type = provider()
	}


