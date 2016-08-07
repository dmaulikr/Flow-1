package flowapp.flow.Main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import java.util.ArrayList;

import flowapp.flow.R;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
 * Basic image adapter for the Sandbox grid view of the flows
 */
public class ImageAdapter extends BaseAdapter {
    // references to our images
    private ArrayList<Integer> elementList;
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
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                /* sets the height and width for the View—this ensures that,
                no matter the size of the drawable, each image is resized and
                cropped to fit in these dimensions, as appropriate.
                 */
            imageView.setScaleType(ImageView.ScaleType.CENTER);
                //Crops to Center if possible
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
                /* If the View passed !=null, than the ImageView is
                initialized with the recycled View object
                */
        }
        imageView.setImageResource(elementList.get(position));

        return imageView;
    }

    /**
     * Implementation of the .add() method normally found in an ArrayAdapter
     * Adds a new drawable resource to the imageView elementList
     */
    public void addOne() {
        elementList.add(R.drawable.flag_black_48dp);
    }
}
