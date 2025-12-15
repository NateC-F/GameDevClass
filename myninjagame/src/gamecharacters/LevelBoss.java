package gamecharacters;



import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.Direction;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameframework.sound.GameAudio;
import gameobjects.NinjaGameObjectType;
import gameobjects.BossLance;
import gameobjects.Key;
import java.awt.Point;

import gameobjects.NinjaPlayerKunai;

import java.awt.image.BufferedImage;

// work on take damage method
// talk to the player team
// we are using hitboxes
//
 // what is the boss going to interact with the player and the ground.

public class LevelBoss extends GameCharacter
{
   // private static final Object DEFAULT_SCORE = 1000;
    private int attackCooldown = 0; // frames until next attack allowed
    private int attackPhase = 1;// 1 = normal, 2 = enraged, etc.
    private int attackPower=25;

    private int frameCounter = 0;
    private boolean isDead;
    private boolean deathSoundPlayed = false;

    //private final int DEAFULT_SCORE = 100;

    // === Boss-specific animations ===
    private Animation heavyAttackLeft, heavyAttackRight;
    private Animation specialAttackLeft, specialAttackRight;
    private Animation idleDying;
    private Animation attackRangedLeft, getAttackRangedRight;



    public LevelBoss(String name,
                     int x, int y, int z,
                     int scaleWidth, int scaleHeight)
    {
        super(name, NinjaGameObjectType.LEVEL_BOSS,
                x, y, scaleWidth, scaleHeight);
        System.out.println("LevelBoss spawned at " + x + ", " + y);
        setKnockbackImpulse(6);

    }

    @Override
    public void initializeStatus()
    {
        // The boss should be stronger and slower than regular enemies
        speed = 5;                 // slower but powerful
        setTotalHealth(10);      // high HP for a boss
        setCurHealth(10);
        //gravity = 0;

        curAnimation = idle;       // start idle
    }

    @Override
    public void initializeAnimations()
    {

// Idle animation (now using spritesheet)
// Suppose your sheet has 4 frames in 1 row — adjust rows/cols/frames as needed
        Spritesheet idleSheet = new Spritesheet("boss_idle/bossIdleSheet.png", 1, 2, 2, false);
        idle = new Animation(idleSheet, scaleWidth, scaleHeight);
        idle.setSpeed(5);

        Spritesheet idleDyingSheet = new Spritesheet("boss_idle/boss_dying_idle_sheet.png", 1, 2, 2, true);
        idleDying = new Animation(idleDyingSheet, scaleWidth, scaleHeight);
        idleDying.setSpeed(5);
        idleDying.runOnlyOnce();
        idleDying.linkSoundEffect("BossDeath.wav", true, false);


// Walk right animation (temporarily we can reuse attack sprite if needed)
// walking right is 3
        Spritesheet walkRightSheet = new Spritesheet("boss_walking/boss_walking_right_sheet.png", 4, 1, 4, false);
        walkRight = new Animation(walkRightSheet, scaleWidth, scaleHeight);
        walkRight.setSpeed(5);
        runRight = walkRight;
// Walk left animation
// walking left is 2
        Spritesheet walkLeftSheet = new Spritesheet("boss_walking/boss_walking_left_sheet.png", 4, 1, 4, false);
        walkLeft = new Animation(walkLeftSheet, scaleWidth, scaleHeight);
        walkLeft.setSpeed(5);
        runLeft = walkLeft;
        runLeft.setSpeed(6);

// Attack animations
        // ====== BASIC ATTACKS ======
        Spritesheet attackRightSheet = new Spritesheet(
                "boss_attack/boss_atack_right_sheet.png", 1, 3, 3);
        attackRight = new Animation(attackRightSheet, (int)(scaleWidth * 1.2), scaleHeight);
        attackRight.setSpeed(4);

        Spritesheet attackLeftSheet = new Spritesheet(
                "boss_attack/boss_attack_left_sheet.png", 1, 3, 3, false);
        attackLeft = new Animation(attackLeftSheet, (int)(scaleWidth * 1.2), scaleHeight);
        attackLeft.setSpeed(4);

// ====== HEAVY ATTACK (Phase 2) ======
        Spritesheet heavyRightSheet = new Spritesheet("boss_attack/bossHeavyAttackSheetRight.png", 3, 1, 3);
        heavyAttackRight = new Animation(heavyRightSheet, (int)(scaleWidth * 1.3), scaleHeight);
        heavyAttackRight.setSpeed(3);

        Spritesheet heavyLeftSheet = new Spritesheet("boss_attack/bossHeavyAttackSheetLeft.png", 3, 1, 3, false);
        heavyAttackLeft = new Animation(heavyLeftSheet, (int)(scaleWidth * 1.3), scaleHeight);
        heavyAttackLeft.setSpeed(3);

/*
        The movement & behavior cycle

The boss uses a counter called frameCounter to decide what to do over time.
As the game updates every frame (usually 60 times per second), the counter goes up each frame.
Here’s the pattern:
Frames 0–39: Move right (velX = speed) and play the walkRight animation.
Frames 40–79: Move left (velX = -speed) and play the walkLeft animation.
Frames 80–99: Stop moving (velX = 0) and play the idle animation.
Frame 100: Still idle, but you could trigger an attack here (it’s commented out for now).
Reset the cycle
Each frame, the counter increases by 1: frameCounter++.
When it hits 120, it resets to 0 — meaning the boss repeats this same 120-frame pattern
(about every 2 seconds if running at 60 FPS).
Attack cooldown (commented out)

The code for managing the delay between attacks (attackCooldown) is present but commented out — so it’s not doing anything right now.
*/
        // ====== LANCE ATTACKS ======
     Spritesheet lanceAttackRightSheet = new Spritesheet(
              "boss_attack/boss_lance_atack_right_sheet.png", 1, 3, 3);
        attackRangedRight = new Animation(lanceAttackRightSheet, (int)(scaleWidth * 1.2), scaleHeight);
       attackRangedRight.setSpeed(4);

        Spritesheet lanceAttackLeftSheet = new Spritesheet(
                "boss_attack/boss_lance_attack_left_sheet.png", 1, 3, 3,false);
        attackRangedLeft = new Animation(lanceAttackLeftSheet, (int)(scaleWidth * 1.2), scaleHeight);
       attackRangedLeft.setSpeed(4);
    }



