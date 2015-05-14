package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class RayCastCallBackExp implements RayCastCallback {

	
	Fixture cFix;
	Vector2 cPoint;
	Vector2 cNormal;
	float cFrac = 100.f;
	
	
	
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		
		if(fraction < cFrac){
			cFix = fixture;
			cPoint = new Vector2(point.x , point.y);
			cNormal = new Vector2(normal.x, normal.y);
		}
		
		return fraction;
	}
	
	public Fixture closestFixture(){
		return cFix;
	}

	public Vector2 closestNormal(){
		return cNormal;
	}
	
	public Vector2 closestPoint(){
		return cPoint;
	}
	
	public float getDistance(){
		return cFrac;
	}
}
