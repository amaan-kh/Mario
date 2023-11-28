package renderer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSouce;
    private String fragmentSource;
    private String filePath;

    public Shader(String filepath) {
        this.filePath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //gets the index just after #type
            //gets end of line of (first line )
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // gets index after the first #type line
            //gets end of line of (first line for fragment)
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSouce = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token'" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSouce = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token'" + secondPattern + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false: "Error: Could not open file for shader : '" + filepath + "'";
        }
    }

    public void compile() {
        int fragmentID, vertexID;
        //compile link shaders:

        //load and compile vertex shaders
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass shader source to the gpu
        glShaderSource(vertexID, vertexSouce);
        //compile shader
        glCompileShader(vertexID);

        //check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len =  glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: ' "+ filePath+ " '\n\tVertex shader compilation failed ");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false: " ";
        }

        //load and compile fragment shaders
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass shader source to the gpu
        glShaderSource(fragmentID, fragmentSource);
        //compile shader
        glCompileShader(fragmentID);

        //check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len =  glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: ' "+ filePath + " '\n\tfragment shader compilation failed ");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false: " ";
        }

        //link shaders and look for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filePath + " '\n\tprogram  shader linking failed ");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false: " ";
        }


    }

    public void use() {
        if (!beingUsed) {
            //bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }

    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        // getting location of uniform variable(default.glsl) -> creating floatbuffer with values of mat4 and uploading
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
    public void uploadMat3f(String varName, Matrix3f mat3) {
        // getting location of uniform variable(default.glsl) -> creating floatbuffer with values of mat4 and uploading
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x,vec.y, vec.z, vec.w );
    }
    public void uploadVec3f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x,vec.y, vec.z );
    }
    public void uploadVec2f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x,vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation,val);
    }
    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation,val);
    }
    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation,slot);
    }

}
