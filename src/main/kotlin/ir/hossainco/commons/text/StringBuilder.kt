package ir.hossainco.commons.text

/** Very simple string builder */
class StringBuilder(init: String? = null) : CharSequence {
	private var text = init ?: ""

	constructor(capacity: Int) : this()

	override var length: Int
		get() = text.length
		set(value) {
			val len = text.length
			if (value < len)
				text = text.subSequence(0, value).toString()
			else if (value > len)
				text = text.padEnd(value - len)
		}

	override operator fun get(index: Int) =
		text[index]

	override fun subSequence(startIndex: Int, endIndex: Int) =
		text.subSequence(startIndex, endIndex)


	fun replace(from: Char, to: Char): StringBuilder {
		text = text.replace(from, to)
		return this
	}

	fun replace(from: String, to: String): StringBuilder {
		text = text.replace(from, to)
		return this
	}

	fun replace(from: Any, to: String): StringBuilder {
		text = text.replace(from.toString(), to)
		return this
	}

	fun replace(from: Any, to: Any): StringBuilder {
		text = text.replace(from.toString(), to.toString())
		return this
	}

//	fun append(value: CharSequence) {
//		text += value
//	}
//
//	fun append(value: Char) {
//		text += value
//	}
//
////	fun append(value: String) {
////		text += value
////	}
//
//	fun append(value: Number) {
//		text += value
//	}
//
//	fun append(value: Long) {
//		text += value
//	}
//
//	fun append(value: Int) {
//		text += value
//	}
//
//	fun append(value: Boolean) {
//		text += value
//	}

	fun append(value: Any?) {
		text += value ?: ""
	}
}
