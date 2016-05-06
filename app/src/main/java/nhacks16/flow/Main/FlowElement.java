package nhacks16.flow.Main;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Owner on 2016-04-17.
 */
public class FlowElement {
    // A flowElement is the basic TASK in the flow app.
    // At the moment, the flowElement can receive a name and time estimate from the user.
    // The element also has x and y coordinates to keep track of its location when drawing onto the main panel
    // **Eventually** A flowElement within a flow will exist in two states: INCOMPLETE/BLOCKED or COMPLETE
    // COMPLETE is indicated by the filled in flag bitmap, while INCOMPLETE is indicated by the unfilled flag bitmap

    private Bitmap bitmap; //The actual bitmap
    private int x; //X Coordinate
    private int y; //Y coordinate
    private boolean touched; //Determines whether object has been touched
    private boolean completed;
    private String elementName;
    private Double timeEstimate;
    private int id;



    public FlowElement(Integer id, String name, Double timeEst) {
        this.id = id;
        this.elementName = name;
        this.timeEstimate = timeEst;

    } //End of constructor

    /** Getter and Setter Methods: **/
    public Bitmap getBitmap(){
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
        //Accepts bitmap argument & sets the Object's
        //Bitmap to the parameter
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() {return y; }
    public void setY(int y) { this.y=y;}


    public void setTouched(boolean touched) {
        this.touched=touched;
    }
    public boolean isTouched() {
        //isTouched?
        return touched;
    }

    //## Action Methods
    public void draw(Canvas canvas) {

        //Draw
    }
}
