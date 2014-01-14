package Temp;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.applet.Applet;
import javax.swing.*;

class Node {
	int id;   // the subscript in nodes[]
	Color color;  // color I should print in (red=source/sink)
    double x;  // x coordinate of node in drawing
    double y;  // y coordinate of node in drawing
    String nodeLabel;//name
    EdgeList adj;  // all edges touching Node in adjacency list form
}

class Edge {
    int from;   // endpoint of start node in terms of subscript in nodes[]
    int to;     // endpoint of end node in terms of subscript in nodes[]
    int cap; // capacity of edge in graph
    int flow; // current flow on edge
    Color color; // current color of edge
    Edge(int f, int t, int l){ from = f; to = t; flow = 0; cap =l; color = Color.black;}
}

class EdgeList{
	Edge edge;  // edge to store
	EdgeList next; // pointer to rest of edge list
	EdgeList(Edge e, EdgeList next) { this.edge = e; this.next = next;}
}

//Helper Class for Graph - GraphPanel manages the drawing.
class GraphPanel extends Panel  {
	static int MAXNODES = 10000;
	static int MAXFLOW = 9999;
    Graph graph;  // graph that will be drawn in this panel, pointer to caller class
    int numNodes; // actual number of nodes
    Node nodes[] = new Node[MAXNODES];
    int numEdges;   // actual number of edges
    // edges is just an edge list - not normally the way we like to see graphs
    Edge edges[] = new Edge[2*MAXNODES];

    GraphPanel(Graph graph) { 
       numNodes = numEdges = 0;
       this.graph = graph;
       setSize(400,400); setVisible(true);
       }
       
  /*  void findFlow(int startId,int finishId){
    	int availFlow = 1;
    	while (availFlow >0){
    	 availFlow = findFlow(nodes[startId], finishId, MAXFLOW);
    	 graph.comment("Found Flow of " + availFlow);
    	 graph.comment("You need to write this part");
    	 graph.comment("Notice, arrows are directed");
    	 graph.comment("There is an oval closer \r\nto the target of the arc \r\n- a poor man's arrow.");
    	 graph.comment("As before, you can move the\r\nnodes around by dragging");
    	 graph.comment("The source and sink are red");
    	 
        }
    }   
    
    int findFlow(Node n, int finishId, int inflow){
       if (n.id == finishId) return inflow;
       int outflow = 0;
       for (EdgeList elist = n.adj; elist !=null; elist = elist.next)
       { Edge e =elist.edge;
       	 System.out.println("findFlow  for " + inflow + " from " + nodes[e.from].nodeLabel 
       	    + " to " + nodes[e.to].nodeLabel);

      }
       update();
       return outflow;	
    }
    */
    /* return index of node matching nodeLabel */
    int findNode(String nodeLabel) {
        for (int i = 0; i < numNodes; i++) {
            if (nodes[i].nodeLabel.equals(nodeLabel)) {return i;}
        }
        return addNode(nodeLabel);
    }

    /* add new node with label nodeLabel an random location, return index */
    int addNode(String nodeLabel) {
        return addNode(nodeLabel,30 + (int)(300 * Math.random()), 30 + (int)(300 * Math.random()),Color.cyan); 
    }
    
        /* add new node with label nodeLabel at loc (x,y), return index */
    int addNode(String nodeLabel,int x, int y,Color color) {
        Node n = new Node();  // create contents of array 
        n.x = x; 
        n.y = y;  
        n.color = color;
        n.nodeLabel = nodeLabel;
        nodes[numNodes] = n;
        n.id = numNodes;
        return numNodes++;
    }

    // Add edge between "from" and "to" with initial capacity cap "
    void addEdge(String from, String to, int cap) {
    	System.out.println("addEdge " + from + "->" + to);
        int fromSub = findNode(from);
        int toSub = findNode(to);
        Edge e = new Edge(fromSub,toSub, cap);
        edges[numEdges++] = e;
        nodes[fromSub].adj = new EdgeList(e,nodes[fromSub].adj);
        nodes[toSub].adj = new EdgeList(e,nodes[toSub].adj);       
    }

    Node pick;   // Node you have picked
    Image offscreen;    // Double buffering so you don't get flicker
    Dimension offscreensize;  // Size of screen
    Graphics offgraphics;     // Graphics for offscreen
    final Color labelDistanceColor = Color.gray;

    // Draw node n on graphics g knowing sizes of fonts fm
    public void paintNode(Graphics g, Node n, FontMetrics fm) {
        int x = (int) n.x;  // grab current x location
        int y = (int) n.y;  // grab current y location
        g.setColor(n.color);
        int w = fm.stringWidth(n.nodeLabel) + 10;  // width of nodeLabel
        int h = fm.getHeight() + 4;                // height of nodeLabel
        g.fillRect(x - w/2 , y - h/2 , w, h);
        g.setColor(Color.BLUE);
        g.drawRect(x - w/2 , y - h/2, w-1, h-1);
        g.drawString(n.nodeLabel, x - (w - 10) / 2, (y - (h - 4) / 2) + fm.getAscent());
    }
    
    public synchronized void update(){
      Graphics g = getGraphics();
      update(g);	
    }

