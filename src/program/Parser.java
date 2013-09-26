package program;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Parser extends DefaultHandler {
       private Point point;
       private String temp;
       private int cnt = 1;
       private static ArrayList<Point> pointsList = new ArrayList<Point>();			//Lista punktów
       private Point[][] points;
       private static File file;
       private static JTextField xStart = new JTextField(10);
       private static JTextField yStart = new JTextField(10);
       private static JTextField xEnd = new JTextField(10);
       private static JTextField yEnd = new JTextField(10);
       private static JTextField partSize = new JTextField(10);
       private static JTextField step = new JTextField(10);
       private static Parser handler;											
       
       public static void main(String args[]) throws IOException, SAXException,
       ParserConfigurationException {
           final JFrame frame = new JFrame("triangulate");
           frame.addWindowListener(new WindowAdapter() {
               public void windowClosing(WindowEvent e) {
                   System.exit(0);
               }
           });
           JLabel xStartLabel = new JLabel();
           xStartLabel.setText("X Start:");

           JLabel yStartLabel = new JLabel();
           yStartLabel.setText("Y Start:");

           JLabel xEndLabel = new JLabel();
           xEndLabel.setText("X End:");

           JLabel yEndLabel = new JLabel();
           yEndLabel.setText("Y End:");
           
           JLabel partSizeLabel = new JLabel();
           partSizeLabel.setText("Part Size:");
           
           JLabel stepLabel = new JLabel();
           stepLabel.setText("Step:");

           xStart.setText("0");
           yStart.setText("0");
           partSize.setText("100");
           step.setText("1");
           
           final JTextComponent textPanel = new JTextArea();
           
           final JScrollPane pane = new JScrollPane(textPanel);
           pane.setPreferredSize(new Dimension(800, 300));
           
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
				   try {
					handler.triangulate(textPanel,xStart,yStart,xEnd,yEnd,partSize,step);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
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
					((JTextArea) textPanel).append("Wczytalem plik "+file.getName()+".\n");
					sp.parse(file.toString(), handler);
					long timeElapsed = System.currentTimeMillis() - s_time;
			         ((JTextArea) textPanel).append("Wczytanie pliku: "+timeElapsed+" ms.\n");
			         setSize(textPanel,xEnd,yEnd);
			         
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
           
           JPanel panel = new JPanel();
           panel.setLayout(new BorderLayout());
           
           JPanel settingsPanel = new JPanel();
           JPanel topSettingsPanel = new JPanel();
           JPanel centerSettingsPanel = new JPanel();
           JPanel bottomSettingsPanel = new JPanel();
           settingsPanel.setLayout(new BorderLayout());
           topSettingsPanel.add(xStartLabel);
           topSettingsPanel.add(xStart);
           topSettingsPanel.add(yStartLabel);
           topSettingsPanel.add(yStart);
           topSettingsPanel.add(xEndLabel);
           topSettingsPanel.add(xEnd);
           topSettingsPanel.add(yEndLabel);
           topSettingsPanel.add(yEnd);
           
           centerSettingsPanel.add(partSizeLabel);
           centerSettingsPanel.add(partSize);
           centerSettingsPanel.add(stepLabel);
           centerSettingsPanel.add(step);
           
           bottomSettingsPanel.add(filebutton);
           bottomSettingsPanel.add(startbutton);
           
           settingsPanel.add("North", topSettingsPanel);
           settingsPanel.add("Center", centerSettingsPanel);
           settingsPanel.add("South", bottomSettingsPanel);
           
           panel.add("Center",settingsPanel);
           panel.add("South", pane);
           
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
		         point.setX(Float.parseFloat(temp));
		  } else if (qName.equalsIgnoreCase("y")) {
		         point.setY(Float.parseFloat(temp));
		  } else if (qName.equalsIgnoreCase("z")) {
		         point.setZ(Float.parseFloat(temp));
		  }
       }
       
       private static void setSize(JTextComponent panel,JTextComponent _xEnd,
   		    JTextComponent _yEnd)
       {
    	   int l = (int)Math.sqrt((pointsList.size()));
    	   xEnd.setText(Integer.toString(l));
    	   yEnd.setText(Integer.toString(l));
    	   ((JTextArea) panel).append("Wymiary "+l+"x"+l+"\n");
       }
       
       //metoda ta wykonuje triangulacje, zapisy do bazy i do pliku
       private void triangulate(JTextComponent panel,
    		   					JTextComponent _xStart,
				    		    JTextComponent _yStart,
				    		    JTextComponent _xEnd,
				    		    JTextComponent _yEnd,
				    		    JTextComponent _partSize,
				    		    JTextComponent _step) throws IOException 
       {
    	  long start = System.currentTimeMillis();
          ListIterator<Point> it = pointsList.listIterator();	//iterator po punktach
          String path = "";
          path = System.getProperty("user.dir");
          int width, lenght;
          FileOutputStream fos2 = null;
          BufferedOutputStream bos = null;
          DataOutputStream dos = null;
          cnt = 0;      
          
          
          //przegl¹damy listê a¿ znajdziemy dwie nowe wartoœci X i Y, to da nam szerokoœæ
    	  it = pointsList.listIterator();
    	  //obliczamy d³ugoœæ
    	  width = lenght = (int)Math.sqrt((pointsList.size()));
    	  points = new Point[width][lenght];
    	  
    	  ((JTextArea) panel).append("D³ugoœæ siatki: "+ lenght+"\n");
    	  ((JTextArea) panel).append("Szerokoœæ siatki: "+ width+"\n");
    	  
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
					 float tempZ;
					 while(true)
					 {
						 if((tempZ=it.next().getZ())>=0.0f)
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
			  lenght_cnt++;
		  }
		  it = pointsList.listIterator();
		  for (int i=0;i<lenght;i++)
		  {
			  for(int j=0;j<width;j++)
			  {
				  points[i][j] = it.next();
			  }
		  }
		  
		  //opcje 
		  int partSize = Integer.parseInt(_partSize.getText()), step = Integer.parseInt(_step.getText());
		  int cntWidth=0, cntLenght=0;
		  String name;
		  boolean directory = new File("out").mkdir();
		  if(directory)
			  ((JTextArea) panel).append("Utworzy³em folder out w: "+ path +"\n");
		  for (int i = Integer.parseInt(_xStart.getText()); i<Integer.parseInt(_xEnd.getText()); i+=partSize)
          {
        	  for (int j = Integer.parseInt(_yStart.getText()); j<Integer.parseInt(_yEnd.getText()); j+=partSize)
              {
        		  name = file.getName()+"__"+i+"-"+(((i+partSize)>=width)?lenght:i+partSize)+"__"+j+"-"+(((j+partSize)>=width)?width:j+partSize)+"__"+step+".dat";
        		  fos2 = new FileOutputStream(path+"\\out\\"+name);	  
        		  bos = new BufferedOutputStream(fos2);
	              dos = new DataOutputStream(bos);
	             // dos.writeInt(partSize*partSize);
		          for (int partX=i, endX=((i+partSize)>=width)?lenght:i+partSize; partX<=endX+step-1; partX+=step)//
		          {
		        	  //if(partX>endX)
		        		//  partX = endX;
		        	  for (int partY=j, endY=((j+partSize)>=width)?width:j+partSize; partY<=endY+step-1; partY+=step) //width
		              {
		        		 // if(partY>endY)
			        	//	  partY = endY;
		        		  if(partX >=lenght || partY >= width)
		        		  {
		        			  break;
		        		  }
		        		  try 
		        		  {
				            dos.writeFloat(points[partX][partY].getX());
				            dos.writeFloat(points[partX][partY].getY());
				            dos.writeFloat(points[partX][partY].getZ());
						   } catch (FileNotFoundException e) {
								e.printStackTrace();
						   }
		        		   cntWidth++;
		        		   if(partY==j)
		        		      cntLenght++;
		              }
		          }
		          dos.writeInt(cntLenght);
		          dos.writeInt((cntWidth/cntLenght));
		          dos.close();
		          cntLenght = 0; cntWidth = 0;   
              }
          }
			long end = System.currentTimeMillis();
			((JTextArea) panel).append("Przetworzono w: "+ (end-start) +"ms.\n");
			((JTextArea) panel).append("Zapisano w "+ path + "\\out\n");
		 
				  
       } 
       
}