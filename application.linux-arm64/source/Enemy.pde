abstract class Enemy extends GameObject{
  
  float lethality;
  int score_modifier;
  
  Enemy(int velX, int velY, int posX, int posY){
    super(velY, velY, posX, posY);
  }
  
  abstract void tick();
  
  abstract void update();
  
  abstract void display();
  
  abstract boolean collision(Player player);
  
  abstract boolean destroy();
}
