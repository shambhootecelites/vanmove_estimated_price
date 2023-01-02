package com.vanmove.passesger.animationpackage;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AnimationForViews
{

    public void handleAnimation(AppCompatActivity appCompatActivity,
                                View animatedView,
                                int duration,
                                int animatefrom,
                                int animateto,
                                String animationType,
                                final IsAnimationEndedCallback isAnimationEndedCallback)
    {

        ObjectAnimator anim = ObjectAnimator.ofFloat(animatedView, animationType, animatefrom, animateto);
        anim.setDuration(duration);//set duration
        anim.start();//start animation
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation)
            {
                isAnimationEndedCallback.getAnimationStatus(IsAnimationEndedCallback.animationStart);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationEndedCallback.getAnimationStatus(IsAnimationEndedCallback.animationEnd);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimationEndedCallback.getAnimationStatus(IsAnimationEndedCallback.animationCancel);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAnimationEndedCallback.getAnimationStatus(IsAnimationEndedCallback.animationRepeat);
            }
        });
    }
}
