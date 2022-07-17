/**
 * Class for Unit objects
 * @author: Damian Curran
 *
 */

abstract class Unit extends Entity {

	private String name;
	private int currentHealth;
	
	/*
	 *  Constructor
	 */
	public Unit (String name) {
		super();
		this.name = name;
	}
	
	/*
	 *  Getter methods
	 */
	public String getName() {
		return this.name;
	}

	public int getCurrentHealth() {
		return this.currentHealth;
	}
	
	/*
	 *  Setter method
	 */
	public void setCurrentHealth(int newHealth) {
		this.currentHealth = newHealth;
	}
		
	/*
	 *  Method to return player to full health. Used at commencement of new MapLoop
	 */
	public void toFullHealth() {
		this.setCurrentHealth(this.getMaxHealth());
	}
	
	/*
	 *  Method to check if player / monster defeated. Used after and during a BattleLoop
	 */
	public boolean isDefeated() {
		
		if (this.getCurrentHealth() <= 0) {
			return true; 
		} else {
			return false;
		}
		
	}
	
	/*
	 *  Abstract methods to return max health
	 *  (required because Player and Monster process max health differently)
	 */
	abstract int getMaxHealth();
	
	/*
	 *  Abstract methods to return attack damage
	 *  (required because Player and Monster process attack differently)
	 */
	abstract int getAttackDamage();
	
}
