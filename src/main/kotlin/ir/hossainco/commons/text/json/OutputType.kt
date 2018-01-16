package ir.hossainco.commons.text.json

import ir.hossainco.commons.text.StringBuilder

enum class OutputType {
	/** Normal JSON, with all its double quotes.  */
	json,
	/** Like JSON, but names are only double quoted if necessary.  */
	javascript,
	/** Like JSON, but:
	 *
	 *  * Names only require double quotes if they start with `space` or any of `":,}/` or they contain
	 * C style comments or `:`.
	 *  * Values only require double quotes if they start with `space` or any of `":,{[]/` or they
	 * contain C style comments or any of `}],` or they are equal to `true`,
	 * `false` , or `null`.
	 *  * Newlines are treated as commas, making commas optional in many cases.
	 *  * C style comments may be used
	 */
	minimal;

	fun quoteValue(value: Any?): String {
		if (value == null) return "null"
		val string = value.toString()
		if (value is Number || value is Boolean) return string
		val buffer = StringBuilder(string)
		buffer.replace('\\', "\\\\").replace('\r', "\\r").replace('\n', "\\n").replace('\t', "\\t")
		if ((this == OutputType.minimal && string != "true" && string != "false" && string != "null"
			&& !string.contains("//") && !string.contains("/*"))) {
			val length = buffer.length
			if (length > 0 && buffer[length - 1] != ' ' && minimalValuePattern.matches(buffer))
				return buffer.toString()
		}
		return "\"" + buffer.replace("\"", "\\\"").toString() + "\""
	}

	fun quoteName(value: String): String {
		val buffer = StringBuilder(value)
		buffer.replace('\\', "\\\\").replace('\r', "\\r").replace('\n', "\\n").replace('\t', "\\t")
		if (this == minimal) {
			if (!value.contains("//") && !value.contains("/*") && minimalNamePattern.matches(buffer))
				return buffer.toString()
			if (javascriptPattern.matches(buffer))
				return buffer.toString()
		}
		if (this == javascript && javascriptPattern.matches(buffer)) return buffer.toString()
		return "\"" + buffer.replace("\"", "\\\"").toString() + "\""
	}

	companion object {
		var default = json

		private val javascriptPattern = "^[a-zA-Z_$][a-zA-Z_$0-9]*$".toRegex()
		private val minimalNamePattern = "^[^\":,}/ ][^:]*$".toRegex()
		private val minimalValuePattern = "^[^\":,{\\[\\]/ ][^}\\],]*$".toRegex()
	}
}

