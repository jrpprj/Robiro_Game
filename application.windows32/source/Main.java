import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.awt.PSurfaceAWT; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Main extends PApplet {



final int maxAlpha = 256;
float myalpha = 0;

PFont myfont;

PImage p1, right, left;

PImage ennemy_box;

PImage headsUpDisplay;

PImage start;

PImage icone;

PImage ScreenShot;

PImage pressed_button, button, background, mountain, clouds, canvas;
String [] my_menus = {"Play", "Scores", "Toggle full screen", "Close the game"};
int N_MENUS = my_menus.length;

int posi_cursor = 0;

int button_width = 300;
int button_height = 40;

int y1;
int padding;

int x = 0;
int copyX = 0;

int resize;

int [] button_x1, button_y1, button_x2, button_y2;

int[] posRight = new int[24];

int gameSpeed;
int speedCount;

boolean fullScreen;
boolean running;

FSM fsm;
LVL lvl;

Player player;
ArrayList<Enemy> enemies;

public void settings() {
  size(1200, 675);

  resize = width;
}

public void setup() {
  icone = loadImage("./images/icone.png");

  fullScreen = false;
  running = false;

  //setup loading screen

  surface.setResizable(true);
  PSurfaceAWT awtSurface = (PSurfaceAWT)surface;
  awtSurface.setIcon(icone);
  PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas)awtSurface.getNative();
  smoothCanvas.getFrame().setAlwaysOnTop(true);
  smoothCanvas.getFrame().removeNotify();
  smoothCanvas.getFrame().setUndecorated(true);
  smoothCanvas.getFrame().addNotify();

  //setup surface
  surface.setTitle("Ribania");
  surface.setResizable(true);

  //setup images
  /* player */
  p1 = loadImage("./images/still.png");
  right = loadImage("./images/start_right_animation.png");
  left = loadImage("./images/start_left_animation.png");

  headsUpDisplay = loadImage("./images/HUD.png");
  /* enemy */
  ennemy_box = loadImage("./images/box.png");
  /* menu */
  start = loadImage("./Images/start.png");
  pressed_button = loadImage("./images/button1.png");
  button = loadImage("./images/button.png");
  //canvas = loadImage("./images/canvas.png");
  /* scenaries */
  background = loadImage("./images/BACK.png");
  mountain = loadImage("./images/Mountain.png");
  clouds = loadImage("./images/Clouds.png");

  speedCount = 0;
  gameSpeed = 300;

  //setup menu buttons
  button_x1 = new int[N_MENUS];
  button_y1 = new int[N_MENUS];
  button_x2 = new int[N_MENUS];
  button_y2 = new int[N_MENUS];

  y1 = 0;
  padding = 20;

  for (int i = 0; i < N_MENUS; i++) {
    button_x1[i] = width/2 - 150;
    button_y1[i] = 4*height/10 + y1;
    button_x2[i] = button_x1[i] + button_width;
    button_y2[i] = button_y1[i] + button_height;

    y1 += button_height + padding;
  }

  fsm = FSM.MENU;
  lvl = LVL.ZERO;
}

public void draw() {
  if (millis() < 5000) {
    //image(ScreenShot, 0, 0);
    background(0);
    fill(255, myalpha);
    rect(0, 0, width, height);
    myfont = createFont("Arial", 75, true);
    textFont(myfont);
    text("Robiro", width/4, height/2 + height/18);
    myfont = createFont("Arial", 50, true);
    textFont(myfont);
    text("by Patrick", width - width/3 - width/9, height/2 + height/18);

    myalpha=(myalpha+0.5f)%maxAlpha;
    image(start, 0, 0);
  } else {
    switch(fsm) {
    case MENU:
      display_menu();
      break;
    case GAME:
    case MOVE_LEFT:
    case MOVE_RIGHT: 
      game();
      update();
      tick();
      speedCount++;
      if (speedCount == gameSpeed) {
        speedCount = 0;
        switch(lvl) {
        case ZERO:
          break;
        case ONE:
          level1loop();
          break;
        default:
          break;
        }
      }
      break;

    default:
      break;
    }
  }
}

public void mouseClicked() {
  if (fsm == FSM.MENU) {
    for (int i = 0; i < N_MENUS; i++) {
      if (mouseX > button_x1[i] && mouseX < button_x2[i] && mouseY > button_y1[i] && mouseY < button_y2[i]) {
        if (i == 0) {
          fsm = FSM.GAME;
          restart();
        }
        if (i == 2) {
          if (!fullScreen) {
            surface.setSize(displayWidth, displayHeight);
            fullScreen = true;
          }
          if (fullScreen)
          {
            surface.setSize(1200, 675);
            fullScreen = false;
          }
        }
        if (i == 3) {
          exit();
        }
      }
    }
  }
  if (fsm == FSM.GAME) {
    if (mouseX > 578 * width/1200 && mouseX < 623 * width/1200 && mouseY > 565 * height/675 && mouseY < 585 * height/675) {
      player.percentage = !player.percentage;
    }
  }
}

