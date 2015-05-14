package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

public class InputProcessor extends InputAdapter {

	Game1Screen game;
	public InputProcessor(Game1Screen game1Screen){
		this.game = game1Screen;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
//		Vector3 loc = new Vector3(game.camera.unproject(new Vector3(screenX, screenY, 0)));
//		game.etester.createExplosion(loc.x, loc.y, 100.f, 10.f);
//		game.map.cameraShake(1, .4f);
//		game.timeSpeed = .2f;
//		game.map.slowMoTimer = 1.5f;
		return true;
	}
	
	@Override
	public boolean keyUp(int keyCode){
		switch(keyCode){
			case Keys.SPACE:
			game.etester.createRandomExplosion();
			break;
		
		}
		return true;
	}
	
	@Override
	public boolean keyDown(int keyCode){
		switch(keyCode){
			case Keys.NUM_1:
				// set resolution to HD ready (1280 x 720) and set full-screen to true
				Gdx.graphics.setDisplayMode(1280, 720, true);
				// set resolution to default and set full-screen to true
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
				break;
			
			case Keys.ESCAPE:
				Gdx.graphics.setDisplayMode(1280, 720, false);
				break;
				
			case Keys.D:
				for(PlayerClass p : game.players){
					if(p.keyset == 1){
						if(p.coolDown < 0)
						p.fireBullet = true;
					}
				}
				break;
			case Keys.LEFT:
				for(PlayerClass p : game.players){
					if(p.keyset == 0){
						if(p.coolDown < 0)
						p.fireBullet = true;
					}
				}
				break;
			case Keys.R:
				game.map.nextReset = true;
				break;
		
		}
		return true;
	}
	
}
