package ca.utoronto.utsc.tracademia;


public interface StudentListener {
    StudentsAdapter getStudentsAdapter();
    void onStudentSelected(int position);
    void onStudentSelected(String studentNumber);
    void onStudentInfoSubmitted();
}
