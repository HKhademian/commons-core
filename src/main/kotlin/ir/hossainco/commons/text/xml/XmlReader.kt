@file:Suppress("unused", "ObjectPropertyName", "MemberVisibilityCanPrivate")

package ir.hossainco.commons.text.xml

import ir.hossainco.commons.utils.removeLast
import ir.hossainco.commons.text.SerializationException
import ir.hossainco.commons.types.call

/** Lightweight XML parser. Supports a subset of XML features: elements, attributes, text, predefined entities, CDATA, mixed
 * content. Namespaces are parsed as part of the element or attribute name. Prologs and doctypes are ignored. Only 8-bit character
 * encodings are supported. Input is assumed to be well formed.
 *
 * The default behavior is to parse the XML into a DOM. Extends this class and override methods to perform event driven parsing.
 * When this is done, the parse methods will return null.
 */
class XmlReader {
	private val elements = ArrayList<Element>(8)
	private var root: Element? = null
	private var current: Element? = null
	private val textBuffer = StringBuilder(64)

	fun parse(xml: String): Element? {
		val data = xml.toCharArray()
		return parse(data, 0, data.size)
	}

//	fun parse(reader: Reader): Element? {
//		try {
//			var data = CharArray(1024)
//			var offset = 0
//			while (true) {
//				val length = reader.read(data, offset, data.size - offset)
//				if (length == -1) break
//				if (length == 0) {
//					val newData = CharArray(data.size * 2)
//					System.arraycopy(data, 0, newData, 0, data.size)
//					data = newData
//				} else
//					offset += length
//			}
//			return parse(data, 0, offset)
//		} catch (ex: IOException) {
//			throw SerializationException(ex)
//		} finally {
//			StreamUtils.closeQuietly(reader)
//		}
//	}
//
//	fun parse(input: InputStream): Element? =
//		try {
//			parse(InputStreamReader(input, "UTF-8"))!!
//		} catch (ex: IOException) {
//			throw SerializationException(ex)
//		} finally {
//			StreamUtils.closeQuietly(input)
//		}
//
//	fun parse(file: FileHandle): Element =
//		try {
//			parse(file.reader("UTF-8"))!!
//		} catch (ex: Exception) {
//			throw SerializationException("Error parsing file: " + file, ex)
//		}

