public class snapshot{
    String type;
    actor actor;
    repo repo;
    long time;

    @Override
    public String toString() {
        return  time +" "+ type + " " + actor.id + " " + repo.id;
    }

}
