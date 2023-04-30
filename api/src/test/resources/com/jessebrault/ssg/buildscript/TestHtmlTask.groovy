import com.jessebrault.ssg.html.HtmlTask
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.util.Diagnostic

final class TestHtmlTask implements HtmlTask {

    @Override
    String getPath() {
        return null
    }

    @Override
    String getName() {
        return null
    }

    @Override
    Collection<Diagnostic> execute(Collection<Task> allTasks) {
        return null
    }

}