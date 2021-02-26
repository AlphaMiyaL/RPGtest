package io;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

public class Window {
	private long window;// window handle---unique identifer that Windows assigns to each window created
	private int width;
	private int height;
	private boolean fullScreen;
	private boolean hasResized;
	private GLFWWindowSizeCallback windowSizeCallback;
	private Input input;

	public static void setCallbacks() {
		// Setup an error callback. The default is printing the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		/*
		 * glfwSetErrorCallback(new GLFWErrorCallback() {
		 * 
		 * @Override public void invoke(int error, long description) { // error code, descripion of error
		 * throw new IllegalStateException(GLFWErrorCallback.getDescription(description)); } });
		 */
	}
	
	private void setLocalCallbacks() {
		windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long argWindow, int argWidth, int argHeight) {
				width = argWidth;
				height = argHeight;
				hasResized = true;	
			}
		};
		glfwSetWindowSizeCallback(window, windowSizeCallback);
	}

	public Window() {
		setSize(640, 480);
		fullScreen = false;
		hasResized = false;
	}

	public void createWindow(String title) {
		// Creating the window-// size, title, fullscreen, monitor
		// ? is better way of using if statement whether fullScreen is true or not
		window = GLFW.glfwCreateWindow(width, height, title, fullScreen? glfwGetPrimaryMonitor() : 0, NULL);
		if(window==NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		if(!fullScreen) {
			// Get resolution of primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			// Centering window
			glfwSetWindowPos(window, (vidmode.width()-width)/2, (vidmode.height()-height)/2);
			glfwShowWindow(window); // Make window visible
			// Making OpenGL context current - for any OpenGL commands to work, a context must be current
			// Context is what OpenGL likes to send to the graphics card and draw on(like a canvas)
		}
		glfwMakeContextCurrent(window);
		input = new Input(window);
		setLocalCallbacks();
	}
	
	public void cleanUp() {
		windowSizeCallback.close();
	}

	public void swapBuffers() {
		glfwSwapBuffers(window); // draws on the back buffer, front buffer is shown to us, then swapped, and done again
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public void update() {
		hasResized = false;
		input.update();
		glfwPollEvents(); // Poll for window events. The key callback above will only be invoked during this call.
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public long getWindow() {
		return window;
	}

	public void setWindow(long window) {
		this.window = window;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}
	
	public boolean hasResized() {
		return hasResized;
	}
}
