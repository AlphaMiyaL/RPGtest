package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import assets.Assets;
import collision.AABB;
import collision.Collision;
import gui.GamePrompt;
import gui.Gui;
import io.Window;
import rendering.DrawString;
import rendering.Shader;
import rendering.Texture;
import rendering.Transition;

public class MainMenu extends Gui {
	private Shader shader2;
	private Texture background;
	private ArrayList<Texture> buttonTextures;
	private AABB back;
	private DrawString start;
	private DrawString load;
	private boolean promptExit;

	public MainMenu(Window window, String tex) {
		super(window);
		shader2 = new Shader("shader");
		background = new Texture(tex);
		buttonTextures = new ArrayList<Texture>();
		buttonTextures.add(new Texture("gui/back.png"));
		back = new AABB(new Vector2f(-window.getWidth()*4/10, window.getHeight()*4/10),
		        new Vector2f(window.getHeight()/20, window.getHeight()/20));
		start = new DrawString("Start");
		load = new DrawString("Load");
		promptExit = false;
	}
	
	public int update(Window window, int gameTrigger, GamePrompt exitGame, Transition transition) {
		//keyPressed
		if(promptExit) {
			if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
				//set off exit game
				transition.stateToFading();
				gameTrigger = -1;
			}
		}
		else {
			if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
				// exit game prompt
				promptExit = true;
			}
		}
		//mousePressed
		if(window.getInput().isMouseButtonReleased(0)) { // 0= left, 1 = right, 2 = middle
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window.getWindow(), xBuffer, yBuffer);
				float x = (float)xBuffer.get(0)-window.getWidth()/2;
				float y = (float)yBuffer.get(0)-window.getHeight()/2;
				AABB clickArea = new AABB(new Vector2f(x, -y), new Vector2f(1, 1));
			Collision collideData;
			if(promptExit) {
				collideData = clickArea.getCollision(exitGame.getButtonList().get(0).getBoundingBox());
				if(collideData.isIntersecting) {
					//set off exit game
					transition.stateToFading();
					gameTrigger = -1;
				}
				collideData = clickArea.getCollision(exitGame.getButtonList().get(1).getBoundingBox());
				if(collideData.isIntersecting) {
					//game back to main menu
					promptExit=false;
				}
			}
			else{
				// see whether intersected with the two buttons on mainMenu
				collideData = clickArea.getCollision(super.getButtonList().get(0).getBoundingBox());
				if(collideData.isIntersecting) {
					// set off start button effect
					transition.stateToFading();
					gameTrigger = 1;
				}
				collideData = clickArea.getCollision(super.getButtonList().get(1).getBoundingBox());
				if(collideData.isIntersecting) {
					// set off load/continue button effect
					transition.stateToFading();
					gameTrigger = 2;
				}
				collideData = clickArea.getCollision(back);
				if(collideData.isIntersecting) {
					// exit game prompt
					promptExit = true;
				}
			}
		}
		return gameTrigger;
	}

	public void render(Window window) {
		shader2.bind();
		Matrix4f mat = new Matrix4f();
		Matrix4f projection = new Matrix4f().ortho2D(-window.getWidth()/2, window.getWidth()/2, window.getHeight()/2,
		        -window.getHeight()/2);
		super.getCamera().getUntransformedProjection().scale(window.getHeight(), mat);
		background.bind(0);
		shader2.setUniform("color", projection);
		shader2.setUniform("projection", mat);
		Assets.getModel().render();
		super.getShader().bind();
		for(int i = 0; i<super.getButtonList().size(); i++) {
			super.getButtonList().get(i).render(super.getCamera(), super.getSheet(), super.getShader());
		}
		shader2.bind();
		//start and load
		float x = super.getButtonList().get(0).getBoundingBox().getCenter().x;
		float y = super.getButtonList().get(0).getBoundingBox().getCenter().y;
		start.render(x, y, 25, window.getHeight()/40, super.getCamera(), shader2);
		x = super.getButtonList().get(1).getBoundingBox().getCenter().x;
		y = super.getButtonList().get(1).getBoundingBox().getCenter().y;
		load.render(x, y, 25, window.getHeight()/40, super.getCamera(), shader2);
		// back button(exit)
		mat = new Matrix4f();
		super.getCamera().getUntransformedProjection().scale(window.getHeight()/20, mat);
		x = back.getCenter().x;
		y = back.getCenter().y;
		mat.translate(x/50, y/50, 0);
		shader2.setUniform("sampler", 0);
		shader2.setUniform("projection", mat);
		buttonTextures.get(0).bind(0);
		Assets.getModel().render();
	}

	public AABB getBack() {
		return back;
	}

	public boolean isPromptExit() {
		return promptExit;
	}
	
	public Shader getShader2() {
		return shader2;
	}
}