	fun parse(data: CharArray, offset: Int, length: Int): Element? {
		var cs: Int
		var p = offset

		var s = 0
		var attributeName: String? = null
		var hasBody = false

		cs = xml_start

		call {
			var _klen: Int
			var _trans: Int
			var _acts: Int
			var _nacts: Int
			var _keys: Int
			var _goto_targ = 0

			_goto@ while (true) {
				when (_goto_targ) {
					0 -> {
						if (p == length) {
							_goto_targ = 4
							continue@_goto
						}
						if (cs == 0) {
							_goto_targ = 5
							continue@_goto
						}
						_match@ do {
							_keys = _xml_key_offsets[cs].toInt()
							_trans = _xml_index_offsets[cs].toInt()
							_klen = _xml_single_lengths[cs].toInt()
							if (_klen > 0) {
								var _lower = _keys
								var _mid: Int
								var _upper = _keys + _klen - 1
								while (true) {
									if (_upper < _lower) break

									_mid = _lower + (_upper - _lower shr 1)
									if (data[p] < _xml_trans_keys[_mid])
										_upper = _mid - 1
									else if (data[p] > _xml_trans_keys[_mid])
										_lower = _mid + 1
									else {
										_trans += _mid - _keys
										break@_match
									}
								}
								_keys += _klen
								_trans += _klen
							}

							_klen = _xml_range_lengths[cs].toInt()
							if (_klen > 0) {
								var _lower = _keys
								var _mid: Int
								var _upper = _keys + (_klen shl 1) - 2
								while (true) {
									if (_upper < _lower) break

									_mid = _lower + (_upper - _lower shr 1 and 1.inv())
									if (data[p] < _xml_trans_keys[_mid])
										_upper = _mid - 2
									else if (data[p] > _xml_trans_keys[_mid + 1])
										_lower = _mid + 2
									else {
										_trans += _mid - _keys shr 1
										break@_match
									}
								}
								_trans += _klen
							}
						} while (false)

						_trans = _xml_indicies[_trans].toInt()
						cs = _xml_trans_targs[_trans].toInt()

						if (_xml_trans_actions[_trans].toInt() != 0) {
							_acts = _xml_trans_actions[_trans].toInt()
							_nacts = _xml_actions[_acts++].toInt()
							while (_nacts-- > 0) {
								when (_xml_actions[_acts++]) {
									0.toByte() -> s = p
									1.toByte() -> {
										val c = data[s]
										if (c == '?' || c == '!') {
											if (data[s + 1] == '[' && //

												data[s + 2] == 'C' && //

												data[s + 3] == 'D' && //

												data[s + 4] == 'A' && //

												data[s + 5] == 'T' && //

												data[s + 6] == 'A' && //

												data[s + 7] == '[') {
												s += 8
												p = s + 2
												while (data[p - 2] != ']' || data[p - 1] != ']' || data[p] != '>')
													p++
												text(String(data, s, p - s - 2))
											} else if (c == '!' && data[s + 1] == '-' && data[s + 2] == '-') {
												p = s + 3
												while (data[p] != '-' || data[p + 1] != '-' || data[p + 2] != '>')
													p++
												p += 2
											} else
												while (data[p] != '>')
													p++
											cs = 15
											_goto_targ = 2
											if (true) continue@_goto
										}
										hasBody = true
										open(String(data, s, p - s))
									}
									2.toByte() -> {
										hasBody = false
										close()
										cs = 15
										_goto_targ = 2
										if (true) continue@_goto
									}
									3.toByte() -> {
										close()
										cs = 15
										_goto_targ = 2
										if (true) continue@_goto
									}
									4.toByte() -> {
										if (hasBody) {
											cs = 15
											_goto_targ = 2
											if (true) continue@_goto
										}
									}
									5.toByte() -> {
										attributeName = String(data, s, p - s)
									}
									6.toByte() -> {
										attribute(attributeName!!, String(data, s, p - s))
									}
									7.toByte() -> {
										var end = p
										_goto_while@ while (end != s) {
											when (data[end - 1]) {
												' ', '\t', '\n', '\r' -> {
													end--
													continue@_goto_while
												}
											}
											break
										}
										var current = s
										var entityFound = false
										while (current != end) {
											if (data[current++] != '&') continue
											val entityStart = current
											while (current != end) {
												if (data[current++] != ';') continue
												textBuffer.append(data, s, entityStart - s - 1)
												val name = String(data, entityStart, current - entityStart - 1)
												val value = entity(name)
												textBuffer.append(value ?: name)
												s = current
												entityFound = true
												break
											}
										}
										if (entityFound) {
											if (s < end) textBuffer.append(data, s, end - s)
											text(textBuffer.toString())
											textBuffer.setLength(0)
										} else
											text(String(data, s, end - s))
									}
								}// line 286 "XmlReader.java"
							}
						}
						if (cs == 0) {
							_goto_targ = 5
							continue@_goto
						}
						if (++p != length) {
							_goto_targ = 1
							continue@_goto
						}
					}
					1 -> {
						_match@ do {
							_keys = _xml_key_offsets[cs].toInt()
							_trans = _xml_index_offsets[cs].toInt()
							_klen = _xml_single_lengths[cs].toInt()
							if (_klen > 0) {
								var _lower = _keys
								var _mid: Int
								var _upper = _keys + _klen - 1
								while (true) {
									if (_upper < _lower) break
									_mid = _lower + (_upper - _lower shr 1)
									if (data[p] < _xml_trans_keys[_mid])
										_upper = _mid - 1
									else if (data[p] > _xml_trans_keys[_mid])
										_lower = _mid + 1
									else {
										_trans += _mid - _keys
										break@_match
									}
								}
								_keys += _klen
								_trans += _klen
							}
							_klen = _xml_range_lengths[cs].toInt()
							if (_klen > 0) {
								var _lower = _keys
								var _mid: Int
								var _upper = _keys + (_klen shl 1) - 2
								while (true) {
									if (_upper < _lower) break
									_mid = _lower + (_upper - _lower shr 1 and 1.inv())
									if (data[p] < _xml_trans_keys[_mid])
										_upper = _mid - 2
									else if (data[p] > _xml_trans_keys[_mid + 1])
										_lower = _mid + 2
									else {
										_trans += _mid - _keys shr 1
										break@_match
									}
								}
								_trans += _klen
							}
						} while (false)
						_trans = _xml_indicies[_trans].toInt()
						cs = _xml_trans_targs[_trans].toInt()
						if (_xml_trans_actions[_trans].toInt() != 0) {
							_acts = _xml_trans_actions[_trans].toInt()
							_nacts = _xml_actions[_acts++].toInt()
							while (_nacts-- > 0) {
								when (_xml_actions[_acts++]) {
									0.toByte() -> {
										s = p
									}
									1.toByte() -> {
										val c = data[s]
										if (c == '?' || c == '!') {
											if (data[s + 1] == '[' && data[s + 2] == 'C' && data[s + 3] == 'D' && data[s + 4] == 'A' && data[s + 5] == 'T' && data[s + 6] == 'A' && data[s + 7] == '[') {
												s += 8
												p = s + 2
												while (data[p - 2] != ']' || data[p - 1] != ']' || data[p] != '>')
													p++
												text(String(data, s, p - s - 2))
											} else if (c == '!' && data[s + 1] == '-' && data[s + 2] == '-') {
												p = s + 3
												while (data[p] != '-' || data[p + 1] != '-' || data[p + 2] != '>')
													p++
												p += 2
											} else
												while (data[p] != '>')
													p++
											cs = 15
											_goto_targ = 2
											if (true) continue@_goto
										}
										hasBody = true
										open(String(data, s, p - s))
									}
									2.toByte() -> {
										hasBody = false
										close()
										cs = 15
										_goto_targ = 2
										if (true) continue@_goto
									}
									3.toByte() -> {
										close()
										cs = 15
										_goto_targ = 2
										if (true) continue@_goto
									}
									4.toByte() -> {
										if (hasBody) {
											cs = 15
											_goto_targ = 2
											if (true) continue@_goto
										}
									}
									5.toByte() -> {
										attributeName = String(data, s, p - s)
									}
									6.toByte() -> {
										attribute(attributeName!!, String(data, s, p - s))
									}
									7.toByte() -> {
										var end = p
										_goto_while@ while (end != s) {
											when (data[end - 1]) {
												' ', '\t', '\n', '\r' -> {
													end--
													continue@_goto_while
												}
											}
											break
										}
										var current = s
										var entityFound = false
										while (current != end) {
											if (data[current++] != '&') continue
											val entityStart = current
											while (current != end) {
												if (data[current++] != ';') continue
												textBuffer.append(data, s, entityStart - s - 1)
												val name = String(data, entityStart, current - entityStart - 1)
												val value = entity(name)
												textBuffer.append(value ?: name)
												s = current
												entityFound = true
												break
											}
										}
										if (entityFound) {
											if (s < end) textBuffer.append(data, s, end - s)
											text(textBuffer.toString())
											textBuffer.setLength(0)
										} else
											text(String(data, s, end - s))
									}
								}
							}
						}
						if (cs == 0) {
							_goto_targ = 5
							continue@_goto
						}
						if (++p != length) {
							_goto_targ = 1
							continue@_goto
						}
					}

					2 -> {
						if (cs == 0) {
							_goto_targ = 5
							continue@_goto
						}
						if (++p != length) {
							_goto_targ = 1
							continue@_goto
						}
					}
				}
				break
			}
		}

		if (p < length) {
			var lineNumber = 1
			for (i in 0 until p)
				if (data[i] == '\n')
					lineNumber++
			throw SerializationException(
				"Error parsing XML on line " + lineNumber + " near: " + String(data, p, Math.min(32, length - p)))
		} else if (elements.isNotEmpty()) {
			val element = elements.last()
			elements.clear()
			throw SerializationException("Error parsing XML, unclosed element: " + element.name)
		}
		val root = this.root
		this.root = null
		return root
	}

