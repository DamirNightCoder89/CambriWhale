package com.damirkin.cambridgewhale.word;

public class WordVar {
    private String type;
    private String example;
    private String translation;

    public WordVar(String type, String example, String translation) {
        this.type = type;
        this.example = example;
        this.translation = translation;
    }

    public String getType() {
        return type;
    }

    public String getExample() {
        return example;
    }

    public String getTranslation() {
        return translation;
    }
}
