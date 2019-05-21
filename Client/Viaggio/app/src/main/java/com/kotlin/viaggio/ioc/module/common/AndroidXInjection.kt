package com.kotlin.viaggio.ioc.module.common

import android.util.Log
import androidx.fragment.app.Fragment
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber

class AndroidXInjection{
    companion object {
        val TAG = AndroidXInjection::class.java.simpleName
        /**
         * Injects `fragment` if an associated [dagger.android.AndroidInjector] implementation
         * can be found, otherwise throws an [IllegalArgumentException].
         *
         *
         * Uses the following algorithm to find the appropriate `AndroidInjector<Fragment>` to
         * use to inject `fragment`:
         *
         *
         *  1. Walks the parent-fragment hierarchy to find the a fragment that implements [       ], and if none do
         *  1. Uses the `fragment`'s [activity][Fragment.getActivity] if it implements
         * [HasSupportFragmentInjector], and if not
         *  1. Uses the [android.app.Application] if it implements [       ].
         *
         *
         * If none of them implement [HasSupportFragmentInjector], a [ ] is thrown.
         *
         * @throws IllegalArgumentException if no parent fragment, activity, or application implements
         * [HasSupportFragmentInjector].
         */
        fun inject(fragment: Fragment) {
            checkNotNull(fragment) {
                "fragment"
            }
            val hasAndroidXFragmentInjector =
                findHasFragmentInjector(fragment)

            Timber.tag(TAG).d(
                String.format(
                    "An injector for %s was found in %s",
                    fragment.javaClass.canonicalName,
                    hasAndroidXFragmentInjector.javaClass.canonicalName
                )
            )

            val fragmentInjector = hasAndroidXFragmentInjector.androidXFragmentInjector()
            checkNotNull(fragmentInjector) {
                String.format(
                    "%s.supportFragmentInjector() returned null"
                    , hasAndroidXFragmentInjector::class.java.name)
            }
            fragmentInjector.inject(fragment)
        }

        private fun findHasFragmentInjector(fragment: Fragment): HasAndroidXFragmentInjector {
            var parentFragment = fragment.parentFragment
            while (parentFragment != null) {
                if (parentFragment is HasAndroidXFragmentInjector) {
                    return parentFragment
                }
                parentFragment = fragment.parentFragment
            }
            val activity = fragment.activity
            if (activity is HasAndroidXFragmentInjector) {
                return activity
            }
            throw IllegalArgumentException(
                String.format("No injector was found for %s", fragment::class.java.getCanonicalName())
            )
        }
    }
}