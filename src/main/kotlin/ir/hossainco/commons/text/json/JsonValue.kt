@file:Suppress("unused", "MemberVisibilityCanPrivate", "NAME_SHADOWING")

package ir.hossainco.commons.text.json

import ir.hossainco.commons.text.SerializationException
import ir.hossainco.commons.text.StringBuilder


/** Container for a JSON object, array, string, double, long, boolean, or null.
 *
 * JsonValue children are a linked list. Iteration of arrays or objects is easily done using a for loop, either with the enhanced
 * for loop syntactic sugar or like the example below. This is much more efficient than accessing children by index when there are
 * many children.<br></br>
 *
 * JsonValue map = ...;
 * for (JsonValue entry = map.child; entry != null; entry = entry.next)
 * System.out.println(entry.name + " = " + entry.asString());
 */
class JsonValue : Iterable<JsonValue> {
	private var type: ValueType? = null

	/** May be null.  */
	private var stringValue: String? = null
	private var doubleValue: Double = 0.toDouble()
	private var longValue: Long = 0

	var name: String? = null
	var child: JsonValue? = null
	var next: JsonValue? = null
	var prev: JsonValue? = null
	var parent: JsonValue? = null
	var size: Int = 0


	val isArray: Boolean
		get() = type == ValueType.array

	val isObject: Boolean
		get() = type == ValueType.`object`

	val isString: Boolean
		get() = type == ValueType.string

	/** Returns true if this is a double or long value.  */
	val isNumber: Boolean
		get() = type == ValueType.double || type == ValueType.long

	val isDouble: Boolean
		get() = type == ValueType.double

	val isLong: Boolean
		get() = type == ValueType.long

	val isBoolean: Boolean
		get() = type == ValueType.boolean

	val isNull: Boolean
		get() = type == ValueType.nullValue

	/** Returns true if this is not an array or object.  */
	val isValue: Boolean
		get() = when (type) {
			ValueType.string,
			ValueType.double,
			ValueType.long,
			ValueType.boolean,
			ValueType.nullValue -> true
			else -> false
		}

	constructor(type: ValueType) {
		this.type = type
	}

	constructor(value: String) {
		set(value)
	}

	constructor(value: Double, stringValue: String? = null) {
		set(value, stringValue)
	}

	constructor(value: Long, stringValue: String? = null) {
		set(value, stringValue)
	}

	constructor(value: Boolean) {
		set(value)
	}

	/** Returns the child at the specified index. This requires walking the linked list to the specified entry, see
	 * [JsonValue] for how to iterate efficiently.
	 * @return May be null.
	 */
	operator fun get(index: Int): JsonValue? {
		var index = index
		var current = child
		while (current != null && index > 0) {
			index--
			current = current.next
		}
		return current
	}

	/** Returns the child with the specified name.
	 * @return May be null.
	 */
	operator fun get(name: String): JsonValue? {
		var current = child
		while (current != null && (current.name == null || !current.name!!.equals(name, true)))
			current = current.next
		return current
	}

	/** Returns true if a child with the specified name exists.  */
	fun has(name: String): Boolean =
		get(name) != null

	/** Returns the child at the specified index. This requires walking the linked list to the specified entry, see
	 * [JsonValue] for how to iterate efficiently.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun require(index: Int): JsonValue {
		return get(index) ?: throw IllegalArgumentException("Child not found with index: " + index)
	}

	/** Returns the child with the specified name.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun require(name: String): JsonValue {
		return get(name) ?: throw IllegalArgumentException("Child not found with name: " + name)
	}

	/** Removes the child with the specified index. This requires walking the linked list to the specified entry, see
	 * [JsonValue] for how to iterate efficiently.
	 * @return May be null.
	 */
	fun remove(index: Int): JsonValue? {
		val child = get(index) ?: return null
		if (child.prev == null) {
			this.child = child.next
			if (this.child != null) this.child!!.prev = null
		} else {
			child.prev!!.next = child.next
			if (child.next != null) child.next!!.prev = child.prev
		}
		size--
		return child
	}

	/** Removes the child with the specified name.
	 * @return May be null.
	 */
	fun remove(name: String): JsonValue? {
		val child = get(name) ?: return null
		if (child.prev == null) {
			this.child = child.next
			if (this.child != null) this.child!!.prev = null
		} else {
			child.prev!!.next = child.next
			if (child.next != null) child.next!!.prev = child.prev
		}
		size--
		return child
	}


