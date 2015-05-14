package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Grenade {
	
	float fuseTime;
	Body body;
	private World world;
	boolean active = true;
	Game1Screen game;
	private float expForce;
	public boolean toStatic = false;
	Sprite GrenadeSprite;
	Vector2 dimensions;
	Fixture fixture;
	Animation animation;
	float stateTime = 0;
	
	public Grenade(Vector2 location, Vector2 velocity, float fuseTime, World world, Game1Screen game2, float expForce){
		this.fuseTime = fuseTime;
		
		this.expForce = expForce;
		
		this.game = game2;
		
		this.world = world;
		
		GrenadeSprite = new Sprite();
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(location.x, location.y);
		bodyDef.allowSleep = false;
		body = world.createBody(bodyDef);
		body.setLinearVelocity(velocity);
		//body.setLinearDamping(8);
		
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(.2f);
		dimensions= new Vector2(.4f, .4f);
		GrenadeSprite.setSize(dimensions.x, dimensions.y);
		Array<AtlasRegion> regions = game.atlas.findRegions("Grenade");
		animation = new Animation(fuseTime/((float) regions.size), game.atlas.findRegions("Grenade"), PlayMode.NORMAL);
		System.out.println(animation.getFrameDuration());

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = .4f; // Make it bounce a little bit
		fixtureDef.filter.categoryBits = 2;
		
		fixture = body.createFixture(fixtureDef);
	}
	
	public void update(){
		fuseTime -= game.getDeltaTime();
		if(fuseTime < 0){
			game.map.explosions.add(new Explosion(body.getPosition(), new Vector2(.6f,.6f), game, false));
			world.destroyBody(body);
			game.etester.createWorldExplosion(expForce, body.getPosition(), 0.f, 0f, .4f, 2.5f);
			active = false;
		}
		
		if(active && toStatic){
			body.setType(BodyType.StaticBody);
		}
		
		
	}
	
	
	public void draw(SpriteBatch batch){
		GrenadeSprite.setCenter(body.getPosition().x, body.getPosition().y);
		GrenadeSprite.setOriginCenter();
		GrenadeSprite.setRotation(body.getAngle());
		stateTime += game.getDeltaTime();
		GrenadeSprite.setRegion(animation.getKeyFrame(stateTime,false));
		GrenadeSprite.draw(batch);
	}
	
}
