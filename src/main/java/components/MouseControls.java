package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;

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
            holdingobject.transform.position.x = MouseListener.getOrthoX() - 16;
            holdingobject.transform.position.y = MouseListener.getOrthoY() - 16;
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }

        }
    }
}
