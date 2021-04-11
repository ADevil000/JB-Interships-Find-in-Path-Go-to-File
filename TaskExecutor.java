import java.util.*;
import java.util.function.BiFunction;

public class TaskExecutor {

    private static class NodeExplanation {

        private final HashSet<Task> edgesFrom = new HashSet<>();
        private final HashSet<Task> edgesTo = new HashSet<>();
        private int waitTasks = 0;

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

    private HashMap<Task, NodeExplanation> nodes;
    private HashSet<Task> inDFS;
    private HashSet<Task> visited;
    private ArrayList<Task> plan;
    private final static BiFunction<NodeExplanation, NodeExplanation, NodeExplanation> MERGING_EDGES = (a, b) -> {
        b.addEdgesFrom(a.getEdgesFrom());
        b.addEdgesTo(a.getEdgesTo());
        return b;
    };

    private void clearAll() {
        nodes.clear();
        inDFS.clear();
        visited.clear();
    }

    private void showGraphs() {
        System.out.println("Graph");
        show(nodes);
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
        if (!visited.contains(task)) {
            visited.add(task);
            inDFS.add(task);
            dfs(task);
            inDFS.remove(task);
        } else {
            if (inDFS.contains(task)) {
                clearAll();
                throw new IllegalArgumentException("Given dependencies have circle, so can't be resolves. Circle has edge: " + task + " <-> " + parent);
            }
        }
    }

    private void dfs(Task task) {
        nodes.merge(task, new NodeExplanation(task.dependencies(), true), MERGING_EDGES);
        for (Task dependence : task.dependencies()) {
            nodes.merge(dependence, new NodeExplanation(task, false), MERGING_EDGES);
            continueDFS(dependence, task);
        }
    }

    private void fillGraphs(final Collection<Main.EasyTask> tasks) {
        visited = new HashSet<>();
        inDFS = new HashSet<>();
        for (Task task : tasks) {
            continueDFS(task, null);
        }
    }

    private void addToPlan(Map.Entry<Task, NodeExplanation> node) {
        plan.add(node.getKey());
        node.getValue().decrease();
        for (Task task : node.getValue().getEdgesFrom()) {
            NodeExplanation nodeFrom = nodes.get(task);
            nodeFrom.decrease();
        }
    }

    public void execute(Collection<Main.EasyTask> tasks) {
        nodes = new HashMap<>();
        fillGraphs(tasks);
        plan = new ArrayList<>(nodes.size());
        showGraphs();
        boolean notAddedAll = true;
        while (notAddedAll) {
            notAddedAll = false;
            for (Map.Entry<Task, NodeExplanation> entry : nodes.entrySet()) {
                int waiting = entry.getValue().getWaitTasks();
                if (waiting == 0) {
                    addToPlan(entry);
                } else {
                    if (waiting > 0) {
                        notAddedAll = true;
                    }
                }
            }
        }
        System.out.println("PLan");
        for (Task task : plan) {
            System.out.print(task + " ");
            task.execute();
        }
    }

}
