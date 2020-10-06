package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Checker {
    Texture color;
    float x, y;

    public Checker(Boolean dark, float x, float y) {
        this.color = new Texture(dark ? "darkPiece.png" : "LightPiece.png");
        color.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.x = x;
        this.y = y;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(color, x, y);
    }

    public void movePiece(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // This function returns an array of floats in the form of {LEFTMOST X COORD, RIGHTMOST X COORD, TOP MOST Y COORD, BOTTOM MOST Y COORD}
    // It is used to determine which piece is being selected, if the mouse is within this pieces range, this piece is selected
    public float[] getPieceCoordRange() {
        float[] range = {this.x, this.x + 32, this.y, this.y + 32};
        return range;
    }

    public float getX() { return this.x; }

    public float getY() { return this.y; }
}
