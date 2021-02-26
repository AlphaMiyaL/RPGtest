package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import assets.Assets;
import collision.AABB;
import collision.Collision;
import gui.FreeformPrompt;
import gui.GamePrompt;
import gui.Gui;
import gui.Save;
import io.Window;
import rendering.DrawString;
import rendering.Shader;
import rendering.Texture;
import rendering.Transition;

public class SaveMenu extends Gui {
	private Shader shader2;
	private Save[] saveList;
	private DrawString prompt;
	private Texture background;
	private Texture backTex;
	private AABB back;
	private int saveClicked;
	private int saveState;
	private boolean loadCheck;

	public SaveMenu(Window window, String tex, String prompt) {
		super(window);
		shader2 = new Shader("shader");
		saveList = new Save[8];
		background = new Texture(tex);
		saveState = 0;
		saveClicked = 999;
		loadCheck = false;
		this.prompt = new DrawString(prompt);
		backTex = new Texture("gui/back.png");
		back = new AABB(new Vector2f(-window.getWidth()*4/10, window.getHeight()*4/10),
		        new Vector2f(window.getHeight()/20, window.getHeight()/20));
		addButton((float)(-window.getWidth()*0.35), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.125), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.125), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.35), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.35), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.125), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.125), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.35), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
	}

	public void addSaves() {
		for(int i = 0; i<saveList.length; i++) {
			try {
				Scanner reader = new Scanner(new File("./saves/save_" + i + ".txt"));
				reader.useDelimiter("\n");// won't really be used too much unless more factors are added
				String chara = reader.next();
				saveList[i] = new Save(chara, i);
				reader.close();
			}
			catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			catch(NoSuchElementException e) {
				saveList[i] = new Save(null, i);
			}
		}
	}

	public int updateSave(Window window, int gameTrigger, GamePrompt overrideSave, FreeformPrompt chooseChara, Transition transition) {
		// keyPressed
		switch(saveState) {
			case 0:
				if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
					// return back to start
					gameTrigger = -1;
					transition.stateToFading();
				}
				break;
			case 1:
				if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
					// return back to original save page
					saveState = 0;
				}
				break;
			case 2:
				if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
					// return back to original save page
					saveState = 0;
				}
				break;
		}
		// mousePressed
		if(window.getInput().isMouseButtonReleased(0)) { // 0= left, 1 = right, 2 = middle
			DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window.getWindow(), xBuffer, yBuffer);
			float x = (float)xBuffer.get(0)-window.getWidth()/2;
			float y = (float)yBuffer.get(0)-window.getHeight()/2;
			AABB clickArea = new AABB(new Vector2f(x, -y), new Vector2f(1, 1));
			Collision collideData;
			// original save screen
			switch(saveState) {
				case 0:
					for(int i = 0; i<8; i++) {
						collideData = clickArea.getCollision(super.getButtonList().get(i).getBoundingBox());
						if(collideData.isIntersecting) {
							if(saveList[i].getChara()!="gui/new_logo.png") {
								// if overlapping with existing save
								saveState = 1;
								saveClicked = i;
							}
							else {
								// no existing save in slot
								saveClicked = i;
								saveState = 2;
							}
						}
					}
					collideData = clickArea.getCollision(back);
					if(collideData.isIntersecting) {
						// return back to start
						gameTrigger = -1;
						transition.stateToFading();
					}
					break;
				case 1:
					collideData = clickArea.getCollision(overrideSave.getButtonList().get(0).getBoundingBox());
					if(collideData.isIntersecting) {
						// override save
						saveState = 2;
						// clear and reinput all of txt file
						File f = new File("./saves/save_" + saveClicked + ".txt");
						if(f.exists()) {
							// delete if exists
							f.delete();
						}
						saveList[saveClicked] = new Save(null, saveClicked);
						try {
							BufferedWriter createFile = new BufferedWriter(new FileWriter("./saves/save_" + saveClicked + ".txt", true));
							createFile.close();
						}
						catch(IOException e) {
							e.printStackTrace();
						}
					}
					collideData = clickArea.getCollision(overrideSave.getButtonList().get(1).getBoundingBox());
					if(collideData.isIntersecting) {
						// game back to save menu
						saveState = 0;
					}
					collideData = clickArea.getCollision(back);
					if(collideData.isIntersecting) {
						// return back to original save page
						saveState = 0;
					}
					break;
				case 2:
					for(int i = 0; i<chooseChara.getButtonList().size(); i++) {
						collideData = clickArea.getCollision(chooseChara.getButtonList().get(i).getBoundingBox());
						if(collideData.isIntersecting) {
							// append all start data needed
							try {
								BufferedWriter startData = new BufferedWriter(new FileWriter("./saves/save_" + saveClicked + ".txt", true));
								startData.append(chooseChara.getChoiceNames().get(i) + "\n");
								startData.close();
								gameTrigger = 1;
								transition.stateToFading();
							}
							catch(IOException e) {
								e.printStackTrace();
							}
							saveState = 0;
						}
					}
					collideData = clickArea.getCollision(back);
					if(collideData.isIntersecting) {
						// return back to original save page
						saveState = 0;
					}
					break;
			}
		}
		return gameTrigger;
	}

	public int updateLoad(Window window, int gameTrigger, GamePrompt loadQuestion, Transition transition) {
		// keyPressed
		if(loadCheck && window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
			loadCheck = false;
		}
		else if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
			// return back to start
			gameTrigger = -1;
			transition.stateToFading();
		}
		// mousePressed
		boolean clicked = false;
		if(window.getInput().isMouseButtonReleased(0)) { // 0= left, 1 = right, 2 = middle
			DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window.getWindow(), xBuffer, yBuffer);
			float x = (float)xBuffer.get(0)-window.getWidth()/2;
			float y = (float)yBuffer.get(0)-window.getHeight()/2;
			AABB clickArea = new AABB(new Vector2f(x, -y), new Vector2f(1, 1));
			Collision collideData;
			if(loadCheck) {
				collideData = clickArea.getCollision(loadQuestion.getButtonList().get(0).getBoundingBox());
				if(collideData.isIntersecting) {
					// load file
					gameTrigger = 1;
					transition.stateToFading();
					loadCheck = false;
					clicked = true;
				}
				collideData = clickArea.getCollision(loadQuestion.getButtonList().get(1).getBoundingBox());
				if(collideData.isIntersecting) {
					// back to load page
					loadCheck = false;
					clicked = true;
				}
			}
			if(!clicked) {
				for(int i = 0; i<8; i++) {
					collideData = clickArea.getCollision(super.getButtonList().get(i).getBoundingBox());
					if(collideData.isIntersecting) {
						if(saveList[i].getChara()!="gui/new_logo.png") {
							// if clicked on existing save
							loadCheck = true;
							saveClicked = i;
						}
					}
					collideData = clickArea.getCollision(back);
					if(collideData.isIntersecting) {
						// return back to start
						gameTrigger = -1;
						transition.stateToFading();
					}
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
		shader2.setUniform("sampler", projection);
		shader2.setUniform("projection", mat);
		Assets.getModel().render();
		super.getShader().bind();
		for(int i = 0; i<super.getButtonList().size(); i++) {
			super.getButtonList().get(i).render(super.getCamera(), super.getSheet(), super.getShader());
		}
		shader2.bind();
		for(int i = 0; i<saveList.length; i++) {
			Vector2f position = super.getButtonList().get(i).getBoundingBox().getCenter();
			saveList[i].render(position.x, position.y, window, super.getCamera(), shader2);
			saveList[i].renderNum(position.x, position.y, window, super.getCamera(), shader2);
		}
		// prompt
		prompt.render(0, (float)(window.getHeight()/2.2), 50, window.getHeight()/25, super.getCamera(), shader2);
		// back button
		mat = new Matrix4f();
		super.getCamera().getUntransformedProjection().scale(window.getHeight()/20, mat);
		float x = back.getCenter().x;
		float y = back.getCenter().y;
		mat.translate(x/50, y/50, 0);
		shader2.setUniform("sampler", 0);
		shader2.setUniform("projection", mat);
		backTex.bind(0);
		Assets.getModel().render();
	}

	public int getSaveState() {
		return saveState;
	}

	public void setPrompt(DrawString prompt) {
		this.prompt = prompt;
	}

	public boolean isLoadCheck() {
		return loadCheck;
	}
	
	public int getLoadedSave() {
		return saveClicked;
	}
}
