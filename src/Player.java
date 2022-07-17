/**
 * Class for Player objects
 * @author: Damian Curran
 *
 */

public class Player extends Unit {

	private int level;
	private int bonus; 
	
	private final static int BASE_MAX_HEALTH = 17;
	private final static int MAX_HEALTH_LEVEL_MULTIPLIER = 3;
	private final static int BASE_ATTACK_DAMAGE = 1;
	
	/*
	 *  Constructor
	 */
	public Player(String name) {
		
		super(name);	
		this.level = 1;
		this.bonus = 0;
		this.toFullHealth();
		this.setSymbol(Character.toUpperCase(name.charAt(0)));
		
	}
	
	/*
	 *  Constructor (overloaded)
	 */
	public Player(String name, int level) {
		
		super(name);	
		this.level = level;
		this.bonus = 0;
		this.toFullHealth();
		this.setSymbol(Character.toUpperCase(name.charAt(0)));
		
	}

	/*
	 *  Getter methods
	 */
	public int getLevel() {
		return level;
	}
	
	public int getBonus() {
		return bonus;
	}
	
	/*
	 *  Getter methods, using formulas from Assignment 1
	 */
	public int getMaxHealth() {
		return (BASE_MAX_HEALTH + this.getLevel() * MAX_HEALTH_LEVEL_MULTIPLIER);
	}
	
	public int getAttackDamage() {
		return (BASE_ATTACK_DAMAGE + this.getLevel() + this.getBonus());
	}
	
	/*
	 *  Reset attack bonus (called upon leaving the world and returning to main menu)
	 */
	public void resetBonus() {
		this.bonus = 0;
	}	
	
	/*
	 *  Methods to increment level and bonus (called when items picked up by player)
	 */
	public void incrementLevel() {
		this.level = this.level + 1;
	}
	
	public void incrementBonus() {
		this.bonus = this.bonus + 1;
	}
	
}
