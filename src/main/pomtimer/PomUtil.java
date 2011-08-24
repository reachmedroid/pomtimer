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

}
