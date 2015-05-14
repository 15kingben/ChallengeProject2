package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Walker {
	
	Body body;
	Sprite sprite;
	Game1Screen game;
	
	public Walker (Vector2 location, World world, Game1Screen game){
		this.game = game;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(location.x, location.y);
		body = world.createBody(bodyDef);
		
		float radius = .8f;
		
		// Create a circle shape and set its radius to 6
		PolygonShape box = new PolygonShape();
		box.setAsBox(1, 2.5f);;
		
		//dimensions = new Vector2(radius * 2,radius * 2);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = .1f; 
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = .2f; // Make it bounce a little bit
		
		Fixture fixture = body.createFixture(fixtureDef);
	}
	
	public void update(){
		System.out.println(body.getAngle());
		if(Math.abs(body.getAngle()) - 0.f < .001f){
			
			  float nextAngle = body.getAngle() + body.getAngularVelocity() * game.getDeltaTime() ;
			  float totalRotation = 0.f - nextAngle;
			  while ( totalRotation < -Math.PI ) 
				  totalRotation += 2 * Math.PI;
			  while ( totalRotation >  Math.PI ) 
				  totalRotation -= 2 * Math.PI;
			  float desiredAngularVelocity = totalRotation / game.getDeltaTime();
			  float torque = (body.getInertia()) * desiredAngularVelocity * game.getDeltaTime();
			  body.applyTorque(torque, true);
			  if(Math.abs(Math.abs(body.getAngle()) - Math.PI/2.f ) <  .001 || Math.abs(Math.abs(body.getAngle()) - Math.PI/2.f - Math.PI) < .001){
				  System.out.println("poop" + body.getAngle());
				  body.applyLinearImpulse(new Vector2(0,20), body.getPosition(), true);
			  }
		}
	}

}