	/** Returns this value as a string.
	 * @return May be null if this value is null.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asString(): String =
		when (type) {
			ValueType.string -> stringValue!!
			ValueType.double -> if (stringValue != null) stringValue!! else doubleValue.toString()
			ValueType.long -> if (stringValue != null) stringValue!! else longValue.toString()
			ValueType.boolean -> if (longValue != 0L) "true" else "false"
			ValueType.nullValue -> ""
			else -> throw IllegalStateException("Value cannot be converted to string: " + type!!)
		}

	/** Returns this value as a float.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asFloat(): Float =
		when (type) {
			ValueType.string -> stringValue!!.toFloat()
			ValueType.double -> doubleValue.toFloat()
			ValueType.long -> longValue.toFloat()
			ValueType.boolean -> (if (longValue != 0L) 1 else 0).toFloat()
			else -> throw IllegalStateException("Value cannot be converted to float: " + type!!)
		}

	/** Returns this value as a double.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asDouble(): Double =
		when (type) {
			ValueType.string -> stringValue!!.toDouble()
			ValueType.double -> doubleValue
			ValueType.long -> longValue.toDouble()
			ValueType.boolean -> (if (longValue != 0L) 1 else 0).toDouble()
			else -> throw IllegalStateException("Value cannot be converted to double: " + type!!)
		}

	/** Returns this value as a long.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asLong(): Long =
		when (type) {
			ValueType.string -> stringValue!!.toLong()
			ValueType.double -> doubleValue.toLong()
			ValueType.long -> longValue
			ValueType.boolean -> (if (longValue != 0L) 1 else 0).toLong()
			else -> throw IllegalStateException("Value cannot be converted to long: " + type!!)
		}

	/** Returns this value as an int.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asInt(): Int =
		when (type) {
			ValueType.string -> Integer.parseInt(stringValue)
			ValueType.double -> doubleValue.toInt()
			ValueType.long -> longValue.toInt()
			ValueType.boolean -> if (longValue != 0L) 1 else 0
			else -> throw IllegalStateException("Value cannot be converted to int: " + type!!)
		}

	/** Returns this value as a boolean.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asBoolean(): Boolean =
		when (type) {
			ValueType.string -> stringValue!!.toBoolean()
			ValueType.double -> doubleValue != 0.0
			ValueType.long -> longValue != 0L
			ValueType.boolean -> longValue != 0L
			else -> throw IllegalStateException("Value cannot be converted to boolean: " + type!!)
		}

	/** Returns this value as a byte.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asByte(): Byte =
		when (type) {
			ValueType.string -> stringValue!!.toByte()
			ValueType.double -> doubleValue.toByte()
			ValueType.long -> longValue.toByte()
			ValueType.boolean -> if (longValue != 0L) 1.toByte() else 0
			else -> throw IllegalStateException("Value cannot be converted to byte: " + type!!)
		}

	/** Returns this value as a short.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asShort(): Short =
		when (type) {
			ValueType.string -> stringValue!!.toShort()
			ValueType.double -> doubleValue.toShort()
			ValueType.long -> longValue.toShort()
			ValueType.boolean -> if (longValue != 0L) 1.toShort() else 0
			else -> throw IllegalStateException("Value cannot be converted to short: " + type!!)
		}

	/** Returns this value as a char.
	 * @throws IllegalStateException if this an array or object.
	 */
	fun asChar(): Char =
		when (type) {
			ValueType.string -> if (stringValue!!.isEmpty()) 0.toChar() else stringValue!![0]
			ValueType.double -> doubleValue.toChar()
			ValueType.long -> longValue.toChar()
			ValueType.boolean -> if (longValue != 0L) 1.toChar() else 0.toChar()
			else -> throw IllegalStateException("Value cannot be converted to char: " + type!!)
		}

	/** Returns the children of this value as a newly allocated String array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asStringArray(): ArrayList<String> {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = ArrayList<String>(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asString()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated float array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asFloatArray(): FloatArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = FloatArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asFloat()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated double array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asDoubleArray(): DoubleArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = DoubleArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asDouble()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated long array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asLongArray(): LongArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = LongArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asLong()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated int array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asIntArray(): IntArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = IntArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asInt()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated boolean array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asBooleanArray(): BooleanArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = BooleanArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asBoolean()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated byte array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asByteArray(): ByteArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = ByteArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asByte()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated short array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asShortArray(): ShortArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = ShortArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asShort()
			value = value.next
			i++
		}
		return array
	}

	/** Returns the children of this value as a newly allocated char array.
	 * @throws IllegalStateException if this is not an array.
	 */
	fun asCharArray(): CharArray {
		if (type != ValueType.array) throw IllegalStateException("Value is not an array: " + type!!)
		val array = CharArray(size)
		var i = 0
		var value = child
		while (value != null) {
			array[i] = value.asChar()
			value = value.next
			i++
		}
		return array
	}

