package program;




public class Point {
	   private int id;
       private float x;	
       private float y;
       private float z;
       public Point() {
       }

       public Point(int id,float x, float y, int z) {
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
       public float getX() {
              return x;
       }

       public void setX(float x) {
              this.x = x;
       }

       public float getY() {
              return y;
       }

       public void setY(float y) {
              this.y = y;
       }

       public float getZ() {
              return z;
       }

       public void setZ(float z) {
              this.z = z;
       }
       
   
}