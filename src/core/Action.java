package core;

public abstract class Action {
    private String name;

    public abstract void action();

    public Action(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
