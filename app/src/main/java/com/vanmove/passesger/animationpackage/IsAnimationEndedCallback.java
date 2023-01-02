package com.vanmove.passesger.animationpackage;


public interface IsAnimationEndedCallback
{
    public void getAnimationStatus(String status);

    public static String rotation="rotation";
    public static String rotationX="rotationX";
    public static String rotationY="rotationY";
    public static String translationX="translationX";
    public static String translationY="translationY";
    public static String scaleX="scaleX";
    public static String scaleY="scaleY";
    public static String pivotX="pivotX";
    public static String alpha="alpha";
    public static String x="x";
    public static String y="y";

    //Animation Status========================================

    public static String animationStart="start";
    public static String animationEnd="end";
    public static String animationRepeat="repeat";
    public static String animationCancel="cancel";









}
