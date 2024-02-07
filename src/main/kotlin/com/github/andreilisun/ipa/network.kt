package com.github.andreilisun.ipa

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import java.lang.reflect.Type

const val cambridgeBaseUrl = "https://dictionary.cambridge.org"
const val cambridgeUsDictionaryPath = "/us/dictionary/"
const val cambridgeWordLookupPath = "${cambridgeUsDictionaryPath}english/"

fun getLookupInstructions(): LookupInstructions {
    val crawlInstructions = getAllCrawlInstructions().crawlInstructionsByUserAgent["*"]
        ?: UserAgentCrawlInstructions()

    return LookupInstructions(
        !crawlInstructions.disallowed.contains(cambridgeUsDictionaryPath),
        crawlInstructions.crawlDelayInSeconds
    )
}

fun lookupIpa(word: String) = Jsoup.connect("$cambridgeBaseUrl$cambridgeWordLookupPath$word")
    .get()
    // TODO: Grabs first available transcription from the page.
    //  Do this smarter: (1) Find the US transcription. (2) Be ready for "strong" and "weak" transcriptions.
    .select("span.ipa.dipa.lpr-2.lpl-1")
    .first()?.text()

private fun getAllCrawlInstructions(): CrawlInstructions {
    val retrofit = Retrofit.Builder()
        .baseUrl(cambridgeBaseUrl)
        .addConverterFactory(RobotsConverterFactory())
        .build()
    val crawlInstructionsService = retrofit.create(CrawlInstructionsService::class.java)
    val response = crawlInstructionsService.getCrawlInstructions().execute()

    return response.body()!!
}

private class RobotsConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) =
        if (type == CrawlInstructions::class.java) RobotsResponseConverter()
        else super.responseBodyConverter(type, annotations, retrofit)
}

private class RobotsResponseConverter : Converter<ResponseBody, CrawlInstructions> {

    override fun convert(value: ResponseBody): CrawlInstructions {
        val crawlInstructionsByUserAgent = HashMap<String, UserAgentCrawlInstructions>()
        val robotsLines = value.string()
        val userAgentBlocks = robotsLines.split("User-agent:")

        userAgentBlocks.asSequence()
            .filter { it.isNotBlank() }
            .forEach { userAgentBlock ->
                val userAgentLines = userAgentBlock.lines()
                val userAgent = userAgentLines[0].trim()
                var crawlDelay = 0L
                val disallowedPaths = HashSet<String>()
                for (index in 1..userAgentLines.lastIndex) {
                    if (userAgentLines[index].contains("Crawl-delay:")) {
                        crawlDelay = userAgentLines[index].split(":")[1].trim().toLong()
                    } else if (userAgentLines[index].contains("Disallow:")) {
                        disallowedPaths.add(userAgentLines[index].split(":")[1].trim())
                    }
                }
                crawlInstructionsByUserAgent[userAgent] = UserAgentCrawlInstructions(crawlDelay, disallowedPaths)
            }

        return CrawlInstructions(crawlInstructionsByUserAgent)
    }
}

class LookupInstructions(val isLookupAllowed: Boolean, val lookupDelayInSeconds: Long)

private data class UserAgentCrawlInstructions(
    val crawlDelayInSeconds: Long = 0L,
    val disallowed: Set<String> = emptySet()
)

private data class CrawlInstructions(val crawlInstructionsByUserAgent: Map<String, UserAgentCrawlInstructions>)

private interface CrawlInstructionsService {
    @GET("/robots.txt")
    fun getCrawlInstructions(): Call<CrawlInstructions>
}