	/** Returns true if a child with the specified name exists and has a child.  */
	fun hasChild(name: String): Boolean =
		getChild(name) != null

	/** Finds the child with the specified name and returns its first child.
	 * @return May be null.
	 */
	fun getChild(name: String): JsonValue? {
		val child = get(name)
		return child?.child
	}

	/** Finds the child with the specified name and returns it as a string. Returns defaultValue if not found.
	 * @param defaultValue May be null.
	 */
	fun getString(name: String, defaultValue: String): String? {
		val child = get(name)
		return if (child == null || !child.isValue || child.isNull) defaultValue else child.asString()
	}

	/** Finds the child with the specified name and returns it as a float. Returns defaultValue if not found.  */
	fun getFloat(name: String, defaultValue: Float): Float {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asFloat()
	}

	/** Finds the child with the specified name and returns it as a double. Returns defaultValue if not found.  */
	fun getDouble(name: String, defaultValue: Double): Double {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asDouble()
	}

	/** Finds the child with the specified name and returns it as a long. Returns defaultValue if not found.  */
	fun getLong(name: String, defaultValue: Long): Long {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asLong()
	}

	/** Finds the child with the specified name and returns it as an int. Returns defaultValue if not found.  */
	fun getInt(name: String, defaultValue: Int): Int {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asInt()
	}

	/** Finds the child with the specified name and returns it as a boolean. Returns defaultValue if not found.  */
	fun getBoolean(name: String, defaultValue: Boolean): Boolean {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asBoolean()
	}

	/** Finds the child with the specified name and returns it as a byte. Returns defaultValue if not found.  */
	fun getByte(name: String, defaultValue: Byte): Byte {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asByte()
	}

	/** Finds the child with the specified name and returns it as a short. Returns defaultValue if not found.  */
	fun getShort(name: String, defaultValue: Short): Short {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asShort()
	}

	/** Finds the child with the specified name and returns it as a char. Returns defaultValue if not found.  */
	fun getChar(name: String, defaultValue: Char): Char {
		val child = get(name)
		return if (child == null || !child.isValue) defaultValue else child.asChar()
	}

	/** Finds the child with the specified name and returns it as a string.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getString(name: String): String? {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asString()
	}

	/** Finds the child with the specified name and returns it as a float.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getFloat(name: String): Float {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asFloat()
	}

	/** Finds the child with the specified name and returns it as a double.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getDouble(name: String): Double {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asDouble()
	}

	/** Finds the child with the specified name and returns it as a long.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getLong(name: String): Long {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asLong()
	}

	/** Finds the child with the specified name and returns it as an int.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getInt(name: String): Int {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asInt()
	}

	/** Finds the child with the specified name and returns it as a boolean.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getBoolean(name: String): Boolean {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asBoolean()
	}

	/** Finds the child with the specified name and returns it as a byte.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getByte(name: String): Byte {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asByte()
	}

	/** Finds the child with the specified name and returns it as a short.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getShort(name: String): Short {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asShort()
	}

	/** Finds the child with the specified name and returns it as a char.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getChar(name: String): Char {
		val child = get(name) ?: throw IllegalArgumentException("Named value not found: " + name)
		return child.asChar()
	}

	/** Finds the child with the specified index and returns it as a string.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getString(index: Int): String? {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asString()
	}

	/** Finds the child with the specified index and returns it as a float.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getFloat(index: Int): Float {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asFloat()
	}

	/** Finds the child with the specified index and returns it as a double.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getDouble(index: Int): Double {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asDouble()
	}

	/** Finds the child with the specified index and returns it as a long.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getLong(index: Int): Long {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asLong()
	}

	/** Finds the child with the specified index and returns it as an int.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getInt(index: Int): Int {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asInt()
	}

	/** Finds the child with the specified index and returns it as a boolean.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getBoolean(index: Int): Boolean {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asBoolean()
	}

	/** Finds the child with the specified index and returns it as a byte.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getByte(index: Int): Byte {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asByte()
	}

	/** Finds the child with the specified index and returns it as a short.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getShort(index: Int): Short {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asShort()
	}

	/** Finds the child with the specified index and returns it as a char.
	 * @throws IllegalArgumentException if the child was not found.
	 */
	fun getChar(index: Int): Char {
		val child = get(index) ?: throw IllegalArgumentException("Indexed value not found: " + name!!)
		return child.asChar()
	}

