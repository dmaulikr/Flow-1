package nhacks16.flow.Main;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
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

    private int completionTokens;

    private List<FlowElement> childFlowElements = new ArrayList<>();
        // Keeps track of the current FlowElements which belong to this Flow

    private double totalTime=0;
    private int flowManagerIndex;

    /** Overloaded Flow Object constructor.
     * @param name name of the flow object being instantiated
     * @param time total time estimate of the flow object
     */
    public Flow(String name, double time) {
        this.name = name;
        this.totalTime = time;
        this.completionTokens=0;
    } // End of overload constructor


    /*~~~~~~~~~ Getters & Setters ~~~~~~~~~*/

    public List<FlowElement> getChildElements() {
        return childFlowElements;
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
    public Integer getElementCount() {
        return childFlowElements.size();
    }

    /** Gets the total estimated time for the Flow
     * @return totalTime
     */
    public double getTime() {
        return totalTime;
    }

    public String getFormattedTime() {
            // Total time includes hrs and minutes, int truncates the double
        int hrs = (int)totalTime;
        Log.d(TAG, "Hours are: " + hrs);

        int mins = (int)((totalTime-hrs)*60);
        Log.d(TAG, "Minutes are: " + mins);

        return hrs +"H "+mins+"M";
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


    public int getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(int completionTokens) {
        this.completionTokens = completionTokens;
    }

    public void addCompletionToken(){
        completionTokens++;
    }

    /* Action Methods */

    /** Searches for the parameter specified FlowElement and returns the Element's
     *  index in the Flow's children ArrayList if found
     *
     * @param element the element being searched for in the Flow
     * @return index the index position of the element in the Flow
     */
    public int findElement(FlowElement element) {
        Log.d(TAG, "The element was found at index: " + childFlowElements.indexOf(element) + " in the Flow's childFlowElements LinkedList");
        return childFlowElements.indexOf(element);
    }

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
            addToTotalTime(newElement);
        // Will receive argument from the elementDesigner for the new flowElement object
    }
    public void addToTotalTime(FlowElement e) {
        switch (e.getTimeUnits()){
            /* Must cast getTimeEst to double in order to calculate the minutes from hrs*/
            case "minutes":
                totalTime = totalTime +
                        (((double)e.getTimeEstimate()/(60)));
                break;
            case "hours":
                totalTime = totalTime +
                        (double) e.getTimeEstimate();
                break;
            default:
                break;
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
                ", flowManagerIndex=" + flowManagerIndex +
                ", name='" + name  +
                ", totalTime=" + totalTime +
                '}';
    }

    // Parcel Implementation to pass data from the Stream to the Sandbox about
    // the current flow object.
    // Still need to make method to calculate the total time for the flow based on elements
    // The order of READING and WRITING is important (Read and write in same order)
    public Flow(Parcel in) {
        String[] data = new String[4];
        // data[0] = name
        // data[1] = totalTime
        // data[2] = flowManagerIndex
        // data[3] = completionTokens

        in.readStringArray(data);
        this.name = data[0];
        this.totalTime = Double.parseDouble(data[1]);
        this.flowManagerIndex = Integer.parseInt(data[2]);
        this.completionTokens = Integer.parseInt(data[3]);
        this.childFlowElements = in.readArrayList(getClass().getClassLoader());

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
                        String.valueOf(this.flowManagerIndex),
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
