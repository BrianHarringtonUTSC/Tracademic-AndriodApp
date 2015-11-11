package ca.utoronto.utsc.tracademia;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> implements Serializable {
    private static final String TAG = "StudentsAdapter";

    private List<Student> mDataSet;
    private OnStudentSelectedListener mCallback;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public StudentsAdapter(List<Student> dataSet, OnStudentSelectedListener callback) {
        mDataSet = dataSet;
        mCallback = callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StudentsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v, mCallback);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        Student student = mDataSet.get(position);
        viewHolder.mainText.setText(student.getDisplayName());
        viewHolder.subText.setText("XP: " + student.getExperiencePoints() + "    CP: " + student.getChallengePoints() + "    RP: " + student.getTeachingPoints());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addItemsToList(Student... studentPoints) {
        mDataSet.addAll(Arrays.asList(studentPoints));
        notifyDataSetChanged();
    }

    public List<Student> getStudents() {
        return mDataSet;
    }
    //TODO: optimze this
    public Student getStudentByLibraryNumber(String libraryNumber) {
        for (Student student : mDataSet) {
            if (student.getLibaryNumber().equals(libraryNumber)){
                return student;
            }
        }
        return null;
    }
    public Student getStudentByStudentNumber(String studentNumber) {
        if (studentNumber != null) {
            for (Student student : mDataSet) {
                if (studentNumber.equals(student.getStudentNumber())) {
                    return student;
                }
            }
        }
        return null;
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mainText;
        private TextView subText;
        private OnStudentSelectedListener mCallback;

        public ViewHolder(View v, OnStudentSelectedListener callback) {
            super(v);
            mainText = (TextView) v.findViewById(R.id.maintext);
            subText = (TextView) v.findViewById(R.id.subtext);
            mCallback = callback;

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onStudentSelected(getAdapterPosition());
                }
            });

        }
    }
}
