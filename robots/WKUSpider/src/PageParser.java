import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * A tool for parsing the webpage data to collect information useful for building a search engine.
 */
public class PageParser {
	
	String url, title;
	ArrayList<String> links;
	ArrayList<Data> dataNodes;
	String preview = "";
	String[] lowWeight = {"of","a","the","and","is","in","to","all","then","these","are","its"};
	String text = "";
	String paraText;
	boolean isEmpty;

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
		String src = null;
		try {
			src = getSource(url);
		} catch (FileNotFoundException e) {
			System.out.println("Above link 404?");
			return;
		}
		
		if(src==null){
			isEmpty = true;
			return;
		}
		
		links = new ArrayList<String>();
		dataNodes = new ArrayList<Data>();
		title = getTitle(src);
		
		// for parsing
		gatherLinks(src);
		
		src = removeScriptTags(src);
		
		// instead of paragraphs and headers, we'll just grab everything in the body...
		String text = "";
		int bodyStart = src.indexOf("<body>");
		if(bodyStart == -1)
			text = clearTags(src);
		else
			text = clearTags(src.substring(bodyStart,src.length()-7));
		
		// get the paragrapha and preview texts
		preview = getDescription(src);
		paraText = gatherParaText(src);
		if(preview.equals(""))
			preview = paraText.substring(0,paraText.length() >= 295 ? 295 : paraText.length())+"...";

		// remove punctuation and symbols
		
		text = text.replaceAll("\\s+"," ");
		text = text.replaceAll("\\&nbsp", "");
		this.text = text;
		text = text.replaceAll("\\.", "");
		text = text.replaceAll("\"", "");
		text = text.replaceAll("\\:", "");
		text = text.replaceAll("\\,", "");
		text = text.replaceAll("\\;", "");
		text = text.replaceAll("\\)", "");
		text = text.replaceAll("\\(", "");
		text = text.replaceAll("\\?", "");
		text = text.replaceAll("\\#", "");

		paraText = paraText.replaceAll("\\s+"," ");
		paraText = paraText.replaceAll("\\&nbsp", "");
		paraText = paraText.replaceAll("\\.", "");
		paraText = paraText.replaceAll("\"", "");
		paraText = paraText.replaceAll("\\:", "");
		paraText = paraText.replaceAll("\\,", "");
		paraText = paraText.replaceAll("\\;", "");
		paraText = paraText.replaceAll("\\)", "");
		paraText = paraText.replaceAll("\\(", "");
		paraText = paraText.replaceAll("\\?", "");
		paraText = paraText.replaceAll("\\#", "");
		
		// put all keywords into the data structure
		String[] keywords = getKeywords(src);
		if(keywords != null)
			for(int i = 0 ; i < keywords.length ; i++)
				addData(keywords[i], 150);
		
		// the title worsd carry more weight than anything else
		String[] titleWords = title.split(" ");
		for(int i = 0 ; i < titleWords.length ; i++)
			addData(titleWords[i], 380);
		
		// the paragraph text is the meat of the document
		String[] pwords = paraText.split(" ");
		for(int w = 0 ; w < pwords.length ; w++)
			addData(pwords[w], (int)((0.0 + pwords.length - w) / pwords.length * 110.0)+10);
		
		// the remaining text... just for comprehensiveness
		String[] words = text.split(" ");
		for(int w = 0 ; w < words.length ; w++)
			addData(words[w], (int)((0.0 + words.length - w) / words.length * 30.0));
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
			if (inWKURange(link)
					&& (link.contains("php") || link.contains("htm") 
					||  link.charAt(link.length() - 1) == '/')) {
				links.add(link);
			}
			
			// move to the next href if it exists and exit the loop otherwise
			int nextIndex = code.indexOf("href=\"");
			
