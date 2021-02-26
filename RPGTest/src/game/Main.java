package game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import assets.Assets;
import gui.FreeformPrompt;
import gui.GamePrompt;
import gui.Gui;
import io.Timer;
import io.Window;
import menu.Inventory;
import menu.MainMenu;
import menu.SaveMenu;
import rendering.Camera;
import rendering.DrawString;
import rendering.Shader;
import rendering.Transition;
import world.TileRenderer;
import world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.*;
import java.util.Scanner;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;

// I may use processing terms to get more used to the idea of how everything in LWJGL works
// GLFW window terms refreshing site - https://www.glfw.org/docs/3.3/window_guide.html
// Thread stack - https://docs.oracle.com/cd/E13150_01/jrockit_jvm/jrockit/geninfo/diagnos/thread_basics.html
// Event Polling - http://www.jguru.com/faq/view.jsp?EID=267203
// OpenGl - https://www.glprogramming.com/red/index.html
// VBO - passing some work to graphics card to make things smoother
// Indices - pointer to vertices, more memory efficient

public class Main {
	private Window window;

//-------------------------------------------------------------------------------------------------------------------------
	public void run() {
		System.out.println("LWJGL " + Version.getVersion());

		init();// setting
		loop();// draw

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window.getWindow());
		glfwDestroyWindow(window.getWindow());

