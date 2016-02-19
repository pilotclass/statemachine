package statemachine.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class StateContainer<S, T extends statemachine.simple.Transition<S>> {
    private final AtomicReference<S> state;
    private final Map<T, Runnable> transitionActions;

    private StateContainer(final S initial, final Map<T, Runnable> transitionActions) {
        state = new AtomicReference<>(initial);
        this.transitionActions = transitionActions;
    }

    @SuppressWarnings("UnusedParameters")
    public static <S, T extends statemachine.simple.Transition<S>> StartingBuilder<S, T> over(final Class<S> states, final Class<T> transitions) {
        return new StartingBuilder<>();
    }

    public boolean apply(final T transition) {
        while (true) {
            final S currentState = state.get();
            final S nextState = transition.goFrom(currentState);
            if (nextState.equals(currentState)) {
                return false;
            } else {
                if (state.compareAndSet(currentState, nextState)) {
                    Optional.ofNullable(transitionActions.get(transition)).ifPresent(Runnable::run);
                    return true;
                }
            }
        }
    }

    public  static class StartingBuilder<S, T extends statemachine.simple.Transition<S>> {
        public ContainerBuilder<S, T> startingAt(S initialState) {
            return new ContainerBuilder<>(initialState);
        }
    }

    public static class ContainerBuilder<S, T extends statemachine.simple.Transition<S>> {
        private final Map<T, Runnable> onTransition = new HashMap<>();
        private final S initialState;

        private ContainerBuilder(final S initialState) {
            this.initialState = Objects.requireNonNull(initialState, "Initial state must be provided");
        }

        public ActionBuilder when(T t) {
            return new ActionBuilder(t);
        }

        public StateContainer<S, T> build() {
            return new StateContainer<>(initialState, onTransition);
        }

        public class ActionBuilder {
            private final T t;
            public ActionBuilder(final T t) {
                this.t = t;
            }

            public ContainerBuilder<S, T> then(final Runnable action) {
                onTransition.put(t, action);
                return ContainerBuilder.this;
            }

        }
    }
}
