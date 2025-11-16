import GaFr.GFGame;
import GaFr.GFStamp;
import GaFr.GFFont;
import GaFr.Gfx;
import GaFr.GFU;
import GaFr.GFM;
import GaFr.GFKey;
import GaFr.Easings;
import java.util.ArrayList;
import java.util.Iterator;

public class Game extends GFGame {
  GFStamp Dish;
  ArrayList<Dish> dishes = new ArrayList<>();
  public static final float posX = 250; // fixed x position that dish appears
  public static final int timeInterval = 220;// interval time between dishes
  // mouse interaction
  public Dish selectedDish = null;  // stores the dish being selected
  public int selectedDishID; // stores the index of the dish being selected
  public boolean isDragging = false; // whether dragging is active
  public int[][] containerCords = { { 100, 380 }, { 380, 380 }, { 650, 380 }, { 120, 120 }, { 520, 120 } }; // coordinates
  public int score = 0; //score counting for correctly placed dishes
  public int numDish = 10; //number of dishes to initialize
  public int numErrors = 3; // containers
  //ending and winning condition
  boolean end = false;
  boolean win = false;
  // text
  GFFont font = new GFFont("gafr/fonts/spleen/spleen-32x64.ffont.json");
  // load the image - conveyor belt
  GFStamp conveyorBelt = new GFStamp("assets/Conveyor belt.png").rescale(1.04, 1);
  // load the image - 5 types of dishes
  GFStamp blackBowl = new GFStamp("assets/black bowl.png").rescale(0.2, 0.2).centerPin();
  GFStamp blackPlate = new GFStamp("assets/black plate-pixel.png").rescale(0.15, 0.15).centerPin();
  GFStamp whiteCup = new GFStamp("assets/white cup- pixel.png").rescale(0.15, 0.15).centerPin();
  GFStamp whiteRoundPlate = new GFStamp("assets/white round plate.png").rescale(0.2, 0.2).centerPin();
  GFStamp whiteSquarePlate = new GFStamp("assets/square plate.png").rescale(0.2, 0.2).centerPin();
  // load image - 5 containers
  GFStamp blackBowlContainer = new GFStamp("assets/Black bowl container.png").rescale(0.25, 0.25).centerPin();
  GFStamp blackPlateContainer = new GFStamp("assets/Black plate container.png").rescale(0.25, 0.25).centerPin();
  GFStamp whiteCupContainer = new GFStamp("assets/White cup container.png").rescale(0.25, 0.25).centerPin();
  GFStamp whiteRoundPlateContainer = new GFStamp("assets/White round plate container.png").rescale(0.25, 0.25).centerPin();
  GFStamp whiteSquarePlateContainer = new GFStamp("assets/White square plate container.png").rescale(0.25, 0.25).centerPin();

  @Override
  /* initialize the game */
  public void onStartup() {
    HEIGHT = 500;
    WIDTH = 800;
    // randomize and initialize new dishes of different types, and add to the
    // arraylist
    for (int j = 0; j < numDish; j++) {
      int typeDish = GFM.randint(1, 5);
      dishes.add(new Dish(typeDish, j * timeInterval, posX, getDish(j+1)));
    }
  }

  @Override
  /*
   * search and select the object that mouse presses on
   * x & y = position that mouse is pressed
   * 
   */
  public void onMouseDown(int x, int y, int buttons, int flags, int button) {

    if (!isDragging) {
      System.out.println("is dragging");
      // iterate through the dishes arraylist and check if any dish object is collided
      for (int i=0; i<dishes.size(); i++) {
        Dish dish = dishes.get(i);
        System.out.println("Dish type = " + dish.type);
        // if the dish collides with the mouse click
        if (checkCollision(dish, x, y)) {
          selectedDish = dish;
          selectedDishID = i;
          isDragging = true;
          // GFMouse.setCursor(GFMouse.CURSOR_GRABBING);
          break;
        }

      }
    }
  }


  /**
   * Check if the mouse coordinates coincide with that of the dish
   * 
   * @param dish
   * @param x
   * @param y
   * @return true if there is a collision
   */
  public boolean checkCollision(Dish dish, int x, int y) {
    // System.out.println("checkCollision Triggered");
    // System.out.println("Position clicked:" + x + "," + y);
    // System.out.println("Position of dish:" + dish.posX + "," + dish.posY);
    return (((x > dish.posX - 100) && (x < dish.posX + 100)) && ((y > dish.posY - 100) && (y < dish.posY + 100)));

  }

