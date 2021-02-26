package rendering;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*; //shader commands
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Model {
	private int drawCount;
	private int vertexID;
	private int textureID;
	private int indiceID;

	public Model(float[] vertices, float[] textureCoords, int[] indices) {
		drawCount = indices.length; //vertices.length/3; //divide by axis numbers (ex: x ,y, z is 3)
		vertexID = glGenBuffers(); //generating id
		glBindBuffer(GL_ARRAY_BUFFER, vertexID);//bind the id to the array buffer
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW); //pass info into buffer-static-pass info once-dynamic-it's going to be changing
		textureID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, textureID);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(textureCoords), GL_STATIC_DRAW);//texture data is now in graphics card
		indiceID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indiceID);
		IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
		buffer.put(indices);
		buffer.flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW) ;
		glBindBuffer(GL_ARRAY_BUFFER, 0); //unbinding id from array buffer-nothing can affect the array buffer atm
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	protected void finalize() {
		glDeleteBuffers(vertexID);
		glDeleteBuffers(textureID);
		glDeleteBuffers(indiceID);
	}
	
	public void render() {
						//glEnableClientState(GL_VERTEX_ARRAY); // Tells openGL to draw these things
						//glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1); // enabling texture attribute
		//vertex
		glBindBuffer(GL_ARRAY_BUFFER, vertexID);
						//glVertexPointer(3, GL_FLOAT, 0 , 0); // Tells openGL what to do - axis number first input
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		//texture
		glBindBuffer(GL_ARRAY_BUFFER, textureID);
						//glTexCoordPointer(2, GL_FLOAT, 0, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		//indice
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indiceID);
		glDrawElements(GL_TRIANGLES, drawCount, GL_UNSIGNED_INT, 0); //glDrawArrays(GL_TRIANGLES, 0, drawCount);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
						//glDisableClientState(GL_TEXTURE_COORD_ARRAY);
						//glDisableClientState(GL_VERTEX_ARRAY); // Errors if not closed
	}
	
	private FloatBuffer createBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
