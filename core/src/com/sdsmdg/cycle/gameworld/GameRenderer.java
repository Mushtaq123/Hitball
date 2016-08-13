package com.sdsmdg.cycle.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.BufferUtils;
import com.sdsmdg.cycle.chelpers.AssetLoader;
import com.sdsmdg.cycle.objects.Ball;
import com.sdsmdg.cycle.objects.Bat;

import java.nio.IntBuffer;

public class GameRenderer {

    private GameWorld myWorld;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font40, font80;

    private SpriteBatch batcher;

    private Bat bat;
    private Ball ball1;

    private int screenWidth, screenHeight;

    public static Texture texture;
    GlyphLayout glyphLayout;

    public GameRenderer(GameWorld world, int screenWidth, int screenHeight) {
        myWorld = world;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        glyphLayout = new GlyphLayout();

        bat = world.getBat();
        ball1 = world.getBall(0);

        cam = new OrthographicCamera();
        cam.setToOrtho(true, screenWidth, screenHeight);

        batcher = new SpriteBatch();
        batcher.setProjectionMatrix(cam.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        initAssets();
        initGameObjects();

        //Check max size of image that can be used
        IntBuffer buf = BufferUtils.newIntBuffer(16);
        Gdx.gl.glGetIntegerv(Gdx.gl.GL_MAX_TEXTURE_SIZE, buf);
        int maxSize = buf.get();
        Gdx.app.log("GL", "Max openGL texture size : " + String.valueOf(maxSize));
    }

    private void initGameObjects() {
        //the parameter true ensures that text is displayed not flipped
        font40 = AssetLoader.font40;
        font80 = AssetLoader.font80;
    }


    private void initAssets() {
    }

    public void render(float runTime) {

        // Fill the entire screen with black, to prevent potential flickering.
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batcher.begin();

        //draw background
        batcher.draw(AssetLoader.backgroundRegion, 0, 0,
                screenWidth, screenHeight);

        //Draw clouds
        batcher.draw(AssetLoader.cloudRegion, screenWidth / 3, screenHeight / 5,
                0, 0,
                screenWidth / 6, screenWidth / 8,
                1, 1,
                0);

        batcher.draw(AssetLoader.cloudRegion, screenWidth * 3 / 4, screenHeight / 2,
                0, 0,
                screenWidth / 6, screenWidth / 8,
                1, 1,
                0);

        if (myWorld.isRunning()) {
            //Draw score while running
            String text = String.valueOf(myWorld.getScore());
            glyphLayout.setText(font80, text);
            float w = glyphLayout.width;
            float h = glyphLayout.height;
            font80.draw(batcher, text, (screenWidth - w) / 2, (screenHeight - h) / 2);
        }

        //Draw bat
        batcher.draw(AssetLoader.batRegion, bat.getPosition().x, bat.getPosition().y,
                bat.getOriginX() - bat.getPosition().x, bat.getOriginY() - bat.getPosition().y,
                bat.getWidth(), bat.getHeight(),
                1, 1,
                bat.getRotation());

        //Draw ball
        batcher.draw(AssetLoader.ballRegion, ball1.getPosition().x - ball1.getRadius(), ball1.getPosition().y - ball1.getRadius(),
                ball1.getRadius(), ball1.getRadius(),
                ball1.getRadius() * 2, ball1.getRadius() * 2,
                1, 1,
                ball1.getRotation());

        if (myWorld.isReady()) {
            String text = "Click here to play";
            glyphLayout.setText(font40, text);
            float w = glyphLayout.width;
            font40.draw(batcher, text, (screenWidth - w) / 2, screenHeight / 2);
        } else if (myWorld.isOver()) {
            String text = "Best Score\n" + String.valueOf(myWorld.getHighScore()) + "\nScore\n" + String.valueOf(myWorld.getScore());
            glyphLayout.setText(font40, text);
            float w = glyphLayout.width;
            float h = glyphLayout.height;
            font40.draw(batcher, text, (screenWidth - w) / 2, (screenHeight - h) / 2);

        }
        batcher.end();

    }
}
