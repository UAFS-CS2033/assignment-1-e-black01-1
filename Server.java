
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;


public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int portNo;
    private File file;
    public Server(int portNo){
        this.portNo=portNo;
    }


    private void processConnection() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);


        //*** Application Protocol *****
        String buffer = in.readLine();
        String[] bufferSplit = buffer.split(" ");
        while(buffer != null && buffer.length() != 0){
            
            buffer = in.readLine();
        }
        
        String path = bufferSplit[1]; //GET */home.html* HTTP/1.1    gets the middle value for the path
        if (!(path.equals("/"))){
            path = "docroot" + path;
            file = new File(path);

            if (file.exists()){
                String contentType = getContentType(path);
                out.printf("HTTP/1.1 200 OK\n");
                out.println("Content-Length: " + file.length());
                out.println("Content-Type: " + contentType);
                out.println("Connection: closed");
                out.println();
            
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while((line = br.readLine()) != null){
                    out.println(line);
                }
                
               
                br.close();
                return; // Exits after 
            }
            if (!file.exists()){
                out.printf("HTTP/1.1 404 Not Found\n");
                out.println("Content-Type: text/html\n");
                out.println("Connection: closed\n");
                out.println();
                out.println("<h1>404 Error: Page Not Found</h1>");
                return; // Exits after
            }
            
        }
        
        
        out.printf("HTTP/1.1 200 OK\n");
        out.printf("Content-Length: 34\n" );
        out.printf("Content-Type: text/html\n\n");
        out.printf("<h1>Welcome to the Web Server</h1>");
        in.close();
        out.close();
    }

    public static String getContentType(String filePath) {
        if (filePath.endsWith(".html")){
            return "text/html";
        }
        if (filePath.endsWith(".css")){
            return "text/css";
        } 
        if (filePath.endsWith(".png")){
            return "image/png";
        }
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")){
            return "image/jpeg";
        }
        else{
            return "text/plain"; // Default type
        }
         
    }
    


    public void run() throws IOException{
        boolean running = true;

        serverSocket = new ServerSocket(portNo);
        System.out.printf("Listen on Port: %d\n",portNo);
        while(running){
            clientSocket = serverSocket.accept();
            //** Application Protocol
            processConnection();
            clientSocket.close();
        }
        serverSocket.close();
    }
    public static void main(String[] args0) throws IOException{
        Server server = new Server(8080);
        server.run();
    }
}
