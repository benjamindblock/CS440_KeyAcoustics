
/**
 * This class represents a string of length two (i.e. "ab"). 
 * 
 * It contains methods to determine what side of the keyboard each character is on,
 * and whether or not the bigram is a near or far pair.  
 * 
 * @author Ben
 *
 */
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

	public static String getPair() {
		return pair;
	}

	/**
	 * Gives us the side of the keyboard that the first character in the bigram is on.
	 * 
	 * @return Left or Right
	 */
	public KEYBOARD_SIDE getCharOneSide() {
		return charOneSide;
	}

	/**
	 * Gives us the side of the keyboard that the second character in the bigram is on.
	 * 
	 * @return Left or Right
	 */
	public KEYBOARD_SIDE getCharTwoSide() {
		return charTwoSide;
	}
	
	/**
	 * Tells us whether this is a near or far bigram.
	 * 
	 * @return Near or Far
	 */
	public DISTANCE getDistance(){
		return dist;
	}

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
