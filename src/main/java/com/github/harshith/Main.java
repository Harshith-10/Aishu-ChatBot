package com.github.harshith;

import com.hdr.aishu.aiml.Bot;
import com.hdr.aishu.aiml.Chat;
import com.hdr.aishu.aiml.Graphmaster;
import com.hdr.aishu.aiml.MagicBooleans;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.user.User;

import java.util.List;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Main {
    public static void main(String[] args) {
        String botName = "aishu";
        String action = "chat";

        String token = "Your Bot Token";
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        Graphmaster.enableShortCuts = true;

        final Bot bot = new Bot(botName, "./src/main/resources/bots", action);
        final ChatsManager manager = new ChatsManager();
        final ResponseCompiler resp = new ResponseCompiler();
        final BrainfuckBrain brain = new BrainfuckBrain();

        final Chat chatSession = new Chat(bot);
        bot.brain.nodeStats();
        MagicBooleans.trace_mode = false;

        api.addMessageCreateListener(event -> {
            final Message message = event.getMessage();
            final TextChannel channel = event.getChannel();
            final User user = message.getAuthor().asUser().get();
            String msg = message.getContent();
            String ret = "";

            List<User> mentionedUsers = message.getMentionedUsers();

            if (!message.getAuthor().isBotUser()) {
                boolean slash = false;
                boolean isShort = false;

                if(msg.toLowerCase().startsWith("!aishu")){
                    slash = true;
                } else if (msg.toLowerCase().startsWith("!a")) {
                    slash = true;
                    isShort = true;
                }

                if (slash || manager.getLock(channel)) {
                    if(manager.getBFLock(channel) && (msg.contains(">") || msg.contains("+") || msg.contains("<") || msg.contains("-"))){
                        ret = brain.interpret(msg, "");
                    } else if (msg.equalsIgnoreCase("!aishu chatlock") || msg.equalsIgnoreCase("!a chatlock")) {
                        manager.addLock(channel);
                        ret = "ChatLock activated! Now you don't need to type !aishu or !a infront of every message.";
                    } else if (msg.equalsIgnoreCase("!aishu release") || msg.equalsIgnoreCase("!a release")) {
                        manager.removeLock(channel);
                        ret = "ChatLock deactivated.";
                    } else if (msg.startsWith("!a-brainfuck")) {
                        ret = brain.interpret(msg.substring(12), "");
                    } else if (msg.equalsIgnoreCase("!aishu bflock") || msg.equalsIgnoreCase("!a bflock")) {
                        manager.addBFLock(channel);
                        ret = "Brainfuck lock activated.";
                    } else if (msg.contains("!aishu avatar") || msg.contains("!a av")) {
                        ret = "%av";
                        if(mentionedUsers != null && mentionedUsers.size() != 0){
                            ret = "%av_";
                        }
                    } else if (msg.equalsIgnoreCase("!aishu-shutdown") || msg.equalsIgnoreCase("!a-shutdown")) {
                        AllowedMentions allowedMentions = new AllowedMentionsBuilder()
                                .addUser(user.getId())
                                .setMentionRoles(true)
                                .setMentionEveryoneAndHere(false)
                                .build();
                        if(message.getAuthor().isBotOwner()){
                            MessageBuilder rmsg = new MessageBuilder()
                                    .setContent("Program \"Aishu\" aborted with exit code \"0\"")
                                    .replyTo(message);
                                    rmsg.send(channel);
                            System.exit(0);
                        }else{
                            MessageBuilder rmsg = new MessageBuilder()
                                    .setAllowedMentions(allowedMentions)
                                    .append("User ")
                                    .append(user.getMentionTag())
                                    .append(" doesn't have the permission to do this.")
                                    .replyTo(message);
                                    rmsg.send(channel);
                        }
                    } else {
                        for (ret = chatSession.multisentenceRespond(manager.getLock(channel)?(msg):(isShort?(msg.substring(3)):(msg.substring(7)))); ret.contains("&lt;"); ret = ret.replace("&lt;", "<"))
                            while (ret.contains("&gt;")) {
                                ret = ret.replace("&gt;", ">");
                            }
                    }

                    MessageBuilder rmsg = new MessageBuilder();
                    if(ret.equals("%av")) {
                        try {
                            rmsg.setContent("My Avatar:");
                            rmsg.addAttachment(api.getYourself().getAvatar());
                        } catch (Exception e) {
                            rmsg.setContent(ret);
                            if (!(e instanceof IllegalArgumentException)) {
                                e.printStackTrace();
                            }
                        }
                    } else if(ret.equals("%av_")) {
                        if(mentionedUsers.size() > 5) rmsg.setContent("More than 5 users' avatars requested. Order might be confusing.\n");
                        try {
                            for (int i = mentionedUsers.size()-1; i > -1; i--) {
                                User mentionedUser = mentionedUsers.get(i);
                                rmsg.append((mentionedUsers.size()-i)+". "+mentionedUser.getDisplayName(message.getServer().get())+"\n");
                                rmsg.addAttachment(mentionedUser.getAvatar());
                            }
                        } catch (Exception e) {
                            rmsg.setContent(ret);
                            if (!(e instanceof IllegalArgumentException)) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            rmsg.setContent(ret.substring(0, ret.length() - 4).replaceAll("%user", message.getAuthor().getDisplayName()));
                            rmsg.addAttachment(resp.process(ret));
                        } catch (Exception e) {
                            rmsg.setContent(ret);
                            if (!(e instanceof IllegalArgumentException)) {
                                e.printStackTrace();
                            }
                        }
                    }
                    rmsg.replyTo(message).send(channel);
                }
            }
        });

        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}
