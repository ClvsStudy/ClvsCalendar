package hello;

import com.slack.api.bolt.App;
import com.slack.api.methods.response.views.ViewsPublishResponse;
import com.slack.api.model.event.AppHomeOpenedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.slack.api.bolt.jetty.SlackAppServer;

import hello.AppConfig.EnvVariableName;
import static com.slack.api.bolt.AppConfig.EnvVariableName.SLACK_SIGNING_SECRET;
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.view;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import com.slack.api.model.view.View;
import java.time.ZonedDateTime;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.view.Views.*;
@Configuration
public class SlackApp {
    @Bean
    public App initSlackApp() {

        App app = new App();
        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {

            var appHomeView = view(view -> view
                    .type("home")
                    .blocks(asBlocks(
                            section(section -> section.text(markdownText(mt -> mt.text("프로젝트를 시작하시나요?* :tada:")))),
                            divider(),
                            section(section -> section.text(markdownText(mt -> mt.text(
                                    "프로젝트 캘린더를 통해 프로젝트를 관리해보세요!")))),
                            actions(actions -> actions
                                    .elements(asElements(
                                            button(b -> b.text(plainText(pt -> pt.text("프로젝트 추가"))).value("button1").actionId("button_1"))
                                    ))
                            ),
                            actions(actions -> actions
                                    .elements(asElements(
                                        datePicker(d -> d.actionId("datepicker1"))
                                    ))
                            ))
            ));
    /*
         "type": "datepicker",
      "action_id": "datepicker123",
      "initial_date": "1990-04-28",
      "placeholder": {
        "type": "plain_text",
        "text": "Select a date"
    */

            if (payload.getEvent().getView() == null) {
                System.out.println("check1");
                ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                        .userId(payload.getEvent().getUser())
                        .view(appHomeView)
                );
            } else {
                System.out.println("check2");
                ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                        .userId(payload.getEvent().getUser())
                        .hash(payload.getEvent().getView().getHash()) // To safeguard against potential race conditions
                        .view(appHomeView)
                );
            }

            return ctx.ack();
        });

        app.command("/hello", (req, ctx) -> {
            return ctx.ack("What's up?");
        });
        return app;
    }
}
