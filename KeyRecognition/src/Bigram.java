
public class Bigram {
	private final int THRESHOLD = 3;
	
	private static String pair;
	private static KEYBOARD_SIDE charOneSide;
	private static KEYBOARD_SIDE charTwoSide;
	private static DISTANCE dist;
	private static KeyPosition kp1;
	private static KeyPosition kp2;
	
	
	public Bigram(String str){
		pair = str;
		kp1 = new KeyPosition(str.substring(0, 1));
		kp2 = new KeyPosition(str.substring(1, 2));
		setKeyboardSides();
		setDistance();
	}
	
	public void setKeyboardSides(){
		charOneSide = kp1.keyboardSide();
		charTwoSide = kp2.keyboardSide();
	}
	
//	public void setDistance(DISTANCE d){
//		dist = d;
//	}

	public static String getPair() {
		return pair;
	}

//	public static KEYBOARD_SIDE getCharOne() {
//		return charOne;
//	}
//
//	public static KEYBOARD_SIDE getCharTwo() {
//		return charTwo;
//	}

	/**
	 * Determines if the given letter pair is a near-pair or a
	 * far-pair.
	 */
	public void setDistance(){
		int x = (kp2.getXPos() - kp1.getXPos());
		x = x * x;
		
		int y = (kp2.getYPos() - kp1.getYPos());
		y = y * y;
		
		double distance = Math.sqrt(x + y);
		
		if(distance > THRESHOLD){
			dist = DISTANCE.FAR;
		}else{
			dist = DISTANCE.NEAR;
		}
	}
	
	public String toString(){
		String ret = "";
		ret = "Word: "+pair+"\n";
		ret = ret+"Distance: "+dist+"\n";
		ret = ret+"Side one: "+charOneSide+"\n";
		ret = ret+"Side two: "+charTwoSide+"\n";
		return ret;
	}
}
