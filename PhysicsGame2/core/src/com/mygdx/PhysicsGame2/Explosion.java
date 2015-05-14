package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Explosion {
	
	
	Animation animation;
	float stateTime;
	Vector2 position;
	Sprite sprite;
	float DURATION = .4f;
	Game1Screen  game;
	Sound explosionSound;
	
	public Explosion(Vector2 loc, Vector2 dimensions,  Game1Screen game, boolean TNT){
		stateTime = 0;
		animation = new Animation(DURATION/16.f, game.atlas.findRegions("Explosion"), PlayMode.NORMAL);
		sprite = new Sprite();
		sprite.setCenter(loc.x - dimensions.x/2f, loc.y - dimensions.y/2f);
		sprite.setSize(dimensions.x, dimensions.y);
		
		if(!TNT){
			explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosionSound.mp3"));
			explosionSound.play(.1f, .5f, 0.f);

		}else{
		
			explosionSound = Gdx.audio.newSound(Gdx.files.internal("tntSound.mp3"));
			explosionSound.play(.5f, 1f, 0.f);

		}
		this.game = game;
	}

	public void draw(SpriteBatch batch){
		stateTime += game.getDeltaTime();
		sprite.setRegion(animation.getKeyFrame(stateTime));
		sprite.draw(batch);
		
	}
}
