package com.mygdx.PhysicsGame2;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class ControllerProcessor extends ControllerAdapter {

	final int PC_X = 0;
	final int PC_Y = 1;
	final int MAC_X = 1;
	final int MAC_Y = 0;
	int BUTTON_GRENADE;
	
	private Game1Screen game;
	
	public ControllerProcessor(Game1Screen game1Screen){
		this.game = game1Screen;
		XmlReader xml = new XmlReader();
		try {
			Element e = xml.parse(Gdx.files.local("config.xml"));
			BUTTON_GRENADE = Integer.parseInt(e.getChildByName("BUTTON_GRENADE").getAttribute("id"));
			
		} catch (IOException f) {
			// TODO Auto-generated catch block
			f.printStackTrace();
		}
	}
	
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value){
		
		//System.out.println(axisCode);
		//System.out.println((new Vector2(controller.getAxis(1), controller.getAxis(0))));
		return true;
	}
	
	@Override
	public boolean buttonDown(Controller controller, int buttonCode){
		System.out.println(buttonCode);
		for(PlayerClass p : game.players){
			if(p.controller.equals(controller)){
				
					if(buttonCode == BUTTON_GRENADE){
						if(p.coolDown <=0)
							p.nextGrenade = true;
					}else if(buttonCode == 1){
						//p.jump();
					}else if(buttonCode == 0){
						if(p.coolDown <=0)
							p.createBullet();
					}
			}
		}
		
		
		
		return false;
	}
	
	@Override
	public boolean buttonUp(Controller controller, int buttonCode){
		return true;
	}
	
	private int oA(int axisCode){
		return (axisCode == 0) ? 1 : 0;
	}
	
	
}



//if(axisCode == PC_X){
//	player.forceLeft(controller.getAxis(axisCode));
//}
//if(axisCode == PC_Y){
//	player.forceRight(controller.getAxis(axisCode));
//}