/**
 * Class for World objects
 * @author: Damian Curran
 *
 */

import java.util.ArrayList;
import java.util.Scanner;

public class World {

	private Map map;
	private Player player;
	private ArrayList<Monster> monsters;
	private ArrayList<Item> items;
	
	private ArrayList<Monster> monstersToDelete;
	private ArrayList<Item> itemsToDelete;
	
	/*
	 *  Constructor
	 */
	public World(Player p, int height, int width) {
		
		this.map = new Map(height, width);
		this.player = p;
		this.monsters = new ArrayList<Monster>();
		this.items = new ArrayList<Item>();
		this.monstersToDelete = new ArrayList<Monster>();
		this.itemsToDelete = new ArrayList<Item>();
		
	}
	
	/*
	 *  Logic for running the main world loop
	 */
	public void runWorld(Scanner keyboard) {
		
		renderWorld();
		
		boolean inMap = true;	
		
		while (inMap) {
			
			System.out.print("> ");
			String userMove = keyboard.nextLine();
			
			moveMonsters();
			
			switch (userMove) {
			
				case "home":

					displayHomeMapMsg();
					return;
			
				case "w":
					
					movePlayerNorth();
					break;
					
				case "s":
					
					movePlayerSouth();
					break;
					
				case "a":
					
					movePlayerWest();
					break;
					
				case "d":
					
					movePlayerEast();
					break;
									
				default:
					
					break;
					
			}
			
			//Check if any entities in same grid - if so, battle:
			//If player loses any battle, the called method returns true, 
			//	which then exits runWorld() and returns to the calling method in GameEngine.
			if(checkClashAndBattle()) return;
			
			//Check if any player is on same grid as any item:
			//If player reaches the warp stone, the called method returns true, 
			//	which then exits runWorld() and returns to the calling method in GameEngine.
			if(checkItemsAndCollect()) return;
			
			//Check if any entities (monsters or items) remain in the world:
			//If there are no other entities, the called method returns true, 
			//	which then exits runWorld() and returns to the calling method in GameEngine.
			//(Used to exit world where default map (with no warp stone) is being used)
			if(checkIfEntitiesRemaining()) return;
			
			//Render updated world at end of each loop:
			renderWorld();
					
		}
		
	}
	
	/*
	 *  Add monster to <monster> ArrayList
	 *  Used to add default Monster already initialised in GameEngine
	 */
	public void addMonster(Monster m) {
		monsters.add(m);
		
	}	
	
	/*
	 *  Add monster to <monster> ArrayList (overloaded)
	 *  Used to add bespoke monster loaded from map file
	 */
	public void addMonster(int posX, int posY, String name, int maxHealth, int attackDamage) {
		monsters.add(new Monster (name, maxHealth, attackDamage, posX, posY));
		
	}

	/*
	 *  Add item to <item> ArrayList
	 *  Used to add bespoke item loaded from map file
	 */
	public void addItem(int posX, int posY, char c) {
		items.add(new Item (posY, posX, c));
		
	}

	/*
	 *  Update the map terrain elements at (x,y) with new symbol, c
	 *  Used during load map file in GameEngine to override default map with terrain from file
	 */
	public void updateMapTerrain(int y, int x, char c) {
		map.updateTerrain(y, x, c);
		
	}
	
