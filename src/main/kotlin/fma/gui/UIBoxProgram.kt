package fma.gui

import fma.createProgram
import org.lwjgl.opengl.GL11.GL_SHORT
import org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN
import org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP
import org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.glUniform2i
import org.lwjgl.opengl.GL20.glUniform3f
import org.lwjgl.opengl.GL20.glUniform4i
import org.lwjgl.opengl.GL20.glUseProgram

class UIBoxProgram {

    val program: Int = createProgram(uiBoxVertexShaderSource, colorFragmentShaderSource)
    val indexBufferId: Int
    val uBox: Int
    val uColor: Int
    val uWindowSize: Int

    init {
        uBox = GL20.glGetUniformLocation(program, "u_box")
        uColor = GL20.glGetUniformLocation(program, "u_color")
        uWindowSize = GL20.glGetUniformLocation(program, "u_windowSize")

        indexBufferId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, shortArrayOf(0, 1, 2, 3), GL_STATIC_DRAW)
    }

    fun draw(x: Int, y: Int, width: Int, height: Int, windowWidth: Int, windowHeight: Int, color: Int) {
        glUseProgram(program)
        glUniform4i(uBox, x, y, width, height)
        glUniform2i(uWindowSize, windowWidth, windowHeight)
        glUniform3f(uColor, color.red(), color.green(), color.blue())
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId)
        glDrawElements(GL_TRIANGLE_STRIP, 4, GL_UNSIGNED_SHORT, 0)
    }

    private fun Int.red(): Float = ((this shr 16) and 0xff) / 255.0f
    private fun Int.green(): Float = ((this shr 8) and 0xff) / 255.0f
    private fun Int.blue(): Float = (this and 0xff) / 255.0f

    private companion object {
        private val uiBoxVertexShaderSource = """
            #version 130

            uniform ivec4 u_box;
            uniform ivec2 u_windowSize;

            void main() {
                ivec2 boxCoord = ivec2(gl_VertexID / 2, gl_VertexID % 2);
                vec2 worldCoord = u_box.xy + boxCoord * u_box.zw;
                vec2 normalizedCoords = worldCoord / u_windowSize;
                vec2 screenCoords = 2.0 * normalizedCoords - 1.0;
                gl_Position = vec4(screenCoords.x, -screenCoords.y, 0.0, 1.0);
            }
        """.trimIndent()

        private val colorFragmentShaderSource = """
            uniform vec3 u_color;

            void main() {
                gl_FragColor = vec4(u_color, 1.0);
            }
        """.trimIndent()
    }
}