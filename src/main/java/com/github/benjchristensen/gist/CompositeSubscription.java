package com.github.benjchristensen.gist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Subscription;
import rx.exceptions.CompositeException;

/**
 * CompositeSubscription using synchronized, mutable data structure
 *
 * Subscription that represents a group of Subscriptions that are unsubscribed
 * together.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/system.reactive.disposables.compositedisposable(v=vs.103).aspx">Rx.Net equivalent CompositeDisposable</a>
 */
public final class CompositeSubscription implements Subscription {

    private Set<Subscription> subscriptions;
    private boolean unsubscribed = false;

    public CompositeSubscription() {
    }

    public CompositeSubscription(final Subscription... subscriptions) {
        this.subscriptions = new HashSet<Subscription>(Arrays.asList(subscriptions));
    }

    @Override
    public synchronized boolean isUnsubscribed() {
        return unsubscribed;
    }

    public void add(final Subscription s) {
        Subscription unsubscribe = null;
        synchronized (this) {
            if (unsubscribed) {
                unsubscribe = s;
            } else {
                if (subscriptions == null) {
                    subscriptions = new HashSet<Subscription>(4);
                }
                subscriptions.add(s);
            }
        }
        if (unsubscribe != null) {
            // call after leaving the synchronized block so we're not holding a lock while executing this
            unsubscribe.unsubscribe();
        }
    }

    public void remove(final Subscription s) {
        boolean unsubscribe = false;
        synchronized (this) {
            if (unsubscribed || subscriptions == null) {
                return;
            }
            unsubscribe = subscriptions.remove(s);
        }
        if (unsubscribe) {
            // if we removed successfully we then need to call unsubscribe on it (outside of the lock)
            s.unsubscribe();
        }
    }

    public void clear() {
        List<Subscription> unsubscribe = null;
        synchronized (this) {
            if (unsubscribed || subscriptions == null) {
                return;
            } else {
                unsubscribe = new ArrayList<Subscription>(subscriptions);
            }
        }
        unsubscribeFromAll(unsubscribe);
    }

    @Override
    public void unsubscribe() {
        synchronized (this) {
            if (unsubscribed || subscriptions == null) {
                return;
            } else {
                unsubscribed = true;
            }
        }
        // we will only get here once
        unsubscribeFromAll(subscriptions);
    }

    private static void unsubscribeFromAll(Collection<Subscription> subscriptions) {
        List<Throwable> es = null;
        for (Subscription s : subscriptions) {
            try {
                s.unsubscribe();
            } catch (Throwable e) {
                if (es == null) {
                    es = new ArrayList<Throwable>();
                }
                es.add(e);
            }
        }
        if (es != null) {
            if (es.size() == 1) {
                Throwable t = es.get(0);
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                } else {
                    throw new CompositeException(
                            "Failed to unsubscribe to 1 or more subscriptions.", es);
                }
            } else {
                throw new CompositeException(
                        "Failed to unsubscribe to 2 or more subscriptions.", es);
            }
        }
    }
}