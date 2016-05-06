package nhacks16.flow.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nhacks16.flow.R;

/**
 * FlowArrayAdapter is used as Custom implementation of the ArrayAdapter,
 * used to populate The Stream List View.
 */
public class FlowArrayAdapter extends ArrayAdapter<Flow> {
    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView elements;

        /* Incase you want to set images in future:
         ImageView flowIcon;
         ImageView flowElementIcon;
         */
    }
    public FlowArrayAdapter(Context context, ArrayList<Flow> Flows) {
        super(context, R.layout.flow_item_in_stream, Flows);
    } // End of constructor

    /* Each time the ListView needs to display a new row,
    it calls the adapter's getView method and expects to get back a
    View object that it can display as the row.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder holder; // view lookup cache stored in tag

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.flow_item_in_stream, parent, false);


            holder.name = (TextView) convertView.findViewById(R.id.item_flow_name);
            holder.elements = (TextView) convertView.findViewById(R.id.item_element_count);
            convertView.setTag(holder);

            /* In case you want to set images in future:
             holder.flowIcon = (ImageView) convertView.findViewById(R.id.item_flow_icon);
             holder.flowElementIcon = (ImageView) convertView.findViewById(R.id.item_element_icon);
             */
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // Get the data item for this position
        Flow flow = getItem(position);

        // Populate the data into the template view using the data from Flow object
        // getters used because a Flow's instance variables are private
        holder.name.setText(String.valueOf(flow.getFlowName()));
            // String.valueOf() otherwise Resources$NotFoundException thrown
        holder.elements.setText(String.valueOf(flow.getElementCount()));

        // Return the completed view to render on screen
        return convertView;

        /* In case you want to set custom images in future:
        holder.flowIcon.setImageResource(R.drawable.new_flow);
        holder.flowElementIcon.setImageResource(R.drawable.empty_task);

         */
    }


}
