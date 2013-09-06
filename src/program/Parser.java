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
           JLabel resizeLabel = new JLabel();
           resizeLabel.setText("Przeskaluj Z (1%-100%):");		//u¿ywane do okreœlenia stopnia skalowania Z
           resizeText.setText("100");
           
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
				   handler.triangulate(textpane,resizeText);
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
           buttonpanel.add(resizeLabel);
           buttonpanel.add(resizeText);
           
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
              if (qName.equalsIgnoreCase("P")) {
                     point = new Point();
                     point.setId(cnt);
                     cnt++;
              }
       }

       //kiedy parser zakonczy przegladanie elementu, uruchamiana jest ta metoda
       public void endElement(String uri, String localName, String qName) throws SAXException
       {
    	   //jak znajdziemy koniec </P> ³adujemy dane x,y,z
		  if (qName.equalsIgnoreCase("P")) {
		         pointsList.add(point);
		  } else if (qName.equalsIgnoreCase("x")) {
		         point.setX(Integer.parseInt(temp));
		  } else if (qName.equalsIgnoreCase("y")) {
		         point.setY(Integer.parseInt(temp));
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
       private void triangulate(JTextComponent panel, JTextField resizeText) {
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
        	  while(it.hasNext())
        	  {
        		  width++;
        		  nextPoint = it.next();
        		  if(firstPoint.getX()!=nextPoint.getX())
        		  {
        			  difx=true;
        		  }
        		  if(firstPoint.getY()!=nextPoint.getY())
        		  {
        			  dify=true;
        		  } 
        		  if (difx && dify)
        		  {
        			  width--;
        			  break;
        		  }
              }
        	  it = pointsList.listIterator();
        	  
        	  //obliczamy d³ugoœæ
        	  length = (int) ((pointsList.get(pointsList.size()-1).getId()+1)/width);
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
							 double tempZ;
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
						  p.setZ(p.getZ()*Double.parseDouble(resizeText.getText())/100);	//skalujemy Z
					  out.write(p.toString());		//zapisz punkty do pliku .obj
					  lenght_cnt++;
				  }
				  
				  out.write("\n");
				  
				  //tempowy plik do zapisywania danych o trójk¹tach
				  path2 = System.getProperty("user.dir")+"\\triangles.txt";
	    		  FileWriter fstream2 = new FileWriter(path2);
				  BufferedWriter out2 = new BufferedWriter(fstream2);
	        	  for (i=0;i<length-1;i++)
	        	  {
	        		  for(j=0;j<width-1;j++)
	        		  {
	        			  //sprawdzamy która przek¹tna jest krótsza, któtsza bêdzie naszym edgem,
	        			  //przyj¹³em ¿e usuniêcie k¹tów >90 jako najwa¿niejsze
	        			  if( diagonalLenght( pointsList.get(width*i+j), pointsList.get(width*(i+1)+j+1) ) > diagonalLenght( pointsList.get(width*i+j+1), pointsList.get(width*(i+1)+j) ) )
	        			  {
	        				 t = new Triangle(cnt, pointsList.get(width*i+j), pointsList.get(width*(i+1)+j), pointsList.get(width*i+j+1));
	        				 out2.write(t.getTriangle());
	        				 triangleList.add(t);
	        				 cnt++;
	        				 
	        				 t = new Triangle(cnt, pointsList.get(width*i+j+1), pointsList.get(width*(i+1)+j+1), pointsList.get(width*(i+1)+j));
	        				 out2.write(t.getTriangle());
	        				 triangleList.add(t);
	        				 cnt++;
	        			  }
	        			  else
	        			  {
	        				 t = new Triangle(cnt, pointsList.get(width*i+j), pointsList.get(width*i+j+1), pointsList.get(width*(i+1)+j+1));
	        				 out2.write(t.getTriangle());
	        				 triangleList.add(t);
	        				 cnt++;
	        				 
	        				 t = new Triangle(cnt, pointsList.get(width*i+j), pointsList.get(width*(i+1)+j+1), pointsList.get(width*(i+1)+j));
	        				 out2.write(t.getTriangle());
	        				 triangleList.add(t);	        				
	        				 cnt++;	 
	        			  }
	        		  }
	        	  }
	        	out2.close();
	        	Iterator<Triangle> iteratorTriangle = triangleList.iterator();
	        	//zapisz trójk¹ty do pliku .obj
	        	while(iteratorTriangle.hasNext())
	        	{
	        		t = iteratorTriangle.next();
	        		out.write("f " + t.getP1().getId()+" "+t.getP2().getId()+" "+t.getP3().getId()+"\n");
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
			  	
				
				Connection con = null;
				PreparedStatement pst = null;
				
				start = System.currentTimeMillis();
				String url = "jdbc:mysql://localhost:3306/praktyki";
				String user = "root";
				String password = "password";
				try {
				    con = DriverManager.getConnection(url, user, password);
			     	pst = con.prepareStatement("LOAD DATA LOCAL INFILE ? INTO TABLE triangle FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' (x1,y1,z1,x2,y2,z2,x3,y3,z3)");
				    pst.setString(1,path2);   
				    pst.executeUpdate();
				
				} catch (SQLException ex) {
					ex.printStackTrace(System.err);
				    System.err.println("SQLState: " + ((SQLException)ex).getSQLState());		
				    System.err.println("Error Code: " + ((SQLException)ex).getErrorCode());		
				    System.err.println("Message: " + ex.getMessage());				
				} finally {
				    try {
				        if (pst != null) {
				            pst.close();
				        }
				        if (con != null) {
				            con.close();
				        }

				    } catch (SQLException ex) {
				    	System.out.print("bb\n");
				    }
				}
				end = System.currentTimeMillis();
				((JTextArea) panel).append("Zapisano do bazy w: " + (end-start)/1000 +"s.\n");
				
				//usun plik tempowy
				File f = new File(path2);
				f.delete();
					  
       } 
       
}