		// Stop GLFW and free the error callback
		Assets.deleteAssets();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

//-------------------------------------------------------------------------------------------------------------------------
	private void init() {
		Window.setCallbacks();
		// Initialize GLFW. most GLFW functions will NOT work if not initialized beforehand
		if(!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints(); // Window hints-already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Window is hidden after creating it
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // Window is resizable

		// Creating the window
		window = new Window();
		window.setSize(1800, 1000);
		window.setFullScreen(false);
		window.createWindow("Anime Pokemon");

		// Getting thread stack and push a new frame
		try(MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			// Get window size passing glfwCreateWindow
			glfwGetWindowSize(window.getWindow(), pWidth, pHeight);
		} // Stack frame pops automatically here

		// Enable v-sync - go look it up if ur not a gamer
		GLFW.glfwSwapInterval(1);
	}

//-------------------------------------------------------------------------------------------------------------------------
	private void loop() {
		// Critical for LWJGL--creates context
		GL.createCapabilities();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		// creating classes and others
		int gameState = 0;
		int gameTrigger = 0;
		boolean gameLoaded = false;
		Camera camera = new Camera(window.getWidth(), window.getHeight());
		Assets.initAssets();
		TileRenderer tiles = new TileRenderer();
		Shader shader = new Shader("shader");
		World world = new World();
		Transition transition = new Transition();
		Gui gui = new Gui(window);
		MainMenu mainMenu = new MainMenu(window, "gui/background/0.png");
		mainMenu.addButton((float)(-window.getWidth()*0.17), (float)(-window.getHeight()*0.33), 100, 40);
		mainMenu.addButton((float)(window.getWidth()*0.17), (float)(-window.getHeight()*0.33), 100, 40);
		GamePrompt exitGame = new GamePrompt("Exit Game?", "Yes", "No", window, shader);
		SaveMenu saveMenu = new SaveMenu(window, "gui/background/1.png", "Start File");
		saveMenu.addSaves();
		GamePrompt overrideSave = new GamePrompt("Override Save?", "Yes", "No", window, shader);
		GamePrompt loadQuestion = new GamePrompt("Load File?", "Yes", "No", window, shader);
		FreeformPrompt chooseChara = new FreeformPrompt("Choose your Character", "gui/background/prompt_background_2.png", window, shader);
		chooseChara.addCharaChoices(window);
		Inventory inventory = new Inventory(window, "gui/background/pokeball.png");
		double frameCap = 1.0/60.0; // seconds/frames in that second max - ex: 1/60 = 60 frames per second
		double frameTime = 0;
		int frames = 0;
		double time = Timer.getTime();
		double unprocessed = 0;

		// Background
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Running the rendering loop until user closes window or presses esc
		while(!window.shouldClose()) {
			boolean canRender = false;
			double time2 = Timer.getTime();
			double passed = time2-time;
			unprocessed += passed; // time game hasn't been processed yet ex:dragging window
			frameTime += passed;
			time = time2; // prevent the game from going exponetially faster

			switch(gameState) {
				case 0:// Main Menu
					while(unprocessed>=frameCap) {
						if(window.hasResized()) {
							camera.setProjection(window.getWidth(), window.getHeight());
							mainMenu.resizeCamera(window);
							saveMenu.resizeCamera(window);
							inventory.resizeCamera(window);
							glViewport(0, 0, window.getWidth(), window.getHeight());
						}
						canRender = true;
						unprocessed -= frameCap;
						gameTrigger = mainMenu.update(window, gameTrigger, exitGame, transition);
						transition.update();
						switch(gameTrigger) {
							case 0:
								break;
							case -1:
								if(transition.getDoThing()) {
									// exit game
									glfwSetWindowShouldClose(window.getWindow(), true);
								}
								break;
							case 1:
								if(transition.getDoThing()) {
									// Start Button Effect
									gameState = 1;
									saveMenu.setPrompt(new DrawString("Start File..."));
									transition.setDoThing(false);
									gameTrigger = 0;
								}
								break;
							case 2:
								if(transition.getDoThing()) {
									// Load Button Effect
									gameState = 2;
									saveMenu.setPrompt(new DrawString("Load File..."));
									transition.setDoThing(false);
									gameTrigger = 0;
								}
								break;
						}
						window.update();
						if(frameTime>=1) {
							frameTime = 0;
							System.out.println("FPS: " + frames);
							frames = 0;
						}
					}

					if(canRender) {
						// rendering
						// Our context, clearing it sets the pixels to black-(clears to different color if glClearColor is
						// used)
						glClear(GL_COLOR_BUFFER_BIT);
						mainMenu.render(window);
						if(mainMenu.isPromptExit()) {
							exitGame.render(window);
						}
						gui.render();
						transition.render(gui.getCamera(), window);
						window.swapBuffers();
						frames++;
					}
					break;
				case 1:// Save Menu
					while(unprocessed>=frameCap) {
						if(window.hasResized()) {
							camera.setProjection(window.getWidth(), window.getHeight());
							mainMenu.resizeCamera(window);
							saveMenu.resizeCamera(window);
							inventory.resizeCamera(window);
							glViewport(0, 0, window.getWidth(), window.getHeight());
						}
						canRender = true;
						unprocessed -= frameCap;
						gameTrigger = saveMenu.updateSave(window, gameTrigger, overrideSave, chooseChara, transition);
						transition.update();
						switch(gameTrigger) {
							case -1:
								if(transition.getDoThing()) {
									gameState = 0;
									transition.setDoThing(false);
									gameTrigger = 0;
								}
								break;
							case 1:
								if(transition.getDoThing()) {
									gameState = 3;
									transition.setDoThing(false);
									gameTrigger = 0;
									try {
										Scanner fileReader = new Scanner(new File("./saves/save_" + saveMenu.getLoadedSave() + ".txt"));
										fileReader.useDelimiter("\n");
										String chara = fileReader.next();
										world = new World("test_level", camera, chara);
										world.calculateView(window);
									}
									catch(FileNotFoundException e) {
										e.printStackTrace();
										glfwSetWindowShouldClose(window.getWindow(), true);
									}
								}
								break;
						}
						window.update();
						if(frameTime>=1) {
							frameTime = 0;
							System.out.println("FPS: " + frames);
							frames = 0;
						}
					}

					if(canRender) {
						// rendering
						glClear(GL_COLOR_BUFFER_BIT);
						saveMenu.render(window);
						if(saveMenu.getSaveState()==1) {
							overrideSave.render(window);
						}
						if(saveMenu.getSaveState()==2) {
							chooseChara.render(window);
						}
						transition.render(gui.getCamera(), window);
						window.swapBuffers();
						frames++;
					}
					break;
				case 2:// Load Menu
					while(unprocessed>=frameCap) {
						if(window.hasResized()) {
							camera.setProjection(window.getWidth(), window.getHeight());
							mainMenu.resizeCamera(window);
							saveMenu.resizeCamera(window);
							inventory.resizeCamera(window);
							glViewport(0, 0, window.getWidth(), window.getHeight());
						}
						canRender = true;
						unprocessed -= frameCap;
						gameTrigger = saveMenu.updateLoad(window, gameTrigger, loadQuestion, transition);
						transition.update();
						switch(gameTrigger) {
							case -1:
								if(transition.getDoThing()) {
									gameState = 0;
									transition.setDoThing(false);
									gameTrigger = 0;
								}
								break;
							case 1:
								if(transition.getDoThing()) {
									gameState = 3;
									transition.setDoThing(false);
									gameTrigger = 0;
									try {
										Scanner fileReader = new Scanner(new File("./saves/save_" + saveMenu.getLoadedSave() + ".txt"));
										fileReader.useDelimiter("\n");
										String chara = fileReader.next();
										world = new World("test_level", camera, chara);
										world.calculateView(window);
									}
									catch(FileNotFoundException e) {
										e.printStackTrace();
										glfwSetWindowShouldClose(window.getWindow(), true);
									}
								}
								break;
						}
						window.update();
						if(frameTime>=1) {
							frameTime = 0;
							System.out.println("FPS: " + frames);
							frames = 0;
						}
					}

					if(canRender) {
						// rendering
						glClear(GL_COLOR_BUFFER_BIT);
						saveMenu.render(window);
						if(saveMenu.isLoadCheck()) {
							loadQuestion.render(window);
						}
						transition.render(gui.getCamera(), window);
						window.swapBuffers();
						frames++;
					}
					break;
				case 3:// Game
					while(unprocessed>=frameCap) {
						if(window.hasResized()) {
							camera.setProjection(window.getWidth(), window.getHeight());
							mainMenu.resizeCamera(window);
							saveMenu.resizeCamera(window);
							inventory.resizeCamera(window);
							world.calculateView(window);
							glViewport(0, 0, window.getWidth(), window.getHeight());
						}
						canRender = true;
						unprocessed -= frameCap;
						gameTrigger = world.update((float)frameCap, window, camera, transition, gameTrigger);
						switch(gameTrigger) {
							case 1:
								if(transition.getDoThing()) {
									gameState = 4;
									gameTrigger = 0;
									transition.setDoThing(false);
								}
								break;
						}
						transition.update();
						world.correctCamera(camera, window);
						window.update();
						if(frameTime>=1) {
							frameTime = 0;
							System.out.println("FPS: " + frames);
							frames = 0;
						}
					}

					if(canRender) {
						// rendering
						glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
						world.render(tiles, shader, camera);
						transition.render(camera, window);
						window.swapBuffers();
						frames++;
					}
					break;
				case 4: // Inventory
					while(unprocessed>=frameCap) {
						if(window.hasResized()) {
							camera.setProjection(window.getWidth(), window.getHeight());
							mainMenu.resizeCamera(window);
							saveMenu.resizeCamera(window);
							inventory.resizeCamera(window);
							glViewport(0, 0, window.getWidth(), window.getHeight());
						}
						canRender = true;
						unprocessed -= frameCap;
						gameTrigger = inventory.update(window, transition, gameTrigger);
						switch(gameTrigger) {
							case -1:// back to game
								if(transition.getDoThing()) {
									gameState = 3;
									gameTrigger = 0;
									transition.setDoThing(false);
								}
								break;
						}
						transition.update();
						window.update();
						if(frameTime>=1) {
							frameTime = 0;
							System.out.println("FPS: " + frames);
							frames = 0;
						}
					}

					if(canRender) {
						// rendering
						glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
						switch(inventory.getInvSpot()) {
							case 0:
								inventory.render(window);
								break;
							case 1:
								inventory.getPokeSet().render(window, shader);
								break;
							case 2:
								break;
							case 3:
								break;
						}
						transition.render(camera, window);
						window.swapBuffers();
						frames++;
					}
					break;
			}
		}
	}

//---------------------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		new Main().run();
	}
}
