package rendering;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Shader {
	private int program;
	private int vertexShader; // processes all the vertices the shader takes
	private int fragmentShader; // color, texture, blur, etc.

	public Shader(String fileName) {
		program = glCreateProgram();
		// vertexShader
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, readFile(fileName + ".vs"));
		glCompileShader(vertexShader);
		if(glGetShaderi(vertexShader, GL_COMPILE_STATUS)!=1) { // if shader does not work
			System.err.println(glGetShaderInfoLog(vertexShader)); // Tell us what the error is and where
			System.exit(1);
		}
		// fragmentShader
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, readFile(fileName + ".fs"));
		glCompileShader(fragmentShader);
		if(glGetShaderi(fragmentShader, GL_COMPILE_STATUS)!=1) { // if shader does not work
			System.err.println(glGetShaderInfoLog(fragmentShader)); // Tell us what the error is and where
			System.exit(1);
		}
		// attaching shaders
		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);
		// attributes
		glBindAttribLocation(program, 0, "vertices");
		glBindAttribLocation(program, 1, "textures");
		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS)!=1) {
			System.err.println(glGetProgramInfoLog(program));
			System.exit(1);
		}
		glValidateProgram(program);
		if(glGetProgrami(program, GL_VALIDATE_STATUS)!=1) {
			System.err.println(glGetProgramInfoLog(program));
			System.exit(1);
		}
	}
	
	protected void finalize() {
		glDetachShader(program, vertexShader);
		glDetachShader(program, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		glDeleteProgram(program);
	}

	public void setUniform(String name, int value) { // Uniform variables stored in graphics card, openGL returns value to use
		int location = glGetUniformLocation(program, name);
		if(location != -1) { // if location is valid
			glUniform1i(location, value);
		}
	}
	
	public void setUniform(String name, Vector4f value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1) { // if location is valid
			glUniform4f(location, value.x, value.y, value.z, value.w);
		}
	}
	
	public void setUniform(String name, Matrix4f value) {
		int location = glGetUniformLocation(program, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		value.get(buffer);
		if(location != -1) { // if location is valid
			glUniformMatrix4fv(location, false, buffer);
		}
	}

	public void bind() {
		glUseProgram(program);
	}

	private String readFile(String fileName) {
		StringBuilder string = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File("./shaders/" + fileName)));
			String line;
			while((line = br.readLine())!=null) {
				string.append(line);
				string.append("\n");
			}
			br.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return string.toString();
	}
}
