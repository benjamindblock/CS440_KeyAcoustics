
public class Bigram {
	private static String pair;
	private static KEYBOARD_SIDE charOne;
	private static KEYBOARD_SIDE charTwo;
	private static DISTANCE dist;
	
	public Bigram(String str){
		pair = str;
	}
	
	public void setKeyboardSides(KEYBOARD_SIDE ks1, KEYBOARD_SIDE ks2){
		charOne = ks1;
		charTwo = ks2;
	}
	
	public void setDistance(DISTANCE d){
		dist = d;
	}

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

	public static DISTANCE getDist() {
		return dist;
	}
}
