import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class Login extends JFrame
{
	private String name;
	private String username;
	private BufferedReader in;
	private PrintWriter out;
	private String[] newUser;
	
	public Login()
	{
		new JFrame();
		this.setTitle("Campus Letters Client Login");
		//this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//content
		JPanel logoPane = new JPanel();
		JPanel credentialPane = new JPanel();
		JPanel infoPane = new JPanel();
		
		logoPane.setBackground(Color.WHITE);
		credentialPane.setBackground(Color.WHITE);
		infoPane.setBackground(Color.WHITE);
		
		logoPane.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		infoPane.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		
		this.add(logoPane, BorderLayout.NORTH);
		this.add(credentialPane, BorderLayout.CENTER);
		this.add(infoPane, BorderLayout.SOUTH);
		
		JLabel logo = new JLabel("", SwingConstants.CENTER);
		logo.setIcon(new ImageIcon(Login.class.getResource("/resources/cl.png")));
		
		logoPane.add(logo);
		
		JTextField userField = new JTextField();
		userField.setBorder(new TitledBorder("Username"));
		userField.setFont(new Font(getName(), Font.PLAIN, 20));
		
		JPasswordField passField = new JPasswordField();
		passField.setBorder(new TitledBorder("Password"));
		passField.setFont(new Font(getName(), Font.PLAIN, 20));
		
		userField.setColumns(15);
		passField.setColumns(15);
		
		credentialPane.setLayout(new GridLayout(4, 1));
		credentialPane.setBorder(BorderFactory.createEmptyBorder(10,40,10,40));
		
		JPanel pane0 = new JPanel();
		JPanel pane1 = new JPanel();
		JPanel pane2 = new JPanel();
		JPanel pane3 = new JPanel();
		
		pane0.setBackground(Color.WHITE);
		pane1.setBackground(Color.WHITE);
		pane2.setBackground(Color.WHITE);
		pane3.setBackground(Color.WHITE);
		
		pane0.setLayout(new FlowLayout(FlowLayout.CENTER));
		pane1.setLayout(new FlowLayout(FlowLayout.CENTER));
		pane2.setLayout(new FlowLayout(FlowLayout.CENTER));
		pane3.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JLabel userDialog = new JLabel("");
		userDialog.setFont(new Font(getName(), Font.ITALIC, 15));
		
		pane0.add(userField);
		pane1.add(passField);
		pane3.add(userDialog);
		
		JButton signIn = new JButton("Sign In");
		signIn.setEnabled(false);
		signIn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				signIn.setEnabled(false);
				userDialog.setForeground(Color.BLACK);
				userDialog.setText("Connecting to server...");
				StringBuilder builder = new StringBuilder();
				builder.append(passField.getPassword());
				Thread t1 = new Thread(new Runnable() 
				{
				    public void run()
				    {
				    	ActiveDirectoryAuthenticate user = new ActiveDirectoryAuthenticate(userField.getText().toLowerCase(), builder.toString());
				    	try 
						{
				    		try 
				    		{
								Thread.sleep(300);
							} 
				    		catch (InterruptedException e1) 
				    		{
								e1.printStackTrace();
							}
							name = user.authenticate();
							userDialog.setText("Welcome, " + name + "!");
							try 
							{
								Thread.sleep(500);
							} 
							catch (InterruptedException e) 
							{
								e.printStackTrace();
							}
							username = new String(userField.getText().toLowerCase());
							isAuthenticated();
						} 
						catch (NamingException | IOException e1) 
						{
							signIn.setEnabled(true);
							userDialog.setForeground(Color.RED);
							userDialog.setText("Invalid username or password!");
						}
				    }
				});  
				t1.start();
			}
		});
		JButton createacct = new JButton("Create Account");
		createacct.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JFrame frame = new JFrame("Create Account");
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBorder(BorderFactory.createEmptyBorder(10,40,10,40));
				
				frame.setContentPane(panel);
				panel.setLayout(new GridLayout(5,1));
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				JTextField first = new JTextField();
				first.setBorder(new TitledBorder("First Name"));
				first.setFont(new Font(getName(), Font.PLAIN, 20));
				first.setColumns(15);
				
				JTextField last = new JTextField();
				last.setBorder(new TitledBorder("Last Name"));
				last.setFont(new Font(getName(), Font.PLAIN, 20));
				last.setColumns(15);
				
				JTextField user = new JTextField();
				user.setBorder(new TitledBorder("Username"));
				user.setFont(new Font(getName(), Font.PLAIN, 20));
				user.setColumns(15);
				
				JPasswordField pass = new JPasswordField();
				pass.setBorder(new TitledBorder("Password"));
				pass.setFont(new Font(getName(), Font.PLAIN, 20));
				pass.setColumns(15);
				
				JButton submit = new JButton("Submit");
				submit.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						Thread t1 = new Thread(new Runnable()
						{
						    public void run()
						    {
						    	newUser = new String[4];
								newUser[0] = first.getText();
								newUser[1] = last.getText();
								newUser[2] = user.getText();
								StringBuilder builder = new StringBuilder();
								builder.append(pass.getPassword());
								newUser[3] = builder.toString();
								
						    	Socket socket = null;
								try 
								{
									socket = new Socket("54.149.247.168", 3388);
								} 
								catch (IOException e2) 
								{
									e2.printStackTrace();
								}
								try 
								{
									in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								} 
								catch (IOException e1) 
								{
									e1.printStackTrace();
								}
								try 
								{
									out = new PrintWriter(socket.getOutputStream(), true);
								} 
								catch (IOException e1) 
								{
									e1.printStackTrace();
								}
								
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
									out.println("NEW");
								}
								out.println("add " + newUser[0] + " " + newUser[1] + " " + newUser[2] + " " + newUser[3]);
								try 
								{
									socket.close();
								} 
								catch (IOException e1) 
								{
									e1.printStackTrace();
								}
						    }
						});
						t1.start();
						frame.dispose();
					}
				});
				
				pass.addKeyListener(new KeyListener()
				{
					@Override
					public void keyPressed(KeyEvent e) 
					{
						
					}

					@Override
					public void keyReleased(KeyEvent e) 
					{
						if(e.getKeyCode() == KeyEvent.VK_ENTER)
						{
							submit.doClick();
						}
					}

					@Override
					public void keyTyped(KeyEvent e) 
					{
						
					}
				});
				
				JPanel panel1 = new JPanel();
				JPanel panel2 = new JPanel();
				JPanel panel3 = new JPanel();
				JPanel panel4 = new JPanel();
				JPanel panel5 = new JPanel();
				
				panel1.setLayout(new FlowLayout(FlowLayout.CENTER));
				panel2.setLayout(new FlowLayout(FlowLayout.CENTER));
				panel3.setLayout(new FlowLayout(FlowLayout.CENTER));
				panel4.setLayout(new FlowLayout(FlowLayout.CENTER));
				panel5.setLayout(new FlowLayout(FlowLayout.CENTER));
				
				panel1.setBackground(Color.WHITE);
				panel2.setBackground(Color.WHITE);
				panel3.setBackground(Color.WHITE);
				panel4.setBackground(Color.WHITE);
				panel5.setBackground(Color.WHITE);
				
				panel1.add(first);
				panel2.add(last);
				panel3.add(user);
				panel4.add(pass);
				panel5.add(submit);
				
				panel.add(panel1);
				panel.add(panel2);
				panel.add(panel3);
				panel.add(panel4);
				panel.add(panel5);
				
				frame.setSize(400, 400);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
		
		passField.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e) 
			{
				
			}

			@Override
			public void keyReleased(KeyEvent e) 
			{
				if(passField.getPassword().length > 0)
				{
					signIn.setEnabled(true);
				}
				else
				{
					signIn.setEnabled(false);
				}
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					signIn.doClick();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) 
			{
				
			}
		});
		pane2.add(createacct);
		pane2.add(signIn);
		
		credentialPane.add(pane0);
		credentialPane.add(pane1);
		credentialPane.add(pane2);
		credentialPane.add(pane3);
		
		JLabel info = new JLabel("2017 Campus Letters");
		
		infoPane.add(info, SwingConstants.CENTER);
		//end of content
		
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	@SuppressWarnings({ "deprecation" })
	private void isAuthenticated() throws UnknownHostException, IOException
	{
		this.hide();
		@SuppressWarnings("unused")
		ChatWindow window = new ChatWindow("Chat Client 1.0", username);
	}
}