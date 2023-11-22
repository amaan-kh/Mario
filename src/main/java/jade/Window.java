package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int height, width;
    private String title;
    private long glfwWindow;
    private float r, g, b, a;
    private boolean fadeToBlack = false;

    //singleton so that only one instance is created
    private static Window window = null;

    //single instance window is set
    private Window() {
        this.height = 1080;
        this.width = 1920;
        this.title = "MARIO";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    // function to call the window
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    // function to initalise and run event loop
    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();
        loop();

        // free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // terminate glfw and free error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    // function to initialize window
    public void init() {
        //set up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("unable to initalize");
        }

        //configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //create window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("failed to create GLFW Window");
        }

        //set up mouse listener callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //make openGL context current
        glfwMakeContextCurrent(glfwWindow);

        //enable Vsync
        glfwSwapInterval(1);

        //make window visible
        glfwShowWindow(glfwWindow);

        // creates capabilities available for use in the current context
        GL.createCapabilities();
    }

    public void loop() {

        while (!glfwWindowShouldClose(glfwWindow)) {

            // poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (fadeToBlack) {
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                fadeToBlack = true;
            }

            glfwSwapBuffers(glfwWindow);
        }
    }

}