	protected fun open(name: String) {
		val child = Element(name, current)
		val parent = current
		parent!!.addChild(child)
		elements.add(child)
		current = child
	}

	protected fun attribute(name: String, value: String) {
		current!!.setAttribute(name, value)
	}

	protected fun entity(name: String): String? {
		if (name == "lt") return "<"
		if (name == "gt") return ">"
		if (name == "amp") return "&"
		if (name == "apos") return "'"
		if (name == "quot") return "\""
		return if (name.startsWith("#x")) Character.toString(Integer.parseInt(name.substring(2), 16).toChar()) else null
	}

	protected fun text(text: String) {
		val existing = current!!.text
		current!!.text = if (existing != null) existing + text else text
	}

	protected fun close() {
		root = elements.removeLast()
		current = if (elements.isNotEmpty()) elements.last() else null
	}

	class Element(val name: String, val parent: Element?) {
		var attributes: HashMap<String, String>? = null
			private set
		private var children: ArrayList<Element>? = null
		var text: String? = null

		val childCount: Int
			get() = children?.size ?: 0

		/** @throws RuntimeException if the attribute was not found.
		 */
		fun getAttribute(name: String): String {
			if (attributes == null) throw RuntimeException("Element " + this.name + " doesn't have attribute: " + name)
			return attributes!![name] ?: throw RuntimeException("Element " + this.name + " doesn't have attribute: " + name)
		}

