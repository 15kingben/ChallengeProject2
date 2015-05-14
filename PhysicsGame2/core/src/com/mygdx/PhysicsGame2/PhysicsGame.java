package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.Game;

public class PhysicsGame extends Game{

	@Override
	public void create() {

		this.setScreen(new Game1Screen(this));
	}
	
	public void render(){
		super.render();
	}
	
	
}