package com.pressurelabs.flow;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 *
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
 * A Flow (short for workflow) is a framework containing tasks (aka. Flow Elements)
 * arranged according to the order in which they must be performed to complete the end goal.
 * The Flow class, keeps track of the Flow Element children that belong within it and
 * serves as storage house to relay information regarding its elements to other activities,
 * in order to facilitate drawing methods and other activity functions (ie. elementDesigner, etc)
 *
 */
public class Flow implements Parcelable{

    private String name;
    private int completionTokens;

    private LinkedList<FlowElement> childFlowElements;
        // Keeps track of the current FlowElements which belong to this Flow

    private int totalTime=0;
        // Time is stored in Millis

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    /** Overloaded Flow Object constructor.
     * @param name name of the flow object being instantiated
     * @param time total time estimate of the flow object
     */
    public Flow(String name, double time) {
        this.name = name;
        this.totalTime = (int) time;
        this.completionTokens=0;
        this.childFlowElements = new LinkedList<>();
        this.uuid = UUID.randomUUID().toString();
    } // End of overload constructor


    /*~~~~~~~~~ Getters & Setters ~~~~~~~~~*/


    public LinkedList<FlowElement> getChildElements() {
        return childFlowElements;
    }
    public void setChildElements(LinkedList<FlowElement> newDataSet) {
        this.childFlowElements = newDataSet;
    }

    /** Sets the name for the Flow
     * @param name desired name of the flow object
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Gets the name of the Flow
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /** Gets the current Element count for the Flow
     * @return childFlowElements.size()
     */
    public Integer getChildCount() {
        return childFlowElements.size();
    }

    /** Gets the total estimated time for the Flow
     * @return totalTime
     */
    public double getTime() {
        return totalTime;
    }

    /**
     * Returns H and M formatted time for the total Flow Time
     * @return String, time
     */
    public String getFormattedTime() {
            // Total time includes hrs and minutes, int truncates the double
        return AppUtils.buildCardViewStyleTime(this.totalTime);
    }


    @Override
    public boolean equals(Object o) {
        Flow other = (Flow) o;
        String otherUniqueId = other.getUuid();
        return this.uuid.equals(otherUniqueId);
    }

    /**
     * Returns number of completion tokens in this Flow
     * @return completionTokens, int
     */
    public int getCompletionTokens() {
        return completionTokens;
    }

    /**
     * Adds a completion token to the current flow
     */
    public void addCompletionToken(){
        completionTokens++;
    }

    /* Action Methods */

    /** Retrieves the FlowElement at the specified index position within
     *  the Flow's children ArrayList
     *
     * @param flowIndex the index position which the FlowElement is at
     * @return FlowElement the element which has been found
     */
    public FlowElement returnElement(int flowIndex) {
      return childFlowElements.get(flowIndex);
    }

    /** Adds the FlowElement received via Parameters to the Flow's
     *  children ArrayList
     *
     * @param newElement the Element being added to the Flow's ArrayList
     */
    public void addElement(FlowElement newElement) {

        childFlowElements.add(newElement);

        totalTime = totalTime + newElement.getTimeEstimate();
    }


    /**
     * Removes the supplied collection of child elements to remove from this flow.
     *
     * * On avg case:
     *      O(n) of calculating totalTime of deletedChildElement
     *                          <
     *      O(n) recalculating totalTime from remaining elements
     *
     *
     * @param deletedChildElements
     */
    public void removeChildren(LinkedList<FlowElement> deletedChildElements) {
        childFlowElements.removeAll(deletedChildElements);

        for (FlowElement removed: deletedChildElements) {
            totalTime = totalTime - removed.getTimeEstimate();
        }

    }

    /**
     * Calculates the totalTime value from all elements in
     * the current Flow
     */
    public void recalculateTotalTime() {
        totalTime=0;
        for (FlowElement element: childFlowElements) {
            totalTime=totalTime + element.getTimeEstimate();
        }
    }


    /** Overriding of original toString() because its natural
     *  implementation is no bueno!
     *
     * @return Flow
     */
    @Override
    public String toString() {
        return "Flow{" +
                "childFlowElements=" + childFlowElements +
                ", completionTokens='" + completionTokens+
                ", uuid=" + uuid +
                ", name='" + name  +
                ", totalTime=" + totalTime +
                '}';
    }

    public ArrayList<String> buildStatsExportList() {
        ArrayList<String> temp = new ArrayList<>();

        temp.add(this.getName());
        temp.add(String.valueOf(this.getChildCount()));
        temp.add(String.valueOf(this.getFormattedTime()));
        temp.add(String.valueOf(this.getCompletionTokens()));

        return temp;
    }

    // Parcel Implementation to pass data from the Stream to the Sandbox about
    // the current flow object.
    // Still need to make method to calculate the total time for the flow based on elements
    // The order of READING and WRITING is important (Read and write in same order)
    private Flow(Parcel in) {
        String[] data = new String[4];
        // data[0] = name
        // data[1] = totalTime
        // data[2] = uuid
        // data[3] = completionTokens

        in.readStringArray(data);
        this.name = data[0];
        this.totalTime = Integer.parseInt(data[1]);
        this.uuid = data[2];
        this.completionTokens = Integer.parseInt(data[3]);
        in.readList(childFlowElements,getClass().getClassLoader());

          /* Similar implementation:
              this.name = parcel.readString();
              this.totalTime= parcel.readString();
              this.flowManagerIndex = parcel.readString();
              this.childFlowElements = parcel.readArrayList(null);
         */

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        /* Similar implementation:
            dest.writeString(this.name);
            dest.writeString(this.totalTime);
            dest.writeString(this.flowManagerIndex);
            dest.writeList(childFlowElements);
         */
        destination.writeStringArray(
                new String[] {
                        this.name,
                        String.valueOf(this.totalTime),
                        this.uuid,
                        String.valueOf(this.completionTokens)

                }
        );
        destination.writeList(this.childFlowElements);
            // Writes the childFlowElements to the parcel
    }

    public static final Parcelable.Creator<Flow> CREATOR =
            new Parcelable.Creator<Flow>() {

                @Override
                public Flow createFromParcel(Parcel source) {
                    return new Flow(source);
                        //Using Parcelable constructor
                }

                @Override
                public Flow[] newArray(int size) {
                    return new Flow[size];
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
