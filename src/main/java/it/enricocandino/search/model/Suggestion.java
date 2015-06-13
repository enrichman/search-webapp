package it.enricocandino.search.model;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author Enrico Candino
 */
public class Suggestion implements Comparable<Suggestion> {

    private String word;
    private Integer weight;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Suggestion o) {
        return ObjectUtils.compare(o.getWeight(), getWeight(), true);
    }
}
