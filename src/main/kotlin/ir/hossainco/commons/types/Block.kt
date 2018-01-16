@file:Suppress("unused")
@file:JvmName("Blocks")
@file:JvmMultifileClass

package ir.hossainco.commons.types

typealias Block = () -> Unit

inline fun call(block: Block) =
	block.invoke()

inline fun <T> consume(result: T, block: Block): T {
	block.invoke()
	return result
}

inline fun consume(result: Boolean = true, block: Block)
	= consume<Boolean>(result, block)
