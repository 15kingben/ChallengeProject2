package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape.Type;

public class DrawObject {

	Animation animation;
	Body body;
	Vector2 dimensions;
	Sprite sprite;
	Game1Screen game;
	boolean isTNT = false;
	boolean isExplosive = false;
	public boolean toBeDestroyed = false;
	float r;
	Vector2 high = new Vector2(0,0);
	Vector2 startPosition;
	float startRotation;
	
	public DrawObject(Body body, Vector2 dimensions, String spritePath, Game1Screen game2){
		this.game = game2;
		this.body = body;
		
		startPosition = new Vector2(body.getPosition());
		startRotation = body.getAngle();
		
		this.dimensions = dimensions;
		r = dimensions.len();
		sprite = game2.atlas.createSprite(spritePath);
		animation = new Animation(.2f, game2.atlas.findRegions(spritePath), PlayMode.LOOP);
		if(spritePath.equals("TNT")){
			isExplosive = true;
			sprite.flip(false, true);
		}
		sprite.setSize(dimensions.x, dimensions.y);
	}
	
	public void draw(SpriteBatch batch){
		if(game.debugDrawMode == false){
			if(body.getType() != BodyType.StaticBody ){
				sprite.setCenter(body.getPosition().x, body.getPosition().y);
				sprite.setOriginCenter();
				sprite.setRotation((float) (body.getAngle() * 180.f / Math.PI));
				sprite.setTexture(animation.getKeyFrame(game.getStateTime()).getTexture());
				sprite.draw(batch);
			}
		}else{
			sprite.setCenter(body.getPosition().x, body.getPosition().y);
			sprite.setOriginCenter();
			sprite.setRotation((float) (body.getAngle() * 180.f / Math.PI));
			sprite.setTexture(animation.getKeyFrame(game.getStateTime()).getTexture());
			sprite.draw(batch);
		}
	}
	
	public void update(){
		if(toBeDestroyed){
			//game.world.destroyBody(body);
			game.etester.createWorldExplosion(50.f, body.getPosition(), 1.4f, 1.f, .8f , 7.f);
			game.map.explosions.add(new Explosion(body.getPosition(), new Vector2(dimensions).scl(2.f), game, true));
			body.setTransform(new Vector2(1000,1000), 0);
			//game.map.bodies.removeValue(this, true);
			toBeDestroyed = false;
		}
	}
	
	public float getHighestY(){
		if(body.getFixtureList().get(0).getShape().getType() == Type.Circle){
			return body.getPosition().y + dimensions.y / 2.f;
		}else {			
			
			float mx = -666.f;
			Vector2 v = new Vector2();
			for(int i = 0; i < ((PolygonShape)body.getFixtureList().get(0).getShape()).getVertexCount(); i++){
				((PolygonShape)body.getFixtureList().get(0).getShape()).getVertex(i, v);
				v.rotateRad(body.getAngle());
				if(v.y + body.getPosition().y > mx){
					mx = v.y + body.getPosition().y;
					high = new Vector2(v).add(body.getPosition());
					//System.out.println(high);
				}
			}
			
				
			return mx;
		}
	}
	
	public void reset(){
		body.setTransform(startPosition, startRotation);
		body.setLinearVelocity(new Vector2(0,0));
		body.setAngularVelocity(0);
	}
	
}
