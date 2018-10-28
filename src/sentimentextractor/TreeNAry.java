package sentimentextractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class TreeNAry<T> {
	
	private Node<T> root;
	
	public TreeNAry () {
		super ();
	}
	
	public Node<T> getRoot () {
		return root;
	}
	
	public void setRoot(Node<T> r) {
		root = r;
	}
	
	public boolean isEmpty () {
		return (root == null);
	}
	
	public void setChildren (Node<T> p, List<Node<T>> c) {
		p.setChildren(c);
	}
	
	public void addChild (Node<T> p, Node<T> c) {
		p.addChild(c);
	}
	
	public List<Node<T>> getChildren (Node<T> n) {
		return n.getChildren();
	}
	
	public Node<T> getParent (Node<T> n) {
		return n.getParent();
	}
	
	public int getNumberOfChildren (Node<T> n) {
		return n.getNumberOfChildren();
	}
	
	public boolean hasChildren (Node<T> n) {
		return n.hasChildren();
	}
	
	public void removeChildren (Node<T> n) {
		n.removeChildren();
	}
	
	public void preOrderVisit () {
		
		if (root == null) {
			System.out.println("Albero vuoto");
		}
		
		Stack<Node<T>> s = new Stack<Node<T>>();
		
		s.push(root);
		
		while (!s.isEmpty()) {
			Node<T> currentNode = s.pop();
			
			System.out.println(currentNode.getData());
			
			List<Node<T>> children = currentNode.getChildren();
			
			for (Node<T> c : children) {
				s.push(c);
			}
		}
	}
	
	public List<Node<T>> getLeaves () {
		
		List<Node<T>> leaves = new ArrayList<Node<T>> ();
		
		if (root == null) {
			System.out.println("Albero vuoto");
		}
		
		Stack<Node<T>> s = new Stack<Node<T>>();
		
		s.push(root);
		
		while (!s.isEmpty()) {
			Node<T> currentNode = s.pop();
			
			if (!currentNode.hasChildren())
				leaves.add(currentNode);
			
			List<Node<T>> children = currentNode.getChildren();

			for (Node<T> c : children) {
				s.push(c);
			}
		}
		
		return leaves;
	}
	
	

}
