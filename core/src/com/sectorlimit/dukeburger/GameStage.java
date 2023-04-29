package com.sectorlimit.dukeburger;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameStage extends Stage {

    public GameStage() {
    	super(new StretchViewport(320, 180));
    }

    public void resize(int width, int height) {
    	getViewport().update(width, height, true);
    }

}
