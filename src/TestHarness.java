
public class TestHarness {

	public static void main(String[] args) {
		
		World world = new World();

		world.renderWorld();
		
		Map map1 = new Map();
		System.out.println(map1.getWidth());
		System.out.println(map1.getElement(1,1).getSymbol());		
		System.out.println("Done");
		

	}

	
}
