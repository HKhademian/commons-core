@file:Suppress("unused")

package ir.hossainco.commons.collection.pool

/** A [Pool] which keeps track of the obtained items (see [.obtain]), which can be free'd all at once using the [.flush] method.*/
abstract class FlushablePool<T> : Pool<T> {
	private var obtained = ArrayList<T>()

	constructor() : super()
	constructor(initialCapacity: Int) : super(initialCapacity)
	constructor(initialCapacity: Int, max: Int) : super(initialCapacity, max)

	override fun obtain(): T {
		val result = super.obtain()
		obtained.add(result)
		return result
	}

	/** Frees all obtained instances.  */
	fun flush() {
		super.freeAll(obtained.toArray() as Array<T>)
		obtained.clear()
	}

	override fun free(`object`: T) {
		obtained.remove(`object`)
		super.free(`object`)
	}

	override fun freeAll(objects: Array<T>) {
		obtained.removeAll(objects)
		super.freeAll(objects)
	}
}
