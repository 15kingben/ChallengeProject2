package com.mygdx.PhysicsGame2;

import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class EnvironmentTester {

	World world;
	Array<Body> mapBodies = new Array<Body>();
	Array<Body> testBodies = new Array<Body>();
	Random r;
	Game1Screen game;
	Array<Vector2> points = new Array<Vector2>();
	float dropTime = 5.f;
	float dropX;
	int sinceExp = 0;
	boolean nextExp = false;
	boolean spawning = true;
	String mapName = "";
	
	public EnvironmentTester(World world, Game1Screen game) {
		this.world = world;
		r = new Random();
		this.game = game;
	}

	public void createRandomExplosion() {
		float x = (float) r.nextInt(35) + 1;
		float y = (float) r.nextInt(26) + 1;
		createExplosion(x, y, 100.f, 10);
	}

	public void createExplosion(float x, float y, float expForce, float blastRadius) {
		points.clear();
		// System.out.println(x + " " + y);
		game.temp = new Vector2(x, y);
		for (float theta = 0.f; theta < 2 * Math.PI; theta += Math.PI / 24.f) {

			RayCastCallBackExp callback = new RayCastCallBackExp();
			world.rayCast(callback, new Vector2(x, y), new Vector2(
					(float) (x + blastRadius * Math.cos(theta)),
					(float) (y + blastRadius * Math.sin(theta))));
			if (callback.closestFixture() == null)
				continue;
			
			Body body = callback.closestFixture().getBody();
			DrawObject d = game.BodyObjects.get(body);
			
			if(d != null && d.isExplosive)
				d.toBeDestroyed = true;
			
			Vector2 dir = new Vector2((float) (Math.cos(theta)),
					(float) (Math.sin(theta))).nor().scl(expForce);
			// bo0dy.applyLinearImpulse(dir.scl(1.f/(callback.closestPoint().sub(new
			// Vector2(x,y)).len2())), callback.closestPoint(), true);//remember
			
			// attenuation
			
			//const
			body.applyLinearImpulse(dir.scl(.09f), callback.closestPoint(), true);//remember attenuation

			//lin
			//body.applyLinearImpulse(dir.scl(.1f - (.1f/blastRadius) * (new Vector2(callback.closestPoint())).sub(new Vector2(x,y)).len()), callback.closestPoint(), true);//remember attenuation
			//exp
			//body.applyLinearImpulse(dir.scl((float) Math.exp(-(.1f)*((new Vector2(callback.closestPoint())).sub(new Vector2(x,y)).len()) - 2.f)), callback.closestPoint(), true);//remember attenuation
		}

	}

	public void createRandomShape() {
		// shape types: circle or rectangle
		// size r = .5-3
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(new Vector2(20, 10));
		def.allowSleep = false;

		testBodies.add(world.createBody(def));

		if (r.nextInt() % 2 == 0) {// circle
			CircleShape shape = new CircleShape();
			shape.setRadius((float) r.nextInt(3) + r.nextFloat() / 2.f + .5f);

			// Create a fixture definition to apply our shape to
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = 1f;
			fixtureDef.friction = .5f;
			fixtureDef.restitution = .2f; // Make it bounce a little bit

			testBodies.get(testBodies.size - 1).createFixture(fixtureDef);
			shape.dispose();

		} else {// rectangle

			PolygonShape shape = new PolygonShape();
			shape.setAsBox((float) r.nextInt(3) + r.nextFloat() / 2.f,
					(float) r.nextInt(3) + r.nextFloat() / 2.f);

			// Create a fixture definition to apply our shape to
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = 1.f;
			fixtureDef.friction = .1f;
			fixtureDef.restitution = .2f; // Make it bounce a little bit

			testBodies.get(testBodies.size - 1).createFixture(fixtureDef);
			shape.dispose();
		}

		testBodies.get(testBodies.size - 1).applyLinearImpulse(0, -10, 20, 10,
				true);

	}

	public Map gleedMapFromFile() {
		FileHandle handle = Gdx.files.internal(mapName);
		Map map = new Map(game);

		XmlReader xml = new XmlReader();
		Element element;
		try {
			element = xml.parse(handle);
			Array<Element> layers = element.getChildByName("Layers")
					.getChildrenByName("Layer");

			Vector2 scale = new Vector2(1.f, 1.f);

			for (Element e : layers) {
				if (e.get("Name").equals("Static")) {
					Element items = e.getChildByName("Items");
					Array<Element> shapes = items.getChildrenByName("Item");
					for (Element shape : shapes) {
						BodyDef worldDef = new BodyDef();
						worldDef.allowSleep = false;
						worldDef.type = BodyType.StaticBody;
						
						if(shape.getAttribute("xsi:type").equals("RectangleItem")){
							worldDef.position.set(new Vector2(shape.getChildByName(
									"Position").getFloat("X"), shape
									.getChildByName("Position").getFloat("Y")).scl(
									scale).add(
									new Vector2(shape.getFloat("Width") / 2.f,
											shape.getFloat("Height") / 2.f)
											.scl(scale)));
							PolygonShape boxDef = new PolygonShape();
							Vector2 v = new Vector2(shape.getFloat("Width"),
									shape.getFloat("Height")).scl(scale);
							boxDef.setAsBox(v.x / 2.f, Math.abs(v.y / 2.f));
							Body worldBody = world.createBody(worldDef);
	
							worldBody.createFixture(boxDef, 0.0f);
							DrawObject d = new DrawObject(worldBody, v, "Box", game);
							map.bodies.add(d);
							game.BodyObjects.put(worldBody, d);
							boxDef.dispose();
						}else if(shape.getAttribute("xsi:type").equals("CircleItem")){ 
							worldDef.position.set(new Vector2(shape.getChildByName(
									"Position").getFloat("X"), shape
									.getChildByName("Position").getFloat("Y")).scl(
									scale));
							CircleShape circDef = new CircleShape();
							Vector2 v = new Vector2(shape.getFloat("Radius")*2f,
									shape.getFloat("Radius")*2f).scl(scale);
							circDef.setRadius(v.x/2f);;
							Body worldBody = world.createBody(worldDef);
	
							worldBody.createFixture(circDef, 0.0f);
							DrawObject d = new DrawObject(worldBody, v, "Circle", game);
							map.bodies.add(d);
							game.BodyObjects.put(worldBody, d);
							circDef.dispose();
							
						
						}else if(shape.getAttribute("xsi:type").equals("PathItem")){
							worldDef.position.set(new Vector2(shape.getChildByName("Position").getFloat("X"), shape.getChildByName("Position").getFloat("X")).scl(scale));
							PolygonShape boxDef = new PolygonShape();
							Vector2[] vertices;
							
							Array<Element> verts = shape.getChildByName("LocalPoints").getChildrenByName("Vector2");
							vertices = new Vector2[verts.size];
							
							for(int i = 0; i < verts.size; i++){
								vertices[i] = new Vector2(verts.get(i).getFloat("X"), verts.get(i).getFloat("Y")).scl(scale);
							}
							
							
							
							
							
							Element f = shape.getChildByName("CustomProperties");
							
							Array<Element> props = f.getChildrenByName("Property");
							
							Vector2 pos = new Vector2(0,0);
							Vector2 dim = new Vector2(0,0);
							boolean flip = false;
							for(Element g : props){
								if(g.getAttribute("Name" , "").equals("DrawCenter"))
									pos = new Vector2(g.getChildByName("Vector2").getFloat("X"), g.getChildByName("Vector2").getFloat("Y")).scl(scale);
								else if(g.getAttribute("Name" , "").equals("DrawDimensions"))
									dim =  new Vector2(g.getChildByName("Vector2").getFloat("X"), g.getChildByName("Vector2").getFloat("Y")).scl(scale);	
								else if(g.getAttribute("Name" , "").equals("Flip"))
									flip = g.getBoolean("boolean");
							}
							
							dim.y = Math.abs(dim.y);
							
							worldDef.position.set(pos);
							
							
							for(Vector2 l : vertices){
								l.sub(new Vector2(dim).scl(.5f));
							}
							//MUST BE COUNTERCLOCKWISE
							boxDef.set(vertices);
							
							
							
							Body worldBody = world.createBody(worldDef);
							DrawObject d;
							worldBody.createFixture(boxDef, 0.0f);
							if(flip)
							 d = new DrawObject(worldBody, dim, "Slope1", game);
							else
								 d = new DrawObject(worldBody, dim, "Slope", game);

							map.bodies.add(d);
							game.BodyObjects.put(worldBody, d);
							boxDef.dispose();
						}
						
					}
				} else if (e.getAttribute("Name").equals("Dynamic")) {

					Element items = e.getChildByName("Items");
					Array<Element> shapes = items.getChildrenByName("Item");
					for (Element shape : shapes) {
						BodyDef worldDef = new BodyDef();
						worldDef.allowSleep = true;
						worldDef.type = BodyType.DynamicBody;
						worldDef.bullet = true;
						if(shape.getAttribute("xsi:type").equals("RectangleItem")){
							boolean isExplosive = false;
							
							Vector2 v = new Vector2(shape.getFloat("Width"), shape.getFloat("Height"));
							
							worldDef.position.set(new Vector2(shape.getChildByName(
									"Position").getFloat("X"), shape.getChildByName("Position").getFloat("Y")) .scl(scale) .add((new Vector2(v).scl(new Vector2(scale)).scl(.5f)) ));
							
							
							Element f = shape.getChildByName("CustomProperties");
							
							Array<Element> props = f.getChildrenByName("Property");

							for(Element g : props){
								if(g.getAttribute("Name" , "").equals("IsExplosive"))
									isExplosive = true;
							}
							
							
							
							
							
							
							
							
							// Create a circle shape and set its radius to 6
							PolygonShape box = new PolygonShape();
							Vector2 dimensions = v.scl(scale);
							box.setAsBox(dimensions.x/2.f, Math.abs(dimensions.y/2.f));
	
							// Create a fixture definition to apply our shape to
							FixtureDef fixtureDef = new FixtureDef();
							fixtureDef.shape = box;
							fixtureDef.density = .4f; 
							fixtureDef.friction = 0.4f;
							fixtureDef.restitution = .2f; // Make it bounce a little bit
							
							//System.out.println(dimensions + " " + v + " " + box.getVertexCount());
							
							Body worldBody = world.createBody(worldDef);
							worldBody.setLinearDamping(3.f);
							//worldBody.setAngularDamping(3.f);
							worldBody.createFixture(fixtureDef);
							DrawObject d;
							if(isExplosive){
								d = new DrawObject(worldBody, dimensions, "TNT", game);
								d.isExplosive = true;
							}else{
								d = new DrawObject(worldBody, dimensions, "Box", game);
							}
							
							
							map.bodies.add(d);
							game.BodyObjects.put( worldBody, d);

							
						}else if(shape.getAttribute("xsi:type").equals("CircleItem")){
							Vector2 v = new Vector2(shape.getFloat("Radius")*2.f, shape.getFloat("Radius")*2.f);
							
							worldDef.position.set(new Vector2(shape.getChildByName(
									"Position").getFloat("X"), shape.getChildByName("Position").getFloat("Y")) .scl(scale) );
							
							
							// Create a circle shape and set its radius to 6
							CircleShape circ = new CircleShape();
							Vector2 dimensions = v.scl(scale);
							v.y = Math.abs(v.y);
							circ.setRadius(v.y/2.f);;
	
							// Create a fixture definition to apply our shape to
							FixtureDef fixtureDef = new FixtureDef();
							fixtureDef.shape = circ;
							fixtureDef.density = .3f; 
							fixtureDef.friction = 0.4f;
							fixtureDef.restitution = .6f; // Make it bounce a little bit
							
							//System.out.println(dimensions + " " + v + " " + circ.getVertexCount());
							
							Body worldBody = world.createBody(worldDef);
							worldBody.createFixture(fixtureDef);
							
							
							Element f = shape.getChildByName("CustomProperties");
							DrawObject d;
							
							if(f.getChildCount() > 0){
								if(f.getChildByName("Property").getAttribute("Name").equals("BallBody")){
									worldBody.getFixtureList().get(0).setDensity(.9f);
									//worldBody.setLinearDamping(5.f);;
									worldBody.setGravityScale(.3f);
									map.ballBody = worldBody;
									d = new DrawObject(worldBody, dimensions, "Ball", game);
									d.body.setSleepingAllowed(false);
									map.bodies.add(d);
									game.BodyObjects.put(worldBody , d);
									continue;
								}
							}	
							
							d = new DrawObject(worldBody, dimensions, "Circle", game);
							map.bodies.add(d);
							game.BodyObjects.put(worldBody, d);
							
						}else{
							
								PolygonShape boxDef = new PolygonShape();
								Vector2[] vertices;
								
								Array<Element> verts = shape.getChildByName("LocalPoints").getChildrenByName("Vector2");
								vertices = new Vector2[verts.size];
								
								for(int i = 0; i < verts.size; i++){
									vertices[i] = new Vector2(verts.get(i).getFloat("X"), verts.get(i).getFloat("Y")).scl(scale);
								}
								
								
								//MUST BE COUNTERCLOCKWISE
								boxDef.set(vertices);;
								
								
								
								Element f = shape.getChildByName("CustomProperties");
								
								Vector2 pos = new Vector2(f.getChildByName("DrawCenter").getChildByName("Vector2").getFloat("X"), f.getChildByName("DrawCenter").getChildByName("Vector2").getFloat("Y")).scl(scale);
								Vector2 dim =  new Vector2(f.getChildByName("DrawDimensions").getChildByName("Vector2").getFloat("X"), f.getChildByName("DrawDimensions").getChildByName("Vector2").getFloat("Y")).scl(scale);
									
								worldDef.position.set(pos);
								
								
								// Create a fixture definition to apply our shape to
								FixtureDef fixtureDef = new FixtureDef();
								fixtureDef.shape = boxDef;
								fixtureDef.density = .4f; 
								fixtureDef.friction = 0.4f;
								fixtureDef.restitution = .2f; // Make it bounce a little bit
															
								Body worldBody = world.createBody(worldDef);
								worldBody.createFixture(fixtureDef);
								
								DrawObject d = new DrawObject(worldBody, dim, "Box", game);
								game.BodyObjects.put( worldBody, d);
								game.map.bodies.add(d);
						}
					}
				} else if (e.getAttribute("Name").equals("Center")) {
					Element ss = e.getChildByName("ScrollSpeed");
					map.center = new Vector2(ss.getFloat("X"), ss.getFloat("Y"))
							.scl(scale);
				} else if (e.getAttribute("Name").equals("Scale")) {
					Element ssp = e.getChildByName("ScrollSpeed");
					scale = new Vector2(ssp.getFloat("X"), ssp.getFloat("Y"));
				} else if (e.getAttribute("Name").equals("Dimensions")) {
					Element ss = e.getChildByName("ScrollSpeed");
					map.dimensions = new Vector2(new Vector2(ss.getFloat("X"),
							ss.getFloat("Y")).scl(scale));
					map.dimensions.y = Math.abs(map.dimensions.y);
				} else if (e.getAttribute("Name").equals("Start1")) {
					Element ss = e.getChildByName("ScrollSpeed");
					map.start1 = new Vector2(new Vector2(ss.getFloat("X"),
							ss.getFloat("Y")).scl(scale));
					//System.out.println(map.start1);

				}else if (e.getAttribute("Name").equals("Start2")) {
					Element ss = e.getChildByName("ScrollSpeed");
					map.start2 = new Vector2(new Vector2(ss.getFloat("X"),
							ss.getFloat("Y")).scl(scale));
					//System.out.println(map.start2);
				}else if (e.getAttribute("Name").equals("Sensor")) {
					
					//System.out.println("poop");
					Element items = e.getChildByName("Items");
					Array<Element> shapes = items.getChildrenByName("Item");
					for (Element shape : shapes) {
						BodyDef worldDef = new BodyDef();
						PolygonShape boxDef = new PolygonShape();
						worldDef.allowSleep = false;
						worldDef.type = BodyType.StaticBody;
						worldDef.position.set(new Vector2(shape.getChildByName(
								"Position").getFloat("X"), shape
								.getChildByName("Position").getFloat("Y")).scl(
								scale).add(
								new Vector2(shape.getFloat("Width") / 2.f,
										shape.getFloat("Height") / 2.f)
										.scl(scale)));
						Vector2 v = new Vector2(shape.getFloat("Width"),
								shape.getFloat("Height")).scl(scale);
						boxDef.setAsBox(v.x / 2.f, Math.abs(v.y / 2.f));
						Body worldBody = world.createBody(worldDef);

						worldBody.createFixture(boxDef, 0.0f);
						worldBody.getFixtureList().get(0).setSensor(true);
						map.sensors.add(new DrawObject(worldBody, v, "Box", game));
						boxDef.dispose();
					}
					//System.out.println("poop");
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;

	}

	public void mapFromFile() {
		FileHandle handle = Gdx.files.internal(mapName);
		XmlReader xml = new XmlReader();
		Element element;
		try {
			element = xml.parse(handle);
			Array<Element> polygons = element.getChildrenByName("polygon");
			float[] vertices = new float[8];
			BodyDef worldDef = new BodyDef();
			PolygonShape boxDef = new PolygonShape();
			worldDef.type = BodyType.StaticBody;
			for (Element p : polygons) {
				for (int i = 0; i < 4; i++) {
					vertices[2 * i] = p.getChild(i).getFloat("x");
					vertices[2 * i + 1] = p.getChild(i).getFloat("y");
				}
				boxDef.set(vertices);
				worldDef.position.set(p.getChildByName("position")
						.getFloat("x"),
						p.getChildByName("position").getFloat("y"));
				Body WorldBody = world.createBody(worldDef);
				mapBodies.add(WorldBody);
				WorldBody.createFixture(boxDef, 0.0f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void draw(SpriteBatch batch, ShapeRenderer sr) {
		// TODO Auto-generated method stub
		for (Vector2 v : points) {
			// System.out.println(v.x + " " + v.y);
			sr.box(v.x, v.y, 0, .2f, .2f, 0.f);
		}
	}

	public void manageDrops() {
		// function?
		if(spawning){
		dropTime -= game.getDeltaTime();
		if (dropTime < 0) {
			sinceExp++;
			if (sinceExp > 5) {
				if (true){//r.nextInt(7) == 0) {
					nextExp = true;
				}
			}
			dropTime = 20.f;
			
			//dropClump(.1f, .3f, 100);
			//dropShape(dropX, 1 , 1.5f);
			//dropX = (game.map.center.x + r.nextFloat() * game.map.dimensions.x - game.map.dimensions.x / 2.f) * .8f;
		}
		}
	}

	public void dropShape(float dropX, float minSize, float maxSize) {
		// shape types: circle or rectangle
		// size r = .5-3
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(new Vector2(dropX, game.map.center.y
				+ game.map.dimensions.y / 2 - 5.f));
		def.allowSleep = false;
		def.angularVelocity = r.nextFloat() - .5f;

		Body body = (world.createBody(def));
		Vector2 dim;
		String path;

		if (nextExp) {
			nextExp = false;
			PolygonShape shape = new PolygonShape();
			float x = .7f;
			float y = .7f;
			shape.setAsBox(x, y);

			// Create a fixture definition to apply our shape to
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = .1f;
			fixtureDef.friction = .8f;
			fixtureDef.restitution = .1f; // Make it bounce a little bit

			body.createFixture(fixtureDef);
			dim = new Vector2(x, y).scl(2.f);
			shape.dispose();
			path = "TNT";
		} else if (r.nextInt(5) == 5) {// circle
			CircleShape shape = new CircleShape();
			shape.setRadius(minSize  + r.nextFloat()*(maxSize - minSize));

			// Create a fixture definition to apply our shape to
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = .1f;
			fixtureDef.friction = .8f;
			fixtureDef.restitution = .1f; // Make it bounce a little bit

			body.createFixture(fixtureDef);
			dim = new Vector2(shape.getRadius() * 2, shape.getRadius() * 2);
			shape.dispose();
			path = "Circle";

		} else {// rectangle

			PolygonShape shape = new PolygonShape();
			float x = minSize  + r.nextFloat()*(maxSize - minSize);
			float y = minSize  + r.nextFloat()*(maxSize - minSize);
			shape.setAsBox(x, y);

			// Create a fixture definition to apply our shape to
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = .1f;
			fixtureDef.friction = .8f;
			fixtureDef.restitution = .1f; // Make it bounce a little bit

			body.createFixture(fixtureDef);
			dim = new Vector2(x, y).scl(2.f);
			shape.dispose();
			path = "Box";
		}
		DrawObject d = new DrawObject(body, dim, path, game);
		game.BodyObjects.put(body, d);

	}

	public void createWorldExplosion(float expForce, Vector2 location, float shakeTime, float sloTime, float pers, float blastRadius) {

		createExplosion(location.x, location.y, expForce, blastRadius);
		game.map.cameraShake(shakeTime, pers);
		game.timeSpeed = .2f;
		game.map.slowMoTimer = sloTime;

	}
	
	public void dropClump(float minSize, float maxSize, float number){
		for(int i = 0; i < number; i++){
			
			dropShape((game.map.center.x + r.nextFloat() * game.map.dimensions.x - game.map.dimensions.x / 2.f) * .8f, minSize, maxSize);
			
			
			
		}
		
		
	}
}
