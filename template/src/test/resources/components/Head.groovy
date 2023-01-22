package components

import com.jessebrault.ssg.template.gspe.component.Component

class Head implements Component {

    @Override
    String render(Map<String, ?> attr, String body) {
        def b = new StringBuilder()
        b << '<head>\n'
        b << "    <title>${ attr.title }</title>\n"
        b << '</head>\n'
        b.toString()
    }

}
