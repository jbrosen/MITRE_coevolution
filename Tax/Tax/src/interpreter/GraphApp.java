package interpreter;



import interpreter.assets.Annuity;
import interpreter.assets.Material;
import interpreter.assets.Share;
import interpreter.entities.Entity;
import interpreter.misc.Actions;
import interpreter.misc.Graph;
import interpreter.misc.Transaction;
import interpreter.misc.Transfer;
import interpreter.taxCode.TaxCode;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.applet.Applet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Graphics2D;

import javax.swing.*;



//Helper Class for Graph - GraphPanel manages the drawing.
class GraphPanel extends Panel  {
	//static int MAXNODES = 1000;
	//static int MAXFLOW = 9999;
    GraphApp graph;  // graph that will be drawn in this panel, pointer to caller class
    int numNodes; // actual number of nodes
    int numEdges;   // actual number of edges
    //ArrayList<Node> nodes = null;
    GraphModel model;
    JEditorPane editorPane;
    
    GraphPanel(GraphApp graphApp,GraphModel model,JTextPane editorPane) { 
	       numNodes = numEdges = 0;
	       this.graph = graphApp;
	       this.model = model;
	     //  nodes = model.getNodes();
	       //setSize(400,400); 
	       setVisible(true);
	       this.setBackground(Color.BLACK);
	       this.editorPane = editorPane;
       }
       
  
    /* return index of node matching nodeLabel */
    int findNode(String nodeLabel) {
        for (int i = 0; i < model.getNodes().size(); i++) {
            if (model.getNodes().get(i).nodeLabel.equals(nodeLabel)) {return i;}
        }
        return addNode(nodeLabel);
    }

    /* add new node with label nodeLabel an random location, return index */
    int addNode(String nodeLabel) {
        return addNode(nodeLabel,30 + (int)(300 * Math.random()), 30 + (int)(300 * Math.random()),Color.cyan);
        //return addNode(nodeLabel,30 + (int)(300 * 1.5), 30 + (int)(300 *1),Color.cyan); 

    }
    
        /* add new node with label nodeLabel at loc (x,y), return index */
    int addNode(String nodeLabel,int x, int y,Color color) {
        Node n = new Node();  // create contents of array 
        n.x = x; 
        n.y = y;  
        n.color = color;
        n.nodeLabel = nodeLabel;
        //nodes.add(n);
        model.addNode(n);
        n.id = numNodes;
        return numNodes++;
    }

    // Add edge between "from" and "to" with initial capacity cap "
    void addEdge(String from, String to, int cap) {
    	System.out.println("addEdge " + from + "->" + to);
        int fromSub = findNode(from);
        int toSub = findNode(to);
        Edge e = new Edge(fromSub,toSub);
        model.addEdge(e);
        model.getNodes().get(fromSub).adj = new EdgeList(e,model.getNodes().get(fromSub).adj);
        model.getNodes().get(toSub).adj = new EdgeList(e,model.getNodes().get(toSub).adj);       
    }

    Node pick;   // Node you have picked
    Image offscreen;    // Double buffering so you don't get flicker
    Dimension offscreensize;  // Size of screen
    Graphics offgraphics;     // Graphics for offscreen
    final Color labelDistanceColor = Color.gray;

    public void paintNode(Graphics g, Node n, FontMetrics fm) {
    	System.out.println("paint node");

        int x = (int) n.x;  // grab current x location
        int y = (int) n.y;  // grab current y location
    	System.out.println("paint node:x"+ x);

        g.setColor(n.color);
        int w = fm.stringWidth(n.nodeLabel) + 10;  // width of nodeLabel
        int h = fm.getHeight() + 4;                // height of nodeLabel
        //g.fillRect(x - w/2 , y - h/2 , w, h);
        //g.setColor(Color.BLUE);
        //g.drawRect(x - w/2 , y - h/2, w-1, h-1);
        g.fillOval(x - w/2 , y - h/2 , w, h);
        g.setColor(Color.BLUE);
        g.drawOval(x - w/2 , y - h/2, w-1, h-1);
        //Ellipse2D.Double circle = new Ellipse2D.Double(x - w/2 , y - h/2, w, h);
        //((Graphics2D) g).draw(circle);
        g.drawString(n.nodeLabel, x - (w - 10) / 2, (y - (h - 4) / 2) + fm.getAscent());
    }
    
