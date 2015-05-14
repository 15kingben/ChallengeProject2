package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class Map {

	Sprite crosshairsSprite;
	Array<DrawObject> bodies = new Array<DrawObject>();
	Array<DrawObject> sensors = new Array<DrawObject>();
	Array<Explosion> explosions = new Array<Explosion>();

	Vector2 center;
	Vector2 dimensions;
	Game1Screen game;
	float shakeTime;
	PerlinNoise x;
	PerlinNoise y;
	Vector2 originalPos;
	float slowMoTimer = 0.f;
	Body ballBody;
	float BALL_ANTIGRAV = 1.f;
	public boolean nextTransform = false;
	public Vector2 start1;
	public Vector2 start2;
	public boolean nextReset = false;
	Sound wubSound;
	long wubID;
	Animation blinkyLight;
	
	public Map(Vector2 center, float width, float height, Game1Screen game){
		this.game = game;
		this.center = center;
		dimensions = new Vector2(width, height);
		shakeTime = -1.f;
		crosshairsSprite = game.atlas.createSprite("Crosshairs");
		
		wubSound = Gdx.audio.newSound(Gdx.files.internal("tractorBeam.mp3"));
		
	}
	
	public Map(Game1Screen game2){
		wubSound = Gdx.audio.newSound(Gdx.files.internal("tractorBeam.mp3"));
		wubID = wubSound.play(.6f);
		wubSound.pause();
		wubSound.setLooping(wubID, true);

		blinkyLight = new Animation(.4f,game2.atlas.findRegions("Light"), PlayMode.LOOP);
		
		shakeTime = -1.f;
		this.game = game2;
		crosshairsSprite = game2.atlas.createSprite("Crosshairs");
		crosshairsSprite.setSize(2.f, 2.f);
	}

	public void cameraShake(float shakeTime, float pers) {
		
		if(this.shakeTime < 0.f){
			originalPos = new Vector2(game.camera.position.x , game.camera.position.y);
		}
	
		x = new PerlinNoise((int) (shakeTime + 5), pers);
		y = new PerlinNoise((int) (shakeTime + 5), pers);
		this.shakeTime = shakeTime;
	}
	
	public void update(){
		
		if(nextTransform){
			nextTransform = false;
			ballBody.setTransform(center, 0 );
			ballBody.setLinearVelocity(new Vector2(0,0));
			ballBody.setAngularVelocity(0);
		}
		
		for(DrawObject o : bodies){
			o.update();
		}
		
		for(Explosion e : explosions){
			if(e.stateTime > e.DURATION){
				//e.explosionSound.dispose();
				explosions.removeValue(e, true);
			}
		}
		
		if(shakeTime <= 0){
		}else{
			shakeTime -= game.getDeltaTime();
			//if(slowMoTimer<=0.f){
				game.updateCamera(game.camera);
				//game.camera.position.set(originalPos, 0);
				game.camera.translate(new Vector2(x.cameraNoise(2+shakeTime), y.cameraNoise(2+shakeTime)));
			//}

		}
		
		if(slowMoTimer > 0.f){
			slowMoTimer -= Gdx.graphics.getDeltaTime();
		}else{
			game.timeSpeed = 1.f;
		}
		
		if(nextReset){
			this.shakeTime = 0;
			this.slowMoTimer = 0;
			reset();
			reset();
			nextReset = false;
		}
		//.scl(-BALL_ANTIGRAV)
		
		boolean pause = true;
		//sensor gravity
		for(DrawObject o: sensors){
			if(new Vector2(ballBody.getPosition()).sub(o.body.getPosition()).len() < 2.5f){
				ballBody.applyForce(new Vector2(ballBody.getPosition()).sub(o.body.getPosition()).scl(-.5f*new Vector2(ballBody.getPosition()).sub(o.body.getPosition()).len()), ballBody.getPosition(), true);

				wubSound.resume(wubID);
				pause = false;
			}
		}
		if(pause){
			wubSound.pause();
		}
		
	}
	
	Sprite s = new Sprite();
	public void draw(SpriteBatch batch){
		
		
		for(DrawObject o: sensors){
			if(new Vector2(ballBody.getPosition()).sub(o.body.getPosition()).len() < 3.5f){
				//ballBody.applyForce(new Vector2(ballBody.getPosition()).sub(o.body.getPosition()).scl(-.5f*new Vector2(ballBody.getPosition()).sub(o.body.getPosition()).len()), ballBody.getPosition(), true);
				s.setRegion(blinkyLight.getKeyFrame(game.getStateTime(),true));
				s.setCenter(o.body.getPosition().x, o.body.getPosition().y);
				s.setSize(.5f, .5f);
				s.setColor(Color.GREEN);
				//wubSound.resume(wubID);
				//pause = false;
			}else{
			s.setRegion(blinkyLight.getKeyFrame(game.getStateTime(),true));
			s.setCenter(o.body.getPosition().x, o.body.getPosition().y);
			s.setSize(.5f, .5f);
			s.setColor(Color.RED);
			}
			s.draw(batch);
		}
		
		
		Vector2 highestPos = null;
		float highestY = -666.f;
		for (DrawObject b : bodies){
			b.draw(batch);
			if(true){//b.body.getType() != BodyType.StaticBody && !b.isTNT){
				if(highestPos == null){
					highestPos = b.body.getPosition();
					highestY = b.getHighestY();
				}else{
					if(highestY < b.getHighestY()){
						highestPos = b.body.getPosition();
					}
				}
			}
			
		}
		for(Explosion e : explosions){
			e.draw(batch);
		}

				
		
	}

	public void createBall() {
		// TODO Auto-generated method stub
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(center.x, center.y);
		ballBody = game.world.createBody(bodyDef);
		ballBody.setLinearDamping(5.f);
		ballBody.setBullet(true);
		
		float radius = .8f;
		
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = .6f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = .6f; // Make it bounce a little bit
		
		Fixture fixture = ballBody.createFixture(fixtureDef);
	}
	
	public void reset(){
		for(DrawObject b: bodies){
			b.reset();
			
		}
		for(PlayerClass p : game.players){
			((Player)(p)).body.setTransform(p.startPos, 0);
		}
	}
}