    public void update(GameObjects objects) {

        if (isDead)
        {
            frameCounter++;
            curAnimation = idleDying;
            //NinjaPlayer.addScore(1000);
            return;
        }
            super.update(objects);

            // Movement & behavior cycle
            if (frameCounter < 20) {
                setVelX(speed);
                curAnimation = walkRight;
                direction = Direction.RIGHT;
            } else if (frameCounter < 40) {
                setVelX(-speed);
                curAnimation = walkLeft;
                direction = Direction.LEFT;
            } else if (frameCounter < 60) {
                setVelX(0);
                curAnimation = idle;
            } else if (frameCounter == 80) {
                setVelX(0);
                curAnimation = idle;
                performAttack();
            }

            // Increment and reset the cycle
            frameCounter++;
            if (frameCounter == 120) frameCounter = 0;

            // Reduce cooldown between attacks
            if (attackCooldown > 0) attackCooldown--;
        }



// this would be called when it gets hit by a player
    public void takeDamage(int damage)
    {
        Player player = Player.getActivePlayer();
        setCurHealth(getCurHealth() - damage);

        // Optional: play a hurt animation
        System.out.println(getName() + " took " + damage + " damage! Health now: " + getCurHealth());

        if (getCurHealth() <= 0) {
            isDead = true;
            setVelX(0);                    // stop movement immediately

            // Play the death sound ONE TIME
            if (!deathSoundPlayed) {

                idleDying.linkSoundEffect("BossDeath.wav", true, false);
                //GameAudio.playSound("BossDeath.wav");
                deathSoundPlayed = true;
                player.setScore(player.getScore() + 1000);
            }
            curAnimation = idleDying;    // show death animation
            System.out.println(getName() + " has died!");

            updatePhase();   // <--- REQUIRED
            updateAttackPower();
          //  if (player instanceof NinjaPlayer)
              //  ((NinjaPlayer)player).addKey(new Key("boss_key",-1,-1,-1,0,0));
        }
    }



