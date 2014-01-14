package interpreter;

import interpreter.assets.Assets;
import interpreter.assets.PartnershipAsset;
import interpreter.entities.Entity;
import interpreter.misc.Graph;
import interpreter.misc.Transaction;

import java.awt.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;


class Node {
	int id;   // the subscript in nodes[]
	Color color;  // color I should print in (red=source/sink)
    double x;  // x coordinate of node in drawing
    double y;  // y coordinate of node in drawing
    String nodeLabel;//name
    EdgeList adj;  // all edges touching Node in adjacency list form
    double TaxValue;
    HashMap<String,String> map = new HashMap<String,String>();
}

class Edge {
    int from;   // endpoint of start node in terms of subscript in nodes[]
    int to;     // endpoint of end node in terms of subscript in nodes[]
    //int cap; // capacity of edge in graph
    //int flow; // current flow on edge
    Color color; // current color of edge
    String label;
    Edge(int f, int t){ from = f; to = t; color = Color.red;}
    
}

class EdgeList{
	Edge edge;  // edge to store
	EdgeList next; // pointer to rest of edge list
	EdgeList(Edge e, EdgeList next) { this.edge = e; this.next = next;}
}
class Individual{
	int id;
	ArrayList<Transaction> transactions;
	public Individual(){
		transactions = new ArrayList<Transaction>();
	}
}
public class GraphModel extends Observable{
      ArrayList<Node> nodes;
      ArrayList<Edge> edges;
      ArrayList<Individual> individuals;
      Graph graph;

	
	   public GraphModel()
	   {
		   this.graph = new Graph();
		   nodes = new ArrayList<Node>();
		   edges = new ArrayList<Edge>();
		   individuals = new ArrayList<Individual>();

	   }
	   public void addNode(Node n)
	   {
		   System.out.println("inside addNode");
		   this.nodes.add(n);
	   }
	 
	public ArrayList<Node> getNodes() {
		   return nodes;
	}
	   
	public ArrayList<Edge> getEdges() {
		   return edges;
	}
	
	public void addEdge(Edge edge) {
		   System.out.println("inside addEdge");
		   this.edges.add(edge);
	       
	}
	public ArrayList<Individual> getIndividuals(){
		return this.individuals;
	}
	
	public void addIndividual(Individual ind){
		this.individuals.add(ind);
	}
	public void updateModel(Transaction transaction){
		ArrayList<Entity> nodesList = graph.getNodes();
		String from = transaction.getAction1().getFrom();
		String to = transaction.getAction1().getTo();
		System.out.println("from edge:"+from);
		System.out.println("to edge:"+ to);

		int fromNode=0;
		int toNode=0;
		
		for(int i=0;i<nodes.size();i++){
			if (nodes.get(i).nodeLabel.equals(from)){
				fromNode = i;
				System.out.println("from edge int:"+ fromNode);

			}
			if(nodes.get(i).nodeLabel.equals(to)){
				toNode = i;
				System.out.println("to edge int:"+ toNode);

			}
		}
		edges = new ArrayList<Edge>();
		Edge e1 = new Edge(fromNode,toNode);
		e1.label = transaction.getAction1().getTransferableAssets().toString();
		Edge e2 = new Edge(toNode,fromNode);
		e2.label = transaction.getAction2().getTransferableAssets().toString();
		addEdge(e1);
		addEdge(e2);
		
		for(Entity e: nodesList){
			for(Node n : nodes){
				if(e.getName().equals(n.nodeLabel)){
					n.map = new HashMap<String,String>();
					n.TaxValue = e.getTotalTax();
					Iterator<Assets> it = e.getPortfolio().iterator();
					while(it.hasNext()){
						Assets asset = ((Assets) it.next());
						if(asset.toString().equals("PartnershipAsset")){
							String temp = ((PartnershipAsset) asset).printPAsset();
							String[] t = temp.split(" ");
							String temp1 = "";
							for(int i=0;i<t.length;i++){
								temp1+=t[i]+"\n";
							}
							n.map.put("PartnershipAsset",temp1);
						}
						else{
							String s ="";
							s+="FMV:" + asset.getCurrentFMV()+"\n";
							s+="InsideBasis:" +asset.getInsideBasis();
							n.map.put(asset.toString(),s);
						}
						
					}
				}
			}
		}
	       setChanged();
	       notifyObservers();
	}
	
}
