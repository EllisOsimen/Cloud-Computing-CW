package uk.ac.ed.inf.acp.model;

public class Capability {
    Boolean cooling;
    Boolean heating;
    Integer capacity;
    Integer maxMoves;
    Integer costPerMove;
    Integer costInitial;
    Integer costFinal;

    Capability (Boolean cooling, Boolean heating, Integer capacity, Integer maxMoves, Integer costPerMove, Integer costInitial, Integer costFinal) {
        this.cooling = cooling;
        this.heating = heating;
        this.capacity = capacity;
        this.maxMoves = maxMoves;
        this.costPerMove = costPerMove;
        this.costInitial = costInitial;
        this.costFinal = costFinal;
    }
}
