
public class Data {

	String word;
	int weight, URLIndex;
	
	public Data(String word, int URLIndex){
		this.word = word;
		this.URLIndex = URLIndex;
		weight = 1;
	}
	
	@Override
	public boolean equals(Object o){
		return(word.toLowerCase().equals(((Data)o).word.toLowerCase()));
	}
	
	public void print(){
		System.out.println(weight + "\t" + URLIndex + "\t" + word);
	}
}
