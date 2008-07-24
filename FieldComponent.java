import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class FieldComponent extends Component
{
	private static Image piecesImage = null;
	private static final Object lock = new Object();
	private static final Color
		tileColor          = new Color(255,255,192),
		tileBorderColor    = new Color(0,0,0),
		fieldColor         = new Color(64,128,255),
		fieldSelectedColor = new Color(224,0,0),
		fieldHighlitColor  = new Color(224, 96, 96);

	private BoardPanel panel;
	private Field field;
	protected boolean isSelected = false;
	protected boolean isHighlit = false;
	
	{
		synchronized(lock)
		{
			if(piecesImage == null)
			{
				URL url = FieldComponent.class.getResource("pieces.gif");
				piecesImage = getToolkit().createImage(url);
				MediaTracker tracker = new MediaTracker(this);
				tracker.addImage(piecesImage, 0);
				try { tracker.waitForAll(); } catch(InterruptedException e) { };
			}
		}		
		
		enableEvents(AWTEvent.MOUSE_EVENT_MASK); 
	}
		
	public FieldComponent(BoardPanel panel, Field field)
	{
		this.panel = panel;
		this.field = field;
	}
	
	public boolean isOpaque()
	{
		return true;
	}
	
	public void update(Graphics g)
	{
		/* Don't clear background - this component is opaque */
		paint(g);
	}

	public void paint(Graphics g)
	{
		Dimension size = getSize();
		
		// Draw field
		Color color =
			isSelected ? fieldSelectedColor :
				(isHighlit ? fieldHighlitColor : fieldColor);
		g.setColor(color);
		g.fillRect(0, 0, size.width, size.height);
		g.setColor(color.brighter());
		g.drawLine(0, 0, 0, size.height - 2);
		g.drawLine(0, 0, size.width - 1, 0);
		g.setColor(color.darker());
		g.drawLine(1, size.height - 1, size.width, size.height - 1);
		g.drawLine(size.width - 1, 0, size.width - 1, size.height - 1);

		// Draw tile
		if(field.hasTile)
		{
			g.setColor(tileColor);
			g.fillRoundRect(2, 2, size.width-5, size.height-5, size.width/4, size.height/4);
			g.setColor(tileBorderColor);
			g.drawRoundRect(2, 2, size.width-5, size.height-5, size.width/4, size.height/4);
		}

		// Draw piece
		if(field.piece != null)
		{
			int sx, sy;

			if(field.piece.color == Colour.WHITE) sy = 0; else
			if(field.piece.color == Colour.BLACK) sy = 1; else
				sy = -1;

			if(field.piece.kind == Kind.QUEEN)  sx = 0; else
			if(field.piece.kind == Kind.ROOK)   sx = 1; else
			if(field.piece.kind == Kind.BISHOP) sx = 2; else
			if(field.piece.kind == Kind.KNIGHT) sx = 3; else
				sx = -1;

			if((sx >= 0) && (sy >= 0))	
				g.drawImage(piecesImage,
					size.width > 42 ? size.width/2 - 21 : 0, size.height > 42 ? size.height/2 - 21 : 42,
					size.width > 42 ? size.width/2 + 21 : 0, size.height > 42 ? size.height/2 + 21 : 42,
					42 * sx, 42 * sy, 42 * (sx + 1), 42 * (sy + 1), null
				);
		}
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(50,50);
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(50,50);
	}

  public void processEvent(AWTEvent e)
  {
  	if (e.getID() == MouseEvent.MOUSE_PRESSED)
  		panel.fieldClicked(this);
  	super.processEvent(e); 
  }
  
  public Field field()
  {
  	return field;
  }
}
