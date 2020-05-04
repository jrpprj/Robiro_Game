import processing.awt.PSurfaceAWT;

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

void settings() {
  size(1200, 675);

  resize = width;
}

void setup() {
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

void draw() {
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

    myalpha=(myalpha+0.5)%maxAlpha;
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

void mouseClicked() {
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

void mouseMoved() {
  for (int i = 0; i < N_MENUS; i++) {
    if (mouseX > button_x1[i] && mouseX < button_x2[i] && mouseY > button_y1[i] && mouseY < button_y2[i]) {
      posi_cursor = i;
    }
  }
}

void keyReleased() {
  if (fsm != FSM.MENU) {
    fsm = FSM.GAME;
  }
}

void keyPressed() {
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

void update() {
  //update player
  player.update();

  //update enemies and player health
  for (int i = 0; i < enemies.size(); i++) {
    enemies.get(i).update();
    player.collision(enemies.get(i));
  }

  enemies = destroy(enemies, player);
}

void tick() {
  player.tick();

  //update enemies
  for (int i = 0; i < enemies.size(); i++) {
    enemies.get(i).tick();
  }
}

void game() {
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

void restart() {
  player = new Player(0, 0, 0, 0);

  level1();
}

void level1() {
  lvl = LVL.ONE;

  enemies = new ArrayList<Enemy>();

  enemies.add(0, new Box(0, 1, 0, -38));

  player.heal();

  player.setLvlScore(5000);
}

void level1loop() {
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

void display_menu() {
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

void display_the_menus() {
  fill(255);
  textSize(25);

  for (int i = 0; i < N_MENUS; i++) {
    PImage my_image = (posi_cursor == i) ? pressed_button : button;
    image(my_image, button_x1[i], button_y1[i]);

    int x1 = (button_width - (int)textWidth(my_menus[i])) / 2;

    text(my_menus[i], button_x1[i] + x1, 30 + button_y1[i]);
  }
}

ArrayList<Enemy> destroy(ArrayList<Enemy> list1, Player player) {
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
