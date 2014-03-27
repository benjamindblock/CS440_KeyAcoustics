
public class Word {
	private static String keyWord;
	private static Bigram[] bigramArray;
	
	public Word(String word){
		keyWord = word;
		int n = keyWord.length();
		bigramArray = new Bigram [n-1];
		createBigramArray();
	}
	
	public static void createBigramArray(){
		for(int i = 0; i < keyWord.length()-1; i++){
			char[] pairArray = new char [2];
			pairArray[0] = keyWord.charAt(i);
			pairArray[1] = keyWord.charAt(i+1);
			String pair = new String(pairArray);
			Bigram big = new Bigram(pair);
			System.out.println(keyWord + " pair [" + i + "]: "+pair);
			//big.addPair(pair);
		}
	}
		
}
