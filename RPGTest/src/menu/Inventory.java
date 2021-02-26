package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import assets.Assets;
import collision.AABB;
import collision.Collision;
import gui.Gui;
import io.Window;
import rendering.DrawString;
import rendering.Shader;
import rendering.Texture;
import rendering.Transition;

public class Inventory extends Gui {
	private Shader shader2;
	private Texture background;
	private ArrayList<DrawString> text;
	private int invSpot;
	private int transHelper;
	private PokeInv pokeSet;

	public Inventory(Window window, String tex) {
		super(window);
		shader2 = new Shader("shader");
		background = new Texture(tex);
		invSpot = 0;
		transHelper = 0;
		pokeSet = new PokeInv(window, tex);
		addButton((float)(-window.getWidth()*0.35), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.125), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.125), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.35), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.35), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.125), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.125), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.35), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		text = new ArrayList<DrawString>();
		text.add(new DrawString("Pokemon"));
		text.add(new DrawString("Bag"));
		text.add(new DrawString("Save"));
		for(int i = 0; i<5; i++) {
			text.add(new DrawString("WIP"));
		}
	}

	public int update(Window window, Transition transition, int gameTrigger) {
		switch(invSpot) {
			case 0:
				if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
					// return back to game
					gameTrigger = -1;
					transition.stateToFading();
				}
				if(window.getInput().isMouseButtonReleased(0)) { // 0= left, 1 = right, 2 = middle
					DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
					DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
					glfwGetCursorPos(window.getWindow(), xBuffer, yBuffer);
					float x = (float)xBuffer.get(0)-window.getWidth()/2;
					float y = (float)yBuffer.get(0)-window.getHeight()/2;
					AABB clickArea = new AABB(new Vector2f(x, -y), new Vector2f(1, 1));
					Collision collideData;
					for(int i = 0; i<8; i++) {
						collideData = clickArea.getCollision(super.getButtonList().get(i).getBoundingBox());
						if(collideData.isIntersecting) {
							transHelper = i+1;
							transition.stateToFading();
						}
					}
				}
				switch(transHelper) {
					case 1:// pokemon
						if(transition.getDoThing()) {
							invSpot = 1;
							transHelper = 0;
							transition.setDoThing(false);
						}
						break;
					case 2:// bag
						if(transition.getDoThing()) {
							invSpot = 2;
							transHelper = 0;
							transition.setDoThing(false);
						}
						break;
					case 3:// saving
						if(transition.getDoThing()) {
							invSpot = 3;
							transHelper = 0;
							transition.setDoThing(false);
						}
						break;
					default:// for the WIPs
						transHelper = 0;
						break;
				}
				break;
			case 1://pokemon
				transHelper = pokeSet.update(window, transition, transHelper);
				switch(transHelper) {
					case -1:
						if(transition.getDoThing()) {
							invSpot = 0;
							transHelper = 0;
							transition.setDoThing(false);
						}
						break;
					case 1:
						break;
				}
				break;
			case 2://bag
				break;
			case 3://save
				break;
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
		for(int i = 0; i<super.getButtonList().size(); i++) {
			Vector2f position = super.getButtonList().get(i).getBoundingBox().getCenter();
			text.get(i).render(position.x, position.y, 40, window.getHeight()/25, super.getCamera(), shader2);
		}
	}

	public int getInvSpot() {
		return invSpot;
	}

	public void setInvSpot(int invSpot) {
		this.invSpot = invSpot;
	}

	public PokeInv getPokeSet() {
		return pokeSet;
	}
}
