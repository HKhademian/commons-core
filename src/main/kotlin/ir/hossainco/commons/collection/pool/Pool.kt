package ir.hossainco.commons.collection.pool

import ir.hossainco.commons.types.Resettable
import ir.hossainco.commons.utils.removeLast

/** A pool of objects that can be reused to avoid allocation.
 * @see PoolStore */
abstract class Pool<T>(
	initialCapacity: Int = 16,
	/** The maximum number of objects that will be pooled.  */
	val max: Int = Int.MAX_VALUE
) {
	/** The highest number of free objects. Can be reset any time.  */
	var peak: Int = 0

	private val freeObjects =   ArrayList<T>(initialCapacity)

	/** The number of objects available to be obtained.  */
	val free: Int
		get() = freeObjects.size

	operator fun invoke() =
		newObject()

	operator fun invoke(`object`: T) =
		free(`object`)

	operator fun invoke(objects: Array<T>) =
		freeAll(objects)


	protected abstract fun newObject(): T

	/** Returns an object from this pool. The object may be new (from [.newObject]) or reused (previously
	 * [freed][.free]).  */
	open fun obtain(): T =
		if (freeObjects.isEmpty()) newObject()
		else freeObjects.removeLast()

	/** Puts the specified object in the pool, making it eligible to be returned by [.obtain]. If the pool already contains
	 * [.max] free objects, the specified object is reset but not added to the pool.  */
	open fun free(`object`: T) {
		if (freeObjects.size < max) {
			freeObjects.add(`object`)
			peak = Math.max(peak, freeObjects.size)
		}
		reset(`object`)
	}

	/** Called when an object is freed to clear the state of the object for possible later reuse. The default implementation calls
	 * [Resettable.reset] if the object is [Resettable]. */
	protected fun reset(`object`: T) {
		(`object` as? Resettable)?.reset()
	}

	/** Puts the specified objects in the pool. Null objects within the array are silently ignored.
	 * @see .free */
	open fun freeAll(objects: Array<T>) {
		val freeObjects = this.freeObjects
		val max = this.max
		for (i in 0 until objects.size) {
			val `object` = objects[i] ?: continue
			if (freeObjects.size < max) freeObjects.add(`object`)
			reset(`object`)
		}
		peak = Math.max(peak, freeObjects.size)
	}

	/** Removes all free objects from this pool.  */
	fun clear() {
		freeObjects.clear()
	}
}
