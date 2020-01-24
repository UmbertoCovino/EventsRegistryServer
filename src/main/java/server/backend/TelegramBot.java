package server.backend;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import commons.Event;
import commons.User;
import commons.exceptions.GenericSQLException;
import commons.exceptions.InvalidUserEmailException;
import commons.exceptions.VoidClassFieldException;
/*
 * deep-linking telegram
 * https://telegram.me/EventsAppBot?/start=<token> the bot will receive a start string with payload (in our app the user's id)
 */
public class TelegramBot extends TelegramLongPollingBot {
	
	private TelegramUsersAccessObject telegramRegistry = TelegramUsersAccessObject.instance();
	
    @Override
    public void onUpdateReceived(Update update) {
		// We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

        	String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            
            if(message_text.startsWith("start")) {
            	
                String tokenS = message_text.substring(7);
                Integer token = Integer.parseInt(tokenS);
                User user = telegramRegistry.getUserByToken(token);
                
                try {
    				telegramRegistry.updateUserChatId(user.getEmail(), chat_id);
    			} catch (InvalidUserEmailException | GenericSQLException | VoidClassFieldException e1) {
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
    				
    				Date today = new Date();
    				Calendar c = Calendar.getInstance(); 
    				c.setTime(today); 
    				c.add(Calendar.DATE, 1);
    				Date tomorrow = c.getTime();
    				
    				ArrayList<Event> events = EventsAccessObject.getEventsBetweenTwoDates(today, tomorrow);
    				System.out.println("notification thread");
    				for(Event event: events) {
    					// notifica tutti gli utenti che sono interessati ad eventi che si terranno in giornata
    					System.out.println(event.getTitle());
    					ArrayList<User> subscribers = EventsAccessObject.getEventSubscribers(event.getId());
    						
						for(User user: subscribers) {

							long chat_id_notifica = telegramRegistry.getUserChatId(user.getEmail());

							if(chat_id_notifica != 0) {
								SendMessage message = new SendMessage() // Create a message object object
			    						.setChatId(chat_id_notifica)
			    						.setText("Hey, a breve avrà inizio l'evento " + event.getTitle() 
			    						+ "\n" + "inizio: " + event.getFormattedStartDate() + "\n" 
			    						+ "fine: " + event.getFormattedEndDate());
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