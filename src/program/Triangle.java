package program;


public class Triangle {
   private Point p1;
   private Point p2;
   private Point p3;
   
   public Triangle() { }

   public Triangle(int id, Point p1, Point p2, Point p3) 
   {
          this.setP1(p1);
          this.setP2(p2);
          this.setP3(p3);
   }
   
   //metoda zwraca wspó³rzêdne kolejnych punktów
   public String getTriangle() {
       StringBuffer sb = new StringBuffer();
       return sb.toString();
   }

	public Point getP1() {
		return p1;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

	public Point getP3() {
		return p3;
	}

	public void setP3(Point p3) {
		this.p3 = p3;
	}
       
      
}