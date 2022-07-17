/**
 * Class for Monster objects
 * @author: Damian Curran
 *
 */

public class Monster extends Unit{

	private int maxHealth;
	private int attackDamage;
	
	/*
	 *  Constructor
	 */
	public Monster(String name, int maxHealth, int attackDamage) {
		
		super(name);		
		this.maxHealth = maxHealth;
		this.attackDamage = attackDamage;
		this.toFullHealth();
		this.setSymbol(Character.toLowerCase(name.charAt(0)));
	}
	
	/*
	 *  Constructor (overloaded)
	 */
	public Monster(String name, int maxHealth, int attackDamage, int posX, int posY) {
		this(name, maxHealth, attackDamage);
		this.setPosition(posX, posY);
	}
	
	/*
	 *  Getter methods
	 */
	public int getMaxHealth() {
		return this.maxHealth;
	}
	
	public int getAttackDamage() {
		return this.attackDamage;
	}
	
	/*
	 *  Setter method
	 */
	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}
	
}
