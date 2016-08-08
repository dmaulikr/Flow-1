package flowapp.flow.Main;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
// A flowElement is the basic TASK in the flow app.
// At the moment, the flowElement can receive a name and time estimate from the user.
 */
public class FlowElement implements Parcelable{
    private static final String TAG = TheStreamActivity.class.getName();
    public static final String DEFAULT_UNITS = "minutes";

    private String elementName;

    private int timeEstimate;

    private String timeUnits;

    private int location;

    public FlowElement(String name, int timeEst, String units) {
        // Must make sure FlowElement values cannot be null!
        this.elementName = name;
        this.timeEstimate = timeEst;
        this.timeUnits = units;
    } //End of constructor

    /*~~~~~ Getter and Setter Methods: ~~~~~*/

    /** Gets the name for flowElement
     * @return elementName
     */
    public String getElementName() {
        return elementName;
    }

    /** sets name for the FlowElement
     * @param elementName , name of element
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /** returns user's estimated completion time
     * @return timeEstimate time estimate
     */
    public int getTimeEstimate() {
        return timeEstimate;
    }

    /** sets the user's estimated time
     * @param timeEstimate
     */
    public void setTimeEstimate(int timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    /** returns the units the user entered
     * @return timeUnits units for the time
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
     * @return location
     */
    public int getLocation() {
        return location;
    }

    /** Sets the location in the flow array list
     * @param location the location to add
     */
    public void setLocation(int location) {
        this.location = location;
    }

    /**
     * Returns time units for the FlowElement in milliseconds
     * @return millis, long
     */
    public long parseTimeToMilliSecs() {
        if (timeUnits.equals("minutes")){
            return this.timeEstimate*60*1000;
        } else {
            // Else == "hours"
            return this.timeEstimate*3600*1000;
        }

    }

    /**
     * Returns time units for the FlowElement in seconds
     * @return seconds, long
     */
    public long parseTimeToSecs() {
        if (timeUnits.equals("minutes")){
            return this.timeEstimate*60;
        } else {
            // Else == "hours"
            return this.timeEstimate*3600;
        }

    }

    /* Parcel Implementation for Object Passing Between Activities! */
    public FlowElement(Parcel in) {
        String[] data = new String[3];
        // To include: Id, Estimated Time, Task Name, E

        in.readStringArray(data);
        this.elementName = data[0];
        this.timeEstimate = Integer.parseInt(data[1]);
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