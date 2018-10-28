package sentimentextractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.PropertiesUtils;

public class SentimentExtractor {
	
	private StanfordCoreNLP pipeline;
	private ArrayList<String> currentTokens;
	private ArrayList<Integer> currentSentimentTokens;
	private ArrayList<Entity> entities;
	private ArrayList<CoreLabel> currentLabels;
	

	
	public SentimentExtractor () {
		pipeline = new StanfordCoreNLP(
				PropertiesUtils.asProperties(
						"annotators", "tokenize, ssplit, pos, lemma, parse, ner, mention, coref, depparse, sentiment"
				));
		
		currentTokens = new ArrayList<String>();
		currentSentimentTokens = new ArrayList<Integer>();
		currentLabels = new ArrayList<CoreLabel>();
	}
	
	public ArrayList<Entity> getSentiment (String text) {
		ArrayList<Entity> sentimentList = new ArrayList<Entity>();
		EntitiesExtractor ee = new EntitiesExtractor(text);
		entities = ee.findEntities();
		
		if (entities != null && !entities.isEmpty()) {
		
			Annotation document = new Annotation(text);
			
			pipeline.annotate(document);
			
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			
			
			for(CoreMap sentence : sentences) {
				
				
				currentTokens = new ArrayList<String>();
				currentSentimentTokens = new ArrayList<Integer>();
				currentLabels = new ArrayList<CoreLabel>();
				
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					currentTokens.add(token.getString(TextAnnotation.class));
					currentLabels.add(token);
				}
				
				Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				
				TreeNAry<Data> tree = new TreeNAry<Data>();
				createSentimentTree(sentimentTree, tree);
				
				getSentimentToken(tree);
				
				SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);	
				
				for (Entity e : entities) {
					int numToken = e.getEnd();
					int maxDepth = 3;
					int currentDepth = 0;
					int timeToDepthIncrease;
					boolean pendingDepthIncrease = false;
					
					Stack<IndexedWord> s = new Stack<IndexedWord>();
					
					s.push(new IndexedWord (currentLabels.get(numToken)));
					timeToDepthIncrease = s.size();

					ArrayList<IndexedWord> visited = new ArrayList<IndexedWord> ();
					ArrayList<IndexedWord> candidatedSentimentTokens = new ArrayList<IndexedWord>();
					
					while (currentDepth < maxDepth  && !s.isEmpty()) {
						IndexedWord currentIw = s.pop();
						visited.add(currentIw);
						
						timeToDepthIncrease--;
						if (timeToDepthIncrease == 0) {
							currentDepth++;
							pendingDepthIncrease = true;
						}
						
						int currentIndex = currentIw.index() - 1;
						int sentiment = currentSentimentTokens.get(currentIndex);
						if (sentiment != 2 && sentiment != -1 && ((currentIndex < e.getStart() && currentIndex < e.getEnd()) || (currentIndex > e.getStart() && currentIndex > e.getEnd()))) {
							candidatedSentimentTokens.add(currentIw);
							// vecchio metodo
							/*
							List<IndexedWord> childList = dependencies.getChildList(currentIw);
							boolean negated = false;
							for (IndexedWord iw : childList) {
								String relation = dependencies.getEdge(currentIw, iw).getRelation().toString();
								if (relation.equals("neg")) {
									negated = true;
									break;
								}
							}
							if (negated) {
								e.setSentiment(4 - sentiment);
							} else {
								e.setSentiment(sentiment);
							}
							break;
							*/
							
						}
					
						List<IndexedWord> parentList = dependencies.getParentList(currentIw);
						List<IndexedWord> childList = dependencies.getChildList(currentIw);
						
						
						if (pendingDepthIncrease) {
							timeToDepthIncrease = s.size();
							pendingDepthIncrease = false;
						}
						for (IndexedWord iw : parentList) {
							if (!visited.contains(iw))
								s.push(iw);
						}
						for (IndexedWord iw : childList) {
							if (!visited.contains(iw))
								s.push(iw);
						}
					}

					
					int currentMinDistance = currentTokens.size() + 1;
					int currentSentiment = -1;
					int currentTokenIndex = -1;
					for (IndexedWord currentIw : candidatedSentimentTokens) {
						List<IndexedWord> childList = dependencies.getChildList(currentIw);
						boolean negated = false;
						for (IndexedWord iw : childList) {
							String relation = dependencies.getEdge(currentIw, iw).getRelation().toString();
							if (relation.equals("neg")) {
								negated = true;
								break;
							}
						}
						int distance = Math.abs(e.getEnd() - (currentIw.index() - 1));
						if (distance < currentMinDistance) {
							currentMinDistance = distance;
							if (negated) {
								currentSentiment = 4 - currentSentimentTokens.get(currentIw.index() - 1);
							} else {
								currentSentiment = currentSentimentTokens.get(currentIw.index() - 1);
							}
						}
						
						
					}
					e.setSentiment(currentSentiment);
					sentimentList.add(e);	
				}
			}
			
