package il.co.alias;


/**
 * Created by igapo on 25.09.2018.
 */

public class Dictionary {
    private String title;
    private String level;
    private String example;
    private String numOfWords;
    private String name;

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public String getExample() {
        return example;
    }

    public String getNumOfWords() {
        return numOfWords;
    }

    public Dictionary(String title, String level, String example, String numOfWords, String name) {

        this.title = title;
        this.level = level;
        this.example = example;
        this.numOfWords = numOfWords;
        this.name = name;
    }
}
