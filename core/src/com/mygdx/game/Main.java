package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/* TODO BOARD
TODO: add kings: DONE
TODO: Allow kings to jump backwards
TODO: add multi-jump
TODO: allow player to reselect piece to move: DONE
TODO: sophisticate init checkers
TODO: Comment your code man... Seriously
 */

public class Main extends ApplicationAdapter {
	SpriteBatch batch; // Used for rendering textures
	Viewport viewport; // Handles rescaling of windows
	Camera camera; // Handles rescaling of windows
	Board board; // The board lol
	Array<Checker> lightCheckers, darkCheckers; // Arrays of checkers for dark and light team
	private boolean checkerFound = false, lightTurn = true; // checkerFound, determines if a checker was clicked on; lightTurn, who's turn is it
	private int numDarkRemoved = 0, numLightRemoved = 0;
	float x = 0, y = 0; // score
	Checker tempChecker = null; // checker being manipulated during game-play
	boolean kingPiece = false;
	private int kingFactor = 1;

	// Very basic function, only runs once per compile
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, camera);
		board = new Board();
		lightCheckers = new Array<Checker>();
		darkCheckers = new Array<Checker>();
		initCheckers();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.x = Constants.SCREEN_WIDTH / 2;
		camera.position.y = Constants.SCREEN_HEIGHT / 2;
		super.resize(width, height);
	}

	public void render () {
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		board.draw(batch);
		for(Checker i : lightCheckers) { i.draw(batch); }
		for(Checker i : darkCheckers) { i.draw(batch); }
		selectChecker();
		moveChecker();
		batch.end();
	}

	public void dispose () {
		batch.dispose();
	}

	// Don't look at this it's UGLY. DON'T LOOK
	private void initCheckers() {
		// Init light pieces
		for(int i = 0; i < 4; i++) {
			Checker checker1 = new Checker(false, Constants.HORIZ_OFFSET + 2 * Constants.SQUARE_WIDTH * i, Constants.VERT_OFFSET);
			Checker checker3 = new Checker(false, Constants.HORIZ_OFFSET + 2 * Constants.SQUARE_WIDTH * i, Constants.VERT_OFFSET + 2 * Constants.SQUARE_WIDTH);
			Checker checker2 = new Checker( false, Constants.HORIZ_OFFSET + Constants.SQUARE_WIDTH + 2 * Constants.SQUARE_WIDTH * i, Constants.VERT_OFFSET + Constants.SQUARE_WIDTH);
			lightCheckers.add(checker1);
			lightCheckers.add(checker2);
			lightCheckers.add(checker3);
		}

		// Init dark pieces
		for(int i = 0; i < 4; i++) {
			Checker checker1 = new Checker(true, Constants.HORIZ_OFFSET + Constants.SQUARE_WIDTH + 2 * Constants.SQUARE_WIDTH * i, 5 * Constants.SQUARE_WIDTH + Constants.VERT_OFFSET);
			Checker checker3 = new Checker(true, Constants.HORIZ_OFFSET + Constants.SQUARE_WIDTH + 2 * Constants.SQUARE_WIDTH * i, Constants.VERT_OFFSET + 7 * Constants.SQUARE_WIDTH);
			Checker checker2 = new Checker( true, Constants.HORIZ_OFFSET + 2 * Constants.SQUARE_WIDTH * i, Constants.VERT_OFFSET + 6 * Constants.SQUARE_WIDTH);
			darkCheckers.add(checker1);
			darkCheckers.add(checker2);
			darkCheckers.add(checker3);
		}
	}

	private void selectChecker() {
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if(touchPos.x > Constants.HORIZ_OFFSET) { // This disallows players using pieces that are not on the board anymore
				x = touchPos.x;
				y = touchPos.y;
			}
		}

		if(x != 0) {
			for (Checker i : lightTurn ? lightCheckers : darkCheckers) {
				float[] range = i.getPieceCoordRange();
				if (x > range[0] && x < range[1] && y > range[2] && y < range[3]) {
					tempChecker = i;
					checkerFound = true;
					if (tempChecker.isKing())
						kingPiece = true;
				}
			}
		}
	}

	private void moveChecker() {
		if(kingPiece)
			if(Gdx.input.isKeyPressed(Input.Keys.UP))
				kingFactor = 1;
			else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				kingFactor = -1;
				System.out.println("KingFactor set");
			}


		if(checkerFound) {
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				if(tempChecker != null) {
					if(checkValid(false)) {
						checkKing();
						tempChecker.movePiece(lightTurn ? tempChecker.getX() - 32 : tempChecker.getX() + 32, lightTurn ? tempChecker.getY() + (kingFactor * 32) : tempChecker.getY() - (kingFactor * 32));
						System.out.println("actual y: " + tempChecker.getY());
						checkJump(false);
						checkerFound = false;
						kingFactor = 1;
						lightTurn = !lightTurn;
						camera.rotate(180, 0, 0, -1);
					}
				}
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				if(tempChecker != null) {
					if(checkValid(true)) {
						checkKing();
						tempChecker.movePiece(lightTurn ? tempChecker.getX() + 32 : tempChecker.getX() - 32, lightTurn ? tempChecker.getY() + (kingFactor * 32) : tempChecker.getY() - (kingFactor * 32));
						checkJump(true);
						checkerFound = false;
						kingFactor = 1;
						lightTurn = !lightTurn;
						camera.rotate(180, 0, 0, -1);
					}
				}
			}
		}
	}

	// checkJump handles jumping over pieces. Jumps over a piece if possible
	private void checkJump(boolean movedRight) {
		if(lightTurn) {
			// Check right or left based on bool. if a piece is there, move left/right again and remove that piece
			for(Checker i : darkCheckers) {
				if(i.getX() == tempChecker.getX() && i.getY() == tempChecker.getY()) {
					numDarkRemoved++;
					i.movePiece(Constants.HORIZ_OFFSET - 2 - Constants.SQUARE_WIDTH * numDarkRemoved, Constants.VERT_OFFSET + Constants.SQUARE_WIDTH * 7);
					tempChecker.movePiece(movedRight ? tempChecker.getX() + 32 : tempChecker.getX() - 32, tempChecker.getY() + 32);
					tempChecker = null;
					break;
				}
			}
		}
		else {
			for(Checker i : lightCheckers) {
				if(i.getX() == tempChecker.getX() && i.getY() == tempChecker.getY()) {
					numLightRemoved++;
					i.movePiece(Constants.HORIZ_OFFSET - 2 - Constants.SQUARE_WIDTH * numLightRemoved, Constants.VERT_OFFSET);
					tempChecker.movePiece(movedRight ? tempChecker.getX() - 32 : tempChecker.getX() + 32, tempChecker.getY() - 32);
					tempChecker = null;
					break;
				}
			}
		}
	}

	// Checks if a move would be valid: if a piece is at the diagonal of movement, check if a jump is possible. if possible, do move, else, cancel move
	private boolean checkValid(boolean movedRight) {
		if(checkMoveBounds(movedRight)) {
			if(lightTurn) {
				for(Checker i : darkCheckers) {
					if(movedRight ? tempChecker.getX() + 32 == i.getX() && tempChecker.getY() + 32 == i.getY() : tempChecker.getX() - 32 == i.getX() && tempChecker.getY() + 32 == i.getY()) {
						for(Checker j : darkCheckers) {
							if(movedRight ? tempChecker.getX() + 64 == j.getX() && tempChecker.getY() + 64 == j.getY() : tempChecker.getX() - 64 == j.getX() && tempChecker.getY() + 64 == j.getY()) {
								checkerFound = false;
								System.out.println("This is the problem");
								return false; // Jump required and not found
							}
						}
						return true; // Jump required and found
					}
				}
				return true; // Default validity, no jump required
			}
			else {
				for(Checker i : lightCheckers) {
					if(movedRight ? tempChecker.getX() - 32 == i.getX() && tempChecker.getY() - 32 == i.getY() : tempChecker.getX() + 32 == i.getX() && tempChecker.getY() - 32 == i.getY()) {
						for(Checker j : lightCheckers) {
							if(movedRight ? tempChecker.getX() - 64 == j.getX() && tempChecker.getY() - 64 == j.getY() : tempChecker.getX() + 64 == j.getX() && tempChecker.getY() - 64 == j.getY()) {
								checkerFound = false;
								return false; // Jump required and not found
							}
						}
						return true; // Jump required and found
					}
				}
				return true; // Default validity, no jump required
			}
		}
		System.out.println("Nah this the problem");
		checkerFound = false;
		return false;
	}

	// Helper method for checkValid, neaten things up a bit
	private boolean checkMoveBounds(boolean movedRight) {
		if(lightTurn && !kingPiece) {
			if(tempChecker.getY() + 32 == Constants.VERT_OFFSET + Constants.SQUARE_WIDTH * 8) {
				return false; // left board in y direction
			}
			else if (movedRight ? tempChecker.getX() + 32 == Constants.HORIZ_OFFSET + 32 * 8 : tempChecker.getX() - 32 == Constants.HORIZ_OFFSET - 32) {
				return false; // left board in +- x direction
			}
		}
		else if(!kingPiece){
			if(tempChecker.getY() - 32 == Constants.VERT_OFFSET - Constants.SQUARE_WIDTH) {
				return false; // left board in y direction
			}
			else if(movedRight ? tempChecker.getX() - 32 == Constants.HORIZ_OFFSET - 32 : tempChecker.getX() + 32 == Constants.HORIZ_OFFSET + 32 * 8) {
				return false; // left board in +- x direction
			}
		}

		return true;
	}

	// Helper method for setting a piece to a king
	private void checkKing() {
		if(lightTurn ? tempChecker.getY() + 32 == Constants.VERT_OFFSET + Constants.SQUARE_WIDTH * 7 : tempChecker.getY() - 32 == Constants.VERT_OFFSET)
			tempChecker.setKing(true);
	}
}
