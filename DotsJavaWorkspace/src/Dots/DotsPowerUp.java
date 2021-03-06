package Dots;

/**
 * Created by JiaHao on 22/4/15.
 */
public class DotsPowerUp {

    private final DotsPowerUpType powerUpType;
    private final DotsPowerUpState powerUpState;

    public DotsPowerUp(DotsPowerUpType powerUpType, DotsPowerUpState powerUpState) {
        this.powerUpType = powerUpType;
        this.powerUpState = powerUpState;
    }

    @Override
    public String toString() {
        return powerUpType + " " + powerUpState.toString() ;

    }

    public DotsPowerUpType getPowerUpType() {
        return powerUpType;
    }

    public DotsPowerUpState getPowerUpState() {
        return powerUpState;
    }
}



