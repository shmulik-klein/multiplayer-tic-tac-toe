package components

import BoardMove
import Room
import csstype.*
import emotion.react.css
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLButtonElement
import react.*
import react.dom.events.MouseEvent
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3

val mainScope = MainScope()

val client = HttpClient {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
}

val TicTacToe = FC<Props> {
    val (rooms, setRooms) = useState<Collection<Room>>()
    val (currentSocket, setCurrentSocket) = useState<DefaultClientWebSocketSession>()
    val (currentRoom, setCurrentRoom) = useState<Room>()
    val (currentSign, setCurrentSign) = useState<String>()
    var isTurnSet: Boolean // I had to add this "hack" since currentSign seems to fallback to initial value

    useEffectOnce {
        mainScope.launch {
            client.webSocket(
                host = "localhost",
                port = 9000,
                path = "/rooms_info"
            ) {
                while (true) {
                    val roomsList = receiveDeserialized<Collection<Room>>()
                    setRooms(roomsList)
                }
            }
        }
    }

    fun joinRoom(roomId: String) {
        mainScope.launch {
            try {
                currentSocket?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isTurnSet = false
            client.webSocket(
                host = "localhost",
                port = 9000,
                path = "/rooms/${roomId}"
            ) {
                println("Joined $roomId")
                setCurrentSocket(this)
                var shouldListen = true
                while (shouldListen) {
                    this.incoming.tryReceive().onClosed {
                        shouldListen = false
                    }
                    val room = receiveDeserialized<Room>()
                    if (!isTurnSet) {
                        val sign = if (room.numOfPlayers % 2 == 0) "O" else "X"
                        isTurnSet = true
                        setCurrentSign(sign)
                    }
                    setCurrentRoom(room)
                }
            }
        }
    }

    fun createRoom(event: MouseEvent<HTMLButtonElement, *>) {
        mainScope.launch {
            val response = client.post("http://localhost:9000/rooms")
            val roomId = response.bodyAsText()
            joinRoom(roomId)
        }
    }

    val handleClick = fun(x: Int, y: Int) {
        mainScope.launch {
            // for some reason, couldn't use here the auxiliary DefaultClientWebSocketSession#sendSerialized
            currentSocket!!.outgoing.send(Frame.Text(Json.encodeToString(BoardMove(x, y, currentSign!!))))
        }
    }

    div {
        css {
            display = Display.flex
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
            flexDirection = FlexDirection.column

        }
        h1 {
            +"Multi-player Tic Tac Toe"
        }

        button {
            onClick = { createRoom(it) }
            +"Create Room"
        }
        if (currentRoom != null) {
            Board {
                boardRoom = currentRoom.id
                board = currentRoom.board
                onCellClick = handleClick
                winner = currentRoom.winner
            }
        }
    }

    // TODO extract this component to a separate file
    val roomList = FC<Props> {
        if (rooms != null) {
            for (room in rooms) {
                ReactHTML.button {
                    css {
                        margin = 10.px
                    }
                    disabled = room.id == currentRoom?.id
                    h2 {
                        +"Room: ${room.id}"
                    }

                    h3 {
                        +"Number of players: ${room.numOfPlayers}"
                    }
                    h3 {
                        +"Last move: ${room.lastMove}"
                    }
                    h3 {
                        +"Winner: ${room.winner}"
                    }
                    onClick = { joinRoom(room.id) }
                }
            }
        }
    }

    div {
        css {
            margin = 20.px
        }
        roomList {
        }
    }
}