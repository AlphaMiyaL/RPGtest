package gui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import assets.Assets;
import collision.AABB;
import io.Window;
import rendering.Camera;
import rendering.Shader;
import rendering.Texture;

public class Save {
	private Vector2f position;
	private Texture tex;
	private Texture tex2;
	private String chara;

	public Save(String chara, int i) {
		if(chara!=null) {
				this.chara = ("player/" + chara + "/idle_front/0.png");
				tex = new Texture("player/" + chara + "/idle_front/0.png");
		}
		else {
			this.chara = ("gui/new_logo.png");
			tex= new Texture("gui/new_logo.png");
		}
		tex2 = new Texture("gui/numbers/"+(i+1)+".png");
	}

	public void render(float x, float y, Window window, Camera camera, Shader shader) {
		Matrix4f mat = new Matrix4f();
		camera.getUntransformedProjection().scale(window.getHeight()/10, mat);
		mat.translate(x/100, y/100, 0);
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", mat);
		tex.bind(0);
		Assets.getModel().render();
	}
	
	public void renderNum(float x, float y, Window window, Camera camera, Shader shader) {
		Matrix4f mat = new Matrix4f();
		camera.getUntransformedProjection().scale(window.getHeight()/20, mat);
		mat.translate((float)(x/50+2.5), (float)(y/50-1.8), 0);
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", mat);
		tex2.bind(0);
		Assets.getModel().render();
	}

	public String getChara() {
		return chara;
	}

	public void setChara(String chara) {
		this.chara = chara;
	}
}
