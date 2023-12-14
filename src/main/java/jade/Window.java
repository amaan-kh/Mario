package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelScene;
import scenes.Scene;
import scenes.LevelEditorScene;
import util.AssetPool;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int height, width;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

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
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false: "Unknown Scene '" + newScene + "'";
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }
    // function to call the window
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public static int getWidth() {
        return get().width;
    }
    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static int getHeight() {
        return get().height;
    }
    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static Framebuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f/9.0f;
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
        glfwSetWindowSizeCallback(glfwWindow,(w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        //make openGL context current
        glfwMakeContextCurrent(glfwWindow);

        //enable Vsync
        glfwSwapInterval(1);

        //make window visible
        glfwShowWindow(glfwWindow);

        // creates capabilities available for use in the current context
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920,1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0,0,1920,1080);

        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();


        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {

            // poll events
            glfwPollEvents();
            // render 1 : render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);


            // render 2 : render to actual game
            DebugDraw.beginFrame();
            this.framebuffer.bind();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);

                currentScene.update(dt);
                currentScene.render();
            }
            this.framebuffer.unbind();

            this.imGuiLayer.update(dt,currentScene);
            glfwSwapBuffers(glfwWindow);

            endTime = (float) glfwGetTime();
             dt = endTime - beginTime;
            beginTime = endTime;
        }
        currentScene.saveExit();
    }

}
