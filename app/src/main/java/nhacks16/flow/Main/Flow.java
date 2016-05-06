package nhacks16.flow.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owner on 2016-04-29.
 */
public class Flow {
    // A Flow (short for workflow) is the collection of several flowElements strung together from start to finish
    // The class itself is NOT abstract, beacause a user will need a create a new a flow object to manage all the elements and their information
    /** The Flow (short for workflow) is the major concept of the app. A Flow is a framework containing tasks or Flow Elements
     * arranged serially according to the order in which they must be performed to complete the end goal.
     * The Flow class, keeps track of the flow element children that belong within it.
     * The Flow class also serves to relays this information to the SandBoxMain
     * in order to facilitate required drawing methods and other activities activities (ie. elementDesigner, etc)
     */

    private String flowName;
    private static ArrayList<FlowElement> childFlowElements = new ArrayList<FlowElement>();
    // Will keep tracks of the current flowElements that belong to the Flow object

    private Integer numberOfElements = this.childFlowElements.size();
    //Number of flowElements in the actual Flow

    public Flow() {

    } // End of default constructor

    public Flow(String flowName) {
        this.flowName = flowName;
    } // End of constructor

    /** Getters & Setters **/
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowName() {
        return this.flowName;
    }

    public Integer getElementCount() {
        return numberOfElements;
    }

    /** Action Methods **/
    public void addElement(FlowElement newElement) {
        childFlowElements.add(newElement);
        // Will receive argument from the elementDesigner for the new flowElement object
    }
}
