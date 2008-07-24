import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

class MainFrame
    extends Frame
    implements GameController, Runnable, ActionListener
{
	private final Game game = new Game();
	private final GamePanel gamePanel;
	private final Socket socket;
	private final Colour player;
	private ObjectOutputStream oos = null;
	private Move[] initialHistory;

	public MainFrame(Socket s, Colour player, Move[] initialHistory)
	{
		super("Caïssa");
		
		this.socket = s;
		this.player = player;
		this.initialHistory = initialHistory;
		gamePanel = new GamePanel(this, player, this);

		setBackground(Color.white); /* to support JDK 1.3.1 (otherwise, should be Color.WHITE) */
		addWindowListener(
			new WindowAdapter()
			{
        		public void windowClosing(WindowEvent e)
        		{
        			try {
    	    			socket.close();
    	    		} catch(IOException ioexception) {
                        // ignored.
                    };
                    System.exit(0);
				}
			}
		);

		new Thread(this).start();
		
		add(gamePanel);
		pack(); setResizable(false);
		setVisible(true);
	}

    public void run()
    {
      	try
      	{
            oos = new ObjectOutputStream(socket.getOutputStream());
    	  	ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    	  	
    	  	// Process initial history
    	  	if(initialHistory == null)
    	  	    initialHistory = (Move[])ois.readObject();
            else
                oos.writeObject(initialHistory);
            for(int n = 0; n < initialHistory.length; ++n)
                replay(initialHistory[n]);
                
    		while(true)
    		{
    			Move move = (Move)ois.readObject();
    			if((move == null) || (game.player() == player) || (!request(move)))
    				abort(player.other());
    		}
      	}
      	catch(Exception exception)
      	{
    	  	abort(player.other());
      	}  	
	}
	
	public static void main(String[] args)
	{
	    boolean isHost = ((args.length > 0) && (args[0].equals("-")));
	    
		if((args.length < 1) || (args.length > 2) ||
		   ((args.length == 2) && (!isHost)) )
		{
			System.out.println("Usage:\n"+
				"\tjava MainFrame - [<historyfile>]  -- to run as server\n"+
				"\tjava MainFrame <hostname> -- to run as client\n\n"+
				"The server plays white; the client plays black.");
			return;
		}
		
		Socket socket = null;
		try
		{
		    Move[] history = null;
			if(isHost)
			{
        		history = new Move[0];
        		if(args.length > 1)
            		try
            		{
                        ArrayList historyList = new ArrayList();
            		    BufferedReader is = new BufferedReader(
            		        new InputStreamReader(new FileInputStream(args[1]))
                        );
                        String line;
                        while((line = is.readLine()) != null)
                        {
                            historyList.add(Move.fromString(line));
                        }
                        history = (Move[])historyList.toArray(history);
                        is.close();
                    }                       
        	        catch(Exception e)
        	        {
        	            System.err.println("Failed to load history!");
        	            e.printStackTrace();
        	        }
    	        
				System.out.println("Waiting for connection on port 4040...");
				ServerSocket listenSocket = new ServerSocket(4040);
				socket = listenSocket.accept();
				listenSocket.close();
			} else
			{
				System.out.println("Trying to connect on port 4040...");
				socket = new Socket(args[0], 4040);
			}
			
			System.out.println("Connection established!");

			MainFrame frame = new MainFrame(socket,
			    isHost?Colour.WHITE:Colour.BLACK, history);
		}
		catch(IOException e)
		{
			System.out.println("Connection failed!");
		}
	}
	
	private boolean send(Move move)
	{
		try
		{
			oos.writeObject(move);
		}
		catch(IOException exception)
		{
			return false;
		}
		return true;
	}
	
	public boolean replay(Move move)
	{
		if((move == null) || (game.over()) || (!game.isValid(move)))
		    return false;

		game.perform(move);
		gamePanel.processMove(move);

		return true;
	}

	public boolean request(Move move)
	{
		if(game.over()) return false;
		if(!game.isValid(move)) return false;
		if((game.player()==player)&&(!send(move))) return false;

		if(move != null)
		{
			game.perform(move);
			gamePanel.processMove(move);
		}
		else
		{
			abort(player);
		}
		
		return true;
	}
	
	private void abort(Colour player)
	{
		game.abort(player);
		gamePanel.processMove(null);
	}
	
	public Game game()
	{
		return game;
	}

	public void actionPerformed(ActionEvent ae)
	{
	    if(ae.getActionCommand().equals("storeHistory"))
	    {
	        FileDialog fd =
	            new FileDialog(this, "Save History File", FileDialog.SAVE);
	        fd.show();
	        try
	        {
    	        String filepath =
    	            fd.getDirectory().toString() +
    	            fd.getFile().toString();
	            PrintStream os = new PrintStream(new FileOutputStream(
	                filepath.toString()));
	            Move[] history = game.getHistory();
	            for(int n = 0; n < history.length; ++n)
	                os.println(history[n].toString());
            	os.close();
                System.out.println("History saved to file: " + filepath);
	        }
	        catch(Exception e)
	        {
	            System.err.println("Failed to save history!");
	            e.printStackTrace();
	        }
	    }
	}

}
