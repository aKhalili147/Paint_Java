package MiniProject;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class PaintingComponent extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final byte ERASER = 0;
    public static final byte PENCIL = 1;
    public static final byte LINE = 2;
    public static final byte BOX = 3;
    public static final byte ELLIPSE = 4;
    public static final byte ISOSCELES = 5;
    public static final byte RIGHT_TRIANGLE = 6;
    public static final byte DIAMOND = 7;
    public static final byte LINE_REPEATER = 8;
    private final JFileChooser jfc = new JFileChooser("C:/");
    private final String saveExtension = "png";
    private Color primaryColor = Color.BLACK;
    private Color secondaryColor = Color.YELLOW;
    private Color bgColor = Color.WHITE;
    private byte drawMode = PENCIL;
    private Graphics2D g2;
    //the (x,y) coordinates of points upon clicking and dragging
    private int currentX, currentY, oldX, oldY;
    private float lineThickness = 1.0f;
    private boolean fill = true; //whether or not to fill shape with 2nd color
    private BufferedImage image, prevImage;

    //Initializes the mouse listeners for the component
    public PaintingComponent() {
        addMouseListener(new PaintingComponent.ClickListener());
        addMouseMotionListener(new PaintingComponent.DragListener());
    }

    //ClickListener is used only for mousePressed
    private class ClickListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            //Only continue if the graphics context exists
            if (g2 != null) {
                g2.setColor(primaryColor);
                oldX = e.getX();
                oldY = e.getY();

                //The pencil and eraser do not need to preserve the image
                //Every other tool requires a previousImage to be saved
                //and repainted upon dragging.
                if (drawMode > 1) {
                    prevImage = deepCopy(image);
                }

                //If the eraser is selected, we can erase with one click
                if (drawMode == 0) {
                    g2.setColor(bgColor);
                    int ewidth = (int) (10 * lineThickness);
                    g2.setColor(bgColor);
                    g2.fill(new Rectangle(oldX - ewidth / 2,
                            oldY - ewidth / 2, ewidth, ewidth));
                    repaint();
                }
            }
        }
    }

    private class DragListener extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (g2 != null) {
                int[] xpts;
                int[] ypts;
                switch (drawMode) {
                    case ERASER:
                        currentX = e.getX();
                        currentY = e.getY();
                        int ewidth = (int) (10 * lineThickness);
                        g2.setColor(bgColor);
                        Rectangle rec = new Rectangle(currentX - ewidth / 2,
                                currentY - ewidth / 2, ewidth, ewidth);
                        g2.fill(rec);
                        repaint();
                        break;

                    case PENCIL:
                        currentX = e.getX();
                        currentY = e.getY();
                        BasicStroke bs = new BasicStroke(lineThickness);
                        g2.setStroke(bs);
                        g2.drawLine(oldX, oldY, currentX, currentY);
                        repaint();
                        oldX = currentX;
                        oldY = currentY;
                        break;

                    case LINE:
                        currentX = e.getX();
                        currentY = e.getY();
                        g2.drawImage(prevImage, 0, 0, null);
                        g2.drawLine(oldX, oldY, currentX, currentY);
                        repaint();
                        break;

                    case BOX:
                        currentX = e.getX();
                        currentY = e.getY();
                        g2.drawImage(prevImage, 0, 0, null);

                        if (currentX >= oldX && currentY >= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                Rectangle rec2 = new Rectangle(oldX, oldY,currentX - oldX, currentY - oldY);
                                g2.fill(rec2);
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Rectangle(oldX, oldY,currentX - oldX, currentY - oldY));
                        }

                        if (currentX >= oldX && currentY <= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                g2.fill(new Rectangle(oldX, currentY,currentX - oldX, oldY - currentY));
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Rectangle(oldX, currentY,currentX - oldX, oldY - currentY));
                        }

                        if (currentX <= oldX && currentY >= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                g2.fill(new Rectangle(currentX, oldY,oldX - currentX, currentY - oldY));
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Rectangle(currentX, oldY,oldX - currentX, currentY - oldY));
                        }
                        if (currentY <= oldY && currentY <= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                g2.fill(new Rectangle(currentX, currentY,oldX - currentX, oldY - currentY));
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Rectangle(currentX, currentY,oldX - currentX, oldY - currentY));
                        }
                        repaint();
                        break;

                    case ELLIPSE:
                        currentX = e.getX();
                        currentY = e.getY();
                        g2.drawImage(prevImage, 0, 0, null);

                        if (currentX >= oldX && currentY >= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                g2.fill(new Ellipse2D.Double(oldX, oldY,currentX - oldX, currentY - oldY));
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Ellipse2D.Double(oldX, oldY,currentX - oldX, currentY - oldY));
                        }

                        if (currentX >= oldX && currentY <= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                g2.fill(new Ellipse2D.Double(oldX, currentY,currentX - oldX, oldY - currentY));
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Ellipse2D.Double(oldX, currentY,currentX - oldX, oldY - currentY));
                        }

                        if (currentX <= oldX && currentY >= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                g2.fill(new Ellipse2D.Double(currentX, oldY,oldX - currentX, currentY - oldY));
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Ellipse2D.Double(currentX, oldY,oldX - currentX, currentY - oldY));
                        }
                        if (currentY <= oldY && currentY <= oldY) {
                            if (fill) {
                                g2.setColor(secondaryColor);
                                g2.fill(new Ellipse2D.Double(currentX, currentY,oldX - currentX, oldY - currentY));
                                g2.setColor(primaryColor);
                            }
                            g2.draw(new Ellipse2D.Double(currentX, currentY,oldX - currentX, oldY - currentY));
                        }
                        repaint();
                        break;

                    case ISOSCELES:
                        currentX = e.getX();
                        currentY = e.getY();
                        g2.drawImage(prevImage, 0, 0, null);
                        xpts = new int[3];
                        ypts = new int[3];
                        xpts[0] = oldX;
                        ypts[0] = oldY;
                        xpts[1] = (oldX + currentX) / 2;
                        ypts[1] = currentY;
                        xpts[2] = currentX;
                        ypts[2] = oldY;
                        if (fill) {
                            g2.setColor(secondaryColor);
                            g2.fillPolygon(xpts, ypts, 3);
                            g2.setColor(primaryColor);
                        }
                        g2.draw(new Polygon(xpts, ypts, 3));
                        repaint();
                        break;

                    case RIGHT_TRIANGLE:
                        clear();
                        currentX = e.getX();
                        currentY = e.getY();
                        g2.drawImage(prevImage, 0, 0, null);
                        xpts = new int[3];
                        ypts = new int[3];
                        xpts[0] = oldX;
                        ypts[0] = oldY;
                        xpts[1] = oldX;
                        ypts[1] = currentY;
                        xpts[2] = currentX;
                        ypts[2] = currentY;
                        if (fill) {
                            g2.setColor(secondaryColor);
                            g2.fillPolygon(xpts, ypts, 3);
                            g2.setColor(primaryColor);
                        }
                        g2.draw(new Polygon(xpts, ypts, 3));
                        repaint();
                        break;

                    case DIAMOND:
                        clear();
                        currentX = e.getX();
                        currentY = e.getY();
                        g2.drawImage(prevImage, 0, 0, null);
                        xpts = new int[4];
                        ypts = new int[4];
                        xpts[0] = (currentX + oldX) / 2;
                        ypts[0] = oldY;
                        xpts[1] = currentX;
                        ypts[1] = (currentY + oldY) / 2;
                        xpts[2] = (currentX + oldX) / 2;
                        ypts[2] = currentY;
                        xpts[3] = oldX;
                        ypts[3] = (currentY + oldY) / 2;
                        if (fill) {
                            g2.setColor(secondaryColor);
                            g2.fillPolygon(xpts, ypts, 4);
                            g2.setColor(primaryColor);
                        }
                        g2.draw(new Polygon(xpts, ypts, 4));
                        repaint();
                        break;

                    case LINE_REPEATER:
                        currentX = e.getX();
                        currentY = e.getY();
                        g2.drawLine(oldX, oldY, currentX, currentY);
                        repaint();
                        break;
                }
            }
        }
    }


    //Toggles fill (whether a shape is filled in by the secondary color)
    public void setFill(boolean fill) {
        this.fill = fill;
    }

    //Sets the draw mode.
    public void setDrawMode(byte drawMode) {
        this.drawMode = drawMode;
    }

    @Override
    public void paintComponent(Graphics g) {
        //If the image is null, create a blank image
        if (image == null) {
            image = (BufferedImage) createImage(this.getSize().width,this.getSize().height);
            g2 = (Graphics2D) image.getGraphics();
            g2.setColor(primaryColor);
            g2.setStroke(new BasicStroke(lineThickness));
            clear();
        }
        g2.setColor(primaryColor);
        g.drawImage(image, 0, 0, null);
    }

    //This code was taken from:
    //http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
    //Copies one image into another (rather than pointing from a normal assign)
    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    //Sets the line thickness
    public void setLineThickness(float f) {
        lineThickness = f;
        g2.setStroke(new BasicStroke(f)); // Constructs a solid BasicStroke with the specified line width and with default values for the cap and join styles.
        System.out.println("Line thickness set to " + (int) f);
    }

    //Loads an image by prompting the user
    public void load() throws IOException {
        BufferedImage tmpImage;
        int status = jfc.showOpenDialog(this);
        File file = jfc.getSelectedFile(); // Returns the selected file.
        if (status == JFileChooser.APPROVE_OPTION) { //Return value if approve (yes, ok) is chosen.
            tmpImage = ImageIO.read(file);
            g2.drawImage(tmpImage, 0, 0, null);
            repaint();
            System.out.println("Image Opened: " + file.toString());
        }
        if (status == JFileChooser.CANCEL_OPTION) {
            System.out.println("Open canceled.");
        }
    }


    //Saves an image by prompting the user
    public void save() throws IOException {
    	File newFile = new File("untitled." + saveExtension);
        jfc.setSelectedFile(newFile);
        int status = jfc.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            ImageIO.write(image, saveExtension, new File(jfc.getSelectedFile().toString() + "." + saveExtension));
            System.out.println("Image saved: "+ jfc.getSelectedFile().toString());
        }
        if (status == JFileChooser.CANCEL_OPTION) {
            System.out.println("Save canceled.");
        }
    }

    // wipe the screen
    public void clear() {
        if (g2 == null) {
            repaint();
        }
        Color temp = g2.getColor();
        g2.setColor(bgColor);
        g2.fill(new Rectangle(this.getWidth(), this.getHeight()));
        g2.setColor(temp);
        repaint();
    }

    //sets the primary color
    public void setPrimaryColor(Color c) {
        primaryColor = c;
        System.out.println("Primary color changed.");
    }

    //sets the secondary color
    public void setSecondaryColor(Color c) {
        secondaryColor = c;
        System.out.println("Secondary color changed.");
    }
}