package nhacks16.flow.Main;

import java.util.ArrayList;

/** A Flow (short for workflow) is a framework containing tasks (aka. Flow Elements)
 * arranged according to the order in which they must be performed to complete the end goal.
 * The Flow class, keeps track of the Flow Element children that belong within it and
 * serves as storage house to relay information regarding its elements to other activities,
 * in order to facilitate drawing methods and other activity functions (ie. elementDesigner, etc)
 * @author Robert Simoes
 */
public class Flow {

    private String name;
    private static ArrayList<FlowElement> childFlowElements = new ArrayList<FlowElement>();
    // Will keep tracks of the current flowElements that belong to the Flow object

    private Integer numberOfElements = this.childFlowElements.size();
    //Number of flowElements in the actual Flow

    public Flow() {

    } // End of default constructor

    /** Flow Object constructor.
     * @param name
     */
    public Flow(String name) {
        this.name = name;
    } // End of constructor

    /* Getters & Setters */

    /** Flow Name setter
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Flow Name getter
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /** Flow Element Count getter
     * @return numberOfElements
     */
    public Integer getElementCount() {
        return numberOfElements;
    }

    /* Action Methods */


    public void addElement(FlowElement newElement) {
        childFlowElements.add(newElement);
        // Will receive argument from the elementDesigner for the new flowElement object
    }
}
