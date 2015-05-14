package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class StaticPlayer extends PlayerClass{

	Sprite cannonSprite;
	Sprite bodySprite;
	Sprite flameSprite;
	Game1Screen game;
	Body body;
	float health;
	int inputNo;
	//image sprite
	final float TOP_SPEED = 10.f;// m/s
	final float SPEED = 6.f;
	final float keySpeed = 3.f;
	Vector2 look = new Vector2(0 , -1.f);
	final float FRICTION_FORCE = .02f;
	final float JUMP_FORCE = 7.f;
	Vector2 dimensions;
	float rocketFuel = .5f;
	boolean isJumping = false;
	Vector2 Pos;
	
	
	public StaticPlayer(World world, Vector2 location, Controller controller, Game1Screen game1Screen, TextureAtlas atlas, int keyset, Body sensor){
		super(controller, keyset);
		super.coolDown = 0.f;
		
		this.goalSensor = sensor;
		
		cannonSprite = atlas.createSprite("Cannon");
		
		
		dimensions = new Vector2(3,3);		
		this.game = game1Screen;
		
		Pos = location;
		grenades = new Array<Grenade>();
		bullets = new Array<Bullet>();
	}

	
	public void updateLook(){
		Vector2 v = getAxisInput(.6f, 1 ,0);
		v.y *= -1;
		if(v.len() > .01f){
			look = v.nor();
		}
		
		look.setAngle(look.angle() +  keySpeed * getButtonInput());
	}
	
	
	public Vector2 getAxisInput( float deadzone, int x, int y){
		
		//dead zone
		Vector2 v = new Vector2(controller.getAxis(x), controller.getAxis(y));
		if(! (v.len() < deadzone)){
			return v;
		}		

		return new Vector2(0,0);
	}
	
	
	public float getButtonInput(){
		if(keyset == 0){
			if(Gdx.input.isKeyPressed(Keys.UP)){
				return 1;
			}if(Gdx.input.isKeyPressed(Keys.DOWN)){
				return -1;
			}
		}else if(keyset == 1){
			if(Gdx.input.isKeyPressed(Keys.W)){
				return 1;
			}if(Gdx.input.isKeyPressed(Keys.S)){
				return -1;
			}
		}
		return 0;
		
	}
	
	public void draw(ShapeRenderer sr, SpriteBatch batch){
		//Vector2 v0 = body.getPosition();
		//Vector2 v1 = new Vector2(v0).add(new Vector2(look.x, look.y).scl(10.f));
		//System.out.println(v0 + " " +  v1);
		//sr.line(v0, v1);
		
		cannonSprite.draw(batch);
		
	}

	public void update() {
		cannonSprite.setCenter(Pos.x , Pos.y - dimensions.y + dimensions.y * (58.f/50.f));
		
		cannonSprite.setSize(dimensions.x, dimensions.y);
		cannonSprite.setOriginCenter();
		cannonSprite.setRotation(look.angle());
		
		coolDown -= game.getDeltaTime();
		
		if(fireBullet){
			createBullet();
			fireBullet = false;
		}
		
		updateLook();
		updateGrenades();
		updateBullets();
		
	}

	private void updateGrenades() {
		for( Grenade g : grenades){
			g.update();
			if(!g.active)
				grenades.removeValue(g, true);
		}
		
	}

	private void updateBullets() {
		for( Bullet g : bullets){
			g.update();
			if(!g.active)
				bullets.removeValue(g, true);
		}
		
	}
	
	private void applyFriction() {
		Vector2 friction = body.getLinearVelocity().nor().scl(-FRICTION_FORCE * body.getLinearVelocity().len2() / 2.f /50.f );
		body.applyForce(friction, body.getPosition(), true);
	}
	
	
	
	public void createGrenade(){
		coolDown = 1.5f;
		grenades.add(new Grenade(Pos, new Vector2(look).nor().scl(10.f), 3.f, game.world, game, 1.f));
	}


	@Override
	public void createBullet() {
		coolDown = 1.5f;
		bullets.add(new Bullet(Pos, new Vector2(look).nor().scl(30.f), game.world, game, 1.f, this));
		
	}
	
}
