package jade;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene{
    private String vertexShadersrc = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main ()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float [] vertexArray = {
        //position                      //color
    0.5f, -0.5f, 0.0f,                  1.0f, 0.0f, 0.0f, 1.0f,  // bottom right
    -0.5f, 0.5f, 0.0f,                  0.0f, 1.0f, 0.0f, 1.0f,   // top left
    0.5f, 0.5f, 0.0f,                   0.0f, 0.0f, 1.0f, 1.0f,  //top right
    -0.5f, -0.5f, 0.0f,                 1.0f, 1.0f, 0.0f, 1.0f  // bootom left

    };

    private int[] elementArray = {
            2,1,0, // top left triangle
            0, 1, 3 // bottom left triangle
    };
    public LevelEditorScene () {

    }
    private int vaoId, vboId, eboId;

    public void init() {
        //compile link shaders:

        //load and compile vertex shaders
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass shader source to the gpu
        glShaderSource(vertexID, vertexShadersrc);
        //compile shader
        glCompileShader(vertexID);

        //check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len =  glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl '\n\tVertex shader compilation failed ");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false: " ";
        }

        //load and compile fragment shaders
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass shader source to the gpu
        glShaderSource(fragmentID, fragmentShaderSrc);
        //compile shader
        glCompileShader(fragmentID);

        //check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len =  glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl '\n\tfragment shader compilation failed ");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false: " ";
        }

        //link shaders and look for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        //check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl '\n\tprogram  shader linking failed ");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false: " ";
        }
        //++++++++++++++++++++++++++++++++++++++++++
        //generate vao, vbo and ebo objects and send to GPU
        //++++++++++++++++++++++++++++++++++++++++++
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // create vbo upload buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        //bind shader program
        glUseProgram(shaderProgram);
        //bind VAO that we're using
        glBindVertexArray(vaoId);

        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glUseProgram(0);

    }
}
