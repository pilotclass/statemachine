package statemachine.simple;

import java.util.concurrent.atomic.AtomicReference;

public final class StateContainer<S, T extends Transition<S>> {
    private final AtomicReference<S> state;
    private final TransitionListener<S, T> listener;

    public StateContainer(final S initial, final TransitionListener<S, T> listener) {
        state = new AtomicReference<>(initial);
        this.listener = listener;
    }

    public boolean apply(final T transition) {
        while(true) {
            final S currentState = state.get();
            final S nextState = transition.from(currentState);
            if (nextState.equals(currentState)) {
                return false;
            } else {
                if (state.compareAndSet(currentState, nextState)) {
                    listener.onTransition(transition, nextState);
                    return true;
                }
            }
        }
    }
}
