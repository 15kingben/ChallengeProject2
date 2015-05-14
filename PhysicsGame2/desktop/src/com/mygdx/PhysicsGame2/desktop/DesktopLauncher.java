package com.mygdx.PhysicsGame2.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.mygdx.PhysicsGame2.PhysicsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int)(1280.f );
		config.height = (int)(720.f );
		config.samples = 3;
		
		Settings settings = new Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		//TexturePacker.process(settings, "../android/assets/images/", "../android/assets" , "game");
		new LwjglApplication(new PhysicsGame(), config);
		
	}	
}