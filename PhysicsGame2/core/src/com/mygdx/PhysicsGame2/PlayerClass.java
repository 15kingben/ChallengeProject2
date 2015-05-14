package com.mygdx.PhysicsGame2;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public abstract class PlayerClass {
	
	Controller controller;
	float coolDown;
	Array<Grenade> grenades;
	Array<Bullet> bullets;
	int keyset;
	Body goalSensor;
	public boolean fireBullet = false;
	public Vector2 startPos;
	public boolean nextGrenade;

	
	public PlayerClass(Controller controller, int keyset){
		this.controller = controller;
		this.keyset = keyset;
	}
	
	public abstract void update();

	public abstract void draw(ShapeRenderer sr, SpriteBatch batch);

	public abstract void createGrenade() ;
	
	public abstract void createBullet() ;
		
	
}
