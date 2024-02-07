package Games;

public class Bets {
    private String challengerID;
    private String challengedID;
    private long standingBet;

    public Bets(String challengerID, String challengedID, long standingBet) {
        this.challengerID = challengerID;
        this.challengedID = challengedID;
        this.standingBet = standingBet;
    }


    public String getChallengerID() {
        return challengerID;
    }

    public void setChallengerID(String challengerID) {
        this.challengerID = challengerID;
    }

    public String getChallengedID() {
        return challengedID;
    }

    public void setChallengedID(String challengedID) {
        this.challengedID = challengedID;
    }

    public long getStandingBet() {
        return standingBet;
    }

    public void setStandingBet(long standingBet) {
        this.standingBet = standingBet;
    }
}