			if(nextIndex == -1)
				break;
			else
				code = code.substring(nextIndex+6);
		}
	}
	
	/**
	 * Returns the title of the page
	 * @param src of page
	 * @return The title of the page
	 */
	private String getTitle(String src){
		if (src.contains("<title>")) {
			String code = src.substring(src.indexOf("<title>") + 7);
			code = code.substring(0, code.indexOf("</title>"));

			return code;
		}
		else 
			return "";
	}
	
	/**
	 * Gets the page description tag
	 * @param src
	 * @return the description metadata and an empty string otherwise
	 */
	private String getDescription(String src){
		if(src.contains("name=\"description\" content=\"")){
			String code = src.substring(src
					.indexOf("name=\"description\" content=\"") + 28);
			code = code.substring(0, code.indexOf("\">"));

			return code;
		}
		else
			return "";
	}
	
	/**
	 * Gets the page keywords
	 * @param src - the page source
	 * @return an Array containing the string keywords if any
	 */
	private String[] getKeywords(String src){
//		name="description" content="
		if(src.contains("name=\"keywords\" content=\"")){
			String code = src.substring(src
					.indexOf("name=\"keywords\" content=\"") + 25);
			code = code.substring(0, code.indexOf("\">"));
			
			code = code.replaceAll(",\\s+", ",");
			code = code.replaceAll("\\s+", ",");
			
//			code.replaceAll(", ", "");

			return code.split(",");
		}
		else
			return null;
	}
	
	private String gatherParaText(String src){
		String text = "";
		
		int index = src.indexOf("<p>");
		
		if(index == -1)
			return "";
		
		String code = src.substring(index+3);
		
		while (true) {
			
			if(code.contains("</p>"))
				text += " " + clearTags(code.substring(0,code.indexOf("</p>")));

			index = code.indexOf("<p>");
			
			if(index == -1) 
				break;
			else
				code = code.substring(index+3);
		}
		
		return text;
	}
	
	/**
	 * Adds a keyword data to be stored in the database
	 * @param in - the word to add
	 * @param weight - the initial weight
	 */
	private void addData(String in, int weight){
		Data d = new Data(in);

		d.weight += weight;
		
		if(in.equals("") || in.equals(" "))
			return;
		
		if(!dataNodes.contains(d)){
			// low weight words get reduced weight
			for(int i = 0 ; i < lowWeight.length ; i ++)
				if(lowWeight[i].equals(in))
					d.weight /= 4;
			
			dataNodes.add(d);
		}
		else{
			d = dataNodes.get(dataNodes.indexOf(d));
			for(int i = 0 ; i < lowWeight.length ; i ++)
				if(lowWeight[i].equals(in))
					d.weight /= 4;
			d.weight += 5;
		}
		
	}
	
	/**
	 * Returns a string with the script and style tags and their contents removed.
	 * @param src
	 * @return
	 */
	private String removeScriptTags(String src){
		
		int start = src.indexOf("<script");
		
		while(start!=-1){
			int end = src.indexOf("</script>") + 10;
			
			String text = "";
			
			text += src.substring(0,start);
			text += src.substring(end, src.length());
			
			src = text;
			
			start = src.indexOf("<script>");
			
		}
		
		start = src.indexOf("<style");
		
		System.out.println(start);
		
		while(start!=-1){
			int end = src.indexOf("</style>") + 8;
			
			String text = "";
			
			text += src.substring(0,start);
			text += src.substring(end, src.length());
			
			src = text;
			
			start = src.indexOf("<style>");
			
		}
		
		return src;
	}
	
	/**
	 * Removes the html tags and their content "<" to ">" from the string
	 * @param input string
	 * @return the string without html tags
	 */
	private static String clearTags(String input){
		String edited = "";
		
		for(int i = 0; i < input.length() ; i++){
			if(input.charAt(i) == '<'){
				while(input.charAt(i) != '>') i++;
				edited += " ";
			}
			else
				edited += input.charAt(i);
		}
		
		return edited;
	}
	
	/**
	 * Checks if the ip of this url is within WKU's range
	 * @param url - the address of the page
	 * @return true if the ip is in WKU's range
	 */
	private boolean inWKURange(String url){
		String[] prot = getIP(url).split("\\.");
		if(prot[0].equals("161") && prot[1].equals("6"))
			return true;
		else
			return false;
	}
	
	/**
	 * Returns the ip address of given url
	 * @param address - the url
	 * @return the ip address
	 */
	public static String getIP(String address){
		try {
			return InetAddress.getByName(new URL(address).getHost()).getHostAddress();
		} catch (UnknownHostException e) {
		} catch (MalformedURLException e) {
		}
		return "";
	}
	
	
	// some getters
	public ArrayList<String> getLinks(){
		return links;
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
	private static String getSource(String link) throws FileNotFoundException{
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
			System.out.println("Problem getting page source");
		}

        return null;

    }

	
}
