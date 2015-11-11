package ca.utoronto.utsc.tracademia;


public interface OnStudentSelectedListener {
    StudentsAdapter getStudentsAdapter();
    void onStudentSelected(int position);
    void onStudentSelected(String studentNumber);
}
