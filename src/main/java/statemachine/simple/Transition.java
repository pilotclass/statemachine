package statemachine.simple;

public interface Transition<S> {
    S from(S state);
}
