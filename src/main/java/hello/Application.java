package hello;

import com.slack.api.bolt.App;
import com.slack.api.methods.response.views.ViewsPublishResponse;
import com.slack.api.model.event.AppHomeOpenedEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static com.slack.api.model.view.Views.view;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@ServletComponentScan

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        var config = new com.slack.api.bolt.AppConfig();
        config.setSingleTeamBotToken(System.getenv("SLACK_BOT_TOKEN"));
        System.out.println(System.getenv("SLACK_BOT_TOKEN"));
        config.setSigningSecret(System.getenv("SLACK_SIGNING_SECRET"));
        System.out.println(System.getenv("SLACK_SIGNING_SECRET"));

        App app = new App(config);

        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {

            var appHomeView = view(view -> view
                    .type("home")
                    .blocks(asBlocks(
                            section(section -> section.text(markdownText(mt -> mt.text("*Welcome to your _App's Home tab_* :tada:")))),
                            divider(),
                            section(section -> section.text(markdownText(mt -> mt.text("This button won't do much for now but you can set up a listener for it using the `actions()` method and passing its unique `action_id`. See an example on <https://slack.dev/java-slack-sdk/guides/interactive-components|slack.dev/java-slack-sdk>.")))),
                            actions(actions -> actions
                                    .elements(asElements(
                                            button(b -> b.text(plainText(pt -> pt.text("Click me!"))).value("button1").actionId("button_1"))
                                    ))
                            )
                    ))
            );

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

    }
}
