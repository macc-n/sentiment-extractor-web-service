package sentimentextractor;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/sentiment")
public class SentimentExtractorService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Entity> getSentiment(@QueryParam("text")String text) {
		SentimentExtractor se = new SentimentExtractor();
		ArrayList<Entity> sentimentList = se.getSentiment(text);
		System.out.println(sentimentList);
		return sentimentList;
	}
}
