package server.backend;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
/*
 * deep-linking telegram
 * https://telegram.me/EventsAppBot?/start=<token> the bot will receive a start string with payload (in our app the user's id)
 */
public class TelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
    		// We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            System.out.println(message_text);
            long chat_id = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getFirstName();
            String reaction = "I'm EventsAppBot, what I can do for you?";
            if(message_text.contains("start")) {
            	reaction = "Benvenuto in EventsAppBot, " + username + "!" + message_text;
            	//Enable notifications
            	new Notification(chat_id).start();
            	SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(reaction);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(chat_id + " - " + message_text);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "EventsAppBot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "920433937:AAGzR59IS9EZubWqx0ectFjh2tbfIkK8hzQ";
    }
    
    class Notification extends Thread {
    	private long chat_id;
    	
    	public Notification(long chat_id) {
    		this.chat_id = chat_id;
    	}
    	
    	public void run() {
    		try {
    			while (!this.isInterrupted()) {
    				SendMessage message = new SendMessage() // Create a message object object
    						.setChatId(chat_id)
    						.setText("notifica-evento");
    				execute(message); // Sending our message object to user
    				Thread.sleep((long) 1000000);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
}