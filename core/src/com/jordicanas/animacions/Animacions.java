package com.jordicanas.animacions;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;

public class Animacions extends ApplicationAdapter {
	// Constant rows and columns of the sprite sheet
	private static final int FRAME_COLS = 6, FRAME_ROWS = 5;

	// Objects used
	Texture walkSheet;
	Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)

	//Sprite afegit
	Texture walkPrinceOfPersia;
	Animation<TextureRegion> princeOfPersia;
	float sX = 0;
	float sY = 0;
	int speed = 2;

	//Common
	SpriteBatch spriteBatch;

	// A variable for tracking elapsed time for the animation
	float stateTime;
	float wsTimer;

	// PR33 Websockets
	WebSocket socket;
	String address = "localhost";
	int port = 3000;



	@Override
	public void create() {

		//Initialize sprite pos
		sX = Gdx.graphics.getWidth()/2;
		sY = Gdx.graphics.getHeight()/2;

		// Load the sprite sheet as a Texture
		//walkSheet = new Texture(Gdx.files.internal("animation_sheet.png"));
		walkPrinceOfPersia = new Texture(Gdx.files.internal("PrinceOfPersia_Walking-transparent.png"));

		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		/*
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);
		 */
		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.

		TextureRegion animationFrames[] = new TextureRegion[8];
		animationFrames[0] = new TextureRegion(walkPrinceOfPersia,216,0,239-216,53);
		animationFrames[1] = new TextureRegion(walkPrinceOfPersia,247,0,281-247,53);
		animationFrames[2] = new TextureRegion(walkPrinceOfPersia,287,0,316-287,53);
		animationFrames[3] = new TextureRegion(walkPrinceOfPersia,327,0,353-327,53);
		animationFrames[4] = new TextureRegion(walkPrinceOfPersia,359,0,385-359,53);
		animationFrames[5] = new TextureRegion(walkPrinceOfPersia,392,0,427-392,53);
		animationFrames[6] = new TextureRegion(walkPrinceOfPersia,439,0,467-439,53);
		animationFrames[7] = new TextureRegion(walkPrinceOfPersia,480,0,500-480,53);

		// Initialize the Animation with the frame interval and array of frames
		//walkAnimation = new Animation<TextureRegion>(0.025f, walkFrames);
		princeOfPersia = new Animation<TextureRegion>(0.1f,animationFrames);

		// Instantiate a SpriteBatch for drawing and reset the elapsed animation time to 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;

		// PR33 Initialize websockets
		socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(address, port));
		socket.setSendGracefully(false);
		socket.addListener((WebSocketListener) new MyWSListener());
		socket.connect();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		socket.send("Enviar dades");

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		//PR33 Send position to server thru Websockets
		if (stateTime - wsTimer >= 1){
			socket.send("Posició: "+ sX + " - " + sY);
			wsTimer = stateTime;
		}

		// Get touchscreen input & set movement
		if(Gdx.input.isTouched()){
			if (Gdx.input.getX() < Gdx.graphics.getWidth()/3){
				sX -= speed;
				//Left
				if (Gdx.input.getY() < Gdx.graphics.getHeight()/3){
					//Up
					sY += speed;
				} else if (Gdx.input.getY() < Gdx.graphics.getHeight()/3*2){
					//None
				} else {
					//Down
					sY -= speed;
				}

			} else if (Gdx.input.getX() < Gdx.graphics.getWidth()/3*2) {
				//None
				if (Gdx.input.getY() < Gdx.graphics.getHeight()/3){
					//Up
					sY += speed;
				} else if (Gdx.input.getY() < Gdx.graphics.getHeight()/3*2){
					//None
				} else {
					//Down
					sY -= speed;
				}

			} else {
				sX += speed;
				//Right
				if (Gdx.input.getY() < Gdx.graphics.getHeight()/3){
					//Up
					sY += speed;
				} else if (Gdx.input.getY() < Gdx.graphics.getHeight()/3*2){
					//None
				} else {
					//Down
					sY -= speed;
				}

			}
		}

		// Get current frame of animation for the current stateTime
		//TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		TextureRegion spriteFrame = princeOfPersia.getKeyFrame(stateTime,true);
		spriteBatch.begin();
		//spriteBatch.draw(currentFrame, 50, 50, 500, 500); // Draw current frame at (50, 50)
		spriteBatch.draw(spriteFrame, sX, sY);
		spriteBatch.end();
	}

	@Override
	public void dispose() { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
		walkSheet.dispose();
	}

	class MyWSListener implements WebSocketListener {

		@Override
		public boolean onOpen(WebSocket webSocket) {
			System.out.println("Opening...");
			return false;
		}

		@Override
		public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
			System.out.println("Closing...");
			return false;
		}

		@Override
		public boolean onMessage(WebSocket webSocket, String packet) {
			System.out.println("Message:");
			return false;
		}

		@Override
		public boolean onMessage(WebSocket webSocket, byte[] packet) {
			System.out.println("Message:");
			return false;
		}

		@Override
		public boolean onError(WebSocket webSocket, Throwable error) {
			System.out.println("ERROR:"+error.toString());
			return false;
		}
	}

}
