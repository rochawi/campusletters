import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatWindow extends JFrame
{
	private static final long serialVersionUID = 3376502308484451958L;
	private JPanel contentPane;
	private JTextPane output;
	private JTextArea input;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private String username;
	private StyledDocument doc;
	private SimpleAttributeSet systemStyle;
	private SimpleAttributeSet userStyle;
	private SimpleAttributeSet chatStyle;
	private HashSet<String> names = new HashSet<String>();
	private DefaultListModel<String> listModel;
	private JList<String> chatRoomList;

	public ChatWindow()
	{
		
	}
	
	public ChatWindow(String name, String username) throws UnknownHostException, IOException
	{
		this.username = username;
		this.setTitle(name);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(700,650);
		this.setMinimumSize(new Dimension(700,650));
		this.setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		this.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		setLookAndFeel();
		createTitlePane();
		createChatPane();
		createInputPane();
		createOutputPane();
		startChatThread();
		
		this.setVisible(true);
	}
	
	private void createTitlePane()
	{
		//Border border = BorderFactory.createLineBorder(Color.BLACK);
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(gridbag);
		//panel.setBackground(Color.decode("0xFDF9D8"));
		
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				try 
				{
					socket.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		/*
		JMenuItem logout = new JMenuItem("Logout");
		logout.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				try 
				{
					socket.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				logoff();
			}
		});
		file.add(logout);
		*/
		file.add(exit);
		menuBar.add(file);
		
		
		ImageIcon icon = new ImageIcon(Login.class.getResource("/resources/cl.png"));
		JLabel logo = new JLabel();
		logo.setIcon(icon);
		//logo.setBorder(border);
		
		JLabel label = new JLabel("Campus Letters");
		label.setForeground(Color.decode("0x841617"));
		label.setFont(new Font(getName(), Font.BOLD, 70));
		//label.setBorder(border);
		
		JLabel logo2 = new JLabel();
		
		gridbag.setConstraints(panel, c);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 1.0;
		panel.add(menuBar,c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		panel.add(logo,c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		panel.add(label);
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.NONE;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		panel.add(logo2);
		
		contentPane.add(panel, BorderLayout.NORTH);
	}
	
	private void setLookAndFeel()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	private void createChatPane()
	{
		JPanel panel = new JPanel();
		BorderLayout grid = new BorderLayout();
		panel.setLayout(grid);
		
		listModel = new DefaultListModel<String>();
		
		chatRoomList = new JList<String>(listModel);
		chatRoomList.setFont(new Font(getName(), Font.BOLD, 20));
		chatRoomList.setEnabled(false);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		chatRoomList.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		
		chatRoomList.setFixedCellWidth(100);
		chatRoomList.setFixedCellHeight(40);
		
		//JLabel newRoom = new JLabel("Add Chat Room");
		//newRoom.setToolTipText("Click to create new Chat Room.");
		
		//newRoom.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		//newRoom.setForeground(Color.WHITE);
		panel.setBackground(Color.BLUE);
		
		panel.add(chatRoomList,BorderLayout.CENTER);
		//panel.add(newRoom,BorderLayout.SOUTH);
		contentPane.add(panel, BorderLayout.EAST);
	}
	
	private void createInputPane()
	{
		input = new JTextArea("Type Here...");
		input.setForeground(Color.GRAY);
		input.addKeyListener(new KeyListener() 
		{
			@Override
			public void keyTyped(KeyEvent e) 
			{
				
			}

			@Override
			public void keyPressed(KeyEvent e) 
			{
				if(input.getForeground().equals(Color.GRAY))
				{
					input.setText("");
					input.setForeground(Color.BLACK);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) 
			{	
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String temp = input.getText();
					temp = temp.replaceAll("\n", "");
					e.consume();
					out.println(temp);
					input.setText("");
				}
				if(input.getText().equals(""))
				{
					input.setForeground(Color.GRAY);
					input.setText("Type Here...");
					input.setCaretPosition(0);
				}
			}
		});
		input.addMouseListener(new MouseListener() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				
			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				if(input.getForeground().equals(Color.GRAY))
				{
					input.setCaretPosition(0);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				
			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				
			}

			@Override
			public void mouseExited(MouseEvent e) 
			{
				
			}
		});
		input.setEditable(false);
		input.setCaretPosition(0);
		input.setLineWrap(true);
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		input.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		contentPane.add(input, BorderLayout.SOUTH);
	}
	
	private void createOutputPane()
	{		
		output = new JTextPane();
		JScrollPane scrollpane = new JScrollPane(output);
		
		doc = output.getStyledDocument();
		systemStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(systemStyle, Color.decode("0x841617"));
		//StyleConstants.setBackground(systemStyle, Color.decode("0xFDF9D8"));
		StyleConstants.setBold(systemStyle, true);
		userStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(userStyle, Color.BLUE);
		//StyleConstants.setBackground(userStyle, Color.BLUE);
		StyleConstants.setBold(userStyle, true);
		chatStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(chatStyle, Color.GRAY);
		//StyleConstants.setBackground(chatStyle, Color.GRAY);
		StyleConstants.setBold(chatStyle, true);
		try 
		{
			doc.insertString(0, "", null);
		} 
		catch (BadLocationException e) 
		{
			e.printStackTrace();
		}
		
		output.setEditable(false);
		//output.setLineWrap(true);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		output.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		contentPane.add(scrollpane, BorderLayout.CENTER);
	}
	
	private void startChatThread() throws UnknownHostException, IOException
	{
		socket = new Socket("54.149.247.168", 3388);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		Thread t1 = new Thread(new Runnable()
		{
		    public void run()
		    {
		    	while(true)
		    	{
			    	String line = null;
					try 
					{
						line = in.readLine();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					
					if (line.startsWith("SUBMITNAME")) 
					{
						out.println(username);
					} 
					else if (line.startsWith("NAMEACCEPTED")) 
					{
						input.setEditable(true);
						input.grabFocus();
					}
					else if (line.startsWith("MESSAGE")) 
					{
						String[] temp = line.split(":", 2);

						output.setFont(new Font(getName(), Font.PLAIN, 20));
						try 
						{
							if (temp[0].substring(8).equals("SERVER"))
							{
								doc.insertString(doc.getLength(), temp[0].substring(8), systemStyle);
							}
							else if (temp[0].substring(8).equals(username))
							{
								doc.insertString(doc.getLength(), temp[0].substring(8), userStyle);
							}
							else
							{
								StyleConstants.setForeground(chatStyle, Color.DARK_GRAY);
								doc.insertString(doc.getLength(), temp[0].substring(8), chatStyle);
								StyleConstants.setForeground(chatStyle, Color.GRAY);
							}
						} 
						catch (BadLocationException e) 
						{
							e.printStackTrace();
						}

						output.setFont(new Font(getName(), Font.PLAIN, 20));
						try 
						{
							if (temp[0].substring(8).equals("SERVER"))
							{
								doc.insertString(doc.getLength(), ":" + temp[1] + "\n", systemStyle);
							}
							//else if (temp[0].substring(8).equals(username))
							//{
							//	doc.insertString(doc.getLength(), ":" + temp[1] + "\n", userStyle);
							//}
							else
							{
								doc.insertString(doc.getLength(), ":" + temp[1] + "\n", chatStyle);
							}
							output.setCaretPosition(output.getDocument().getLength());
						} 
						catch (BadLocationException e) 
						{
							e.printStackTrace();
						}
					}
					else if (line.startsWith("NAME"))
					{
						System.out.println("GOT HERE!!!!!!");
						boolean contains = true;
						for (String name: names)
						{
							if (name.equals(line.substring(5)))
							{
								contains = false;
							}
						}
						if (contains)
						{
							names.add(line.substring(5));
							listModel.addElement(line.substring(5));
							System.out.println(line.substring(5));
							chatRoomList.setSelectedValue(username, true);
						}
					}
					else if (line.startsWith("REMOVE"))
					{
						names.remove(line.substring(7));
						listModel.removeElement(line.substring(7));
					}
		    	}
		    }
		});  
		t1.start();
	}
}