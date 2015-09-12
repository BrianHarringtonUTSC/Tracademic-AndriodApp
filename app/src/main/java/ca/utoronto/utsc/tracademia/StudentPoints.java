package ca.utoronto.utsc.tracademia;

public class StudentPoints {

    private String studentNumber;
    private String studentName;
    private int experiencePoints;
    private int challengePoints;
    private int regularPoints;

    public StudentPoints(String studentNumber, String studentName, int experiencePoints, int challengePoints, int regularPoints) {
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.experiencePoints = experiencePoints;
        this.challengePoints = challengePoints;
        this.regularPoints = regularPoints;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public int getChallengePoints() {
        return challengePoints;
    }

    public int getRegularPoints() {
        return regularPoints;
    }
}
