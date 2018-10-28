package sentimentextractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;

public class EntitiesExtractor {
	
	private String message;
	private ArrayList<Entity> entities;
	
	public EntitiesExtractor (String m) {
		message = m;
		entities = new ArrayList<Entity>();
	}
	
	public ArrayList<Entity> findEntities () {
		try {
			String urlStr = "http://193.204.187.35:9001/swap/nel?text=" + URLEncoder.encode(message, "UTF-8");
			URL url = new URL (urlStr);
			
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String jsonStr = "";
			String temp = "";
			
			while ((temp = in.readLine()) != null) {
				jsonStr += temp;
			}
			in.close();
			
			JSONObject jsonObj = new JSONObject(jsonStr);
			
			JSONArray entitiesJsonArr = jsonObj.getJSONArray("entities");
			for (int i = 0; i < entitiesJsonArr.length(); i++) {
				Entity e = new Entity();
				e.setStart(entitiesJsonArr.getJSONObject(i).getInt("start"));
				e.setEnd(entitiesJsonArr.getJSONObject(i).getInt("end"));
				
				JSONArray conceptsJsonArr = entitiesJsonArr.getJSONObject(i).getJSONArray("concepts");
				JSONObject conceptObj = conceptsJsonArr.getJSONObject(0);
				e.setLabel(conceptObj.getString("label"));
				
				
				// converte id da wikidata a dbpedia
				String wikidataId = conceptObj.getString("id");
				e.setUriDBpedia(getDBpediaId(wikidataId));
				
				entities.add(e);
				
			}
			
			return entities;
			
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getDBpediaId (String id) {
		String queryStr = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + 
							"PREFIX wikidata: <http://www.wikidata.org/entity/>\n" +
							"SELECT ?x WHERE {?x owl:sameAs wikidata:" + id + "}";
		Query query = QueryFactory.create(queryStr);
		QueryExecution qExe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		ResultSet results = qExe.execSelect();
		while (results.hasNext()) {
			return results.next().get("?x").toString();
		}
		return null;
	}
}
