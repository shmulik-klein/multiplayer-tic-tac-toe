package components

import csstype.*
import csstype.Float
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.td

external interface CellProps : Props {
    var cellValue: String
    var onClick: () -> Unit
}

val Cell = FC<CellProps> { props ->
    run {
        td {
            css {
                border = Border(1.px, LineStyle.solid, Color("#999"))
                width = 50.px
                height = 50.px
                float = Float.left
                fontSize = 30.px
                textAlign = TextAlign.center
                fontWeight = FontWeight.bold
                lineHeight = 50.px

            }
            onClick = { if (props.cellValue == " ")props.onClick() }
            +props.cellValue.toString()
        }
    }
}