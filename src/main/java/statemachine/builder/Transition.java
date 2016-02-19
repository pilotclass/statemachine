package statemachine.builder;

public interface Transition<S> {
    S whenFrom(S state);
}
