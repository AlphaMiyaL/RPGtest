package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import assets.Assets;
import gui.Gui;
import io.Window;
import rendering.DrawString;
import rendering.Shader;
import rendering.Texture;
import rendering.Transition;

public class PokeInv extends Gui{
	private Texture background;
	private Texture[] pokeSprite;
	private DrawString[] names;

	public PokeInv(Window window, String tex) {
		super(window);
		background = new Texture(tex);
		pokeSprite = new Texture[6];
		names = new DrawString[6];
		addButton((float)(-window.getWidth()*0.35), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.125), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.125), (float)(window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.35), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(-window.getWidth()*0.125), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
		addButton((float)(window.getWidth()*0.125), (float)(-window.getHeight()*0.15), window.getWidth()/10, window.getHeight()/7);
	}
	
	public void setPokemonInInv(Texture[] a, DrawString[] b) {
		pokeSprite = a;
		names = b;
	}
	
	public int update(Window window, Transition transition, int transHelper) {
		if(window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
			// return back to game
			transHelper = -1;
			transition.stateToFading();
		}
		return transHelper;
	}
	
	public void render(Window window, Shader shader2) {
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
			if(pokeSprite[i]!=null) {
				float x = super.getButtonList().get(i).getBoundingBox().getCenter().x;
				float y = super.getButtonList().get(i).getBoundingBox().getCenter().y;
				mat = new Matrix4f();
				super.getCamera().getUntransformedProjection().scale(window.getHeight()/10, mat);
				mat.translate(x/100, y/100, 0);
				shader2.setUniform("sampler", 0);
				shader2.setUniform("projection", mat);
				pokeSprite[i].bind(0);
				Assets.getModel().render();
				names[i].render(position.x, position.y, 40, window.getHeight()/25, super.getCamera(), shader2);
			}
		}
	}

}
