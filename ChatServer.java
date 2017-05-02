import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer 
{
    private static final int PORT = 3388;
    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws Exception 
    {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        FileWriter filewrite = new FileWriter("C:\\alltext.txt");//new
        filewrite.close();//new
        try 
        {
            while(true) 
            {
                new Handler(listener.accept()).start();
            }
        } 
        finally 
        {
            listener.close();
        }
    }

    private static class Handler extends Thread 
    {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) 
        {
            this.socket = socket;
        }

        public void run() 
        {
            try 
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while(true) 
                {
                	out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) 
                    {
                        return;
                    }
                    synchronized (names) 
                    {
                        if (!names.contains(name)) 
                        {
                            names.add(name);
                            
                            FileReader fileread = new FileReader("C:\\alltext.txt");//new
                            BufferedReader br = new BufferedReader(fileread);//new
                            String newLine;//new
                            while((newLine = br.readLine()) != null)
                            {
                            	out.println(newLine);//new
                            }
                            br.close();//new
                            fileread.close();//new
                            
                            out.println("MESSAGE" + " SERVER: " + name + " has been added to the chatroom.");
                            
                            for(String name: names)
	                        {
	                        	out.println("NAME " + name);
	                        }
                            if(!name.equals("NEW"))
	                            {
	                            for (PrintWriter writer : writers) 
	                            {
	                            	writer.println("MESSAGE" + " SERVER: " + name + " has been added to the chatroom.");
	                            }
	                            FileWriter filewrite = new FileWriter("C:\\alltext.txt", true);//new
	                            BufferedWriter bw = new BufferedWriter(filewrite);//new
	                            bw.write("MESSAGE" + " SERVER: " + name + " has been added to the chatroom.");//new
	                            bw.newLine();//new
	                            bw.close();//new
	                            filewrite.close();//new
                            }
                            
                            System.out.println(name + " has been added to the Chatroom.");
                            break;
                        }
                    }
                }
                
                out.println("NAMEACCEPTED");
                writers.add(out);

                while(true) 
                {
                	for (PrintWriter writer : writers) 
                    {
                    	for(String name: names)
                        {
                        	writer.println("NAME " + name);
                        }
                    }
                    String input = in.readLine();
                    
                    if (input == null) 
                    {
                        return;
                    }
                    else if(input.startsWith("add"))
                    {
                    	String[] addUser = input.split(" ");
                    	String text = "dsadd user \"cn=" + addUser[1] + " " + addUser[2] + ", ou=ChatUsers, dc=campusletters, dc=org\" -samid " + 
                    					addUser[3] + " -upn " + addUser[3] + "@campusletters.org -fn " + addUser[1] + " -ln " + addUser[2] +
                    					" -desc \"Chat User\" -disabled no -pwd "+ addUser[4] + "\n exit";
                    	FileWriter fwriter = new FileWriter("C:\\addUser.bat");
                    	fwriter.write(text);
                    	fwriter.close();
                    	Runtime.getRuntime().exec("cmd /c start C:\\addUser.bat");
                    }
                    if(!input.startsWith("add"))
                    {
	                    for (PrintWriter writer : writers) 
	                    {
		                        writer.println("MESSAGE " + name + ": " + input);
		                        System.out.println("MESSAGE " + name + ": " + input);
	                    }
	                    FileWriter filewrite = new FileWriter("C:\\alltext.txt", true);//new
	                    BufferedWriter bw = new BufferedWriter(filewrite);//new
	                    bw.write("MESSAGE " + name + ": " + input + "\n");//new
	                    bw.newLine();//new
	                    bw.close();//new
	                    filewrite.close();//new
                    }
                }
            } 
            catch (IOException e) 
            {
                System.out.println(e);
            } 
            finally 
            {
                if (name != null) 
                {
                    names.remove(name);
                    System.out.println(name + " has been removed from the Chatroom.");
                    if(!name.equals("NEW"))
                    {
	                    for (PrintWriter writer : writers) 
	                    {
	                    	writer.println("REMOVE " + name);
	                    	writer.println("MESSAGE" + " SERVER: " + name + " has been removed to the chatroom.");
	                    }
	                    try 
	                    {
	                    	FileWriter filewrite = new FileWriter("C:\\alltext.txt", true);//new
	                    	BufferedWriter bw = new BufferedWriter(filewrite);//new
							bw.write("MESSAGE" + " SERVER: " + name + " has been removed to the chatroom." + "\n");//new
							bw.newLine();//new
							bw.close();//new
							filewrite.close();//new
						} 
	                    catch (IOException e) 
	                    {
							e.printStackTrace();
						}
                    }
                }
                if (out != null) 
                {
                    writers.remove(out);
                }
                try 
                {
                    socket.close();
                } 
                catch (IOException e) 
                {
                	
                }
            }
        }
    }
}