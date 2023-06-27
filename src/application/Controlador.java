package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
public class Controlador extends Application {

    public static final int WIDTH = 800, HEIGHT = 800;

    public int flashed = 0, dark, ticks, indexPattern;

    public boolean creatingPattern = true;

    public ArrayList<Integer> pattern;

    public Random random;

    private boolean gameOver;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Configurar la ventana principal y el lienzo
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext g = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Simon");
        primaryStage.show();

        // Configurar el temporizador de animación
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                ticks++;

                if (ticks % 20 == 0) {
                    flashed = 0;

                    if (dark >= 0) {
                        dark--;
                    }
                }

                if (creatingPattern) {
                    if (dark <= 0) {
                        if (indexPattern >= pattern.size()) {
                            flashed = random.nextInt(40) % 4 + 1;
                            pattern.add(flashed);
                            indexPattern = 0;
                            creatingPattern = false;
                        } else {
                            flashed = pattern.get(indexPattern);
                            indexPattern++;
                        }

                        dark = 2;
                    }
                } else if (indexPattern == pattern.size()) {
                    creatingPattern = true;
                    indexPattern = 0;
                    dark = 2;
                }

                draw(g);
            }
        };
        timer.start();

        // Configurar el manejo de eventos del mouse
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);

        startGame();
    }

    public void startGame() {
        random = new Random();
        pattern = new ArrayList<>();
        indexPattern = 0;
        dark = 2;
        flashed = 0;
        ticks = 0;
        gameOver = false;
    }

    public void drawRectangle(GraphicsContext g, int x, int y, int width, int height, Color color) {
        g.setFill(color);
        g.fillRect(x, y, width, height);
    }

    public void drawCircle(GraphicsContext g, int x, int y, int radius, Color color) {
        g.setFill(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    public void drawText(GraphicsContext g, String text, int x, int y, int size, Color color) {
        g.setFill(color);
        g.setFont(new Font("Arial", size));
        g.setTextAlign(TextAlignment.CENTER);
        g.fillText(text, x, y);
    }

    public void drawGameOverText(GraphicsContext g) {
        if (gameOver) {
            drawText(g, "PERDISTE", WIDTH / 2, HEIGHT / 2 + 42, 142, Color.WHITE);
        } else {
            drawText(g, indexPattern + "/" + pattern.size(), WIDTH / 2, HEIGHT / 2 + 42, 142, Color.WHITE);
        }
    }

    public void draw(GraphicsContext g) {
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // Dibujar los rectángulos de colores
        if (flashed == 1) {
            drawRectangle(g, 0, 0, WIDTH / 2, HEIGHT / 2, Color.GREEN);
        } else {
            drawRectangle(g, 0, 0, WIDTH / 2, HEIGHT / 2, Color.GREEN.darker());
        }

        if (flashed == 2) {
            drawRectangle(g, WIDTH / 2, 0, WIDTH / 2, HEIGHT / 2, Color.RED);
        } else {
            drawRectangle(g, WIDTH / 2, 0, WIDTH / 2, HEIGHT / 2, Color.RED.darker());
        }

        if (flashed == 3) {
            drawRectangle(g, 0, HEIGHT / 2, WIDTH / 2, HEIGHT / 2, Color.ORANGE);
        } else {
            drawRectangle(g, 0, HEIGHT / 2, WIDTH / 2, HEIGHT / 2, Color.ORANGE.darker());
        }

        if (flashed == 4) {
            drawRectangle(g, WIDTH / 2, HEIGHT / 2, WIDTH / 2, HEIGHT / 2, Color.BLUE);
        } else {
            drawRectangle(g, WIDTH / 2, HEIGHT / 2, WIDTH / 2, HEIGHT / 2, Color.BLUE.darker());
        }

        // Dibujar el tablero y el círculo central
        drawRectangle(g, WIDTH / 2 - WIDTH / 12, 0, WIDTH / 7, HEIGHT, Color.BLACK);
        drawRectangle(g, 0, WIDTH / 2 - WIDTH / 12, WIDTH, HEIGHT / 7, Color.BLACK);

        g.setStroke(Color.GRAY);
        g.strokeArc(-100, -100, WIDTH + 200, HEIGHT + 200, 0, 360, javafx.scene.shape.ArcType.OPEN);

        g.setStroke(Color.BLACK);
        g.strokeOval(0, 0, WIDTH, HEIGHT);

        drawRectangle(g, 220, 220, 350, 350, Color.BLACK);

        // Dibujar el texto de fin de juego
        drawGameOverText(g);
    }

    public void handleMousePressed(MouseEvent e) {
        double x = e.getX(), y = e.getY();

        if (!creatingPattern && !gameOver) {
            if (x > 0 && x < WIDTH / 2 && y > 0 && y < HEIGHT / 2) {
                flashed = 1;
                ticks = 1;
            } else if (x > WIDTH / 2 && x < WIDTH && y > 0 && y < HEIGHT / 2) {
                flashed = 2;
                ticks = 1;
            } else if (x > 0 && x < WIDTH / 2 && y > HEIGHT / 2 && y < HEIGHT) {
                flashed = 3;
                ticks = 1;
            } else if (x > WIDTH / 2 && x < WIDTH && y > HEIGHT / 2 && y < HEIGHT) {
                flashed = 4;
                ticks = 1;
            }

            if (flashed != 0) {
                if (pattern.get(indexPattern) == flashed) {
                    indexPattern++;
                } else {
                    gameOver = true;
                }
            }
        } else if (gameOver) {
            startGame();
            gameOver = false;
        }
    }
    
    
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void Jugar(ActionEvent event) {
        Stage primaryStage = new Stage();

        // Configurar la ventana principal y el lienzo
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext g = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Simon");
        primaryStage.show();

        // Configurar el temporizador de animación
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                ticks++;

                if (ticks % 20 == 0) {
                    flashed = 0;

                    if (dark >= 0) {
                        dark--;
                    }
                }

                if (creatingPattern) {
                    if (dark <= 0) {
                        if (indexPattern >= pattern.size()) {
                            flashed = random.nextInt(40) % 4 + 1;
                            pattern.add(flashed);
                            indexPattern = 0;
                            creatingPattern = false;
                        } else {
                            flashed = pattern.get(indexPattern);
                            indexPattern++;
                        }

                        dark = 2;
                    }
                } else if (indexPattern == pattern.size()) {
                    creatingPattern = true;
                    indexPattern = 0;
                    dark = 2;
                }

                draw(g);
            }
        };
        timer.start();

        // Configurar el manejo de eventos del mouse
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);

        startGame();
    }

    public void Volver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("InterfazSB.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        reproducirSonido();
    }

    public void Tutorial(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Tutorial.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        reproducirSonido();
    }

    public void Opciones(ActionEvent event) throws IOException {
    
        Parent root = FXMLLoader.load(getClass().getResource("Opciones.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        reproducirSonido();
    }

    public void Creditos(ActionEvent event) throws IOException {
    	reproducirSonido();
        Parent root = FXMLLoader.load(getClass().getResource("Creditos.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
    }
    
    
//    Vincular el sonido de los botones, a cada boton hay que vincularlo
    private void reproducirSonido() {
    	
    	String soundFile = "/application/Sonido de botón para tus videos.mp3";
        Media soundMedia = new Media(getClass().getResource(soundFile).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(soundMedia);
        mediaPlayer.play();
    }
    
       public void Salir(ActionEvent event) {
    	reproducirSonido();
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
 }
  