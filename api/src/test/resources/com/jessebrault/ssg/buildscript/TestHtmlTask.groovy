import com.jessebrault.ssg.html.HtmlOutput
import com.jessebrault.ssg.html.HtmlTask
import com.jessebrault.ssg.task.Task
import com.jessebrault.ssg.task.TaskInput
import com.jessebrault.ssg.util.Diagnostic

final class TestHtmlTask implements HtmlTask {

    @Override
    String getHtmlPath() {
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

    @Override
    TaskInput getInput() {
        return null
    }

    @Override
    HtmlOutput getOutput() {
        return null
    }

}