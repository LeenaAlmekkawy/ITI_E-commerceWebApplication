package jets.projects.stats;

import java.util.concurrent.atomic.AtomicInteger;

public class OnlineStats {
    private static final AtomicInteger currentlyLoggedInUsers 
            = new AtomicInteger(0);
    
    public static void userLoggedIn() {
        currentlyLoggedInUsers.incrementAndGet();
    }
    
    public static void userLoggedOut() {
        currentlyLoggedInUsers.decrementAndGet();
    }
    
    public static int getCurrentlyLoggedInUsers() {
        return currentlyLoggedInUsers.get();
    }
}
