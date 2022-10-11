package me.shmulik.tictactoe

import BoardMove
import Room
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

val rooms = ConcurrentHashMap<String, Room>()
fun main() {
    embeddedServer(Netty, port = 9000, host = "127.0.0.1") {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
        }
        install(Compression) {
            gzip()
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(), ContentType.Text.Html
                )
            }

            post("/rooms") {
                val id = UUID.randomUUID().toString()
                rooms[id] = Room(id, 0, Array(10) { Array(10) { " " } }, "", "")
                call.respondText(id)
            }

            webSocket("/rooms_info") {
                call.application.log.info(this.toString())
                while (true) {
                    try {
                        sendSerialized(rooms.values)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    withContext(Dispatchers.IO) {
                        TimeUnit.SECONDS.sleep(1)
                    }
                }
            }
            webSocket("/rooms/{roomId}") {
                val logger = call.application.log
                val roomId = call.parameters["roomId"]
                logger.info("A player joined room: $roomId")
                rooms[roomId]!!.numOfPlayers++
                var shouldListen = true
                while (shouldListen) {
                    incoming.tryReceive()
                        .onSuccess {
                            val request = converter!!.deserialize<BoardMove>(it)
                            // TODO: 1. check and set should be atomic - currently the game isn't thread safe
                            // TODO: 2. extract logic to controller
                            if (rooms[roomId]!!.winner == "" && rooms[roomId]!!.lastMove != request.sign) {
                                rooms[roomId]!!.board[request.x][request.y] = request.sign
                                rooms[roomId]!!.lastMove = request.sign
                                if (GameChecker.isWinning(rooms[roomId]!!.board, request.x, request.y, request.sign)) {
                                    rooms[roomId]!!.winner = request.sign
                                }
                            } else {
                                logger.debug("Can't update $request")
                            }
                        }.onClosed {
                            logger.debug("Leaving room $roomId")
                            rooms[roomId]!!.numOfPlayers--
                            shouldListen = false
                        }
                    sendSerialized(rooms[roomId]!!)
                    withContext(Dispatchers.IO) {
                        TimeUnit.MILLISECONDS.sleep(20)
                    }
                }
                logger.info("A player left room: $roomId")
            }
            static("/") {
                resources()
            }
        }
    }.start(wait = true)
}