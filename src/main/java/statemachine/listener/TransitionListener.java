package statemachine.listener;

import statemachine.Transition;

public interface TransitionListener<S, T extends Transition<S>> {
    void onTransition(T transition, final S nextState);
}
