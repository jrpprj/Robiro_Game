class HUD {
  float HEALTH = 100;
  int MAX_HEALTH = 100;

  void tick() {
    HEALTH = clamp(HEALTH, 0, MAX_HEALTH);
  }

  float clamp(float var, int min, int max) {
    if (var >= max) {
      return var = max;
    } else if (var <= min) {
      return var = min;
    } else
      return var;
  }

  void decreaseHealth(float x) {
    HEALTH -= x;
  }

  void increaseHealth(float x) {
    HEALTH += x;
  }
  
  void decreaseMax_Health(float x){
    MAX_HEALTH -= x;
  }
  
  void increaseMax_Health(float x){
    MAX_HEALTH += x;
  }

  void display(boolean percentage, Player player) {
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

  void update(boolean percentage, Player player) {
    display(percentage, player);
  }
}
