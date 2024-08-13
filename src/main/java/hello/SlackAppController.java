package hello;

import com.slack.api.bolt.App;

import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import jakarta.servlet.annotation.WebServlet;

// With Spring Boot 2, the imports can be a bit different
// import com.slack.api.bolt.servlet.SlackAppServlet;
// import javax.servlet.annotation.WebServlet;

@WebServlet("/slack/events")
public class SlackAppController extends SlackAppServlet {
    public SlackAppController(App app) {
        super(app);
        System.out.println("test!gg3");
    }
}
