package world;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import collision.AABB;
import entity.Entity;
import entity.Player;
import entity.Transform;
import io.Window;
import rendering.Animation;
import rendering.Camera;
import rendering.Shader;
import rendering.Transition;

public class World {
	private int viewX;
	private int viewY;
	private byte[] tiles;
	private AABB[] boundingBoxes; // all collisions in-game
	private List<Entity> entities;
	private int width;
	private int height;
	private Matrix4f world;
	private int scale;

	public World(String world, Camera camera, String character) {
		try {
			BufferedImage tileSheet = ImageIO.read(new File("res/levels/" + world + "/tiles.png"));
			BufferedImage entitySheet = ImageIO.read(new File("res/levels/" + world + "/entities.png"));
			width = tileSheet.getWidth();
			height = tileSheet.getHeight();
			scale = 32;
			this.world = new Matrix4f().setTranslation(new Vector3f(0));
			this.world.scale(scale);
			// returns all pixels within the image
			int[] colorTileSheet = tileSheet.getRGB(0, 0, width, height, null, 0, width);
			int[] colorEntitySheet = entitySheet.getRGB(0, 0, width, height, null, 0, width);
			tiles = new byte[width*height];
			boundingBoxes = new AABB[width*height];
			entities = new ArrayList<Entity>();
			Transform transform;
			for(int y = 0; y<height; y++) {
				for(int x = 0; x<width; x++) {
					int red = (colorTileSheet[x+y*width]>>16) & 0xFF;
					int entityIndex = (colorEntitySheet[x+y*width]>>16) & 0xFF;
					int entityAlpha = (colorEntitySheet[x+y*width]>>24) & 0xFF;
					Tile t;
					try {
						t = Tile.tiles[red];
					}
					catch(ArrayIndexOutOfBoundsException e) {
						t = null;
					}
					if(t!=null) {
						setTile(t, x, y);
					}
					if(entityAlpha>0) {
						transform = new Transform();
						// moving the entity to the right spot on the level
						transform.position.x = x*2;
						transform.position.y = -y*2;
						switch(entityIndex) {
							case 1:
								Player player = new Player(transform, character);
								entities.add(player);
								camera.getPosition().set(transform.position.mul(-scale, new Vector3f()));
								break;
							default:
								break;
						}
					}
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public World() {
		width = 128;
		height = 128;
		scale = 32;
		tiles = new byte[width*height];
		boundingBoxes = new AABB[width*height];
		world = new Matrix4f().setTranslation(new Vector3f(0));
		world.scale(scale);
	}

	public void calculateView(Window window) {
		viewX = window.getWidth()/(scale*2)+4;
		viewY = window.getHeight()/(scale*2)+4;
	}

	public Matrix4f getWorldMatrix() {
		return world;
	}

	public void render(TileRenderer render, Shader shader, Camera camera) {
		// see example 1 in res for details
		// Center of the screen with offset of the world
		int positionX = (int)camera.getPosition().x/(scale*2);
		int positionY = (int)camera.getPosition().y/(scale*2);
		for(int i = 0; i<viewX; i++) {
			for(int j = 0; j<viewY; j++) {
				Tile t = getTile(i-positionX-(viewX/2)+1, j+positionY-(viewY/2));
				if(t!=null) {
					render.renderTile(t, i-positionX-(viewX/2)+1, -j-positionY+(viewY/2), shader, world, camera);
				}
			}
			for(Entity entity: entities) {
				entity.render(shader, camera, this);
			}
		}
	}

	public int update(float delta, Window window, Camera camera, Transition transition, int gameTrigger) {
		if(window.getInput().isKeyDown(GLFW_KEY_ESCAPE)) {
			gameTrigger = 1;
			transition.stateToFading();
		}
		for(Entity entity: entities) {
			entity.update(delta, window, camera, this);
			for(int i = 0; i<entities.size(); i++) {
				entities.get(i).collideWithTiles(this);
				for(int j = i+1; j<entities.size(); j++) {
					entities.get(i).collideWithEntity(entities.get(j));
				}
				entities.get(i).collideWithTiles(this); // so that we don't get pushed into tile after colliding with another entity
			}
		}
		return gameTrigger;
	}

	public void correctCamera(Camera camera, Window window) {
		Vector3f position = camera.getPosition();
		int w = -width*scale*2;// exact scale of width of the world
		int h = height*scale*2;// exact scale of height of the world
		if(position.x>-(window.getWidth()/2)+scale) {
			position.x = -(window.getWidth()/2)+scale;
		}
		if(position.x<w+(window.getWidth()/2)+scale) {
			position.x = w+(window.getWidth()/2)+scale;
		}
		if(position.y<(window.getHeight()/2)-scale) {
			position.y = (window.getHeight()/2)-scale;
		}
		if(position.y>h-(window.getHeight()/2)-scale) {
			position.y = h-(window.getHeight()/2)-scale;
		}
	}

	public void setTile(Tile tile, int x, int y) {
		tiles[x+y*width] = tile.getId();
		if(tile.isSolid()) {
			boundingBoxes[x+y*width] = new AABB(new Vector2f(x*2, -y*2), new Vector2f(1, 1));
		}
		else {
			boundingBoxes[x+y*width] = null;
		}
	}

	public Tile getTile(int x, int y) {
		try {
			return Tile.tiles[tiles[x+y*width]];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public AABB getTileBoundingBox(int x, int y) {
		try {
			return boundingBoxes[x+y*width];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public int getScale() {
		return scale;
	}
}