    public synchronized void update(){
      Graphics g = getGraphics();
      //Graphics2D g2 = (Graphics2D) g;
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
        
        
        
        ArrayList<Edge> edges = model.getEdges();
        // Draw each edge
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            //System.out.println("drawEdge from " + e.from);
            int x1 = (int) model.getNodes().get(e.from).x;
            int y1 = (int) model.getNodes().get(e.from).y;
            int x2 = (int) model.getNodes().get(e.to).x;
            int y2 = (int) model.getNodes().get(e.to).y;

            if(i==0){
            	x1-=4;y1-=4;x2-=4;y2-=4;
            }
            else{
            	x1+=4;y1+=4;x2+=4;y2+=4;

            }
            offgraphics.setColor(e.color);
            offgraphics.drawLine(x1,y1,x2,y2);
            int rad = 4;
            int circlex= (int)(x1+.85*(x2-x1))-rad;
            int circley=(int) (y1+.85*(y2-y1))-rad;
            //offgraphics.fillOval(circlex, circley,rad*2, rad*2);
            //String nodeLabel =  String.valueOf(e.flow)+ "/" +String.valueOf(e.cap);
            String nodeLabel =  e.label;

            offgraphics.setColor(labelDistanceColor);
            offgraphics.drawString(nodeLabel, x1 + 2*(x2 - x1) / 3,
                    y1 + 2*(y2 - y1) / 3);
            offgraphics.setColor(Color.black);

        }

