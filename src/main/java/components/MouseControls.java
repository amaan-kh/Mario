package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingobject = null;

    public void pickupObject(GameObject go) {
        this.holdingobject = go;
        Window.getScene().addGameObjectToScene(go);
    }
    public void place() {
        this.holdingobject = null;
    }
    @Override
    public void update(float dt) {
        if (holdingobject != null) {
            holdingobject.transform.position.x = MouseListener.getOrthoX() ;
            holdingobject.transform.position.y = MouseListener.getOrthoY() ;
            holdingobject.transform.position.x = (int)(holdingobject.transform.position.x/ Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingobject.transform.position.y = (int)(holdingobject.transform.position.y/ Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }

        }
    }
}
