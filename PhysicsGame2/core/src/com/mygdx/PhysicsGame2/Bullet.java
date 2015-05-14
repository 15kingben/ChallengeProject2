package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Bullet {
	
	Body body;
	private World world;
	boolean active = true;
	Game1Screen game;
	private float expForce;
	boolean toBeDestroyed = false;
	
	PlayerClass player;
	
	public Bullet(Vector2 location, Vector2 velocity,  World world, Game1Screen game2, float expForce, PlayerClass player){
		
		this.player = player;
		
		this.expForce = expForce;
		
		this.game = game2;
		
		this.world = world;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(location.x, location.y);
		bodyDef.allowSleep = false;
		body = world.createBody(bodyDef);
		body.setLinearVelocity(velocity);
		
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(.2f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = .4f; // Make it bounce a little bit
		
		Fixture fixture = body.createFixture(fixtureDef);
	}
	
	public void update(){
		if(toBeDestroyed){
			game.world.destroyBody(body);
			game.etester.createWorldExplosion(5.f, body.getPosition(), .6f, .4f, .6f, 4.f );
			player.bullets.removeValue(this, true);
			active = false;
		}
	}
	
	
}
