package Model;

public class Gonderi {
    private String gonderiId;
    private String gonderen;
    private String gonderiHakkinda;
    private String gonderiResmi;

    public Gonderi() {
    }

    public Gonderi(String gonderiId, String gonderen, String gonderiHakkinda, String gonderiResmi) {
        this.gonderiId = gonderiId;
        this.gonderen = gonderen;
        this.gonderiHakkinda = gonderiHakkinda;
        this.gonderiResmi = gonderiResmi;
    }

    public String getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(String gonderiId) {
        this.gonderiId = gonderiId;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getGonderiHakkinda() {
        return gonderiHakkinda;
    }

    public void setGonderiHakkinda(String gonderiHakkinda) {
        this.gonderiHakkinda = gonderiHakkinda;
    }

    public String getGonderiResmi() {
        return gonderiResmi;
    }

    public void setGonderiResmi(String gonderiResmi) {
        this.gonderiResmi = gonderiResmi;
    }
}
