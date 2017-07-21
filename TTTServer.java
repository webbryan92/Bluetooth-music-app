import java.net.*;
import java.io.*;

public class TTTServer
{
   public static void main(String args[]) throws Exception
   {
        ServerSocket connection = new ServerSocket( 1490 ); // Port 1490

        while(true) {
            Socket s = connection.accept();                     // Wait for connection

            BufferedReader in = new BufferedReader(     // Socket input and output
                                new InputStreamReader(
                                        s.getInputStream() ) );
            PrintStream out = new PrintStream(s.getOutputStream());

            String str;
            str=in.readLine();                                          // Read one line to \n or \r
            System.out.println( str );                               // Echo input, ex. 012345678 or oo2x45678
        
            int move = (new Computer()).move(str);      // Generate computer move
            System.out.println("Move " + move);
        
            out.print(move);                                            // Send computer move
                                                                 
            in.close();
            out.close();                                                   // Close stream
            s.close();                                                      // Close connection
            System.out.println("Connection closed");
        }
   }
}