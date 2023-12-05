package components;

import jade.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

import java.sql.SQLOutput;

public class SpriteRenderer extends Component {

    Vector4f color;
    private Vector2f[] texCoords;
    private Texture texture;
    private Sprite sprite;
    public SpriteRenderer() {

    }
    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1,1,1,1);
    }
    public SpriteRenderer(Vector4f color) {

        this.color = color;
        this.sprite = new Sprite(null);
    }
    @Override
    public void start() {

    }
    @Override
    public void update(float dt) {

    }
    public Vector4f getColor() {
        return this.color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }
    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }
}
