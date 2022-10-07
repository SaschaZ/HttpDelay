import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.delay
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.h3
import kotlinx.html.p
import org.apache.log4j.BasicConfigurator

fun main(args: Array<String>) {
    BasicConfigurator.configure()

    val port = args.getArg("-p")?.toIntOrNull() ?: 8080
    val pathPrefix = args.getArg("-pp")?.let { "$it/" } ?: ""
    println("port=$port - pathPrefix=$pathPrefix")

    embeddedServer(Netty, port) {
        routing {
            get("${pathPrefix}delay") {
                call.parameters["duration"]?.toLongOrNull()?.let { duration ->
                    call.parameters["url"]?.let { url ->
                        call.respondDelayed(duration, url)
                    }
                } ?: call.respondDelayDescription()
            }
        }
    }.start(true)
}

fun Array<String>.getArg(name: String): String? =
    indexOf(name).takeIf { it >= 0 }?.let { get(it + 1) }

private suspend fun ApplicationCall.respondDelayDescription() {
    respondHtml {
        body {
            h3 { +"HTTP Delay" }
            p {
                +"duration: duration in ms to delay"
                br()
                +"url: encoded url to respond"
            }
            p {
                +"example: ../delay?duration=5000&url=https://www.google.de"
            }
        }
    }
}

private val client = HttpClient(OkHttp) {
    engine {
        config {
            followSslRedirects(true)
            followSslRedirects(true)
        }
    }
}

@OptIn(InternalAPI::class)
private suspend fun ApplicationCall.respondDelayed(duration: Long, url: String) {
    val beforeCall = System.currentTimeMillis()
    val input = client.get(url)

    val callDuration = System.currentTimeMillis() - beforeCall
    if (callDuration < duration)
        delay(duration - callDuration)

    respondBytes(input.content.toByteArray(), input.contentType())
}