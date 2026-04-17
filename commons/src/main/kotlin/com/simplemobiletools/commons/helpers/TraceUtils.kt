package com.simplemobiletools.commons.helpers

import android.os.Looper
import android.util.Log


/**
 * @author djkim
 */
object TraceUtils {
    private const val TAG = "__T"

    fun e1(msg: String) {
        print(Log.ERROR, 1, msg)
    }

    fun e(msg: String) {
        print(Log.ERROR, msg)
    }

    fun w(msg: String) {
        print(Log.WARN, msg)
    }

    fun i(msg: String) {
        print(Log.INFO, msg)
    }

    fun d(msg: String) {
        print(Log.DEBUG, msg)
    }

    fun v(msg: String) {
        print(Log.VERBOSE, msg)
    }

    fun print(level: Int, msg: String) {
        print(level, level - 1, msg)
    }

    fun print(level: Int, lines: Int, msg: String) {
        val sb = StringBuilder()
        sb.append(msg)
        if (lines > 2) {
            sb.append("\n")
        }
        var count = 1
        for (e in Thread.currentThread().getStackTrace()) {
            val trace = e.toString()
            if (!trace.contains("VMStack.getThreadStackTrace")
                && !trace.contains("java.lang.Thread.getStackTrace")
                && !trace.contains(TraceUtils::class.java.getPackage()!!.name)
            ) {
                sb.append("  at ").append(trace)
                sb.append("\n")
                count++
                if (count > lines) {
                    break
                }
            }
        }

        when (level) {
            Log.ERROR -> Log.e(TAG, sb.toString())
            Log.WARN -> Log.w(TAG, sb.toString())
            Log.INFO -> Log.i(TAG, sb.toString())
            Log.DEBUG -> Log.d(TAG, sb.toString())
            Log.VERBOSE -> Log.v(TAG, sb.toString())
        }
    }

    fun assertUiThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw AssertionError("assert Ui Thread")
        }
    }
}

fun Lx(msg: String, level: Int = Log.ERROR, lines: Int = 1) = TraceUtils.print(level, lines, msg)
fun Te(msg: String, lines: Int = 5) = TraceUtils.print(Log.ERROR, lines, msg)
fun Tw(msg: String, lines: Int = 4) = TraceUtils.print(Log.WARN, lines, msg)
fun Ti(msg: String, lines: Int = 3) = TraceUtils.print(Log.INFO, lines, msg)
fun Td(msg: String, lines: Int = 2) = TraceUtils.print(Log.DEBUG, lines, msg)
fun Tv(msg: String, lines: Int = 1) = TraceUtils.print(Log.VERBOSE, lines, msg)
