package net.example.demo;

public class SideEffect {
    Runnable sideEffect;

    public SideEffect(Runnable sideEffect) {
        this.sideEffect = sideEffect;
    }

    public void triggerEffect() {
        this.sideEffect.run();
    }

    public SideEffect derivate(Runnable newEffect) {
        return new SideEffect(() -> {
            this.sideEffect.run();
            newEffect.run();
        });
    }
}
