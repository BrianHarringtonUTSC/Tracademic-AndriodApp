package ca.utoronto.utsc.tracademia;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder> {
    private static final String TAG = "PointsAdapter";

    private List<StudentPoints> mDataSet;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public PointsAdapter(List<StudentPoints> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PointsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        Log.d(TAG, "Create View Holder.");

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        StudentPoints studentPoints = mDataSet.get(position);
        viewHolder.mainText.setText(studentPoints.getDisplayName());
        viewHolder.subText.setText("XP: " + studentPoints.getExperiencePoints() + "    CP: " + studentPoints.getChallengePoints() + "    RP: " + studentPoints.getTeachingPoints());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addItemsToList(StudentPoints... studentPoints) {
        Log.d(TAG, "add items to list");
        mDataSet.addAll(Arrays.asList(studentPoints));
        notifyDataSetChanged();
    }

    //TODO: optimze this
    public StudentPoints getStudentPointsByLibraryNumber(String libraryNumber) {
//        for (StudentPoints studentPoints : mDataSet) {
//            // hello.
//        }

        return null;
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mainText;
        private TextView subText;

        public ViewHolder(View v) {
            super(v);
            mainText = (i) v.findViewById(R.id.maintext);
            subText = (TextView) v.findViewById(R.id.subtext);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });

        }
    }
}
