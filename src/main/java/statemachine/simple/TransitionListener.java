package statemachine.simple;

public interface TransitionListener<S, T extends Transition<S>> {
    void onTransition(T transition, final S nextState);
}
