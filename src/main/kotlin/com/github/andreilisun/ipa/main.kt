@file:JvmName("Main")

package com.github.andreilisun.ipa

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch
import com.github.ajalt.clikt.parameters.types.file
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) = Ipa().main(args)

class Ipa : CliktCommand(help = "Add English IPA transcription to a list of words.") {
    private val inputFile by argument(
        "input-file",
        "Path to the input file. Each word of the input file should be on a new line."
    ).file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )
    private val outputFile by argument("output-file", "Path to the output file.").file(
        canBeDir = false
    )
    private val outputDelimiter by option(
        "--output-delimiter",
        help = "Each word-transcription pair always starts from a new line. " +
                "This option is used to specify how a word and transcription are separated. " +
                "Default is comma: \"word,transcription\"."
    ).default(",")
    private val logger: Logger by option(
        "--verbose",
        help = "Enable verbose logging."
    ).switch<Logger>("--verbose" to Logger.Verbose()).default(Logger.Disabled())

    override fun run() {
        val lookupInstructions = getLookupInstructions()
        if (lookupInstructions.isLookupAllowed) {
            val words = inputFile.readLines()
            val wordsWithIpa = words.asSequence()
                .onEach { Thread.sleep(TimeUnit.SECONDS.toMillis(lookupInstructions.lookupDelayInSeconds)) }
                .map { "$it$outputDelimiter${lookupIpa(it)}" }
                .onEach { logger.log(it) }
            outputFile.printWriter().use { out ->
                wordsWithIpa.forEach {
                    out.println(it)
                }
            }
        } else {
            echo("Lookup is not allowed. Restricted by Cambridge Dictionary.", err = true)
        }
    }
}