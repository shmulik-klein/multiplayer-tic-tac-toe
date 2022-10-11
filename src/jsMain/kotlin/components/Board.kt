package components

import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.tr

external interface BoardProps : Props {
    var boardRoom: String?
    var board: Array<Array<String>>
    var onCellClick: (Int, Int) -> Unit
    var winner: String
}

val Board = FC<BoardProps> { props ->
    run {
        ReactHTML.div {
            css {
                display = Display.block
                alignItems = AlignItems.center
                justifyContent = JustifyContent.center
            }
            h3 {
                val winner = if (props.winner != "") " - ${props.winner} wins!" else ""
                +"Room ${props.boardRoom} $winner"
            }
            table {
                tbody {
                    for ((rIndex, rowArray) in props.board.withIndex()) {
                        tr {
                            for ((cIndex, value) in rowArray.withIndex()) {
                                Cell {
                                    cellValue = value
                                    onClick = { props.onCellClick(rIndex, cIndex) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}