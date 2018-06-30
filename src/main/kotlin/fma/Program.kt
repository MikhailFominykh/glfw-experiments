package fma

import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.GL_COMPILE_STATUS
import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_LINK_STATUS
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import org.lwjgl.opengl.GL20.glAttachShader
import org.lwjgl.opengl.GL20.glCompileShader
import org.lwjgl.opengl.GL20.glCreateProgram
import org.lwjgl.opengl.GL20.glCreateShader
import org.lwjgl.opengl.GL20.glDeleteShader
import org.lwjgl.opengl.GL20.glDetachShader
import org.lwjgl.opengl.GL20.glGetProgramInfoLog
import org.lwjgl.opengl.GL20.glGetProgramiv
import org.lwjgl.opengl.GL20.glGetShaderInfoLog
import org.lwjgl.opengl.GL20.glGetShaderiv
import org.lwjgl.opengl.GL20.glLinkProgram
import org.lwjgl.opengl.GL20.glShaderSource
import org.lwjgl.opengl.GL20.glUseProgram

private val int1 = intArrayOf(0)

class Program(val vertexShaderSource: String, val fragmentShaderSource: String) {

    val program: Int

    init {
        val vertexShader = createShader(GL_VERTEX_SHADER, vertexShaderSource)
        val fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentShaderSource)
        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)

        glGetProgramiv(program, GL_LINK_STATUS, int1)
        if (int1[0] == GL_FALSE) {
            val log = glGetProgramInfoLog(program)
            println("Program link error:\n$log")
        }
        glDetachShader(program, vertexShader)
        glDeleteShader(vertexShader)
        glDetachShader(program, fragmentShader)
        glDeleteShader(fragmentShader)
    }

    fun use() {
        glUseProgram(program)
    }

    private fun createShader(type: Int, source: String): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, source)
        glCompileShader(shader)
        glGetShaderiv(shader, GL_COMPILE_STATUS, int1)
        if (int1[0] == GL_FALSE) {
            val log = glGetShaderInfoLog(shader)
            println("Shader (type: $type) compilation error:\n$log")
        }
        return shader
    }
}