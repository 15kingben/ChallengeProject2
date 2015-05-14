package com.mygdx.PhysicsGame2;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Player extends PlayerClass {

	private static final float MAX_COOLDOWN = 2;
	//Sprite cannonSprite;
	Sprite bodySprite;
	Sprite flameSprite;
	Game1Screen game;
	Body body;
	float health;
	int inputNo;
	//image sprite
	final float TOP_SPEED = 5f;// m/s
	final float SPEED = 4f;
	Vector2 look = new Vector2(0 , -1.f);
	final float FRICTION_FORCE = .02f;
	final float JUMP_FORCE = 5.5f;
	final float MAX_FUEL = .1f;
	final float PLAYER_DENSITY = .35f;
	final float EXP_FORCE = 4.f;
	Vector2 dimensions;
	float rocketFuel = MAX_FUEL + 1000f;
	boolean isJumping = false;
	int controllerconfig;
	boolean BINARY_LOOK = true;
	boolean CONTROLLER_INPUT_MODE = false;
	boolean LOOK_FOLLOW_MOVE = true;
	boolean BINARY_JUMP = true;
	float MAX_JUMP = .4f;
	float jumpFuel = MAX_JUMP;
	float HOP_FORCE = .8f;
	float hopDisplayTimer = 0.f;
	Animation runAnimation;
	Animation restAnimation;
	Animation jumpAnimation;
	float walkStateTime = 0;
	Sound swooshSound;
	int BUTTON_JUMP;
	int AXIS_X;
	int AXIS_Y;
	
	public Player(World world, Vector2 location, Controller controller, Game1Screen game1Screen, TextureAtlas atlas, int keyset, int controllerconfig, Body sensor){
		super(controller, keyset);
		grenades = new Array<Grenade>();
		bullets = new Array<Bullet>();

		this.goalSensor = sensor;
		
		
		XmlReader xml = new XmlReader();
		try {
			Element e = xml.parse(Gdx.files.local("config.xml"));
			BUTTON_JUMP = Integer.parseInt(e.getChildByName("BUTTON_JUMP").getAttribute("id"));
			AXIS_X = Integer.parseInt(e.getChildByName("AXIS_X").getAttribute("id"));
			AXIS_Y = Integer.parseInt(e.getChildByName("AXIS_Y").getAttribute("id"));
		} catch (IOException f) {
			// TODO Auto-generated catch block
			f.printStackTrace();
		}
		

		
		
		
		swooshSound = Gdx.audio.newSound(Gdx.files.internal("swoosh.wav"));
		
		if(controllerconfig == -1)
			CONTROLLER_INPUT_MODE = false;
		else
			this.controllerconfig = controllerconfig;
		
		super.coolDown = 0;
		
		runAnimation = new Animation(.2f, atlas.findRegions("SpacemanRun"), PlayMode.LOOP);
		restAnimation = new Animation(.2f , atlas.findRegion("SpacemanRest"));
		jumpAnimation = new Animation(.2f , atlas.findRegion("SpacemanJump"));

		
		dimensions = new Vector2(.3f*2,.425f*2);

		
		//cannonSprite = atlas.createSprite("Cannon");
		bodySprite = new Sprite();
		
		if(keyset == 0)
			bodySprite.setColor(Color.BLUE);
		else
			bodySprite.setColor(Color.ORANGE);
		
		flameSprite = atlas.createSprite("Flames");
		flameSprite.setSize(dimensions.x, dimensions.y);
		flameSprite.setCenter(location.x, location.y);
		flameSprite.setOriginCenter();
		flameSprite.scale(.4f);

		startPos = new Vector2(location);
		
		this.game = game1Screen;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(location.x, location.y);
		bodyDef.fixedRotation = true;
		body = world.createBody(bodyDef);
		body.setBullet(true);
		//body.setLinearDamping(5.f);
		
		float radius = .4f;
		
		/*
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		
		dimensions = new Vector2(radius * 2,radius * 2);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = PLAYER_DENSITY; 
		fixtureDef.friction = .5f;
		fixtureDef.restitution = .2f; // Make it bounce a little bit
		fixtureDef.filter.maskBits = 1;
		
		Fixture fixture = body.createFixture(fixtureDef);*/
		
		
		// Create a circle shape and set its radius to 6
		PolygonShape box = new PolygonShape();
		box.setAsBox(.3f, .425f);
		

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = PLAYER_DENSITY; 
		fixtureDef.friction = .5f;
		fixtureDef.restitution = .2f; // Make it bounce a little bit
		fixtureDef.filter.maskBits = 1;
		
		Fixture fixture = body.createFixture(fixtureDef);
		
	}

	public void applyPlayerForce(){
		Vector2 input;
		input = getAxisInput(this , .2f, AXIS_X, AXIS_Y);
		
		input.y = 0;//-input.y;
		Vector2 real;
				
		if(Math.abs(body.getLinearVelocity().x) > TOP_SPEED){//if greater than top speed, must be working against force
			if(input.x * body.getLinearVelocity().x > 1){//Math.abs(Math.acos(input.dot(body.getLinearVelocity()) / input.len() / body.getLinearVelocity().len())) < 90){
				//float dot = input.dot(body.getLinearVelocity()) / body.getLinearVelocity().len();
				//Vector2 vec = body.getLinearVelocity().nor().scl(dot);
				//real = input.sub(vec).scl(SPEED);
				real = new Vector2(0,0);//body.getLinearVelocity().scl(-.2f).x, 0);
			}else{//do it normal
				real = input.scl(SPEED);
			}
		}else{
			real = input.scl(SPEED);
		}
		
		if(real.x == 0){
			//real.x = body.getLinearVelocity().x*-.1f;
		}
		body.applyForce(real, body.getPosition(), true);
	}
	
	public void updateLook(){
		Vector2 v = new Vector2(0,0);
				
		if(LOOK_FOLLOW_MOVE){
			v = getAxisInput(this, .4f, AXIS_X , AXIS_Y);
		}
		
		v.y *= -1;
		if(v.len() > .001f){
			if(BINARY_LOOK){
				look = new Vector2(v.x,0).nor();
				look.setAngle(look.angle() + (90f - look.angle())*(.4f));
			}else
				look = v.nor();
		}
	}
	
	
	public Vector2 getAxisInput(Player player, float deadzone, int x, int y){
		
		//dead zone
		Vector2 v = new Vector2(controller.getAxis(x), controller.getAxis(y));
		if(! (v.len() < deadzone)){
			return v;
		}		

		return new Vector2(0,0);
	}
	
	public void draw(ShapeRenderer sr, SpriteBatch batch){
		//Vector2 v0 = body.getPosition();
		//Vector2 v1 = new Vector2(v0).add(new Vector2(look.x, look.y).scl(10.f));
		//System.out.println(v0 + " " +  v1);
		//sr.line(v0, v1);
		bodySprite.setPosition(body.getPosition().x - dimensions.x /2.f , body.getPosition().y - dimensions.y / 2.f);
		bodySprite.setSize(dimensions.x, dimensions.y);
		
		
		if (Math.abs(body.getLinearVelocity().y) > .01f){
			bodySprite.setRegion(jumpAnimation.getKeyFrame(game.getStateTime(), true));
			walkStateTime = 0;
		}else if(Math.abs(body.getLinearVelocity().x) > .2f){
			bodySprite.setRegion(runAnimation.getKeyFrame(game.getStateTime(), false));
			walkStateTime += game.getDeltaTime();
		}else{
			bodySprite.setRegion(restAnimation.getKeyFrame(game.getStateTime()));
			walkStateTime = 0;
		}
		
		
		
		flameSprite.setCenter(body.getPosition().x, body.getPosition().y);
		//flameSprite.setSize(dimensions.x, dimensions.y);
		flameSprite.setOriginCenter();
		
		
		if(look.x > 0){
			bodySprite.setFlip(true, false);
			flameSprite.setFlip(true, false);
		}else{
			bodySprite.setFlip(false, false);
			flameSprite.setFlip(false,  false);
		}
		
//		cannonSprite.setCenter(body.getPosition().x , body.getPosition().y - dimensions.y + dimensions.y * (58.f/50.f));
//		
//		cannonSprite.setSize(dimensions.x, dimensions.y);
//		//cannonSprite.setOrigin(body.getPosition().x , body.getPosition().y );//- dimensions.y / 2.f +  dimensions.y*(58.f/50.f));
//		cannonSprite.setOriginCenter();
//		cannonSprite.setScale(2.f);
//		cannonSprite.setRotation(look.angle());
//		
		
		if(isJumping)
			flameSprite.draw(batch);
		if(hopDisplayTimer > 0){
			hopDisplayTimer -= game.getDeltaTime();
			flameSprite.draw(batch);
		}
		bodySprite.draw(batch);
		//cannonSprite.draw(batch);
		
		for(Grenade g : grenades)
			g.draw(batch);
		
	}

	public void update() {
		if(nextGrenade){
			createGrenade();
			nextGrenade = false;
		}
		
		
		coolDown -= game.getDeltaTime();
		if(rocketFuel < MAX_FUEL){
			rocketFuel += game.getDeltaTime() / 3.f;
		}
		
		if(jumpFuel < MAX_JUMP){
			jumpFuel += game.getDeltaTime() / 3.f;
		}
		
		updateLook();
		isJumping = jump();
		applyPlayerForce();
		if(!CONTROLLER_INPUT_MODE)
			applyFriction();
		updateGrenades();
	}

	private void updateGrenades() {
		for( Grenade g : grenades){
			g.update();
			if(!g.active)
				grenades.removeValue(g, true);
		}
		
	}

	private void applyFriction() {
		//Vector2 friction = body.getLinearVelocity().nor().scl(-FRICTION_FORCE * body.getLinearVelocity().len2() / 2.f /50.f );
		Vector2 friction = body.getLinearVelocity().scl(-.8f);
		body.applyForce(new Vector2(friction.x , 0), body.getPosition(), true);
		if(body.getLinearVelocity().y < 0){
			body.applyForce(new Vector2(0, body.getLinearVelocity().y*-.5f), body.getPosition(), true);
		}
	}
	
	public void createGrenade(){
		coolDown = MAX_COOLDOWN;
		grenades.add(new Grenade(body.getPosition().add(new Vector2(look).nor().scl(body.getFixtureList().get(0).getShape().getRadius() + .2f)), new Vector2(body.getLinearVelocity()).add(new Vector2(look).nor().scl(1.f)), 2.f,  game.world, game , EXP_FORCE));
	}

	public boolean jump() {
		// IF COLLIDING
		if(BINARY_JUMP){
			if(jumpFuel >= MAX_JUMP ){
				
					if(controller.getButton(BUTTON_JUMP)){
						jumpFuel = 0;
						body.applyLinearImpulse(new Vector2(0,1).scl(HOP_FORCE), body.getPosition(), true);
						hopDisplayTimer = .1f;
						swooshSound.play(.5f, .5f, 0.f);
						return true;
					}
					
					
			}
		}else{
		if(rocketFuel > 0 ){
			if(controllerconfig == 0){
				if(controller.getButton(1)){
					rocketFuel -= game.getDeltaTime();
					if(rocketFuel > .02)
						body.applyForce(new Vector2(0, 1).scl(JUMP_FORCE), body.getPosition(), true);
					return true;
				}
			}else if(controllerconfig == 1){
				if(controller.getButton(0)){
					rocketFuel -= game.getDeltaTime();
					if(rocketFuel > .02)
						body.applyForce(new Vector2(0, 1).scl(JUMP_FORCE), body.getPosition(), true);
					return true;
				}
			}
				
		}
		}
		return false;
	}

	@Override
	public void createBullet() {
		// TODO Auto-generated method stub
		
	}
	
}
