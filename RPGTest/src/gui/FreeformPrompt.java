package gui;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import assets.Assets;
import io.Window;
import rendering.DrawString;
import rendering.Shader;
import rendering.Texture;

public class FreeformPrompt extends Gui {
	private DrawString prompt;
	private Texture background;
	private ArrayList<String> choiceNames;
	private ArrayList<DrawString> choices;
	private ArrayList<Texture> choiceImages;
	private Shader shader;

	public FreeformPrompt(String prompt, String tex, Window window, Shader shader) {
		super(window);
		this.shader = shader;
		this.prompt = new DrawString(prompt);
		this.background = new Texture(tex);
		this.choiceNames = new ArrayList<String>();
		this.choices = new ArrayList<DrawString>();
		this.choiceImages = new ArrayList<Texture>();
	}

	public void addCharaChoices(Window window) {
		choices.add(new DrawString("Yoshino"));
		choiceNames.add("Yoshino");
		choiceImages.add(new Texture("/player/Yoshino/idle_front/0.png"));
		addButton((float)(-window.getWidth()*0.35), 0, window.getWidth()/10, window.getHeight()/7);
		choices.add(new DrawString("Mako"));
		choiceNames.add("Mako");
		choiceImages.add(new Texture("/player/Mako/idle_front/0.png"));
		addButton((float)(-window.getWidth()*0.125), 0, window.getWidth()/10, window.getHeight()/7);
		choices.add(new DrawString("Murasame"));
		choiceNames.add("Murasame");
		choiceImages.add(new Texture("/player/Murasame/idle_front/0.png"));
		addButton((float)(window.getWidth()*0.125), 0, window.getWidth()/10, window.getHeight()/7);
		choices.add(new DrawString("Lena"));
		choiceNames.add("Lena");
		choiceImages.add(new Texture("/player/Lena/idle_front/0.png"));
		addButton((float)(window.getWidth()*0.35), 0, window.getWidth()/10, window.getHeight()/7);
	}

	public void render(Window window) {
		Matrix4f mat = new Matrix4f();
		Matrix4f projection = new Matrix4f().ortho2D(-window.getWidth()/2, window.getWidth()/2, window.getHeight()/2,
		        -window.getHeight()/2);
		super.getCamera().getUntransformedProjection().scale((float)(window.getHeight()/1.2), mat);
		background.bind(0);
		shader.setUniform("color", projection);
		shader.setUniform("projection", mat);
		Assets.getModel().render();
		super.getShader().bind();
		for(int i = 0; i<super.getButtonList().size(); i++) {
			super.getButtonList().get(i).render(super.getCamera(), super.getSheet(), super.getShader());
		}
		shader.bind();
		if(!prompt.getLetters().isEmpty()) {
			prompt.render(0, window.getHeight()/4, 35, window.getHeight()/30, super.getCamera(), shader);
		}
		for(int i = 0; i<choices.size(); i++) {
			choices.get(i).render(super.getButtonList().get(i).getBoundingBox().getCenter().x,
			        (super.getButtonList().get(i).getBoundingBox().getCenter().y)-window.getHeight()/12, 25, window.getHeight()/40, super.getCamera(), shader);
			mat = new Matrix4f();
			super.getCamera().getUntransformedProjection().scale(window.getHeight()/10, mat);
			mat.translate(super.getButtonList().get(i).getBoundingBox().getCenter().x/100,
			        (float)((super.getButtonList().get(i).getBoundingBox().getCenter().y/100)+0.3), 0);
			shader.setUniform("sampler", 0);
			shader.setUniform("projection", mat);
			choiceImages.get(i).bind(0);
			Assets.getModel().render();
		}
	}

	public ArrayList<DrawString> getChoices() {
		return choices;
	}

	public ArrayList<String> getChoiceNames() {
		return choiceNames;
	}
}