		fun getAttribute(name: String, defaultValue: String?): String? {
			return if (attributes == null) defaultValue else attributes!![name] ?: return defaultValue
		}

		fun hasAttribute(name: String): Boolean {
			return if (attributes == null) false else attributes!!.containsKey(name)
		}

		fun setAttribute(name: String, value: String) {
			if (attributes == null) attributes = HashMap(8)
			attributes!!.put(name, value)
		}

		/** @throws RuntimeException if the element has no children. */
		fun getChild(index: Int): Element {
			if (children == null) throw RuntimeException("Element has no children: " + name)
			return children!![index]
		}

		fun addChild(element: Element) {
			if (children == null)
				children = ArrayList(8)
			children!!.add(element)
		}

		fun removeChild(index: Int) {
			children?.removeAt(index)
		}

		fun removeChild(child: Element) {
			children?.remove(child)
		}

		fun remove() {
			parent!!.removeChild(this)
		}

		override fun toString() =
			toString("")

		fun toString(indent: String): String {
			val buffer = StringBuilder(128)
			buffer.append(indent)
			buffer.append('<')
			buffer.append(name)
			if (attributes != null) {
				for (entry in attributes!!.entries) {
					buffer.append(' ')
					buffer.append(entry.key)
					buffer.append("=\"")
					buffer.append(entry.value)
					buffer.append('\"')
				}
			}
			if (children == null && (text == null || text!!.isEmpty()))
				buffer.append("/>")
			else {
				buffer.append(">\n")
				val childIndent = indent + '\t'
				if (text != null && text!!.isNotEmpty()) {
					buffer.append(childIndent)
					buffer.append(text!!)
					buffer.append('\n')
				}
				if (children != null) {
					for (child in children!!) {
						buffer.append(child.toString(childIndent))
						buffer.append('\n')
					}
				}
				buffer.append(indent)
				buffer.append("</")
				buffer.append(name)
				buffer.append('>')
			}
			return buffer.toString()
		}

		/** @param name the name of the child [Element]
		 * @return the first child having the given name or null, does not recurse
		 */
		fun getChildByName(name: String): Element? {
			if (children == null) return null
			for (i in 0 until children!!.size) {
				val element = children!![i]
				if (element.name == name)
					return element
			}
			return null
		}

