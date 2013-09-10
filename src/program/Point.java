package program;




public class Point {
	   private int id;
       private double x;	
       private double y;
       private int z;
       public double z2;
       
       public Point() {
       }

       public Point(int id,double x, double y, int z) {
              this.x = x;
              this.y = y;
              this.z = z;
              this.id = id;
       }
       
       public Point(double x, double y, double z2) {
           this.x = x;
           this.y = y;
           this.z2 = z2;
    }
       
       public int getId() {
           return id;
   		}

    public void setId(int id) {
           this.id = id;
   		}
       public double getX() {
              return x;
       }

       public void setX(double x) {
              this.x = x;
       }

       public double getY() {
              return y;
       }

       public void setY(double y) {
              this.y = y;
       }

       public int getZ() {
              return z;
       }

       public void setZ(int z) {
              this.z = z;
       }
       
       public Point sub(Point rhs)
   		{
    	   return new Point(
   			x - rhs.getX(),
   			y - rhs.getY(),
   			(double)z - rhs.getZ());
   		}
       
       public Point cross(Point rhs)
   	{
   		return new Point(
   			y*rhs.z2 - z2*rhs.getY(),
   			x*rhs.z2 - z2*rhs.getX(),
   			x*rhs.getY() - y*rhs.getX()
   		);
   	}
       public float norm()
   	{
   		return (float) Math.sqrt(this.dot(this));	
   	}
   	
   	public Point normalize()
   	{
   		return this.div(norm());
   	}
   	public Point div(float c)
	{
		return new Point(x/c, y/c, z2/c);
	}

	public double dot(Point rhs)
	{
		return x*rhs.getX() +
			y*rhs.getY() +
			z2*rhs.z2;
	}
       //metoda zwraca wspó³rzêdne punktu
       public String getXYZ() {
           StringBuffer sb = new StringBuffer();
           sb.append(getX()+","+getY()+","+getZ());
           return sb.toString();
      }
       
       //metoda zwraca odpowiednio spreparowany format wierzo³ka dla pliku obj
       public String toString() {
              StringBuffer sb = new StringBuffer();
              sb.append("v "+(int)getX()+" "+(int)getY()+" "+getZ()+"\n");
              return sb.toString();
       }
}