    // =============================
    // Attack logic by phase
    // =============================
    private void performAttack() {
        if (attackCooldown > 0) return;

        double healthPercent = (double) getCurHealth() / getTotalHealth();

        if (healthPercent > 0.7) {
            attackPhase = 1;
        } else if (healthPercent > 0.4) {
            attackPhase = 2;
        } else {
            attackPhase = 3;
        }

        updateAttackPower();  // <<< IMPORTANT — keeps attack damage correct

        double rng = Math.random();

        switch (attackPhase) {
            case 1:
                if (rng < 0.9) basicAttack();
                break;

            case 2:
                if (rng < 0.5) heavyAttack();
                break;

            case 3 :
                if(rng < 0.3) lanceAttack();
                break;
        }
    }

    // add this to master
    private void basicAttack() {

        // Randomly choose attack direction (50/50)
        boolean attackLeftSide = Math.random() < 0.5;

        if (attackLeftSide) {
            curAnimation = attackLeft;
            System.out.println("Boss uses BASIC attack LEFT!");
        } else {
            curAnimation = attackRight;
            System.out.println("Boss uses BASIC attack RIGHT!");
        }

        // Optional: set direction for consistency (helps later logic)
        direction = attackLeftSide ? Direction.LEFT : Direction.RIGHT;

        System.out.println("Boss BASIC attack (" + attackPower + " dmg)");

        // Attack cooldown — adjust to control frequency
        attackCooldown = 40;
    }

    private void heavyAttack() {
        curAnimation = (direction == Direction.LEFT) ? heavyAttackLeft : heavyAttackRight;
        System.out.println("Boss HEAVY attack (" + attackPower + " dmg)");
        //System.out.println("Boss uses HEAVY attack!");
        attackCooldown = 80;
    }


    private void lanceAttack() {
        if (direction == Direction.LEFT && attackRangedLeft != null) {
            curAnimation = attackRangedLeft;
        }
        else if(attackRangedRight != null) {
            curAnimation = attackRangedRight;
        }
        else{
            curAnimation = (direction == Direction.LEFT) ? attackLeft : attackRight;
        }

        GameObject lance = spawnLance();

        if(lance != null) {
            GameThread.data.addObjectWhenSafe(lance);
        }

        attackCooldown = 60;
    }

    private GameObject spawnLance() {
        Player player = Player.getActivePlayer();
        if(player == null){
            return null;
        }

        int startX;
        if(direction == Direction.RIGHT){
            startX = getX() + getBounds().width;
        }
        else{
            startX = getX();
        }

        int startY = getY() + getBounds().height / 2;

        int targetX = player.getX() + player.getBounds().width / 2;
        int targetY = player.getX() + player.getBounds().width / 2;

        Point target = new Point(targetX, targetY);

        int ttlMs = 3500;

        int projWidth = 50;
        int projHeight = 50;

        return new BossLance("boss_lance", startX, startY, 5, projWidth, projHeight, target, ttlMs);
    }




// make a mthod that takes its attachphase and fix the attack power to that and phase one attack poer should be 25 and then going up by 25
public void updateAttackPower() {
    attackPower = attackPhase * 25;
    System.out.println("Boss attack power updated: " + attackPower
            + " (phase " + attackPhase + ")");
}

    private void updatePhase() {

        int max = getTotalHealth();
        int cur = getCurHealth();

        if (cur <= max * 0.33f) {
            attackPhase = 3;
        }
        else if (cur <= max * 0.66f) {
            attackPhase = 2;
        }
        else {
            attackPhase = 1;
        }

        updateAttackPower();
    }

    @Override
    public boolean handleObjectCollision(GameObject go)
    {
        boolean handled = true;

        switch (go.getType())
        {
            case NinjaGameObjectType.PLAYER :
            case NinjaGameObjectType.ELF_PLAYER:
                knockback(go.getPosition().x < getPosition().x);//go.getDirection().compareTo(this.getDirection()) > 0);
                // When boss collides with player, trigger an attack
                handleCollision(go);
                takeDamage(5);// Resolve overlap (prevents clipping)
                performAttack();      // Decide what kind of attack to do
                break;
// have it takeDamage with the star

            case NinjaGameObjectType.NINJA_PLAYER_KUNAI:
                // If the boss is hit by a player attack, take damage
                handleCollision(go);
                System.out.println("Boss hit by kunai!");
                takeDamage(25);       // You can adjust the damage value or read it from the attack
                performAttack();      // Recalculate attack phase after taking damage
                break;

            default:
                // Let the superclass handle all other generic collisions
                handled = super.handleObjectCollision(go);
                break;
        }

        return handled;
    }

// use the direction to face the correct way

}