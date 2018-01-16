package ir.hossainco.commons.text.json

class PrettyPrintSettings(
	var outputType: OutputType? = null,

	/** If an object on a single line fits this many columns, it won't wrap. */
	var singleLineColumns: Int = 0,

	/** Arrays of floats won't wrap. */
	var wrapNumericArrays: Boolean = false
)
