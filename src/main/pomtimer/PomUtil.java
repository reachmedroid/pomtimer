package main.pomtimer;

public class PomUtil {
	
	 public static boolean isNullOrBlank(String str) {
	      if (str == null || str.length() == 0) {
	          return true;
	      }
	      for (int i = 0; i < str.length(); i++) {
	          if ((Character.isWhitespace(str.charAt(i)) == false)) {
	              return false;
	          }
	      }
	      return true;
	  }
	 
	 public static String formatTime(long millis) {
		 int seconds = (int)((millis / 1000) % 60);
		 int minutes = (int) ((millis / 1000) / 60 );
		 
		 return String.format("%02d : %02d", new Object [] {minutes, seconds});
	 }
	 
	 public static long minsToMillis(int mins) {
		 return mins * 60 * 1000;
	 }

}
