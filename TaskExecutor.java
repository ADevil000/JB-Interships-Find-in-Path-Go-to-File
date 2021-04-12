import java.util.*;
import java.util.function.BiFunction;

public class TaskExecutor {

    private static class NodeExplanation {

        private final HashSet<Task> edgesFrom = new HashSet<>();
        private final HashSet<Task> edgesTo = new HashSet<>();
        private int waitTasks = 0; // can be used in future for waiting in thread

        public NodeExplanation(Collection<Task> edges, boolean to) {
            if (to) {
                edgesTo.addAll(edges);
                waitTasks = edges.size();
            } else {
                edgesFrom.addAll(edges);
            }
        }

        public NodeExplanation(Task task, boolean to) {
            if (to) {
                edgesTo.add(task);
                waitTasks = 1;
            } else {
                edgesFrom.add(task);
            }
        }

        public HashSet<Task> getEdgesFrom() {
            return edgesFrom;
        }

        public HashSet<Task> getEdgesTo() {
            return edgesTo;
        }

        public void addEdgesFrom(Collection<Task> tasks) {
            edgesFrom.addAll(tasks);
        }

        public void addEdgesTo(Collection<Task> tasks) {
            edgesTo.addAll(tasks);
            waitTasks = edgesTo.size();
        }

        public int getWaitTasks() {
            return waitTasks;
        }

        public void increase() {
            waitTasks++;
        }

        public void increase(int i) {
            waitTasks += i;
        }

        public void decrease() {
            waitTasks--;
        }

        public void decrease(int i) {
            waitTasks -= i;
        }

    }

    private final HashMap<Task, NodeExplanation> NODES = new HashMap<>();
    private final HashSet<Task> IN_DFS = new HashSet<>();
    private final HashSet<Task> VISITED = new HashSet<>();
    private final ArrayList<Task> PLAN = new ArrayList<>();
    private final static BiFunction<NodeExplanation, NodeExplanation, NodeExplanation> MERGING_EDGES = (a, b) -> {
        b.addEdgesFrom(a.getEdgesFrom());
        b.addEdgesTo(a.getEdgesTo());
        return b;
    };

    private void clearAll() {
        NODES.clear();
        IN_DFS.clear();
        VISITED.clear();
        PLAN.clear();
    }

    private void showGraphs() {
        System.out.println("Graph");
        show(NODES);
    }

    private void show(HashMap<Task, NodeExplanation> graph) {
        for (Map.Entry<Task, NodeExplanation> p : graph.entrySet()) {
            System.out.println(p.getKey() + ": wait = " + p.getValue().getWaitTasks() + ": ");
            System.out.println("Edges from");
            for (Task value : p.getValue().getEdgesFrom()) {
                System.out.print(value + " ");
            }
            System.out.println();
            System.out.println("Edges to");
            for (Task value : p.getValue().getEdgesTo()) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    void continueDFS(Task task, Task parent) {
        if (!VISITED.contains(task)) {
            VISITED.add(task);
            IN_DFS.add(task);
            dfs(task);
            IN_DFS.remove(task);
        } else {
            if (IN_DFS.contains(task)) {
                clearAll();
                throw new IllegalArgumentException("Given dependencies have circle, so can't be resolves. Circle has edge: " + task + " <-> " + parent);
            }
        }
    }

    private void dfs(Task task) {
        NODES.merge(task, new NodeExplanation(task.dependencies(), true), MERGING_EDGES);
        for (Task dependence : task.dependencies()) {
            NODES.merge(dependence, new NodeExplanation(task, false), MERGING_EDGES);
            continueDFS(dependence, task);
        }
        PLAN.add(task);
    }

    private void fillGraphAndPlan(final Collection<Main.EasyTask> tasks) {
        for (Task task : tasks) {
            continueDFS(task, null);
        }
    }

    public void execute(Collection<Main.EasyTask> tasks) {
        clearAll();
        fillGraphAndPlan(tasks); // add graph of all dependencies and all tasks. Fill plan which is reversed topsort.
        showGraphs();
        System.out.println("PLan");
        for (Task task : PLAN) {
            System.out.print(task + " ");
            task.execute();
        }
        System.out.println();
    }

}
