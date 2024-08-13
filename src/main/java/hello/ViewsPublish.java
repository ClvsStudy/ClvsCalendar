package hello;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jetty.SlackAppServer;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppHomeOpenedEvent;

import java.io.IOException;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.element.BlockElements.asContextElements;
import static com.slack.api.model.view.Views.view;

public class ViewsPublish {

    public static void main(String[] args) throws Exception {
        var config = new AppConfig();
        config.setSingleTeamBotToken(System.getenv("SLACK_BOT_TOKEN"));
        System.out.println(System.getenv("SLACK_BOT_TOKEN"));
        config.setSigningSecret(System.getenv("SLACK_SIGNING_SECRET"));
        System.out.println(System.getenv("SLACK_SIGNING_SECRET"));

        var app = new App(config); // `new App()` does the same

        app.event(AppHomeOpenedEvent.class, (req, ctx) -> {
            var logger = ctx.logger;
            var userId = req.getEvent().getUser();
            try {
                // Call the conversations.create method using the built-in WebClient
                var modalView = view(v -> v
                        .type("home")
                        .blocks(asBlocks(
                                section(s -> s.text(markdownText(mt ->
                                        mt.text("*Welcome home, <@" + userId + "> :house:*")))),
                                section(s -> s.text(markdownText(mt ->
                                        mt.text("About the simplest modal you could conceive of :smile:\\n\\nMaybe <https://api.slack.com/reference/block-kit/block-elements|*make the modal interactive*> or <https://api.slack.com/surfaces/modals/using#modifying|*learn more advanced modal use cases*>.")))),
                                divider(),
                                context(c -> c.elements(asContextElements(
                                        markdownText("Psssst this modal was designed using <https://api.slack.com/tools/block-kit-builder|*Block Kit Builder*>")
                                )))
                        ))
                );
                var result = ctx.client().viewsPublish(r -> r
                        // The token you used to initialize your app
                        .token(System.getenv("SLACK_BOT_TOKEN"))
                        .userId(userId)
                        .view(modalView)
                );
                // Print result
                logger.info("result: {}", result);
            } catch (IOException | SlackApiException e) {
                logger.error("error: {}", e.getMessage(), e);
            }
            return ctx.ack();
        });

        var server = new SlackAppServer(app);
        server.start();
    }

}
