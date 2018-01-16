package ir.hossainco.commons.collection.pool

import ir.hossainco.commons.types.provide

/** Stores a map of [Pool]s by type for convenient static access. */
object PoolStore {
	var provider: PoolProvider<Any>? = null
	private val typePools = HashMap<Class<*>, Pool<Any>>()

	operator fun <T> invoke(type: Class<T>) =
		obtain(type)

	/** Returns a new or existing pool for the specified type, stored in a Class to [Pool] map. Note the max size is ignored
	 * if this is not the first time this pool has been requested.  */
	operator fun <T> get(type: Class<T>, max: Int = 100): Pool<T> {
		var pool = typePools[type]
		if (pool == null) {
			if (provider == null)
				throw Exception("Pool Provider is null")
			pool = provider!!.provide(type as Class<Any>)
			typePools[type] = pool
		}
		return pool as Pool<T>
	}

	/** Sets an existing pool for the specified type, stored in a Class to [Pool] map.  */
	operator fun <T> set(type: Class<T>, pool: Pool<T>): Pool<T> {
		typePools[type] = pool as Pool<Any>
		return pool
	}

	/** Obtains an object from the [pool][.get]. */
	fun <T> obtain(type: Class<T>): T {
		return get(type).obtain()
	}

	/** Frees an object from the [pool][.get].  */
	fun <T : Any> free(`object`: T) {
		val pool = typePools[`object`.javaClass] ?: return
		// Ignore freeing an object that was never retained.
		pool.free(`object`)
	}

	/** Frees the specified objects from the [pool][.get]. Null objects within the array are silently ignored.
	 * @param samePool If true, objects don't need to be from the same pool but the pool must be looked up for each object.
	 */
	fun freeAll(objects: List<Any?>, samePool: Boolean = false) {
		var pool: Pool<Any>? = null
		var i = 0
		val n = objects.size
		while (i < n) {
			val `object` = objects[i]
			if (`object` == null) {
				i++
				continue
			}
			if (pool == null) {
				pool = typePools[`object`.javaClass]
				if (pool == null) {
					i++
					continue
				} // Ignore freeing an object that was never retained.
			}
			pool.free(`object`)
			if (!samePool) pool = null
			i++
		}
	}
}
