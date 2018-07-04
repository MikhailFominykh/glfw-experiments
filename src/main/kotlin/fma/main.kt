package fma

import fma.gui.GUI
import fma.gui.UIPrograms
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.GLFW_FALSE
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDefaultWindowHints
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor
import org.lwjgl.glfw.GLFW.glfwGetVideoMode
import org.lwjgl.glfw.GLFW.glfwGetWindowSize
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL

fun main(args: Array<String>) {
    HelloWorld().run()
}

class HelloWorld {

    // The window handle
    private var window: Long = 0
    private val windowWidth = 400
    private val windowHeight = 300

    fun run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!")

        init()
        loop()

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    private fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE) // the window will be resizable

        // Create the window

        window = glfwCreateWindow(windowWidth, windowHeight, "Hello World!", NULL, NULL)
        if (window == NULL)
            throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
        }

        // Get the thread stack and push a new frame
        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode!!.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            )
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        val xpos = DoubleArray(1)
        val ypos = DoubleArray(1)
        var mouseX = 0
        var mouseY = 0
        var mouseDeltaX = 0
        var mouseDeltaY = 0
        var isMouseDown = false
        var squareX = 100
        var squareY = 100

        glfwSetMouseButtonCallback(window) { _, button, action, mods ->
            isMouseDown = action != GLFW_RELEASE
        }

        val gui = GUI(windowWidth, windowHeight)
        val minButtonsCount = 2
        val maxButtonsCount = 5
        var buttonsCount = minButtonsCount
        val buttonsColors = intArrayOf(0x666666, 0x888888, 0xAAAAAA, 0xCCCCCC, 0xEEEEEE)
        val squareColors = intArrayOf(0xFF0000, 0x00FF00, 0x0000FF)
        var squareColorIndex = 0

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glfwGetCursorPos(window, xpos, ypos)
            mouseDeltaX = xpos[0].toInt() - mouseX
            mouseX = xpos[0].toInt()
            mouseDeltaY = ypos[0].toInt() - mouseY
            mouseY = ypos[0].toInt()

            gui.setMouseState(mouseX, mouseY, isMouseDown)

            if (isMouseDown) {
                squareX += mouseDeltaX
                squareY += mouseDeltaY
            }

            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer
            glDisable(GL_DEPTH_TEST)

            gui.group(10, 20, GUI.Direction.VERTICAL)
            for (buttonIndex in 0 until buttonsCount) {
                val color = buttonsColors[buttonIndex]
                if (gui.button(color.toString(), color)) {
                    when (buttonIndex) {
                        0 -> buttonsCount = (buttonsCount + 1).coerceAtMost(maxButtonsCount)
                        1 -> buttonsCount = (buttonsCount - 1).coerceAtLeast(minButtonsCount)
                        2 -> squareX += 10
                        3 -> squareX -= 10
                        4 -> squareColorIndex = (squareColorIndex + 1) % squareColors.size
                    }
                }
            }
            gui.endGroup()

            UIPrograms.boxProgram.draw(squareX, squareY, 50, 50, windowWidth, windowHeight, squareColors[squareColorIndex])

            glfwSwapBuffers(window) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
        }
    }
}
