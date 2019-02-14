package com.kotlin.viaggio.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import android.widget.LinearLayout
import com.kotlin.viaggio.R

class PageIndicatorView : LinearLayout {

    private var mCurrPage: Int = 0
    private var mDimen: Int = 0
    private var mTotalPageCount: Int = 0
    private var mIndicatorImg: Drawable? = null
    private val viewSparseArray = SparseArray<View>()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.PageIndicatorView, defStyle, 0)
        val defaultDimen = resources.getDimensionPixelSize(R.dimen.indicator_space)
        mDimen = a.getDimensionPixelOffset(R.styleable.PageIndicatorView_spacing, defaultDimen)
        mIndicatorImg = a.getDrawable(R.styleable.PageIndicatorView_imgSrc)
        if (mIndicatorImg == null) mIndicatorImg = resources.getDrawable(R.drawable.slide_indicator, null)
        orientation = HORIZONTAL
        a.recycle()
    }

    fun setTotalPageNumber(totalPageCount: Int) {
        if (totalPageCount <= 1) {
            visibility = View.INVISIBLE
            return
        }
        visibility = View.VISIBLE
        removeAllViews()
        viewSparseArray.clear()
        var imageView: ImageView
        var lp: LinearLayout.LayoutParams
        val maxCount = if (totalPageCount > 5) 9 else totalPageCount
        var state: Drawable?
        mTotalPageCount = totalPageCount
        mCurrPage = 0
        for (i in 0 until maxCount) {
            imageView = ImageView(context)
            state = mIndicatorImg?.constantState?.newDrawable()
            state?.let {
                lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                imageView.layoutParams = lp
                imageView.setImageDrawable(it.mutate())
                lp.marginEnd = mDimen
                lp.marginStart = mDimen
                val anim: ViewPropertyAnimator
                if (maxCount == 9) {
                    when {
                        i == 7 -> imageView.animate()
                            .scaleX(0.5f)
                            .scaleY(0.5f)
                            .setDuration(0)
                            .start()
                        i == 6 -> imageView.animate()
                            .scaleX(0.75f)
                            .scaleY(0.75f)
                            .setDuration(0)
                            .start()
                        i <= 2 || i > 7 -> {
                            anim = imageView.animate()
                                .alpha(0f)
                                .setDuration(0)
                            when (i) {
                                0, 8 -> anim.scaleX(0.25f)
                                    .scaleY(0.25f)
                                1 -> anim.scaleX(0.5f)
                                    .scaleY(0.5f)
                                else -> anim.scaleX(0.75f)
                                    .scaleY(0.75f)
                            }
                            anim.start()
                        }
                    }
                }
                viewSparseArray.put(i, imageView)
                addView(imageView)
            }
        }
        setCurrPageNumber(0)
    }

    fun setCurrPageNumber(currPageNumber: Int) {
        if (mTotalPageCount <= 1) return

        if (currPageNumber >= mCurrPage) {
            if (mTotalPageCount <= 5 || currPageNumber < 3) {
                selectCurrPageNumber(currPageNumber, isPositive = true, fromAnim = false)
            }  else {
                var currentView: ImageView
                var previousView: ImageView? = null
                var moveTo: Float
                var animator: ViewPropertyAnimator
                val moveToLast = viewSparseArray.get(8).x
                for (i in 8 downTo 0) {
                    if (i != 0) {
                        previousView = viewSparseArray.get(i - 1) as ImageView
                        moveTo = previousView.x
                        currentView = viewSparseArray.get(i) as ImageView
                        animator = currentView.animate()
                            .setDuration(250)
                            .x(moveTo)
                    } else {
                        currentView = viewSparseArray.get(i) as ImageView
                        animator = currentView.animate()
                            .setDuration(0)
                            .x(moveToLast)
                    }
                    animator.setListener(null)

                    if (i == 1) {
                        animator.setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                selectCurrPageNumber(currPageNumber, true, true)
                            }
                        })
                    }
                    when (i) {
                        1 -> animator
                            .scaleX(0.25f)
                            .scaleY(0.25f)
                            .alpha(0f)
                        8, 2 -> {
                            animator
                                .scaleX(0.5f)
                                .scaleY(0.5f)
                            if (currPageNumber + 2 < mTotalPageCount) {
                                if (i == 8 && previousView!!.alpha == 1f) {
                                    animator.alpha(1f)
                                }
                            }
                        }
                        7, 3 -> {
                            animator
                                .scaleX(0.75f)
                                .scaleY(0.75f)
                            if (currPageNumber + 1 < mTotalPageCount) {
                                if (i == 7) {
                                    if (currentView.alpha != 1f) {
                                        animator.alpha(1f)
                                    }
                                }
                            }

                        }
                        6 -> animator
                            .scaleX(1f)
                            .scaleY(1f)
                    }
                    animator.start()
                }
                val loopCnt = 9
                for (i in 0..loopCnt) {
                    if (i != loopCnt) {
                        viewSparseArray.put(i - 1, viewSparseArray.get(i))
                    } else {
                        viewSparseArray.put(i - 1, viewSparseArray.get(-1))
                        viewSparseArray.remove(-1)
                    }
                }
            }
        } else {
            if (currPageNumber >= mTotalPageCount - 3 || !viewSparseArray.get(3).isSelected) {
                selectCurrPageNumber(currPageNumber, isPositive = false, fromAnim = false)
            } else {
                var currentView: ImageView
                var previousView: ImageView? = null
                var moveTo: Float
                var animator: ViewPropertyAnimator
                val moveToLast = viewSparseArray.get(0).x
                for (i in 0..8) {
                    if (i != 8) {
                        previousView = if(viewSparseArray.get(i + 1) != null ) viewSparseArray.get(i + 1) as ImageView else ImageView(context)
                        moveTo = previousView.x
                        currentView = viewSparseArray.get(i) as ImageView
                        animator = currentView.animate()
                            .setDuration(250)
                            .x(moveTo)
                    } else {
                        currentView = viewSparseArray.get(i) as ImageView
                        animator = currentView.animate()
                            .setDuration(0)
                            .x(moveToLast)
                    }

                    animator.setListener(null)

                    if (i == 7) {
                        animator.setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                selectCurrPageNumber(currPageNumber, false, true)
                            }
                        })
                    }

                    when (i) {
                        7 -> animator
                            .scaleX(0.25f)
                            .scaleY(0.25f)
                            .alpha(0f)
                        0, 6 -> {
                            animator
                                .scaleX(0.5f)
                                .scaleY(0.5f)
                            if (currPageNumber >= 2) {
                                if (i == 0 && previousView!!.alpha == 1f) {
                                    animator.alpha(1f)
                                }
                            }
                        }
                        1, 5 -> {
                            animator
                                .scaleX(0.75f)
                                .scaleY(0.75f)
                            if (currPageNumber >= 1) {
                                if (i == 1 && currentView.alpha != 1f) {
                                    animator.alpha(1f)
                                }
                            }

                        }
                        2 -> animator
                            .scaleX(1f)
                            .scaleY(1f)
                    }
                    animator.start()
                }
                val loopCnt = -1
                for (i in 8 downTo loopCnt) {
                    if (i != loopCnt) {
                        viewSparseArray.put(i + 1, viewSparseArray.get(i))
                    } else {
                        viewSparseArray.put(i + 1, viewSparseArray.get(9))
                        viewSparseArray.remove(9)
                    }
                }
            }
        }
    }

    private fun selectCurrPageNumber(currPageNumber: Int, isPositive: Boolean, fromAnim: Boolean) {
        if (childCount == 9) {
            val calibration: Int
            var view: View?
            var prevSelected: Int? = null

            for (i in 0..8) {
                view = viewSparseArray.get(i)
                view?.let {
                    if (it.isSelected) {
                        prevSelected = i
                    }
                    it.isSelected = false
                }?: break
            }

            if (isPositive) {
                if (!fromAnim && prevSelected != null) {
                    viewSparseArray.get(prevSelected!! + 1).isSelected = true
                } else {
                    calibration = when {
                        viewSparseArray.get(2).alpha == 0f -> 3
                        viewSparseArray.get(1).alpha == 0f -> 2
                        else -> 1
                    }
                    if (calibration == 1) {
                        prevSelected?.let {
                            viewSparseArray
                                .get(it + (currPageNumber - mCurrPage)).isSelected = true
                        }?: viewSparseArray.get(5).setSelected(true)
                    } else {
                        val calibratedIndex = if (mTotalPageCount < 5) currPageNumber else currPageNumber + calibration
                        viewSparseArray.get(calibratedIndex).isSelected = true
                    }
                }
            } else {
                if (!fromAnim && prevSelected != null) {
                    viewSparseArray.get(prevSelected!! - 1).isSelected = true
                } else {
                    if (viewSparseArray.get(7).alpha == 0f && viewSparseArray.get(6).alpha == 0f) {
                        val index = 3
                        calibration = 2 - (mTotalPageCount - 1 - currPageNumber)
                        val calibratedIndex = if (mTotalPageCount < 5)
                            currPageNumber
                        else
                            index + calibration
                        viewSparseArray.get(calibratedIndex).isSelected = true
                    } else {
                        prevSelected?.let {
                            viewSparseArray
                                .get(it + (currPageNumber - mCurrPage)).isSelected = true
                        }?: viewSparseArray.get(3).setSelected(true)
                    }
                }
            }
        } else {
            getChildAt(mCurrPage).isSelected = false
            getChildAt(currPageNumber).isSelected = true
        }

        mCurrPage = currPageNumber
    }
}