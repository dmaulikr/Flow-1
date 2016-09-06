package com.pressurelabs.flow;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

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

    private String elementName;



    private String elementNotes;

    private int timeEstimate;
        // Time is stored in Millis

    private String timeUnits;

    private int location;

    public FlowElement(String name, int timeEst, String units) {
        // Must make sure FlowElement values cannot be null!
        this.elementName = name;
        this.timeEstimate = timeEst;
        this.timeUnits = units;
        this.elementNotes = "No notes.";
    } //End of constructor

    /*~~~~~ Getter and Setter Methods: ~~~~~*/

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public void setTimeEstimate(int timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public void setTimeUnits(String timeUnits) {
        this.timeUnits = timeUnits;
    }

    public String getElementNotes() {
        return this.elementNotes;
    }

    /** Gets the name for flowElement
     * @return elementName
     */
    public String getElementName() {
        return elementName;
    }

    /** returns user's estimated completion time
     * @return timeEstimate time estimate
     */
    public int getTimeEstimate() {
        return timeEstimate;
    }

    /** returns the units the user entered
     * @return timeUnits units for the time
     */
    public String getTimeUnits() {
        return timeUnits;
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

    @Override
    public String toString() {
        return "\n#FLOW ELEMENT\n"
                + this.elementName + " "
                + "\nTime: " + this.getTimeEstimate()  + " "
                + "\nLocation " + this.getLocation() +"\n";
    }



    /* Parcel Implementation for Object Passing Between Activities! */
    private FlowElement(Parcel in) {
        String[] data = new String[4];
        // To include: Id, Estimated Time, Task Name, E

        in.readStringArray(data);
        this.elementName = data[0];
        this.timeEstimate = Integer.parseInt(data[1]);
        this.timeUnits = data[2];
        this.elementNotes = data[3];
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
                        String.valueOf(this.timeUnits),
                        this.elementNotes
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

    public void setElementNotes(String elementNotes) {
        this.elementNotes = elementNotes;
    }
}