public void mouseMoved() {
  for (int i = 0; i < N_MENUS; i++) {
    if (mouseX > button_x1[i] && mouseX < button_x2[i] && mouseY > button_y1[i] && mouseY < button_y2[i]) {
      posi_cursor = i;
    }
  }
}

public void keyReleased() {
  if (fsm != FSM.MENU) {
    fsm = FSM.GAME;
  }
}

public void keyPressed() {
  if (fsm != FSM.MENU) {
    switch(key) {
    case 'd':
    case 'D':
      fsm = FSM.MOVE_RIGHT; 
      break;
    case 'q':
    case 'Q':
      fsm = FSM.MOVE_LEFT; 
      break;
    case 'p':
    case 'P':
      fsm = FSM.MENU;
      break;
      /*
    case 'o':
       case 'O':
       screenFull();
       */
    default :
      break;
    }
  }
}

public void update() {
  //update player
  player.update();

  //update enemies and player health
  for (int i = 0; i < enemies.size(); i++) {
    enemies.get(i).update();
    player.collision(enemies.get(i));
  }

  enemies = destroy(enemies, player);
}

public void tick() {
  player.tick();

  //update enemies
  for (int i = 0; i < enemies.size(); i++) {
    enemies.get(i).tick();
  }
}

public void game() {
  image(background, 0, 0, width, height);
  image(clouds, x/3, 0, 3*width, height);
  image(clouds, x/3+clouds.width, 0, 3*width, height);
  image(mountain, 0, 0, 2*width, height);
  //image(canvas, 0, 0, width, height);
  x--;
  if ((x < -3*width)  || (resize != width)) {
    x = 0;
    resize = width;
  }
}

public void restart() {
  player = new Player(0, 0, 0, 0);

  level1();
}

public void level1() {
  lvl = LVL.ONE;

  enemies = new ArrayList<Enemy>();

  enemies.add(0, new Box(0, 1, 0, -38));

  player.heal();

  player.setLvlScore(5000);
}

public void level1loop() {
  if (player.getScore() < 480) {
    for (int i = 0; i < 15; i++)
      enemies.add(0, new Box(0, 1, ((int)random(16)) * 75, -38));
  } else {
    if (player.getScore() < 500) {
      player.setScore(500);
    } else {
      gameSpeed = 100;
      if (player.getScore() < 1000) {
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
        enemies.add(0, new Box(0, 5, ((int)random(16)) * 75, -38));
      } else {
        gameSpeed = 25;
        if (player.getScore() < 2000){
          enemies.add(0, new Box(0, 10, ((int)random(16)) * 75, -38));
          enemies.add(0, new Box(0, 10, ((int)random(16)) * 75, -38));
          enemies.add(0, new Box(0, 10, ((int)random(16)) * 75, -38));
        }
      }
    }
  }
}

public void display_menu() {
  y1 = 0;
  padding = 20;

  for (int i = 0; i < N_MENUS; i++) {
    button_x1[i] = width/2 - 150;
    button_y1[i] = 4*height/10 + y1;
    button_x2[i] = button_x1[i] + button_width;
    button_y2[i] = button_y1[i] + button_height;

    y1 += button_height + padding;
  }
  image(background, x, 0, width, height);
  image(background, x+width, 0, width, height);
  image(background, x+2*background.width, 0, width, height);
  image(background, x+3*background.width, 0, width, height);
  image(clouds, x/3, 0, 3*width, height);
  image(clouds, x/3+clouds.width, 0, 3*width, height);
  image(mountain, 2*x/3, 0, 2*width, height);
  image(mountain, 2*x/3+mountain.width, 0, 2*width, height);
  image(mountain, 2*x/3+2*mountain.width, 0, 2*width, height);

  //image(canvas, 0, 0, width, height);

  /* display width */
  //fill(255);
  //textSize(25);
  //text("width :" + width, 30, 30);
  //text("resize :" + width, 30, 50);

  x--;
  if ((x < -3*width) || (resize != width)) {
    x = 0;
    resize = width;
  }
  display_the_menus();
}

