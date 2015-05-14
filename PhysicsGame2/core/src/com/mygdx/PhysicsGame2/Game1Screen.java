package com.mygdx.PhysicsGame2;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlWriter;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.PhysicsGame2.ControllerProcessor;

public class Game1Screen implements Screen {
	SpriteBatch batch;
	Texture img;
	World world;
	Box2DDebugRenderer debug;
	OrthographicCamera camera;
	FitViewport viewport;
	EnvironmentTester etester;
	final float simTime = 1/60.f;
	float stateTime = 0.0f;
	ShapeRenderer sr;
	Vector2 temp = new Vector2(0,0);
	Array<PlayerClass> players = new Array<PlayerClass>();
	ObjectMap<Body, DrawObject> BodyObjects = new ObjectMap<Body, DrawObject>();
	Map map;
	float timeSpeed = 1.f;
	TextureAtlas atlas;
	Game game;
	boolean staticMode = true;
	Walker walker;
	float C_BUFFER = 4.f;
	float CAM_SPEED = 4.f; //smaller is faster
	Texture starryBackground;
	Texture LevelScreen;
	Music music;
	Music slowMusic;
	boolean nextReset = false;
	float resetTimer = 0.f;
	boolean blueWins = false;
	Sprite BlueWinsSprite, RedWinsSprite;
	boolean debugDrawMode = false;
	
	
	public Game1Screen (Game game) {
		this.game = game;
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		FileHandle ConfigFile = Gdx.files.local("config.xml");
		if(!ConfigFile.exists()){
			try {
				ConfigFile.file().createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			genConfigFile(ConfigFile);
		}
		
		atlas = new TextureAtlas(Gdx.files.internal("game.atlas"));
		
		world = new World(new Vector2(0 , -10), true);
		world.setContactListener(new CollisionManager(this));
		
		debug = new Box2DDebugRenderer();
		
		music = Gdx.audio.newMusic(Gdx.files.internal("spacemusic.mp3"));
		slowMusic = Gdx.audio.newMusic(Gdx.files.internal("spacemusics.mp3"));
		music.setLooping(true);
		slowMusic.setLooping(true);
		music.setVolume(.1f);
		slowMusic.setVolume(.1f);
		slowMusic.play();
		slowMusic.pause();
		music.play();

		
		sr = new ShapeRenderer();
		
		Gdx.input.setInputProcessor(new InputProcessor(this));
		
		starryBackground = new Texture(Gdx.files.internal("StarryBackground.jpg"));
		LevelScreen = new Texture(Gdx.files.internal("LevelScreen.png"));

		
		BlueWinsSprite = atlas.createSprite("BlueWins");
		RedWinsSprite = atlas.createSprite("OrangeWins");
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false,40,22.5f);
		viewport = new FitViewport(40,22.5f, camera);
		//camera.zoom = 2;
		//camera.translate(new Vector2(20,20));
		
		etester = new EnvironmentTester(world, this);
		
		//setMode
		etester.spawning = true;
		etester.mapName = "Map11.xml";
		staticMode = false;
		
		map = etester.gleedMapFromFile();
		camera.translate(new Vector2(camera.position.x, camera.position.y).sub(map.center).scl(-1));
		//System.out.println(map.center + " " + camera.position);
		
		//map.createBall();
		
		
		System.out.println("Controllers: " + Controllers.getControllers().size);
		int i = 0;
		for (Controller controller : Controllers.getControllers()) {
			System.out.println("#" + i++ + ": " + controller.getName());
		}
		if (Controllers.getControllers().size < 2)
			System.out.println("Not Enough Controllers");
		Controllers.addListener(new ControllerProcessor(this));
		
		
		
		//Players
		if(!staticMode){
		if(Controllers.getControllers().size>0)
		players.add(new Player(world, map.start1, Controllers.getControllers().get(0), this , atlas, 0, 0, map.sensors.get(1).body ));
		if(Controllers.getControllers().size>1)
		players.add(new Player(world, map.start2, Controllers.getControllers().get(1), this , atlas, 1, 1, map.sensors.get(0).body ));		
		}else{
		if(Controllers.getControllers().size>0)
			players.add(new StaticPlayer(world, new Vector2(map.center).add(map.dimensions.x*.3f, 0), Controllers.getControllers().get(0),this , atlas, 0, map.sensors.get(1).body));
			if(Controllers.getControllers().size>1)
			players.add(new StaticPlayer(world, new Vector2(map.center).sub(map.dimensions.x*.3f, 0), Controllers.getControllers().get(1), this , atlas, 1, map.sensors.get(0).body));		
		}
		
		
		//walker = new Walker(new Vector2(map.center), world, this);//.sub(0,map.dimensions.y/2.f)
		
		
		
		
		
	}

