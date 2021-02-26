package rendering;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import assets.Assets;
import io.Window;

import static org.lwjgl.opengl.GL11.*;

public class Transition {
	private static enum State {
		WAITING,
		FADING,
		APPEARING;
	}
	
	private State state;
	private float alpha;
	private boolean doThing;
	private Shader shader;
	
	public Transition() {
		state=State.WAITING;
		alpha = 0;
		doThing = false;
		shader = new Shader("fader");
	}
	
	public void render(Camera camera, Window window) {
		Matrix4f mat = new Matrix4f();
		camera.getUntransformedProjection().scale(window.getWidth()/2, mat);
		shader.bind();
		shader.setUniform("projection", mat);
		shader.setUniform("color", new Vector4f(0,0,0, alpha));
		Assets.getModel().render();
	}
	
	public void update() {
		switch(state) {
			case WAITING:
				break;
			case FADING:
				alpha+=0.05;
				if(alpha>=1) {
					doThing = true;
					state=State.APPEARING;
				}
				break;
			case APPEARING:
				alpha-=0.05;
				if(alpha<=0) {
					alpha=0;
					state=State.WAITING;
				}
				break;
		}
	}
	
	public void stateToFading() {
		state=State.FADING;
	}
	
	public boolean getDoThing() {
		return doThing;
	}
	
	public void setDoThing(boolean t) {
		doThing=t;
	}
}
