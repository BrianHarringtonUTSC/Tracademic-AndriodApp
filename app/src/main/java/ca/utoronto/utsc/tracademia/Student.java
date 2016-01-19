package ca.utoronto.utsc.tracademia;

import java.io.Serializable;

public class Student implements Serializable {

    private String _id;
    private String username;
    private String displayNameCanonical;
    private String displayName;
    private String profileText;
    private String libraryNumber;
    private String studentNumber;
    private int experiencePoints;
    private int challengePoints;
    private int teachingPoints;

    /**
     * @return string containing the amounts of all types of points.
     */
    public String getPointsInfo() {
        return "XP: " + experiencePoints + " TP: " + teachingPoints + " CP: " + challengePoints;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public int getChallengePoints() {
        return challengePoints;
    }

    public void setChallengePoints(int challengePoints) {
        this.challengePoints = challengePoints;
    }

    public int getTeachingPoints() {
        return teachingPoints;
    }

    public void setTeachingPoints(int teachingPoints) {
        this.teachingPoints = teachingPoints;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDisplayNameCanonical() {
        return displayNameCanonical;
    }

    public void setDisplayNameCanonical(String displayNameCanonical) {
        this.displayNameCanonical = displayNameCanonical;
    }

    public String getLibaryNumber(){
        return libraryNumber;
    }

    public void setLibraryNumber(String libraryNumber){
        this.libraryNumber = libraryNumber;
    }
    public String getProfileText() {
        return profileText;
    }

    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }


    public String getStudentNumber() {
        return this.studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
