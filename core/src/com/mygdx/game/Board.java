package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

public class Board {
    private Texture darkSqr = new Texture("DarkBoard.png");
    private Texture lightSqr = new Texture("LightBoard.png");
    final int SQUARE_LENGTH = 32; // Length in pixels of one board square

    public Board() {

    }

    public void draw(SpriteBatch batch) {
        // Draw dark squares
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                batch.draw(darkSqr, Constants.HORIZ_OFFSET + 2 * SQUARE_LENGTH * i, Constants.VERT_OFFSET + 2 * SQUARE_LENGTH * j);
                batch.draw(darkSqr, Constants.HORIZ_OFFSET + SQUARE_LENGTH + 2 * SQUARE_LENGTH * i, Constants.VERT_OFFSET + SQUARE_LENGTH + j * 2 * SQUARE_LENGTH);
            }
        }
        // Draw light squares
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                batch.draw(lightSqr, Constants.HORIZ_OFFSET + SQUARE_LENGTH + 2 * SQUARE_LENGTH * i, Constants.VERT_OFFSET + 2 * SQUARE_LENGTH * j);
                batch.draw(lightSqr, Constants.HORIZ_OFFSET + 2 * SQUARE_LENGTH * i, Constants.VERT_OFFSET + SQUARE_LENGTH + j * 2 * SQUARE_LENGTH);
            }
        }
    }
}
