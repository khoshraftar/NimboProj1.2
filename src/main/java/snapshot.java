public class snapshot{
    private String type;
    private  actor actor;
    private repo repo;
    private long time;

    @Override
    public String toString() {
        return  time + " " + type + " " + actor.getId() + " " + repo.getId() + " " + actor.getLogin() + " " + repo.getName();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public actor getActor() {
        return actor;
    }

    public void setActor(actor actor) {
        this.actor = actor;
    }

    public repo getRepo() {
        return repo;
    }

    public void setRepo(repo repo) {
        this.repo = repo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
