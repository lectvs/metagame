package platform;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
//import org.newdawn.slick.UnicodeFont;
//import org.newdawn.slick.font.effects.ColorEffect;
//import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.opengl.GL11.*;

public class Main {

    // The size of the game window
    public static int gameWidth = 720;
    public static int gameHeight = 405;
    public static final int GAME_SCALE = 1; //scaling

    public static int windowWidth = gameWidth * GAME_SCALE;      
    public static int windowHeight = gameHeight * GAME_SCALE; 

    public static FBO gameFBO;
    public static Game game;
    public static Screen screen;

    // Time
    public static float delta;
    public static long lastTime;

    // Keybinds
    public static boolean keyLeftDown;
    public static boolean keyRightDown, keyRightLast;
    public static boolean keyJumpDown, keyJumpLast;
    public static boolean keyDashDown, keyDashLast;
    public static boolean keyAttackDown, keyAttackLast;
    public static boolean keyEnterDown;

    public void start() {
        delta = 0;
        lastTime = 0;

        initGL(windowWidth, windowHeight);

        Stats.loadStats();

        gameFBO = new FBO(gameWidth, gameHeight);
        game.init();
        screen = game; // Sets the current screen (game world) to our game class

        while (true) {
            updateTimer();
            updateInput();
            screen.update(); // Updates the current screen (Game, MainMenu, PauseMenu, etc)
            SequenceTimer.update(); // Updates the sequence timer, a small utility I made for dealing with timed events

            gameFBO.setFramebuffer();
            screen.render(); // Renders the current screen
            FBO.unset();
            glColor4f(1, 1, 1, 1);
            gameFBO.render(0, 0, windowWidth, windowHeight); // Renders the game at 2x scaling

            // LWJGL stuff
            Display.update();
            Display.sync(100);

            if (Display.isCloseRequested()) {
                Display.destroy();
                System.exit(0);
            }
        }
    }


    // Initialize OpenGL. Probably won't be editing this very much
    private void initGL(int width, int height) {
        try {
            setDisplayMode(width, height, false);
            Display.create();
            Display.setVSyncEnabled(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Enable alpha blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glViewport(0, 0, windowWidth, windowHeight);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, windowWidth, windowHeight, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Initialize FBO
        if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            System.out.println("FBO not supported!!!");
            System.exit(0);
        }
    }

//    public void loadFont() {
//        try {
//            InputStream inputStream	= ResourceLoader.getResourceAsStream("platform/res/visitor1.ttf");
//
//            Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
//            font30 = new UnicodeFont(awtFont.deriveFont(0, 30f));
//            font12 = new UnicodeFont(awtFont.deriveFont(0, 12f));
//            font30.addAsciiGlyphs();
//            font12.addAsciiGlyphs();
//            ColorEffect e = new ColorEffect();
//            e.setColor(Color.white);
//            font30.getEffects().add(e);
//            font12.getEffects().add(e);
//            try {
//                font30.loadGlyphs();
//                font12.loadGlyphs();
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    // Update the current game time
    private void updateTimer() {
        delta = (System.currentTimeMillis() - lastTime) / 1000f;
        if (delta > 1) {
            delta = 0;
        }
        lastTime = System.currentTimeMillis();
    }

    // Deal with input, for easy-to-use, universal input detection in the game (with customizable keybinds)
    private void updateInput() {
        keyLeftDown = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_Q);

        keyRightLast = keyRightDown;
        keyRightDown = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
        keyEnterDown = Keyboard.isKeyDown(Keyboard.KEY_RETURN);

        keyJumpLast = keyJumpDown;
        keyJumpDown = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        keyDashLast = keyDashDown;
        keyDashDown = Keyboard.isKeyDown(Keyboard.KEY_Z);
        keyAttackLast = keyAttackDown;
        keyAttackDown = Keyboard.isKeyDown(Keyboard.KEY_X);

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
            System.exit(0);
    }

    // More OpenGL initialization
    public void setDisplayMode(int width, int height, boolean fullscreen) {

        if ((Display.getDisplayMode().getWidth() == width) &&
                (Display.getDisplayMode().getHeight() == height) &&
                (Display.isFullscreen() == fullscreen)) {
            return;
        }

        try {
            DisplayMode targetDisplayMode = null;
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                for (int i=0;i<modes.length;i++) {
                    DisplayMode current = modes[i];

                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                                (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width,height);
            }
            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
                return;
            }
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
            Display.setTitle("TEMPLATE GAME");
        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
        }
    }

    // The infamous main function
    public static void main(String[] args) {
        Main m = new Main();
        game = new Game();
        m.start();
    }
}
