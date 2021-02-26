package collision;

import org.joml.Vector2f;

//link for more on AABB collision
//https://www.youtube.com/watch?v=bQJat8QoIfE&list=PLILiqflMilIxta2xKk2EftiRHD4nQGW0u&index=25&t=1s

public class AABB {
	private Vector2f center;
	private Vector2f halfExtent;

	public AABB(Vector2f center, Vector2f halfExtent) {
		this.center = center;
		this.halfExtent = halfExtent;
	}
	
	public Collision getCollision(AABB box2) {
		Vector2f distance = box2.center.sub(center, new Vector2f());//find distance from AABB box center to other AABB box center
		distance.x = (float)Math.abs(distance.x);
		distance.y = (float)Math.abs(distance.y);
		distance.sub(halfExtent.add(box2.halfExtent, new Vector2f()));//subtracts both half-extents
		return new Collision(distance, (distance.x<0 && distance.y<0));
	}
	
	public void CorrectPosition(AABB box2, Collision data) {
		Vector2f correction = box2.center.sub(center, new Vector2f());
		if(data.distance.x>data.distance.y) {
			if(correction.x>0) {
				center.add(data.distance.x, 0);
			}
			else {
				center.add(-data.distance.x, 0);
			}
		}
		else {
			if(correction.y>0) {
				center.add(0,data.distance.y);
			}
			else {
				center.add(0,-data.distance.y);
			}
		}
	}

	public Vector2f getCenter() {
		return center;
	}

	public Vector2f getHalfExtent() {
		return halfExtent;
	}
	
}
