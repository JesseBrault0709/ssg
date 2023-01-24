package components

import com.jessebrault.gcp.Component

class Greeting implements Component {

    @Override
    String render(Map<String, ?> attr, String body) {
        "<h1>${ attr.person }, ${ attr.person }!</h1>"
    }

}
