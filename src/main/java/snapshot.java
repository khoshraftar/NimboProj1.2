public class snapshot {
    String type;
    actor actor;
    rep repo;
    long time;
    String Tostring(){
        String str=String.valueOf(time)+" "+actor.id+" "+repo.id;
        return str;
    }
}
