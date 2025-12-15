package gameframework.weapons;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjectType;
import gameframework.gameobjects.GameObjects;
import gameframework.gameobjects.InanimateObject;
import gameframework.inputhandlers.MouseHandler;

import java.awt.*;

//copied this code from thrown weapon class in ninjaGame
public abstract class Projectile extends InanimateObject {


    private Weapon origin;
    private GameObject owner;
    private HitBox hitbox;
    private double speed;
    private Point targetPositon;
    private Point startPosition;
    private double vx;
    private double vy;

    private double subX;
    private double subY;

    private Animation disappear;
    private Animation spin1;
    private Animation spin2;

    //variables used for a projectile's expected time to live
    private long ttlStartTime = 0;
    private int ttlDelay;
    private int TTL = 1400;

    private Animation left;
    private Animation right;

    public Projectile(String name, int x, int y, int scaleWidth, int scaleHeight, Weapon origin, GameObject owner, int speed, Point startPosition) {
        super(name, x, y, 1, scaleWidth, scaleHeight);

        this.setOrigin(origin);
        this.setSpeed(speed);
        this.setSubX(x);
        this.setSubY(y);


        this.setStartPosition(startPosition);
        this.setTargetPositon(new Point(MouseHandler.targetX, MouseHandler.targetY));

        setGravity(0);
        requiresUpdating = true;
        this.ttlStartTime = System.currentTimeMillis();
        this.ttlDelay = TTL;

        //this.setThrownByPlayer(thrownByPlayer);
        this.setOwner(owner);

        hitbox = new HitBox(x, y, scaleWidth, scaleHeight);

        initVelocityVectors();
        initAnimations();

        System.out.println("Projectile spawned at: " + x + "," + y);
        System.out.println("Player at: " + owner.getX() + "," + owner.getY());
        System.out.println("Weapon hitbox: " + hitbox.getBounds().getX() + "," +
                hitbox.getBounds().getY());
    }


    /*@Override
    public void render(Graphics g) {
        super.render(g);
        if (hitbox != null) {
            Rectangle bounds = hitbox.getBounds();
            Color oldColor = g.getColor();
            g.setColor(Color.RED);
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g.setColor(oldColor);
        }

    }*/

    @Override
    public void update(GameObjects objects) {
        super.update(objects);
        long currentTime = System.currentTimeMillis();
        setSubX(getSubX() + getVx());
        setSubY(getSubY() + getVy());

        setPosition((int) Math.round(getSubX()), (int) Math.round(getSubY()));
        updateHitBox();
        //System.out.println("PROJECTILE HITBOX UPDATED");

        for (GameObject obj : objects) {
            //dont collide with itself
            if (obj == this) continue;
            //dont collide with owner
            if (obj == getOwner()) continue;
            //dont collide with weapon
            if (obj instanceof Weapon && ((Weapon)obj).getWeaponHolder() == getOwner()) {
                continue;
            }


            //if hitbox intercepts bounds of any other object
            if (hitbox.intersects(obj)) {
                System.out.println("Projectile hit: " + obj.getName());
                System.out.println("projectile x: " + obj.getX() + ", y: " + obj.getY());

                if(obj instanceof GameCharacter target){
                    target.takeDamage(getOrigin().getDamage());
                }

                obj.handleObjectCollision(this);
                GameThread.data.removeObjectWhenSafe(this);
                break;
            }
        }

        // Check if the TTL has run out
        if (currentTime > ttlStartTime + ttlDelay) {
            GameThread.data.removeObjectWhenSafe(this);
        }

    }

    public void updateHitBox() {
        setHitbox(getX(), getY(), scaleWidth, scaleHeight);
    }

    @Override
    public boolean handleCollision(GameObject collidingObject) {

        return true;
    }

    private void initVelocityVectors() {
        //Point target = new Point(MouseHandler.targetX, MouseHandler.targetY);

        double dx = targetPositon.x - startPosition.x;
        double dy = targetPositon.y - startPosition.y;

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) len = 1;

        double nx = dx / len;
        double ny = dy / len;

        setVx(nx * getSpeed());
        setVy(ny * getSpeed());
    }

    public abstract void initAnimations();


    @Override
    public boolean shouldIgnoreCollisionWith(GameObject other){
        boolean shouldIgnore = false;

        switch(other.getType()){

            case GameObjectType.PLAYER:
                shouldIgnore = true;
                break;

            case GameObjectType.NPC:
                shouldIgnore = true;
                break;

        }
        return shouldIgnore;
    }









    public HitBox getHitbox() {
        return hitbox;
    }

    public void setHitbox(int x, int y, int scaleWidth, int scaleHeight) {
        this.hitbox = new HitBox(x, y, scaleWidth, scaleHeight);
    }

    @Override
    public boolean isUnmovable() {
        return false;
    }

    @Override
    public boolean handleObjectCollision(GameObject object) {
        return false;
    }

    public GameObject getOwner() {
        return owner;
    }

    public void setOwner(GameObject owner) {
        this.owner = owner;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Point getTargetPositon() {
        return targetPositon;
    }

    public void setTargetPositon(Point targetPositon) {
        this.targetPositon = targetPositon;
    }

    public Point getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Point startPosition) {
        this.startPosition = startPosition;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getSubX() {
        return subX;
    }

    public void setSubX(double subX) {
        this.subX = subX;
    }

    public double getSubY() {
        return subY;
    }

    public void setSubY(double subY) {
        this.subY = subY;
    }

    public Animation getDisappear() {
        return disappear;
    }

    public void setDisappear(Animation disappear) {
        this.disappear = disappear;
    }

    public Animation getSpin1() {
        return spin1;
    }

    public void setSpin1(Animation spin1) {
        this.spin1 = spin1;
    }

    public Animation getSpin2() {
        return spin2;
    }

    public void setSpin2(Animation spin2) {
        this.spin2 = spin2;
    }

    public Weapon getOrigin() {
        return origin;
    }

    public void setOrigin(Weapon origin) {
        this.origin = origin;
    }
}
