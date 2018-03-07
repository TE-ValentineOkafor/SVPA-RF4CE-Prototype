package android.support.v17.leanback.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.PagingIndicator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sony.svpa.rf4ceprototype.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for OnboardingFragments in our class.
 * <p>
 * This code needs to be in the android.support.v17.leanback.app package
 * so that we can access package-protected methods and members from the base class
 * and expose them to derived classes.
 */
public abstract class AlexaOnboardingFragment<T extends OnboardingPage> extends OnboardingFragment {

  public static final String TAG = AlexaOnboardingFragment.class.getSimpleName();

  // Duration of content fade animation
  private static final int ANIMATION_DURATION = 500;

  private FrameLayout contentView;
  private PagingIndicator pagingIndicator;
  private final List<T> pages;

  protected AlexaOnboardingFragment(List<T> pages) {
    this.pages = pages;
  }

  @Override
  protected int getPageCount() {
    return pages.size();
  }

  @Override
  protected CharSequence getPageTitle(int pageIndex) {
    return getPageTitle(pages.get(pageIndex));
  }

  @Override
  protected CharSequence getPageDescription(int pageIndex) {
    return getPageDescription(pages.get(pageIndex));
  }

  protected abstract CharSequence getPageTitle(T page);

  protected abstract CharSequence getPageDescription(T page);


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = super.onCreateView(inflater, container, savedInstanceState);
    pagingIndicator = (PagingIndicator) v.findViewById(android.support.v17.leanback.R.id.page_indicator);
    return v;
  }

  @Nullable
  @Override
  protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
    return null;
  }

  @Nullable
  @Override
  protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
    // set up frame layout to hold the content
    contentView = new FrameLayout(getActivity());
    // perform setup for first page
    onPageChanged(pages.get(0), null);
    return contentView;
  }

  @Nullable
  @Override
  protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
    return null;
  }

  protected FrameLayout getContentView() {
    return contentView;
  }

  protected PagingIndicator getPagingIndicator() {
    return pagingIndicator;
  }

  @Override
  protected void onPageChanged(final int newPage, int previousPage) {
    T newPageItem = pages.get(newPage);
    T previousPageItem = pages.get(previousPage);
    onPageChanged(newPageItem, previousPageItem);
  }

  /**
   * For derived classes to implement content changes when the page changes.
   *
   * @param newPage
   * @param previousPage
   */
  protected abstract void onPageChanged(T newPage, T previousPage);

  /**
   * Animate new content into the content view.
   *
   * @param content            New content View to show.
   * @param layoutParams       Layout params for the view.
   * @param completionRunnable Runnable to be called when animation is complete.
   */
  protected void animateNewContent(final View content, final FrameLayout.LayoutParams layoutParams, final Runnable completionRunnable) {
    ArrayList<Animator> animators = new ArrayList<>();
    Animator fadeOut = createFadeOutAnimator(contentView);
    fadeOut.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        contentView.removeAllViews();
        if (content != null && layoutParams != null) {
          contentView.addView(content, layoutParams);
        }
      }
    });
    animators.add(fadeOut);
    animators.add(createFadeInAnimator(contentView));
    AnimatorSet set = new AnimatorSet();
    set.playSequentially(animators);
    if (completionRunnable != null) {
      set.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          completionRunnable.run();
        }
      });
    }
    set.start();
  }

  private Animator createFadeInAnimator(View view) {
    return ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f).setDuration(ANIMATION_DURATION);
  }

  private Animator createFadeOutAnimator(View view) {
    return ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f).setDuration(ANIMATION_DURATION);
  }

  /**
   * Expose next page control to subclasses.
   */
  protected void moveToNextPage() {
    super.moveToNextPage();
  }

  /**
   * Expose previous page control to subclasses.
   */
  protected void moveToPreviousPage() {
    super.moveToPreviousPage();
  }

  /**
   * Skip to a specific setup page
   *
   * @param page Page to skip to.
   */
  protected void moveToPage(OnboardingPage page) {
    int index = pages.indexOf(page);
    if (index >= 0 && index < getPageCount()) {
      int oldIndex = mCurrentPageIndex;
      mCurrentPageIndex = index;
      try {
        // need to invoke a private method
        Method method = OnboardingFragment.class.getDeclaredMethod("onPageChangedInternal", int.class);
        method.setAccessible(true);
        method.invoke(this, oldIndex);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Return the UI control for the start button, to allowe renaming, etc.
   */
  protected TextView getStartButton() {
    return (TextView) mStartButton;
  }

  /**
   * Show an error alert with Quit button as main action.
   *
   * @param message Message to display.
   */
  protected void showError(String message) {
    new AlertDialog.Builder(getActivity())
        .setTitle(R.string.error)
        .setMessage(message)
        .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            getActivity().finish();
          }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
            getActivity().finish();
          }
        })
        .create()
        .show();
  }

}