	fun type(): ValueType? {
		return type
	}

	fun setType(type: ValueType?) {
		if (type == null) throw IllegalArgumentException("type cannot be null.")
		this.type = type
	}

	/** Sets the name of the specified value and adds it after the last child.  */
	fun addChild(name: String, value: JsonValue) {
		value.name = name
		addChild(value)
	}

	/** Adds the specified value after the last child.  */
	fun addChild(value: JsonValue) {
		value.parent = this
		var current = child
		if (current == null)
			child = value
		else {
			while (true) {
				if (current!!.next == null) {
					current.next = value
					return
				}
				current = current.next
			}
		}
	}

	/** the next sibling of this value. */
	operator fun next(): JsonValue? =
		next

	fun set(value: String?) {
		stringValue = value
		type = if (value == null) ValueType.nullValue else ValueType.string
	}

	fun set(value: Double, stringValue: String? = null) {
		doubleValue = value
		longValue = value.toLong()
		this.stringValue = stringValue
		type = ValueType.double
	}

	fun set(value: Long, stringValue: String? = null) {
		longValue = value
		doubleValue = value.toDouble()
		this.stringValue = stringValue
		type = ValueType.long
	}

	fun set(value: Boolean) {
		longValue = (if (value) 1 else 0).toLong()
		type = ValueType.boolean
	}

	fun toJson(outputType: OutputType): String? {
		if (isValue) return asString()
		val buffer = StringBuilder(512)
		json(this, buffer, outputType)
		return buffer.toString()
	}

	private fun json(`object`: JsonValue, buffer: StringBuilder, outputType: OutputType) {
		if (`object`.isObject) {
			if (`object`.child == null)
				buffer.append("{}")
			else {
				//val start = buffer.length
				while (true) {
					buffer.append('{')
					var child = `object`.child
					while (child != null) {
						buffer.append(outputType.quoteName(child.name!!))
						buffer.append(':')
						json(child, buffer, outputType)
						if (child.next != null) buffer.append(',')
						child = child.next
					}
					break
				}
				buffer.append('}')
			}
		} else if (`object`.isArray) {
			if (`object`.child == null)
				buffer.append("[]")
			else {
				//val start = buffer.length
				while (true) {
					buffer.append('[')
					var child = `object`.child
					while (child != null) {
						json(child, buffer, outputType)
						if (child.next != null) buffer.append(',')
						child = child.next
					}
					break
				}
				buffer.append(']')
			}
		} else if (`object`.isString) {
			buffer.append(outputType.quoteValue(`object`.asString()))
		} else if (`object`.isDouble) {
			val doubleValue = `object`.asDouble()
			val longValue = `object`.asLong()
			buffer.append(if (doubleValue == longValue.toDouble()) longValue else doubleValue)
		} else if (`object`.isLong) {
			buffer.append(`object`.asLong())
		} else if (`object`.isBoolean) {
			buffer.append(`object`.asBoolean())
		} else if (`object`.isNull) {
			buffer.append("null")
		} else
		throw SerializationException("Unknown object type: " + `object`)
	}

	override fun toString() =
		if (isValue)
			if (name == null) asString()
			else name + ": " + asString()
		else
			(if (name == null) "" else name!! + ": ") + prettyPrint(OutputType.minimal, 0)

	fun prettyPrint(outputType: OutputType, singleLineColumns: Int): String {
		val settings = PrettyPrintSettings()
		settings.outputType = outputType
		settings.singleLineColumns = singleLineColumns
		return prettyPrint(settings)
	}

	fun prettyPrint(settings: PrettyPrintSettings): String {
		val buffer = StringBuilder(512)
		prettyPrint(this, buffer, 0, settings)
		return buffer.toString()
	}

