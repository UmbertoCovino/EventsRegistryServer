package server.backend;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Date;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import commons.Event;
import commons.User;
import commons.exceptions.InvalidUserEmailException;
/*
 * deep-linking telegram
 * https://telegram.me/EventsAppBot?/start=<token> the bot will receive a start string with payload (in our app the user's id)
 */
public class TelegramBot extends TelegramLongPollingBot {
	
	private TelegramUsersRegistry telegramRegistry = TelegramUsersRegistry.instance();

	
    @Override
    public void onUpdateReceived(Update update) {
		// We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

        	String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            
            if(message_text.contains("start")) {
            	
                String tokenS = message_text.substring(7);
                Integer token = Integer.parseInt(tokenS);
                User user = telegramRegistry.getUserByToken(token);
                
                try {
    				telegramRegistry.addChatIdByEmail(user.getEmail(), chat_id);
    			} catch (InvalidUserEmailException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
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
    
    public void startNotificationManager() {
    	new Notification().start();
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
    	
    	public void run() {
    		try {
    			while (!this.isInterrupted()) {
    				
    				ArrayList<Event> events = EventsAccessObject.getEvents();
    				Date today = new Date();
    				
    				for(Event event: events) {
    					
    					// notifica tutti gli utenti che sono interessati ad eventi che si terranno in giornata

    					if((event.getStartDate().getTime() - today.getTime()) <= 86400000) {	// 1 day  

    						
    						ArrayList<User> subscribers = EventsAccessObject.getEventSubscribers(event.getId());
    						
    						for(User user: subscribers) {
    							
    							long chat_id_notifica = telegramRegistry.getChatIdByEmail(user.getEmail());
    							
    							SendMessage message = new SendMessage() // Create a message object object
    		    						.setChatId(chat_id_notifica)
    		    						.setText("notifica-evento");
    		    				execute(message); // Sending our message object to user
    						}
    					}
    				}
    				
    				Thread.sleep((long)60000);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
}