	private void genConfigFile(FileHandle configFile) {
		StringWriter writer = new StringWriter();
		XmlWriter xml = new XmlWriter(writer);
		try {
			xml.element("Codes").element("BUTTON_JUMP").attribute("id", "1").pop().element("BUTTON_GRENADE").attribute("id", "0").pop().element("AXIS_X").attribute("id", "0").pop().element("AXIS_Y").attribute("id", "1").pop().pop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		configFile.writeString(writer.toString(),false);
		
	}

	float timeCounter = 0.f;
	private float totalTime = 0.f;

	
	@Override
	public void render (float delta) {
		stateTime += getDeltaTime();
		totalTime += getDeltaTime();
		
		
		
		if(nextReset){
			map.wubSound.stop();
			if(blueWins){
				BlueWinsSprite.setSize(map.dimensions.x /2f, map.dimensions.x/2f*(.6f));
				BlueWinsSprite.setCenter((float)Math.cos(totalTime / 3f), (float) Math.sin(2*totalTime));
				draw();
				batch.begin();
				BlueWinsSprite.draw(batch);
				batch.end();
				
			} else{
				RedWinsSprite.setSize(map.dimensions.x /2f, map.dimensions.x/2f*(.6f));
				RedWinsSprite.setCenter((float)Math.cos(totalTime / 3f), (float) Math.sin(2*totalTime));
				draw();
				batch.begin();
				RedWinsSprite.draw(batch);
				batch.end();
							
			}
			
			
			resetTimer += Gdx.graphics.getDeltaTime();
			if(resetTimer > 8.f){
				resetTimer = 0.f;
				nextReset = false;
				map.nextReset = true;
				blueWins = false;
			}
		}else{
		
		for(PlayerClass p : players){
			p.update();
		}
		
		etester.manageDrops();
		map.update();
		
		if(map.slowMoTimer > 0){
			if(slowMusic.isPlaying()){
				
			}else{
				slowMusic.setPosition(music.getPosition() * (1.f/.8f) );
				music.pause();
				slowMusic.play();
			}
		}else{
			if(music.isPlaying()){
				
			}else{
				slowMusic.pause();
				music.setPosition(slowMusic.getPosition() * .8f);
				music.play();
			}
		}
		
		
		if(stateTime > simTime*timeSpeed){
			world.step(stateTime, 6, 2);
			stateTime = 0;
		}
		
		updateCamera(camera);
		
		
		
		//walker.update();
//		timeCounter += .1;
//		if(timeCounter < 20 && timeCounter - (int)timeCounter < .01){
//			etester.createRandomShape();
//		}
		
		//update objects
		
		draw();
		//debug.render(world, camera.combined);
		
//		Array<Body> bodies = new Array<Body>();
//		world.getBodies(bodies);
//		for(Body b: bodies){
//			System.out.println(b.getFixtureList().get(0));
//		}
		
		}

	}
	
	private void draw() {
		
		if(debugDrawMode){
			camera.update();
			sr.setProjectionMatrix(camera.combined);
			Gdx.gl.glClearColor(.6f, .3f, .3f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			sr.begin(ShapeType.Line);
			sr.setColor(1, 1, 1, .7f);
			

			//for(float theta = 0.f; theta < 2*Math.PI; theta += Math.PI / 24.f){		
			//	sr.line(new Vector2(temp.x,temp.y), new Vector2((float)(temp.x + 100.f*Math.cos(theta)),(float)( temp.y + 100.f*Math.sin(theta))));
			//}
			
			//etester.draw(batch, sr);
			//batch.draw(img, 0, 0);
			float bgScale = 2.f;
			float parralaxSpeed = 1.4f;
			sr.end();
			
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
					//batch.draw(starryBackground, bgScale*(-map.dimensions.x/2.f) + camera.position.x /parralaxSpeed, bgScale*(-map.dimensions.y/2.f) + camera.position.y / parralaxSpeed, bgScale* map.dimensions.x, bgScale*map.dimensions.y);

			//batch.draw(LevelScreen, -map.dimensions.x/2f, -map.dimensions.y/2f - .05f, map.dimensions.x, map.dimensions.y);
			for(PlayerClass p : players){
				p.draw(sr, batch);
			}
			map.draw(batch);
			batch.end();
			
			
			
		} else{
		
		camera.update();
		sr.setProjectionMatrix(camera.combined);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sr.begin(ShapeType.Line);
		sr.setColor(1, 1, 1, .7f);
		

		//for(float theta = 0.f; theta < 2*Math.PI; theta += Math.PI / 24.f){		
		//	sr.line(new Vector2(temp.x,temp.y), new Vector2((float)(temp.x + 100.f*Math.cos(theta)),(float)( temp.y + 100.f*Math.sin(theta))));
		//}
		
		//etester.draw(batch, sr);
		//batch.draw(img, 0, 0);
		float bgScale = 2.f;
		float parralaxSpeed = 1.4f;
		sr.end();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
				batch.draw(starryBackground, bgScale*(-map.dimensions.x/2.f) + camera.position.x /parralaxSpeed, bgScale*(-map.dimensions.y/2.f) + camera.position.y / parralaxSpeed, bgScale* map.dimensions.x, bgScale*map.dimensions.y);

		batch.draw(LevelScreen, -map.dimensions.x/2f, -map.dimensions.y/2f - .05f, map.dimensions.x, map.dimensions.y);
		for(PlayerClass p : players){
			p.draw(sr, batch);
		}
		map.draw(batch);
		batch.end();
		
		}
		
	}

	Array<Body> bodies = new Array<Body>();
	
	public void updateCamera(OrthographicCamera camera) {
		float maxY = (map.center.y + map.dimensions.y/2.f) + C_BUFFER;
		float maxX = (map.center.x + map.dimensions.x/2.f) + C_BUFFER;
		float minY = (map.center.y - map.dimensions.y/2.f) - C_BUFFER;
		float minX = (map.center.x - map.dimensions.x/2.f) - C_BUFFER;
		
		Body p1 = ((Player)(players.get(0))).body;
		Body p2 = ((Player)(players.get(1))).body;
		Body m = map.ballBody;
		
		bodies.add(p1);
		bodies.add(p2);
		bodies.add(m);
		
		float hx = Integer.MIN_VALUE, hy = Integer.MIN_VALUE;
		float lx = Integer.MAX_VALUE, ly = Integer.MAX_VALUE;
		for(Body b : bodies){
			float nx = b.getPosition().x + C_BUFFER;
			if( nx > hx){
				if(nx < maxX )
					hx = nx;
				else hx = maxX;
			}
			
			float mx = b.getPosition().x - C_BUFFER;
			if( mx < lx){
				if(mx > minX )
					lx = mx;
				else lx = minX;
			}
			
			float ny = b.getPosition().y + C_BUFFER;
			if( ny > hy){
				if(ny < maxY )
					hy = ny;
				else hy = maxY;
			}
			
			float my = b.getPosition().y - C_BUFFER;
			if( my < ly){
				if(my > minY )
					ly = my;
				else ly = minY;
			}
			
			
			
		}
		
		float cw = hx - lx;
		float ch = hy - ly;
		Vector2 cCenter = new Vector2((hx+lx)/2.f, (hy+ly)/2.f);
		
		
		float aspectRatio = camera.viewportWidth/ camera.viewportHeight;
		
		if(cw / aspectRatio < ch){
			cw = ch * aspectRatio;
		}else{
			ch = cw / aspectRatio;
		}
		
		if(cCenter.x + cw /2f > maxX){
			cCenter.x += maxX - (cCenter.x + cw/2f);
		}
		if(cCenter.x - cw /2f < minX){
			cCenter.x += minX - (cCenter.x - cw/2f);
		}
		if(cCenter.y + ch /2f > maxY){
			cCenter.y += maxY - (cCenter.y + ch/2f);
		}
		if(cCenter.y - ch /2f < minY){
			cCenter.y += minY - (cCenter.y - ch/2f);
		}
		
		camera.position.add((cCenter.x - camera.position.x)/CAM_SPEED, (cCenter.y - camera.position.y)/CAM_SPEED, 0);

		camera.viewportWidth += (cw - camera.viewportWidth)/CAM_SPEED;
		camera.viewportHeight += (ch - camera.viewportHeight)/CAM_SPEED;
		
		
		
		
		bodies.clear();
	}

	@Override
	public void resize(int width, int height){
		viewport.update(width, height);
		
	}
	
	public float getDeltaTime(){
		return Gdx.graphics.getDeltaTime() * timeSpeed;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public float getStateTime() {
		// TODO Auto-generated method stub
		return totalTime;
	}
}
