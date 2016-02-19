package statemachine.simple;

public interface Transition<S> {
    S goFrom(S state);
}
