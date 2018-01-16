# Commons
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/HossainCo/Commons.svg?branch=master)](https://travis-ci.org/HossainCo/Commons)
[![JitPack](https://jitpack.io/v/HossainCo/Commons.svg)](https://jitpack.io/#HossainCo/Commons)

Collection of useful Kotlin and Android utils for development

## Commons

### consume
Run the block and return true or any other result from outside of block
Useful in `when` command.
#### Syntax
```kotlin
// true as return
val result = consume {
	// commands
}

val result = consume("Result") {
	// commands
}

val result = consume({ "onDemand result" }) {
	// commands
}
```
#### Usage
```kotlin
val result = when(id) {
	USERNAME_ID -> consume {
		// ...
	}
	PASSWORD_ID -> consume {
		// ...
	}
	ELSE_ID -> consume(false) {
		// ...
	}
}
```

## Block
Simple function with no return
```kotlin
typealias Block = () -> Unit

val block = Block { 
	// body
}
```

## Provider
Just a no parameter function that return `T`.
Good in on demand calculation.
```kotlin
typealias Provider<T> = () -> T

val data: Provider<String> = { "Hello" }
```

### *.toProvider
Converts normal values to a Provider that provides it!
```kotlin
val provider = 1995.toProvider()

val provider = "HossainCo".toProvider()
```

### Provider.provide
Get Value on Demand.
Nullable on nullable types only.
```kotlin
val value = provider.provide()
```

### Provider.provideOrThrow
Provide value or throw if it's `null`
```kotlin
val value = provider.provideOrThrow()

// custom message
val value = provider.provideOrThrow("provides null data!")
```

### Provider.provideOrDefault
Provide value or use default value / provider
```kotlin
val value = provider.provideOrDefault("Default data")

val value = provideOrDefault("Default data") {
	call
}

// provider value
val value = provider.provideOrDefault({ "OnDemand Default data" })
```

### Provider.tryOrThrow
Try to `provide` or throw error
```kotlin
val value = provider.tryOrThrow()

val value = tryOrThrow {
	// provider block
}

// with custom message
val value = tryOrThrow("Custom message")
```

### Provider.tryOrNull
Try to `provide` or return `null`
```kotlin
val data = provider.tryOrNull()

val data = tryOrNull {
	// provider block
}

// with custom message
val data = tryOrNull("can not receive value!") {
	// provider block
}
```

### Provider.tri
Same as `tryOrNull`

### Provider.tryOrDefault
Try to `provide` or return default value | default provider's value
```kotlin
val data = provider.tryOrDefault("Default value")

val data = tryOrDefault("Default value") {
	// provider block
}

// lazy default value
val data = tryOrDefault({ "OnDemand Default value" }) {
	// provider block
}
```

### Provider.tryNotNullOrThrow
Try to `provide` non-null or throw error
```kotlin
val data = provider.tryNotNullOrThrow("Custom message")

val data = tryNotNullOrThrow {
	// provider block
}
```

### Provider.tryNotNullOrDefault
Try to `provide` non-null or return default value | provider's value
```kotlin
val data = provider.tryNotNullOrDefault("Default value")

val data = tryNotNullOrDefault("Default value") {
	// provider block
}
val data = tryNotNullOrDefault({ "OnDemand Default value" }) {
	// provider block
}
```


## Array

Arrays util

### Array.firstOrDefault

Use `Array.firstOrNull` and return non-null result or return default value or provider's value.
```kotlin
val item = items.firstOrDefault("Item Default") {
	// predicate
}

val item = items.firstOrDefault({ "OnDemand Item Default" }) {
	// predicate
}
```


## LiveData

### LiveData.observe

Observe on google architecture's LiveData,
```kotlin
// if owner is null then observeForEver
liveData.observe(owner) {
	// observer
}

// observeForEver
liveData.observe {
	// observer
}
```

### LiveData.zip
Zip (like Rx) two liveDatas.
```kotlin
val liveData = liveData1.zip(liveData2)
```

### LiveData.map
Map (like Rx) a liveData.
if mapper gets `A` and returns `B` then `LiveData<A>` converts to `LiveData<B>`
```kotlin
val liveData = liveData1.map {
	// mapper
}
```

### LiveData.switchMap
SwitchMap a liveData.
if mapper gets `A` and returns `LiveData<B>` then `LiveData<A>` converts to `LiveData<B>`
```kotlin
val liveData = liveData1.switchMap {
	// mapper
}
```

## RxBus2
see [documention](README.RxBus2.md)

## LiveEvent
see [documention](README.LiveEvent.md)

## PrefKot
see [documention](README.PrefKot.md)

## Gradle
```Groovy
// in root project
allprojects {
  repositories {
    // ...
    maven { url 'https://jitpack.io' }
  }
}
 
// in project
dependencies {
  compile 'com.github.hossainco:Commons:0.1.5'
  // ...
}
```


## License
```text
MIT License
 
Copyright (c) 2017 HossainCo
 
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
