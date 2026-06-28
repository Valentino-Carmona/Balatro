package com.balatro.modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericHand implements Hand{
    protected List<Card> relevantCards;
    protected Score score;

    public GenericHand(){
        this.relevantCards = new ArrayList<>();
    }

    @Override
    public List<Card> getRelevatCards() {
        return this.relevantCards;
    }

    @Override
    public int getTotalPoints(){
        return this.score.getTotalPoints();
    }

    @Override
    public Score incRelevantValues(){
        Score finalScore = new Score(this.score.getPoints(), this.score.getMult(), this.score.getAddMult());
        finalScore.logEvent("base", this.handToString(), this.score.getPoints(), this.score.getMult());
        
        for (Card card : this.relevantCards) {
            int oldPoints = finalScore.getPoints();
            float oldMult = finalScore.getMult();
            card.applyScore(finalScore);
            finalScore.logEvent("card", card.getRankName() + " de " + card.getSuit(), finalScore.getPoints() - oldPoints, finalScore.getMult() - oldMult);
        }
        return finalScore;
    }

    @Override
    public void applyTarot(TarotApply tarot) {
        tarot.apply(this.score);
    }

    @Override
    public boolean equalHand(Hand hand) {
        return this.getClass() == hand.getClass();
    }

    @Override
    public String handToString() {
        return this.getClass().getSimpleName();
    }
}
