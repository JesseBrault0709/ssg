import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.util.Diagnostic

final class AnotherTask implements Task {

    @Override
    String getName() {
        return null
    }

    @Override
    Collection<Diagnostic> execute(Collection<Task> allTasks) {
        return null
    }

}