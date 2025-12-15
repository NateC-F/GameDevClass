package gameframework.gameai.navigation.flying;

import gameframework.gamecharacters.Player;

/*
 * NOTE:
 * This interface currently serves a minimal role because only one
 * type of flying chase behavior needs to be implemented (FireSpirit).
 * However, it establishes a clear structure so additional flying
 * chase styles can be added later without modifying the AI system.
 */
public interface FlyingChaseBehavior {

    void chase(Player target);

}