/**
 * Main game engine
 * @author: Damian Curran
 *
 */

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;

public class GameEngine {
	
	private World world;
	private Player player;
	private Monster defaultMonster;
	
	private static Scanner keyboard = new Scanner(System.in);
	
	private static final int DEFAULT_PLAYER_POSX = 1;
	private static final int DEFAULT_PLAYER_POSY = 1;
	private static final int DEFAULT_MONSTER_POSX = 4;
	private static final int DEFAULT_MONSTER_POSY = 2;
	private static final int DEFAULT_MAP_HEIGHT = 4;
	private static final int DEFAULT_MAP_WIDTH = 6;	
	
	private static final String PLAYER_DATA_FILE_NAME = "player.dat";
	
	public static void main(String[] args) {
				
		// Create an instance of the game engine
		GameEngine gameEngine = new GameEngine();
		
		// Run the main game loop
		gameEngine.runMainMenuLoop();
		
	}
	
	/*
	 *  Logic for running the main game loop
	 */
	private void runMainMenuLoop() {
		
		displayMainMenu();
		boolean inGame = true;	
		
		while (inGame) {
			
			System.out.print("> ");
			String[] userInput = keyboard.nextLine().split(" ");
			switch (userInput[0]) {
			
				case "help":
					
					displayHelpMsg();
					break;
					
				case "commands":
					
					displayCommandsMsg();
					break;
					
				case "player":
					
					optionPlayer();
					break;
					
				case "monster":
					
					createMonster();
					break;
					
				case "start":
					
					optionStart(userInput);
					break;
				
				case "save":
					
					save();
					break;
				
				case "load":
					
					load();
					break;
					
				case "exit":

					inGame = false;
					break;
					
				default:
					
					displayDefaultMsg();
					break;
			
			}
			
			if (!inGame) {
				displayExitMsg();
			}
			
		}
		
	}
	
	/*
	 *  Method to check if player exists and, if not, create player (called from MainMenuLoop)
	 */
	private void optionPlayer() {
		
		if (player == null) {
			createPlayer();
		} else {
			displayPlayerDetails();
		}					
		
	}

	/*
	 *  Method to check conditions prior to starting new game and direct to next method 
	 *  	(called from MainMenuLoop)
	 *  Next method call depends on if player found, and if map file exists
	 */
	private void optionStart(String[] userInput) {
		
		if (player == null) {
			displayNotFound("player");	
			
		} else {
			
			if (userInput.length == 1) {
				startNoFile(); 
				//This method directs to world.runWorld() (based on default world)
				
			} else if (userInput.length == 2) {
				startFromFile(userInput[1]); 
				//This method directs to world.runWorld() (based on loaded world)
			
			} else if (userInput.length > 2) {
				displayDefaultMsg();
				return;
				
			}
			
			//Upon returning from game, any player attack bonuses received are reset
			player.resetBonus();
			
		}
		
		pressEnterToReturn();
		
	}

	/*
	 *  Method to create player (called from OptionPlayer)
	 */
	private void createPlayer() {
		
		System.out.println("What is your character's name?");
		
		String playerName;
		
		try {
			playerName = keyboard.nextLine();
			if (playerName.length() == 0) throw new Exception();
		} catch (Exception e) {
			System.out.println("Error. Player must have a valid name. Please type 'player' to start again.\n");
			return;
		}
		
		player = new Player(playerName);
		
		System.out.println("Player '" + player.getName() + "' created.");
		System.out.println();
		
		pressEnterToReturn();
		
	}
	
	/*
	 *  Method to create monster (called from MainMenuLoop)
	 */
	private void createMonster() {
		
		System.out.print("Monster name: ");
		String monsterName;
		try {
			monsterName = keyboard.nextLine();
			if (monsterName.length() == 0) throw new Exception(); //Checks name length is not zero
		} catch (Exception e) {
			System.out.println("Error. Monster must have a valid name. Please type 'monster' to start again.\n");
			return;
		}
				
		System.out.print("Monster health: ");
		int maxHealth;
		//Check if next input is an integer. If not, display error message and return:
		try {
			maxHealth = Integer.parseInt(keyboard.nextLine()); 
		} catch (Exception e) {
			System.out.println("Error. Monster Health needs to be an integer. Please type 'monster' to start again.\n");
			return;
		}
		
		System.out.print("Monster damage: ");
		int attackDamage;
		//Check if next input is an integer. If not, display error message and return:
		try {
			attackDamage = Integer.parseInt(keyboard.nextLine()); 
		} catch (Exception e) {
			System.out.println("Error. Attack Damage needs to be an integer. Please type 'monster' to start again.\n");
			return;
		}
		
		
		//Create new monster based on above inputs:
		defaultMonster = new Monster(monsterName, maxHealth, attackDamage);
		
		System.out.println("Monster '" + defaultMonster.getName() + "' created.");
		System.out.println();
		
		pressEnterToReturn();
		
	}
	
