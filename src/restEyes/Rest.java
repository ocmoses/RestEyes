package restEyes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Rest {

	static int restTime; //actually this is in minutes
	static JFrame frame;
	static int serial;
	static int numFiles = 100;
	static Random random = new Random();
	static ImagePanel panel;
	static private final String DURATION = "rest time.confg";
	static private final int DEFAULT_TIME = 15;
	static private int screenWidth;
	static private int screenHeight;
	static File jarFile = null;
	static String jarDir = null;
	static BufferedImage imageStr;

	//static File file = new File("bin/images");

	private static Timer timer;

	//Frame used to set the time...
	private static JFrame newTime;

	private static int toMillisecs(int i){
		return i * 60 * 1000;
	}

	public static void main(String[] args) {



				CodeSource codeSource = Rest.class.getProtectionDomain().getCodeSource();
				
				try {
					jarFile = new File(codeSource.getLocation().toURI().getPath());
					jarDir = jarFile.getParentFile().getPath() + "/bin/images"; 
					System.out.println("Path of jar file: " + jarDir);
					
					
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 


		//Get screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();		

		screenWidth = (int)screenSize.getWidth();
		screenHeight = (int)screenSize.getHeight();

		System.out.println("The frame size is " + screenSize.getWidth() + ", " + screenSize.getHeight());

		try(Scanner sc = new Scanner(new File(DURATION))){

			System.out.println("confg file was present...");

			try{

				restTime = sc.nextInt();
				System.out.println("restTime: " + restTime);

			}catch(Exception exception){
				restTime = DEFAULT_TIME;
			}
			
		}catch(FileNotFoundException e){
			
			System.out.println("No file confg present...");
			
			try(PrintWriter pw = new PrintWriter(DURATION)){
				System.out.println("confg file just created...");
				pw.print(DEFAULT_TIME);				
				restTime = DEFAULT_TIME;
			} catch (FileNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		
		run();

		//numFiles = jarFile.list().length;

		frame = new JFrame();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if(gd.isFullScreenSupported()){
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			frame.setUndecorated(true);

		}else{
			frame.setSize(screenWidth, screenHeight);
			frame.setUndecorated(true);
		}
		
		
		panel = new ImagePanel("src/images/image0.png");
		
		panel.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

				if(e.getButton() == MouseEvent.BUTTON1){
					//frame.setExtendedState(JFrame.ICONIFIED);
					frame.setVisible(false);
					//stop();
					//run();
					if(!timer.isRunning()){
						timer.restart();
						System.out.println("Timer restarted..." + "delay: " + timer.getDelay());
					}
					
				}else{

					Popup popup = new Popup();
					popup.show(panel, e.getX(), e.getY());

				}


			}

			@Override
			public void mouseReleased(MouseEvent e) { 

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});


		frame.setContentPane(panel);
		frame.setBackground(new Color(255, 255, 255, 0));

		frame.setVisible(true); 

		frame.setAlwaysOnTop(true);

		timer.start();
	}

	public static void run(){

		timer  = new Timer(toMillisecs(restTime), new ActionListener(){

			public void actionPerformed(ActionEvent e){

				serial = random.nextInt(numFiles) + 1;
				
				
				
				try {
					imageStr = ImageIO.read(getClass().getResource("/images/image" + serial + ".png"));					
					panel.setImage(new ImageIcon(imageStr).getImage());
					System.out.println("Chosen picture: " + "image" + serial);

					System.out.println("frame dimensions: " + frame.getWidth() + ", " + frame.getHeight());

					frame.setVisible(true);



					if(timer.isRunning()){
						timer.stop();
						System.out.println("Timer stopped...");
					}
					frame.repaint();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				

			}
		});
	}

	public static void stop(){
		if(timer.isRunning())
			timer.stop();

		if(newTime != null)
			newTime.dispose();

		if(frame.isVisible()){
			frame.setVisible(false);
		}

		frame.dispose();


	}

	static class ImagePanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Image image;


		public ImagePanel(String image){
			this.image = new ImageIcon(image).getImage();
		}

		public Image getImage() {
			return image;
		}



		public void setImage(Image image) {
			this.image = image;
		}



		protected void paintComponent(Graphics g){

			g.drawImage(image, 0, 0, screenWidth, screenHeight, this);


		}
	}

	static class Popup extends JPopupMenu{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		static JMenuItem changeTime = new JMenuItem("Change Duration");
		static JMenuItem suspend = new JMenuItem("Suspend");
		static JMenuItem shutdown = new JMenuItem("Exit");

		public Popup(){
			super("Preferences");
			this.add(changeTime);
			this.add(suspend);
			this.add(shutdown);

			addListeners();
		}

		public static void addListeners(){

			changeTime.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					newTime = new JFrame("Enter new time in Minutes...");

					JLabel feedBack = new JLabel(" ");
					feedBack.setForeground(Color.RED);

					JTextField tf = new JTextField(20);
					JButton set = new JButton("Set");

					set.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent e) {

							// TODO Auto-generated method stub

							if(tf.getText().equals("")){
								feedBack.setText("Enter something, schmuck?!");
							}else{

								try{

									int minutes = Integer.parseInt(tf.getText());

									try(PrintWriter writer = new PrintWriter(DURATION)){

										writer.print(minutes);
										writer.flush();

										//stop();
										restTime = minutes;
										run();

									}catch(FileNotFoundException ex){


									}

								}catch(Exception exeption){
									feedBack.setText("Enter only whole numbers, nitwit!!!");
									tf.setText("");
								}
							}


						}

					});

					JPanel timePanel = new JPanel();

					BoxLayout layout = new BoxLayout(timePanel, BoxLayout.Y_AXIS);

					timePanel.setLayout(layout);
					timePanel.setAlignmentY(SwingConstants.CENTER);

					timePanel.add(feedBack);
					feedBack.setAlignmentX(Component.CENTER_ALIGNMENT);
					timePanel.add(tf);
					tf.setHorizontalAlignment(JTextField.CENTER);
					tf.setAlignmentX(Component.CENTER_ALIGNMENT);
					timePanel.add(new JLabel(""));
					timePanel.add(set);
					set.setAlignmentX(Component.CENTER_ALIGNMENT);
					timePanel.add(new JLabel(""));
					newTime.setContentPane(timePanel);

					newTime.pack();
					newTime.setAlwaysOnTop(true);
					newTime.setLocationRelativeTo(null);
					newTime.setVisible(true);

				}

			});
		}



	}

}
