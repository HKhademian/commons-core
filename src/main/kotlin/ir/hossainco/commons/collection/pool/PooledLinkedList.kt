@file:Suppress("unused")

package ir.hossainco.commons.collection.pool

/** A simple linked list that pools its nodes. */
class PooledLinkedList<T>(maxPoolSize: Int) {
	private var head: Item<T>? = null
	private var tail: Item<T>? = null
	private var iter: Item<T>? = null
	private var curr: Item<T>? = null
	var size = 0
		private set

	private val pool = pool(16, maxPoolSize) { Item<T>() }

	internal class Item<T> {
		var payload: T? = null
		var next: Item<T>? = null
		var prev: Item<T>? = null
	}

	/** Adds the specified object to the end of the list regardless of iteration status  */
	fun add(`object`: T) {
		val item = pool.obtain()
		item.payload = `object`
		item.next = null
		item.prev = null

		if (head == null) {
			head = item
			tail = item
			size++
			return
		}

		item.prev = tail
		tail!!.next = item
		tail = item
		size++
	}

	/** Starts iterating over the list's items from the head of the list  */
	fun iter() {
		iter = head
	}

	/** Starts iterating over the list's items from the tail of the list  */
	fun iterReverse() {
		iter = tail
	}

	/** Gets the next item in the list
	 *
	 * @return the next item in the list or null if there are no more items
	 */
	operator fun next(): T? {
		if (iter == null) return null

		val payload = iter!!.payload
		curr = iter
		iter = iter!!.next
		return payload
	}

	/** Gets the previous item in the list
	 *
	 * @return the previous item in the list or null if there are no more items
	 */
	fun previous(): T? {
		if (iter == null) return null

		val payload = iter!!.payload
		curr = iter
		iter = iter!!.prev
		return payload
	}

	/** Removes the current list item based on the iterator position.  */
	fun remove() {
		if (curr == null) return

		size--
		pool.free(curr!!)

		val c = curr
		val n = curr!!.next
		val p = curr!!.prev
		curr = null

		if (size == 0) {
			head = null
			tail = null
			return
		}

		if (c == head) {
			n!!.prev = null
			head = n
			return
		}

		if (c == tail) {
			p!!.next = null
			tail = p
			return
		}

		p!!.next = n
		n!!.prev = p
	}

	fun clear() {
		iter()
		var v = next()
		while (v != null) {
			remove()
			v = next()
		}
	}
}
