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
  
  void tick() {
    this.posY += velY;
  }
  
  void update(){
    //hitbox x coordinates
    this.hbX1 = (this.posX) * width/1200;
    this.hbX2 = (this.hbX1) + 75 * width/1200;
    //hitbox y coordinates
    this.hbY1 = (this.posY) * height/675;
    this.hbY2 = (this.hbY1) + 38 * height/675;
    display();
  }
  
  void display(){
    image(ennemy_box, posX * width/1200, posY * height/675, 75 * width/1200, 38 * height/675);
  }
  
  boolean destroy(){
    return (490 - this.posY) < 0; //TODO
  }
  
  boolean collision(Player player) {
    if (player.hbX2 >= this.hbX1 &&
      player.hbX1 <= this.hbX2 &&
      player.hbY2 >= this.hbY1 &&
      player.hbY1 <= this.hbY2) {
      return true;
    }
    return false;
  }
}
