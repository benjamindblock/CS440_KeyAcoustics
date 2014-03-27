import java.util.ArrayList;


public class KeyPosition {

	private int xPos;
	private int yPos;
	private String name;

	public KeyPosition(String name){
		this.name = name;
		determineLocation();
	}

	private void determineLocation(){

		if(name.equals("q")){
			xPos = -5;
			yPos = 1;
		}
		
		if(name.equals("w")){
			xPos = -4;
			yPos = 1;
		}
		
		if(name.equals("e")){
			xPos = -3;
			yPos = 1;
		}
		
		if(name.equals("r")){
			xPos = -2;
			yPos = 1;
		}
		
		if(name.equals("t")){
			xPos = -1;
			yPos = 1;
		}
		
		if(name.equals("y")){
			xPos = 0;
			yPos = 1;
		}
		
		if(name.equals("u")){
			xPos = 1;
			yPos = 1;
		}
		
		if(name.equals("i")){
			xPos = 2;
			yPos = 1;
		}
		
		if(name.equals("o")){
			xPos = 3;
			yPos = 1;
		}
		
		if(name.equals("p")){
			xPos = 4;
			yPos = 1;
		}
		
		if(name.equals("a")){
			xPos = -4;
			yPos = 0;
		}
		
		if(name.equals("s")){
			xPos = -3;
			yPos = 0;
		}
		
		if(name.equals("d")){
			xPos = -2;
			yPos = 0;
		}
		
		if(name.equals("f")){
			xPos = -1;
			yPos = 0;
		}
		
		if(name.equals("g")){
			xPos = 0;
			yPos = 0;
		}
		
		if(name.equals("h")){
			xPos = 1;
			yPos = 0;
		}
		
		if(name.equals("j")){
			xPos = 2;
			yPos = 0;
		}
		
		if(name.equals("k")){
			xPos = 3;
			yPos = 0;
		}
		
		if(name.equals("l")){
			xPos = 4;
			yPos = 0;
		}
		
		if(name.equals("z")){
			xPos = -3;
			yPos = -1;
		}
		
		if(name.equals("x")){
			xPos = -2;
			yPos = -1;
		}
		
		if(name.equals("c")){
			xPos = -1;
			yPos = -1;
		}
		
		if(name.equals("v")){
			xPos = 0;
			yPos = -1;
		}
		
		if(name.equals("b")){
			xPos = 1;
			yPos = -1;
		}
		
		if(name.equals("n")){
			xPos = 2;
			yPos = -1;
		}
		
		if(name.equals("m")){
			xPos = 3;
			yPos = -1;
		}

	}
	
	public int getXPos(){
		return xPos;
	}
	
	public int getYPos(){
		return yPos;
	}
	
	public KEYBOARD_SIDE keyboardSide(){

		ArrayList<String> lKeys = new ArrayList();
		
		lKeys.add("q");
		lKeys.add("w");
		lKeys.add("e");
		lKeys.add("r");
		lKeys.add("t");
		lKeys.add("a");
		lKeys.add("s");
		lKeys.add("d");
		lKeys.add("f");
		lKeys.add("g");
		lKeys.add("z");
		lKeys.add("x");
		lKeys.add("c");
		lKeys.add("v");
		lKeys.add("b");
		
		if(lKeys.contains(name)){
			return KEYBOARD_SIDE.LEFT;
		}else{
			return KEYBOARD_SIDE.RIGHT;
		}
	}
	

}
