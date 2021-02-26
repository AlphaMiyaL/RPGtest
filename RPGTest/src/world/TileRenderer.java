package world;

import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import assets.Assets;
import rendering.Camera;
import rendering.Model;
import rendering.Shader;
import rendering.Texture;

public class TileRenderer {
	private HashMap<String, Texture> tileTextures;
	private Model model;

	public TileRenderer() {
		tileTextures = new HashMap<String, Texture>();
		model = Assets.getModel();
		for(int i = 0; i<Tile.tiles.length; i++) {
			if(Tile.tiles[i]!=null) {
				String tex = Tile.tiles[i].getTexture();
				if(!tileTextures.containsKey(tex)) {// check whether a particular key is mapped in the HashMap
					tileTextures.put(tex, new Texture("tiles/" + tex + ".png"));
				}
			}
		}
	}

	public void renderTile(Tile tile, int x, int y, Shader shader, Matrix4f world, Camera camera) {
		shader.bind();
		String tex = tile.getTexture();
		if(tileTextures.containsKey(tex)) { // test if texture is there, and binds it if so
			tileTextures.get(tex).bind(0);
		}
		Matrix4f tilePosition = new Matrix4f().translate(new Vector3f(x*2, y*2, 0)); // multiplies with scale
		Matrix4f target = new Matrix4f();
		camera.getProjection().mul(world, target);// projection and world multiplied and put into target
		target.mul(tilePosition);
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", target);
		model.render();
	}
}
