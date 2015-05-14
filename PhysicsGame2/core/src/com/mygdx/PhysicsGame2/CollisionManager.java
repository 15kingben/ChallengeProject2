package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class CollisionManager implements ContactListener {

	
	private Game1Screen game;

	public CollisionManager(Game1Screen game1Screen){
		this.game = game1Screen;
	}
	
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub

//		for(DrawObject b : game.map.bodies){
//			if(b.body == contact.getFixtureA().getBody() || b.body == contact.getFixtureB().getBody()){
//				if(b.isTNT){
//					b.toBeDestroyed = true;
//				}
//			}
//		}
		
//		for(PlayerClass p : game.players){
//			
//			for(Bullet b : p.bullets){
//				if(b.body == contact.getFixtureA().getBody() || b.body == contact.getFixtureB().getBody()){
//					b.toBeDestroyed = true;
//				}
//			}
//		}
		
		for(PlayerClass p : game.players){
			if(p.goalSensor == contact.getFixtureA().getBody() || p.goalSensor == contact.getFixtureB().getBody()){
				if(game.map.ballBody == contact.getFixtureA().getBody() || game.map.ballBody == contact.getFixtureB().getBody()){
					if(contact.getFixtureA().getBody() == game.map.sensors.get(1).body || contact.getFixtureB().getBody() == game.map.sensors.get(1).body){
						game.blueWins = true;
					}
					game.nextReset = true;
				}
			}
			
			for(Grenade g : p.grenades){
				if(g.body == contact.getFixtureA().getBody() || g.body == contact.getFixtureB().getBody()){
					Body otherBody;
					if(g.body == contact.getFixtureA().getBody())
						otherBody = contact.getFixtureA().getBody();
					else
						otherBody = contact.getFixtureA().getBody();
					if (BodyType.DynamicBody != otherBody.getType() && BodyType.DynamicBody != otherBody.getType() && !contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor()){//(((Player)p).body != contact.getFixtureA().getBody() && ((Player)p).body != contact.getFixtureA().getBody() ){
						g.toStatic = true;
					}
				}
			}
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
