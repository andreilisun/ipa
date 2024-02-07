package com.github.andreilisun.ipa

interface Logger {

    fun log(message: String)

    class Verbose : Logger {
        override fun log(message: String) {
            println("[VERBOSE] $message")
        }
    }

    class Disabled : Logger {
        override fun log(message: String) {
        }
    }
}
