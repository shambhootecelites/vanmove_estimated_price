package com.vanmove.passesger.enum

enum class JobStatus (val value:String, val status:String){

    RECEIVED("0","RECEIVED"),
    ASSIGNED("1","ASSIGNED"),
    ACCEPTED("2","ACCEPTED"),
    REJECTED("3","REJECTED"),
    EXPIRED("4","EXPIRED"),
    ARRIVED("5","ARRIVED"),
    ONBOARD("6","ON BOARD"),
    COMPLETED("7","COMPLETED"),
    NO_DRIVERS("8","NO DRIVERS"),
    STARTED("9","STARTED"),
    GO_TO_PICK_UP("10","GO TO PICK-UP"),
    CANCELLED("11","CANCELLED"),
    ADVANCE_PAID_OFFER("12","ADVANCE PAID OFFER"),
    UNDERINVESTIGATION("13","UNDER INVESTIGATION"),
    Go_to_dro_poff("14","Go to drop off"),
    Arrived_at_drop_off("15","Arrived at drop off")


}