package io;
import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

public class Input {
	private long window;
	private Boolean keys[];
	private Boolean buttons[];

	public Input(long window) {
		this.window = window;
		keys = new Boolean[GLFW_KEY_LAST];
		buttons = new Boolean[3];
		Arrays.fill(keys, Boolean.FALSE);
		Arrays.fill(buttons, Boolean.FALSE);
	}

	public boolean isKeyDown(int key) {
		return glfwGetKey(window, key)==1;
	}

	public boolean isMouseButtonDown(int mouseButton) {
		return glfwGetMouseButton(window, mouseButton)==1;
	}

	public void update() {
		for(int i = 0; i<GLFW_KEY_LAST; i++) {
			if(glfwGetKeyName(i, -1)!=null) {
				keys[i] = isKeyDown(i);
			}
		}
		//Manually fixing keys that don't work - https://www.glfw.org/docs/3.3/group__keys.html
		keys[256] = isKeyDown(256); //esc
		//Mouse Buttons
		buttons[0] = isMouseButtonDown(0);
		buttons[1] = isMouseButtonDown(1);
		buttons[2] = isMouseButtonDown(2);
	}
	
	public boolean isMouseButtonPressed(int button) {
		return(isMouseButtonDown(button) && !buttons[button]);
	}
	
	public boolean isMouseButtonReleased(int button) {
		return(!isMouseButtonDown(button) && buttons[button]);
	}

	public boolean isKeyPressed(int key) {
		return(isKeyDown(key) && !keys[key]);
	}

	public boolean isKeyReleased(int key) {
		return(!isKeyDown(key) && keys[key]);
	}
}
