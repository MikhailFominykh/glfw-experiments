package fma.gui

import fma.Program
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_LINE_LOOP
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL15.glBufferData
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glGetAttribLocation
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL20.glUniform2f
import org.lwjgl.opengl.GL20.glUniform3fv
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL20.glVertexAttribPointer

class WindowFrame {

    private val frameProgram: Program
    private val aFramePosition: Int
    private val uFrameColor: Int
    private val uWindowSize: Int
    private val frameBufferId: Int
    private val frameVertexData = FloatArray(8)
    private val frameColor = FloatArray(3)

    init {
        frameProgram = Program(frameVertexShaderSource, colorFragmentShaderSource)
        aFramePosition = glGetAttribLocation(frameProgram.program, "a_position")
        uFrameColor = glGetUniformLocation(frameProgram.program, "u_color")
        uWindowSize = glGetUniformLocation(frameProgram.program, "u_windowSize")

        frameBufferId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, frameBufferId)
        glBufferData(GL_ARRAY_BUFFER, frameVertexData, GL_STATIC_DRAW)
    }

    fun setFrameColor(r: Float, g: Float, b: Float) {
        frameColor[0] = r
        frameColor[1] = g
        frameColor[2] = b
    }

    fun draw(x: Int, y: Int, width: Int, height: Int, windowWidth: Int, windowHeight: Int) {
        setVertex(0, x, y)
        setVertex(1, x, y + height)
        setVertex(2, x + width, y + height)
        setVertex(3, x + width, y)

        glBindBuffer(GL_ARRAY_BUFFER, frameBufferId)
        glBufferSubData(GL_ARRAY_BUFFER, 0, frameVertexData)

        glUseProgram(frameProgram.program)
        glEnableVertexAttribArray(aFramePosition)
        glVertexAttribPointer(aFramePosition, 2, GL_FLOAT, false, 0, 0)
        glUniform2f(uWindowSize, windowWidth.toFloat(), windowHeight.toFloat())
        glUniform3fv(uFrameColor, frameColor)
        glDrawArrays(GL_LINE_LOOP, 0, 4)
        glDisableVertexAttribArray(aFramePosition)
    }

    private fun setVertex(vertexIndex: Int, x: Int, y: Int) {
        val offset = 2 * vertexIndex
        frameVertexData[offset] = x.toFloat()
        frameVertexData[offset + 1] = y.toFloat()
    }

    private companion object {
        private val frameVertexShaderSource = """
            attribute vec2 a_position;

            uniform vec2 u_windowSize;

            void main() {
                vec2 normalizedCoords = a_position / u_windowSize;
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