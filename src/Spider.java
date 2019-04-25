import java.awt.*;

public class Spider{

    /*** Small grid location of spider ***/
    public Point loc;

    /*** Health of spider ***/
    public int health;

    /*** horizontal direction of spider  */
    public int sHorizontal;

    /*** vertical direction of spider */
    public int sVertical;



    public Spider(Point inloc, int inhealth, int inHorizontal,int inVertical){
        health = inhealth;
        loc = inloc;
        sHorizontal = inHorizontal;
        sVertical = inVertical;
    }

    public int contains(Point testLoc){     
            if ((loc.x == testLoc.x) &&
                (loc.y == testLoc.y)){
                return 1;
            }
        return -1;
    }
}