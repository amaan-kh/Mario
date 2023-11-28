package jade;

import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene{

    private int vertexID, fragmentID, shaderProgram;

    private float [] vertexArray = {
        //position                      //color                      //UV coordinates
    100.5f, -0.5f, 0.0f,                  1.0f, 0.0f, 0.0f, 1.0f,    1,1,// bottom right
    -0.5f, 100.5f, 0.0f,                  0.0f, 1.0f, 0.0f, 1.0f,    0,0,// top left
    100.5f, 100.5f, 0.0f,                   1.0f, 0.0f, 1.0f, 1.0f,  1,0,//top right
    -0.5f, -0.5f, 0.0f,                 1.0f, 1.0f, 0.0f, 1.0f,      0,1// bootom left

    };

    private int[] elementArray = {
            2,1,0, // top left triangle
            0, 1, 3 // bottom left triangle
    };
    private int vaoId, vboId, eboId;
    private Shader defaultShader;
    private Texture testTexture;
    GameObject testObj;
    private boolean firstTime = false;

    public LevelEditorScene () {

    }

    public void init() {
        System.out.println("creating test object");
        this.testObj = new GameObject("test object");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/images/testImage.jpg");

        //++++++++++++++++++++++++++++++++++++++++++
        //generate vao, vbo and ebo objects and send to GPU
        //++++++++++++++++++++++++++++++++++++++++++

            //creating vertex array object
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //generating vertex buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // create vbo upload vertex buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //generating element buffer
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        // create ebo upload element buffer
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
        //specifying how vertex data is stored in vbo -> position -> colorSize -> uvSize
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        //enabling above vbo at index 0 of vertex attribute array
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize)*Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;
        camera.position.y -= dt * 20.0f;


        defaultShader.use();

        //upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        //bind VAO that we're using
        glBindVertexArray(vaoId);

        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();

        if(!firstTime) {
            System.out.println("creating gameObject");
            GameObject go = new GameObject("game test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

    }
}
