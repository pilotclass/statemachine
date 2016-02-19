package statemachine.listener;

import statemachine.Transition;

import java.util.concurrent.atomic.AtomicReference;

public final class StateContainer<S, T extends Transition<S>> {
    private final AtomicReference<S> state;
    private final TransitionListener<S, T> transitionListener;

    public StateContainer(final S initial, final TransitionListener<S, T> transitionListener) {
        state = new AtomicReference<>(initial);
        this.transitionListener = transitionListener;
    }

    public boolean apply(final T transition) {
        while (true) {
            final S currentState = state.get();
            final S nextState = transition.goFrom(currentState);
            if (nextState.equals(currentState)) {
                return false;
            } else {
                if (state.compareAndSet(currentState, nextState)) {
                    transitionListener.onTransition(transition, nextState);
                    return true;
                }
            }
        }
    }
}
