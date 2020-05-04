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

  void right() {
    if (posX > 1125) {
      velX = 0;
    } else
      this.velX = 1;
  }

  void left() {
    if (posX < 0) {
      velX = 0;
    } else
      this.velX = -1;
  }

  void stand() {
    this.velX = 0;
  }

  void tick() {
    this.posX += 5 * velX;
    this.hud.tick();
  }

  void update() {
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

  void display() {
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

  void scoreDisplay() {
    fill(0);
    textSize(15 * height/675);

    text("Score : " + getScore(), width/2 - 45, 11*height/12);
  }

  int getScore() {
    return this.score;
  }

  int getLvlScore() {
    return this.lvlScore;
  }

  void setScore(int score) {
    this.score = score;
  }

  void setLvlScore(int score) {
    this.lvlScore = score;
  }

  void collision(Enemy enemy) {
    if (this.hbX2 >= enemy.hbX1 &&
      this.hbX1 <= enemy.hbX2 &&
      this.hbY2 >= enemy.hbY1 &&
      this.hbY1 <= enemy.hbY2) {
      hud.decreaseHealth(enemy.lethality);
    }
  }

  void heal() {
    hud.HEALTH = hud.MAX_HEALTH;
  }
}