        FontMetrics fm = offgraphics.getFontMetrics();
        // Draw each node
        for (int i = 0; i < model.getNodes().size(); i++) {
            paintNode(offgraphics, model.getNodes().get(i), fm);
        }
        // put the offscreen image to the screen
        g.drawImage(offscreen, 0, 0, null);
        
    }
    
    // match clicked location with known locations of nodes to find closest node 
    // Note that node is "picked" so you can color it differently
    public synchronized boolean mouseDown(Event evt, int x, int y) {
    	
        double bestdist = Double.MAX_VALUE;  // Distance between mouse click and closest node
        for (int i = 0; i < model.getNodes().size(); i++) {
            Node n = model.getNodes().get(i);
            double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
            if (dist < bestdist) {
                pick = n;
                bestdist = dist;
            }
        }
        pick.x = x;   pick.y = y;   
        repaint();
        
        String s = "";
        s+="*Total Tax: "+ pick.TaxValue;
        s+="\n";
        s+="Portfolio:\n";
        HashMap<String,String> map = pick.map;
        for(String name:map.keySet()){
        	s+="*"+ name + "\n";
        	s+=" "+ map.get(name);
        	s+="\n";
        }
        
        editorPane.setText(s);
        
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
public class GraphApp extends JApplet implements Observer {
    GraphPanel panel;
    
    private ArrayList<Transaction> trans;
    private ArrayList<Transaction> tempTrans;

	private Graph graph = null;
	private ArrayList<Entity> nodesList = null;
	private Transfer transfer = null;
    private GraphModel model = null;
	public JTextPane tp;
	//public static JTextPane editorPaneEdges;
    private BufferedReader in;
    public ArrayList<Transaction> transactionHistory;
    
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
    	
    public ArrayList<Transaction> getTrans(){
        return model.getIndividuals().remove(0).transactions;
    }
    public void createGUI(){
    	 model = new GraphModel();
         model.addObserver(this);
         trans = new ArrayList<Transaction>();
         tempTrans = new ArrayList<Transaction>();

         this.graph = new Graph();
         this.nodesList = graph.getNodes();
         this.transfer = new Transfer(nodesList, new TaxCode());
         this.transactionHistory = new ArrayList<Transaction>();
         
         try {
			 in = new BufferedReader(new FileReader("/Users/Badar/MITRE_ArtificialStepUpBasisTransactions/code/code/Tax/Tax/src/interpreter/temp"));
			 String line = null;
			 ArrayList<String> tline = new ArrayList<String>();
			 while((line = in.readLine())!=null){
				if(!line.isEmpty()){
					 tline.add(line);
				 }
				 else{
					 //System.out.println(tline.size());
					 graph = new Graph();
					 graph.createAction(tline);
					 
				 	 ArrayList<Transaction> transactionList = graph.getTransactions();
					 Individual ind = new Individual();
					 ind.transactions.addAll(transactionList);
					 tline = new ArrayList<String>();
					 System.out.println("size of transactionList:" + transactionList.size());
					 model.addIndividual(ind);
				 }
			 }
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
         
        System.out.println("SIZE: " + model.getIndividuals().size());
        this.tempTrans = model.getIndividuals().remove(0).transactions;
        System.out.println("SIZE of temp: " + tempTrans.size());
       

         Share share = new Share(5);
         Annuity an1 = new Annuity(1000,30);
         Material m1 = new Material(40,"PC",1);
         Annuity ann = new Annuity(40,3);
         Actions a1 = new Actions("FamilyTrust","NewCo",an1);
         Actions a2 = new Actions("NewCo","FamilyTrust",share);
         Actions a3 = new Actions("NewCo","JonesCo",m1);
         Actions a4 = new Actions("JonesCo","NewCo",share);
         Actions a5 = new Actions("JonesCo","Jones",share);
         Actions a6 = new Actions("Jones","JonesCo",ann);
         
         Transaction t1 = new Transaction(a1,a2);
         Transaction t2 = new Transaction(a3,a4);
         Transaction t3 = new Transaction(a5,a6);

         trans.add(t1);
         trans.add(t2);
         trans.add(t3);
         
       
    	
        setSize(700,500);
        //setLayout(new BorderLayout());
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        Panel p1 = new Panel(new BorderLayout());
        p1.setBackground(Color.gray);
        JTextPane editorPaneNodes = new JTextPane();
        editorPaneNodes.setEditable(false);
        //editorPaneNodes.setSize(200,300);
        //editorPaneNodes.setPreferredSize(new Dimension(200,300));
        //p1.setSize(200, 300);
        
        //editorPaneNodes.setBackground(Color.cyan);
        p1.add(editorPaneNodes,BorderLayout.CENTER);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 1.0;

        c.gridwidth = 1;
        c.gridheight = 1;
        //c.ipadx = 100;      //make this component tall
        //c.ipady = 300;      //make this component tall
        //c.anchor = GridBagConstraints.LAST_LINE_END; //bottom of space
        add(p1, c);
        
        
        Panel p2 = new Panel();
        p2.setBackground(Color.cyan);
        JTextPane editorPaneEdges = new JTextPane();
        editorPaneEdges.setEditable(false);
        //editorPane1.setSize(200, 100);
        //editorPaneEdges.setBackground(Color.gray);
        p2.add(editorPaneEdges);
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight =1;
         //c.ipadx =100;      //make this component tall
         //c.ipady = 200;      //make this component tall
        //c.anchor = GridBagConstraints.LAST_LINE_END; //bottom of space

        add(p2, c);
        

        

 		

        Panel p4 = new Panel(new BorderLayout());
        //p4.setBackground(Color.green);
        JButton button = new JButton();
 		button.setName("button");
 		button.setText("Next");
 		
 		JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS ));

        box.add(button);
        box.add(Box.createHorizontalGlue());

        
 		
        p4.add(box,BorderLayout.PAGE_START);
 		//p4.add(button,BorderLayout.PAGE_START);
 		tp = new JTextPane();
 		tp.setEditable(false);
 		tp.setBackground(Color.MAGENTA);
 		JScrollPane scrollPane = new JScrollPane(tp);
 		p4.add(scrollPane, BorderLayout.CENTER);
 		p4.add(tp,BorderLayout.CENTER);
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        c.gridheight =1;
        //c.ipadx = 100;      //make this component tall
        //c.ipady = 200;      //make this component tall
        //c.anchor = GridBagConstraints.LAST_LINE_END; //bottom of space
        add(p4, c);
        
        
        Panel p5 = new Panel();
        p5.setBackground(Color.LIGHT_GRAY);
 		JTextPane tp1 = new JTextPane();
 		tp1.setEditable(false);
        p5.add(tp1);
        
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight =1;
        //c.ipadx = 100;      //make this component tall
        //c.ipady = 150;      //make this component tall
        //c.anchor = GridBagConstraints.LAST_LINE_END; //bottom of space
        add(p5, c);
        JButton button5 = new JButton();
        
        
        panel = new GraphPanel(this,model,editorPaneNodes);
        //panel.setSize(500, 500);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        c.gridheight = 2;
        //c.ipadx = 500;      //make this component tall
        //c.ipady = 500;      //make this component tall
        //c.anchor = GridBagConstraints.LAST_LINE_END; //bottom of space
        add(panel, c);
        
        
        
        
        /*Panel p1 = new Panel();
        add (p1,BorderLayout.EAST);
        this.editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setSize(200, 100);
        //editorPane.setBackground(Color.gray);
        p1.add(editorPane); 
       
        
        Panel p = new Panel();
        add(p,BorderLayout.WEST);
        JButton button = new JButton();
		button.setName("button");
		button.setText("Next");
		
		p.add(button);
        
        panel = new GraphPanel(this,model,editorPane);
        add(panel,BorderLayout.CENTER);
        
       */
        //add(panel,c);

        button.addActionListener(
		        new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                    	System.out.println("pressed");
                    	if(tempTrans.isEmpty()){
                    		tempTrans = getTrans();
                    	}
	                    Transaction transaction = tempTrans.remove(0);
						transactionHistory.add(transaction);
                    	System.out.println("pressed:"+ transaction.toString());
                    	String s ="";
                    	for(int i=0;i<transactionHistory.size();i++){
                    		
                    		s += "Individual " + Integer.toString(i)+":" + transactionHistory.get(i).toString() + "\n";
                    	}
                    	tp.setText(s);
                    	transfer.doTransfer(transaction);
	                    model.updateModel(transaction);
                    	
                   /* 	
                    if(trans.size()!=0){
                    	Transaction transaction = trans.remove(0);
                    	transactionHistory.add(transaction);
                    	System.out.println("pressed:"+ transaction.toString());
                    	String s ="";
                    	for(int i=0;i<transactionHistory.size();i++){
                    		
                    		s += "Individual " + Integer.toString(i)+":" + transactionHistory.get(i).toString() + "\n";
                    	}
                    	tp.setText(s);
                    	
                    	transfer.transfer(transaction);
                    	model.updateModel(transaction);
                    	
                    	
                    }*/
                    }}            
		        
		);
        
    
        //panel.setSize(900,700);
  
       
        /*panel.addNode("JonesCo");
        panel.addNode("Jones");
        panel.addNode("NewCo");
        panel.addNode("FamilyTrust");*/
        //panel.addEdge("JonesCo","Mr.Jones",0);
        //panel.addEdge("NewCo", "FamilyTrust", 0);
        //panel.addEdge("JonesCo", "FamilyTrust", 0);
        //panel.addEdge("FamilyTrust", "JonesCo", 0);

       ArrayList<Entity> nodes = graph.getNodes();
        for(Entity e : nodes){
        	panel.addNode(e.getName());
        }

        panel.update();
        panel.repaint();
        
    }

    public void start() {panel.repaint();}
    
    public void stop() {panel.repaint();}
    	
    


    @Override
    public void update(Observable obs, Object obj)
    {
       if (obs == model)
       {
           System.out.println("got response");
           ArrayList<Node> n = model.getNodes();
           for(int i=0;i<n.size();i++)
        	   System.out.println(n.get(i).map.keySet());
           panel.repaint();
       }
    }
    
	

  
}
