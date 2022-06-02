import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
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

fun main(args: Array<String>) {
    val port = args.indexOf("-p").takeIf { it >= 0 }?.let { args[it + 1] }?.toIntOrNull() ?: 8080
    val pathPrefix = args.indexOf("-pp").takeIf { it >= 0 }?.let { args[it + 1] }?.let { "$it/" } ?: ""

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

private suspend fun ApplicationCall.respondDelayDescription() {
    respondHtml {
        body {
            h3 { +"HTTP Delay" }
            p {
                +"duration: duration in ms to delay"
                br()
                +"url: encoded url to respond"
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

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(InternalAPI::class)
private suspend fun ApplicationCall.respondDelayed(duration: Long, url: String) {
    delay(duration)

    val input = client.get(url).content
    respondBytes(input.toByteArray())
}