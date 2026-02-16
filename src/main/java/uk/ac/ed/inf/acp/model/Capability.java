package uk.ac.ed.inf.acp.model;

public class Capability {
    Boolean cooling;
    Boolean heating;
    Integer capacity;
    Integer maxMoves;
    Integer costPerMove;
    Integer costInitial;
    Integer costFinal;

    Capability(){

    }
    Capability (Boolean cooling, Boolean heating, Integer capacity, Integer maxMoves, Integer costPerMove, Integer costInitial, Integer costFinal) {
        this.cooling = cooling;
        this.heating = heating;
        this.capacity = capacity != null ? capacity : 0;
        this.maxMoves = maxMoves  != null ? maxMoves : 0;
        this.costPerMove = costPerMove  != null ? costPerMove : 0;
        this.costInitial = costInitial  != null ? costInitial : 0;
        this.costFinal = costFinal  != null ? costFinal : 0;
    }

    public Boolean getCooling() {
        return cooling;
    }
    public void setCooling(Boolean cooling) {
        this.cooling = cooling;
    }

    public Boolean getHeating() {
        return heating;
    }
    public void setHeating(Boolean heating) {
        this.heating = heating;
    }
    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    public Integer getMaxMoves() {
        return maxMoves;
    }
    public void setMaxMoves(Integer maxMoves) {
        this.maxMoves = maxMoves;
    }
    public Integer getCostPerMove() {
        return costPerMove;
    }
    public void setCostPerMove(Integer costPerMove) {
        this.costPerMove = costPerMove;
    }
    public Integer getCostInitial() {
        return costInitial;
    }
    public void setCostInitial(Integer costInitial) {
        this.costInitial = costInitial;
    }
    public Integer getCostFinal() {
        return costFinal;
    }
    public void setCostFinal(Integer costFinal) {
        this.costFinal = costFinal;
    }
}
