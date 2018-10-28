package sentimentextractor;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
	

	private T data;
	private List<Node<T>> children;
	private Node<T> parent;
	
	
	public Node () {
		children = new ArrayList<Node<T>> ();
		parent = null;
	}
	
	public Node (T d) {
		this();
		data = d;
	}
	

	public void setChildren (List<Node<T>> c) {
		for (Node<T> child : c) {
			child.parent = this;
		}
		children = c;
	}	
	
	public void setData (T d) {
		data = d;
	}
	
	public void addChild (Node<T> child) {
		child.parent = this;
		children.add(child);
	}
	
	public List<Node<T>> getChildren () {
		return children;
	}
	
	public Node<T> getParent () {
		return parent;
	}
	
	public int getNumberOfChildren () {
		return children.size();
	}
	
	public T getData () {
		return data;
	}
	
	public boolean hasChildren () {
		return (children.size() > 0);
	}
	
	public void removeChildren () {
		children = new ArrayList<Node<T>> ();
	}
	
	public String toString () {
		return data.toString();
	}
	
	
}
