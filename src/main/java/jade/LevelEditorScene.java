package jade;

import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene{

    private boolean changingScene = false;
    private static float timeToChangeScene = 2.0f;
    public LevelEditorScene () {
        System.out.println("inside level editor scene");
    }
    @Override
    public void update(float dt) {

        //System.out.println(" " + (1.0f/dt) + " FPS");
        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE))
        {
            changingScene = true;
        }
        if (changingScene && timeToChangeScene > 0 )
        {
            // takes two seconds to go from 2f to < 0
            timeToChangeScene -= dt;
            // change screen color from white to black
            Window.get().r -= dt * 5.0f;
            Window.get().g -= dt * 5.0f;
            Window.get().b -= dt * 5.0f;
        }
        // is executed once 2f is <= 0
        else if (changingScene) {
            Window.changeScene(1);

        }
    }
}