	/*
	 *  Method to start world when NO map file called (called from OptionStart)
	 */
	private void startNoFile () {
		
		//Check if default monster exists, if not, return:
		if (defaultMonster == null) {		
			displayNotFound("monster");
			return;
		}
		
		//Reset defaults:
		player.toFullHealth();
		defaultMonster.toFullHealth();
		player.setPosition(DEFAULT_PLAYER_POSX, DEFAULT_PLAYER_POSY);
		defaultMonster.setPosition(DEFAULT_MONSTER_POSX, DEFAULT_MONSTER_POSY);
		
		//Create new world with defaults:
		world = new World (player, DEFAULT_MAP_HEIGHT, DEFAULT_MAP_WIDTH);
		world.addMonster(defaultMonster);
		
		//Run new world just created:
		world.runWorld(keyboard);
		
	}
	
	/*
	 *  Method to start world from a map file (called from OptionStart)
	 */
	private void startFromFile(String s) {
		
		String filename = s + ".dat";
		ArrayList<String> fileData = null;
		
		//Try open file and read file data into ArrayList:
		try {
			fileData = readIn(filename);	
		} catch (GameLevelNotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println();
			return;
		}
	
		//Try process map file data and load new world with those attributes:
		try {
			updateWorldFromFileData(fileData);
		} catch (Exception e) {
			System.out.println("The following error occured while loading the file:");
			System.out.println(e.getMessage());
			return;
		}
		
		//Reset player health when starting new game
		//	(carried over from Assignment 1 specs (and not contradicted in Assignment 2 specs))
		player.toFullHealth();
				
		//Run world just updated from the file:
		world.runWorld(keyboard);
		
	}
	
	/*
	 *  Helper method to open map file and read data into ArrayList (called from StartFromFile)
	 */
	private ArrayList<String> readIn(String filename)
		throws GameLevelNotFoundException {
		
		ArrayList<String> fileData = new ArrayList<String>();		
		
		try {

			File file = new File(filename);
			Scanner in = new Scanner(file);
			
			while (in.hasNextLine()) {
				fileData.add(in.nextLine());
			}
			
			in.close();
			
		} catch (FileNotFoundException e) {
			throw new GameLevelNotFoundException();
			
		} catch (Exception e) { //Catch any other exception types
			throw new GameLevelNotFoundException("Error reading map file.");
			
		}
		
		return fileData;
		
	}
	
	/*
	 *  Helper method to create new world, and update it with info from map file 
	 *  	(called from StartFromFile)
	 */
	private void updateWorldFromFileData(ArrayList<String> fileData) {
				
		//Taking each line of the ArrayList String (previously read from the map file), in turn:
		
		//Get map dimensions from fileData:
		String[] mapDimensions = fileData.get(0).split(" ");
		int mapWidth = Integer.parseInt(mapDimensions[0]);
		int mapHeight = Integer.parseInt(mapDimensions[1]);
		
		//Create new world object with map dimensions, and existing player:
		world = new World (player, mapHeight, mapWidth);
		
		//Update Terrain info based on file input:
		//Get the terrain info from the fileDate as an array of Strings:
		String[] terrainInfo = new String[mapHeight];
		for (int i = 0; i < mapHeight; i ++) {
			terrainInfo[i] = fileData.get(i + 1);
		}
		//Process each character (ie. terrain item) in the array of Strings:
		for (int i = 0; i < terrainInfo.length; i ++) {
			for (int j = 0; j < terrainInfo[i].length(); j ++) {
				world.updateMapTerrain(i, j, terrainInfo[i].charAt(j));
			}
		}
		
		//Process balance of file data (ie. processing the player, monster and item entities):
		int count = mapHeight + 1;
		while (count < fileData.size()) {
			
			String[] line = fileData.get(count).split(" ");
			int x;
			int y;
			String name;
			int health;
			int attack;
			char symbol;
	
			switch(line[0]) {
			
				case "player":
					x = Integer.parseInt(line[1]);
					y = Integer.parseInt(line[2]);
					player.setPosition(x, y);
					break;
				
				case "monster":
					x = Integer.parseInt(line[1]);
					y = Integer.parseInt(line[2]);
					name = line[3];
					health = Integer.parseInt(line[4]);
					attack = Integer.parseInt(line[5]);
					world.addMonster(x, y, name, health, attack);
					break;
				
				case "item":
					x = Integer.parseInt(line[1]);
					y = Integer.parseInt(line[2]);
					symbol = line[3].charAt(0);
					world.addItem(x, y, symbol);
					break;
					
				default:
					break;
					
			}
			
			count++;
			
		}
		
	}

	/*
	 *  Method to save player data to file (called from MainMenuLoop)
	 */
	private void save() {
		
		//Check if player exists:
		if (player == null) {
			
			System.out.println("No player data to save.");
			System.out.println();
			return;
			
		}
		
		//Try creating player data file:
		PrintWriter outputStream = null;
		
		try {
			outputStream = new PrintWriter(new FileOutputStream(PLAYER_DATA_FILE_NAME));
			
		} catch (Exception e) {
			System.out.println("Error saving player data.");
			System.out.println();
			return;	
			
		}
		
		//Write player name and level to data file:
		outputStream.print(player.getName() + " " + player.getLevel());
		outputStream.close();		
		
		System.out.println("Player data saved.");
		System.out.println();
		
	}
	
