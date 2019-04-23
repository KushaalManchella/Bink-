import java.awt.*;

public class Spider{

    /*** Small grid location of spider ***/
    public Point loc;

    /*** Health of spider ***/
    public int health;



    public Spider(Point loc, int health){
        this.health = health;
        this.loc = loc;
    }
}