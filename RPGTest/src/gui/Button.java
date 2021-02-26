package gui;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import assets.Assets;
import collision.AABB;
import rendering.Camera;
import rendering.Shader;
import rendering.TileSheet;

public class Button {
	public static final int STATE_IDLE = 0;
	public static final int STATE_SELECTED = 1;
	public static final int STATE_CLICKED = 2;
	private AABB boundingBox;
	private int selectedState;
	private static Matrix4f transform = new Matrix4f();
	
	public Button(Vector2f position, Vector2f scale) {
		this.boundingBox = new AABB(position, scale);
	}
	
	public void render(Camera camera, TileSheet sheet, Shader shader) {
		Vector2f position = boundingBox.getCenter(), 
				 scale = boundingBox.getHalfExtent();
		transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1); //Middle/fill of button
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 1, 1);
		Assets.getModel().render();
		renderSides(position, scale, camera, sheet, shader);
		renderCorners(position, scale, camera, sheet, shader);
	}
	
	private void renderSides(Vector2f position, Vector2f scale, Camera camera, TileSheet sheet, Shader shader) {
		transform.identity().translate(position.x, position.y+scale.y-16, 0).scale(scale.x, 16, 1); //Top
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 1, 0);
		Assets.getModel().render();
		transform.identity().translate(position.x, position.y-scale.y+16, 0).scale(scale.x, 16, 1); //Bottom
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 1, 2);
		Assets.getModel().render();
		transform.identity().translate(position.x - scale.x + 16, position.y, 0).scale(16, scale.y, 1); //Left
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 0, 1);
		Assets.getModel().render();
		transform.identity().translate(position.x + scale.x - 16, position.y, 0).scale(16, scale.y, 1); //Right
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 2, 1);
		Assets.getModel().render();
	}
	
	private void renderCorners(Vector2f position, Vector2f scale, Camera camera, TileSheet sheet, Shader shader) {
		transform.identity().translate(position.x-scale.x+16, position.y+scale.y-16, 0).scale(16, 16, 1); //Top Left
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 0, 0);
		Assets.getModel().render();
		transform.identity().translate(position.x+scale.x-16, position.y+scale.y-16, 0).scale(16, 16, 1); //Top Right
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 2, 0);
		Assets.getModel().render();
		transform.identity().translate(position.x-scale.x+16, position.y-scale.y+16, 0).scale(16, 16, 1); //Bottom Left
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 0, 2);
		Assets.getModel().render();
		transform.identity().translate(position.x+scale.x-16, position.y-scale.y+16, 0).scale(16, 16, 1); //Bottom Right
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 2, 2);
		Assets.getModel().render();
	}

	public AABB getBoundingBox() {
		return boundingBox;
	}
}
