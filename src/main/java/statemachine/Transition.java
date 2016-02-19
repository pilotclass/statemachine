package statemachine;

public interface Transition<S> {
    S goFrom(S state);
}
