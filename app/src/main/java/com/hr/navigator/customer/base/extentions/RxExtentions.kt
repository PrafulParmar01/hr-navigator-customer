package com.hr.navigator.customer.base.extentions

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun Disposable.autoDispose(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun <T> Observable<T>.subscribeAndObserveOnMainThread(onNext: (t: T) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNext)
}

fun <T> Observable<T>.subscribeOnIoAndObserveOnMainThread(
    onNext: (t: T) -> Unit,
    onError: (Throwable) -> Unit
): Disposable {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNext, onError)
}

fun <T> Single<T>.subscribeOnIoAndObserveOnIoThread(
    onNext: (t: T) -> Unit,
    onError: (Throwable) -> Unit
): Disposable {
    return subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onNext, onError)
}

fun <T> Single<T>.subscribeOnIoAndObserveOnMainThread(
    onNext: (t: T) -> Unit,
    onError: (Throwable) -> Unit
): Disposable {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNext, onError)
}

fun <T> Single<T>.subscribeOnIoAndObserveOnIo(
    onNext: (t: T) -> Unit,
    onError: (Throwable) -> Unit
): Disposable {
    return subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onNext, onError)
}

fun <T> Observable<T>.subscribeOnIoAndObserveOnIo(
    onNext: (t: T) -> Unit,
    onError: (Throwable) -> Unit
): Disposable {
    return subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onNext, onError)
}



fun <T> SingleEmitter<in T>.onSafeSuccess(t: T) {
    if (!isDisposed) onSuccess(t)
}

fun <T> SingleEmitter<T>.onSafeError(throwable: Throwable) {
    if (!isDisposed) onError(throwable)
}

fun <T> MaybeEmitter<in T>.onSafeSuccess(t: T) {
    if (!isDisposed) onSuccess(t)
}

fun <T> MaybeEmitter<T>.onSafeComplete() {
    if (!isDisposed) onComplete()
}

fun <T> MaybeEmitter<T>.onSafeError(throwable: Throwable) {
    if (!isDisposed) onError(throwable)
}

fun CompletableEmitter.onSafeComplete() {
    if (!isDisposed) onComplete()
}

fun CompletableEmitter.onSafeError(throwable: Throwable) {
    if (!isDisposed) onError(throwable)
}

fun View.throttleClicks(): Observable<Unit> {
    return clicks().throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.throttleClicks(): Observable<T> {
    return this.throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
}