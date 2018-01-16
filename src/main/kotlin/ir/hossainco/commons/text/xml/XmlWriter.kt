@file:Suppress("unused", "MemberVisibilityCanPrivate")

package ir.hossainco.commons.text.xml

import ir.hossainco.commons.utils.removeLast
import java.io.IOException
import java.io.Writer

/**
 * Builder style API for emitting XML.
 *
 * val writer = StringWriter()
 * val xml = XmlWriter(writer)
 * xml.element("meow")
 * .attribute("moo", "cow")
 * .element("child")
 * .attribute("moo", "cow")
 * .element("child")
 * .attribute("moo", "cow")
 * .text("XML is like violence. If it doesn't solve your problem, you're not using enough of it.")
 * .pop()
 * .pop()
 * .pop()
 *
 * System.out.println(writer)
 */
class XmlWriter(private val writer: Writer) : Writer() {
	private val stack = ArrayList<String>()
	private var currentElement: String? = null
	private var indentNextClose: Boolean = false
	var indent: Int = 0

	private fun Writer.write(c: Char) =
		write(c.toInt())

	@Throws(IOException::class)
	private fun indent() {
		var count = indent
		if (currentElement != null) count++
		for (i in 0 until count)
			writer.write('\t')
	}

	@Throws(IOException::class)
	fun element(name: String): XmlWriter {
		if (startElementContent()) writer.write('\n')
		indent()
		writer.write('<')
		writer.write(name)
		currentElement = name
		return this
	}

	@Throws(IOException::class)
	fun element(name: String, text: Any): XmlWriter {
		return element(name).text(text).pop()
	}

	@Throws(IOException::class)
	private fun startElementContent(): Boolean {
		if (currentElement == null) return false
		indent++
		stack.add(currentElement!!)
		currentElement = null
		writer.write(">")
		return true
	}

	@Throws(IOException::class)
	fun attribute(name: String, value: Any?): XmlWriter {
		if (currentElement == null) throw IllegalStateException()
		writer.write(' ')
		writer.write(name)
		writer.write("=\"")
		writer.write(value?.toString() ?: "null")
		writer.write('"')
		return this
	}

	@Throws(IOException::class)
	fun text(text: Any?): XmlWriter {
		startElementContent()
		val string = text?.toString() ?: "null"
		indentNextClose = string.length > 64
		if (indentNextClose) {
			writer.write('\n')
			indent()
		}
		writer.write(string)
		if (indentNextClose) writer.write('\n')
		return this
	}

	@Throws(IOException::class)
	fun pop(): XmlWriter {
		if (currentElement != null) {
			writer.write("/>\n")
			currentElement = null
		} else {
			indent = Math.max(indent - 1, 0)
			if (indentNextClose) indent()
			writer.write("</")
			writer.write(stack.removeLast())
			writer.write(">\n")
		}
		indentNextClose = true
		return this
	}

	/** Calls [.pop] for each remaining open element, if any, and closes the stream.  */
	@Throws(IOException::class)
	override fun close() {
		while (stack.isNotEmpty())
			pop()
		writer.close()
	}

	@Throws(IOException::class)
	override fun write(cbuf: CharArray?, off: Int, len: Int) {
		startElementContent()
		writer.write(cbuf, off, len)
	}

	@Throws(IOException::class)
	override fun flush() {
		writer.flush()
	}
}

//private fun Writer.write(cbuf: CharArray, off: Int, len: Int) {}