    public synchronized void update(Graphics g) {
        Dimension d = getSize();  // current size of graphics  
        if ((offscreen == null) || (d.width != offscreensize.width)
                || (d.height != offscreensize.height)) {
            offscreen = createImage(d.width, d.height);
            offscreensize = d;
            offgraphics = offscreen.getGraphics();
            Font f = new Font("Helvetica", Font.BOLD, 18);
            offgraphics.setFont(f);
        }
        offgraphics.setColor(getBackground());
        offgraphics.fillRect(0, 0, d.width, d.height);
        
        // Draw each edge
        for (int i = 0; i < numEdges; i++) {
            Edge e = edges[i];
            //System.out.println("drawEdge from " + e.from);
            int x1 = (int) nodes[e.from].x;
            int y1 = (int) nodes[e.from].y;
            int x2 = (int) nodes[e.to].x;
            int y2 = (int) nodes[e.to].y;

            offgraphics.setColor(e.color);
            offgraphics.drawLine(x1,y1,x2,y2);
            int rad = 4;
            int circlex= (int)(x1+.85*(x2-x1))-rad;
            int circley=(int) (y1+.85*(y2-y1))-rad;
            offgraphics.fillOval(circlex, circley,rad*2, rad*2);
            //String nodeLabel =  String.valueOf(e.flow)+ "/" +String.valueOf(e.cap);
            String nodeLabel =  "Action";

            offgraphics.setColor(labelDistanceColor);
            offgraphics.drawString(nodeLabel, x1 + 2*(x2 - x1) / 3,
                    y1 + 2*(y2 - y1) / 3);
            offgraphics.setColor(Color.black);

        }

        FontMetrics fm = offgraphics.getFontMetrics();
        // Draw each node
        for (int i = 0; i < numNodes; i++) {
            paintNode(offgraphics, nodes[i], fm);
        }
        // put the offscreen image to the screen
        g.drawImage(offscreen, 0, 0, null);
        
    }
    
    // match clicked location with known locations of nodes to find closest node 
    // Note that node is "picked" so you can color it differently
    public synchronized boolean mouseDown(Event evt, int x, int y) {
        double bestdist = Double.MAX_VALUE;  // Distance between mouse click and closest node
        for (int i = 0; i < numNodes; i++) {
            Node n = nodes[i];
            double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
            if (dist < bestdist) {
                pick = n;
                bestdist = dist;
            }
        }
        pick.x = x;   pick.y = y;   repaint();
        return true;
    }

    // Update the coordinates of picked node and redraw */
    public synchronized boolean mouseDrag(Event evt, int x, int y) {
        pick.x = x;    pick.y = y;     repaint();
        return true;
    }
    
    //When you release the mouse, the node is no longer selected
    // In our case, I made the node fixed (so it wouldn't change automatically)
    public synchronized boolean mouseUp(Event evt, int x, int y) {
        pick.x = x;  pick.y = y;   pick = null;   repaint();
        return true;
    }
}



/* instantiate the applet
 * Somewhat odd division of responsibilities between Graph and GraphPanel
 */
public class Graph extends JApplet {
    GraphPanel panel;
    GraphPanel panel2;
    int startId;
    int finishId;
    JTextArea comments;
    
    //public void comment(String s) {comments.append(s+"\n");}
    public void init() {
    	
    	   try {
    	        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
    	            public void run() {
    	                createGUI();
    	            }
    	        });
    	    } catch (Exception e) {
    	        System.err.println("createGUI didn't successfully complete");
    	    }
    	}
    	

public void createGUI(){
	
        setSize(700,500);
        setLayout(new BorderLayout());
        panel = new GraphPanel(this);
        add(panel,BorderLayout.CENTER);
        Panel p = new Panel();
        add(p,BorderLayout.WEST);
        
        JButton button = new JButton();
		button.setName("button");
		button.setText("Next");
        
        //p.add(new Button("Next"));
        p.add(button);

        button.addActionListener(
		        new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                    	System.out.println("pressed");
                    	
                    }}
		            
		        
		);
        
        //comments = new JTextArea(" ", 10,15);
        //p.add(comments);
        panel.setSize(700,700);
        Dimension d = panel.getSize();
       // String start = "O";

    /*    if (start != null) {
            startId = panel.addNode(start,20,d.height/2,Color.red);
         }
        String finish = "S";
        if (finish != null) {
            finishId = panel.addNode(finish,d.width-20,d.height/2,Color.red);
         }
       */
        //String edges = "O-A/6,B-F/2,A-B/3,B-D/5,O-G/6,G-B/4,B-P/4,P-T/2,B-T/3,D-T/6,D-S/5,T-S/10,G-D/10,F-S/8,O-B/6,A-F/4";
        //String edges = "O-A/100,A-S/100,O-B/100,B-S/100,A-B/1";      
       // String edges = "O-s1/100,O-s2/100,O-s3/100,s1-a/1,s2-a/16,s2-c/13,s3-c/2,c-d/14,d-b/7,d-t1/4,s3-d/3,s3-t2/4,a-c/4,c-a/10,b-c/9,a-b/12,b-t1/20,t1-S/100,d-t2/5,t2-S/100";
        	  
        /*for (StringTokenizer t = new StringTokenizer(edges, ","); t.hasMoreTokens();) {
            String str = t.nextToken();
            int i = str.indexOf('-');
            if (i > 0) {
                int cap = 50;
                int j = str.indexOf('/');
                if (j > 0) {
                    cap = Integer.valueOf(str.substring(j + 1)).intValue();
                    str = str.substring(0, j);
                }
                panel.addEdge(str.substring(0, i), str.substring(i + 1), cap);
            }
        }
        */
        panel.addNode("JonesCo");
        panel.addNode("Mr.Jones");
        panel.addNode("NewCo");
        panel.addNode("FamilyTrust");
        panel.addEdge("JonesCo","Mr.Jones",0);
        panel.addEdge("NewCo", "FamilyTrust", 0);
        panel.addEdge("JonesCo", "FamilyTrust", 0);
        

        panel.update();
        panel.repaint();
    }

    public void start() {panel.repaint(); }
    public void stop() { panel.repaint();}

    public boolean action(Event evt, Object arg) {
         if ("Flow".equals(arg)) {
          	//panel.findFlow(startId,finishId);
            return true;
        }
        return false;
    }
}