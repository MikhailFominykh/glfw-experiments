package fma.gui

class Vec2i(var x: Int = 0, var y: Int = 0) {

    operator fun get(i: Int): Int {
        return when (i) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("Index value: $i")
        }
    }

    operator fun set(i: Int, value: Int) {
        return when (i) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException("Index value: $i")
        }
    }

    fun put(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}