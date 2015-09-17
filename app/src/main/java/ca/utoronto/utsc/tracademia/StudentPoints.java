package ca.utoronto.utsc.tracademia;

public class StudentPoints {

    private String _id;
    private String displayNameCanonical;
    private String displayName;
    private String profileText;
    private String libraryNumber;
    private int experiencePoints;
    private int challengePoints;
    private int teachingPoints;
    private int __v;


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

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
