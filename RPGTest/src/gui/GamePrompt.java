package gui;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import assets.Assets;
import io.Window;
import rendering.Camera;
import rendering.DrawString;
import rendering.Shader;
import rendering.Texture;
import rendering.TileSheet;

public class GamePrompt extends Gui {
	private DrawString prompt;
	private Texture background;
	private DrawString choice1;
	private DrawString choice2;
	private Shader shader;

	public GamePrompt(String prompt, String choice1, String choice2, Window window, Shader shader) {
		super(window);
		this.shader = shader;
		this.prompt = new DrawString(prompt);
		this.choice1 = new DrawString(choice1);
		this.choice2 = new DrawString(choice2);
		this.background = new Texture("gui/background/prompt_background.png");
		addButton((float)(-window.getWidth()*0.1), (float)(-window.getHeight()*0.2), 100, 40);
		addButton((float)(window.getWidth()*0.1), (float)(-window.getHeight()*0.2), 100, 40);
	}

	public void render(Window window) {
		shader.bind();
		Matrix4f mat = new Matrix4f();
		Matrix4f projection = new Matrix4f().ortho2D(-window.getWidth()/2, window.getWidth()/2, window.getHeight()/2,
		        -window.getHeight()/2);
		super.getCamera().getUntransformedProjection().scale(window.getHeight()/3, mat);
		background.bind(0);
		shader.setUniform("color", projection);
		shader.setUniform("projection", mat);
		Assets.getModel().render();
		super.getShader().bind();
		for(int i = 0; i<super.getButtonList().size(); i++) {
			super.getButtonList().get(i).render(super.getCamera(), super.getSheet(), super.getShader());
		}
		shader.bind();
		prompt.render(0, window.getHeight()/8, 35, window.getHeight()/30, super.getCamera(), shader);
		choice1.render(super.getButtonList().get(0).getBoundingBox().getCenter().x,
		        super.getButtonList().get(0).getBoundingBox().getCenter().y, 25, window.getHeight()/40, super.getCamera(), shader);
		choice2.render(super.getButtonList().get(1).getBoundingBox().getCenter().x,
		        super.getButtonList().get(1).getBoundingBox().getCenter().y, 25, window.getHeight()/40, super.getCamera(), shader);
	}
}
