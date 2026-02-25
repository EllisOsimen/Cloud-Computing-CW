package uk.ac.ed.inf.acp.model;

public class Capability {
    Boolean cooling;
    Boolean heating;
    Double capacity = 0.0;
    Double maxMoves = 0.0;
    Double costPerMove = 0.0;
    Double costInitial = 0.0;
    Double costFinal = 0.0;

    Capability(){

    }
    Capability (Boolean cooling, Boolean heating, Double capacity, Double maxMoves, Double costPerMove, Double costInitial, Double costFinal) {
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
    public Double getCapacity() {
        return capacity;
    }
    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }
    public Double getMaxMoves() {
        return maxMoves;
    }
    public void setMaxMoves(Double maxMoves) {
        this.maxMoves = maxMoves;
    }
    public Double getCostPerMove() {
        return costPerMove;
    }
    public void setCostPerMove(Double costPerMove) {
        this.costPerMove = costPerMove;
    }
    public Double getCostInitial() {
        return costInitial;
    }
    public void setCostInitial(Double costInitial) {
        this.costInitial = costInitial;
    }
    public Double getCostFinal() {
        return costFinal;
    }
    public void setCostFinal(Double costFinal) {
        this.costFinal = costFinal;
    }
}