public void display_the_menus() {
  fill(255);
  textSize(25);

  for (int i = 0; i < N_MENUS; i++) {
    PImage my_image = (posi_cursor == i) ? pressed_button : button;
    image(my_image, button_x1[i], button_y1[i]);

    int x1 = (button_width - (int)textWidth(my_menus[i])) / 2;

    text(my_menus[i], button_x1[i] + x1, 30 + button_y1[i]);
  }
}

public ArrayList<Enemy> destroy(ArrayList<Enemy> list1, Player player) {
  ArrayList<Enemy> list2 = new ArrayList<Enemy>();
  for (int i = 0; i < list1.size(); i++) {
    if (!(list1.get(i).collision(player))) {
      if (!(list1.get(i).destroy()))
        list2.add(list1.get(i));
      else
        player.score += list1.get(i).score_modifier;
    }
  }
  return list2;
}
class Box extends Enemy{
  
  Box (int velX, int velY, int posX, int posY){
    super(velX, velY, posX, posY);
    
    //hitbox x coordinates
    this.hbX1 = (this.posX) * width/1200;
    this.hbX2 = (this.hbX1) + 75 * width/1200;
    //hitbox y coordinates
    this.hbY1 = (this.posY) * height/675;
    this.hbY2 = (this.hbY1) + 38 * height/675;
    
    this.lethality = 10;
    this.score_modifier = 1;
  }
  
  public void tick() {
    this.posY += velY;
  }
  
  public void update(){
    //hitbox x coordinates
    this.hbX1 = (this.posX) * width/1200;
    this.hbX2 = (this.hbX1) + 75 * width/1200;
    //hitbox y coordinates
    this.hbY1 = (this.posY) * height/675;
    this.hbY2 = (this.hbY1) + 38 * height/675;
    display();
  }
  
  public void display(){
    image(ennemy_box, posX * width/1200, posY * height/675, 75 * width/1200, 38 * height/675);
  }
  
  public boolean destroy(){
    return (490 - this.posY) < 0; //TODO
  }
  
  public boolean collision(Player player) {
    if (player.hbX2 >= this.hbX1 &&
      player.hbX1 <= this.hbX2 &&
      player.hbY2 >= this.hbY1 &&
      player.hbY1 <= this.hbY2) {
      return true;
    }
    return false;
  }
}
abstract class Enemy extends GameObject{
  
  float lethality;
  int score_modifier;
  
  Enemy(int velX, int velY, int posX, int posY){
    super(velY, velY, posX, posY);
  }
  
  public abstract void tick();
  
  public abstract void update();
  
  public abstract void display();
  
  public abstract boolean collision(Player player);
  
  public abstract boolean destroy();
}
public enum FSM{
  MENU,
  GAME,
  MOVE_RIGHT,
  MOVE_LEFT
}
abstract class GameObject{
  int posX;
  int posY;
  
  //hit box coordinates
  int hbX1, hbX2;
  int hbY1, hbY2;
  
  int velX;
  int velY;
  
  GameObject(int velX, int velY, int posX, int posY){
    this.posX = posX;
    this.posY = posY;
    
    this.velX = velX;
    this.velY = velY;
  }
  
  public abstract void tick();

  public int getX() {
    return this.posX;
  }

  public int getY() {
    return this.posY;
  }

  public int velocityX() {
    return this.velX;
  }
  
  public int velocityY() {
    return this.velY;
  }

  public abstract void update();

  public abstract void display();
}
class HUD {
  float HEALTH = 100;
  int MAX_HEALTH = 100;

  public void tick() {
    HEALTH = clamp(HEALTH, 0, MAX_HEALTH);
  }

  public float clamp(float var, int min, int max) {
    if (var >= max) {
      return var = max;
    } else if (var <= min) {
      return var = min;
    } else
      return var;
  }

  public void decreaseHealth(float x) {
    HEALTH -= x;
  }

  public void increaseHealth(float x) {
    HEALTH += x;
  }
  
  public void decreaseMax_Health(float x){
    MAX_HEALTH -= x;
  }
  
  public void increaseMax_Health(float x){
    MAX_HEALTH += x;
  }

  public void display(boolean percentage, Player player) {
    fill(215, 32, 28);
    rect(463 * width/1200, 560 * height/675, HEALTH/MAX_HEALTH * 275 * width/1200, 17 * height/675);
    fill(0, 128, 255);
    rect(0, 660 * height/675, (100 * player.getScore())/player.getLvlScore() * width * width/120000, 15 * height/675);
    image(headsUpDisplay, 0, 0, width, height);
    
    if (percentage){
      fill(0);
      textSize(10 * height/675);
      text((int)(HEALTH/MAX_HEALTH * 100) + "%", 591 * width/1200, 573 * height/675);
    }
    else{
      fill(0);
      textSize(10 * height/675);
      text((int)HEALTH, 591 * width/1200, 573 * height/675);
    }
  }