			// test coreference
			/*
			for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
			      //System.out.println("cc" + cc);
			      
			      //System.out.println (cc.getMentionMap());
			      Collection<Set<CorefMention>> mentions = cc.getMentionMap().values();
			      System.out.println (cc.getMentionMap().values());
			      //for (Set<CorefChain.CorefMention> cmSet : cc.getMentionMap().values()) {
			      boolean isCoref = false;
			      ArrayList<Integer> corefList = new ArrayList<Integer> ();
			      for (Set<CorefChain.CorefMention> cmSet : mentions) {
			    	  //System.out.println("cmSet" + cmSet);
			    	  
			    	  for (CorefChain.CorefMention cm : cmSet) {
			    		  //System.out.println("cm" + cm);
			    		  for (Entity e : entities) {
			    			  if ((cm.startIndex - 1) == e.getStart()) {
			    				  isCoref = true;
			    				  break;
			    			  }
			    		  }
			    		  if (isCoref) {
			    				  corefList.add(cm.startIndex - 1);
			    		  }
			    		  //break;
			    	  }
			    	  
			    	 
			    	  // break;
			      }
			      isCoref = false;
			      
			      System.out.println(corefList);
			      
			      String headLabel = "";
			      for (Entity e : entities) {
			    	  if (corefList.get(0) == e.getStart()) {
			    		  headLabel = e.getLabel();
			    		  corefList.remove(0);
			    		  break;
			    	  }
			      }	      
			}*/
		}
		
		else {
			Annotation document = new Annotation(text);
			
			pipeline.annotate(document);
			
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			
			
			for(CoreMap sentence : sentences) {
				Entity e = new Entity();
				e.setStart(-1);
				e.setEnd(-1);
				e.setUriDBpedia("");
				e.setLabel("");
				
				Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(sentimentTree);
				e.setSentiment(sentiment);
				sentimentList.add(e);
			}
			
		}
		
		return sentimentList;
	}
	
	private void getSentimentToken (TreeNAry<Data> tree) {
		List<Node<Data>> leaves = tree.getLeaves();
		for (Node<Data> l : leaves) {
			if (l.getData().getLabel().equals("like")) {
				currentSentimentTokens.add(3);
			} else {
				currentSentimentTokens.add(l.getParent().getData().getSentiment());
			}
		}
	}
	
	private void createSentimentTree (Tree tSrc, TreeNAry<Data> tDst) {
		
		if (tSrc == null)
			return;
		
		Stack<Tree> s = new Stack<Tree>();
		Stack<Node<Data>> sDst = new Stack<Node<Data>>();

		Node<Data> root = new Node<Data>();
		tDst.setRoot(root);
		
		s.push(tSrc);
		sDst.push(root);
		
		int numCurrentToken = 0;
		
		while (!s.isEmpty()) {
			Tree currentNode = s.pop();
			Node<Data> currentNodeDst = sDst.pop();
			
			int sentiment = -1;
			
			if (currentNode.label().toString().equals("like")) {
				sentiment = 3;
			} else {
				sentiment = RNNCoreAnnotations.getPredictedClass(currentNode);
			}
			
			
			if (currentNode.isLeaf()) {
				EntityTag tag = getEntityTag(numCurrentToken);
				currentNodeDst.setData(new Data(currentNode.label().toString(), sentiment, tag, numCurrentToken));
				numCurrentToken++;
			} else {
				currentNodeDst.setData(new Data(currentNode.label().toString(), sentiment, EntityTag.NA, -1));
			}
			
			
			
			List<Tree> children = currentNode.getChildrenAsList();
			Collections.reverse(children);
			
			for (Tree c : children) {
				s.push(c);
				Node<Data> n = new Node<Data>();
				currentNodeDst.addChild(n);
				sDst.push(n);
			}
		}
	}
	
	private EntityTag getEntityTag (int index) {
		
		for (Entity e : entities) {
			if (index >= e.getStart() && index <= e.getEnd()) {
				return EntityTag.ENTITY;
			}
		}
		
		return EntityTag.NA;
	}
}
