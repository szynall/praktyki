package program;


public class Point {
	   private int id;
       private int x;
       private int y;
       private double z;
       
       public Point() {
       }

       public Point(int id,int x, int y, double z) {
              this.x = x;
              this.y = y;
              this.z = z;
              this.id = id;
       }
       
       public int getId() {
           return id;
   		}

    public void setId(int id) {
           this.id = id;
   		}
       public int getX() {
              return x;
       }

       public void setX(int x) {
              this.x = x;
       }

       public int getY() {
              return y;
       }

       public void setY(int y) {
              this.y = y;
       }

       public double getZ() {
              return z;
       }

       public void setZ(double z) {
              this.z = z;
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
              sb.append("v "+getX()+".0 "+getY()+".0 "+getZ()+"\n");
              return sb.toString();
       }
}