  public void update(boolean percentage, Player player) {
    display(percentage, player);
  }
}
public enum LVL{
  ZERO,
  ONE,
  TWO,
  THREE,
  FOUR,
  FIVE,
  SIX,
  SEVEN,
  EIGHT,
  NINE
}
class Player extends GameObject {

  int i = 0;

  HUD hud;

  boolean percentage;

  boolean alive;

  int score;
  int lvlScore;

  Player(int velX, int velY, int posX, int posY) {
    super(velX, velY, posX, posY);

    this.posX = 563;
    this.posY = 412;

    //hitbox x coordinates
    this.hbX1 = (this.posX + 12) * width/1200;
    this.hbX2 = (this.hbX1) + 51 * width/1200;
    //hitbox y coordinates
    this.hbY1 = (this.posY) * height/675;
    this.hbY2 = (this.hbY1) + 122 * height/675;

    this.velX = 0;
    this.velY = 0;

    this.percentage = true;
    this.alive = true;

    this.score = 0;
    this.lvlScore = 9999;

    hud = new HUD();
  }

  public void right() {
    if (posX > 1125) {
      velX = 0;
    } else
      this.velX = 1;
  }

  public void left() {
    if (posX < 0) {
      velX = 0;
    } else
      this.velX = -1;
  }

  public void stand() {
    this.velX = 0;
  }

  public void tick() {
    this.posX += 5 * velX;
    this.hud.tick();
  }

  public void update() {
    switch(fsm) {
    case MOVE_RIGHT:
      this.right();
      this.display();
      break;
    case MOVE_LEFT:
      this.left();
      this.display();
      break;
    case GAME:
      this.stand();
      this.display();
      break;
    default:
      break;
    }
  }

  public void display() {
    hud.update(percentage, player);
    scoreDisplay();
    if (this.velocityX() == 1) {
      //modify hitbox x coordinates
      this.hbX1 = (this.posX + 20) * width/1200;
      this.hbX2 = (this.hbX1) + 35 * width/1200;

      this.hbY1 = (this.posY) * height/675;
      this.hbY2 = (this.hbY1) + 122 * height/675;

      image(right.get(75*(i %48), 0, 75, 122), this.getX() * width/1200, this.getY() * height/675, 75 * width/1200, 122 * height/675);
      image(right.get(75*(i %48), 0, 75, 122), this.getX() * width/1200, this.getY() * height/675, 75 * width/1200, 122 * height/675);
    }
    if (this.velocityX() == -1) {
      //modify hitbox x coordinates
      this.hbX1 = (this.posX + 20) * width/1200;
      this.hbX2 = (this.hbX1) + 35 * width/1200;

      this.hbY1 = (this.posY) * height/675;
      this.hbY2 = (this.hbY1) + 122 * height/675;

      image(left.get(75*(i %48), 0, 75, 122), this.getX() * width/1200, this.getY() * height/675, 75 * width/1200, 122 * height/675);
      image(left.get(75*(i %48), 0, 75, 122), this.getX() * width/1200, this.getY() * height/675, 75 * width/1200, 122 * height/675);
    }
    if (this.velocityX() == 0) {
      //modify hitbox x coordinates
      this.hbX1 = (this.posX + 12) * width/1200;
      this.hbX2 = (this.hbX1) + 51 * width/1200;
      //hitbox y coordinates
      this.hbY1 = (this.posY) * height/675;
      this.hbY2 = (this.hbY1) + 122 * height/675;

      image(p1, this.getX() * width/1200, this.getY() * height/675, 75 * width/1200, 122 * height/675);
    }
    i++;
    i = i%96;
  }

  public void scoreDisplay() {
    fill(0);
    textSize(15 * height/675);

    text("Score : " + getScore(), width/2 - 45, 11*height/12);
  }

  public int getScore() {
    return this.score;
  }

  public int getLvlScore() {
    return this.lvlScore;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public void setLvlScore(int score) {
    this.lvlScore = score;
  }

  public void collision(Enemy enemy) {
    if (this.hbX2 >= enemy.hbX1 &&
      this.hbX1 <= enemy.hbX2 &&
      this.hbY2 >= enemy.hbY1 &&
      this.hbY1 <= enemy.hbY2) {
      hud.decreaseHealth(enemy.lethality);
    }
  }

  public void heal() {
    hud.HEALTH = hud.MAX_HEALTH;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
