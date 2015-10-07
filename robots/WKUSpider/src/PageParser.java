import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A tool for parsing the webpage data to collect information.
 * @author Ploaj
 *
 */
public class PageParser {
	
	String url, title;
	ArrayList<String> links;
	ArrayList<Data> dataNodes;
	int pageIndex;
	String preview = "";
	String[] lowWeight = {"of","a","the","and","is","in","to","all","in"};
	// we are also gonna need a dictionary of words and a way to determine weights
	// database specs are needed past here
	
	
	//161.6.0.0 - 161.6.255.255
	
	/*
	 * Notes:
	 * We can actually parse all the info in one go, 
	 * but for now I have it separate so we can
	 * catch area-specific bugs easier.
	 */
	
	/**
	 * This object will hold all of the page info until it's stored in a database.
	 * @param url of page
	 */
	public PageParser(String url){
		
		// store the url with this object for later storage in the database
		this.url = url;
		
		// the source code of the page
		String src = getSource(url);
		
		links = new ArrayList<String>();
		dataNodes = new ArrayList<Data>();
		
		// The title text is probably among the most important things to consider
		
		title = getTitle(src);
//		System.out.println(title + "\n");
		
		
		// for parsing
		// for now just gets links
		gatherLinks(src);
		
		String text = gatherText(src);
		
		preview = text.substring(397)+"...";

		// remove punctuation for now
		text = text.replaceAll("\\.", "");
		text = text.replaceAll("\\:", "");
		text = text.replaceAll(",", "");
		
		String[] words = text.split(" ");
		
		
		//temp
		pageIndex = 0;
		
		
		for(int w = 0 ; w < words.length ; w++)
			addData(words[w], (int)((0.0 + words.length - w) / words.length * 100.0));
		
		Collections.sort(dataNodes,new OrderNode());
		
//		for(Data d : dataNodes)
//			d.print();
		
		
	}
	
	class OrderNode implements Comparator<Data>{

		@Override
		public int compare(Data o1, Data o2) {
			if(o1.weight > o2.weight)
				return -1;
			else if (o1.weight < o2.weight)
				return 1;
			return 0;
		}
		
	}
	
		
	/**
	 * This function should be used when the object is created in order to gather all the links 
	 * on the page.
	 * @param src - sourse code of site
	 */
	private void gatherLinks(String src){
		
		// gets the first link location (every page should have at least one)
		String code = src.substring(src.indexOf("href=\"")+6);
		
		// infinite loop so we can go through the entire string
		// we could use while it contains a link, but this actually takes less processes
		while (true) {
			
			// gets the link are; ends with a "
			String link = code.substring(0, code.indexOf('"'));

			// adds the wku site to incomplete links
			if (link.charAt(0) == '/')
				link = "https://www.wku.edu" + link;

			
			// make sure it's a WKU page and not a file
			if (link.contains("wku.")
					&& (link.contains("php") || link.contains("htm") 
					||  link.charAt(link.length() - 1) == '/')) {
//				System.out.println(link);
				links.add(link);
			}
			
			// move to the next href if it exists and exit the loop otherwise
			int nextIndex = code.indexOf("href=\"");
			
			if(nextIndex == -1)
				break;
			else
				code = code.substring(nextIndex+6);
		}
		
//		System.out.println("\nDone - found " + links.size() + " WKU links");
	}
	
	/**
	 * Returns the title of the page
	 * @param src of page
	 * @return The title of the page
	 */
	private String getTitle(String src){
		String code = src.substring(src.indexOf("<title>")+7);
		code = code.substring(0,code.indexOf("</title>"));
		
		return code;
	}
	
//	private int getLinkCode(String link){
//		int code = 0;
//		for(int i = 0 ; i < link.length() ; i++)
//			code += link.charAt(i)+i;
//		return code;
//	}
	
//	private void gatherHeadings(String src){
//		
//	}
	
	private String gatherText(String src){
		String text = "";
		
		int index = src.indexOf("<p>");
		
		if(index == -1)
			return null;
		
		String code = src.substring(index+3);
		
		while (true) {
			
			text += clearTags(code.substring(0,code.indexOf("</p>")));

			index = code.indexOf("<p>");
			
			if(index == -1) 
				break;
			else
				code = code.substring(index+3);
		}
		
		return text;
	}
	
	private void addData(String in, int weight){
		Data d = new Data(in, pageIndex);

		d.weight += weight;
		
		if(!dataNodes.contains(d)){
			if(title.toLowerCase().contains(in.toLowerCase()))
				d.weight += 60;
			for(int i = 0 ; i < lowWeight.length ; i ++)
				if(lowWeight[i].equals(in))
					d.weight /= 4;
			dataNodes.add(d);
		}
		else{
			d = dataNodes.get(dataNodes.indexOf(d));
			d.weight += 1;
		}
		
	}
	
	public ArrayList<Data> getPageData(String[] words){
		return null;
	}
	
	/**
	 * Removes the html tags and their content "<" to ">" from the string
	 * @param input string
	 * @return the string without html tags
	 */
	private static String clearTags(String input){
		
		String edited = "";
		
		for(int i = 0; i < input.length() ; i++){
			if(input.charAt(i) == '<')
				while(input.charAt(i) != '>') i++;
			else
				edited += input.charAt(i);
		}
		
		return edited;
	}
	
//	private static String removeArticles(String input){
//		
//		return null;
//	}
	
	public String[] getLinks(){
		return (String[]) links.toArray();
	}
	
	public ArrayList<Data> getData(){
		return dataNodes;
	}
	
	/**
	 * Returns the source code from an weblink as a string.
	 * @param link of the webpage
	 * @return Source Code as String
	 * @throws IOException
	 */
	private static String getSource(String link){
		// Gonna go ahead and catch the IO Expection here
        try {
        	// opens a url and buffer
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
           
            // reads the buffer into string
            String inputLine;
            StringBuilder str = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                str.append(inputLine);
			in.close();

			// return the completed string
	        return str.toString();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}

        return null;

    }

	
}