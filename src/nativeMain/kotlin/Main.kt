import io.ktor.server.application.log
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import platform.posix.SIGINT
import platform.posix.exit
import platform.posix.signal

private val LOGGER = KtorSimpleLogger("Main")

@OptIn(ExperimentalForeignApi::class)
fun main(): Unit = runBlocking {
    launch(Dispatchers.IO) {
        LOGGER.info("Setting up signal handler...")
        signal(SIGINT, staticCFunction(::handleSignal))
        LOGGER.info("Signal setup done!")
    }
    embeddedServer(CIO, port = 7777) {
        routing {
            get("/") {
                log.info("Hello!")
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}

private fun handleSignal(signalNumber: Int) {
    LOGGER.info("Interrupt: $signalNumber")
    exit(0)
}
