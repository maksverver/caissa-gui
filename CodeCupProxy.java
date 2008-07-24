import java.io.*;
import java.net.*;

public class CodeCupProxy
{
	public static void main(String[] args)  throws Exception
	{
		if((args.length < 1) || (args.length > 2))
		{
			System.out.println("Usage:");
			System.out.println("\tjava CodeCupProxy <command line> [<hostname>]");
			return;
		}

		String
			command = args[0],
			hostname = (args.length < 2) ? null : args[1];

		Process
			process = Runtime.getRuntime().exec(command);

		BufferedReader
			processIn = new BufferedReader(new InputStreamReader(process.getInputStream())),
			processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		PrintStream
			processOut = new PrintStream(process.getOutputStream(), true);

		ObjectInputStream socketIn;
		ObjectOutputStream socketOut;

		if(hostname == null)
		{
			ServerSocket serverSocket = new ServerSocket(4040);
			Socket socket = serverSocket.accept();
			socketIn = new ObjectInputStream(socket.getInputStream());
			socketOut = new ObjectOutputStream(socket.getOutputStream());
			serverSocket.close();

			processOut.println("start");
			socketOut.writeObject(Move.fromString(processIn.readLine()));
		} else
		{
			Socket socket = new Socket(hostname, 4040);
			socketIn = new ObjectInputStream(socket.getInputStream());
			socketOut = new ObjectOutputStream(socket.getOutputStream());
		}

		while(true)
		{
			Move move = (Move)socketIn.readObject();
			if(move == null) return;
			processOut.println(move.toString().toLowerCase() /* hack! */ );
			String line = processIn.readLine();
			System.err.println("Process line: \""+line+"\"");
			socketOut.writeObject(Move.fromString(line));
		}
	}
}