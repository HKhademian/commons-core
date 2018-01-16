@file:Suppress("unused")

package ir.hossainco.commons.text

/** Indicates an error during serialization due to misconfiguration or during deserialization due to invalid input data. */
class SerializationException : RuntimeException {
	private var trace: StringBuffer? = null

	override val message: String
		get() {
			if (trace == null) return super.message ?: ""
			val buffer = StringBuffer(512)
			buffer.append(super.message)
			if (buffer.isNotEmpty()) buffer.append('\n')
			buffer.append("Serialization trace:")
			buffer.append(trace)
			return buffer.toString()
		}

	constructor() : super()
	constructor(message: String, cause: Throwable) : super(message, cause)
	constructor(message: String) : super(message)
	constructor(cause: Throwable) : super(null, cause)

	/** Returns true if any of the exceptions that caused this exception are of the specified type.  */
	fun causedBy(type: Class<in Any>): Boolean {
		return causedBy(this, type)
	}

	private fun causedBy(ex: Throwable?, type: Class<in Any>): Boolean {
		val cause = ex?.cause
		if (cause == null || cause == ex) return false
		return if (type.isAssignableFrom(cause.javaClass)) true else causedBy(cause, type)
	}

	/** Adds information to the exception message about where in the the object graph serialization failure occurred. Serializers
	 * can catch [SerializationException], add trace information, and rethrow the exception.  */
	fun addTrace(info: String?) {
		if (info == null) throw IllegalArgumentException("info cannot be null.")
		if (trace == null) trace = StringBuffer(512)
		trace!!.append('\n')
		trace!!.append(info)
	}
}
