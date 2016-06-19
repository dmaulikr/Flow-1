package nhacks16.flow.Main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import java.util.ArrayList;

import nhacks16.flow.R;

/** @author Robert Simoes
 */
public class ImageAdapter extends BaseAdapter {
    // references to our images
    private ArrayList<Integer> elementList;

    private int thumbCount;
    private Context mContext;

    public ImageAdapter(Context c, ArrayList<Integer> res) {
        mContext = c;
        elementList = res;
    }

    public int getCount() {
        return elementList.size();
    }

    public FlowElement getItem(int position) {
        return null;
    }

    //return the row id of the item
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                /* sets the height and width for the View—this ensures that,
                no matter the size of the drawable, each image is resized and
                cropped to fit in these dimensions, as appropriate.
                 */
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //Crops to Center if possible
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
                /* If the View passed !=null, than the ImageView is
                initialized with the recycled View object
                */
        }

        //TODO imageView throwing NPE, :(
        imageView.setImageResource(elementList.get(position));

        return imageView;
    }

    public void addOne() {
        elementList.add(R.drawable.empty_task_large);
    }

        /* TODO Determine a method by which an expandable Integer[] can be created to accommodate newly created elements
            Unfortunately, the initial implementation is with an Integer[], which has to be preset to a size,
            but... our list expands depending on the created elements
            have to decide whether we want to allocate and cap a specific number of element per flow
            or if there is a way we can dynamically change the array size, and then use this
            attach element method to notify that a new element needs to be added.
         */
}
