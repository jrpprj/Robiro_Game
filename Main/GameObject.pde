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
  
  abstract void tick();

  int getX() {
    return this.posX;
  }

  int getY() {
    return this.posY;
  }

  int velocityX() {
    return this.velX;
  }
  
  int velocityY() {
    return this.velY;
  }

  abstract void update();

  abstract void display();
}
