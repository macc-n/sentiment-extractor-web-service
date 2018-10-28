package sentimentextractor;


public class Data {

	private String label;
	private int sentiment;
	private EntityTag tag;
	private int numToken;
	
	public Data () {
		super ();
	}
	
	public Data (String label, int sentiment, EntityTag tag, int numToken) {
		this.label = label;
		this.sentiment = sentiment;
		this.tag = tag;
		this.numToken = numToken;
	}
	
	public void setLabel (String label) {
		this.label = label;
	}
	
	public void setSentiment (int sentiment) {
		this.sentiment = sentiment;
	}
	
	public void setTag (EntityTag tag) {
		this.tag = tag;
	}
	
	public void setNumToken (int numToken) {
		this.numToken = numToken;
	}
	
	public String getLabel () {
		return label;
	}
	
	public int getSentiment () {
		return sentiment;
	}
	
	public EntityTag getTag () {
		return tag;
	}
	
	public int getNumToken () {
		return numToken;
	}
	
	public String getSentimentStr () {
		switch (sentiment) {
			case 0:
				return "Very negative";
			case 1:
				return "Negative";
			case 2:
				return "Neutral";
			case 3:
				return "Positive";
			case 4:
				return "Very positive";
			default:
				return "N/A";
		}
			
	}
	
	public String toString () {
		return label + " " + sentiment + " " + tag;
	}
}