  @Override
  /*
   * search and select the object that mouse presses on
   * update x and y position
   */
  public void onMouseMove(int x, int y, int buttons, int flags) {
    //System.out.println("onMouseMoveTriggered");
    if (selectedDish != null && isDragging) {   
      selectedDish.posX = y;
      selectedDish.posY = x;
    }
  }

  @Override
  /*
   * search and select the object that mouse presses on
   * x & y = position that mouse is pressed
   * 
   */
  public void onMouseUp(int x, int y, int buttons, int flags, int button){

    if (isDragging && selectedDish != null){
      checkContainer();
      Dish dishToRemove = selectedDish;
      selectedDish = null;
      dishes.remove(dishToRemove);
      isDragging = false;

      /*  Check end condition
      if (dishes.isEmpty()) {
        System.out.println("Empty list");
      end = true;
      win = (score >= 7);
      }*/
    }
  }

  /**
   * Check if click is inside a container
   */
  public void checkContainer() {
    System.out.println("checkContainer Triggered");
    // loop through the container cordinates to find which container is clicked upon
    for (int i = 0; i < containerCords.length; i++) {
      // if inside a container, check if it is the right container
      if (((selectedDish.posY > containerCords[i][0] - 60) && (selectedDish.posY < containerCords[i][0] + 60))
          && ((selectedDish.posX > containerCords[i][1] - 60) && (selectedDish.posX < containerCords[i][1] + 60))) {
        isCorrectContainer(i + 1);
      }
    }
    //delete the selected dish afterward
    dishes.remove(selectedDish);
  }

  /**
   * Check if the container matches the dish type
   * 
   * @param number of container
   */
  public void isCorrectContainer(int type) {
    System.out.println("isCorrectContainer Triggered");
    if (selectedDish.type == type){
      score ++;
      System.out.println("correct");
    }
    // compare type of selected dish and container
    else {
      // decrease one life if the container is incorrect
      updateStatus();
    }
  }

  /**
   * Update the number of lives we have left
   */
  public void updateStatus() {
    numErrors--;
    // game terminates
    if (numErrors <= 0) {
      numErrors = 0;
      end = true;
      win = false;
    }
    // output warning message
    else {
      font.draw(0, 0, "You can make at most " + numErrors + " errors now.");
    }
  }

  /**
   * remove offscreen dishes
   */
  public void removeOffScreenDishes() {
    Iterator<Dish> it = dishes.iterator();
    while (it.hasNext()) {
        Dish d = it.next();
        if (d.isOffScreen()) {
            it.remove();
        }
    }
    if (dishes.isEmpty()) {
      System.out.println("Empty list - game ends!");
      end = true;
      win = (score >= 7);
  }

}
  /*
   * helper method
   * input: type
   * 
   */
  public GFStamp getDish(int type) {
    switch (type) {
      case 1:
        return blackBowl;
      // break;
      case 2:
        return blackPlate;
      // break;
      case 3:
        return whiteCup;
      // break;
      case 4:
        return whiteRoundPlate;
      // break;
      case 5:
        return whiteSquarePlate;
      // break;
      default:
        return blackBowl;
    }
  }


  @Override
  /* draw the frame */
  public void onDraw(int frameCount) {
    if (end) {
      if (win) {
          font.draw(250, 200, "NICE SHIFT!");
      } else {
          font.draw(250, 200, "YOU ARE FIRE!");
      }
      return; // stop drawing anything else
    }
    /*if (numErrors <= 0) {
      GFStamp loseMessage = new GFStamp("assets/Fire.png");
      loseMessage.moveTo(0, 0).stamp();
    }*/
    // move conveyor belt
    conveyorBelt.moveTo(0, 80).stamp();
    // move containers
    blackBowlContainer.moveTo(containerCords[0][0], containerCords[0][1]).stamp();
    blackPlateContainer.moveTo(containerCords[1][0], containerCords[1][1]).stamp();
    whiteCupContainer.moveTo(containerCords[2][0], containerCords[2][1]).stamp();
    whiteRoundPlateContainer.moveTo(containerCords[3][0], containerCords[3][1]).stamp();
    whiteSquarePlateContainer.moveTo(containerCords[4][0], containerCords[4][1]).stamp();

    // Remove dishes that went off-screen
    removeOffScreenDishes();

    for (Dish dish : dishes) {
      if (dish != selectedDish) { 
          dish.update(frameCount);
      }
    }

    // update dish status for every count of timer
    for (Dish dish : dishes) {
      if (frameCount >= dish.appearTime) {
        getDish(dish.type).moveTo(dish.posY, dish.posX).stamp();
      }
    }
    font.draw(30,0, "Place 7 dishes correctly" );
    font.draw(30, 430, score + "/" + numDish);
  }

  public int[][] getContainerCords() {
    return containerCords;
  }
}