	/*
	 *  Method to load player data (called from MainMenuLoop)
	 */
	private void load() {
		
		String [] player_details = null;
		
		//Check if player data file exists, and if so, read:
		try {
			
			File file = new File(PLAYER_DATA_FILE_NAME);
			Scanner in = new Scanner(file);
			player_details = in.nextLine().split(" ");
			in.close();
			
		} catch (FileNotFoundException e) {
			
			System.out.println("No player data found.");
			System.out.println();
			return;
		}
		
		//Parse data read from player data file, and update player variables:
		try {
			
			String name = player_details[0];
			int level = Integer.parseInt(player_details[1]);
			
			player = new Player(name, level);
			
			System.out.println("Player data loaded.");
			System.out.println();
			
		} catch (Exception c) {
			
			System.out.println("Error reading player data.");
			System.out.println();
			
		}
		
	}
	
	/*
	 *  Method to assess if Enter key is pressed - called variously throughout Game Engine
	 *  Used to reset display and return to main menu
	 */
	private void pressEnterToReturn() {
		
		System.out.println("(Press enter key to return to main menu)");
		
		while(keyboard.nextLine() == null) {
			//Wait here for ENTER press.
		}
		
		displayMainMenu();
	
	}
	
	/*
	 *  Displays the current player details:
	 */
	private void displayPlayerDetails() {
		
		String playerDetails = player.getName() + " (Lv. " + player.getLevel() + ")\n"
				+ "Damage: " + player.getAttackDamage() + "\n"
				+ "Health: " + player.getCurrentHealth() + "/" + player.getMaxHealth();
		
		System.out.println(playerDetails);
		System.out.println();
		
		pressEnterToReturn();
		
	}
	
	/*
	 *  Calls each listed method which, combined, display the main menu:
	 */
	private void displayMainMenu() {
		
		displayTitleText();
		displayCurrentConfig();
		displayInitialMsg();
		
	}
	
	/*
	 *  Displays the title text:
	 */
	private void displayTitleText() {
		
		String titleText = " ____                        \n" + 
				"|  _ \\ ___   __ _ _   _  ___ \n" + 
				"| |_) / _ \\ / _` | | | |/ _ \\\n" + 
				"|  _ < (_) | (_| | |_| |  __/\n" + 
				"|_| \\_\\___/ \\__, |\\__,_|\\___|\n" + 
				"COMP90041   |___/ Assignment ";
		
		System.out.println(titleText);
		System.out.println();

	}
	
	/*
	 *  Displays current player-monster configuration and health:
	 */
	private void displayCurrentConfig() {
		
		String playerInfo = "[None]";
		String monsterInfo = "[None]";
		
		if (player != null) {
			playerInfo = player.getName() + " " + player.getCurrentHealth() + "/" + player.getMaxHealth();
		}
		
		if (defaultMonster != null) {
			 monsterInfo = defaultMonster.getName() + " " + defaultMonster.getCurrentHealth() + "/" + defaultMonster.getMaxHealth();
		}
		
		System.out.println("Player: " + playerInfo + "  | Monster: " + monsterInfo);
		System.out.println();
				
	}
	
	/*
	 *  Displays the initial message to user below the Current Configuration display:
	 */
	private void displayInitialMsg() {
		
		String initialMsg = "Please enter a command to continue.\n"
				+ "Type 'help' to learn how to get started.";
		
		System.out.println(initialMsg);
		System.out.println();
		
	}
	
	/*
	 *  Displays the help text:
	 */
	private void displayHelpMsg() {
		
		String helpMsg = "Type 'commands' to list all available commands\n"
				+ "Type 'start' to start a new game\n"
				+ "Create a character, battle monsters, and find treasure!";
		
		System.out.println(helpMsg);
		System.out.println();

	}
	
	/*
	 *  Displays are error message when player / monster is not yet defined:
	 */
	private void displayNotFound(String thing) {
		
		String notFound = "No " + thing + " found, please create a " + thing + " with '" + thing + "' first.";

		System.out.println(notFound);
		System.out.println();
				
	}
	
	/*
	 *  Displays the text response to the 'command' input:
	 */
	private void displayCommandsMsg() {
		
		String commandsMsg = "help\n"
				+ "player\n"
				+ "monster\n"
				+ "start\n"
				+ "load\n"
				+ "save\n"
				+ "exit";
		
		System.out.println(commandsMsg);
		System.out.println();

	}
	
	/*
	 *  Displays default error text:
	 */
	private void displayDefaultMsg() {
		
		System.out.println("Sorry, that command is not recognised.");
		displayHelpMsg();
		
	}
	
	/*
	 *  Displays exit message upon quitting the program:
	 */
	private void displayExitMsg() {
		
		System.out.println("Thank you for playing Rogue!");
		
	}

}
