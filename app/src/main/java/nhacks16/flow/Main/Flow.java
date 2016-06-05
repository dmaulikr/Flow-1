package nhacks16.flow.Main;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/** A Flow (short for workflow) is a framework containing tasks (aka. Flow Elements)
 * arranged according to the order in which they must be performed to complete the end goal.
 * The Flow class, keeps track of the Flow Element children that belong within it and
 * serves as storage house to relay information regarding its elements to other activities,
 * in order to facilitate drawing methods and other activity functions (ie. elementDesigner, etc)
 * @author Robert Simoes
 */
public class Flow implements Parcelable{
    private static final String TAG = Flow.class.getName();
    private String name;
    private List<FlowElement> childFlowElements = new LinkedList<FlowElement>();
        // Will keep tracks of the current flowElements that belong to the Flow object

    private double totalTime;
    private int flowManagerIndex;

    /** Basic Flow Object constructor.
     * @param name name of the flow object being instantiated
     */
    public Flow(String name) {
        this.name = name;
    } // End of constructor

    /** Overloaded Flow Object constructor.
     * @param name name of the flow object being instantiated
     * @param time total time estimate of the flow object
     */
    public Flow(String name, double time) {
        this.name = name;
        this.totalTime = time;
    } // End of overload constructor


    /*~~~~~~~~~ Getters & Setters ~~~~~~~~~*/

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
    public Integer getElementCount() {
        return childFlowElements.size();
    }

    /** Gets the total estimated time for the Flow
     * @return totalTime
     */
    public double getTime() {
        if (totalTime != 0.0) {
            return totalTime;
        } else {
            totalTime = 0.0;
            return totalTime;
        }
    }

    /** Sets the totalTime for the Flow once called from calculateTime()
     * @param time desired time of the flow object
     */
    public void setTotalTime(double time){
        this.totalTime = time;
    }

    /** Get the index which this Flow belongs to in the flowManager
     *  ArrayList<Flow>
     * @return flowManagerIndex , the index of the Flow in flowManager
     */
    public int getFlowManagerIndex() {
        return flowManagerIndex;
    }
    /** Sets the index which this Flow belongs to in the flowManager
     *  ArrayList<Flow>
     * @param  flowManagerIndex , the index of the Flow in flowManager
     */

    public void setFlowManagerIndex(int flowManagerIndex) {
        this.flowManagerIndex = flowManagerIndex;
    }

    /* Action Methods */

    // Overloaded findElement Methods
    public int findElement(FlowElement element) {
        Log.d(TAG, "The element was found at index: " + childFlowElements.indexOf(element) + " in the Flow's childFlowElements LinkedList");
        return childFlowElements.indexOf(element);
    }

    public FlowElement findElement(int flowIndex) {
      return childFlowElements.get(flowIndex);
    }

    public void addElement(FlowElement newElement) {
            childFlowElements.add(newElement);
        // Will receive argument from the elementDesigner for the new flowElement object
    }


    // Parcel Implementation to pass data from the Stream to the Sandbox about
    // the current flow object.
    // Still need to make method to calculate the total time for the flow based on elements
    // The order of READING and WRITING is important (Read and write in same order)
    public Flow(Parcel in) {
        String[] data = new String[2];
        // To include: name, elementCount and totalTime;

        in.readStringArray(data);
        this.name = data[0];
        this.totalTime = Double.parseDouble(data[1]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStringArray(
                new String[] {
                        this.name,
                        String.valueOf(this.totalTime)
                }
        );
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
