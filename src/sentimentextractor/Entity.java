package sentimentextractor;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Entity {
	
	private int start;
	private int end;
	private String uriDBpedia;
	private String label;
	private int sentiment;
	
	public Entity () {
		sentiment = -1;
	}
	
	public void setStart (int start) {
		this.start = start;
	}
	
	public void setEnd (int end) {
		this.end = end;
	}
	
	public void setUriDBpedia (String id) {
		this.uriDBpedia = id;
	}
	
	public void setLabel (String label) {
		this.label = label;
	}
	
	public void setSentiment (int sentiment) {
		this.sentiment = sentiment;
	}
	
	public int getStart () {
		return start;
	}
	
	public int getEnd () {
		return end;
	}
	
	public String getUriDBpedia () {
		return uriDBpedia;
	}
	
	public String getLabel () {
		return label;
	}
	
	public int getSentiment () {
		return sentiment;
	}
	
	@JsonIgnore
	public boolean isMultiToken () {
		if (start == end)
			return false;
		else
			return true;
	}
	
	public String toString () {
		return this.getStart() + " " + this.getEnd() + " " + this.getUriDBpedia() + " " + this.getLabel() + " " + this.getSentiment();
	}
}
