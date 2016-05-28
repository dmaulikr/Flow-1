package nhacks16.flow.Main;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Owner on 2016-04-17.
 */
public class FlowElement implements Parcelable{

    public static final String DEFAULT_UNITS = "minutes";
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

    private String timeUnits;

    private int flowIndex;
    private String gsonKey;

    public FlowElement() {
    } //End of Default Constructor

    public FlowElement(String name, Double timeEst, String units) {
        // Must make sure FlowElement values cannot be null!
        this.elementName = name;
        this.timeEstimate = timeEst;
        this.timeUnits = units;
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

    /** Gets the name for flowElement
     * @return elementName
     */
    public String getElementName() {
        return elementName;
    }

    /** sets name for the FlowElement
     * @param elementName
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /** returns user's estimated completion time
     * @return timeEstimate
     */
    public Double getTimeEstimate() {
        return timeEstimate;
    }

    /** sets the user's estimated time
     * @param timeEstimate
     */
    public void setTimeEstimate(Double timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    /** returns the units the user entered
     * @return timeUnits
     */
    public String getTimeUnits() {
        return timeUnits;
    }

    /** sets the time units for a Flow Element
     * @param timeUnits
     */
    public void setTimeUnits(String timeUnits) {
        this.timeUnits = timeUnits;
    }

    /**
     * @return flowIndex
     */
    public int getFlowIndex() {
        return flowIndex;
    }

    /**
     * @param flowIndex
     */
    public void setFlowIndex(int flowIndex) {
        this.flowIndex = flowIndex;
    }

    /** sets the GSON Key for Identification
     *
     * @param gsonKey
     */
    public void setGsonKey(String gsonKey) {
        this.gsonKey = gsonKey;
    }

    /** gets the GSON Key for Identification
     *
     * @return gsonKey
     */
    public String getGsonKey() {
        return gsonKey;
    }



    //## Action Methods
    public void draw(Canvas canvas) {

        //Draw
    }


    // Parcel Implementation to Objects between Activities
    public FlowElement(Parcel in) {
        String[] data = new String[3];
        // To include: Id, Estimated Time, Task Name, E

        in.readStringArray(data);
        this.elementName = data[0];
        this.timeEstimate = Double.parseDouble(data[1]);
        this.timeUnits = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStringArray(
                new String[] {
                        this.elementName,
                        String.valueOf(this.timeEstimate),
                        String.valueOf(this.timeUnits)
                }
        );
    }

    public static final Parcelable.Creator<FlowElement> CREATOR =
            new Parcelable.Creator<FlowElement>() {

                @Override
                public FlowElement createFromParcel(Parcel source) {
                    return new FlowElement(source);
                    //Using Parcelable constructor
                }

                @Override
                public FlowElement[] newArray(int size) {
                    return new FlowElement[size];
                }
            };

    /* How to use:
        ~ SENDING ACTIVITY ~
        Flow obj = new Flow("bah");
        Intent i = new Intent(this, recievingActivity.class);
        i.putExtra("userTag",obj);
        startActivity(i)
       -------------------------------------
       ~ RECEIVING ACTIVITY ~
       Flow myObj = getIntent().getParcelableExtra("userTag");
     */
}


/*
FOR USE LATER
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
 */