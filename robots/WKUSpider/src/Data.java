
public class Data {
//ر للطلبة بيئة دراس
	String word;
	int weight;
	
	public Data(String word){
		this.word = word;
		weight = 1;
	}
	
	@Override
	public boolean equals(Object o){
		return(word.toLowerCase().equals(((Data)o).word.toLowerCase()));
	}
	
	public void print(){
		System.out.println(weight + "\t" + word);
	}
}
