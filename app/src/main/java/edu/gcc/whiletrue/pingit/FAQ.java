package edu.gcc.whiletrue.pingit;

import java.util.ArrayList;

public class FAQ {
    private String category;
    ArrayList<ArrayList<String>> questionArr;

    public FAQ() {
        category = "Problem";
        questionArr.get(0).add("A question");
        questionArr.get(0).add("An answer");
    }

    public FAQ(String cat, ArrayList<ArrayList<String>> arr) {
        this.category = cat;
        this.questionArr = arr;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<ArrayList<String>> getQuestionArr() {
        return questionArr;
    }
}
