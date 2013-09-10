package program;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Parser extends DefaultHandler {
       private Point point;
       private String temp;
       private int cnt = 1;
       private ArrayList<Point> pointsList = new ArrayList<Point>();			//Lista punktów
       private ArrayList<Triangle> triangleList = new ArrayList<Triangle>();	//Lista trójk¹tów
       private static File file;
       private static JTextField resizeText = new JTextField(10);
       private static Parser handler;											
       
       public static void main(String args[]) throws IOException, SAXException,
       ParserConfigurationException {
           final JFrame frame = new JFrame("triangulate");
           frame.addWindowListener(new WindowAdapter() {
               public void windowClosing(WindowEvent e) {
                   System.exit(0);
               }
           });

           final JTextComponent textpane = new JTextArea();
           
           final JScrollPane pane = new JScrollPane(textpane);
           pane.setPreferredSize(new Dimension(600, 600));
           
           //pobieramy katalog w którym siê znajdujemy
           String cwd = System.getProperty("user.dir");
           final JFileChooser fileChooser = new JFileChooser(cwd);

           JButton filebutton = new JButton("Wybierz plik");
           JButton startbutton = new JButton("Start");
           
           
           //ob³uga przycisku startbutton
           startbutton.addActionListener(new ActionListener() 
           {
			   public void actionPerformed(ActionEvent e) 
			   {
				   //uruchomienie trangulacji
				   handler.triangulate(textpane);
			   }
           });
           
           //obs³uga przycisku filebutton
           filebutton.addActionListener(new ActionListener() 
           {
			   public void actionPerformed(ActionEvent e) 
			   {
				   //wybieramy plik
				   if (fileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
			           return;
			       file = fileChooser.getSelectedFile();
			       
				   final long s_time = System.currentTimeMillis();
				   
				   frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				   
				    
           		try {       			
           			SAXParserFactory spfac = SAXParserFactory.newInstance();
           			
           			//tworzymy obiekt SAXParser
					SAXParser sp = spfac.newSAXParser();
					handler = new Parser();
					
					//sparsuj plik
					((JTextArea) textpane).append("Wczytalem plik "+file.getName()+".\n");
					sp.parse(file.toString(), handler);
					long timeElapsed = System.currentTimeMillis() - s_time;
			         ((JTextArea) textpane).append("Wczytanie pliku: "+timeElapsed+" ms.\n");
			         
				} catch (ParserConfigurationException e1) {
					e1.printStackTrace();
				} catch (SAXException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
           		
				 SwingUtilities.invokeLater(new Runnable() 
				 {
				     public void run() 
				     {
				         frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				         
				     }
				  });
               }
           });
           
           //rozmieœæ elementy
           JPanel buttonpanel = new JPanel();
           buttonpanel.add(filebutton);
           buttonpanel.add(startbutton);
           
           JPanel panel = new JPanel();
           panel.setLayout(new BorderLayout());
           panel.add("North", buttonpanel);
           panel.add("Center", pane);

           frame.getContentPane().add(panel);
           frame.pack();
           frame.setVisible(true);
       }
       



       public void characters(char[] buffer, int start, int length) {
              temp = new String(buffer, start, length);
       }
       
       //kiedy parser znajdzie nowy element uruchamiana jest ta metoda
       public void startElement(String uri, String localName, 
                     String qName, Attributes attributes) throws SAXException {
              temp = "";
              //jest znaleŸliœmy znacznik <P> utwórz nowy obiekt Point i zwiêksz licznik
              if (qName.equalsIgnoreCase("GeoPoint")) {
                     point = new Point();
                     point.setId(cnt);
                     cnt++;
              }
       }

       //kiedy parser zakonczy przegladanie elementu, uruchamiana jest ta metoda
       public void endElement(String uri, String localName, String qName) throws SAXException
       {
    	   //jak znajdziemy koniec </P> ³adujemy dane x,y,z
		  if (qName.equalsIgnoreCase("GeoPoint")) {
		         pointsList.add(point);
		  } else if (qName.equalsIgnoreCase("x")) {
		         point.setX(Double.parseDouble(temp));
		  } else if (qName.equalsIgnoreCase("y")) {
		         point.setY(Double.parseDouble(temp));
		  } else if (qName.equalsIgnoreCase("z")) {
		         point.setZ(Integer.parseInt(temp));
		  }
       }
       
       
       //metoda ta zwraca odleg³oœæ miêdzy dwoma punktami
       private double diagonalLenght(Point a,  Point b)
       {
    	   return Math.sqrt(Math.pow(a.getX() - b.getX(),2)+Math.pow(a.getY() - b.getY(),2)+Math.pow(a.getZ() - b.getZ(),2));
       }
       
       //metoda ta wykonuje triangulacje, zapisy do bazy i do pliku
       private void triangulate(JTextComponent panel) {
        	  long start = System.currentTimeMillis();
              ListIterator<Point> it = pointsList.listIterator();	//iterator po punktach
              Point nextPoint;
              String path = "";
              String path2 = "";
			  boolean difx = false,dify=false;
              int width, length,i,j;
              Point firstPoint;
              cnt = 0;
              Triangle t;
              
              //sprawdzamy czy XML zawiera jakies punkty
              if(it.hasNext())
              {
	              firstPoint = it.next();
	              width=1;
	              length=1;
              }
              else
            	  return;
              
              //przegl¹damy listê a¿ znajdziemy dwie nowe wartoœci X i Y, to da nam szerokoœæ
        	  it = pointsList.listIterator();
        	  
        	  //obliczamy d³ugoœæ
        	  length = 1201;
        	  width = 1201;
        	  ((JTextArea) panel).append("D³ugoœæ siatki: "+ length+"\n");
        	  ((JTextArea) panel).append("Szerokoœæ siatki: "+ width+"\n");
        	  
        	  try 
        	  {
        		  path = System.getProperty("user.dir")+"\\out.obj";
	    		  FileWriter fstream = new FileWriter(path);
				  BufferedWriter out = new BufferedWriter(fstream);
				  
				  int lenght_cnt=0; 	//zawiera informacje w której kolumnie jesteœmy
				  while (it.hasNext() ) 
				  {
					  Point p = it.next();
					  
					  //wykrywamy punkty o ujemnych wartoœciach Z
 					  if (p.getZ()<0)
					  {
 						  //dla punktów le¿¹cych na nie zerowej kolumnie przyjmujemy wartoœc poprzedniego punktu
						  if(lenght_cnt%width!=0)
						  {
							  it.previous();
							  p.setZ(it.previous().getZ());
							  it.next();
							  it.next();
						  }
						  else		//dla zerowej kolumny szukamy nastêpnego punktu który bêdzie nieujemny
						  {
							 int przesuniecie=0;
							 int tempZ;
							 while(true)
							 {
								 if((tempZ=it.next().getZ())>=0L)
								 {
									 break;
								 }
								 przesuniecie++;
							 }
							 p.setZ(tempZ);
							 for (;przesuniecie >= 0; przesuniecie--)
								 it.previous();
						  }
					  }
					  else
						  p.setZ(p.getZ());	//skalujemy Z
					  out.write(p.toString());		//zapisz punkty do pliku .obj
					  lenght_cnt++;
				  }
				  
				  out.write("\n");
				  System.out.println(length+" "+width);
	        	  for (i=0;i<length-1;i++)
	        	  {
	        		  for(j=0;j<width-1;j++)
	        		  {
	        			  //sprawdzamy która przek¹tna jest krótsza, któtsza bêdzie naszym edgem,
	        			  //przyj¹³em ¿e usuniêcie k¹tów >90 jako najwa¿niejsze
	        			  if( diagonalLenght( pointsList.get(width*i+j), pointsList.get(width*(i+1)+j+1) ) > diagonalLenght( pointsList.get(width*i+j+1), pointsList.get(width*(i+1)+j) ) )
	        			  {
	        				 t = new Triangle(cnt, pointsList.get(width*i+j), pointsList.get(width*(i+1)+j), pointsList.get(width*i+j+1));
	        				 triangleList.add(t);
	        				 cnt++;
	        				 
	        				 t = new Triangle(cnt, pointsList.get(width*i+j+1), pointsList.get(width*(i+1)+j+1), pointsList.get(width*(i+1)+j));
	        				 triangleList.add(t);
	        				 cnt++;
	        			  }
	        			  else
	        			  {
	        				 t = new Triangle(cnt, pointsList.get(width*i+j), pointsList.get(width*i+j+1), pointsList.get(width*(i+1)+j+1));
	        				 triangleList.add(t);
	        				 cnt++;
	        				 
	        				 t = new Triangle(cnt, pointsList.get(width*i+j), pointsList.get(width*(i+1)+j+1), pointsList.get(width*(i+1)+j));
	        				 triangleList.add(t);	        				
	        				 cnt++;	 
	        			  }
	        		  }
	        	  }
	        	System.out.println("size: "+triangleList.size());
	        	Iterator<Triangle> iteratorTriangle = triangleList.iterator();
	        	//zapisz trójk¹ty do pliku .obj
	        	while(iteratorTriangle.hasNext())
	        	{
	        		t = iteratorTriangle.next();
	        		out.write("f " + t.getP1().getId()+" "+t.getP2().getId()+" "+t.getP3().getId()+"\n");
	        	}
	        	iteratorTriangle = triangleList.iterator();
	        	//zapisz trójk¹ty do pliku .obj
	        	while(iteratorTriangle.hasNext())
	        	{
	        		t = iteratorTriangle.next();
	        		
	        		Point n1,n2,n3, v1,v2;
					v1 = t.getP3().sub(t.getP1());
					v2 = t.getP2().sub(t.getP1());					
					n1 = v1.cross(v2);
					n1 = n1.normalize();
					
					v1 = t.getP1().sub(t.getP2());
					v2 = t.getP3().sub(t.getP2());
					n2 = v1.cross(v2);	
					n2 = n2.normalize();
					
					v1 = t.getP2().sub(t.getP3());
					v2 = t.getP1().sub(t.getP3());
					n3 = v1.cross(v2);	
					n3 = n3.normalize();
					
					out.write("vn "+(float)n1.getX()+" "+(float)n1.getY()+" "+(float)n1.z2+"\n");
					out.write("vn "+(float)n2.getX()+" "+(float)n2.getY()+" "+(float)n2.z2+"\n");
					out.write("vn "+(float)n3.getX()+" "+(float)n3.getY()+" "+(float)n3.z2+"\n");
	        	}
	        	
	        	
	        	
	        	out.close();
				} 
        	    catch (IOException e) 
				{
    				e.printStackTrace();
    			}
        	 
				long end = System.currentTimeMillis();
				((JTextArea) panel).append("Triangluacja: "+ (end-start) +"ms.\n");
				((JTextArea) panel).append("Zapisano w "+ path + "\n");
			  	
				
				//usun plik tempowy
				File f = new File(path2);
				f.delete();
					  
       } 
       
}