		fun hasChild(name: String): Boolean {
			return if (children == null) false else getChildByName(name) != null
		}

		/** @param name the name of the child [Element]
		 * @return the first child having the given name or null, recurses
		 */
		fun getChildByNameRecursive(name: String): Element? {
			if (children == null) return null
			for (i in 0 until children!!.size) {
				val element = children!![i]
				if (element.name == name) return element
				val found = element.getChildByNameRecursive(name)
				if (found != null) return found
			}
			return null
		}

		fun hasChildRecursive(name: String): Boolean {
			return if (children == null) false else getChildByNameRecursive(name) != null
		}

		/** @param name the name of the children
		 * @return the children with the given name or an empty [Array]
		 */
		fun getChildrenByName(name: String): ArrayList<Element> {
			val result = ArrayList<Element>()
			if (children == null) return result
			for (i in 0 until children!!.size) {
				val child = children!![i]
				if (child.name == name) result.add(child)
			}
			return result
		}

		/** @param name the name of the children
		 * @return the children with the given name or an empty [Array]
		 */
		fun getChildrenByNameRecursively(name: String): ArrayList<Element> {
			val result = ArrayList<Element>()
			getChildrenByNameRecursively(name, result)
			return result
		}

		private fun getChildrenByNameRecursively(name: String, result: ArrayList<Element>) {
			if (children == null) return
			for (i in 0 until children!!.size) {
				val child = children!![i]
				if (child.name == name) result.add(child)
				child.getChildrenByNameRecursively(name, result)
			}
		}

		/** @throws RuntimeException if the attribute was not found. */
		fun getFloatAttribute(name: String): Float =
			getAttribute(name).toFloat()

		fun getFloatAttribute(name: String, defaultValue: Float): Float =
			getAttribute(name, null)?.toFloatOrNull() ?: defaultValue

		/** @throws RuntimeException if the attribute was not found. */
		fun getIntAttribute(name: String): Int {
			return getAttribute(name).toInt()
		}

		fun getIntAttribute(name: String, defaultValue: Int): Int =
			getAttribute(name, null)?.toIntOrNull() ?: defaultValue

		/** @throws RuntimeException if the attribute was not found. */
		fun getBooleanAttribute(name: String): Boolean {
			return getAttribute(name).toBoolean()
		}

		fun getBooleanAttribute(name: String, defaultValue: Boolean): Boolean =
			getAttribute(name, null)?.toBoolean() ?: defaultValue

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		operator fun get(name: String): String =
			get(name, null) ?: throw RuntimeException("Element " + this.name + " doesn't have attribute or child: " + name)

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		operator fun get(name: String, defaultValue: String?): String? {
			if (attributes != null) {
				val value = attributes!![name]
				if (value != null) return value
			}
			val child = getChildByName(name) ?: return defaultValue
			return child.text ?: return defaultValue
		}

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		fun getInt(name: String): Int =
			get(name, null)?.toIntOrNull() ?: throw RuntimeException("Element " + this.name + " doesn't have attribute or child: " + name)

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		fun getInt(name: String, defaultValue: Int): Int =
			get(name, null)?.toIntOrNull() ?: defaultValue

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		fun getFloat(name: String): Float =
			get(name, null)?.toFloatOrNull() ?: throw RuntimeException("Element " + this.name + " doesn't have attribute or child: " + name)

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		fun getFloat(name: String, defaultValue: Float): Float =
			get(name, null)?.toFloatOrNull() ?: defaultValue

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		fun getBoolean(name: String): Boolean =
			get(name, null)?.toBoolean() ?: throw RuntimeException("Element " + this.name + " doesn't have attribute or child: " + name)

		/** Returns the attribute value with the specified name, or if no attribute is found, the text of a child with the name.
		 * @throws RuntimeException if no attribute or child was not found.
		 */
		fun getBoolean(name: String, defaultValue: Boolean): Boolean =
			get(name, null)?.toBoolean() ?: defaultValue
	}

