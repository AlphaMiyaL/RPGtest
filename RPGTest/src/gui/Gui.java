package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import assets.Assets;
import io.Window;
import rendering.Animation;
import rendering.Camera;
import rendering.Shader;
import rendering.Texture;
import rendering.TileSheet;
import entity.Transform;

import static org.lwjgl.opengl.GL11.*;

public class Gui {
	private Shader shader;
	private Camera camera;
	private TileSheet sheet;
	private ArrayList<Button> buttonList;

	public Gui(Window window) {
		shader = new Shader("gui");
		camera = new Camera(window.getWidth(), window.getHeight());
		sheet = new TileSheet("gui.png", 9);
		buttonList = new ArrayList<Button>();
	}

	public void addButton(float x, float y, int buttonWidth, int buttonHeight) {
		// first Vector2f location, second size of button
		buttonList.add(new Button(new Vector2f(x, y), new Vector2f(buttonWidth, buttonHeight)));
	}

	public void resizeCamera(Window window) {
		camera.setProjection(window.getWidth(), window.getHeight());
	}

	public void render() {
		shader.bind();
		for(int i = 0; i<buttonList.size(); i++) {
			buttonList.get(i).render(camera, sheet, shader);
		}
	}

	public ArrayList<Button> getButtonList() {
		return buttonList;
	}

	public void setButtonList(ArrayList<Button> buttonList) {
		this.buttonList = buttonList;
	}

	public Shader getShader() {
		return shader;
	}

	public Camera getCamera() {
		return camera;
	}

	public TileSheet getSheet() {
		return sheet;
	}
}