	/*
	 *  Renders the map terrain with entities (called from runWorld loop):
	 */
	private void renderWorld() {
		
		int height = map.getHeight();
		int width = map.getWidth();
		
		//Renderable Interface is implemented on all objects which can be rendered on the map.
		//Nested loop prints the relevant "Renderable" object one-by-one.
		//The applicable object to print is selected by the getObjectToRender helper method.
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Renderable objectToRender = getObjectToRender(i, j);
				objectToRender.render();
			}
			System.out.println();
		}
		
		System.out.println();
		
	}

	/*
	 *  Helper method for renderWorld()
	 *  Wherever there are two Renderable objects with the same (x,y) coordinates
	 *  	the method returns the Renderable object with the top printing priority.
	 */
	private Renderable getObjectToRender(int y, int x) {
		
		//Renderable priority is given by the order in which the object types are examined, below:
		//The first object to match (x,y) coordinates is returned
		//(ie. Priority = 1. Player; 2. Monsters; 3. Items; 4. Terrain)
		
		if(player.atPosition(x, y)) return player;
		
		for(Monster m: monsters) {
			if(m.atPosition(x, y)) return m;
		}
		
		for(Item i: items) {
			if(i.atPosition(x, y)) return i;
		}
		
		return map.getElement(y, x);
		
	}

	/*
	 *  Helper method to adjust player position on map (Move North)
	 *  Called from runWorld loop
	 */
	private void movePlayerNorth() {
		
		if (player.getPosY() > 0 
				&& map.isTraversable(player.getPosY() - 1, player.getPosX())) {
			player.setPosY(player.getPosY() - 1);			
		}
		
	}
	
	/*
	 *  Helper method to adjust player position on map (Move South)
	 *  Called from runWorld loop
	 */
	private void movePlayerSouth() {
		
		if (player.getPosY() < (map.getHeight() - 1) 
				&& map.isTraversable(player.getPosY() + 1, player.getPosX())) {
			player.setPosY(player.getPosY() + 1);			
		}
		
	}
	
	/*
	 *  Helper method to adjust player position on map (Move West)
	 *  Called from runWorld loop
	 */
	private void movePlayerWest() {
		
		if (player.getPosX() > 0 
				&& map.isTraversable(player.getPosY(), player.getPosX() - 1)) {
			player.setPosX(player.getPosX() - 1);
		}
		
	}
	
	/*
	 *  Helper method to adjust player position on map (Move East)
	 *  Called from runWorld loop
	 */
	private void movePlayerEast() {
		
		if (player.getPosX() < (map.getWidth() - 1) 
				&& map.isTraversable(player.getPosY(), player.getPosX() + 1)) {
			player.setPosX(player.getPosX() + 1);
		}
		
	}
	
	/*
	 *  Helper method to automate any monster movements (if required) in the runWorld() loop
	 *  Called from runWorld loop
	 */
	private void moveMonsters() {

		//Check every monster position in the <monsters> array:
		for (Monster m: monsters) {
			
			//Check if that monster is within a 5:5 grid of the player:
			if ((Math.abs(m.getPosX() - player.getPosX()) <= 2) 
					&& (Math.abs(m.getPosY() - player.getPosY()) <= 2)) {
				
				//If monster is within 5:5 range, move monster accordingly: 
				
				//If player is to the LEFT of the monster:
				if (m.getPosX() > player.getPosX() 
						&& map.isTraversable(m.getPosY(), m.getPosX() - 1)) { 
					m.setPosX(m.getPosX() - 1);
					continue;					
				}
				
				//If player is to the RIGHT of the monster:
				if (m.getPosX() < player.getPosX() 
						&& map.isTraversable(m.getPosY(), m.getPosX() + 1)) { 
					m.setPosX(m.getPosX() + 1);
					continue;					
				}				
				
				//If player is ABOVE the monster:
				if (m.getPosY() > player.getPosY() 
						&& map.isTraversable(m.getPosY() - 1, m.getPosX())) { 
					m.setPosY(m.getPosY() - 1);
					continue;					
				}		
				
				//If player is BELOW the monster:
				if (m.getPosY() < player.getPosY() 
						&& map.isTraversable(m.getPosY() + 1, m.getPosX())) { 
					m.setPosY(m.getPosY() + 1);
					continue;					
				}		
				
			}
						
		}
				
	}
	
	/*
	 *  Method to check if player is on same grid as any monster, and if so, enter battle
	 *  Called from runWorld loop
	 *  Returns 'true' if player loses battle (which then exits runWorld), otherwise returns false
	 */
	private boolean checkClashAndBattle() {
				
		//Check every monster position in the <monsters> array:
		for (Monster m : monsters) {
			
			//If monster position == player position, run the battle loop:
			if (player.getPosX() == m.getPosX() && player.getPosY() == m.getPosY()) {
				
				runBattleLoop(m);
				
				//Before each battle with next monster, check if player health is still > 0. 
				//If not > 0, return true (which will exit the world loop):
				if (player.isDefeated()) return true;
				
			}
		}
		
		//Permanently delete all monsters that lost in battle:
		for (Monster m : monstersToDelete) monsters.remove(m);		
		monstersToDelete.clear();
		
		//Default return false (ie. player is still alive and game continues)
		return false;
		
	}
	
	/*
	 *  Method to check if player is on same grid as any item, and if so, collect
	 *  Called from runWorld loop
	 *  Returns 'true' if player collects warp stone (which then exits runWorld), 
	 *  	otherwise returns false
	 */
	private boolean checkItemsAndCollect() {
				
		//Check every item position in the <item> array:
		for (Item i : items) {
			
			//If item position = player position, assess item type and proceed accordingly:
			if (player.getPosX() == i.getPosX() && player.getPosY() == i.getPosY()) {
				
				char symbol = i.getSymbol();
				
				switch (symbol) {
				
					case '+': //Item = Healing Item
						
						player.toFullHealth();
						System.out.println("Healed!");
						itemsToDelete.add(i);
						break;
					
					case '^': //Item = Damage Perk
						
						player.incrementBonus();
						System.out.println("Attack up!");
						itemsToDelete.add(i);
						break;	
					
					case '@': //Item = Warp Stone
						
						player.incrementLevel();
						System.out.println("World complete! (You leveled up!)");
						System.out.println();
						return true; //Return true (exit runWorld) if player collects warp stone
						
					default:
						
						break;
						
				}
					
			}
			
		}
		
		//Permanently delete all items that were collected:
		for (Item i : itemsToDelete) items.remove(i);		
		itemsToDelete.clear();
		
		//Default return false (ie. warp stone was not collected and game continues)
		return false;
		
	}
	
	/*
	 *  Method to check if any entities remaining
	 *  Called from runWorld loop
	 *  Returns 'true' if nothing remaining on map
	 *  Used to exit Default Map (with no warp stone) once monster killed
	 */
	private boolean checkIfEntitiesRemaining() {
		
		//Check entities - return true if monsters and items is empty - game to exit
		if (monsters.isEmpty() && items.isEmpty()) return true;
		
		//Default return false - some entities still exist - game continues
		return false;
		
	}
	
	/*
	 *  Battle loop called if monster position = player position
	 *  Called bycheckClashandBattle()
	 */
	private void runBattleLoop(Monster m) {

		displayEncounterMsg(m);
		
		boolean inBattle = true;

		while(inBattle) {
			
			displayCurrentHealth(m);
			
			playerAttacks(m);
			
			//Check if monster alive, if not, process accordingly:
			if (m.isDefeated()) {
				System.out.println(player.getName() + " wins!");
				System.out.println();
				monstersToDelete.add(m); //Dead monster added to array to be deleted at end of turn
				inBattle = false;
				break;
			}
			
			monsterAttacks(m);
			
			//Check if player alive, if not, process accordingly:
			if (player.isDefeated()) {
				System.out.println(m.getName() + " wins!");
				System.out.println();
				inBattle = false;
				break;
			}
			
			System.out.println();
			
		}
				
	}
	
	/*
	 *  Helper method to decrement monster health. Called from runBattleLoop()
	 */
	private void playerAttacks(Monster m) {
		
		int newHealth = m.getCurrentHealth() - player.getAttackDamage();
		m.setCurrentHealth(newHealth);
		System.out.println(player.getName() + " attacks " + m.getName() 
			+ " for " + player.getAttackDamage() + " damage.");
	
	}
	
	/*
	 *  Method to decrement player health. Called from runBattleLoop()
	 */
	private void monsterAttacks(Monster m) {
		
		int newHealth = player.getCurrentHealth() - m.getAttackDamage();
		player.setCurrentHealth(newHealth);
		System.out.println(m.getName() + " attacks " + player.getName() 
			+ " for " + m.getAttackDamage() + " damage.");
		
	}
	
	/*
	 *  Displays return home message from MapLoop to main GameLoop.
	 */
	private void displayHomeMapMsg() {
		System.out.println("Returning home...");
		System.out.println();
		
	}
	
	/*
	 *  Displays encounter message used at commencement of BattleLoop.
	 */
	private void displayEncounterMsg(Monster m) {
		System.out.println(player.getName() + " encountered a " + m.getName() + "!");
		System.out.println();
		
	}
	
	/*
	 *  Displays current health of player and monster used during BattleLoop.
	 */
	private void displayCurrentHealth(Monster m) {
		System.out.println(player.getName() + " " + player.getCurrentHealth() + "/" + player.getMaxHealth()
				+ " | "
				+ m.getName() + " " + m.getCurrentHealth() + "/" + m.getMaxHealth());
		
	}
		
}