	companion object {
		private val _xml_actions = byteArrayOf(0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 2, 0, 6, 2, 1, 4, 2, 2, 4)
		private val _xml_key_offsets = byteArrayOf(0, 0, 4, 9, 14, 20, 26, 30, 35, 36, 37, 42, 46, 50, 51, 52, 56, 57, 62, 67, 73, 79, 83, 88, 89, 90, 95, 99, 103, 104, 108, 109, 110, 111, 112, 115)
		private val _xml_trans_keys = charArrayOf(32.toChar(), 60.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 47.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 47.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 47.toChar(), 61.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 47.toChar(), 61.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 61.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 34.toChar(), 39.toChar(), 9.toChar(), 13.toChar(), 34.toChar(), 34.toChar(), 32.toChar(), 47.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 39.toChar(), 39.toChar(), 32.toChar(), 60.toChar(), 9.toChar(), 13.toChar(), 60.toChar(), 32.toChar(), 47.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 47.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 47.toChar(), 61.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 47.toChar(), 61.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 61.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 34.toChar(), 39.toChar(), 9.toChar(), 13.toChar(), 34.toChar(), 34.toChar(), 32.toChar(), 47.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 32.toChar(), 62.toChar(), 9.toChar(), 13.toChar(), 60.toChar(), 32.toChar(), 47.toChar(), 9.toChar(), 13.toChar(), 62.toChar(), 62.toChar(), 39.toChar(), 39.toChar(), 32.toChar(), 9.toChar(), 13.toChar(), 0.toChar())
		private val _xml_single_lengths = byteArrayOf(0, 2, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 1, 2, 1, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 2, 1, 1, 1, 1, 1, 0)
		private val _xml_range_lengths = byteArrayOf(0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0)
		private val _xml_index_offsets = shortArrayOf(0, 0, 4, 9, 14, 20, 26, 30, 35, 37, 39, 44, 48, 52, 54, 56, 60, 62, 67, 72, 78, 84, 88, 93, 95, 97, 102, 106, 110, 112, 116, 118, 120, 122, 124, 127)
		private val _xml_indicies = byteArrayOf(0, 2, 0, 1, 2, 1, 1, 2, 3, 5, 6, 7, 5, 4, 9, 10, 1, 11, 9, 8, 13, 1, 14, 1, 13, 12, 15, 16, 15, 1, 16, 17, 18, 16, 1, 20, 19, 22, 21, 9, 10, 11, 9, 1, 23, 24, 23, 1, 25, 11, 25, 1, 20, 26, 22, 27, 29, 30, 29, 28, 32, 31, 30, 34, 1, 30, 33, 36, 37, 38, 36, 35, 40, 41, 1, 42, 40, 39, 44, 1, 45, 1, 44, 43, 46, 47, 46, 1, 47, 48, 49, 47, 1, 51, 50, 53, 52, 40, 41, 42, 40, 1, 54, 55, 54, 1, 56, 42, 56, 1, 57, 1, 57, 34, 57, 1, 1, 58, 59, 58, 51, 60, 53, 61, 62, 62, 1, 1, 0)
		private val _xml_trans_targs = byteArrayOf(1, 0, 2, 3, 3, 4, 11, 34, 5, 4, 11, 34, 5, 6, 7, 6, 7, 8, 13, 9, 10, 9, 10, 12, 34, 12, 14, 14, 16, 15, 17, 16, 17, 18, 30, 18, 19, 26, 28, 20, 19, 26, 28, 20, 21, 22, 21, 22, 23, 32, 24, 25, 24, 25, 27, 28, 27, 29, 31, 35, 33, 33, 34)
		private val _xml_trans_actions = byteArrayOf(0, 0, 0, 1, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 1, 0, 1, 0, 0, 0, 15, 1, 0, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 0, 0, 7, 1, 0, 0)

		internal val xml_start = 1
		internal val xml_first_final = 34
		internal val xml_error = 0

		internal val xml_en_elementBody = 15
		internal val xml_en_main = 1
	}
}
