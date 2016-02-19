package statemachine.builder;

public interface Transition<S> {
    S goFrom(S state);
}
