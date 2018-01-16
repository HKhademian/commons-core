@file:Suppress("NOTHING_TO_INLINE", "unused")
@file:JvmName("Providers")
@file:JvmMultifileClass

package ir.hossainco.commons.types

typealias Provider<R> = () -> R

inline fun <R> Provider<R>.provide() = this()

fun <T> T.toProvider(): Provider<T>
	= ({ this })

fun <T> T?.toProviderOrNull(): Provider<T>?
	= if (this != null) ({ this }) else null


inline fun <T> provideOrThrow(message: String? = null, provider: Provider<T?>)
	= provider.provide() ?: throw RuntimeException(message)


inline fun <T> provideOrDefault(default: T, provider: Provider<T?>)
	= provider.provide() ?: default


inline fun <T> provideOrDefault(default: Provider<T>, provider: Provider<T?>)
	= provider.provide() ?: default.provide()


inline fun <T> tryOrThrow(message: String? = null, provider: Provider<T>)
	= try {
	provider.provide()
} catch (e: Throwable) {
	throw if (message != null) RuntimeException(message, e) else RuntimeException(e)
}


inline fun <T> tryOrNull(provider: Provider<T>)
	= try {
	provider.provide()
} catch (e: Throwable) {
	null
}

inline fun <T> tri(provider: Provider<T>)
	= tryOrNull(provider)


inline fun <T> tryOrDefault(default: T, provider: Provider<T>)
	= try {
	provider.provide()
} catch (e: Throwable) {
	default
}


inline fun <T> tryOrDefault(default: Provider<T>, provider: Provider<T>)
	= try {
	provider.provide()
} catch (e: Throwable) {
	default.provide()
}


inline fun <T> tryNotNullOrThrow(message: String? = null, provider: Provider<T>)
	= try {
	provider.provide()!!
} catch (e: Throwable) {
	throw if (message != null) RuntimeException(message, e) else RuntimeException(e)
}


inline fun <T> tryNotNullOrDefault(default: T, provider: Provider<T?>)
	= try {
	provider.provide()!!
} catch (e: Throwable) {
	default
}


inline fun <T> tryNotNullOrDefault(default: Provider<T>, provider: Provider<T?>)
	= try {
	provider.provide()!!
} catch (e: Throwable) {
	default.provide()
}
