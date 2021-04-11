import java.util.Collection;

public interface Task {
    void execute();
    Collection<Task> dependencies();
}
