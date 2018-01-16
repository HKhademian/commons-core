package ir.hossainco.commons.types

interface Resettable {
	/** Resets the object for reuse. Object references should be nulled and fields may be set to default values. */
	fun reset()
}
