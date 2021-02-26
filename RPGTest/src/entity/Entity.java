package entity;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import assets.Assets;
import collision.AABB;
import collision.Collision;
import io.Window;
import rendering.Animation;
import rendering.Camera;
import rendering.Shader;
import world.World;

public abstract class Entity {
	protected AABB boundingBox;
	// protected Texture texture;
	protected Animation[] animations;
	private int animationPointer;
	protected Transform transform;

	public Entity(int maxAnimations, Transform t) {
		// this.texture = new Texture("Koharu2.png");
		this.animations = new Animation[maxAnimations]; // num of frames, frames in one second, name of files
		this.transform = t;
		this.animationPointer=0;
		boundingBox = new AABB(new Vector2f(transform.position.x, transform.position.y), new Vector2f(t.scale.x, t.scale.y));
	}
	
	protected void setAnimation(int index, Animation animation) {
		animations[index]=animation;
	}
	
	public void setAnimationPointer(int index) {
		this.animationPointer=index;
	}
	
	public void move(Vector2f direction) {
		transform.position.add(new Vector3f(direction, 0));
		boundingBox.getCenter().set(transform.position.x, transform.position.y);
	}
	
	public void collideWithTiles(World world) {
		AABB[] boxes = new AABB[25];// 5 by 5 box of collsion boxes
		for(int i = 0; i<5; i++) {
			for(int j = 0; j<5; j++) {
				boxes[i+j*5] = world.getTileBoundingBox((int)(((transform.position.x/2)+0.5f)-(5/2))+i,
				        (int)(((-transform.position.y/2)+0.5f)-(5/2))+j);
			}
		}
		AABB box = null;
		for(int i = 0; i<boxes.length; i++) {
			if(boxes[i]!=null) {
				if(box==null) {
					box = boxes[i];
				}
				Vector2f length1 = box.getCenter().sub(transform.position.x, transform.position.y, new Vector2f());
				Vector2f length2 = boxes[i].getCenter().sub(transform.position.x, transform.position.y, new Vector2f());
				if(length1.lengthSquared()>length2.lengthSquared()) {
					box = boxes[i];
				}
			}
		}
		if(box!=null) {
			Collision data = boundingBox.getCollision(box);
			if(data.isIntersecting) {
				boundingBox.CorrectPosition(box, data);
				transform.position.set(boundingBox.getCenter(), 0);
			}
			for(int i = 0; i<boxes.length; i++) {
				if(boxes[i]!=null) {
					if(box==null) {
						box = boxes[i];
					}
					Vector2f length1 = box.getCenter().sub(transform.position.x, transform.position.y, new Vector2f());
					Vector2f length2 = boxes[i].getCenter().sub(transform.position.x, transform.position.y, new Vector2f());
					if(length1.lengthSquared()>length2.lengthSquared()) {
						box = boxes[i];
					}
				}
			}
			data = boundingBox.getCollision(box);
			if(data.isIntersecting) {
				boundingBox.CorrectPosition(box, data);
				transform.position.set(boundingBox.getCenter(), 0);
			}
		}
	}

	public abstract void update(float delta, Window window, Camera camera, World world);

	public void render(Shader shader, Camera camera, World world) {
		Matrix4f target = camera.getProjection();
		target.mul(world.getWorldMatrix());
		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", transform.getProjection(target));
		animations[animationPointer].bind(0);
		Assets.getModel().render();
	}

	public void collideWithEntity(Entity entity) {
		Collision collision = boundingBox.getCollision(entity.boundingBox);
		if(collision.isIntersecting) {
			collision.distance.x/=2;
			collision.distance.y/=2;
			boundingBox.CorrectPosition(entity.boundingBox, collision);
			transform.position.set(boundingBox.getCenter().x, boundingBox.getCenter().y, 0);
			entity.boundingBox.CorrectPosition(boundingBox, collision);
			entity.transform.position.set(entity.boundingBox.getCenter().x, entity.boundingBox.getCenter().y, 0);
		}
	}
}
