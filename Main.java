import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Main {

    public static class EasyTask implements Task {

        private final HashSet<Task> dependencies = new HashSet<>();
        private final int id;

        public EasyTask(int i, Collection<Task> tasks) {
            id = i;
            dependencies.addAll(tasks);
        }

        public EasyTask(int i) {
            id = i;
        }

        private void setDependense(Collection<Task> tasks) {
            dependencies.clear();
            dependencies.addAll(tasks);
        }

        @Override
        public void execute() {
            return;
        }

        @Override
        public Collection<Task> dependencies() {
            return dependencies;
        }

        @Override
        public boolean equals(Object o) {
            return (this == o) || id == ((EasyTask) o).id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "EasyTask{" +
                    "id=" + id +
                    '}';
        }
    }

    public static void main(String[] args) {
	    TaskExecutor taskExecutor = new TaskExecutor();
	    EasyTask one = new EasyTask(1);
	    EasyTask two = new EasyTask(2);
        EasyTask three = new EasyTask(3);
        EasyTask four = new EasyTask(4);
        List<Task> depOne = List.of();
        one.setDependense(depOne);
        List<Task> depTwo = List.of(one, three);
        two.setDependense(depTwo);
        List<Task> depThree = List.of(one);
        three.setDependense(depThree);
        List<Task> depFour = List.of(one);
        four.setDependense(depFour);
        // correct input
	    List<EasyTask> list = List.of(one, two, four);
	    taskExecutor.execute(list);
	    depOne = List.of(three);
	    one.setDependense(depOne);
	    // input with circle in dependencies
	    taskExecutor.execute(list);
    }
}
