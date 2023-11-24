package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int height, width;
    private String title;
    private long glfwWindow;
    public float r, g, b, a;
    private boolean fadeToBlack = false;

    //singleton instance
    private static Window window = null;

    private static Scene currentScene;


    //single instance constructor window is private to restrict instantiation from outside by another class
    private Window() {
        this.height = 1080;
        this.width = 1920;
        this.title = "MARIO";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }


    public static void changeScene (int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                //init scene
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false: "Unknown Scene '" + newScene + "'";
        }
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
        //setting up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //loading GLFW library
        if (!glfwInit()) {
            throw new IllegalStateException("unable to initalize");
        }

        //configuring GLFW window properties
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //creating window configured above
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

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {

            // poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
            }
            glfwSwapBuffers(glfwWindow);
            endTime = Time.getTime();
             dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

}
