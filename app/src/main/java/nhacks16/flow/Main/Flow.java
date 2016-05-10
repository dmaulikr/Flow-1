package nhacks16.flow.Main;

import android.os.Parcel;
import android.os.Parcelable;

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

    private String name;
    private static List<FlowElement> childFlowElements = new ArrayList<FlowElement>();
    // Will keep tracks of the current flowElements that belong to the Flow object

    private Integer numberOfElements = childFlowElements.size();
        //Number of flowElements no need for this.???

    private double totalTime;

    /** Basic Flow Object constructor.
     * @param name name of the flow object being instantiated
     */
    public Flow(String name) {
        this.name = name;
    } // End of constructor

    /** Overloaded Flow Object constructor.
     * @param name name of the flow object being instantiated
     * @param elementCount number of elements in the flow object
     * @param time total time estiamte of the flow object
     */
    public Flow(String name, Integer elementCount, double time) {
        this.name = name;
        this.numberOfElements = elementCount;
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
     * @return numberOfElements
     */
    public Integer getElementCount() {
        return numberOfElements;
    }

    /** Gets the total estimated time for the Flow
     * @return totalTime
     */
    public double getTime() {
        return totalTime;
    }

    /** Sets the totalTime for the Flow once called from calculateTime()
     * @param time desired time of the flow object
     */
    public void setTotalTime(double time){
        this.totalTime = time;
    }

    /* Action Methods */


    public void addElement(FlowElement newElement) {
        childFlowElements.add(newElement);
        // Will receive argument from the elementDesigner for the new flowElement object
    }


    // Parcel Implementation to pass data from the Stream to the Sandbox about
    // the current flow object.
    // Still need to make method to calculate the total time for the flow based on elements
    // The order of READING and WRITING is important (Read and write in same order)
    public Flow(Parcel in) {
        String[] data = new String[3];
        // To include: name, elementCount and totalTime;

        in.readStringArray(data);
        this.name = data[0];
        this.numberOfElements = Integer.parseInt(data[1]);
        this.totalTime = Double.parseDouble(data[2]);
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
                        String.valueOf(this.numberOfElements),
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