	private fun prettyPrint(`object`: JsonValue, buffer: StringBuilder, indent: Int, settings: PrettyPrintSettings) {
		val outputType = settings.outputType
		if (`object`.isObject) {
			if (`object`.child == null)
				buffer.append("{}")
			else {
				var newLines = !isFlat(`object`)
				val start = buffer.length
				outer@ while (true) {
					buffer.append(if (newLines) "{\n" else "{ ")
					var child = `object`.child
					while (child != null) {
						if (newLines) indent(indent, buffer)
						buffer.append(outputType!!.quoteName(child.name!!))
						buffer.append(": ")
						prettyPrint(child, buffer, indent + 1, settings)
						if ((!newLines || outputType !== OutputType.minimal) && child.next != null) buffer.append(',')
						buffer.append(if (newLines) '\n' else ' ')
						if (!newLines && buffer.length - start > settings.singleLineColumns) {
							buffer.length = start
							newLines = true
							continue@outer
						}
						child = child.next
					}
					break
				}
				if (newLines) indent(indent - 1, buffer)
				buffer.append('}')
			}
		} else if (`object`.isArray) {
			if (`object`.child == null)
				buffer.append("[]")
			else {
				var newLines = !isFlat(`object`)
				val wrap = settings.wrapNumericArrays || !isNumeric(`object`)
				val start = buffer.length
				outer@ while (true) {
					buffer.append(if (newLines) "[\n" else "[ ")
					var child = `object`.child
					while (child != null) {
						if (newLines) indent(indent, buffer)
						prettyPrint(child, buffer, indent + 1, settings)
						if ((!newLines || outputType !== OutputType.minimal) && child.next != null) buffer.append(',')
						buffer.append(if (newLines) '\n' else ' ')
						if (wrap && !newLines && buffer.length - start > settings.singleLineColumns) {
							buffer.length = start
							newLines = true
							continue@outer
						}
						child = child.next
					}
					break
				}
				if (newLines) indent(indent - 1, buffer)
				buffer.append(']')
			}
		} else if (`object`.isString) {
			buffer.append(outputType!!.quoteValue(`object`.asString()))
		} else if (`object`.isDouble) {
			val doubleValue = `object`.asDouble()
			val longValue = `object`.asLong()
			buffer.append(if (doubleValue == longValue.toDouble()) longValue else doubleValue)
		} else if (`object`.isLong) {
			buffer.append(`object`.asLong())
		} else if (`object`.isBoolean) {
			buffer.append(`object`.asBoolean())
		} else if (`object`.isNull) {
			buffer.append("null")
		} else
		throw SerializationException("Unknown object type: " + `object`)
	}

	private fun isFlat(`object`: JsonValue): Boolean {
		var child = `object`.child
		while (child != null) {
			if (child.isObject || child.isArray) return false
			child = child.next
		}
		return true
	}

	private fun isNumeric(`object`: JsonValue): Boolean {
		var child = `object`.child
		while (child != null) {
			if (!child.isNumber) return false
			child = child.next
		}
		return true
	}

	private fun indent(count: Int, buffer: StringBuilder) {
		for (i in 0 until count)
			buffer.append('\t')
	}

	override fun iterator() =
		JsonIterator()


	/** Returns a human readable string representing the path from the root of the JSON object graph to this value.  */
	fun trace(): String {
		if (parent == null) {
			if (type == ValueType.array) return "[]"
			return if (type == ValueType.`object`) "{}" else ""
		}
		var trace: String
		if (parent!!.type == ValueType.array) {
			trace = "[]"
			var i = 0
			run {
				var child = parent!!.child
				while (child != null) {
					if (child === this) {
						trace = "[$i]"
						break
					}
					child = child.next
					i++
				}
			}
		} else if (name!!.indexOf('.') <= 0)
			trace = ".\"" + name!!.replace("\"", "\\\"") + "\""
		else
			trace = '.' + name!!
		return parent!!.trace() + trace
	}


	inner class JsonIterator : Iterator<JsonValue>, Iterable<JsonValue> {
		internal var entry = child
		internal var current: JsonValue? = null

		override fun hasNext(): Boolean =
			entry != null

		override fun next(): JsonValue {
			current = entry
			if (current == null)
				throw NoSuchElementException()
			entry = current!!.next
			return current!!
		}

		fun remove() {
			if (current!!.prev == null) {
				child = current!!.next
				if (child != null) child!!.prev = null
			} else {
				current!!.prev!!.next = current!!.next
				if (current!!.next != null) current!!.next!!.prev = current!!.prev
			}
			size--
		}

		override fun iterator(): Iterator<JsonValue> =
			this
	}
}
