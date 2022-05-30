package ro.pub.cs.systems.pdsd.practicaltest02.model;

public class Result {

    private String definition;

    public Result() {
        this.definition = null;
    }

    public Result(String definition){
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "WordInformation{" +
                "definition='" + definition + '\'' +
                '}';
    }

}
