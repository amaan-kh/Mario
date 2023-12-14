package editor;

import imgui.ImGui;
import jade.GameObject;
import scenes.Scene;

public class PropertiesWindow {
    protected GameObject activeGameObject = null;
    public void update(float dt, Scene currentScene) {

    }

    public void imgui() {
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }
    }

}
