package fma.gui

class GUI(windowWidth: Int, windowHeight: Int) {

    enum class Direction {
        HORIZONTAL, VERTICAL
    }

    val windowSize = Vec2i(windowWidth, windowHeight)

    val style = Style()

    private var hotItem: Int = 0
    private var activeItem: Int = 0

    private val mousePos = Vec2i()
    private var isMouseDown: Boolean = false

    private var hovered: Int = 0

    private var layoutDirection: Direction = Direction.HORIZONTAL
    private val groupStart = Vec2i()
    private val nextItemPos = Vec2i()

    fun setMouseState(x: Int, y: Int, isDown: Boolean) {
        mousePos.put(x, y)
        isMouseDown = isDown
    }

    fun group(x: Int, y: Int, direction: Direction) {
        groupStart.put(x, y)
        layoutDirection = direction
        nextItemPos.put(x, y)
    }

    fun endGroup() {
    }

    fun button(name: String, color: Int): Boolean {
        var result = false
        var actualColor = color

        val id = name.hashCode()
        if (isMouseInBox(nextItemPos.x, nextItemPos.y)) {
            if (isMouseDown) {
                if (tryMakeActive(id)) {
                    actualColor = style.boxColorActive
                }
            } else {
                if (isActive(id)) {
                    result = true
                    clearActive()
                } else {
                    if (tryMakeHot(id)) {
                        actualColor = style.boxColorHot
                    }
                }
            }
        } else {
            if (isActive(id)) {
                if (isMouseDown) {
                    actualColor = style.boxColorActive
                } else {
                    clearActive()
                }
            } else if (isHot(id)) {
                clearHot()
            }
        }

        val x = nextItemPos.x
        val y = nextItemPos.y
        val dimenstion = layoutDirection.ordinal
        nextItemPos[dimenstion] += style.spacing[dimenstion] + style.boxSize[dimenstion]

        UIPrograms.boxProgram.draw(x, y, style.boxSize.x, style.boxSize.y, windowSize.x, windowSize.y, actualColor)
        return result
    }

    private fun tryMakeActive(id: Int): Boolean {
        if (isHot(id)) {
            activeItem = id
            return true
        }
        return false
    }

    private fun tryMakeHot(id: Int): Boolean {
        if (activeItem == 0) {
            hotItem = id
            return true
        }
        return false
    }

    private fun isActive(id: Int): Boolean {
        return id == activeItem
    }

    private fun isHot(id: Int): Boolean {
        return id == hotItem
    }

    private fun clearActive() {
        activeItem = 0
    }

    private fun clearHot() {
        hotItem = 0
    }

    private fun isMouseInBox(x: Int, y: Int): Boolean {
        val right = x + style.boxSize.x
        val bottom = y + style.boxSize.y
        return mousePos.x in x..right && mousePos.y in y..bottom
    }

    class Style {
        var boxSize = Vec2i(60, 20)
        var boxColorHot: Int = 0xeeeeee
        var boxColorActive: Int = 0x00ff00
        var spacing = Vec2i(10, 5)
    }
}