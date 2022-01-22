package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;


import static com.mygdx.game.utils.Constants.PPM;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;

	private final float scale = 2.0f;
	private OrthographicCamera camera;

	private Box2DDebugRenderer b2dr;
	private World world;
	private Body player;
	private Body platform;

	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		batch = new SpriteBatch();
		img = new Texture("PirateShip3Mast.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w / scale, h / scale);

		world = new World(new Vector2(0, 0f), false);
		b2dr = new Box2DDebugRenderer();

		player = createBox(8 , 10, 32, 32, false);
		platform = createBox(0 , 0, 128, 32, true);
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());  // deltaTime is time between a frame refresh
		ScreenUtils.clear(0, 0, 0, 1);

		b2dr.render(world, camera.combined.scl(PPM));

		batch.begin();
		batch.draw(img, player.getPosition().x * PPM - (img.getWidth() / scale), player.getPosition().y * PPM  - (img.getHeight() / scale));
		batch.end();
	}

	@Override
	public void resize(int width, int height){
		camera.setToOrtho(false, width / scale, height / scale);
	}
	
	@Override
	public void dispose () {
		world.dispose();
		b2dr.dispose();
		batch.dispose();
	}

	public void update (float delta){
		world.step(1/60f, 6, 2);

		inputUpdate(delta);
		cameraUpdate(delta);
		batch.setProjectionMatrix(camera.combined);
	}

	public void inputUpdate(float delta){
		int horizontalforce = 0;
		int verticalforce = 0;

		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			horizontalforce -= 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)){
			horizontalforce += 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)){
			verticalforce += 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)){
			verticalforce -= 1;
		}
		player.setLinearVelocity(horizontalforce * 5, verticalforce * 5);

	}

	public void cameraUpdate(float delta) {
		Vector3 position = camera.position;

		position.x = player.getPosition().x * PPM;
		position.y = player.getPosition().y * PPM;
		camera.position.set(position);

		camera.update();
	}

	public Body createBox(int x, int y, int width, int height, boolean isStatic) {
		Body pBody;
		BodyDef def = new BodyDef();

		if(isStatic){
			def.type = BodyDef.BodyType.StaticBody;
		}
		else{
			def.type = BodyDef.BodyType.DynamicBody;
		}
		def.position.set(x / PPM, y / PPM);
		def.fixedRotation = true;
		pBody = world.createBody(def);


		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / PPM,height / 2 / PPM);

		pBody.createFixture(shape, 1.0f);
		shape.dispose();

		return pBody;
	}
}
