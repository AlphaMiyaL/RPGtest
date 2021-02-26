package entity;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.Window;
import rendering.Animation;
import rendering.Camera;
import world.World;

public class Player extends Entity{
	public static final int ANIM_IDLE_UP = 0;
	public static final int ANIM_IDLE_DOWN = 1;
	public static final int ANIM_IDLE_LEFT = 2;
	public static final int ANIM_IDLE_RIGHT = 3;
	public static final int ANIM_WALK_UP = 4;
	public static final int ANIM_WALK_DOWN = 5;
	public static final int ANIM_WALK_LEFT = 6;
	public static final int ANIM_WALK_RIGHT = 7;
	public static final int ANIM_SIZE = 8;
	public static int direction = 0;

	public Player(Transform t, String character) {
		super(ANIM_SIZE, t);
		setAnimation(ANIM_IDLE_UP, new Animation(1, 1, "player/"+character+"/idle_back"));// num of frames, frames in one second, name of files
		setAnimation(ANIM_IDLE_DOWN, new Animation(1, 1, "player/"+character+"/idle_front"));
		setAnimation(ANIM_IDLE_LEFT, new Animation(1, 1, "player/"+character+"/idle_left"));
		setAnimation(ANIM_IDLE_RIGHT, new Animation(1, 1, "player/"+character+"/idle_right"));
		setAnimation(ANIM_WALK_UP, new Animation(4, 10, "player/"+character+"/walking_back"));
		setAnimation(ANIM_WALK_DOWN, new Animation(4, 10, "player/"+character+"/walking_front"));
		setAnimation(ANIM_WALK_LEFT, new Animation(4, 10, "player/"+character+"/walking_left"));
		setAnimation(ANIM_WALK_RIGHT, new Animation(4, 10, "player/"+character+"/walking_right"));
	}
	
	@Override
	public void update(float delta, Window window, Camera camera, World world) {
		Vector2f movement = new Vector2f();
		if(window.getInput().isKeyDown(GLFW_KEY_A)) {
			movement.add(-15*delta, 0); // left-right, up-down, depth
			direction = 2;
		}
		if(window.getInput().isKeyDown(GLFW_KEY_W)) {
			movement.add(0, 15*delta);
			direction = 1;
		}
		if(window.getInput().isKeyDown(GLFW_KEY_S)) {
			movement.add(0, -15*delta);
			direction = 0;
		}
		if(window.getInput().isKeyDown(GLFW_KEY_D)) {
			movement.add(15*delta, 0);
			direction = 3;
		}
		move(movement);
		if(movement.x!=0 || movement.y!=0) {
			switch(direction) {
				case 0:
					setAnimationPointer(ANIM_WALK_DOWN);
					break;
				case 1:
					setAnimationPointer(ANIM_WALK_UP);
					break;
				case 2:
					setAnimationPointer(ANIM_WALK_LEFT);
					break;
				case 3:
					setAnimationPointer(ANIM_WALK_RIGHT);
					break;
			}
		}
		else {
			switch(direction) {
				case 0:
					setAnimationPointer(ANIM_IDLE_DOWN);
					break;
				case 1:
					setAnimationPointer(ANIM_IDLE_UP);
					break;
				case 2:
					setAnimationPointer(ANIM_IDLE_LEFT);
					break;
				case 3:
					setAnimationPointer(ANIM_IDLE_RIGHT);
					break;
			}
		}
		// linear interpolation...yeah idk either, just better
		camera.getPosition().lerp(transform.position.mul(-world.getScale(), new Vector3f()), 0.15f); // mess with the ..f value to smooth
		// camera.setPosition(transform.position.mul(-world.getScale(), new Vector3f()));
	}
}

