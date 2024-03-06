package org.malex;

import static it.tdlight.jni.TdApi.*;

import it.tdlight.Init;
import it.tdlight.client.*;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.TDLibSettings;
import java.nio.file.*;

public final class UserBotClient extends ClientProperties {

  private static SimpleTelegramClient client;

  public static void main(String[] args) throws Exception {
    Init.init();
    try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
      APIToken apiToken = new APIToken(API_ID, HASH_CODE);

      // Sql database
      TDLibSettings settings = TDLibSettings.create(apiToken);
      Path sessionPath = Paths.get("example-tdlight-session");
      settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));

      SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);
      SimpleAuthenticationSupplier<?> authenticationData =
          AuthenticationSupplier.user(PHONE_NUMBER);

      // handle
      clientBuilder.addUpdateHandler(
          UpdateAuthorizationState.class, UserBotClient::onUpdateAuthorizationState);
      clientBuilder.addUpdateHandler(UpdateNewMessage.class, UserBotClient::onUpdateNewMessage);

      client = clientBuilder.build(authenticationData);
      client.waitForExit();
    }
  }

  private static void onUpdateAuthorizationState(UpdateAuthorizationState update) {
    AuthorizationState authorizationState = update.authorizationState;
    String state = "";
    if (authorizationState instanceof AuthorizationStateReady) state = "Logged in";
    else if (authorizationState instanceof AuthorizationStateClosing) state = "Closing...";
    else if (authorizationState instanceof AuthorizationStateClosed) state = "Closed";
    else if (authorizationState instanceof AuthorizationStateLoggingOut) state = "Logging out...";
    LOG.info(state);
  }

  private static void onUpdateNewMessage(UpdateNewMessage update) {
    // handle chat id of 'Time of Ukraine' chanel
    if (update.message.chatId != -1001431180517L) {
      return;
    }
    MessageContent messageContent = update.message.content;

    // If message is text
    if (messageContent instanceof MessageText messageText) {
      long chatId = update.message.chatId;
      long msgId = update.message.id;
      String text = messageText.text.text;
      String logMsg = String.format("chatId - %s, msgId - %s, text - %s", chatId, msgId, text);
      LOG.info(logMsg);

      long replyToMessageId = update.message.replyToMessageId;
      // If message start witch "check"
      if (replyToMessageId != 0 && text.trim().startsWith("check")) {
        // Retrieve the replied message by its replyMessageID
        var getMessage = new GetMessage(update.message.chatId, replyToMessageId);
        client.send(getMessage, repliedMessage -> {});
      }
    }
  }
}
